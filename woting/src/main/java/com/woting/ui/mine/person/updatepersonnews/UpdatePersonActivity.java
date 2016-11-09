package com.woting.ui.mine.person.updatepersonnews;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.ToastUtils;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.mine.person.updatepersonnews.datepicker.PickerUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 修改个人信息(还未完成，后台接口暂时没有)
 * @author 辛龙
 * 2016年7月19日
 */
public class UpdatePersonActivity extends BaseActivity implements OnClickListener,
        DatePicker.OnDateChangedListener,DatePickerDialog.OnDateSetListener {

    private LinearLayout lin_gender_man;
    private LinearLayout lin_gender_woman;
    private String gender;
    protected Boolean genderFlag=false;//设置发生更改
    private Dialog dateDialog;
    private int screenWidth;
    private String dateTime;
    private Dialog cityDialog;
    private String phoneNumber;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.lin_age:// 年龄
                dateDialog.show();
          /*      ToastUtils.show_allways(context, "设置年龄");*/
                break;
            case R.id.lin_xingzuo:// 星座
          /*      ToastUtils.show_allways(context, "设置星座");*/
                cityDialog.show();
                break;
            case R.id.lin_gender_man:
                if(!gender.equals("M")){
                    gender="M";
                    genderFlag=true;
                    changViewGender();
                }
                break;
            case R.id.lin_gender_woman:
                if(!gender.equals("F")){
                    gender="F";
                    genderFlag=true;
                    changViewGender();
                }

                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateperson);
        initView();
    }

    //电话号码获取 根据电话号码的储存情况，判断是否可以修改手机号
    /*@Override
    protected void onResume() {
        super.onResume();
        phoneNumber = BSApplication.SharedPreferences.getString(StringConstant.USERPHONENUMBER, ""); // 用户手机号
        textPhoneNumber.setText(phoneNumber);
    }*/

    // 初始化性别选择对话框
    private void genderDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_datepicker, null);

    }

    // 设置界面
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        findViewById(R.id.lin_age).setOnClickListener(this);
        findViewById(R.id.lin_xingzuo).setOnClickListener(this);

        lin_gender_man=(LinearLayout) findViewById(R.id.lin_gender_man);
        lin_gender_man.setOnClickListener(this);
        lin_gender_woman= (LinearLayout)findViewById(R.id.lin_gender_woman);
        lin_gender_woman.setOnClickListener(this);

        String userId = BSApplication.SharedPreferences.getString(StringConstant.USERID, "");// 账号 用户 ID
        TextView textAccount  = (TextView) findViewById(R.id.tv_zhanghu);
        textAccount.setText(userId);

        String userName = BSApplication.SharedPreferences.getString(StringConstant.USERNAME, "");// 用户昵称
        TextView textName = (TextView) findViewById(R.id.tv_name);

        gender=BSApplication.SharedPreferences.getString(StringConstant.GENDER,"M");
        changViewGender();
        textName.setText(userName);

        datePickerDialog();
        cityPickerDialog();

    }

    /**
     * 日期选择框
     * */
    private void datePickerDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_datepicker, null);
        TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        DatePicker mPicker=(DatePicker)dialog.findViewById(R.id.datePicker);

        PickerUtils pk=new PickerUtils();
        Calendar calendar = pk.getCalender(context);
        int year= calendar.YEAR;
        int month=calendar.MONTH;
        int day=Calendar.DAY_OF_MONTH;
        mPicker.init(calendar.YEAR,calendar.MONTH,Calendar.DAY_OF_MONTH,this);
        dateDialog = new Dialog(context, R.style.MyDialog);
        // 从底部上升到一个位置
        dateDialog.setContentView(dialog);
        Window window = dateDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        ViewGroup.LayoutParams params = dialog.getLayoutParams();
        params.width = screenWidth;
        dialog.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        dateDialog.setCanceledOnTouchOutside(true);
        dateDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        tv_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              //获取时间

            }
        });
        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateDialog.isShowing()) {
                    dateDialog.dismiss();
                }
            }
        });
    }



    /**
     * 城市选择框
     * */
    private void cityPickerDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_city, null);
        ListView lv_city = (ListView) dialog.findViewById(R.id.lv_city);
        ListView lv_zone = (ListView) dialog.findViewById(R.id.lv_zone);
        TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        cityDialog = new Dialog(context, R.style.MyDialog);
        // 从底部上升到一个位置
        cityDialog.setContentView(dialog);
        Window window = dateDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        ViewGroup.LayoutParams params = dialog.getLayoutParams();
        params.width = screenWidth;
        dialog.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        cityDialog.setCanceledOnTouchOutside(true);
        cityDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
       /* dialog1 = DialogUtils.Dialogphnoshow(context, "通讯中", dialog1);
        Config.dialog = dialog1;
        final List<ShareModel> mList = ShareUtils.getShareModelList();
        ImageAdapter shareAdapter = new ImageAdapter(context, mList);
        mGallery.setAdapter(shareAdapter);*/
        tv_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //打印结果

            }
        });
        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityDialog.isShowing()) {
                    cityDialog.dismiss();
                }
            }
        });
    }

    /**
    * 根据share存储值 修改性别
    * */
    private void changViewGender() {
        if(gender.equals("M")){
            lin_gender_man.setBackgroundColor(getResources().getColor(R.color.dinglan_orange));
            lin_gender_woman.setBackgroundColor(getResources().getColor(R.color.up_bg_unselected));
        }else{
            lin_gender_man.setBackgroundColor(getResources().getColor(R.color.up_bg_unselected));
            lin_gender_woman.setBackgroundColor(getResources().getColor(R.color.dinglan_orange));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (genderFlag = true) {
        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putString(StringConstant.GENDER, gender);
        if (!et.commit()) {
            Log.v("commit", "数据 commit 失败!");
        }
        }

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear,dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        dateTime = sdf.format(calendar.getTime());
        ToastUtils.show_allways(context,"选中的日期为"+dateTime);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


    }
}
