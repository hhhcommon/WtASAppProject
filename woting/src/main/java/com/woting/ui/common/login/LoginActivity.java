package com.woting.ui.common.login;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.woting.common.manager.SharePreferenceManager;
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
    private UMShareAPI mShareAPI;

    private Dialog dialog;// 加载数据对话框
    private EditText editUserName;// 输入 用户名
    private EditText editPassword;// 输入密码

    private String userName;// 用户名
    private String password;// 密码
    private String userId;// 用户 ID
    private String imageUrl;
    private String imageUrlBig;
    private String returnUserName;

    // 三方登录信息
    private String thirdNickName;
    private String thirdUserId;
    private String thirdUserImg;
    private String county;
    private String province;
    private String city;
    private String thirdType;
    private String description;
    private String tag = "LOGIN_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mShareAPI = UMShareAPI.get(context);// 初始化友盟
        setView();
    }

    // 初始化视图
    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);// 返回按钮
        findViewById(R.id.tv_wjmm).setOnClickListener(this);// 忘记密码
        findViewById(R.id.btn_login).setOnClickListener(this);// 登录按钮
        findViewById(R.id.btn_register).setOnClickListener(this);// 注册按钮
        findViewById(R.id.lin_login_wx).setOnClickListener(this);// 微信
        findViewById(R.id.lin_login_qq).setOnClickListener(this);// qq登录
        findViewById(R.id.lin_login_wb).setOnClickListener(this);// 微博登录

        editUserName = (EditText) findViewById(R.id.edittext_username);    // 输入用户名
        editPassword = (EditText) findViewById(R.id.edittext_password);    // 输入密码按钮

        String phoneName = (String) SharePreferenceManager.getSharePreferenceValue(context, "USER_NAME", "USER_NAME", "");
        editUserName.setText(phoneName);
        editUserName.setSelection(editUserName.getText().length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.btn_login:// 登录
                checkData();
                break;
            case R.id.btn_register:// 注册
                startActivityForResult(new Intent(context, RegisterActivity.class), 0);
                break;
            case R.id.tv_wjmm:// 忘记密码
                startActivity(new Intent(context, ForgetPasswordActivity.class));
                break;
            case R.id.lin_login_wx:// 微信登录
                SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
                mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
                    @Override
                    public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
                        ToastUtils.show_allways(context, "认证异常" + arg2.toString());
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
                        mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0, int arg1) {
                        ToastUtils.show_allways(context, "用户退出认证");
                    }
                });
                break;
            case R.id.lin_login_qq:// QQ登录
                SHARE_MEDIA platform1 = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(this, platform1, new UMAuthListener() {
                    @Override
                    public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
                        ToastUtils.show_allways(context, "认证异常" + arg2.toString());
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
                        mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, umAuthListener);
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0, int arg1) {
                        ToastUtils.show_allways(context, "用户退出认证");
                    }
                });
                break;
            case R.id.lin_login_wb:// 新浪微博登录
                SHARE_MEDIA platform2 = SHARE_MEDIA.SINA;
                mShareAPI.doOauthVerify(LoginActivity.this, platform2, new UMAuthListener() {
                    @Override
                    public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
                        ToastUtils.show_allways(context, "认证异常" + arg2.toString());
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
                        mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, umAuthListener);
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0, int arg1) {
                        ToastUtils.show_allways(context, "用户退出认证");
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
            ToastUtils.show_allways(context, "用户名不能为空");
            return;
        }
        if (password == null || password.trim().equals("")) {
            ToastUtils.show_allways(context, "密码不能为空");
            return;
        }
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "登录中");
            send();
        } else {
            ToastUtils.show_allways(context, "网络失败，请检查网络");
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

        VolleyRequest.RequestPost(GlobalConfig.loginUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;
            private String UserNum;
            private String phoneNumber;
            private String gender;// 性别
            private String region;// 区域
            private String birthday;// 生日
            private String age;// 年龄
            private String starSign;// 星座
            private String email;// 邮箱
            private String userSign;// 签名

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    Message = result.getString("Message");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        Editor et = BSApplication.SharedPreferences.edit();
                        et.putString(StringConstant.ISLOGIN, "true");
                        et.putString(StringConstant.PERSONREFRESHB, "true");
                        JSONObject ui;
                        try {
                            String u = result.getString("UserInfo");
                            ui = (JSONObject) new JSONTokener(u).nextValue();
                            if (ui != null) {
                                try {
                                    imageUrl = ui.getString("PortraitMini");
                                    et.putString(StringConstant.IMAGEURL, imageUrl);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    returnUserName = ui.getString("UserName");
                                    et.putString(StringConstant.USERNAME, returnUserName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    UserNum = ui.getString("UserNum");
                                    et.putString(StringConstant.USER_NUM, UserNum);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    imageUrlBig = ui.getString("PortraitBig");
                                    et.putString(StringConstant.IMAGEURBIG, imageUrlBig);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    userId = ui.getString("UserId");
                                    et.putString(StringConstant.USERID, userId);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    phoneNumber = ui.getString("PhoneNum");
                                    et.putString(StringConstant.PHONENUMBER, phoneNumber);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    gender = ui.getString("Sex");
                                    et.putString(StringConstant.GENDERUSR, gender);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    region = ui.getString("Region");
                                    et.putString(StringConstant.REGION, region);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    birthday = ui.getString("Birthday");
                                    et.putString(StringConstant.BIRTHDAY, birthday);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    age = ui.getString("Age");
                                    et.putString(StringConstant.AGE, age);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    starSign = ui.getString("StarSign");
                                    et.putString(StringConstant.STAR_SIGN, starSign);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    email = ui.getString("Email");
                                    et.putString(StringConstant.EMAIL, email);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    userSign = ui.getString("UserSign");
                                    et.putString(StringConstant.USER_SIGN, userSign);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    nickName = ui.getString("NickName");
                                    et.putString(StringConstant.NICK_NAME, nickName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (!et.commit()) {
                                    Log.v("commit", "数据 commit 失败!");
                                }
                                context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                                context.sendBroadcast(new Intent(BroadcastConstants.PUSH_DOWN_COMPLETED));// 刷新下载界面
                                String phoneName = editUserName.getText().toString().trim();
                                SharePreferenceManager.saveBatchSharedPreference(context, "USER_NAME", "USER_NAME", phoneName);
                                InterPhoneControl.sendEntryMessage(context);
                                setResult(1);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (ReturnType != null && ReturnType.equals("1002")) {
                        ToastUtils.show_allways(context, "您输入的用户暂未注册!");
                    } else if (ReturnType != null && ReturnType.equals("1003")) {
                        ToastUtils.show_allways(context, "您输入的密码错误!");
                    } else if (ReturnType != null && ReturnType.equals("0000")) {
                        ToastUtils.show_allways(context, "发生未知错误，请稍后重试!");
                    } else if (ReturnType != null && ReturnType.equals("T")) {
                        ToastUtils.show_allways(context, "发生未知错误，请稍后重试!");
                    } else {
                        if (Message != null && !Message.trim().equals("")) {
                            ToastUtils.show_allways(context, Message + "");
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

        VolleyRequest.RequestPost(GlobalConfig.afterThirdAuthUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;
            private String UserNum;
            private String phoneNumber;
            private String gender;// 性别
            private String region;// 区域
            private String birthday;// 生日
            private String age;// 年龄
            private String starSign;// 星座
            private String email;// 邮箱
            private String userSign;// 签名

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Message = result.getString("Message");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    Editor et = BSApplication.SharedPreferences.edit();
                    et.putString(StringConstant.ISLOGIN, "true");
                    et.putString(StringConstant.PERSONREFRESHB, "true");
                    JSONObject ui;
                    try {
                        String u = result.getString("UserInfo");
                        ui = (JSONObject) new JSONTokener(u).nextValue();
                        if (ui != null) {
                            try {
                                imageUrl = ui.getString("PortraitMini");
                                et.putString(StringConstant.IMAGEURL, imageUrl);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                returnUserName = ui.getString("UserName");
                                et.putString(StringConstant.USERNAME, returnUserName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                UserNum = ui.getString("UserNum");
                                et.putString(StringConstant.USER_NUM, UserNum);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                imageUrlBig = ui.getString("PortraitBig");
                                et.putString(StringConstant.IMAGEURBIG, imageUrlBig);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userId = ui.getString("UserId");
                                et.putString(StringConstant.USERID, userId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                phoneNumber = ui.getString("PhoneNum");
                                et.putString(StringConstant.PHONENUMBER, phoneNumber);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                gender = ui.getString("Sex");
                                et.putString(StringConstant.GENDERUSR, gender);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                region = ui.getString("Region");
                                et.putString(StringConstant.REGION, region);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                birthday = ui.getString("Birthday");
                                et.putString(StringConstant.BIRTHDAY, birthday);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                age = ui.getString("Age");
                                et.putString(StringConstant.AGE, age);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                starSign = ui.getString("StarSign");
                                et.putString(StringConstant.STAR_SIGN, starSign);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                email = ui.getString("Email");
                                et.putString(StringConstant.EMAIL, email);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                userSign = ui.getString("UserSign");
                                et.putString(StringConstant.USER_SIGN, userSign);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                nickName = ui.getString("NickName");
                                et.putString(StringConstant.NICK_NAME, nickName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!et.commit()) {
                                Log.v("commit", "数据 commit 失败!");
                            }
                            context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                            context.sendBroadcast(new Intent(BroadcastConstants.PUSH_DOWN_COMPLETED));// 刷新下载界面
                            InterPhoneControl.sendEntryMessage(context);
                            setResult(1);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_allways(context, "登录失败,请稍后再试!");
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
                    ToastUtils.show_allways(context, "账号注册成功，已进行自动登录!");
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialogph(context, "正在用新浪信息注册");
                        sendThird();
                    } else {
                        ToastUtils.show_allways(context, "网络失败，请检查网络");
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
                        dialog = DialogUtils.Dialogph(context, "正在用微信信息注册");
                        sendThird();
                    } else {
                        ToastUtils.show_allways(context, "网络失败，请检查网络");
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
                        dialog = DialogUtils.Dialogph(context, "正在用qq信息注册");
                        sendThird();
                    } else {
                        ToastUtils.show_allways(context, "网络失败，请检查网络");
                    }
                }
            } else {
                ToastUtils.show_allways(context, "个人信息获取异常!");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            ToastUtils.show_allways(context, "个人信息获取异常!");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.show_allways(context, "您已取消操作，本程序无法获取到您的个人信息!");
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
        userId = null;
        imageUrl = null;
        imageUrlBig = null;
        mShareAPI = null;
        returnUserName = null;
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
