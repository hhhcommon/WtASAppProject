package com.woting.ui.common.login.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.ToastUtils;
import com.woting.ui.base.baseactivity.BaseActivity;
import com.woting.ui.base.baseinterface.CheckDataInterface;
import com.woting.ui.common.login.presenter.LoginPresenter;
import com.woting.ui.mine.person.forgetpassword.ForgetPasswordActivity;
import com.woting.ui.common.register.RegisterActivity;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.util.DialogUtils;

/**
 * 登录界面
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class LoginView extends BaseActivity implements LoginViewInterface,CheckDataInterface, OnClickListener {

    private Dialog dialog;             // 加载数据对话框
    private Button btn_login;          // 登录按钮
    private EditText editUserName;     // 输入 用户名
    private EditText editPassword;     // 输入密码
    private String tag = "LOGIN_VOLLEY_REQUEST_CANCEL_TAG";
    private LoginPresenter loginPresenter;
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setView();
        setEditListener();                                                  // 设置输入框的监听
        loginPresenter = new LoginPresenter(this);
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

    }

    private void setEditListener() {
        // 用户名的监听
        editUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userName = editUserName.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                loginPresenter.getBtView(userName, password);
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
                String userName = editUserName.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                loginPresenter.getBtView(userName, password);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:             // 返回
                finish();
                break;
            case R.id.btn_login:                 // 登录
                checkData();                     // 验证数据
                break;
            case R.id.btn_register:              // 注册
                startActivityForResult(new Intent(context, RegisterActivity.class), 0);
                break;
            case R.id.tv_wjmm:                   // 忘记密码
                startActivity(new Intent(context, ForgetPasswordActivity.class));
                break;
            case R.id.lin_login_wx:              // 微信登录
                loginPresenter.wxLogin();
                break;
            case R.id.lin_login_qq:              // QQ登录
                loginPresenter.qqLogin();
                break;
            case R.id.lin_login_wb:              // 新浪微博登录
                loginPresenter.wbLogin();
                break;
        }
    }

    /**
     * 验证数据
     */
    @Override
    public void checkData() {
        String userName = editUserName.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        if (loginPresenter.checkData(userName, password)) {
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                loginPresenter.sendLogin(userName, password);
            } else {
                ToastUtils.show_always(this, "网络连接失败，请检查网络");
            }
        }
    }

    /**
     * 更改按钮样式
     *
     * @param bt true/false
     */
    public void setBtView(boolean bt) {
        if (bt) {
            btn_login.setBackgroundResource(R.drawable.zhuxiao_press);
        } else {
            btn_login.setBackgroundResource(R.drawable.bg_graybutton);
        }
    }

    /**
     * 展示加载提示
     */
    @Override
    public void showDialog() {
        dialog = DialogUtils.Dialog(this);
    }

    /**
     * 隐藏加载提示
     */
    @Override
    public void removeDialog() {
        if (dialog != null) dialog.dismiss();
    }

    /**
     * 获取当前页面的加载状态
     *
     * @return
     */
    @Override
    public boolean getCancelRequest() {
        return isCancelRequest;
    }

    /**
     * 获取当前页面标签
     *
     * @return
     */
    @Override
    public String getTag() {
        return tag;
    }

    /**
     * 关闭当前activity
     */
    public void closeActivity() {
        setResult(1);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginPresenter.OnShareActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: // 从注册界面返回数据，注册成功
                if (resultCode == 1) {
                    closeActivity();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        setContentView(R.layout.activity_null);
    }


}
