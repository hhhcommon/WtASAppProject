package com.woting.ui.home.player.main.play.more;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
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
import com.woting.ui.home.player.main.play.PlayerActivity;
import com.woting.ui.home.program.album.main.AlbumFragment;
import com.woting.ui.home.program.album.model.ContentInfo;
import com.woting.ui.home.program.comment.CommentActivity;
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.favorite.main.FavoriteFragment;
import com.woting.ui.mine.playhistory.main.PlayHistoryFragment;
import com.woting.ui.mine.subscriber.activity.SubscriberListFragment;

import org.json.JSONException;
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

    private Dialog dialog;
    private View rootView;
    private TextView textPlayName;// 正在播放的节目
    private TextView mPlayAudioTextLike;// 喜欢
    private TextView mPlayAudioTextDownLoad;// 下载

    private View viewLinear1;
    private View viewLinear2;

    private Dialog shareDialog;// 分享对话框

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

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
        }
        return rootView;
    }

    // 初始化视图
    private void initView() {
        rootView.findViewById(R.id.image_left).setOnClickListener(this);// 返回
        textPlayName = (TextView) rootView.findViewById(R.id.text_play_name);// 正在播放的节目标题
        mPlayAudioTextLike = (TextView) rootView.findViewById(R.id.text_like);// 喜欢
        mPlayAudioTextDownLoad = (TextView) rootView.findViewById(R.id.text_down);// 下载

        viewLinear1 = rootView.findViewById(R.id.view_linear_1);
        viewLinear2 = rootView.findViewById(R.id.view_linear_2);
        if (GlobalConfig.playerObject == null) {
            viewLinear1.setVisibility(View.GONE);
            viewLinear2.setVisibility(View.GONE);
            return ;
        }


        String type = GlobalConfig.playerObject.getMediaType();// 正在播放的节目类型

        // 正在播放的节目
        String name = GlobalConfig.playerObject.getContentName();
        if (name == null) name = "未知";
        textPlayName.setText(name);

        // 喜欢
        String contentFavorite = GlobalConfig.playerObject.getContentFavorite();
        if (type != null && type.equals("TTS")) {// TTS 不支持喜欢
            mPlayAudioTextLike.setClickable(false);
            mPlayAudioTextLike.setText("喜欢");
            mPlayAudioTextLike.setTextColor(context.getResources().getColor(R.color.gray));
            mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                    null, context.getResources().getDrawable(R.mipmap.wt_dianzan_nomal_gray), null, null);
        } else {
            mPlayAudioTextLike.setClickable(true);
            if (contentFavorite == null || contentFavorite.equals("0")) {
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
        if (type != null && type.equals("AUDIO")) {// 可以下载
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
    }

    // 初始化点击事件
    private void initEvent() {
        rootView.findViewById(R.id.text_shape).setOnClickListener(this);// 分享
        rootView.findViewById(R.id.text_comment).setOnClickListener(this);// 评论
        rootView.findViewById(R.id.text_details).setOnClickListener(this);// 节目详情
        rootView.findViewById(R.id.text_sequ).setOnClickListener(this);// 查看专辑
        rootView.findViewById(R.id.text_anchor).setOnClickListener(this);// 查看主播
        rootView.findViewById(R.id.text_timer).setOnClickListener(this);// 定时关闭
        mPlayAudioTextLike.setOnClickListener(this);// 喜欢
        mPlayAudioTextDownLoad.setOnClickListener(this);// 下载

        rootView.findViewById(R.id.text_history).setOnClickListener(this);// 播放历史
        rootView.findViewById(R.id.text_subscribe).setOnClickListener(this);// 我的订阅
        rootView.findViewById(R.id.text_local).setOnClickListener(this);// 我的下载
        rootView.findViewById(R.id.text_liked).setOnClickListener(this);// 我喜欢的
    }

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
            case R.id.text_shape:// 分享
                shareDialog.show();
                break;
            case R.id.text_details:// 节目详情
                ToastUtils.show_always(context, "节目详情");
                break;
            case R.id.text_anchor:// 查看主播
                if (!CommonHelper.checkNetwork(context)) return;
                ToastUtils.show_always(context, "查看主播");
                break;
            case R.id.text_timer:// 定时关闭
//                Intent intentTimeOff = new Intent(context, TimerPowerOffActivity.class);
//                if (isPlaying) {
//                    intentTimeOff.putExtra(StringConstant.IS_PLAYING, true);
//                } else {
//                    intentTimeOff.putExtra(StringConstant.IS_PLAYING, false);
//                }
//                startActivity(intentTimeOff);
                break;
            case R.id.text_sequ:// 查看专辑
                if (!CommonHelper.checkNetwork(context)) return;
                if (GlobalConfig.playerObject == null) return;
                if (GlobalConfig.playerObject.getSequId() != null) {
                    AlbumFragment fragment = new AlbumFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("fromType", 1);
                    bundle.putString("type", "player");
                    bundle.putSerializable("list", GlobalConfig.playerObject);
                    fragment.setArguments(bundle);
                    PlayerActivity.open(fragment);
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
                bundleHis.putInt("fromType", 6);
                historyFrag.setArguments(bundleHis);
                PlayerMoreOperationActivity.open(historyFrag);
                break;
            case R.id.text_subscribe:// 我的订阅
                SubscriberListFragment subscriberListFragment = new SubscriberListFragment();
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
                bundle.putInt("fromType", 6);
                favoriteFragment.setArguments(bundle);
                PlayerMoreOperationActivity.open(favoriteFragment);
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
            if (GlobalConfig.playerObject.getContentFavorite().equals("0")) {
                jsonObject.put("Flag", 1);
            } else {
                jsonObject.put("Flag", 0);
            }
        } catch (JSONException e) {
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
                        if (GlobalConfig.playerObject.getContentFavorite().equals("0")) {
                            mPlayAudioTextLike.setText("已喜欢");
                            mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                                    null, context.getResources().getDrawable(R.mipmap.wt_dianzan_select), null, null);
                            GlobalConfig.playerObject.setContentFavorite("1");
//                            if (index > 0) playList.get(index).setContentFavorite("1");
                        } else {
                            mPlayAudioTextLike.setText("喜欢");
                            mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                                    null, context.getResources().getDrawable(R.mipmap.wt_dianzan_nomal), null, null);
                            GlobalConfig.playerObject.setContentFavorite("0");
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
}
