package com.woting.ui.common.scanning.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.manager.MyActivityManager;
import com.woting.common.util.DialogUtils;

/**
 * 扫描结果界面
 * @author 辛龙
 *2016年8月8日
 */
public class ResultActivity extends Activity {
	private TextView mResultText;
	private WebView webview;
	private Dialog dialog;
	private LinearLayout head_left_btn;
	private ResultActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		context = this;
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		Bundle extras = getIntent().getExtras();
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
		head_left_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		webview = (WebView) findViewById(R.id.webView);
		mResultText = (TextView) findViewById(R.id.result_text);
		if (null != extras) {
			String result = extras.getString("result");
			Log.e("扫描结果", result);
			if (result.contains("http")) {
				webview.setVisibility(View.VISIBLE);
				mResultText.setVisibility(View.GONE);
				dialog = DialogUtils.Dialogph(this, "正在加载", dialog);
				WebSettings setting = webview.getSettings();
				setting.setJavaScriptEnabled(true);	// 支持js
				webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 解决缓存问题
				webview.loadUrl(result);
				webview.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						view.loadUrl(url);			// 使用当前的WebView加载页面
						return true;
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						super.onPageFinished(view, url);
						if (dialog != null) {
							dialog.dismiss();
						}
					}
				});

				webview.setWebChromeClient(new WebChromeClient());
			} else {
				webview.setVisibility(View.GONE);
				mResultText.setVisibility(View.VISIBLE);
				mResultText.setText(result);
			}
		} else {
			webview.setVisibility(View.GONE);
			mResultText.setVisibility(View.VISIBLE);
			mResultText.setText("抱歉，暂无结果");
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

	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			// message就是wave函数里alert的字符串，这样你就可以在android客户端里对这个数据进行处理
			result.confirm();
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		mResultText=null;
		webview=null;
		dialog=null;
		head_left_btn=null;
		context=null;
		setContentView(R.layout.activity_null);
	}
}
