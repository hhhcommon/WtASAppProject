package com.woting.ui.mine.person.phonecheck;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 变更手机号
 * @author 辛龙
 * 2016年7月19日
 */
public class PhoneCheckActivity extends AppActivity implements OnClickListener {
    private CountDownTimer mCountDownTimer;         // 计时器

    private Dialog dialog;                          // 加载数据对话框
    private EditText editPhoneNumber;               // 输入新手机号码
    private EditText editVerificationCode;          // 输入 验证码
    private TextView textGetVerificationCode;       // 获取验证码
    private TextView textResend;                    // 重新发送验证码
    private Button btUpdate;                        // 修改按钮

    private String tag = "MODIFY_PHONE_NUMBER_VOLLEY_REQUEST_CANCEL_TAG";
    private String phoneNumber;                     // 新手机号

    private String verificationCode;                // 验证码
    private int sendType = 1;                       // == 1 为第一次发送验证码  == 2 为重新发送验证码
    private boolean isCancelRequest;
    private boolean isGetCode;                      // 判断是否已经获取验证码
    private String phoneNumberNew;
//  private int ViewType = 2;                       // == 1 为已经有手机号  == 2 为无手机号
    private TextView tv_Phone_Desc;

    @Override
    protected int setViewId() {
        return R.layout.activity_modify_phone_number;
    }

    @Override
    protected void init() {
        setTitle("绑定手机号");
        editPhoneNumber = (EditText) findViewById(R.id.edit_phone_number);                  // 新手机号码
        editVerificationCode = (EditText) findViewById(R.id.edit_verification_code);        // 验证码
        editPhoneNumber.addTextChangedListener(new MyEditListener());
        editVerificationCode.addTextChangedListener(new MyEditListener());
        textGetVerificationCode = (TextView) findViewById(R.id.text_get_verification_code); // 获取验证码
        textGetVerificationCode.setOnClickListener(this);

        textResend = (TextView) findViewById(R.id.text_resend);                             // 重新发送验证码
        textResend.setOnClickListener(this);
        btUpdate = (Button) findViewById(R.id.btn_confirm);                                 // 确定修改
        btUpdate.setOnClickListener(this);

        tv_Phone_Desc=(TextView) findViewById(R.id.tv_Phone_Desc);
        handleIntent();
    }

    private void handleIntent() {
        String phoneType = getIntent().getStringExtra("PhoneType");
        if(!TextUtils.isEmpty(phoneType)){
           if(phoneType.equals("1")){
               phoneNumber = getIntent().getStringExtra("PhoneNumber");// 有手机号
//               ViewType = 1;
               tv_Phone_Desc.setText("当前绑定的手机号码为："+phoneNumber.replaceAll("(\\d{3})\\d{6}(\\d{2})","$1******$2")
                       + "\n更换手机号后，下次登录可以使用新手机号码登录。");
               editPhoneNumber.setHint("请输入新的手机号码");
           }
        }
    }

    @Override
    public void onClick(View v) {
        // 以下操作都需要网络 所以没有网络就不需要继续验证直接提示用户设置网络
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            ToastUtils.show_allways(context, "网络失败，请检查网络");
            return;
        }
        switch (v.getId()) {
            case R.id.btn_confirm:                  // 确定修改
                if (!isGetCode) {
                    ToastUtils.show_allways(context, "请先获取验证码!");
                    return;
                }
                checkValue();
                break;
            case R.id.text_get_verification_code:   // 获取验证码
                checkVerificationCode();
                break;
        }
    }

    // 验证码手机号正确就获取验证码
    private void checkVerificationCode() {
        phoneNumberNew = editPhoneNumber.getText().toString().trim();
        if ("".equalsIgnoreCase(phoneNumberNew) || phoneNumberNew.length() != 11) {// 检查输入数字是否为手机号
            ToastUtils.show_allways(context, "请输入正确的手机号码!");
            return;
        }
        dialog = DialogUtils.Dialogph(context, "正在获取验证码");
        sendVerificationCode();                                         // 发送网络请求 获取验证码
    }

    // 检查数据的正确性
    private void checkValue() {
        if (isComplete(1)) {
            dialog = DialogUtils.Dialogph(context, "正在验证手机号");
            sendRequest();
        }
    }

    // 请求网络获取验证码
    private void sendVerificationCode() {
        String url;
        if (sendType == 1) {
            url = GlobalConfig.registerByPhoneNumUrl;       // 第一次发送验证码接口
        } else {
            url = GlobalConfig.reSendPhoneCheckCodeNumUrl;  // 重新发送验证码接口
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PhoneNum", phoneNumberNew);
            if (sendType == 2) {
                jsonObject.put("OperType", "1");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(url, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        sendType = 2;// 再次发送验证码
                        isGetCode = true;
                        ToastUtils.show_allways(context, "验证码已经发送");
                        timerDown();
                        textGetVerificationCode.setVisibility(View.GONE);
                        textResend.setVisibility(View.VISIBLE);
                    } else if (returnType != null && returnType.equals("T")) {
                        ToastUtils.show_allways(context, "获取异常，请确认后重试!");
                    } else if (returnType != null && returnType.equals("1002")) {
                        ToastUtils.show_allways(context, "此号码已经注册");
                    } else {
                        try {
                            String message = result.getString("Message");
                            if (message != null && !message.trim().equals("")) {
                                ToastUtils.show_allways(context, message + "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
            jsonObject.put("PhoneNum", phoneNumber);
            jsonObject.put("CheckCode", verificationCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.checkPhoneCheckCodeUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        dialog = DialogUtils.Dialogph(context, "正在绑定手机号");
                        sendBinding();
                    } else if (returnType != null && returnType.equals("T")) {
                        ToastUtils.show_allways(context, "数据出错了,请稍后再试");
                    } else if (returnType != null && returnType.equals("1002")) {
                        ToastUtils.show_allways(context, "验证码错误!");
                    } else {
                        try {
                            String message = result.getString("Message");
                            if (message != null && !message.trim().equals("")) {
                                ToastUtils.show_allways(context, message + "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
    private void sendBinding() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PhoneNum", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.updateUserUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        ToastUtils.show_allways(context, "手机号修改成功!");
                        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
                        et.putString(StringConstant.PHONENUMBER, phoneNumber);
                        if (!et.commit()) {
                            Log.w("commit", " 数据 commit 失败!");
                        }
                        setResult(1);
                        finish();
                    } else {
                        ToastUtils.show_allways(context, "手机号修改失败!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 验证码时间
    private void timerDown() {
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textResend.setText(millisUntilFinished / 1000 + "s后重新发送");
            }

            @Override
            public void onFinish() {
                textResend.setVisibility(View.GONE);
                textGetVerificationCode.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    //    用正则验证手机号
    //    private boolean isMobile(String str) {
    //        Pattern pattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
    //        Matcher matcher = pattern.matcher(str);
    //        return matcher.matches();
    //    }

    // 判断数据是否填写完整
    private boolean isComplete(int type) {
        verificationCode = editVerificationCode.getText().toString().trim();
        phoneNumber = editPhoneNumber.getText().toString().trim();

        if ("".equalsIgnoreCase(phoneNumber) || phoneNumber.length() != 11) {// 检查输入数字是否为手机号
            if(type == 1) ToastUtils.show_allways(context, "请输入新的正确的手机号码!");
            return false;
        } else if ("".equalsIgnoreCase(verificationCode) || verificationCode.length() != 6) {
            if(type == 1) ToastUtils.show_allways(context, "验证码不正确!");
            return false;
        } else {
            return true;
        }
    }

    // 输入框监听
    class MyEditListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isComplete(2)) {
                btUpdate.setBackgroundResource(R.drawable.wt_commit_button_background);
            } else {
                btUpdate.setBackgroundResource(R.drawable.bg_graybutton);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);// 根据 TAG 取消网络请求
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }
}
