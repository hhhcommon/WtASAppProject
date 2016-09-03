package com.woting.activity.login.splash.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.login.splash.model.UserInfo;
import com.woting.activity.login.welcome.activity.WelcomeActivity;
import com.woting.activity.main.MainActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.BitmapUtils;
import com.woting.util.CommonUtils;
import com.woting.util.PhoneMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 启动页面，第一个activity
 * @author 辛龙
 * 2016年2月19日
 */
public class SplashActivity extends Activity {
	private SharedPreferences sharedPreferences;
	private String first;
	private Dialog dialog;
	private Bitmap bmp;
	private String tag = "SPLASH_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	private ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		imageView = (ImageView)findViewById(R.id.imageView1);
		bmp = BitmapUtils.readBitMap(SplashActivity.this, R.mipmap.splash);
		imageView.setImageBitmap(bmp);
		sharedPreferences = this.getSharedPreferences("wotingfm",Context.MODE_PRIVATE);
		first = sharedPreferences.getString(StringConstant.FIRST, "0");//是否是第一次登录
		Editor et=sharedPreferences.edit();
		et.putString(StringConstant.PERSONREFRESHB,"true");  
		et.commit();
		//		Intent show = new Intent(this, FloatingWindowService.class);
		//		show.putExtra(FloatingWindowService.OPERATION, FloatingWindowService.OPERATION_SHOW);
		//		startService(show);
		//		try {  
		//			File file = new File(PATH);    
		//			File files = new File(PATHS);    
		//			DeleteFileOrDirectory(file);
		//			DeleteFileOrDirectory(files);
		//		} catch (Exception e) {  
		//			e.printStackTrace();  
		//		} 
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				send();
			}
		}, 1000);
	}

	protected void send() {
		JSONObject jsonObject = new JSONObject();
		try {
			// 公共请求属性
			jsonObject.put("SessionId", CommonUtils.getSessionId(this));
			jsonObject.put("MobileClass", PhoneMessage.model+"::"+PhoneMessage.productor);
			jsonObject.put("PCDType", GlobalConfig.PCDType);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(this);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude", PhoneMessage.latitude);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.splashUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;
			private String SessionId;
			private String UserInfos;

			@Override
			protected void requestSuccess(JSONObject result) {
				if(dialog!=null){
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				try {
					ReturnType = result.getString("ReturnType");
					SessionId =	result.getString("SessionId");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					UserInfos =	result.getString("UserInfo");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(ReturnType.equals("1001")){
					Editor et = sharedPreferences.edit();
					et.putString(StringConstant.SESSIONID, SessionId); 
					if(UserInfos == null || UserInfos.trim().equals("")){
						et.putString(StringConstant.USERID, "userid"); 
						et.putString(StringConstant.USERNAME, "username"); 
						et.putString(StringConstant.IMAGEURL, "imageurl"); 
						et.putString(StringConstant.IMAGEURBIG, "imageurlbig"); 
						et.commit();
					}else{
						UserInfo list = new UserInfo();
						list = new Gson().fromJson(UserInfos, new TypeToken<UserInfo>(){}.getType());
						String userid = list.getUserId();
						String username = list.getUserName();
						String imageurl = list.getPortraitMini();
						String imageurlbig = list.getPortraitBig();
						et.putString(StringConstant.USERID, userid); 
						et.putString(StringConstant.IMAGEURL, imageurl); 
						et.putString(StringConstant.IMAGEURBIG, imageurlbig); 
						et.putString(StringConstant.USERNAME, username); 
						et.commit();
					}
				}
				if(first != null && first.equals("1")){
					startActivity(new Intent(SplashActivity.this, MainActivity.class));		//跳转到主页
				}else{
					startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));	//跳转到引导页
				}
				//				overridePendingTransition(R.anim.wt_fade, R.anim.wt_hold);
				//				overridePendingTransition(R.anim.wt_zoom_enter, R.anim.wt_zoom_exit);
				finish();
			}

			@Override
			protected void requestError(VolleyError error) {
				if(dialog!=null){
					dialog.dismiss();
				}
				if(first != null && first.equals("1")){
					startActivity(new Intent(SplashActivity.this, MainActivity.class));		//跳转到主页
				}else{
					startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));	//跳转到引导页
				}
//				overridePendingTransition(R.anim.wt_fade, R.anim.wt_hold);
//				overridePendingTransition(R.anim.wt_zoom_enter, R.anim.wt_zoom_exit);
				finish();
			}
		});
	}		

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		sharedPreferences = null;
		first = null;
		dialog = null;
		tag = null;
		imageView.setImageBitmap(null);
		if(bmp != null && !bmp.isRecycled()) {  
			bmp.recycle();
			bmp = null;
		}
		sharedPreferences=null;
		imageView=null;
		setContentView(R.layout.activity_null);
	}
}
