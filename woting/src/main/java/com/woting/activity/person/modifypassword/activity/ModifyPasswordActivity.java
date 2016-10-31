package com.woting.activity.person.modifypassword.activity;

import android.app.AlertDialog;
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
import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.activity.login.login.activity.LoginActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.MyActivityManager;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 修改密码
 * @author 辛龙
 * 2016年7月19日
 */
public class ModifyPasswordActivity extends BaseActivity {
	private EditText et_oldpassword;
	private EditText et_newpassword;
	private EditText et_newpassword_confirm;
	private TextView btn_password_modify;
	private boolean flag;
	private String oldpassword;
	private String newpassword;
	private String passwordconfirm;
	private Dialog dialog;
	private LinearLayout lin_back;
	private ModifyPasswordActivity context;
	private int ViewType;		// =0时说明来自通过验证过手机号的请求，此时userid来自上一个传入，并且界面要发生改变
	private LinearLayout lin_oldpassword;
	private String userid;
	private String phonenum;
	private String tag = "MODIFY_PASSWORD_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		context = this;
		setView();
		handleIntent();
		setListener();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
	}

	private void handleIntent() {
		ViewType = context.getIntent().getIntExtra("origin", 1);
		userid = context.getIntent().getStringExtra("userid");
		phonenum=context.getIntent().getStringExtra("phonenum");
		if (ViewType == 0) {
			lin_oldpassword.setVisibility(View.GONE);
		}
	}

	private void setView() {
		et_oldpassword = (EditText) findViewById(R.id.edit_oldpassword);
		et_newpassword = (EditText) findViewById(R.id.edit_newpassword);
		et_newpassword_confirm = (EditText) findViewById(R.id.edit_confirmpassword);
		btn_password_modify = (TextView) findViewById(R.id.btn_modifypassword);
		lin_back = (LinearLayout) findViewById(R.id.head_left_btn);
		lin_oldpassword = (LinearLayout) findViewById(R.id.lin_oldpassword);
	}

	private void setListener() {
		btn_password_modify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean result = checkData();
				if (result == true) {
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						if (ViewType != 0) {
							send();
						} else {
							sendmodifypassword();
						}
					} else {
						ToastUtils.show_short(ModifyPasswordActivity.this, "网络连接失败，请稍后重试");
					}
				}
			}
		});
		
		lin_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	protected void sendmodifypassword() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("RetrieveUserId", userid);
			jsonObject.put("NewPassword", newpassword);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.updatePwd_AfterCheckPhoneOKUrl, tag, jsonObject, new VolleyCallback() {
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
					ToastUtils.show_allways(ModifyPasswordActivity.this, "密码修改成功");
					Intent intent = new Intent(ModifyPasswordActivity.this, LoginActivity.class);
					intent.putExtra("phonenum",phonenum);
					startActivity(intent);
					finish();
				}
				if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(ModifyPasswordActivity.this, "" + Message);
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_allways(ModifyPasswordActivity.this, Message + "");
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

	protected Boolean checkData() {
		oldpassword = et_oldpassword.getText().toString().trim();
		newpassword = et_newpassword.getText().toString().trim();
		passwordconfirm = et_newpassword_confirm.getText().toString().trim();
		flag = true;
		if (ViewType != 0) {
			if ("".equalsIgnoreCase(oldpassword)) {
				Toast.makeText(this, "请输入您的旧密码", Toast.LENGTH_LONG).show();
				flag = false;
				return flag;
			}
			if ("".equalsIgnoreCase(newpassword)) {
				Toast.makeText(this, "请输入您的新密码", Toast.LENGTH_LONG).show();
				flag = false;
				return flag;
			}
		}
		if (newpassword.length() < 6) {
			Toast.makeText(this, "密码请输入六位以上", Toast.LENGTH_LONG).show();
			flag = false;
			return flag;
		}
		if ("".equalsIgnoreCase(newpassword)) {
			Toast.makeText(this, "请再次输入密码", Toast.LENGTH_LONG).show();
			flag = false;
			return flag;
		}
		if (!newpassword.equals(passwordconfirm)) {
			new AlertDialog.Builder(this).setMessage("两次输入的密码不一致").setPositiveButton("确定", null).show();
			flag = false;
			return flag;
		}
		if (passwordconfirm.length() < 6) {
			Toast.makeText(this, "密码请输入六位以上", Toast.LENGTH_LONG).show();
			flag = false;
			return flag;
		}
		return flag;
	}

	protected void send() {
		dialog = DialogUtils.Dialogph(this, "正在提交请求", dialog);
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("OldPassword", oldpassword);// 待改
			jsonObject.put("NewPassword", newpassword);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.modifyPasswordUrl, tag, jsonObject, new VolleyCallback() {
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
					ToastUtils.show_allways(ModifyPasswordActivity.this, "密码修改成功");
					finish();
				}
				if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(ModifyPasswordActivity.this, "" + Message);
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_allways(ModifyPasswordActivity.this, Message + "");
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
		mam.popOneActivity(context);
		et_oldpassword = null;
		et_newpassword = null;
		et_newpassword_confirm = null;
		btn_password_modify = null;
		oldpassword = null;
		newpassword = null;
		passwordconfirm = null;
		dialog = null;
		lin_back = null;
		context = null;
		lin_oldpassword = null;
		userid = null;
		phonenum = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
