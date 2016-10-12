package com.woting.activity.login.splash.activity;

import android.app.Activity;
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
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.BitmapUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 启动页面，第一个activity
 * 作者：xinlong on 2016/2/19 12:29
 * 邮箱：645700751@qq.com
 */

public class SplashActivity extends Activity {
    private SharedPreferences sharedPreferences = BSApplication.SharedPreferences;
    private String first;
    private String tag = "SPLASH_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        Bitmap bmp = BitmapUtils.readBitMap(SplashActivity.this, R.mipmap.splash);
        imageView.setImageBitmap(bmp);

        first = sharedPreferences.getString(StringConstant.FIRST, "0");//是否是第一次登录
        Editor et = sharedPreferences.edit();
        et.putString(StringConstant.PERSONREFRESHB, "true");
        et.commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                send();
            }
        }, 1000);
    }

    protected void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(this);
        VolleyRequest.RequestPost(GlobalConfig.splashUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (isCancelRequest) {
                    return;
                }
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType.equals("1001")) {
                        Editor et = sharedPreferences.edit();
                        try {
                            String UserInfo = result.getString("UserInfo");
                            if (UserInfo == null || UserInfo.trim().equals("")) {
                                et.putString(StringConstant.USERID, "userid");
                                et.putString(StringConstant.USERNAME, "username");
                                et.putString(StringConstant.IMAGEURL, "imageurl");
                                et.putString(StringConstant.IMAGEURBIG, "imageurlbig");
                                et.commit();
                            } else {
                                UserInfo list = new UserInfo();
                                list = new Gson().fromJson(UserInfo, new TypeToken<UserInfo>() {
                                }.getType());
                                String userId = list.getUserId();
                                String userName = list.getUserName();
                                String imageUrl = list.getPortraitMini();
                                String imageUrlBig = list.getPortraitBig();
                                et.putString(StringConstant.USERID, userId);
                                et.putString(StringConstant.IMAGEURL, userName);
                                et.putString(StringConstant.IMAGEURBIG, imageUrl);
                                et.putString(StringConstant.USERNAME, imageUrlBig);
                                et.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (first != null && first.equals("1")) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));        //跳转到主页
                } else {
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));    //跳转到引导页
                }
                // overridePendingTransition(R.anim.wt_fade, R.anim.wt_hold);
                // overridePendingTransition(R.anim.wt_zoom_enter, R.anim.wt_zoom_exit);
                finish();
            }

            @Override
            protected void requestError(VolleyError error) {
                if (first != null && first.equals("1")) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));        //跳转到主页
                } else {
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));    //跳转到引导页
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
