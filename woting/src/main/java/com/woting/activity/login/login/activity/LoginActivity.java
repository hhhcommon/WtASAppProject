package com.woting.activity.login.login.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.umeng.socialize.Config;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.woting.R;
import com.woting.activity.interphone.commom.service.InterPhoneControl;
import com.woting.activity.login.forgetpassword.activity.ForgetPasswordActivity;
import com.woting.activity.login.register.activity.RegisterActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.SharePreferenceManager;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

/**
 * 登录界面
 * @author 辛龙
 *  2016年2月23日
 */
public class LoginActivity extends Activity implements OnClickListener {
	private EditText edittext_username;
	private EditText edittext_password;
	private TextView tv_wjmm;
	private TextView btn_login;
	private TextView btn_register;
	private LinearLayout lin_login_wx;
	private LinearLayout lin_login_qq;
	private LinearLayout lin_login_wb;
	private String username;
	private String password;
	private Dialog dialog;
	private LoginActivity context;
	private String ssid;
	private String userid;
	private String imageurl;
	private String imageurlbig;
	private UMShareAPI mShareAPI;
	private String returnusername;
	// 三方登录信息
	private String thirdnickname;
	private String ThirdUserId;
	private String ThirdUserImg;
	private String county;
	private String province;
	private String city;
	private String ThirdType;
	private String description;
	//	private int type = -1;// 标记从哪个页面来的
	private String phonenum;
	private Dialog dialog1;
	private String tag = "LOGIN_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	private Intent p_intent;
	private Intent pushintent;
	private LinearLayout pubBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context = this;
		mShareAPI = UMShareAPI.get(this);	// 初始化友盟
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		setView();
		setlistener();
		dialog1 = DialogUtils.Dialogphnoshow(context, "通讯中", dialog1);
		Config.dialog = dialog1;
	}

	//初始化视图
	private void setView(){
		pubBtn = (LinearLayout) findViewById(R.id.head_left_btn);	// 返回按钮
		edittext_username = (EditText) findViewById(R.id.edittext_username);	// 输入用户名
		edittext_password = (EditText) findViewById(R.id.edittext_password);	// 输入密码按钮
		tv_wjmm = (TextView) findViewById(R.id.tv_wjmm);						// 忘记密码
		btn_login = (TextView) findViewById(R.id.btn_login);					// 登录按钮
		btn_register = (TextView) findViewById(R.id.btn_register);				// 注册按钮
		lin_login_wx = (LinearLayout) findViewById(R.id.lin_login_wx);			// 微信denglu
		lin_login_qq = (LinearLayout) findViewById(R.id.lin_login_qq);			// qq登录 lin_login_qq
		lin_login_wb = (LinearLayout) findViewById(R.id.lin_login_wb);			// 微博登录 lin_login_wb

		String phoneName = (String) SharePreferenceManager.getSharePreferenceValue(context, "USER_NAME", "USER_NAME", "");
		edittext_username.setText(phoneName);
		p_intent = new Intent("push_down_completed");
		pushintent = new Intent("push_refreshlinkman");
	}

	private void setlistener() {
		pubBtn.setOnClickListener(this);
		tv_wjmm.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_register.setOnClickListener(this);
		lin_login_wx.setOnClickListener(this);
		lin_login_qq.setOnClickListener(this);
		lin_login_wb.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		handleIntent();
	}

	private void handleIntent() {
		//		type = context.getIntent().getIntExtra("type", -1);
		phonenum = context.getIntent().getStringExtra("phonenum");
		if (phonenum != null && !phonenum.equals("")) {
			edittext_username.setText(phonenum);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		case R.id.btn_login:
			checkdata();		// 验证数据
			break;
		case R.id.btn_register:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivityForResult(intent, 0);		// 跳转到注册界面
			break;
		case R.id.tv_wjmm:
			Intent PasswordIntent = new Intent(this, ForgetPasswordActivity.class);
			startActivity(PasswordIntent);			// 跳转到忘记密码界面
			break;
		case R.id.lin_login_wx:
			ToastUtils.show_allways(context, "weixin");
			SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
			mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
				@Override
				public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
					ToastUtils.show_allways(context, "认证异常" + arg2.toString());
				}
				@Override
				public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
					mShareAPI.getPlatformInfo(context, SHARE_MEDIA.WEIXIN, umAuthListener);
				}
				@Override
				public void onCancel(SHARE_MEDIA arg0, int arg1) {
					ToastUtils.show_allways(context, "用户退出认证");
				}
			});
			break;
		case R.id.lin_login_qq:
			SHARE_MEDIA platform1 = SHARE_MEDIA.QQ;
			mShareAPI.doOauthVerify(this, platform1, new UMAuthListener() {
				@Override
				public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
					ToastUtils.show_allways(context, "认证异常" + arg2.toString());
				}
				@Override
				public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
					mShareAPI.getPlatformInfo(context, SHARE_MEDIA.QQ, umAuthListener);
				}
				@Override
				public void onCancel(SHARE_MEDIA arg0, int arg1) {
					ToastUtils.show_allways(context, "用户退出认证");
				}
			});
			break;
		case R.id.lin_login_wb:
			SHARE_MEDIA platform2 = SHARE_MEDIA.SINA;
			mShareAPI.doOauthVerify(this, platform2, new UMAuthListener() {
				@Override
				public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
					ToastUtils.show_allways(context, "认证异常" + arg2.toString());
				}
				@Override
				public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
					mShareAPI.getPlatformInfo(context, SHARE_MEDIA.SINA, umAuthListener);
				}
				@Override
				public void onCancel(SHARE_MEDIA arg0, int arg1) {
					ToastUtils.show_allways(context, "用户退出认证");
				}
			});
			break;
		}
	}

	/// 获取用户信息接口
	private UMAuthListener umAuthListener = new UMAuthListener() {
		@Override
		public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
			if (data != null) {
				ToastUtils.show_allways(context, "认证成功，已经获取到个人信息");
				if (platform.equals(SHARE_MEDIA.SINA)) {
					String resultlist = data.get("result");
					JSONTokener jsonParser = new JSONTokener(resultlist);
					try {
						/* android.util.Log.e("个人信息", data.toString()+""); */
						JSONObject arg1 = (JSONObject) jsonParser.nextValue();
						thirdnickname = arg1.getString("name");
						ThirdUserId = arg1.getString("idstr");
						ThirdUserImg = arg1.getString("profile_image_url");
						ThirdType = "微博";
						province = arg1.getString("province");
						city = arg1.getString("city");
						description = arg1.getString("description");
						county = arg1.getString("country");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						dialog = DialogUtils.Dialogph(context, "正在用新浪信息注册", dialog);
						sendThird();
					} else {
						ToastUtils.show_allways(context, "网络失败，请检查网络");
					}
				} else if (platform.equals(SHARE_MEDIA.WEIXIN)) {
					/* android.util.Log.e("个人信息", data.toString()+""); */
					thirdnickname = data.get("nickname");
					ThirdUserId = data.get("unionid");
					ThirdUserImg = data.get("headimgurl");
					ThirdType = "微信";
					county = data.get("country");
					province = data.get("province");
					city = data.get("city");
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						/*
						 * Log.e("微信返回信息","thirdnickname="+thirdnickname+
						 * "ThirdUserId="+ThirdUserId+"ThirdUserImg="+
						 * ThirdUserImg+"county="+county+"province="+province+
						 * "city="+city);
						 */
						dialog = DialogUtils.Dialogph(context, "正在用微信信息注册", dialog);
						sendThird();
					} else {
						ToastUtils.show_allways(context, "网络失败，请检查网络");
					}
				} else {
					/* android.util.Log.e("个人信息", data.toString()+""); */
					thirdnickname = data.get("screen_name");
					ThirdUserId = data.get("openid");
					ThirdUserImg = data.get("profile_image_url");
					ThirdType = "QQ";
					county = data.get("country");
					province = data.get("province");
					city = data.get("city");
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						dialog = DialogUtils.Dialogph(context, "正在用qq信息注册", dialog);
						sendThird();
					} else {
						ToastUtils.show_allways(context, "网络失败，请检查网络");
					}
				}
			} else {
				ToastUtils.show_allways(context, "个人信息获取异常");
			}
		}

		@Override
		public void onError(SHARE_MEDIA platform, int action, Throwable t) {
			ToastUtils.show_allways(context, "个人信息获取异常");
		}

		@Override
		public void onCancel(SHARE_MEDIA platform, int action) {
			ToastUtils.show_allways(context, "您已取消操作，本程序无法获取到您的个人信息");
		}
	};

	private UMShareListener umShareListener = new UMShareListener() {

		@Override
		public void onResult(SHARE_MEDIA platform) {
			Log.d("plat", "platform" + platform);
			Toast.makeText(context, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(context, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			ToastUtils.show_allways(context, "用户退出认证");
		}
	};

	private void checkdata() {
		// 验证数据
		username = edittext_username.getText().toString().trim();
		password = edittext_password.getText().toString().trim();
		if (username == null || username.trim().equals("")) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			// 验证失败，返回
			return;
		}
		if (password == null || password.trim().equals("")) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			// 验证失败，返回
			return;
		}
		// 验证成功提交数据
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "登录中", dialog);
			send();
		} else {
			ToastUtils.show_allways(this, "网络失败，请检查网络");
		}
	}

	private void send() {
		JSONObject jsonObject =VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("UserName", username);
			jsonObject.put("Password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.loginUrl, tag, jsonObject, new VolleyCallback() {
			private String SessionId;
			private String ReturnType;
			private String Message;
			private String phonenumber;

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
				} catch (Exception e1) {
					e1.printStackTrace();
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
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						String userinfo = result.getString("UserInfo");
						JSONTokener jsonParser = new JSONTokener(userinfo);
						JSONObject arg1 = (JSONObject) jsonParser.nextValue();
						returnusername = arg1.getString("UserName");
						userid = arg1.getString("UserId");
						imageurl = arg1.getString("PortraitMini");
						imageurlbig = arg1.getString("PortraitBig");
						phonenumber=arg1.getString("PhoneNum");
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 通过shareperfrence存储用户的登录信息
					SharedPreferences sp = getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
					Editor et = sp.edit();
					et.putString(StringConstant.USERID, userid);
					et.putString(StringConstant.ISLOGIN, "true");
					et.putString(StringConstant.USERNAME, returnusername);
                    et.putString(StringConstant.IMAGEURL, imageurl);
					et.putString(StringConstant.IMAGEURBIG, imageurlbig);
					et.putString(StringConstant.PERSONREFRESHB, "true");
					et.commit();
					pushintent = new Intent("push_refreshlinkman");
					context.sendBroadcast(pushintent);
					//刷新下载界面
					context.sendBroadcast(p_intent);
					setResult(1);
					String phoneName = edittext_username.getText().toString().trim();
					SharePreferenceManager.saveBatchSharedPreference(context, "USER_NAME", "USER_NAME", phoneName);
					InterPhoneControl.sendEntryMessage(context);
					finish();
				} else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "服务器端无此用户");
				} else if (ReturnType != null && ReturnType.equals("1003")) {
					ToastUtils.show_allways(context, "密码错误");
				} else if (ReturnType != null && ReturnType.equals("0000")) {
					ToastUtils.show_allways(context, "发生未知错误，请稍后重试");
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "发生未知错误，请稍后重试");
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

	// 三方登录
	private void sendThird() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("ThirdUserId",ThirdUserId);
			jsonObject.put("ThirdType", ThirdType);
			jsonObject.put("ThirdUserImg", ThirdUserImg);
			jsonObject.put("ThirdUserName", thirdnickname);
			JSONObject jsonobject1 = new JSONObject();
			jsonobject1.put("nickname", thirdnickname);
			jsonobject1.put("unionid", ThirdUserId);
			jsonobject1.put("headimgurl", ThirdUserImg);
			jsonobject1.put("country", county);
			jsonobject1.put("province", province);
			jsonobject1.put("city", city);
			jsonobject1.put("description", description);
			jsonObject.put("ThirdUserInfo", jsonobject1);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.afterThirdAuthUrl, tag, jsonObject, new VolleyCallback() {
			private String SessionId;
			private String ReturnType;
			private String Message;
			private String imageurl1;
			private String returnusername1;
			private String imageurlbig1;
			private String userid1;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				Log.e("登录返回值",""+result.toString());
				if(isCancelRequest){
					return ;
				}
				try {
					ReturnType = result.getString("ReturnType");
					SessionId = result.getString("SessionId");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						String userinfo = result.getString("UserInfo");
						JSONTokener jsonParser = new JSONTokener(userinfo);
						JSONObject arg1 = (JSONObject) jsonParser.nextValue();
						imageurl1 = arg1.getString("PortraitMini");
						returnusername1 = arg1.getString("UserName");
						imageurlbig1 = arg1.getString("PortraitBig");
						userid1 = arg1.getString("UserId");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					ToastUtils.show_allways(context, "登陆成功");
					// 通过shareperfrence存储用户的登录信息
					SharedPreferences sp = getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
					Editor et = sp.edit();
					et.putString(StringConstant.USERID, userid1);
					et.putString(StringConstant.ISLOGIN, "true");
					et.putString(StringConstant.USERNAME, returnusername1);
					et.putString(StringConstant.IMAGEURL, imageurl1);
					et.putString(StringConstant.IMAGEURBIG, imageurlbig1);
					et.putString(StringConstant.PERSONREFRESHB, "true");
					et.commit();

					context.sendBroadcast(pushintent);
					//刷新下载界面
					context.sendBroadcast(p_intent);
					// TalkOldListFragment.update(context);
					InterPhoneControl.sendEntryMessage(context);
					setResult(1);
					finish();
				}
				if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "无法获取用户Id");
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("1011")) {
					ToastUtils.show_allways(context, "没有好友");
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mShareAPI.onActivityResult(requestCode, resultCode, data);// 友盟
		switch (requestCode) {
		case 0: // 从注册界面返回数据，注册成功
			if (resultCode == 1) {
				setResult(1);
				finish();
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		edittext_username = null;
		edittext_password = null;
		tv_wjmm = null;
		btn_login = null;
		pubBtn=null;
		btn_register = null;
		lin_login_wx = null;
		lin_login_qq = null;
		lin_login_wb = null;
		username = null;
		password = null;
		dialog = null;
		context = null;
		ssid = null;
		userid = null;
		imageurl = null;
		imageurlbig = null;
		mShareAPI = null;
		returnusername = null;
		thirdnickname = null;
		ThirdUserId = null;
		ThirdUserImg = null;
		county = null;
		province = null;
		city = null;
		ThirdType = null;
		description = null;
		phonenum = null;
		dialog1 = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
