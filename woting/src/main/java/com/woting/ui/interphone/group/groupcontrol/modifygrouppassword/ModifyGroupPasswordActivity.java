package com.woting.ui.interphone.group.groupcontrol.modifygrouppassword;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 更改群密码
 * @author 辛龙
 * 2016年7月19日
 */
public class ModifyGroupPasswordActivity extends AppBaseActivity implements OnClickListener {
    private Dialog dialog;
	private EditText et_newpassword;
	private EditText et_newpassword_confirm;

	private String oldpassword;
	private String newpassword;
	private String passwordconfirm;
	private String groupid;
	private String tag = "MODIFY_GROUP_PASSWORD_VOLLEY_REQUEST_CANCEL_TAG";

	private boolean isCancelRequest;
	private String groupPassword;
	private TextView tv_password;
	private TextView tv_modify;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_grouppassword);
        initView();
	}

    // 初始化视图
	private void initView() {
        groupid = getIntent().getExtras().getString("GroupId");          // 获取上个界面传递过来的 ID
		groupPassword=getIntent().getExtras().getString("GroupPassword");//
		tv_password=(TextView)findViewById(R.id.tv_password);

        findViewById(R.id.head_left_btn).setOnClickListener(this);
		tv_modify=(TextView)findViewById(R.id.btn_modifypassword);
		tv_modify.setOnClickListener(this);

		et_newpassword = (EditText) findViewById(R.id.edit_newpassword);
		et_newpassword_confirm = (EditText) findViewById(R.id.edit_confirmpassword);

		if(!TextUtils.isEmpty(groupPassword)){
			tv_password.setText("当前群密码为:  "+groupPassword);
		}

		et_newpassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(TextUtils.isEmpty(s)){
					tv_modify.setBackgroundResource(R.drawable.bg_gray_edit);
					tv_modify.setTextColor(getResources().getColor(R.color.group_4b));
				}else{
					if(!TextUtils.isEmpty(et_newpassword_confirm.getText().toString().trim())&&!TextUtils.isEmpty(et_newpassword.getText().toString().trim())){
						tv_modify.setBackgroundResource(R.drawable.wt_commit_button_background);
						tv_modify.setTextColor(getResources().getColor(R.color.white));
					}
				}
			}
		});

		et_newpassword_confirm.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(TextUtils.isEmpty(s)){
					tv_modify.setBackgroundResource(R.drawable.bg_gray_edit);
					tv_modify.setTextColor(getResources().getColor(R.color.group_4b));
				}else{
					if(!TextUtils.isEmpty(et_newpassword_confirm.getText().toString().trim())&&!TextUtils.isEmpty(et_newpassword.getText().toString().trim())){
						tv_modify.setBackgroundResource(R.drawable.wt_commit_button_background);
						tv_modify.setTextColor(getResources().getColor(R.color.white));
					}
				}
			}
		});

	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.btn_modifypassword:
                if(CommonHelper.checkNetwork(context)) {
                    if (checkData()) {
                        if (groupid != null && !groupid.equals("")) {
                            send();
                        } else {
                            ToastUtils.show_always(context, "获取 groupId 失败，请返回上一级界面重试");
                        }
                    }
                }
                break;
        }
    }

    // 检查数据的正确性
	protected boolean checkData() {
		newpassword = et_newpassword.getText().toString().trim();
		passwordconfirm = et_newpassword_confirm.getText().toString().trim();

		if ("".equalsIgnoreCase(newpassword)) {
            ToastUtils.show_always(context, "请输入您的新密码");
			return false;
		}
		if (newpassword.length() < 6) {
            ToastUtils.show_always(context, "密码请输入6位以上");
			return false;
		}
		if (newpassword.length() >11) {
			ToastUtils.show_always(context, "密码请输入11位以下");
			return false;
		}
		if ("".equalsIgnoreCase(newpassword)) {
            ToastUtils.show_always(context, "请再次输入密码");
			return false;
		}
		if (passwordconfirm.length() < 6) {
			ToastUtils.show_always(context, "密码请输入六位以上");
			return false;
		}
		if (passwordconfirm.length() >11) {
			ToastUtils.show_always(context, "密码请输入11位以下");
			return false;
		}
		if (!newpassword.equals(passwordconfirm)) {
            ToastUtils.show_always(context, "两次输入的密码不一致");
			return false;
		}

		return true;
	}

	protected void send() {
		dialog = DialogUtils.Dialog(context);
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("OldPassword", groupPassword);
			jsonObject.put("NewPassword", newpassword);
			jsonObject.put("GroupId", groupid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.requestPost(GlobalConfig.UpdateGroupPassWordUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;
			private String Message;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) dialog.dismiss();
				if(isCancelRequest) return ;
				try {
					ReturnType = result.getString("ReturnType");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					ToastUtils.show_always(context, "密码修改成功");
					Intent intent=new Intent();
					String s=newpassword;
					intent.putExtra("GroupPassword",newpassword);
					setResult(1,intent);
					finish();
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_always(context, Message + "");
					}
				}
			}
			
			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		et_newpassword = null;
		et_newpassword_confirm = null;
		oldpassword = null;
		newpassword = null;
		passwordconfirm = null;
		dialog = null;
		groupid = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
