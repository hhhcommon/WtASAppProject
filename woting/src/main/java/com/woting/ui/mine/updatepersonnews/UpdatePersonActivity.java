package com.woting.ui.mine.updatepersonnews;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.common.application.BSApplication;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.ToastUtils;

/**
 * 修改个人信息(还未完成，后台接口暂时没有)
 * @author 辛龙
 * 2016年7月19日
 */
public class UpdatePersonActivity extends BaseActivity implements OnClickListener {
    private Dialog genderDialog;
    private TextView textGender;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.lin_gender:// 性别
                genderDialog.show();
                break;
            case R.id.lin_age:// 年龄
                ToastUtils.show_allways(context, "设置年龄");
                break;
            case R.id.lin_xingzuo:// 星座
                ToastUtils.show_allways(context, "设置星座");
                break;
            case R.id.tv_confirm:
                textGender.setText("女");
                genderDialog.dismiss();
                break;
            case R.id.tv_cancle:
                textGender.setText("男");
                genderDialog.dismiss();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateperson);

        genderDialog();
        initView();
    }

    // 初始化性别选择对话框
    private void genderDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        TextView textTitle = (TextView) dialog.findViewById(R.id.tv_title);
        textTitle.setText("请选择您的性别");

        TextView textCancel = (TextView) dialog.findViewById(R.id.tv_cancle);
        textCancel.setText("男");
        textCancel.setOnClickListener(this);

        TextView textConfirm = (TextView) dialog.findViewById(R.id.tv_confirm);
        textConfirm.setText("女");
        textConfirm.setOnClickListener(this);

        genderDialog = new Dialog(context, R.style.MyDialog);
        genderDialog.setContentView(dialog);
        genderDialog.setCanceledOnTouchOutside(true);
        genderDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    // 设置界面
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        findViewById(R.id.lin_gender).setOnClickListener(this);
        findViewById(R.id.lin_age).setOnClickListener(this);
        findViewById(R.id.lin_xingzuo).setOnClickListener(this);

        String userId = BSApplication.SharedPreferences.getString(StringConstant.USERID, "");// 账号 用户 ID
        TextView textAccount  = (TextView) findViewById(R.id.tv_zhanghu);
        textAccount.setText(userId);

        String userName = BSApplication.SharedPreferences.getString(StringConstant.USERNAME, "");// 用户昵称
        TextView textName = (TextView) findViewById(R.id.tv_name);
//        if(userName.equals("")) {
//            userName = (String) SharePreferenceManager.getSharePreferenceValue(context, "USER_NAME", "USER_NAME", "");
//        }
        textName.setText(userName);

        textGender = (TextView) findViewById(R.id.tv_gender);// 性别
    }
}
