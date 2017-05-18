package com.woting.ui.mine.hardware;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;

import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ShareUtils;
import com.woting.common.widgetui.HorizontalListView;
import com.woting.common.widgetui.TipView;
import com.woting.ui.base.baseactivity.AppBaseActivity;
import com.woting.ui.musicplay.play.adapter.ImageAdapter;
import com.woting.ui.music.model.share.ShareModel;

import java.util.List;

/**
 * 智能硬件
 * 作者：xinlong on 2016/7/19 21:18
 * 邮箱：645700751@qq.com
 */
public class HardwareIntroduceActivity extends AppBaseActivity implements View.OnClickListener, TipView.WhiteViewClick {
    private WebView webView;
    private Dialog dialog;
    private Dialog shareDialog;

    private TipView tipView;// 没有网络提示

    @Override
    public void onWhiteViewClick() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            setWeb();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_introduce);

        shareDialog();
        initView();
    }

    // 初始化控件
    private void initView() {
        findViewById(R.id.image_back).setOnClickListener(this);// 返回
        findViewById(R.id.text_shape).setOnClickListener(this);// 分享
        webView = (WebView) findViewById(R.id.web_view);

        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            setWeb();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    // 设置 WebView
    private void setWeb() {
        tipView.setVisibility(View.GONE);
        dialog = DialogUtils.Dialog(context);
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);                               // 支持js
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);    // 解决缓存问题

        String url = "http://www.wotingfm.com/download/download.html";
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (dialog != null) dialog.dismiss();
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:// 返回
                finish();
                break;
            case R.id.text_shape:// 分享
                shareDialog.show();
                break;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    // 分享模块
    private void shareDialog() {
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sharedialog, null);
        HorizontalListView mGallery = (HorizontalListView) dialogView.findViewById(R.id.share_gallery);
        shareDialog = new Dialog(context, R.style.MyDialog);
        shareDialog.setContentView(dialogView);// 从底部上升到一个位置
        Window window = shareDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        ViewGroup.LayoutParams params = dialogView.getLayoutParams();
        params.width = screenWidth;
        dialogView.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        Config.dialog = DialogUtils.DialogForShare(context);
        shareDialog.setCanceledOnTouchOutside(true);
        shareDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
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
                shareDialog.dismiss();
            }
        });
    }

    protected void callShare(SHARE_MEDIA Platform) {
        String shareName = "我听科技";
        String shareDesc = "我听科技分享硬件设备!";

        String shareContentImg = "http://182.92.175.134/img/logo-web.png";
        UMImage image = new UMImage(context, shareContentImg);

        String shareUrl = "http://www.wotingfm.com/download/download.html";
        new ShareAction(HardwareIntroduceActivity.this).setPlatform(Platform).withMedia(image)
                .withText(shareDesc).withTitle(shareName).withTargetUrl(shareUrl).share();
    }
}
