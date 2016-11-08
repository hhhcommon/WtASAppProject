package com.woting.ui.interphone.group.groupcontrol.modifygrouppassword;

import android.app.Activity;
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
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 更改群密码
 * @author 辛龙
 * 2016年7月19日
 */
public class ModifyGroupPasswordActivity extends Activity {
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
	private ModifyGroupPasswordActivity context;
	private String groupid;
	private String tag = "MODIFY_GROUP_PASSWORD_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_grouppassword);
		context = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		handleIntent();
		setView();
		setListener();
	}

	private void handleIntent() {
		Intent intent = context.getIntent();
		Bundle bundle = intent.getExtras();
		groupid = bundle.getString("GroupId");
	}

	private void setView() {
		et_oldpassword = (EditText) findViewById(R.id.edit_oldpassword);
		et_newpassword = (EditText) findViewById(R.id.edit_newpassword);
		et_newpassword_confirm = (EditText) findViewById(R.id.edit_confirmpassword);
		btn_password_modify = (TextView) findViewById(R.id.btn_modifypassword);
		lin_back = (LinearLayout) findViewById(R.id.head_left_btn);
	}

	private void setListener() {
		btn_password_modify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean result = checkData();
				if (result == true) {
					if (groupid != null && !groupid.equals("")) {
						if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
							send();
							/* ToastUtil.show_short(context, "接口尚未完成"); */
						} else {
							ToastUtils.show_allways(ModifyGroupPasswordActivity.this, "网络连接失败，请稍后重试");
						}
					} else {
						ToastUtils.show_allways(context, "获取groupid失败，请返回上一级界面重试");
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

	protected Boolean checkData() {
		oldpassword = et_oldpassword.getText().toString().trim();
		newpassword = et_newpassword.getText().toString().trim();
		passwordconfirm = et_newpassword_confirm.getText().toString().trim();
		flag = true;
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
		dialog = DialogUtils.Dialogph(this, "修改群密码提交请求");
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("OldPassword", oldpassword);
			jsonObject.put("NewPassword", newpassword);
			jsonObject.put("GroupId", groupid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.UpdateGroupPassWordUrl, tag, jsonObject, new VolleyCallback() {
//			private String SessionId;
			private String ReturnType;
//			private String GroupName;
			private String Message;
//			private String PlayHistory;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				try {
					// 获取列表
//					PlayHistory = result.getString("PlayHistory");
					ReturnType = result.getString("ReturnType");
//					SessionId = result.getString("SessionId");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					ToastUtils.show_allways(ModifyGroupPasswordActivity.this, "密码修改成功");
					finish();
				}else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(ModifyGroupPasswordActivity.this, "" + Message);
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_allways(ModifyGroupPasswordActivity.this, Message + "");
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
		groupid = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
