package com.woting.ui.mine.phonecheck;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 变更手机号
 * @author 辛龙
 * 2016年7月19日
 */
public class PhoneCheckActivity extends BaseActivity implements OnClickListener, TextWatcher {
    private CountDownTimer mCountDownTimer;// 再次获取验证码时间

    private Dialog dialog;// 加载数据对话框
    private EditText editPhoneNum;// 输入用户手机号
    private EditText editVerifyCode;// 输入 验证码
    private TextView textGetVerifyCode;// 获取验证码
    private TextView textNext;// 下一步
    private TextView textCxFaSong;// 重新发送验证码  不可点击
    private TextView textNextDefault;// 下一步 不可点击

    private String verifyCode;// 验证码
    private String phoneNum;// 手机号
    private String tag = "PHONE_CHECK_VOLLEY_REQUEST_CANCEL_TAG";
    private int sendType = 1;// sendType == 1 首次发送验证码 sendType == 2 重发验证码
    private boolean isCancelRequest;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.tv_getyzm:
                checkVerifyCode();
                break;
            case R.id.tv_next:// 下一步
                checkValue();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonecheck);
        initView();
    }

    // 设置界面
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);

        editVerifyCode = (EditText) findViewById(R.id.et_yzm);
        editVerifyCode.addTextChangedListener(this);

        textGetVerifyCode = (TextView) findViewById(R.id.tv_getyzm);
        textGetVerifyCode.setOnClickListener(this);

        textNext = (TextView) findViewById(R.id.tv_next);
        textNext.setOnClickListener(this);

        editPhoneNum = (EditText) findViewById(R.id.et_phonenum);
        textCxFaSong = (TextView) findViewById(R.id.tv_cxfasong);
        textNextDefault = (TextView) findViewById(R.id.tv_next_default);
    }

    // 验证码与手机号匹配
    private void checkVerifyCode() {
        phoneNum = editPhoneNum.getText().toString().trim();
        if ("".equalsIgnoreCase(phoneNum) || !isMobile(phoneNum)) {
            ToastUtils.show_allways(context, "请输入正确的手机号!");
            return;
        }
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在验证手机号", dialog);
            sendGetVerifyCode();
        } else {
            ToastUtils.show_allways(context, "网络失败，请检查网络");
        }
    }

    // 检查输入到页面的信息是否符合接口返回的结果进行验证
    private void checkValue() {
        verifyCode = editVerifyCode.getText().toString().trim();
        if ("".equalsIgnoreCase(phoneNum) || !isMobile(phoneNum)) {
            ToastUtils.show_allways(context, "请输入正确的手机号码!");
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
            ToastUtils.show_short(context, "网络失败，请检查网络");
        }
    }

    // 获取验证码
    private void sendGetVerifyCode() {
        String url;
        if(sendType == 1) {
            url = GlobalConfig.retrieveByPhoneNumUrl;
        } else {
            url = GlobalConfig.reSendPhoneCheckCodeNumUrl;
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PhoneNum", phoneNum);
            if(sendType == 2) {
                jsonObject.put("OperType", "1");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(url, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    ToastUtils.show_allways(context, "验证码已经发送");
                    sendType = 2;
                    timerDown();
                    editPhoneNum.setEnabled(false);
                    textGetVerifyCode.setVisibility(View.GONE);
                    textCxFaSong.setVisibility(View.VISIBLE);
                } else if (ReturnType != null && ReturnType.equals("T")) {
                    ToastUtils.show_allways(context, "异常返回值");
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_allways(context, "此手机号在系统内没有注册");
                } else {
                    ToastUtils.show_allways(context, "验证码发送失败,请重试!");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 提交数据到服务器进行验证
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
                    Message = result.getString("Message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    dialog = DialogUtils.Dialogph(context, "正在修改绑定手机号", dialog);
                    sendBinding();
                } else if (ReturnType != null && ReturnType.equals("T")) {
                    ToastUtils.show_allways(context, "异常返回值");
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_allways(context, "验证码不匹配");
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

    // 修改手机号方法 利用目前的修改手机号接口
    protected void sendBinding() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PhoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.bindExtUserUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                    Message = result.getString("Message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    ToastUtils.show_allways(context, "手机号已经成功修改为" + phoneNum);
                    finish();
                } else {
                    ToastUtils.show_allways(context, "" + Message);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
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
                textGetVerifyCode.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 6 && phoneNum != null && !phoneNum.equals("")) {
            textNextDefault.setVisibility(View.GONE);
            textNext.setVisibility(View.VISIBLE);
        } else {
            textNext.setVisibility(View.GONE);
            textNextDefault.setVisibility(View.VISIBLE);
        }
    }

    // 验证手机号的方法
    public static boolean isMobile(String str) {
        Pattern pattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: //从注册界面返回数据，注册成功
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
        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        editPhoneNum = null;
        editVerifyCode = null;
        textGetVerifyCode = null;
        textNext = null;
        phoneNum = null;
        dialog = null;
        textCxFaSong = null;
        verifyCode = null;
        textNextDefault = null;
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
