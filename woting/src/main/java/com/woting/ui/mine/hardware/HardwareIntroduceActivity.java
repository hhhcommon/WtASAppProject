package com.woting.ui.mine.hardware;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.ui.baseactivity.BaseActivity;

/**
 * 智能硬件
 */
public class HardwareIntroduceActivity extends BaseActivity implements View.OnClickListener {
    private WebView webView;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_introduce);

        initView();
    }

    // 初始化控件
    private void initView() {
        findViewById(R.id.image_back).setOnClickListener(this);
        webView = (WebView) findViewById(R.id.web_view);

//        setWeb();
    }

    // 设置 WebView
    private void setWeb() {
        dialog = DialogUtils.Dialogph(context, "正在加载", dialog);
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
}
