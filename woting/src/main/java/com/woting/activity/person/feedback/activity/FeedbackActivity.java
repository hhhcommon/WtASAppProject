package com.woting.activity.person.feedback.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.shenstec.activity.BaseActivity;
import com.woting.R;
import com.woting.activity.person.feedback.feedbacklist.activity.FeedbackListActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.MyActivityManager;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 提交意见反馈
 * @author 辛龙
 *2016年8月1日
 */
public class FeedbackActivity extends BaseActivity implements OnClickListener {
	private EditText mEditContent;
	private Dialog dialog;
	private String sEditContent;
	private String tag = "FEEDBACK_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(this);
		setView();
	}

	private void setView() {
		mEditContent = (EditText) findViewById(R.id.edit_feedback_content);
		TextView mBtnFeedback = (TextView) findViewById(R.id.submit_button);
		mBtnFeedback.setOnClickListener(this);
		LinearLayout mHeadLeftln = (LinearLayout) findViewById(R.id.head_left_btn);
		mHeadLeftln.setOnClickListener(this);
		LinearLayout mHeadRightln = (LinearLayout) findViewById(R.id.head_right_btn);
		mHeadRightln.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.submit_button:
			checkdata();
			break;
		case R.id.head_left_btn:
			finish();
			break;
		case R.id.head_right_btn:
			Intent intent = new Intent(this,FeedbackListActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	private void checkdata() {
		sEditContent = mEditContent.getText().toString().trim();      
		if ("".equalsIgnoreCase(sEditContent)) {
			Toast.makeText(this, "请您输入您的意见", Toast.LENGTH_LONG).show();
		}else{
			if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE!=-1){
				dialog = DialogUtils.Dialogph(FeedbackActivity.this, "反馈中", dialog);
				send();
			}else{
				ToastUtils.show_short(FeedbackActivity.this, "网络失败，请检查网络");
			}
		}				
	}

	private void send() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(this);
		try {
			jsonObject.put("Opinion", sEditContent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.FeedBackUrl, tag, jsonObject, new VolleyCallback() {
//			private String SessionId;
			private String ReturnType;
			private String Message;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				try {
					ReturnType = result.getString("ReturnType");
//					SessionId = result.getString("SessionId");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					ToastUtils.show_short(getApplicationContext(), "提交成功");
					Intent intent = new Intent(FeedbackActivity.this, FeedbackListActivity.class);
					startActivity(intent);
					finish();
				} else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_short(FeedbackActivity.this, "提交失败，失败原因");
				} else {
					if (Message != null && !Message.trim().equals("")) {
						Toast.makeText(FeedbackActivity.this, "提交意见反馈失败, " + Message, Toast.LENGTH_SHORT).show();
					}
				}
			}
			
			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(this);
		mEditContent = null;
		dialog = null;
		sEditContent = null;
		tag = null;
		mEditContent =null;
		setContentView(R.layout.activity_null);
	}
}