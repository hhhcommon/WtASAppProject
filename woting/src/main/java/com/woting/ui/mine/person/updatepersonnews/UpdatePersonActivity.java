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
import android.widget.TextView;

import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.ToastUtils;
import com.woting.common.widgetui.pickview.LoopView;
import com.woting.common.widgetui.pickview.OnItemSelectedListener;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.mine.person.updatepersonnews.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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
    private LoopView pick_Year;
    private LoopView pick_Month;
    private LoopView pick_Day;
    private int p_Year;
    private int p_Month;
    private int p_Day;
    private List<String> yearList;
    private List<String> monthList;
    private List<String> dateList;
    private int wheelTypeYear=-1;
    private int wheelTypeMonth=-1;
    private int wheelTypeDay=-1;
    private String Year;
    private String Month;
    private String Day;
    private TextView tv_age;
    private TextView tv_xingzuo;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.lin_age:// 年龄
                dateDialog.show();
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

    // 设置界面
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        findViewById(R.id.lin_age).setOnClickListener(this);

        lin_gender_man=(LinearLayout) findViewById(R.id.lin_gender_man);
        lin_gender_man.setOnClickListener(this);
        lin_gender_woman= (LinearLayout)findViewById(R.id.lin_gender_woman);
        lin_gender_woman.setOnClickListener(this);

        tv_age=(TextView)findViewById(R.id.tv_age);
        tv_xingzuo=(TextView)findViewById(R.id.tv_xingzuo);

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
        pick_Year=(LoopView)dialog.findViewById(R.id.pick_year);
        pick_Month=(LoopView)dialog.findViewById(R.id.pick_month);
        pick_Day=(LoopView)dialog.findViewById(R.id.pick_day);

        yearList=DateUtil.getYearList();
        monthList=DateUtil.getMonthList();
        dateList=DateUtil.getDayList31();

        pick_Year.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                wheelTypeYear=1;
                p_Year=index;
                if(wheelTypeMonth==1){
                    if(monthList.get(p_Month).equals(" 2月")) {
                        //判断是不是闰年
                        if(wheelTypeYear==-1){
                            //说明没变过，还是1989年这年不是闰年
                            dateList=DateUtil.getDayList28();
                            pick_Day.setItems(dateList);
                        }else{
                            String year=yearList.get(p_Year).trim();
                            int yearInt=Integer.valueOf(year.substring(0,4));
                            if(yearInt%4==0&&yearInt%100!=0||yearInt%400==0){
                                //是闰年
                                dateList=DateUtil.getDayList29();
                                pick_Day.setItems(dateList);
                            }else {
                                dateList=DateUtil.getDayList28();
                                pick_Day.setItems(dateList);
                            }
                        }
                    }
                }
            }
        });
        pick_Month.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                wheelTypeMonth=1;
                p_Month=index;
                if(monthList.get(p_Month).equals(" 2月")) {
                    //判断是不是闰年
                    if(wheelTypeYear==-1){
                        //说明没变过，还是1989年这年不是闰年
                        dateList=DateUtil.getDayList28();
                        pick_Day.setItems(dateList);
                    }else{
                        String year=yearList.get(p_Year).trim();
                        int yearInt=Integer.valueOf(year.substring(0,4));
                        if(yearInt%4==0&&yearInt%100!=0||yearInt%400==0){
                            //是闰年
                            dateList=DateUtil.getDayList29();
                            pick_Day.setItems(dateList);
                        }else {
                            dateList=DateUtil.getDayList28();
                            pick_Day.setItems(dateList);
                        }
                    }
                }else if(monthList.get(p_Month).equals(" 1月")||monthList.get(p_Month).equals(" 3月")
                        ||monthList.get(p_Month).equals(" 5月")||monthList.get(p_Month).equals(" 7月")
                        ||monthList.get(p_Month).equals(" 8月")||monthList.get(p_Month).equals("10月")
                        ||monthList.get(p_Month).equals("12月"))
                {   //31天
                    dateList=DateUtil.getDayList31();
                    pick_Day.setItems(dateList);
                }else{
                    //30天
                    dateList=DateUtil.getDayList30();
                    pick_Day.setItems(dateList);
                }
            }
        });
        pick_Day.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                wheelTypeDay=1;
                p_Day=index;
            }
        });

        pick_Year.setItems(yearList);
        pick_Month.setItems(monthList);
        pick_Day.setItems(dateList);

        pick_Year.setInitPosition(59);
        pick_Month.setInitPosition(4);
        pick_Day.setInitPosition(24);

        pick_Year.setTextSize(30);
        pick_Month.setTextSize(30);
        pick_Day.setTextSize(30);

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
                if(wheelTypeYear==1){
                    Year=yearList.get(p_Year);
                }else{
                    Year="1989年";
                }
                if(wheelTypeMonth==1){
                    Month=monthList.get(p_Month);
                }else{
                    Month="5月";
                }
                if(wheelTypeDay==1){
                    Day=dateList.get(p_Day);
                }else{
                    Day="25日";
                }

                String Constellation=DateUtil.getConstellation(Integer.valueOf(Month.substring(0,Month.length()-1).trim()),
                        Integer.valueOf(Day.substring(0,Day.length()-1).trim()));

                tv_xingzuo.setText(Constellation);
                tv_age.setText(Year+Month+Day);

                dateDialog.dismiss();
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
/*        ListView lv_city = (ListView) dialog.findViewById(R.id.lv_city);
        ListView lv_zone = (ListView) dialog.findViewById(R.id.lv_zone);*/
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
