package com.woting.ui.mine.person.modifypassword;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 修改密码
 * 作者：xinlong on 2016/7/19 21:18
 * 邮箱：645700751@qq.com
 */
public class ModifyPasswordActivity extends AppBaseActivity implements OnClickListener {
    private Dialog dialog;
    private EditText editOldPassword;// 输入 旧密码
    private EditText editNewPassword;// 输入新密码
    private EditText editNewPasswordConfirm;// 输入 确认新密码

    private String oldPassword;// 旧密码
    private String newPassword;// 新密码
    private String passwordConfirm;// 确定新密码
    private String tag = "MODIFY_PASSWORD_VOLLEY_REQUEST_CANCEL_TAG";

    private boolean isCancelRequest;
    private Button btn_modifypassword;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.btn_modifypassword:// 确定修改密码
                if (CommonHelper.checkNetwork(context) && checkData()) {
                    send();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        initView();
        setEditListener();
    }

    // 初始化视图
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);                  // 返回
        btn_modifypassword = (Button) findViewById(R.id.btn_modifypassword);        // 确定修改密码
        btn_modifypassword.setOnClickListener(this);

        editOldPassword = (EditText) findViewById(R.id.edit_oldpassword);           // 输入 旧密码
        editNewPassword = (EditText) findViewById(R.id.edit_newpassword);           // 输入 新密码
        editNewPasswordConfirm = (EditText) findViewById(R.id.edit_confirmpassword);// 输入 确定新密码
    }

    private void setEditListener() {
        // 输入 旧密码
        editOldPassword.addTextChangedListener(new TextWatcher() {
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

        // 输入 新密码
        editNewPassword.addTextChangedListener(new TextWatcher() {
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

        // 输入 确定新密码
        editNewPasswordConfirm.addTextChangedListener(new TextWatcher() {
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
        String password_old = editOldPassword.getText().toString().trim();
        String password = editNewPassword.getText().toString().trim();
        String password_qz = editNewPasswordConfirm.getText().toString().trim();

        if (password_old != null && !password_old.equals("") && password_old.length() > 5) {
            if (password != null && !password.equals("") && password.length() > 5) {
                if (password_qz != null && !password_qz.equals("") && password_qz.length() > 5) {
                    btn_modifypassword.setBackgroundResource(R.drawable.zhuxiao_press);
                } else {
                    btn_modifypassword.setBackgroundResource(R.drawable.bg_graybutton);
                }
            } else {
                btn_modifypassword.setBackgroundResource(R.drawable.bg_graybutton);
            }
        } else {
            btn_modifypassword.setBackgroundResource(R.drawable.bg_graybutton);
        }
    }

    // 检查数据的正确性
    protected boolean checkData() {
        oldPassword = editOldPassword.getText().toString().trim();
        newPassword = editNewPassword.getText().toString().trim();
        passwordConfirm = editNewPasswordConfirm.getText().toString().trim();
        if ("".equalsIgnoreCase(oldPassword)) {
            ToastUtils.show_always(context, "请输入您的旧密码");
            return false;
        }
        if ("".equalsIgnoreCase(newPassword)) {
            ToastUtils.show_always(context, "请输入您的新密码");
            return false;
        }
        if (newPassword.length() < 6) {
            ToastUtils.show_always(context, "密码请输入六位以上");
            return false;
        }
        if ("".equalsIgnoreCase(newPassword)) {
            ToastUtils.show_always(context, "请再次输入密码");
            return false;
        }
        if (!newPassword.equals(passwordConfirm)) {
            ToastUtils.show_always(context, "您两次输入的密码不一样!");
            return false;
        }
        if (passwordConfirm.length() < 6) {
            ToastUtils.show_always(context, "密码请输入六位以上");
            return false;
        }
        return true;
    }

    protected void send() {
        dialog = DialogUtils.Dialogph(context, "正在提交请求");
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("OldPassword", oldPassword);// 待改
            jsonObject.put("NewPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.modifyPasswordUrl, tag, jsonObject, new VolleyCallback() {
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
                    ToastUtils.show_always(context, "密码修改成功");
                    finish();
                } else {
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_always(context, Message + "");
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
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        editOldPassword = null;
        editNewPassword = null;
        editNewPasswordConfirm = null;
        oldPassword = null;
        newPassword = null;
        passwordConfirm = null;
        dialog = null;
        tag = null;
        setContentView(R.layout.activity_null);
    }
}
