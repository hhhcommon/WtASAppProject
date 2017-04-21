package com.woting.ui.common.login;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.woting.R;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
import com.woting.ui.mine.person.forgetpassword.ForgetPasswordActivity;
import com.woting.ui.common.register.RegisterActivity;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

/**
 * 登录界面
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
    private UMShareAPI mShareAPI;      // 友盟

    private Dialog dialog;             // 加载数据对话框
    private Button btn_login;          // 登录按钮
    private EditText editUserName;     // 输入 用户名
    private EditText editPassword;     // 输入密码
    private String userName;           // 用户名
    private String password;           // 密码
    // 三方登录信息
    private String thirdNickName, thirdUserId, thirdUserImg, county, province, city, thirdType, description;
    private String tag = "LOGIN_VOLLEY_REQUEST_CANCEL_TAG";
    private String viewTag = "LoginActivity";
    private boolean isCancelRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mShareAPI = UMShareAPI.get(context);                              // 初始化友盟
        setView();
    }

    // 初始化视图
    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);         // 返回按钮
        findViewById(R.id.tv_wjmm).setOnClickListener(this);               // 忘记密码

        findViewById(R.id.btn_register).setOnClickListener(this);          // 注册按钮
        findViewById(R.id.lin_login_wx).setOnClickListener(this);          // 微信
        findViewById(R.id.lin_login_qq).setOnClickListener(this);          // qq登录
        findViewById(R.id.lin_login_wb).setOnClickListener(this);          // 微博登录

        btn_login = (Button) findViewById(R.id.btn_login);                 // 登录按钮
        btn_login.setOnClickListener(this);
        editUserName = (EditText) findViewById(R.id.edittext_username);    // 输入用户名
        editPassword = (EditText) findViewById(R.id.edittext_password);    // 输入密码按钮

        // 设置上次登录的手机号,此方法已经失效，注销后手机号等就会置为空
        // String phoneName = BSApplication.SharedPreferences.getString(StringConstant.USER_PHONE_NUMBER, "");
        // editUserName.setText(phoneName);
        // editUserName.setSelection(editUserName.getText().length());      // 移动光标到最后
        setEditListener();                                                  // 设置输入框的监听
    }

    private void setEditListener() {
        // 用户名的监听
        editUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setBtView();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //  密码的监听
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setBtView();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setBtView() {
        String userName = editUserName.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        if (userName != null && !userName.trim().equals("")) {
            if (password != null && !password.trim().equals("") && password.length() > 5) {
                btn_login.setBackgroundResource(R.drawable.zhuxiao_press);
            } else {
                btn_login.setBackgroundResource(R.drawable.bg_graybutton);
            }
        } else {
            btn_login.setBackgroundResource(R.drawable.bg_graybutton);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:             // 返回
                finish();
                break;
            case R.id.btn_login:                 // 登录
                checkData();
                break;
            case R.id.btn_register:              // 注册
                startActivityForResult(new Intent(context, RegisterActivity.class), 0);
                break;
            case R.id.tv_wjmm:                   // 忘记密码
                startActivity(new Intent(context, ForgetPasswordActivity.class));
                break;
            case R.id.lin_login_wx:              // 微信登录
                SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
                mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
                    @Override
                    public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
                        ToastUtils.show_always(context, "认证异常" + arg2.toString());
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
                        mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0, int arg1) {
                        ToastUtils.show_always(context, "用户退出认证");
                    }
                });
                break;
            case R.id.lin_login_qq:                // QQ登录
                SHARE_MEDIA platform1 = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(this, platform1, new UMAuthListener() {
                    @Override
                    public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
                        ToastUtils.show_always(context, "认证异常" + arg2.toString());
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
                        mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, umAuthListener);
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0, int arg1) {
                        ToastUtils.show_always(context, "用户退出认证");
                    }
                });
                break;
            case R.id.lin_login_wb:// 新浪微博登录
                SHARE_MEDIA platform2 = SHARE_MEDIA.SINA;
                mShareAPI.doOauthVerify(LoginActivity.this, platform2, new UMAuthListener() {
                    @Override
                    public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
                        ToastUtils.show_always(context, "认证异常" + arg2.toString());
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
                        mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, umAuthListener);
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0, int arg1) {
                        ToastUtils.show_always(context, "用户退出认证");
                    }
                });
                break;
        }
    }

    // 检查数据的正确性  检查通过则进行登录
    private void checkData() {
        userName = editUserName.getText().toString().trim();
        password = editPassword.getText().toString().trim();
        if (userName == null || userName.trim().equals("")) {
            ToastUtils.show_always(context, "登录账号不能为空");
            return;
        }
        if (password == null || password.trim().equals("")) {
            ToastUtils.show_always(context, "密码不能为空");
            return;
        }
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            send();
        } else {
            ToastUtils.show_always(context, "网络连接失败，请检查网络");
        }
    }

    // 发送登录请求
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("UserName", userName);
            jsonObject.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.loginUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            JSONObject ui = (JSONObject) new JSONTokener(result.getString("UserInfo")).nextValue();
                            Editor et = BSApplication.SharedPreferences.edit();
                            et.putString(StringConstant.ISLOGIN, "true");
                            et.putString(StringConstant.PERSONREFRESHB, "true");
                            try {
                                String imageUrl = ui.getString("PortraitMini");
                                et.putString(StringConstant.IMAGEURL, imageUrl);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.IMAGEURL, "");
                            }
//                            try {
//                                String returnUserName = ui.getString("UserName");
//                                et.putString(StringConstant.USERNAME, returnUserName);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                et.putString(StringConstant.USERNAME, "");
//                            }
                            try {
                                String UserNum = ui.getString("UserNum");
                                et.putString(StringConstant.USER_NUM, UserNum);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.USER_NUM, "");
                            }
                            try {
                                String imageUrlBig = ui.getString("PortraitBig");
                                et.putString(StringConstant.IMAGEURBIG, imageUrlBig);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.IMAGEURBIG, "");
                            }
                            try {
                                String userId = ui.getString("UserId");// 用户 ID
                                et.putString(StringConstant.USERID, userId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.USERID, "");
                            }
                            try {
                                String phoneNumber = ui.getString("PhoneNum");
                                et.putString(StringConstant.USER_PHONE_NUMBER, phoneNumber);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.USER_PHONE_NUMBER, "");
                            }
                            try {
                                String isPub = ui.getString("PhoneNumIsPub");
                                et.putString(StringConstant.PHONE_NUMBER_FIND, isPub);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.PHONE_NUMBER_FIND, "0");
                            }
                            try {
                                String gender = ui.getString("Sex");// 性别
                                if (gender != null && !gender.equals("")) {
                                    if (gender.equals("男")) {
                                        et.putString(StringConstant.GENDERUSR, "xb001");
                                    } else if (gender.equals("女")) {
                                        et.putString(StringConstant.GENDERUSR, "xb002");
                                    }
                                } else {
                                    et.putString(StringConstant.REGION, "xb001");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.REGION, "xb001");
                            }
                            try {
                                String birthday = ui.getString("Birthday");// 生日
                                et.putString(StringConstant.BIRTHDAY, birthday);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.BIRTHDAY, "");
                            }
                            try {
                                String region = ui.getString("Region");  // 区域

                                /**
                                 * 地区的三种格式
                                 * 1、行政区划\/**市\/市辖区\/**区
                                 * 2、行政区划\/**特别行政区  港澳台三地区
                                 * 3、行政区划\/**自治区\/通辽市  自治区地区
                                 */
                                if (region != null && !region.equals("")) {
                                    String[] subRegion = region.split("/");
                                    if (subRegion.length > 3) {
                                        region = subRegion[1] + " " + subRegion[3];
                                    } else if (subRegion.length == 3) {
                                        region = subRegion[1] + " " + subRegion[2];
                                    } else {
                                        region = subRegion[1].substring(0, 2);
                                    }
                                    et.putString(StringConstant.REGION, region);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.REGION, "");
                            }
//                            try {
//                                String age = ui.getString("Age");   // 年龄
//                                et.putString(StringConstant.AGE, age);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                et.putString(StringConstant.AGE, "");
//                            }
                            try {
                                String starSign = ui.getString("StarSign");// 星座
                                et.putString(StringConstant.STAR_SIGN, starSign);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.STAR_SIGN, "");
                            }
                            try {
                                String email = ui.getString("Email");// 邮箱
                                if (email != null && !email.equals("")) {
                                    if (email.equals("&null")) {
                                        et.putString(StringConstant.EMAIL, "");
                                    } else {
                                        et.putString(StringConstant.EMAIL, email);
                                    }
                                } else {
                                    et.putString(StringConstant.EMAIL, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.EMAIL, "");
                            }
                            try {
                                String userSign = ui.getString("UserSign");// 签名
                                if (userSign != null && !userSign.equals("")) {
                                    if (userSign.equals("&null")) {
                                        et.putString(StringConstant.USER_SIGN, "");
                                    } else {
                                        et.putString(StringConstant.USER_SIGN, userSign);
                                    }
                                } else {
                                    et.putString(StringConstant.USER_SIGN, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.USER_SIGN, "");
                            }
                            try {
                                String nickName = ui.getString("NickName");
                                if (nickName != null && !nickName.equals("")) {
                                    if (nickName.equals("&null")) {
                                        et.putString(StringConstant.NICK_NAME, "");
                                    } else {
                                        et.putString(StringConstant.NICK_NAME, nickName);
                                    }
                                } else {
                                    et.putString(StringConstant.NICK_NAME, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.NICK_NAME, "");
                            }

                            if (!et.commit()) {
                                Log.v("commit", "数据 commit 失败!");
                            }
                            // 更新通讯录
                            context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                            // 更改所有界面的登录状态
                            context.sendBroadcast(new Intent(BroadcastConstants.PUSH_ALLURL_CHANGE));
                            // socket重新连接
                            InterPhoneControl.sendEntryMessage(context);
                            setResult(1);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtils.show_always(context, "登录失败，请您稍后再试");
                        }

                    } else if (ReturnType != null && ReturnType.equals("1002")) {
                        Log.i(viewTag, "1002");
                        ToastUtils.show_always(context, "您输入的用户暂未注册!");
                    } else if (ReturnType != null && ReturnType.equals("1003")) {
                        Log.i(viewTag, "1003");
                        ToastUtils.show_always(context, "您输入的密码错误!");
                    } else if (ReturnType != null && ReturnType.equals("0000")) {
                        Log.i(viewTag, "0000");
                        ToastUtils.show_always(context, "登录失败，请稍后重试!");
                    } else if (ReturnType != null && ReturnType.equals("T")) {
                        Log.i(viewTag, "T");
                        ToastUtils.show_always(context, "登录失败，请稍后重试!");
                    } else {
                        Log.i(viewTag, "Message");
                        try {
                            String Message = result.getString("Message");
                            if (Message != null && !Message.trim().equals("")) {
                                ToastUtils.show_always(context, Message + "");
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            ToastUtils.show_always(context, "登录失败，请稍后重试!");
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 三方登录
    private void sendThird() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("ThirdUserId", thirdUserId);
            jsonObject.put("ThirdType", thirdType);
            jsonObject.put("ThirdUserImg", thirdUserImg);
            jsonObject.put("ThirdUserName", thirdNickName);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("nickname", thirdNickName);
            jsonObject1.put("unionid", thirdUserId);
            jsonObject1.put("headimgurl", thirdUserImg);
            jsonObject1.put("country", county);
            jsonObject1.put("province", province);
            jsonObject1.put("city", city);
            jsonObject1.put("description", description);
            jsonObject.put("ThirdUserInfo", jsonObject1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.afterThirdAuthUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {

                        try {
                            JSONObject ui = (JSONObject) new JSONTokener(result.getString("UserInfo")).nextValue();
                            Editor et = BSApplication.SharedPreferences.edit();
                            et.putString(StringConstant.ISLOGIN, "true");
                            et.putString(StringConstant.PERSONREFRESHB, "true");
                            try {
                                String imageUrl = ui.getString("PortraitMini");
                                et.putString(StringConstant.IMAGEURL, imageUrl);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.IMAGEURL, "");
                            }
//                            try {
//                                String returnUserName = ui.getString("UserName");
//                                et.putString(StringConstant.USERNAME, returnUserName);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                et.putString(StringConstant.USERNAME, "");
//                            }
                            try {
                                String UserNum = ui.getString("UserNum");
                                et.putString(StringConstant.USER_NUM, UserNum);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.USER_NUM, "");
                            }
                            try {
                                String imageUrlBig = ui.getString("PortraitBig");
                                et.putString(StringConstant.IMAGEURBIG, imageUrlBig);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.IMAGEURBIG, "");
                            }
                            try {
                                String userId = ui.getString("UserId");
                                et.putString(StringConstant.USERID, userId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.USERID, "");
                            }
                            try {
                                String phoneNumber = ui.getString("PhoneNum");
                                et.putString(StringConstant.USER_PHONE_NUMBER, phoneNumber);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.USER_PHONE_NUMBER, "");
                            }
                            try {
                                String isPub = ui.getString("PhoneNumIsPub");
                                et.putString(StringConstant.PHONE_NUMBER_FIND, isPub);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.PHONE_NUMBER_FIND, "0");
                            }

                            try {
                                String gender = ui.getString("Sex");
                                if (gender.equals("男")) {
                                    et.putString(StringConstant.GENDERUSR, "xb001");
                                } else if (gender.equals("女")) {
                                    et.putString(StringConstant.GENDERUSR, "xb002");
                                } else {
                                    et.putString(StringConstant.GENDERUSR, "xb001");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.GENDERUSR, "xb001");
                            }
                            try {
                                String region = ui.getString("Region");
                                /**
                                 * 地区的三种格式
                                 * 1、行政区划\/**市\/市辖区\/**区
                                 * 2、行政区划\/**特别行政区  港澳台三地区
                                 * 3、行政区划\/**自治区\/通辽市  自治区地区
                                 */
                                if (region != null && !region.equals("")) {
                                    String[] subRegion = region.split("/");
                                    if (subRegion.length > 3) {
                                        region = subRegion[1] + " " + subRegion[3];
                                    } else if (subRegion.length == 3) {
                                        region = subRegion[1] + " " + subRegion[2];
                                    } else {
                                        region = subRegion[1].substring(0, 2);
                                    }
                                    et.putString(StringConstant.REGION, region);
                                } else {
                                    et.putString(StringConstant.REGION, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.REGION, "");
                            }
                            try {
                                String birthday = ui.getString("Birthday");
                                et.putString(StringConstant.BIRTHDAY, birthday);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.BIRTHDAY, "");
                            }
//                            try {
//                                String age = ui.getString("Age");
//                                et.putString(StringConstant.AGE, age);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                et.putString(StringConstant.AGE, "");
//                            }
                            try {
                                String starSign = ui.getString("StarSign");
                                et.putString(StringConstant.STAR_SIGN, starSign);
                            } catch (Exception e) {
                                e.printStackTrace();
                                et.putString(StringConstant.STAR_SIGN, "");
                            }
                            try {
                                String email = ui.getString("Email");
                                if (email != null && !email.equals("")) {
                                    if (email.equals("&null")) {
                                        et.putString(StringConstant.EMAIL, "");
                                    } else {
                                        et.putString(StringConstant.EMAIL, email);
                                    }
                                } else {
                                    et.putString(StringConstant.EMAIL, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                String userSign = ui.getString("UserSign");
                                if (userSign != null && !userSign.equals("")) {
                                    if (userSign.equals("&null")) {
                                        et.putString(StringConstant.USER_SIGN, "");
                                    } else {
                                        et.putString(StringConstant.USER_SIGN, userSign);
                                    }
                                } else {
                                    et.putString(StringConstant.USER_SIGN, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                String nickName = ui.getString("NickName");
                                if (nickName != null && !nickName.equals("")) {
                                    if (nickName.equals("&null")) {
                                        et.putString(StringConstant.NICK_NAME, "");
                                    } else {
                                        et.putString(StringConstant.NICK_NAME, nickName);
                                    }
                                } else {
                                    et.putString(StringConstant.NICK_NAME, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!et.commit()) {
                                Log.v("commit", "数据 commit 失败!");
                            }
                            // 更新通讯录
                            context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                            // 更改所有界面的登录状态
                            context.sendBroadcast(new Intent(BroadcastConstants.PUSH_ALLURL_CHANGE));
                            // socket重新连接
                            InterPhoneControl.sendEntryMessage(context);
                            setResult(1);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (ReturnType != null && ReturnType.equals("1002")) {
                        Log.i(viewTag, "1002");
                        ToastUtils.show_always(context, "登录失败,请稍后再试!");
                    } else if (ReturnType != null && ReturnType.equals("T")) {
                        Log.i(viewTag, "T");
                        ToastUtils.show_always(context, "登录失败,请稍后再试!");
                    } else if (ReturnType != null && ReturnType.equals("1011")) {
                        Log.i(viewTag, "1011");
                        ToastUtils.show_always(context, "登录失败,请稍后再试!");
                    } else {
                        Log.i(viewTag, "Message");
                        try {
                            String Message = result.getString("Message");
                            if (Message != null && !Message.trim().equals("")) {
                                ToastUtils.show_always(context, Message + "");
                            } else {
                                ToastUtils.show_always(context, "登录失败,请稍后再试!");
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            ToastUtils.show_always(context, "登录失败,请稍后再试!");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.show_always(context, "登录失败,请稍后再试!");
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

    // 获取用户信息接口
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (data != null) {
                if (platform.equals(SHARE_MEDIA.SINA)) {
                    try {
                        JSONTokener jsonParser = new JSONTokener(data.get("result"));
                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                        thirdNickName = arg1.getString("name");
                        thirdUserId = arg1.getString("idstr");
                        thirdUserImg = arg1.getString("profile_image_url");
                        thirdType = "微博";
                        province = arg1.getString("province");
                        city = arg1.getString("city");
                        description = arg1.getString("description");
                        county = arg1.getString("country");
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                            dialog = DialogUtils.Dialog(context);
                            sendThird();
                        } else {
                            ToastUtils.show_always(context, "网络失败，请检查网络");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.show_always(context, "登录失败，请稍后再试！");
                    }

                } else if (platform.equals(SHARE_MEDIA.WEIXIN)) {
                    thirdNickName = data.get("nickname");
                    thirdUserId = data.get("unionid");
                    thirdUserImg = data.get("headimgurl");
                    thirdType = "微信";
                    county = data.get("country");
                    province = data.get("province");
                    city = data.get("city");
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialog(context);
                        sendThird();
                    } else {
                        ToastUtils.show_always(context, "网络失败，请检查网络");
                    }
                } else {
                    thirdNickName = data.get("screen_name");
                    thirdUserId = data.get("openid");
                    thirdUserImg = data.get("profile_image_url");
                    thirdType = "QQ";
                    county = data.get("country");
                    province = data.get("province");
                    city = data.get("city");
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialog(context);
                        sendThird();
                    } else {
                        ToastUtils.show_always(context, "网络失败，请检查网络");
                    }
                }
            } else {
                ToastUtils.show_always(context, "登录失败，请稍后再试！");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            ToastUtils.show_always(context, "登录失败，请稍后再试！");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.show_always(context, "您已取消操作，本程序无法获取到您的个人信息!");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        editUserName = null;
        editPassword = null;
        userName = null;
        password = null;
        dialog = null;
        mShareAPI = null;
        thirdNickName = null;
        thirdUserId = null;
        thirdUserImg = null;
        county = null;
        province = null;
        city = null;
        thirdType = null;
        description = null;
        tag = null;
        setContentView(R.layout.activity_null);
    }
}
