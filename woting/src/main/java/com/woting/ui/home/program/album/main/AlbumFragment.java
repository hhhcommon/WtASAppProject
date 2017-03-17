package com.woting.ui.home.program.album.main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ShareUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.HorizontalListView;
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseadapter.MyFragmentChildPagerAdapter;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.adapter.ImageAdapter;
import com.woting.ui.home.player.main.model.LanguageSearchInside;
import com.woting.ui.home.player.main.model.ShareModel;
import com.woting.ui.home.player.main.play.PlayerActivity;
import com.woting.ui.home.program.accuse.activity.AccuseFragment;
import com.woting.ui.home.program.album.fragment.DetailsFragment;
import com.woting.ui.home.program.album.fragment.ProgramFragment;
import com.woting.ui.home.program.comment.CommentActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.home.program.radiolist.mode.ListInfo;
import com.woting.ui.mine.main.MineActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 专辑页
 * 作者：xinlong on 2016/4/1 17:40
 * 邮箱：645700751@qq.com
 */
public class AlbumFragment extends Fragment implements OnClickListener, TipView.WhiteViewClick {

    private DetailsFragment detailsFragment;
    private static ProgramFragment programFragment;

    public static TextView tv_album_name;
    public static TextView tv_favorite;
    private static TextView textSubscriber;      // 订阅
    private static ImageView imageSubscriber;    // 订阅小图标
    private TextView textDetails;               // text_details
    private TextView textProgram;               // text_program
    public static ImageView img_album;
    public static ImageView imageFavorite;
    private ImageView imageCursor;              // cursor

    private LinearLayout lin_share, lin_pinglun, lin_favorite, lin_subscriber;
    private ViewPager mPager;
    private Dialog dialog, shareDialog, dialog1;
    private UMImage image;

    private int fromType;// == 1 PlayerActivity  == 2 HomeActivity  == 3 MineActivity
    public static int returnResult = -1;        // == 1 说明信息获取正常，returnType == 1001
    private int offset;                         // 图片移动的偏移量
    private boolean isCancelRequest;

    public static String ContentDesc, ContentImg, ContentShareURL, ContentName, id;
    public static String ContentFavorite;       // 从网络获取的当前值，如果为空，表示页面并未获取到此值
    private String tag = "ALBUM_VOLLEY_REQUEST_CANCEL_TAG";
    private String RadioName;
    private static int flag = 0;                       // == 0 还没有订阅  == 1 已经订阅

    private View rootView;
    private static TipView tipView;                    // 提示
    private FragmentActivity context;
    private View viewBack;

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            if (detailsFragment != null) detailsFragment.send();
            if (programFragment != null) programFragment.send();
        } else {
            setTip(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_album, container, false);
            rootView.setOnClickListener(this);
            setView();// 设置界面
            setListener();
            InitImage();
            InitViewPager();
            handleIntent();
            shareDialog();// 分享 dialog
        }
        return rootView;
    }

    private void setView() {
        rootView.findViewById(R.id.head_right_btn).setOnClickListener(this);// 播放专辑
        viewBack = rootView.findViewById(R.id.view_back);

        tv_album_name = (TextView) rootView.findViewById(R.id.head_name_tv);
        img_album = (ImageView) rootView.findViewById(R.id.img_album);
        imageFavorite = (ImageView) rootView.findViewById(R.id.img_favorite);
        lin_share = (LinearLayout) rootView.findViewById(R.id.lin_share);              // 分享按钮
        lin_pinglun = (LinearLayout) rootView.findViewById(R.id.lin_pinglun);          // 评论
        lin_subscriber = (LinearLayout) rootView.findViewById(R.id.lin_subscriber);    // 订阅
        lin_favorite = (LinearLayout) rootView.findViewById(R.id.lin_favorite);        // 喜欢按钮
        tv_favorite = (TextView) rootView.findViewById(R.id.tv_favorite);              // tv_favorite
        textDetails = (TextView) rootView.findViewById(R.id.text_details);             // 专辑详情
        textDetails.setOnClickListener(this);
        textProgram = (TextView) rootView.findViewById(R.id.text_program);             // 专辑列表
        textProgram.setOnClickListener(this);

        textSubscriber = (TextView) rootView.findViewById(R.id.text_subscriber);       // 订阅
        imageSubscriber = (ImageView) rootView.findViewById(R.id.image_subscriber);    // 订阅小图标

        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);
    }

    private void setListener() {
        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);             // 返回按钮
        lin_share.setOnClickListener(this);
        lin_favorite.setOnClickListener(this);
        lin_pinglun.setOnClickListener(this);
        lin_subscriber.setOnClickListener(this);
    }

    /**
     * 设置cursor的宽
     */
    public void InitImage() {
        imageCursor = (ImageView) rootView.findViewById(R.id.cursor);
        LayoutParams lp = imageCursor.getLayoutParams();
        lp.width = PhoneMessage.ScreenWidth / 2;
        imageCursor.setLayoutParams(lp);
        offset = PhoneMessage.ScreenWidth / 2;
        // imageView 设置平移，使下划线平移到初始位置（平移一个 offset）
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        imageCursor.setImageMatrix(matrix);
    }

    /**
     * 初始化 ViewPager
     */
    public void InitViewPager() {
        mPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mPager.setOffscreenPageLimit(1);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        detailsFragment = new DetailsFragment();                      // 专辑详情页
        programFragment = new ProgramFragment();                      // 专辑列表页
        fragmentList.add(detailsFragment);
        fragmentList.add(programFragment);
        mPager.setAdapter(new MyFragmentChildPagerAdapter(getChildFragmentManager(), fragmentList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());  // 页面变化时的监听器
        mPager.setCurrentItem(0);                                      // 设置当前显示标签页为第一页 mPager
    }

    public static void setInfo(String contentId, String contentImg, String contentName, String contentDesc) {
        programFragment.setInfo(contentId, contentImg, contentName, contentDesc);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private int one = offset;                                                        // 两个相邻页面的偏移量
        private int currIndex;

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);        // 平移动画
            currIndex = arg0;
            animation.setFillAfter(true);         // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);           // 动画持续时间 0.2 秒
            imageCursor.startAnimation(animation);// 是用ImageView来显示动画的
            if (arg0 == 0) {
                textDetails.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                textProgram.setTextColor(context.getResources().getColor(R.color.group_item_text2));// 全部
            } else if (arg0 == 1) {               // 专辑
                textProgram.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                textDetails.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            }
        }
    }

    private void shareDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sharedialog, null);
        HorizontalListView mGallery = (HorizontalListView) dialog.findViewById(R.id.share_gallery);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancle);
        shareDialog = new Dialog(context, R.style.MyDialog);
        // 从底部上升到一个位置
        shareDialog.setContentView(dialog);
        Window window = shareDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        LayoutParams params = dialog.getLayoutParams();
        params.width = screenWidth;
        dialog.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        shareDialog.setCanceledOnTouchOutside(true);
        shareDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        final List<ShareModel> mList = ShareUtils.getShareModelList();
        ImageAdapter shareAdapter = new ImageAdapter(context, mList);
        mGallery.setAdapter(shareAdapter);
        dialog1 = DialogUtils.Dialogphnoshow(context, "通讯中", dialog1);
        Config.dialog = dialog1;
        mGallery.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SHARE_MEDIA Platform = mList.get(position).getSharePlatform();
                CallShare(Platform);
            }
        });

        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });
    }

    protected void CallShare(SHARE_MEDIA Platform) {
        if (returnResult == 1) {// 此处需从服务器获取分享所需要的信息，拿到字段后进行处理
            String shareName;
            String shareDesc;
            String shareContentImg;
            String shareUrl;
            if (ContentName != null && !ContentName.equals("")) {
                shareName = ContentName;
            } else {
                shareName = "我听我享听";
            }
            if (ContentDesc != null && !ContentDesc.equals("")) {
                shareDesc = ContentDesc;
            } else {
                shareDesc = "暂无本节目介绍";
            }
            if (ContentImg != null && !ContentImg.equals("")) {
                shareContentImg = ContentImg;
                image = new UMImage(context, shareContentImg);
            } else {
                shareContentImg = "http://182.92.175.134/img/logo-web.png";
                image = new UMImage(context, shareContentImg);
            }
            if (ContentShareURL != null && !ContentShareURL.equals("")) {
                shareUrl = ContentShareURL;
            } else {
                shareUrl = "http://www.wotingfm.com/";
            }
            dialog1 = DialogUtils.Dialogph(context, "分享中");
            Config.dialog = dialog1;
            new ShareAction(context).setPlatform(Platform).setCallback(umShareListener).withMedia(image)
                    .withText(shareDesc).withTitle(shareName).withTargetUrl(shareUrl).share();
        } else {
            ToastUtils.show_always(context, "分享失败，请稍后再试！");
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            Toast.makeText(context, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            shareDialog.dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(context, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            shareDialog.dismiss();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtils.show_always(context, "用户退出认证");
            shareDialog.dismiss();
        }
    };

    private void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            tipView.setTipView(TipView.TipStatus.IS_ERROR, "数据出错了\n请返回重试!");
            return;
        }
        fromType = bundle.getInt("fromType");
        if (fromType == 3) viewBack.setVisibility(View.VISIBLE);
        else viewBack.setVisibility(View.GONE);
        String type = bundle.getString("type");
        if (type != null && type.trim().equals("radiolistactivity")) {
            ListInfo list = (ListInfo) bundle.getSerializable("list");
            RadioName = list.getContentName();
            ContentDesc = list.getContentDescn();
            id = list.getContentId();
        } else if (type != null && type.trim().equals("recommend")) {
            RankInfo list = (RankInfo) bundle.getSerializable("list");
            RadioName = list.getContentName();
            ContentDesc = list.getContentDescn();
            id = list.getContentId();
        } else if (type != null && type.trim().equals("search")) {
            RankInfo list = (RankInfo) bundle.getSerializable("list");
            RadioName = list.getContentName();
            ContentDesc = list.getContentDescn();
            id = list.getContentId();
        } else if (type != null && type.trim().equals("main")) {
            // 再做一个
            RadioName = bundle.getString("conentname");
            id = bundle.getString("id");
        } else if (type != null && type.trim().equals("player")) {
            // 再做一个
            LanguageSearchInside list = (LanguageSearchInside) bundle.getSerializable("list");
            RadioName = list.getSequName();
            ContentDesc = list.getSequDesc();
            id = list.getSequId();
        } else {
            LanguageSearchInside list = (LanguageSearchInside) bundle.getSerializable("list");
            RadioName = list.getContentName();
            ContentDesc = list.getContentDescn();
            id = list.getContentId();
        }
        if (RadioName != null && !RadioName.equals("")) {
            tv_album_name.setText(RadioName);
        } else {
            tv_album_name.setText("未知");
        }
        Log.e("本节目的专辑ID为", id + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn: // 左上角返回键
                if (fromType == 1) {// Play
                    PlayerActivity.close();
                } else if (fromType == 2) {// Home
                    HomeActivity.close();
                } else {// Mine
                    MineActivity.close();
                }
                break;
            case R.id.lin_share: // 分享
                shareDialog.show();
                break;
            case R.id.lin_favorite: // 喜欢
                if (ContentFavorite != null && !ContentFavorite.equals("")) {
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialogph(context, "正在获取数据");
                        sendFavorite();
                    } else {
                        ToastUtils.show_always(context, "网络失败，请检查网络");
                    }
                } else {
                    ToastUtils.show_always(context, "数据出错了请稍后再试！");
                }
                break;
            case R.id.text_details: // 详情
                mPager.setCurrentItem(0);
                textDetails.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                textProgram.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                break;
            case R.id.text_program: // 列表
                mPager.setCurrentItem(1);
                textProgram.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                textDetails.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                break;
            case R.id.lin_pinglun: // 评论
                if (!TextUtils.isEmpty(id)) {
                    if (CommonUtils.getUserIdNoImei(context) != null && !CommonUtils.getUserIdNoImei(context).equals("")) {
                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra("contentId", id);
                        intent.putExtra("MediaType", "SEQU");
                        startActivity(intent);
                    } else {
                        ToastUtils.show_always(context, "请先登录~~");
                    }
                } else {
                    ToastUtils.show_always(context, "当前播放的节目的信息有误，无法获取评论列表");
                }
                break;
            case R.id.lin_subscriber: // 订阅
                String isLogin = BSApplication.SharedPreferences.getString(StringConstant.ISLOGIN, "false");
                if (!isLogin.trim().equals("") && isLogin.equals("true")) {
                    dialog = DialogUtils.Dialogph(context, "通讯中");
                    sendSubscribe();  // 发送订阅信息（订阅/取消订阅）
                } else {
                    ToastUtils.show_always(context, "请先登录~~");
                }
                break;
            case R.id.head_right_btn://  举报
                if(!TextUtils.isEmpty(id)){
                AccuseFragment fragment = new AccuseFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ContentId", id);
                bundle.putString("MediaType","SEQU");
                fragment.setArguments(bundle);
                HomeActivity.open(fragment);
                }else{
                    ToastUtils.show_always(context,"获取本专辑信息有误，请回退回上一级界面重试");
                }
                break;
        }
    }

    public static void setFlag(String contentSubscribe) {
        flag = Integer.valueOf(contentSubscribe);
        if(flag == 0) {
            textSubscriber.setText("订阅");
//            imageSubscriber
        } else {
            textSubscriber.setText("已订阅");
//            imageSubscriber
        }
    }

    // 发送订阅信息（订阅/取消订阅）
    private void sendSubscribe() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "SEQU");
            jsonObject.put("ContentId", id);
            if (flag == 0) {
                jsonObject.put("Flag", "1");
            } else {
                jsonObject.put("Flag", "0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.clickSubscribe, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    if (flag == 1) {
                        flag = 0;// 已经取消订阅
                        textSubscriber.setText("订阅");
//                        imageSubscriber
                    } else {
                        textSubscriber.setText("已订阅");
                        flag = 1;// 订阅成功
//                        imageSubscriber
                    }
                } else {
                    ToastUtils.show_always(context, "获取数据出错了，请重试!");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                ToastUtils.showVolleyError(context);
                if (dialog != null) dialog.dismiss();
            }
        });
    }

    // 发送网络请求  获取喜欢数据
    private void sendFavorite() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "SEQU");
            jsonObject.put("ContentId", id);
            if (ContentFavorite.equals("0")) {
                jsonObject.put("Flag", "1");
            } else {
                jsonObject.put("Flag", "0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.clickFavoriteUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    if (ContentFavorite.equals("0")) {
                        ContentFavorite = "1";
                        tv_favorite.setText("已喜欢");
                        imageFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.wt_img_liked));
                    } else {
                        ContentFavorite = "0";
                        tv_favorite.setText("喜欢");
                        imageFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.wt_img_like));
                    }
                } else {
                    ToastUtils.show_always(context, "获取数据出错了，请重试!");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
            }
        });
    }

    // 设置提示
    public static void setTip(TipView.TipStatus tipStatus) {
        tipView.setVisibility(View.VISIBLE);
        tipView.setTipView(tipStatus);
    }

    // 隐藏提示
    public static void hideTip() {
        tipView.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        context = null;
        RadioName = null;
        tv_album_name = null;
        img_album = null;
        ContentDesc = null;
        ContentImg = null;
        ContentShareURL = null;
        ContentName = null;
        id = null;
        ContentFavorite = null;
        tv_favorite = null;
        lin_share = null;
        lin_favorite = null;
        dialog = null;
        shareDialog = null;
        dialog1 = null;
        image = null;
        textDetails = null;
        textProgram = null;
        imageCursor = null;
        detailsFragment = null;
        programFragment = null;

        Log.e("TAG", "onDestroy album");
    }
}
