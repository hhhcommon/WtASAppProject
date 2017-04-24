package com.woting.ui.mine.set.help;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseactivity.AppBaseActivity;

/**
 * 帮助--h5
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class HelpActivity extends AppBaseActivity implements OnClickListener, TipView.WhiteViewClick {
    private WebView webview;
    private Dialog dialog;

    private TipView tipView;

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
        setContentView(R.layout.activity_help);
        initView();
    }

    private void initView() {
        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        findViewById(R.id.head_left_btn).setOnClickListener(this);
        webview = (WebView) findViewById(R.id.webView);

        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            setWeb();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    private void setWeb() {
        tipView.setVisibility(View.GONE);
        dialog = DialogUtils.Dialog(context);
        String url = GlobalConfig.wthelpUrl;
        WebSettings setting = webview.getSettings();
        setting.setJavaScriptEnabled(true);                               // 支持js
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);    // 解决缓存问题
        webview.loadUrl(url);
        webview.setWebViewClient(new WebViewClient() {
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
        webview.setWebChromeClient(new WebChromeClient());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview = null;
        setContentView(R.layout.activity_null);
    }
}
