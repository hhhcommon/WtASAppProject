package com.woting.activity.set.help;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.manager.MyActivityManager;
import com.woting.util.DialogUtils;
/**
 * 帮助--h5
 * @author 辛龙
 *2016年8月8日
 */
public class HelpActivity extends Activity implements OnClickListener{
	private WebView webview;
	private LinearLayout head_left_btn;
	private String url;
	private Dialog dialog;
	private HelpActivity context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		context = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		dialog = DialogUtils.Dialogph(this, "正在加载", dialog);
		setview();
		setweb();
	}

	private void setview() {
		head_left_btn=(LinearLayout)findViewById(R.id.head_left_btn);
		webview=(WebView)findViewById(R.id.webView);
		head_left_btn.setOnClickListener(this);
		webview.setOnClickListener(this);
	}

	private void setweb() {
		url = GlobalConfig.wthelpUrl;
		WebSettings setting = webview.getSettings();  
		setting.setJavaScriptEnabled(true);								//支持js  
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);	//解决缓存问题  
		webview.loadUrl(url);
		webview .setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				view.loadUrl(url);		// 使用当前的WebView加载页面
				return true ;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if(dialog != null){
					dialog.dismiss();
				}
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
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){  
			if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) { 
				finish();
			}  
			return true;  
		}  
		return super.dispatchKeyEvent(event);  
	}

	final  class MyWebChromeClient extends WebChromeClient{
		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) { 
			//message就是wave函数里alert的字符串，这样你就可以在android客户端里对这个数据进行处理
			result.confirm();        
			return true;  
		}}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		head_left_btn = null;
		webview = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
