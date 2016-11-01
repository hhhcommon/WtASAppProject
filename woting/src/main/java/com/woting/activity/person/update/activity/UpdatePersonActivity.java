package com.woting.activity.person.update.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.common.constant.StringConstant;

/**
 * 修改个人信息(还未完成，后台接口暂时没有)
 * @author 辛龙
 *         2016年7月19日
 */
public class UpdatePersonActivity extends BaseActivity implements OnClickListener {
    private LinearLayout head_left_btn;
    private LinearLayout lin_gender;
    private Dialog Imagedialog;
    private TextView tv_gender;
    private LinearLayout lin_age;
    private LinearLayout lin_xingzuo;
    private TextView tv_zhanghu;
    private TextView tv_name;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateperson);
        sharedPreferences = getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
        setview();// 设置界面
        setLisener();// 设置监听
        calldialog();// 初始化性别选择对话框
        setdata();
    }

    private void setdata() {
        String username = sharedPreferences.getString(StringConstant.USERNAME, "");
        String userid = sharedPreferences.getString(StringConstant.USERID, "");
        tv_zhanghu.setText(userid);
        tv_name.setText(username);
    }

    private void setLisener() {
        head_left_btn.setOnClickListener(this);
        lin_gender.setOnClickListener(this);
        lin_age.setOnClickListener(this);
        lin_xingzuo.setOnClickListener(this);
    }

    private void setview() {
        head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
        lin_gender = (LinearLayout) findViewById(R.id.lin_gender);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_zhanghu = (TextView) findViewById(R.id.tv_zhanghu);
        tv_name = (TextView) findViewById(R.id.tv_name);
        lin_age = (LinearLayout) findViewById(R.id.lin_age);
        lin_xingzuo = (LinearLayout) findViewById(R.id.lin_xingzuo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 注销登录
                finish();
                break;
            case R.id.lin_gender:
                Imagedialog.show();
                break;
            case R.id.lin_age:
                showMonPicker();
                break;
            case R.id.lin_xingzuo:
                showMonPicker();
                break;
        }
    }

    private void showMonPicker() {

    }

    private void calldialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tv_cancle = (TextView) dialog.findViewById(R.id.tv_cancle);
        TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
        tv_cancle.setText("男");
        tv_confirm.setText("女");
        tv_title.setText("请选择您的性别");
        Imagedialog = new Dialog(context, R.style.MyDialog);
        Imagedialog.setContentView(dialog);
        Imagedialog.setCanceledOnTouchOutside(true);
        Imagedialog.getWindow().setBackgroundDrawableResource(R.color.dialog);

        tv_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_gender.setText("女");
                Imagedialog.dismiss();
            }
        });

        tv_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_gender.setText("男");
                Imagedialog.dismiss();
            }
        });

    }
//	private void datepickdialog() {
//		final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
//		TextView tv_title= (TextView) dialog.findViewById(R.id.tv_title);
//		TextView tv_cancle = (TextView) dialog.findViewById(R.id.tv_cancle);
//		TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
//		tv_cancle.setText("男");
//		tv_confirm.setText("女");
//		tv_title.setText("请选择您的性别");
//		Imagedialog = new Dialog(context, R.style.MyDialog);
//		Imagedialog.setContentView(dialog);
//		Imagedialog.setCanceledOnTouchOutside(true);
//		Imagedialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
//		tv_confirm.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				tv_gender.setText("女");
//				Imagedialog.dismiss();
//			}
//		});

//		tv_cancle.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				tv_gender.setText("男");
//				Imagedialog.dismiss();
//			}
//		});
//	}
}
