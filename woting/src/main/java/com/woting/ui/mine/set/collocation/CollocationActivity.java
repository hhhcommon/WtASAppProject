package com.woting.ui.mine.set.collocation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.CollocationConstant;
import com.woting.common.util.ToastUtils;
import com.woting.ui.base.baseactivity.BaseActivity;

/**
 * 配置设置
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class CollocationActivity extends BaseActivity implements OnClickListener {


    private TextView tv_toast_false, tv_toast_true, tv_pcd_1, tv_pcd_2, tv_pcd_3,
                     tv_port_show,tv_socketUrl_show,tv_httpUrl_show,tv_uploadUrl_show;
    private EditText et_socketPort,et_socketUrl,et_httpUrl,et_uploadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collocation);
        initViews();
    }

    // 初始化控件
    private void initViews() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);          // 返回

        // 吐司设置
        tv_toast_false = (TextView) findViewById(R.id.tv_toast_false);
        tv_toast_false.setOnClickListener(this);
        tv_toast_true = (TextView) findViewById(R.id.tv_toast_true);
        tv_toast_true.setOnClickListener(this);

        // PCD类型设置
        tv_pcd_1 = (TextView) findViewById(R.id.tv_pcd_1);
        tv_pcd_2 = (TextView) findViewById(R.id.tv_pcd_2);
        tv_pcd_3 = (TextView) findViewById(R.id.tv_pcd_3);
        tv_pcd_1.setOnClickListener(this);
        tv_pcd_2.setOnClickListener(this);
        tv_pcd_3.setOnClickListener(this);

        // socket端口号设置
        tv_port_show = (TextView) findViewById(R.id.tv_port_show);
        et_socketPort = (EditText) findViewById(R.id.et_socketPort);
        TextView tv_port_set = (TextView) findViewById(R.id.tv_port_set);
        tv_port_set.setOnClickListener(this);

        // socket路径设置
        tv_socketUrl_show = (TextView) findViewById(R.id.tv_socketUrl_show);
        et_socketUrl = (EditText) findViewById(R.id.et_socketUrl);
        TextView tv_socketUrl_set = (TextView) findViewById(R.id.tv_socketUrl_set);
        tv_socketUrl_set.setOnClickListener(this);

        // http路径设置
        tv_httpUrl_show = (TextView) findViewById(R.id.tv_httpUrl_show);
        et_httpUrl = (EditText) findViewById(R.id.et_httpUrl);
        TextView tv_httpUrl_set = (TextView) findViewById(R.id.tv_httpUrl_set);
        tv_httpUrl_set.setOnClickListener(this);

        // 文件上传路径设置
        tv_uploadUrl_show = (TextView) findViewById(R.id.tv_uploadUrl_show);
        et_uploadUrl = (EditText) findViewById(R.id.et_uploadUrl);
        TextView tv_uploadUrl_set = (TextView) findViewById(R.id.tv_uploadUrl_set);
        tv_uploadUrl_set.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setView();// 设置界面
    }

    private void setView() {
        changeToastView();                     // 设置吐司界面更改
        changeHMTypeView();                    // 设置设备类型界面更改
        changeSocketPortView();                // socket端口号界面更改
        changeSocketUrlView();                 // socket路径界面更改
        changeHttpUrlView();                   // http路径界面更改
        changeUploadUrlView();                 // 文件上传路径界面更改
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

    // socket端口号界面更改
    private void changeSocketPortView() {
        String socketPort = BSApplication.SharedPreferences.getString(CollocationConstant.socketPort, "");
        if (socketPort != null && !socketPort.equals("")) {
            tv_port_show.setText(socketPort);
        } else {
            tv_port_show.setText(String.valueOf(GlobalConfig.socketPort));
        }
    }

    // socket路径界面更改
    private void changeSocketUrlView() {
        String socketUrl = BSApplication.SharedPreferences.getString(CollocationConstant.socketUrl, "");
        if (socketUrl != null && !socketUrl.equals("")) {
            tv_socketUrl_show.setText(socketUrl);
        } else {
            tv_socketUrl_show.setText(GlobalConfig.socketUrl);
        }
    }

    // http路径界面更改
    private void changeHttpUrlView() {
        String baseUrl = BSApplication.SharedPreferences.getString(CollocationConstant.baseUrl, "");
        if (baseUrl != null && !baseUrl.equals("")) {
            tv_httpUrl_show.setText(baseUrl);
        } else {
            tv_httpUrl_show.setText(GlobalConfig.baseUrl);
        }
    }

    // 文件上传路径界面更改
    private void changeUploadUrlView() {
        String uploadBaseUrl = BSApplication.SharedPreferences.getString(CollocationConstant.uploadBaseUrl, "");
        if (uploadBaseUrl != null && !uploadBaseUrl.equals("")) {
            tv_uploadUrl_show.setText(uploadBaseUrl);
        } else {
            tv_uploadUrl_show.setText(GlobalConfig.uploadBaseUrl);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:           // 返回
                finish();
                break;
            case R.id.tv_toast_false:          // 不吐司
                setToast(false);
                break;
            case R.id.tv_toast_true:           // 吐司
                setToast(true);
                break;
            case R.id.tv_pcd_1:                // 设备类型为APP
                setPcd("1");
                break;
            case R.id.tv_pcd_2:                // 设备类型为硬件
                setPcd("2");
                break;
            case R.id.tv_pcd_3:                // 设备类型为PC
                setPcd("3");
                break;
            case R.id.tv_port_set:             // socket端口号设置
                setSocketPort();
                break;
            case R.id.tv_socketUrl_set:        // socket路径设置
                setSocketUrl();
                break;
            case R.id.tv_httpUrl_set:          // http路径设置
                setHttpUrl();
                break;
            case R.id.tv_uploadUrl_set:        // 上传路径设置
                setUpLoadUrl();
                break;

        }
    }

    private void setToast(boolean toast) {
        if (toast) {
            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
            et.putString(CollocationConstant.isToast, "0");
            et.commit();
            GlobalConfig.isToast=true;
        } else {
            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
            et.putString(CollocationConstant.isToast, "1");
            et.commit();
            GlobalConfig.isToast=false;
        }
        changeToastView();
    }

    private void setPcd(String pcdType) {
        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putString(CollocationConstant.PCDType, pcdType);
        et.commit();
        GlobalConfig.PCDType = Integer.parseInt(pcdType);
        changeHMTypeView();
    }

    private void setSocketPort() {
        String _sPort=et_socketPort.getText().toString().trim();
        if(_sPort!=null&&!_sPort.equals("")){
            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
            et.putString(CollocationConstant.socketPort, _sPort);
            et.commit();
            GlobalConfig.socketPort = Integer.parseInt(_sPort);
            changeSocketPortView();
        }else{
            ToastUtils.show_short(context,"您还没有输入端口号");
        }
    }

    private void setSocketUrl() {
        String _sUrl=et_socketUrl.getText().toString().trim();
        if(_sUrl!=null&&!_sUrl.equals("")){
            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
            et.putString(CollocationConstant.socketUrl, _sUrl);
            et.commit();
            GlobalConfig.socketUrl = _sUrl;
            changeSocketUrlView();
        }else{
            ToastUtils.show_short(context,"您还没有输入socketIP地址");
        }
    }

    private void setHttpUrl() {
        String _hUrl=et_httpUrl.getText().toString().trim();
        if(_hUrl!=null&&!_hUrl.equals("")){
            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
            et.putString(CollocationConstant.baseUrl, _hUrl);
            et.commit();
            GlobalConfig.baseUrl = _hUrl;
            changeHttpUrlView();
        }else{
            ToastUtils.show_short(context,"您还没有输入http路径");
        }
    }

    private void setUpLoadUrl() {
        String _upUrl=et_uploadUrl.getText().toString().trim();
        if(_upUrl!=null&&!_upUrl.equals("")){
            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
            et.putString(CollocationConstant.uploadBaseUrl, _upUrl);
            et.commit();
            GlobalConfig.uploadBaseUrl = _upUrl;
            changeUploadUrlView();
        }else{
            ToastUtils.show_short(context,"您还没有输入文件上传路径");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.activity_null);
    }
}
