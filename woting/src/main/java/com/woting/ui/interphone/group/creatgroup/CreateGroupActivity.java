package com.woting.ui.interphone.group.creatgroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.woting.R;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.interphone.group.creatgroup.creat.CreateGroupContentActivity;

/**
 * 创建群主页
 * @author 辛龙
 * 2016年5月16日
 */
public class CreateGroupActivity extends AppBaseActivity implements OnClickListener {
	private LinearLayout head_left_btn;
	private LinearLayout lin_groupmain_first;
	private LinearLayout lin_groupmain_second;
	private LinearLayout lin_groupmain_third;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creategroupmain);
		setView();
		setListener();
	}

	private void setListener() {
		lin_groupmain_first.setOnClickListener(this);
		lin_groupmain_second.setOnClickListener(this);
		lin_groupmain_third.setOnClickListener(this);
		head_left_btn.setOnClickListener(this);
	}

	private void setView() {
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
		lin_groupmain_first = (LinearLayout) findViewById(R.id.lin_groupmain_first);
		lin_groupmain_second = (LinearLayout) findViewById(R.id.lin_groupmain_second);
		lin_groupmain_third = (LinearLayout) findViewById(R.id.lin_groupmain_third);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		case R.id.lin_groupmain_first:
			Bundle bundle = new Bundle();
			bundle.putString("Type", "Open");
			Intent intent = new Intent(CreateGroupActivity.this,CreateGroupContentActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, 1);
			break;
		case R.id.lin_groupmain_second:
//			ToastUtil.show_short(context, "密码群");
			Bundle bundle1 = new Bundle();
			bundle1.putString("Type", "PassWord");
			Intent intent1 = new Intent(CreateGroupActivity.this,CreateGroupContentActivity.class);
			intent1.putExtras(bundle1);
			startActivity(intent1);
			break;
		case R.id.lin_groupmain_third:
//			ToastUtil.show_short(context, "验证群");
			Bundle bundle2 = new Bundle();
			bundle2.putString("Type", "Validate");
			Intent intent2 = new Intent(CreateGroupActivity.this,CreateGroupContentActivity.class);
			intent2.putExtras(bundle2);
			startActivity(intent2);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if(resultCode==1){
				finish();
			}
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		context = null;
		head_left_btn = null;
		lin_groupmain_first = null;
		lin_groupmain_second = null;
		lin_groupmain_third = null;
		setContentView(R.layout.activity_null);
	}
}
