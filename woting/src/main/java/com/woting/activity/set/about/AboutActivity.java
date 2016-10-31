package com.woting.activity.set.about;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.manager.MyActivityManager;
import com.woting.util.PhoneMessage;

/**
 * 关于 
 * @author 辛龙
 * 2016年3月9日
 */
public class AboutActivity extends BaseActivity implements OnClickListener {
	private AboutActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		context = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		setview();		//设置界面
	}

	/**
	 * 初始化视图
	 */
	private void setview() {
		LinearLayout head_left_btn=(LinearLayout)findViewById(R.id.head_left_btn);	// 返回
		head_left_btn.setOnClickListener(context);
		TextView tv_verson=(TextView)findViewById(R.id.tv_verson);					// 版本号
		tv_verson.setText(PhoneMessage.appVersonName);
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
