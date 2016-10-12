package com.woting.activity.login.register.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.activity.interphone.commom.service.InterPhoneControl;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
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
 * 注册
 * @author 辛龙
 *2016年8月8日
 */
public class RegisterActivity extends Activity implements OnClickListener {
	private EditText mEditTextName;
	private EditText mEditTextPassWord;
	private String password;
	private String username;
	private RegisterActivity context;
	private Dialog dialog;
	private TextView mTv_register;
	private LinearLayout mlin_head_left;
	private String phonenum;
	protected JSONObject arg1;
	protected String imageurl;
	private EditText mEditText_Userphone;
	private EditText et_yzm;
	private TextView tv_getyzm;
	private String yanzhengma;
	private TextView tv_cxfasong;
	private CountDownTimer mcountDownTimer;
	private TextView tv_next;
	private int sendtype = -1;		//=-1时调发送验证码方法，=其他时调用再次发送验证码方法
	private int vertifystatus = -1;	//=-1时说明没有为此手机号发送过验证码 =1表示成功
	private String phonenumvertify;
	private String tempVertify;
	private String tag = "REGISTER_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		context = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		HandleIntent();	//从上一个界面接收数据
		setView();//设置界面
		setdata();//适配数据
		setListener();//设置监听
	}

	private void setListener() {
		mTv_register.setOnClickListener(this);
		mlin_head_left.setOnClickListener(this);
		tv_getyzm.setOnClickListener(this);
		mlin_head_left.setOnClickListener(this);
		et_yzm.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 6 && phonenumvertify != null && !phonenumvertify.equals("")) {
					if(vertifystatus==1){
						tv_next.setVisibility(View.GONE);
						mTv_register.setVisibility(View.VISIBLE);
					}else{
						ToastUtils.show_allways(context,"请点击获取验证码，获取验证码信息");
					}
				} else {
					mTv_register.setVisibility(View.GONE);
					tv_next.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void HandleIntent() {
		phonenum=context.getIntent().getStringExtra("phonenum");
//		type= context.getIntent().getIntExtra("type", -1);
	
	}

	private void setView() {
		mEditTextName = (EditText)findViewById(R.id.edittext_username);			// 输入用户名
		mEditTextPassWord = (EditText)findViewById(R.id.edittext_password);		// 输入密码
		mEditText_Userphone=(EditText)findViewById(R.id.edittext_userphone);
		mlin_head_left =(LinearLayout)findViewById(R.id.head_left_btn);
		et_yzm = (EditText) findViewById(R.id.et_yzm);
		tv_getyzm = (TextView) findViewById(R.id.tv_getyzm);
		tv_cxfasong = (TextView) findViewById(R.id.tv_cxfasong);
		tv_next= (TextView) findViewById(R.id.tv_next);
		mTv_register=(TextView)findViewById(R.id.tv_register);
	}
	
	private void setdata() {
		mEditTextName.setText("p"+phonenum);
}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:// 返回
			finish();
			break;
		case R.id.tv_register:// 验证数据
			checkvalue();
			break;
		case R.id.tv_getyzm:// 检查手机号是否为空，或者是否是一个正常手机号
			checkyzm();
			break;
		}
	}

	private void checkvalue() {
		yanzhengma = et_yzm.getText().toString().trim();
		username = mEditTextName.getText().toString().trim();
		password = mEditTextPassWord.getText().toString().trim();
		phonenum = mEditText_Userphone.getText().toString().trim();
		
		if ("".equalsIgnoreCase(phonenum)) {
			ToastUtils.show_allways(this, "手机号码不能为空");
			return;
		}
		if (isMobile(phonenum) == false) {
			ToastUtils.show_allways(this, "请您输入正确的手机号");
			return;
		}
		if(!phonenum.equals(phonenumvertify)){
			ToastUtils.show_allways(context, "请输入您之前获取验证的手机号码");
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
		if (username == null || username.trim().equals("")) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (username.length() < 3) {
			Toast.makeText(this, "请输入三位以上用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password == null || password.trim().equals("")) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password.length() < 6) {
			Toast.makeText(this, "请输入六位以上密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在验证手机号", dialog);
			sendrequest();
		} else {
			ToastUtils.show_allways(context, "网络失败，请检查网络");
		}
	}

	private void sendrequest() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("PhoneNum", phonenum);
			jsonObject.put("CheckCode", yanzhengma);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.checkPhoneCheckCodeUrl, tag, jsonObject, new VolleyCallback() {
			private String SessionId;
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					SessionId = result.getString("SessionId");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					UserId = result.getString("UserId");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						dialog = DialogUtils.Dialogph(context, "注册中", dialog);
						send();
					} else {
						ToastUtils.show_allways(context, "网络失败，请检查网络");
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
	
	private void checkyzm() {
		phonenumvertify = mEditText_Userphone.getText().toString().trim();
		if(tempVertify == null){
			tempVertify = phonenumvertify;
		}else{
			if(tempVertify.equals(phonenumvertify)){
			}else{
				sendtype = -1;
				tempVertify = phonenumvertify;
			}
		}
		if ("".equalsIgnoreCase(phonenumvertify)) {
			ToastUtils.show_allways(this, "手机号码不能为空");
			return;
		}
		if (isMobile(phonenumvertify) == false) {
			ToastUtils.show_short(this, "请您输入正确的手机号");
			return;
		}
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在验证手机号", dialog);
			if (sendtype == -1) {
				GetVertifyCode();
			} else {
				ReGetVertifyCode();
			}
		} else {
			ToastUtils.show_short(context, "网络失败，请检查网络");
		}
	}

	private void ReGetVertifyCode() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("PhoneNum", phonenumvertify);
			jsonObject.put("OperType",1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.reSendPhoneCheckCodeNumUrl, tag, jsonObject, new VolleyCallback() {
			private String SessionId;
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					SessionId = result.getString("SessionId");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					timerdown();		// 每秒减1
					tv_getyzm.setVisibility(View.GONE);
					tv_cxfasong.setVisibility(View.VISIBLE);
					vertifystatus = 1;
					ToastUtils.show_allways(context, "验证码已经再次发送，请查收");
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

	private void GetVertifyCode() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("PhoneNum", phonenumvertify);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.registerByPhoneNumUrl, tag, jsonObject, new VolleyCallback() {
			private String SessionId;
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					SessionId = result.getString("SessionId");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					ToastUtils.show_allways(context, "验证码已经发送");
					timerdown();		// 每秒减1
					sendtype = 2;
					vertifystatus=1;
					tv_getyzm.setVisibility(View.GONE);
					tv_cxfasong.setVisibility(View.VISIBLE);
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "此号码已经注册");
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
				}
			}
		});
	}

	private void send() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("UserName",username);
			jsonObject.put("Password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.registerUrl, tag, jsonObject, new VolleyCallback() {
			private String SessionId;
			private String ReturnType;
			private String Message;
//			private String returnusername;
			private String userid;
//			private String imageurlbig;

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
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				try {
					SessionId = result.getString("SessionId");
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				try {
					Message = result.getString("Message");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					userid = result.getString("UserId");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				/*	try {
					imageurlbig= arg1.getString("PortraitBig");
				} catch (JSONException e) {
					e.printStackTrace();
				}*/
				if (ReturnType.equals("1001")) {
					// 通过shareperfrence存储用户的登录信息
					SharedPreferences sp = getSharedPreferences("wotingfm",	Context.MODE_PRIVATE);
					Editor et = sp.edit();
					et.putString(StringConstant.USERID, userid);
					et.putString(StringConstant.ISLOGIN, "true");
					et.putString(StringConstant.USERNAME, username);
					et.putString(StringConstant.PERSONREFRESHB, "true");
					et.commit();
					Intent pushintent = new Intent("push_refreshlinkman");
					context.sendBroadcast(pushintent);
					InterPhoneControl.sendEntryMessage(context);
					setResult(1);
					finish();
				} else if (ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "服务器端无此用户");
				} else if (ReturnType.equals("1003")) {
					ToastUtils.show_allways(context, "用户名重复");
				} else if (ReturnType.equals("0000")) {
					ToastUtils.show_allways(context, "发生未知错误，请稍后重试");
				} else if (ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "发生未知错误，请稍后重试");
				}else{
					ToastUtils.show_allways(context, Message+"");
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
		if(mcountDownTimer != null){
			mcountDownTimer.cancel();
			mcountDownTimer = null;
		}
		mEditTextName = null;
		mEditTextPassWord = null;
		password = null;
		username = null;
		context = null;
		dialog = null;
		mTv_register = null;
		mlin_head_left = null;
		phonenum = null;
		arg1 = null;
		imageurl = null;
		mEditText_Userphone = null;
		et_yzm = null;
		tv_getyzm = null;
		yanzhengma = null;
		tv_cxfasong = null;
		tv_next = null;
		phonenumvertify = null;
		tempVertify = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
