package com.woting.ui.home.player.main.play.more;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.gatherdata.GatherData;
import com.woting.common.gatherdata.model.DataModel;
import com.woting.common.gatherdata.model.ReqParam;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ShareUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.HorizontalListView;
import com.woting.ui.download.activity.DownloadFragment;
import com.woting.ui.download.dao.FileInfoDao;
import com.woting.ui.download.fragment.DownLoadUnCompletedFragment;
import com.woting.ui.download.model.FileInfo;
import com.woting.ui.download.service.DownloadService;
import com.woting.ui.home.player.main.adapter.ImageAdapter;
import com.woting.ui.home.player.main.model.LanguageSearchInside;
import com.woting.ui.home.player.main.model.ShareModel;
import com.woting.ui.home.player.programme.ProgrammeActivity;
import com.woting.ui.home.player.timeset.activity.TimerPowerOffActivity;
import com.woting.ui.home.program.accuse.main.AccuseFragment;
import com.woting.ui.home.program.album.main.AlbumFragment;
import com.woting.ui.home.program.album.model.ContentInfo;
import com.woting.ui.home.program.comment.CommentActivity;
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.favorite.main.FavoriteFragment;
import com.woting.ui.mine.playhistory.main.PlayHistoryFragment;
import com.woting.ui.mine.subscriber.main.SubscriberListFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * PlayerMoreOperation
 * Created by Administrator on 2017/3/16.
 */
public class PlayerMoreOperationFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity context;
    private FileInfoDao mFileDao;// 文件相关数据库

    private Dialog shareDialog;// 分享对话框
    private Dialog dialog;
    private View rootView;
    private TextView textPlayName;// 正在播放的节目
    private TextView mPlayAudioTextLike;// 喜欢
    private TextView mPlayAudioTextDownLoad;// 下载
    private TextView textSequ;// 查看专辑
    private TextView textAnchor;// 查看主播
    private TextView textProgram;// 节目播单
    private TextView textReport1;// 举报
    private TextView textReport2;// 举报

    private View viewLinear1;
    private View viewLinear2;
    private View viewLinear3;
    private View view1;
    private View view2;

    private TextView textLiked;// 我喜欢的
    private TextView textSubscribe;// 我的订阅

    private boolean isPlaying;
    private String contentFavorite;
    private SubscriberListFragment subscriberListFragment;

    private String ObjType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        registerReceiver();
        mFileDao = new FileInfoDao(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_player_more_operation, container, false);
            initView();
            initEvent();
            shareDialog();
            initGatherData();
        }
        return rootView;
    }


    private void initGatherData() {
        if(GlobalConfig.playerObject!=null&&GlobalConfig.playerObject.getMediaType()!=null&&GlobalConfig.playerObject.getContentId()!=null){
            try{
            String beginTime=String.valueOf(System.currentTimeMillis());
                String apiType=StringConstant.APINAME_OPEN;
                if(GlobalConfig.playerObject.getMediaType().equals("AUDIO")){

                    ObjType=StringConstant.OBJTYPE_AUDIO;

                }else if(GlobalConfig.playerObject.getMediaType().equals("RADIO")){

                    ObjType=StringConstant.OBJTYPE_RADIO;

                }else{
                    return;
                }
                ReqParam mReqParam= new ReqParam();

                String objId=GlobalConfig.playerObject.getContentId();

                DataModel mdataModel=new DataModel(beginTime,apiType,ObjType,mReqParam,objId);

                if(mdataModel!=null){
                    GatherData.collectData(IntegerConstant.DATA_UPLOAD_TYPE_GIVEN,mdataModel);
                }

            }catch (Exception e){

                e.printStackTrace();
            }
        }else{
            Log.e("节目详情页的TAG","GlobalConfig.playerObject是个空");
        }
    }

    // 初始化视图
    private void initView() {
        rootView.findViewById(R.id.image_left).setOnClickListener(this);// 返回
        textPlayName = (TextView) rootView.findViewById(R.id.text_play_name);// 正在播放的节目标题
        mPlayAudioTextLike = (TextView) rootView.findViewById(R.id.text_like);// 喜欢
        mPlayAudioTextDownLoad = (TextView) rootView.findViewById(R.id.text_down);// 下载
        textSequ = (TextView) rootView.findViewById(R.id.text_sequ);// 查看专辑
        textAnchor = (TextView) rootView.findViewById(R.id.text_anchor);// 查看主播
        textProgram = (TextView) rootView.findViewById(R.id.text_program);// 节目播单
        textReport1 = (TextView) rootView.findViewById(R.id.text_report_1);// 举报
        textReport2 = (TextView) rootView.findViewById(R.id.text_report_2);// 举报

        textLiked = (TextView) rootView.findViewById(R.id.text_liked);// 我喜欢的
        textSubscribe = (TextView) rootView.findViewById(R.id.text_subscribe);// 我的订阅

        viewLinear1 = rootView.findViewById(R.id.view_linear_1);
        viewLinear2 = rootView.findViewById(R.id.view_linear_2);
        viewLinear3 = rootView.findViewById(R.id.view_linear_3);
        view1 = rootView.findViewById(R.id.view_1);
        view2 = rootView.findViewById(R.id.view_2);

        resetView();
    }

    // 播放节目发生变化时需要更新的 View
    private void resetView() {
        String isLogin = BSApplication.SharedPreferences.getString(StringConstant.ISLOGIN, "false");
        if (isLogin.equals("false")) {// 没有登录
            textLiked.setVisibility(View.GONE);
            textSubscribe.setVisibility(View.GONE);
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
        } else {// 登录状态
            textLiked.setVisibility(View.VISIBLE);
            textSubscribe.setVisibility(View.VISIBLE);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
        }
        if (GlobalConfig.playerObject == null) {
            viewLinear1.setVisibility(View.GONE);
            viewLinear2.setVisibility(View.GONE);
            viewLinear3.setVisibility(View.GONE);
            return ;
        } else {
            viewLinear1.setVisibility(View.VISIBLE);
            viewLinear2.setVisibility(View.VISIBLE);
        }
        String type = GlobalConfig.playerObject.getMediaType();// 正在播放的节目类型
        contentFavorite = GlobalConfig.playerObject.getContentFavorite();// == 0 还没喜欢  == 1 已经喜欢
        if (contentFavorite == null) contentFavorite = "0";

        // 正在播放的节目
        String name = GlobalConfig.playerObject.getContentName();
        if (name == null) name = "未知";
        textPlayName.setText(name);

        // 喜欢
        if (type != null && type.equals("TTS")) {// TTS 不支持喜欢
            mPlayAudioTextLike.setClickable(false);
            mPlayAudioTextLike.setText("喜欢");
            mPlayAudioTextLike.setTextColor(context.getResources().getColor(R.color.gray));
            mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                    null, context.getResources().getDrawable(R.mipmap.wt_dianzan_nomal_gray), null, null);
        } else {
            mPlayAudioTextLike.setClickable(true);
            if (contentFavorite.equals("0")) {
                mPlayAudioTextLike.setText("喜欢");
                mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                        null, context.getResources().getDrawable(R.mipmap.wt_dianzan_nomal), null, null);
            } else {
                mPlayAudioTextLike.setText("已喜欢");
                mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                        null, context.getResources().getDrawable(R.mipmap.wt_dianzan_select), null, null);
            }
        }

        // 下载
        if (type != null && type.equals(StringConstant.TYPE_AUDIO)) {// 可以下载
            if (!TextUtils.isEmpty(GlobalConfig.playerObject.getLocalurl())) {// 已下载
                mPlayAudioTextDownLoad.setClickable(false);
                mPlayAudioTextDownLoad.setCompoundDrawablesWithIntrinsicBounds(
                        null, context.getResources().getDrawable(R.mipmap.wt_play_xiazai_no), null, null);
                mPlayAudioTextDownLoad.setTextColor(context.getResources().getColor(R.color.gray));
                mPlayAudioTextDownLoad.setText("已下载");
            } else {// 没有下载
                mPlayAudioTextDownLoad.setClickable(true);
                mPlayAudioTextDownLoad.setCompoundDrawablesWithIntrinsicBounds(
                        null, context.getResources().getDrawable(R.mipmap.wt_play_xiazai), null, null);
                mPlayAudioTextDownLoad.setTextColor(context.getResources().getColor(R.color.wt_login_third));
                mPlayAudioTextDownLoad.setText("下载");
            }
        } else {// 不可以下载
            mPlayAudioTextDownLoad.setClickable(false);
            mPlayAudioTextDownLoad.setCompoundDrawablesWithIntrinsicBounds(
                    null, context.getResources().getDrawable(R.mipmap.wt_play_xiazai_no), null, null);
            mPlayAudioTextDownLoad.setTextColor(context.getResources().getColor(R.color.gray));
            mPlayAudioTextDownLoad.setText("下载");
        }

        // View 的显示
        if (type != null && type.equals(StringConstant.TYPE_RADIO)) {// 电台才有节目单
            textProgram.setVisibility(View.VISIBLE);
            textReport1.setVisibility(View.VISIBLE);
            viewLinear3.setVisibility(View.GONE);
            textSequ.setVisibility(View.GONE);
            textAnchor.setVisibility(View.GONE);
        } else {
            textProgram.setVisibility(View.GONE);
            textSequ.setVisibility(View.VISIBLE);
            textAnchor.setVisibility(View.VISIBLE);
            textReport1.setVisibility(View.GONE);
            viewLinear3.setVisibility(View.VISIBLE);
        }
    }

    // 初始化点击事件
    private void initEvent() {
        rootView.findViewById(R.id.text_shape).setOnClickListener(this);// 分享
        rootView.findViewById(R.id.text_comment).setOnClickListener(this);// 评论
        rootView.findViewById(R.id.text_details).setOnClickListener(this);// 节目详情
        rootView.findViewById(R.id.text_timer).setOnClickListener(this);// 定时关闭
        mPlayAudioTextLike.setOnClickListener(this);// 喜欢
        textSequ.setOnClickListener(this);// 查看专辑
        textAnchor.setOnClickListener(this);// 查看主播
        mPlayAudioTextDownLoad.setOnClickListener(this);// 下载
        textProgram.setOnClickListener(this);// 节目播放
        textReport1.setOnClickListener(this);// 举报
        textReport2.setOnClickListener(this);// 举报

        textLiked.setOnClickListener(this);// 我喜欢的
        textSubscribe.setOnClickListener(this);// 我的订阅

        rootView.findViewById(R.id.text_history).setOnClickListener(this);// 播放历史
        rootView.findViewById(R.id.text_local).setOnClickListener(this);// 我的下载
    }

    // 注册广播
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastConstants.UPDATE_PLAY_VIEW);
        filter.addAction(BroadcastConstants.UPDATE_DOWN_LOAD_VIEW);
        filter.addAction(BroadcastConstants.UPDATE_PLAY_IMAGE);
        filter.addAction(BroadcastConstants.PUSH_ALLURL_CHANGE);
        context.registerReceiver(mReceiver, filter);
    }

    // 广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BroadcastConstants.UPDATE_PLAY_VIEW:
                    resetView();
                    break;
                case BroadcastConstants.UPDATE_PLAY_IMAGE:
                    isPlaying = intent.getBooleanExtra(StringConstant.PLAY_IMAGE, false);
                    break;
                case BroadcastConstants.UPDATE_DOWN_LOAD_VIEW:// 更新已下载
                    resetView();
                    break;
                case BroadcastConstants.PUSH_ALLURL_CHANGE:// 登录状态发生改变
                    resetView();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_left:// 返回
                MainActivity.change();
                break;
            case R.id.text_like:// 喜欢
                sendFavorite();
                break;
            case R.id.text_down:// 下载
                download();
                break;
            case R.id.text_program:// 节目播单
                Intent p = new Intent(context, ProgrammeActivity.class);
                Bundle b = new Bundle();
                b.putString("BcId", GlobalConfig.playerObject.getContentId());
                p.putExtras(b);
                startActivity(p);
                break;
            case R.id.text_shape:// 分享
                shareDialog.show();
                break;
            case R.id.text_details:// 节目详情
                PlayerMoreOperationActivity.open(new PlayDetailsFragment());
                break;
            case R.id.text_anchor:// 查看主播
                if (!CommonHelper.checkNetwork(context)) return;
                ToastUtils.show_always(context, "查看主播");
                break;
            case R.id.text_timer:// 定时关闭
                Intent intentTimeOff = new Intent(context, TimerPowerOffActivity.class);
                if (isPlaying) {
                    intentTimeOff.putExtra(StringConstant.IS_PLAYING, true);
                } else {
                    intentTimeOff.putExtra(StringConstant.IS_PLAYING, false);
                }
                startActivity(intentTimeOff);
                break;
            case R.id.text_sequ:// 查看专辑
                if (!CommonHelper.checkNetwork(context)) return;
                if (GlobalConfig.playerObject == null) return;
                if (GlobalConfig.playerObject.getSequId() != null) {
                    AlbumFragment fragment = new AlbumFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_MORE);
                    bundle.putString("type", "player");
                    bundle.putSerializable("list", GlobalConfig.playerObject);
                    fragment.setArguments(bundle);
                    PlayerMoreOperationActivity.open(fragment);
                } else {
                    ToastUtils.show_always(context, "本节目没有所属专辑");
                }
                break;
            case R.id.text_comment:// 评论
                if (GlobalConfig.playerObject == null) return;
                if (!TextUtils.isEmpty(GlobalConfig.playerObject.getContentId()) && !TextUtils.isEmpty(GlobalConfig.playerObject.getMediaType())) {
                    if (CommonUtils.getUserIdNoImei(context) != null && !CommonUtils.getUserIdNoImei(context).equals("")) {
                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra("contentId", GlobalConfig.playerObject.getContentId());
                        intent.putExtra("MediaType", GlobalConfig.playerObject.getMediaType());
                        startActivity(intent);
                    } else {
                        ToastUtils.show_always(context, "请先登录~~");
                    }
                } else {
                    ToastUtils.show_always(context, "当前播放的节目的信息有误，无法获取评论列表");
                }
                break;
            case R.id.text_history:// 播放历史
                PlayHistoryFragment historyFrag = new PlayHistoryFragment();
                Bundle bundleHis = new Bundle();
                bundleHis.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_MORE);
                historyFrag.setArguments(bundleHis);
                PlayerMoreOperationActivity.open(historyFrag);
                break;
            case R.id.text_subscribe:// 我的订阅
                subscriberListFragment = new com.woting.ui.mine.subscriber.main.SubscriberListFragment();
                Bundle bundleSub = new Bundle();
                bundleSub.putInt("fromType", 6);
                subscriberListFragment.setArguments(bundleSub);
                PlayerMoreOperationActivity.open(subscriberListFragment);
                break;
            case R.id.text_local:// 我的下载
                PlayerMoreOperationActivity.open(new DownloadFragment());
                break;
            case R.id.text_liked:// 我喜欢的
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_MORE);
                favoriteFragment.setArguments(bundle);
                PlayerMoreOperationActivity.open(favoriteFragment);
                break;
            case R.id.text_report_1:// 举报
            case R.id.text_report_2:
                String contentId = GlobalConfig.playerObject.getContentId();
                String mediaType = GlobalConfig.playerObject.getMediaType();
                if (mediaType == null || mediaType.equals("") || contentId == null || contentId.equals("")) {
                    ToastUtils.show_always(context, "获取内容信息错误，请重试!");
                    return ;
                }
                AccuseFragment fragment = new AccuseFragment();
                Bundle bundleReport = new Bundle();
                bundleReport.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_MORE);
                bundleReport.putString("ContentId", contentId);
                bundleReport.putString("MediaType", mediaType);
                fragment.setArguments(bundleReport);
                PlayerMoreOperationActivity.open(fragment);
                break;
        }
    }

    // 喜欢---不喜欢操作
    private void sendFavorite() {
        dialog = DialogUtils.Dialogph(context, "通讯中");
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", GlobalConfig.playerObject.getMediaType());
            jsonObject.put("ContentId", GlobalConfig.playerObject.getContentId());
            if (contentFavorite.equals("0")) {
                jsonObject.put("Flag", 1);
            } else {
                jsonObject.put("Flag", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.clickFavoriteUrl, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                try {
                    ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && (ReturnType.equals("1001") || ReturnType.equals("1005"))) {
                        if (contentFavorite.equals("0")) {
                            mPlayAudioTextLike.setText("已喜欢");
                            mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                                    null, context.getResources().getDrawable(R.mipmap.wt_dianzan_select), null, null);
                            contentFavorite = "1";
                            GlobalConfig.playerObject.setContentFavorite(contentFavorite);
//                            if (index > 0) playList.get(index).setContentFavorite("1");
                        } else {
                            mPlayAudioTextLike.setText("喜欢");
                            mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                                    null, context.getResources().getDrawable(R.mipmap.wt_dianzan_nomal), null, null);
                            contentFavorite = "0";
                            GlobalConfig.playerObject.setContentFavorite(contentFavorite);
//                            if (index > 0) playList.get(index).setContentFavorite("0");
                        }
                    } else {
                        ToastUtils.show_always(context, "数据出错了，请您稍后再试!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 内容的下载
    private void download() {
        LanguageSearchInside data = GlobalConfig.playerObject;
        if (data == null || !data.getMediaType().equals("AUDIO")) return;
        if (data.getLocalurl() != null) {
            ToastUtils.show_always(context, "此节目已经保存到本地，请到已下载界面查看");
            return;
        }
        // 对数据进行转换
        List<ContentInfo> dataList = new ArrayList<>();
        ContentInfo m = new ContentInfo();
        m.setContentPlay(data.getContentPlay());
        m.setContentImg(data.getContentImg());
        m.setContentName(data.getContentName());
        m.setContentPub(data.getContentPub());
        m.setContentTimes(data.getContentTimes());
        m.setUserid(CommonUtils.getUserId(context));
        m.setDownloadtype("0");
        if (data.getSeqInfo() == null || data.getSeqInfo().getContentName() == null || data.getSeqInfo().getContentName().equals("")) {
            m.setSequname(data.getContentName());
        } else {
            m.setSequname(data.getSeqInfo().getContentName());
        }
        if (data.getSeqInfo() == null || data.getSeqInfo().getContentId() == null || data.getSeqInfo().getContentId().equals("")) {
            m.setSequid(data.getContentId());
        } else {
            m.setSequid(data.getSeqInfo().getContentId());
        }
        if (data.getSeqInfo() == null || data.getSeqInfo().getContentImg() == null || data.getSeqInfo().getContentImg().equals("")) {
            m.setSequimgurl(data.getContentImg());
        } else {
            m.setSequimgurl(data.getSeqInfo().getContentImg());
        }
        if (data.getSeqInfo() == null || data.getSeqInfo().getContentDesc() == null || data.getSeqInfo().getContentDesc().equals("")) {
            m.setSequdesc(data.getContentDescn());
        } else {
            m.setSequdesc(data.getSeqInfo().getContentDesc());
        }
        dataList.add(m);
        // 检查是否重复,如果不重复插入数据库，并且开始下载，重复了提示
        List<FileInfo> fileDataList = mFileDao.queryFileInfoAll(CommonUtils.getUserId(context));
        if (fileDataList.size() != 0) {// 此时有下载数据
            boolean isDownload = false;
            for (int j = 0; j < fileDataList.size(); j++) {
                if (fileDataList.get(j).getUrl().equals(m.getContentPlay())) {
                    isDownload = true;
                    break;
                }
            }
            if (isDownload) {
                ToastUtils.show_always(context, m.getContentName() + "已经存在于下载列表");
            } else {
                mFileDao.insertFileInfo(dataList);
                ToastUtils.show_always(context, m.getContentName() + "已经插入了下载列表");
                List<FileInfo> fileUnDownLoadList = mFileDao.queryFileInfo("false", CommonUtils.getUserId(context));// 未下载列表
                for (int kk = 0; kk < fileUnDownLoadList.size(); kk++) {
                    if (fileUnDownLoadList.get(kk).getDownloadtype() == 1) {
                        DownloadService.workStop(fileUnDownLoadList.get(kk));
                        mFileDao.updataDownloadStatus(fileUnDownLoadList.get(kk).getUrl(), "2");
                    }
                }
                for (int k = 0; k < fileUnDownLoadList.size(); k++) {
                    if (fileUnDownLoadList.get(k).getUrl().equals(m.getContentPlay())) {
                        FileInfo file = fileUnDownLoadList.get(k);
                        mFileDao.updataDownloadStatus(m.getContentPlay(), "1");
                        DownloadService.workStart(file);
                        Intent p_intent = new Intent(BroadcastConstants.PUSH_DOWN_UNCOMPLETED);
                        context.sendBroadcast(p_intent);
                        break;
                    }
                }
            }
        } else {// 此时库里没数据
            mFileDao.insertFileInfo(dataList);
            ToastUtils.show_always(context, m.getContentName() + "已经插入了下载列表");
            List<FileInfo> fileUnDownloadList = mFileDao.queryFileInfo("false", CommonUtils.getUserId(context));// 未下载列表
            for (int k = 0; k < fileUnDownloadList.size(); k++) {
                if (fileUnDownloadList.get(k).getUrl().equals(m.getContentPlay())) {
                    FileInfo file = fileUnDownloadList.get(k);
                    mFileDao.updataDownloadStatus(m.getContentPlay(), "1");
                    DownloadService.workStart(file);
                    DownLoadUnCompletedFragment.dwType = true;
                    Intent p_intent = new Intent(BroadcastConstants.PUSH_DOWN_UNCOMPLETED);
                    context.sendBroadcast(p_intent);
                    break;
                }
            }
        }
    }

    // 分享模块
    private void shareDialog() {
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sharedialog, null);
        HorizontalListView mGallery = (HorizontalListView) dialogView.findViewById(R.id.share_gallery);
        shareDialog = new Dialog(context, R.style.MyDialog);
        // 从底部上升到一个位置
        shareDialog.setContentView(dialogView);
        Window window = shareDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        ViewGroup.LayoutParams params = dialogView.getLayoutParams();
        params.width = screenWidth;
        dialogView.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        shareDialog.setCanceledOnTouchOutside(true);
        shareDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        dialog = DialogUtils.Dialogphnoshow(context, "通讯中", dialog);
        Config.dialog = dialog;
        final List<ShareModel> mList = ShareUtils.getShareModelList();
        ImageAdapter shareAdapter = new ImageAdapter(context, mList);
        mGallery.setAdapter(shareAdapter);
        mGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SHARE_MEDIA Platform = mList.get(position).getSharePlatform();
                callShare(Platform);
                shareDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareDialog.isShowing()) shareDialog.dismiss();
            }
        });
    }

    // 分享数据详情
    protected void callShare(SHARE_MEDIA Platform) {
        if (GlobalConfig.playerObject != null) {
            String shareName = GlobalConfig.playerObject.getContentName();
            if (shareName == null || shareName.equals("")) {
                shareName = "我听我享听";
            }
            String shareDesc = GlobalConfig.playerObject.getContentDescn();
            if (shareDesc == null || shareDesc.equals("")) {
                shareDesc = "暂无本节目介绍";
            }
            String shareContentImg = GlobalConfig.playerObject.getContentImg();
            if (shareContentImg == null || shareContentImg.equals("")) {
                shareContentImg = "http://182.92.175.134/img/logo-web.png";
            }
            UMImage image = new UMImage(context, shareContentImg);
            String shareUrl = GlobalConfig.playerObject.getContentShareURL();
            if (shareUrl == null || shareUrl.equals("")) {
                shareUrl = "http://www.wotingfm.com/";
            }
            new ShareAction(context).setPlatform(Platform).withMedia(image).withText(shareDesc).withTitle(shareName).withTargetUrl(shareUrl).share();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(mReceiver);
    }
}
