package com.woting.activity.set.contactus.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.manager.MyActivityManager;

/**
 * 联系我们界面
 * @author 辛龙
 *2016年8月8日
 */
public class ContactUsActivity extends BaseActivity implements OnClickListener {
	private ContactUsActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactus);
		context = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		setView();	// 设置界面
	}

	/**
	 * 初始化视图
	 */
	private void setView() {
		LinearLayout head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);	// 返回
		head_left_btn.setOnClickListener(context);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:	// 返回
			finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		context = null;
		setContentView(R.layout.activity_null);
	}
}
