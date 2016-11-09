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

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ShareUtils;
import com.woting.common.widgetui.HorizontalListView;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.home.player.main.adapter.ImageAdapter;
import com.woting.ui.home.player.main.model.ShareModel;

import java.util.List;

/**
 * 智能硬件
 */
public class HardwareIntroduceActivity extends BaseActivity implements View.OnClickListener {
    private WebView webView;
    private Dialog dialog;
    private Dialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_app);

        shareDialog();
        initView();
    }

    // 初始化控件
    private void initView() {
        findViewById(R.id.image_back).setOnClickListener(this);// 返回
        findViewById(R.id.text_shape).setOnClickListener(this);// 分享
        webView = (WebView) findViewById(R.id.web_view);

        setWeb();
    }

    // 设置 WebView
    private void setWeb() {
        dialog = DialogUtils.Dialogph(context, "正在加载");
        String url = GlobalConfig.wthelpUrl;// 需要 URL
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);                               // 支持js
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);    // 解决缓存问题
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
        shareDialog.setCanceledOnTouchOutside(true);
        shareDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        final List<ShareModel> mList = ShareUtils.getShareModelList();
        ImageAdapter shareAdapter = new ImageAdapter(context, mList);
        mGallery.setAdapter(shareAdapter);

        mGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                SHARE_MEDIA Platform = mList.get(position).getSharePlatform();
                callShare();
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

    protected void callShare() {

    }
}
