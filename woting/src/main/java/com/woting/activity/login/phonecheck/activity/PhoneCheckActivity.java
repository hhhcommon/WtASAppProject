package com.woting.activity.login.phonecheck.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shenstec.activity.BaseActivity;
import com.woting.R;
import com.woting.activity.login.register.activity.RegisterActivity;
import com.woting.activity.person.modifypassword.activity.ModifyPasswordActivity;
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
 * 账号绑定==找回密码--变更手机号
 * @author 辛龙
 * 2016年7月19日
 */
public class PhoneCheckActivity extends BaseActivity implements OnClickListener {
	private PhoneCheckActivity context;
	private LinearLayout head_left;
	private EditText et_phonenum;
	private EditText et_yzm;
	private TextView tv_getyzm;
	private TextView tv_next;
	private String phonenum;
	private Dialog dialog;
	private CountDownTimer mcountDownTimer;
	private TextView tv_cxfasong;
	private int sendtype = 1;		// sendtype=1 掉发送验证码接口 sendtype=2时调重发验证码接口
	private String yanzhengma;
	private TextView tv_next_default;
	private int ViewType;			// =0时为忘记密码界面跳入 =1时为默认事件 =2为修改手机号
	private TextView tv_head_name;
	private int type = -1;
	private String tag = "PHONE_CHECK_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phonecheck);
		context = this;
		handleIntent();//接收数据
		setview();	// 设置界面
		setdata();//数据适配
		setLisener();	// 设置监听
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
	}

	

	private void handleIntent() {
		ViewType = context.getIntent().getIntExtra("origin", 1);
		type= context.getIntent().getIntExtra("type", -1);
		Log.e("VIEWTYPE", ViewType + "");
	}

	private void setview() {
		head_left = (LinearLayout) findViewById(R.id.head_left_btn);
		et_phonenum = (EditText) findViewById(R.id.et_phonenum);
		et_yzm = (EditText) findViewById(R.id.et_yzm);
		tv_getyzm = (TextView) findViewById(R.id.tv_getyzm);
		tv_next = (TextView) findViewById(R.id.tv_next);
		tv_cxfasong = (TextView) findViewById(R.id.tv_cxfasong);
		tv_next_default = (TextView) findViewById(R.id.tv_next_default);
		tv_head_name = (TextView) findViewById(R.id.head_name_tv);
	}
	
	private void setdata() {
		if (ViewType == 0) {
			tv_head_name.setText("找回密码");
		} else if ((ViewType == 2)) {
			tv_head_name.setText("变更手机号");
			et_phonenum.setHint("请输入您要变更的手机号码");
		}
	}

	private void setLisener() {
		head_left.setOnClickListener(this);
		tv_getyzm.setOnClickListener(this);
		tv_next.setOnClickListener(this);
		et_yzm.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 6 && phonenum != null && !phonenum.equals("")) {
					tv_next_default.setVisibility(View.GONE);
					tv_next.setVisibility(View.VISIBLE);
				} else {
					tv_next.setVisibility(View.GONE);
					tv_next_default.setVisibility(View.VISIBLE);
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
		case R.id.tv_getyzm:
			checkyzm();		// 检查手机号是否为空，或者是否是一个正常手机号
			break;
		case R.id.tv_next:
			checkvalue();	// 检查输入到页面的信息是否符合接口返回的结果进行验证
			break;
		}
	}

	private void checkvalue() {
		yanzhengma = et_yzm.getText().toString().trim();
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
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在验证手机号", dialog);
			sendrequest();
		} else {
			ToastUtils.show_short(context, "网络失败，请检查网络");
		}
	}

	private void checkyzm() {
		//检查手机号内容是否为空 检查输入数字是否为手机号 发送网络请求 返回值如果为正常的话 开启线程 每一秒刷新一次一下按钮
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
				if (ViewType == 1 || ViewType == 2) {
					send();
				} else {
					sendfindpassword();
				}
			} else {
				Resend();
			}
		} else {
			ToastUtils.show_short(context, "网络失败，请检查网络");
		}
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
//					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					ToastUtils.show_allways(context, "验证码已经发送");
					sendtype = 2;
					timerdown();		// 每秒减1
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
			if (ViewType == 1 || ViewType == 2) {
				jsonObject.put("OperType", "1");
			} else {
				jsonObject.put("OperType", "2");
			}
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

	// 提交数据到服务器进行验证
	private void sendrequest() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("PhoneNum", phonenum);
			jsonObject.put("CheckCode", yanzhengma);
			if (ViewType == 0) {
				jsonObject.put("NeedUserId", "true");
			}
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
					UserId = result.getString("UserId");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					if (ViewType == 1) {
						ToastUtils.show_allways(context, "验证成功,跳往注册界面");
						Intent intent = new Intent(context, RegisterActivity.class);
						intent.putExtra("phonenum", phonenum);
						intent.putExtra("type", type);
						startActivityForResult(intent,0);
						setResult(1);
						finish();
					} else if (ViewType == 0) {
						if (UserId != null && !UserId.equals("")) {
							ToastUtils.show_allways(context, "验证成功,跳往修改密码界面");
							Intent intent = new Intent(context, ModifyPasswordActivity.class);
							intent.putExtra("origin", 0);
							intent.putExtra("userid", UserId);
							intent.putExtra("phonenum", phonenum);
							startActivityForResult(intent, 0);
							setResult(1);
							finish();
						} else {
							ToastUtils.show_allways(context, "获取UserId异常");
						}
					} else if (ViewType == 2) {
						// 修改手机号的界面
						if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
							dialog = DialogUtils.Dialogph(context, "正在修改绑定手机号", dialog);
							sendbingding();
							}
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

	// 修改手机号方法 利用目前的修改手机号接口
	protected void sendbingding() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("PhoneNum", phonenum);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.bindExtUserUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;
//			private String SessionId;
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
				if (ReturnType != null && !ReturnType.equals("")) {
					if (ReturnType.equals("1001")) {
                        ToastUtils.show_allways(context, "登录用手机号已经成功修改为" + phonenum);
                        finish();
					} else {
						ToastUtils.show_allways(context, Message + "");
					}
				} else {
                   ToastUtils.show_allways(context, "数据获取异常，返回值为null");
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

	// 首次发送验证码方法
	private void send() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("PhoneNum", phonenum);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.registerByPhoneNumUrl, tag, jsonObject, new VolleyCallback() {
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
					sendtype = 2;
					timerdown();		// 每秒减1
					et_phonenum.setEnabled(false);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) { 
		case 0: //从注册界面返回数据，注册成功
			if(resultCode==1){
				setResult(1); 
				finish();
			} break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		context = null;
		head_left = null;
		et_phonenum = null;
		et_yzm = null;
		tv_getyzm = null;
		tv_next = null;
		phonenum = null;
		dialog = null;
		mcountDownTimer = null;
		tv_cxfasong = null;
		yanzhengma = null;
		tv_next_default = null;
		tv_head_name = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
