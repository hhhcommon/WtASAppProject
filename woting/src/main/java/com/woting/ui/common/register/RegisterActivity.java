package com.woting.ui.common.register;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 注册
 * @author 辛龙
 * 2016年8月8日
 */
public class RegisterActivity extends BaseActivity implements OnClickListener, TextWatcher {
    private CountDownTimer mCountDownTimer;// 再次获取验证码时间

    private Dialog dialog;// 加载数据对话框
    private EditText mEditTextName;// 输入 用户名
    private EditText mEditTextPassWord;// 输入 密码
    private EditText mEditTextUserPhone;// 输入 手机号
    private EditText editVerify;// 输入 验证码
    private TextView textGetYzm;// 获取验证码
    private TextView textRegister;// 注册
    private TextView textCxFaSong;// 重新获取验证码
    private TextView textNext;// 注册

    private String password;// 密码
    private String userName;// 用户名
    private String phoneNum;// 手机号
    private String verifyCode;// 验证码
    private String phoneNumber;
    private String tempVerify;
    private String tag = "REGISTER_VOLLEY_REQUEST_CANCEL_TAG";
    private int sendType = -1;// == -1 首次获取验证码 == 其他 再次发送验证码
    private int verifyStatus = -1;// == -1 没有发送过验证码  == 1 成功
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    // 设置界面
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);// 返回

        mEditTextName = (EditText) findViewById(R.id.edittext_username);// 输入 用户名
        mEditTextPassWord = (EditText) findViewById(R.id.edittext_password);// 输入 密码
        mEditTextUserPhone = (EditText) findViewById(R.id.edittext_userphone);// 输入 手机号

        editVerify = (EditText) findViewById(R.id.et_yzm);// 输入 验证码
        editVerify.addTextChangedListener(this);

        textGetYzm = (TextView) findViewById(R.id.tv_getyzm);// 获取验证码
        textGetYzm.setOnClickListener(this);

        textCxFaSong = (TextView) findViewById(R.id.tv_cxfasong);// 重新获取验证码
        textNext = (TextView) findViewById(R.id.tv_next);

        textRegister = (TextView) findViewById(R.id.tv_register);// 注册
        textRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.tv_register:// 验证数据
                checkValue();
                break;
            case R.id.tv_getyzm:// 检查手机号是否为空，或者是否是一个正常手机号
                checkYzm();
                break;
        }
    }

    // 检查数据的正确性
    private void checkValue() {
        verifyCode = editVerify.getText().toString().trim();
        userName = mEditTextName.getText().toString().trim();
        password = mEditTextPassWord.getText().toString().trim();
        phoneNum = mEditTextUserPhone.getText().toString().trim();

        if ("".equalsIgnoreCase(phoneNum) || !isMobile(phoneNum)) {
            ToastUtils.show_allways(context, "手机号码不正确!");
            return;
        }
        if (!phoneNum.equals(phoneNumber)) {
            ToastUtils.show_allways(context, "请输入您之前获取验证的手机号码");
            return;
        }
        if (userName == null || userName.trim().equals("") || userName.length() < 3) {
            ToastUtils.show_allways(context, "用户名格式不正确!");
            return;
        }
        if (password == null || password.trim().equals("") || password.length() < 6) {
            ToastUtils.show_allways(context, "请输入六位以上密码!");
            return;
        }
        if ("".equalsIgnoreCase(verifyCode) || verifyCode.length() != 6) {
            ToastUtils.show_allways(context, "请输入正确的验证码!");
            return;
        }
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在验证手机号", dialog);
            sendRequest();
        } else {
            ToastUtils.show_allways(context, "网络失败，请检查网络");
        }
    }

    // 验证手机号通过则进行注册
    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PhoneNum", phoneNum);
            jsonObject.put("CheckCode", verifyCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.checkPhoneCheckCodeUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    dialog = DialogUtils.Dialogph(context, "注册中", dialog);
                    send();
                } else if (ReturnType != null && ReturnType.equals("T")) {
                    ToastUtils.show_allways(context, "出错了，请您稍后再试!");
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_allways(context, "您输入的验证码不匹配!");
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

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("UserName", userName);
            jsonObject.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.registerUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;
            private String userId;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                try {
                    Message = result.getString("Message");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                try {
                    userId = result.getString("UserId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    Editor et = BSApplication.SharedPreferences.edit();
                    et.putString(StringConstant.USERID, userId);
                    et.putString(StringConstant.USERNAME, userName);
                    et.putString(StringConstant.USERPHONENUMBER, phoneNum);
                    et.putString(StringConstant.ISLOGIN, "true");
                    et.putString(StringConstant.PERSONREFRESHB, "true");
                    if (!et.commit()) {
                        Log.v("commit", "数据 commit 失败!");
                    }
                    context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                    InterPhoneControl.sendEntryMessage(context);
                    setResult(1);
                    finish();
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_allways(context, "当前用户暂未注册!");
                } else if (ReturnType != null && ReturnType.equals("1003")) {
                    ToastUtils.show_allways(context, "您输入的用户名重复了!");
                } else if (ReturnType != null && ReturnType.equals("0000")) {
                    ToastUtils.show_allways(context, "发生未知错误，请稍后重试");
                } else if (ReturnType != null && ReturnType.equals("T")) {
                    ToastUtils.show_allways(context, "发生未知错误，请稍后重试");
                } else {
                    ToastUtils.show_allways(context, Message + "");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 获取验证码
    private void checkYzm() {
        phoneNumber = mEditTextUserPhone.getText().toString().trim();
        if (tempVerify == null) {
            tempVerify = phoneNumber;
        } else {
            if (!tempVerify.equals(phoneNumber)) {
                sendType = -1;
                tempVerify = phoneNumber;
            }
        }
        if ("".equalsIgnoreCase(phoneNumber) || !isMobile(phoneNumber)) {
            ToastUtils.show_allways(context, "请输入正确的手机号!");
            return;
        }
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在验证手机号", dialog);
            getVerifyCode();
        } else {
            ToastUtils.show_short(context, "网络失败，请检查网络");
        }
    }

    // 发送获取验证码请求
    private void getVerifyCode() {
        String url;
        if(sendType == -1) {
            url = GlobalConfig.registerByPhoneNumUrl;// 第一次获取验证码
        } else {
            url = GlobalConfig.reSendPhoneCheckCodeNumUrl;// 再次获取验证码
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PhoneNum", phoneNumber);
            if(sendType != -1) {
                jsonObject.put("OperType", 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(url, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    ToastUtils.show_allways(context, "验证码已经发送!");
                    timerDown();
                    sendType = 2;
                    verifyStatus = 1;
                    textGetYzm.setVisibility(View.GONE);
                    textCxFaSong.setVisibility(View.VISIBLE);
                } else if (ReturnType != null && ReturnType.equals("T")) {
                    ToastUtils.show_allways(context, "数据出错了,请您稍后再试!");
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_allways(context, "此号码已经注册!");
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

    // 验证手机号的方法
    public static boolean isMobile(String str) {
        Pattern pattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    // 再次获取验证码时间
    private void timerDown() {
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timeString = millisUntilFinished / 1000 + "s后重新发送";
                textCxFaSong.setText(timeString);
            }

            @Override
            public void onFinish() {
                textCxFaSong.setVisibility(View.GONE);
                textGetYzm.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 6 && phoneNumber != null && !phoneNumber.equals("")) {
            if (verifyStatus == 1) {
                textNext.setVisibility(View.GONE);
                textRegister.setVisibility(View.VISIBLE);
            } else {
                ToastUtils.show_allways(context, "请点击获取验证码，获取验证码信息!");
            }
        } else {
            textRegister.setVisibility(View.GONE);
            textNext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mEditTextName = null;
        mEditTextPassWord = null;
        password = null;
        userName = null;
        context = null;
        dialog = null;
        textRegister = null;
        phoneNum = null;
        mEditTextUserPhone = null;
        editVerify = null;
        textGetYzm = null;
        verifyCode = null;
        textCxFaSong = null;
        textNext = null;
        phoneNumber = null;
        tempVerify = null;
        tag = null;
        setContentView(R.layout.activity_null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
