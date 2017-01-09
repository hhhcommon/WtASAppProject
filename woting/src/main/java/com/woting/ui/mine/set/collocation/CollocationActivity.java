package com.woting.ui.mine.set.collocation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.constant.CollocationConstant;
import com.woting.ui.baseactivity.BaseActivity;

/**
 * 设置
 *
 * @author 辛龙
 *         2016年2月26日
 */
public class CollocationActivity extends BaseActivity implements OnClickListener {


    private TextView tv_toast_false, tv_toast_true, tv_pcd_1, tv_pcd_2, tv_pcd_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collocation);
        initViews();
    }

    // 初始化控件
    private void initViews() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);          // 返回

        tv_toast_false = (TextView) findViewById(R.id.tv_toast_false);
        tv_toast_false.setOnClickListener(this);
        tv_toast_true = (TextView) findViewById(R.id.tv_toast_true);
        tv_toast_true.setOnClickListener(this);

        tv_pcd_1 = (TextView) findViewById(R.id.tv_pcd_1);
        tv_pcd_2 = (TextView) findViewById(R.id.tv_pcd_2);
        tv_pcd_3 = (TextView) findViewById(R.id.tv_pcd_3);
        tv_pcd_1.setOnClickListener(this);
        tv_pcd_2.setOnClickListener(this);
        tv_pcd_3.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setView();// 设置界面
    }

    private void setView() {
        changeToastView();      // 设置吐司界面更改
        changeHMTypeView();     // 设置设备类型界面更改
    }

    private void changeToastView() {
        // 是否弹出提示框，0提示，1不提示
        String isToast = BSApplication.SharedPreferences.getString(CollocationConstant.isToast, "1");
        if (isToast != null && !isToast.equals("") && isToast.trim().equals("0")) {
            tv_toast_false.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
            tv_toast_true.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
        } else {
            tv_toast_true.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
            tv_toast_false.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
        }
    }

    private void changeHMTypeView() {
        // 终端类型1=app,2=设备，3=pc
        String PCDType = BSApplication.SharedPreferences.getString(CollocationConstant.PCDType, "1");
        if (PCDType != null && !PCDType.equals("") && PCDType.trim().equals("1")) {
            tv_pcd_1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
            tv_pcd_2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
            tv_pcd_3.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
        } else if (PCDType != null && !PCDType.equals("") && PCDType.trim().equals("2")) {
            tv_pcd_1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
            tv_pcd_2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
            tv_pcd_3.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
        } else if (PCDType != null && !PCDType.equals("") && PCDType.trim().equals("3")) {
            tv_pcd_1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
            tv_pcd_2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
            tv_pcd_3.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:        // 返回
                finish();
                break;
            case R.id.tv_toast_false:        // 不吐司
                setToast(false);
                break;
            case R.id.tv_toast_true:        // 吐司
                setToast(true);
                break;
            case R.id.tv_pcd_1:        // 设备类型为APP
                setPcd("1");
                break;
            case R.id.tv_pcd_2:        // 设备类型为硬件
                setPcd("2");
                break;
            case R.id.tv_pcd_3:        // 设备类型为PC
                setPcd("3");
                break;
        }
    }

    private void setToast(boolean toast) {
        if (toast) {
            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
            et.putString(CollocationConstant.isToast, "0");
            et.commit();
        } else {
            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
            et.putString(CollocationConstant.isToast, "1");
            et.commit();
        }
        changeToastView();
    }

    private void setPcd(String pcdType) {
        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putString(CollocationConstant.PCDType, pcdType);
        et.commit();
        changeHMTypeView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.activity_null);
    }
}
