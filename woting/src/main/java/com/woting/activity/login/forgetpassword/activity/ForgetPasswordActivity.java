package com.woting.activity.login.forgetpassword.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.MyActivityManager;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 修改密码
 * @author 辛龙
 *2016年7月19日
 */
public class ForgetPasswordActivity extends Activity implements OnClickListener {
	private ForgetPasswordActivity context;
	private EditText mEditTextPassWord;
	private LinearLayout mlin_head_left;
	private EditText et_yzm;
	private TextView tv_getyzm;
	private TextView tv_cxfasong;
	private TextView tv_next;
	private TextView mTv_register;
	private EditText et_phonenum;
	private String phonenum;
	private Dialog dialog;
	private int sendtype = 1;// sendtype=1 掉发送验证码接口 sendtype=2时调重发验证码接口
	private String yanzhengma;
	private CountDownTimer mcountDownTimer;
	private String password;
	private int Vertifycode = -1;//-1时为点击过获取验证码按钮，或未正常发验证码
	private String tag = "FORGET_PASSWORD_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);	
		context = this;
		setView();
		setListener();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
	}

	private void setView() {
		mEditTextPassWord = (EditText)findViewById(R.id.edittext_password);		// 输入密码
		et_phonenum=(EditText)findViewById(R.id.edittext_userphone);
		mlin_head_left =(LinearLayout)findViewById(R.id.head_left_btn);
		et_yzm = (EditText) findViewById(R.id.et_yzm);
		tv_getyzm = (TextView) findViewById(R.id.tv_getyzm);
		tv_cxfasong = (TextView) findViewById(R.id.tv_cxfasong);
		tv_next= (TextView) findViewById(R.id.tv_next);
		mTv_register=(TextView)findViewById(R.id.tv_register);
	}

	private void setListener() {
		tv_getyzm.setOnClickListener(this);
		mlin_head_left.setOnClickListener(this);
		mTv_register.setOnClickListener(this);
		et_yzm.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s!= null&&s.length() == 6&& !s.equals("")) {
					tv_next.setVisibility(View.GONE);
					mTv_register.setVisibility(View.VISIBLE);
				} else {
					mTv_register.setVisibility(View.GONE);
					tv_next.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:		// 返回
			finish();
			break;
		case R.id.tv_register:			// 验证数据
			checkvalue();
			break;
		case R.id.tv_getyzm:			// 检查手机号是否为空，或者是否是一个正常手机号
			checkyzm();
			break;
		}
	}

	private void checkyzm() {
		// 检查手机号内容是否为空 检查输入数字是否为手机号 发送网络请求 返回值如果为正常的话 开启线程 每一秒刷新一次一下按钮
		phonenum = et_phonenum.getText().toString().trim();
		if ("".equalsIgnoreCase(phonenum)) {
			ToastUtils.show_allways(this, "手机号码不能为空");
			return;
		}
		if (isMobile(phonenum) == false) {
			ToastUtils.show_short(this, "请您输入正确的手机号");
			return;
		}
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在验证手机号", dialog);
			if (sendtype == 1) {
				sendfindpassword();
			} else {
				Resend();
			}
		} else {
			ToastUtils.show_short(context, "网络失败，请检查网络");
		}
	}

	private void checkvalue() {
		yanzhengma = et_yzm.getText().toString().trim();
		password=mEditTextPassWord.getText().toString().trim();
		if(Vertifycode==-1){
			ToastUtils.show_allways(context, "未获取验证码，或者验证码异常");
			return;
		}
		if ("".equalsIgnoreCase(phonenum)) {
			ToastUtils.show_allways(this, "手机号码不能为空");
			return;
		}
		if ("".equalsIgnoreCase(yanzhengma)) {
			ToastUtils.show_allways(this, "验证码码不能为空");
			return;
		}
		if (yanzhengma.length() != 6) {
			ToastUtils.show_allways(this, "请输入六位验证码");
			return;
		}
		if (password.length() != 6) {
			ToastUtils.show_allways(this, "请输入六位密码");
			return;
		}
		if ("".equalsIgnoreCase(password)) {
			ToastUtils.show_allways(this, "密码不能为空");
			return;
		}
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在验证手机号", dialog);
			sendrequest();
		} else {
			ToastUtils.show_short(context, "网络失败，请检查网络");
		}
	}

	// 提交数据到服务器进行验证
	private void sendrequest() {
		JSONObject jsonObject =VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("PhoneNum", phonenum);
			jsonObject.put("CheckCode", yanzhengma);
			jsonObject.put("NeedUserId", "true");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.checkPhoneCheckCodeUrl, tag, jsonObject, new VolleyCallback() {
			//			private String SessionId;
			private String ReturnType;
			private String Message;
			private String UserId;

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
					try {
						UserId = result.getString("UserId");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (UserId != null && !UserId.equals("")) {
						sendmodifypassword(UserId);			// 进入modifypassword修改当前userid的手机号
					} else {
						ToastUtils.show_allways(context, "获取UserId异常");
					}
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "验证码不匹配");
				}else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_allways(context, Message + "");
					}
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) {
					dialog.dismiss();
					ToastUtils.show_allways(context, "VolleyError捕获到异常");
				}
			}
		});
	}

	// 查找密码的相关接口
	private void sendfindpassword() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("PhoneNum", phonenum);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.retrieveByPhoneNumUrl, tag, jsonObject, new VolleyCallback() {
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
					ToastUtils.show_allways(context, "验证码已经发送");
					Vertifycode=1;
					sendtype = 2;
					timerdown();// 每秒减1
					et_phonenum.setEnabled(false);
					tv_getyzm.setVisibility(View.GONE);
					tv_cxfasong.setVisibility(View.VISIBLE);
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "此手机号在系统内没有注册");
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_allways(context, Message + "");
					}
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) {
					dialog.dismiss();
					ToastUtils.show_allways(context, "VolleyError捕获到异常");
				}
			}
		});
	}


	// 再次发送验证码
	private void Resend() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("PhoneNum", phonenum);
			// OperType
			jsonObject.put("OperType", "2");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.reSendPhoneCheckCodeNumUrl, tag, jsonObject, new VolleyCallback() {
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
					ToastUtils.show_allways(context, "验证码已经再次发送，请查收");
					Vertifycode=1;
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_allways(context, Message + "");
					}
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) {
					dialog.dismiss();
					ToastUtils.show_allways(context, "VolleyError捕获到异常");
				}
			}
		});
	}

	protected void sendmodifypassword(String userid) {
		dialog = DialogUtils.Dialogph(this, "正在提交请求", dialog);
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("RetrieveUserId", userid);
			jsonObject.put("NewPassword", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.updatePwd_AfterCheckPhoneOKUrl, userid, jsonObject, new VolleyCallback() {
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
					ToastUtils.show_allways(context,"密码修改成功");
					finish();
				}
				if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "" + Message);
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_allways(context, Message + "");
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

	private void timerdown() {
		mcountDownTimer = new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				tv_cxfasong.setText(millisUntilFinished / 1000 + "s后重新发送");
			}

			@Override
			public void onFinish() {
				tv_cxfasong.setVisibility(View.GONE);
				tv_getyzm.setVisibility(View.VISIBLE);
				if (mcountDownTimer != null) {
					mcountDownTimer.cancel();
				}
			}
		};
		mcountDownTimer.start();
	}

	// 验证手机号的方法
	public static boolean isMobile(String str) {
		Pattern pattern = null;
		Matcher mathcer = null;
		boolean bool = false;
		pattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号格式
		mathcer = pattern.matcher(str);
		bool = mathcer.matches();
		return bool;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		context = null;
		mEditTextPassWord = null;
		mlin_head_left = null;
		et_yzm = null;
		tv_getyzm = null;
		tv_cxfasong = null;
		tv_next = null;
		mTv_register = null;
		et_phonenum = null;
		phonenum = null;
		dialog = null;
		yanzhengma = null;
        if(mcountDownTimer != null) {
            mcountDownTimer.cancel();
            mcountDownTimer = null;
        }
		password = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
