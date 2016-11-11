package com.woting.ui.mine.person.updatepersonnews;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.TimeUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.pickview.LoopView;
import com.woting.common.widgetui.pickview.OnItemSelectedListener;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.home.program.fenlei.model.Catalog;
import com.woting.ui.home.program.fenlei.model.CatalogName;
import com.woting.ui.mine.person.updatepersonnews.model.personModel;
import com.woting.ui.mine.person.updatepersonnews.util.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 修改个人信息(还未完成，后台接口暂时没有)
 * @author 辛龙
 * 2016年7月19日
 */
public class UpdatePersonActivity extends BaseActivity implements
        OnClickListener, DatePicker.OnDateChangedListener, DatePickerDialog.OnDateSetListener {

    private List<String> yearList;
    private List<String> monthList;
    private List<String> dateList;
    private ArrayList<String> provinceList; // 一级菜单 list
    private Map<String, List<CatalogName>> TempMap;
    private Map<String, List<String>> positionMap = new HashMap<>(); // 主数据 Map

//    private Dialog dialog;
    private Dialog cityDialog;
    private Dialog dateDialog;
    private View lin_gender_man;
    private View lin_gender_woman;
//    protected LinearLayout lin_Area;

    private TextView tv_age;
    private TextView tv_xingzuo;
    private TextView textAccount;
    private TextView tv_region;
    private EditText textName;
    private EditText tv_mail;
    private EditText tv_signature;

    private LoopView pick_Year;
    private LoopView pick_Month;
    private LoopView pick_Day;
    private LoopView pick_Province;
    private LoopView pick_City;

    private String Year;
    private String Month;
    private String Day;
//    private String userCount;
    private String nickName;
    private String birthday;
    private String starSign;
    private String region;
    private String Email;
    private String userSign;
//    private String dateTime;
//    private String phoneNumber;
    private String tag = "UPDATE_PERSON_VOLLEY_REQUEST_CANCEL_TAG";
    private String gender;

//    private boolean genderFlag;// 设置发生更改
    private boolean isCancelRequest;
    private int screenWidth;
    private int p_Year;
    private int p_Month;
    private int p_Day;
    private int wheelTypeYear = -1;
    private int wheelTypeMonth = -1;
    private int wheelTypeDay = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateperson);
        initView();
        setValueByPrefer();
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            send();
        } else {
            ToastUtils.show_allways(context, "网络失败，请检查网络");
        }
    }

    private void setValueByPrefer() {
        // 账号
        String userCount = BSApplication.SharedPreferences.getString(StringConstant.PHONENUMBER, "");
        if (userCount.equals("")) {
            userCount = BSApplication.SharedPreferences.getString(StringConstant.USERNAME, "");
            textAccount.setText(userCount);
        } else {
            textAccount.setText(userCount.replaceAll("(\\d{3})\\d{6}(\\d{2})", "$1 * * * * * * $2"));
        }

        // 昵称
        nickName = BSApplication.SharedPreferences.getString(StringConstant.NICK_NAME, "");
        textName.setText(nickName);

        // 性别
        gender = BSApplication.SharedPreferences.getString(StringConstant.GENDERUSR, "xb001");
        Log.v("gender", "gender -- > > " + gender);
        changViewGender();

        // 生日
        birthday = BSApplication.SharedPreferences.getString(StringConstant.BIRTHDAY, "");
        tv_age.setText(TimeUtils.timeStamp2Date(birthday));

        // 星座
        starSign = BSApplication.SharedPreferences.getString(StringConstant.STAR_SIGN, "");
        tv_xingzuo.setText(starSign);

        // 地区
        region = BSApplication.SharedPreferences.getString(StringConstant.REGION, "");
        tv_region.setText(region);

        // 邮箱
        Email = BSApplication.SharedPreferences.getString(StringConstant.EMAIL, "");
        tv_mail.setText(Email);

        // 个性签名
        userSign = BSApplication.SharedPreferences.getString(StringConstant.USER_SIGN, "");
        tv_signature.setText(userSign);
    }


    // 设置界面
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        findViewById(R.id.lin_age).setOnClickListener(this);
        findViewById(R.id.lin_area).setOnClickListener(this);

        lin_gender_man = findViewById(R.id.lin_gender_man);
        lin_gender_man.setOnClickListener(this);

        lin_gender_woman = findViewById(R.id.lin_gender_woman);
        lin_gender_woman.setOnClickListener(this);

        textAccount = (TextView) findViewById(R.id.tv_zhanghu);
        textName = (EditText) findViewById(R.id.tv_name);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_xingzuo = (TextView) findViewById(R.id.tv_xingzuo);
        tv_region = (TextView) findViewById(R.id.tv_region);
        tv_mail = (EditText) findViewById(R.id.tv_mail);
        tv_signature = (EditText) findViewById(R.id.tv_signature);

        datePickerDialog();
    }

    // 获取地理位置
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", "2");
            jsonObject.put("ResultType", "1");
            jsonObject.put("RelLevel", "0");
            jsonObject.put("Page", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.getCatalogUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (isCancelRequest) return ;
                try {
                    String ReturnType = result.getString("ReturnType");
                    Log.v("ReturnType", "ReturnType -- > > " + ReturnType);

                    if (ReturnType != null && ReturnType.equals("1001")) {
                        Catalog SubList_all = new Gson().fromJson(result.getString("CatalogData"), new TypeToken<Catalog>() {}.getType());
                        List<CatalogName> s = SubList_all.getSubCata();
                        if (s != null && s.size() > 0) {
                            TempMap = new HashMap<>();
                            provinceList = new ArrayList<>();
                            for (int i = 0; i < s.size(); i++) {
                                if (!TextUtils.isEmpty(s.get(i).getCatalogId()) && !TextUtils.isEmpty(s.get(i).getCatalogName())
                                        && s.get(i).getSubCata() != null && s.get(i).getSubCata().size() > 0) {
//                                            CatalogName  mFenLeiName=new CatalogName();
//                                            mFenLeiName.setCatalogId(s.get(i).getCatalogId());
//                                            mFenLeiName.setCatalogName(s.get(i).getCatalogName());
//                                            provinceCatalogNameList.add(mFenLeiName);
                                    provinceList.add(s.get(i).getCatalogName());
                                    List<CatalogName> myList = s.get(i).getSubCata();
                                    TempMap.put(s.get(i).getCatalogName(), myList);
                                }
                            }
                            if (TempMap.size() > 0) {
                                for (int i = 0; i < provinceList.size(); i++) {
                                    List<CatalogName> mList = TempMap.get(provinceList.get(i));
                                    ArrayList<String> cityList = new ArrayList<>();
                                    for (int j = 0; j < mList.size(); j++) {
                                        if (mList.get(j).getCatalogName() != null) {
                                            cityList.add(mList.get(j).getCatalogName());
                                        }
                                    }
                                    positionMap.put(provinceList.get(i), cityList);
                                }
                            }
                            cityPickerDialog();
                        } else {
                            Log.e("", "获取城市列表为空");
                        }
                    } else {
                        ToastUtils.show_allways(context, "数据获取异常，请稍候重试");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                ToastUtils.showVolleyError(context);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                saveData();
                finish();
                break;
            case R.id.lin_age:// 年龄
                dateDialog.show();
                break;
            case R.id.lin_gender_man:
                if (!gender.equals("xb001")) {
                    gender = "xb001";
                    changViewGender();
                }
                break;
            case R.id.lin_gender_woman:
                if (!gender.equals("xb002")) {
                    gender = "xb002";
                    changViewGender();
                }
                break;
            case R.id.lin_area:
                cityDialog.show();
                break;
        }
    }

    // 此方法用来保存当前页面的数据
    private void saveData() {
        nickName = textName.getText().toString().trim();// 昵称
        starSign = tv_xingzuo.getText().toString();// 星座
        region = tv_region.getText().toString().trim();// 地区
        Email = tv_mail.getText().toString().trim();// 邮箱
        userSign = tv_signature.getText().toString().trim();// 签名

        Log.v("gender", "gender -- > > " + gender);

        Intent intent = new Intent();
        personModel pM = new personModel(nickName, birthday, starSign, region, userSign, gender, Email);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", pM);
        intent.putExtras(bundle);
        setResult(1, intent);
    }

    // 日期选择框
    private void datePickerDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_datepicker, null);
        TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        pick_Year = (LoopView) dialog.findViewById(R.id.pick_year);
        pick_Month = (LoopView) dialog.findViewById(R.id.pick_month);
        pick_Day = (LoopView) dialog.findViewById(R.id.pick_day);

        yearList = DateUtil.getYearList();
        monthList = DateUtil.getMonthList();
        dateList = DateUtil.getDayList31();

        pick_Year.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                wheelTypeYear = 1;
                p_Year = index;
                if (wheelTypeMonth == 1) {
                    if (monthList.get(p_Month).equals(" 2月")) {
                        // 判断是不是闰年
                        if (wheelTypeYear == -1) {
                            // 说明没变过，还是 1989 年这年不是闰年
                            dateList = DateUtil.getDayList28();
                            pick_Day.setItems(dateList);
                        } else {
                            String year = yearList.get(p_Year).trim();
                            int yearInt = Integer.valueOf(year.substring(0, 4));
                            if (yearInt % 4 == 0 && yearInt % 100 != 0 || yearInt % 400 == 0) {
                                // 是闰年
                                dateList = DateUtil.getDayList29();
                                pick_Day.setItems(dateList);
                            } else {
                                dateList = DateUtil.getDayList28();
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
                wheelTypeMonth = 1;
                p_Month = index;
                if (monthList.get(p_Month).equals(" 2月")) {
                    // 判断是不是闰年
                    if (wheelTypeYear == -1) {
                        // 说明没变过，还是 1989 年这年不是闰年
                        dateList = DateUtil.getDayList28();
                        pick_Day.setItems(dateList);
                    } else {
                        String year = yearList.get(p_Year).trim();
                        int yearInt = Integer.valueOf(year.substring(0, 4));
                        if (yearInt % 4 == 0 && yearInt % 100 != 0 || yearInt % 400 == 0) {
                            // 是闰年
                            dateList = DateUtil.getDayList29();
                            pick_Day.setItems(dateList);
                        } else {
                            dateList = DateUtil.getDayList28();
                            pick_Day.setItems(dateList);
                        }
                    }
                } else if (monthList.get(p_Month).equals(" 1月") || monthList.get(p_Month).equals(" 3月")
                        || monthList.get(p_Month).equals(" 5月") || monthList.get(p_Month).equals(" 7月")
                        || monthList.get(p_Month).equals(" 8月") || monthList.get(p_Month).equals("10月")
                        || monthList.get(p_Month).equals("12月")) {   // 31 天
                    dateList = DateUtil.getDayList31();
                    pick_Day.setItems(dateList);
                } else {
                    // 30 天
                    dateList = DateUtil.getDayList30();
                    pick_Day.setItems(dateList);
                }
            }
        });

        pick_Day.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                wheelTypeDay = 1;
                p_Day = index;
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
                if (wheelTypeYear == 1) {
                    Year = yearList.get(p_Year);
                } else {
                    Year = "1989年";
                }
                if (wheelTypeMonth == 1) {
                    Month = monthList.get(p_Month);
                } else {
                    Month = "5月";
                }
                if (wheelTypeDay == 1) {
                    Day = dateList.get(p_Day);
                } else {
                    Day = "25日";
                }

                String Constellation = DateUtil.getConstellation(Integer.valueOf(Month.substring(0, Month.length() - 1).trim()),
                        Integer.valueOf(Day.substring(0, Day.length() - 1).trim()));

                tv_xingzuo.setText(Constellation);
                birthday = TimeUtils.date2TimeStamp(Year + Month + Day);
                tv_age.setText(Year + Month + Day);

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
     */
    private void cityPickerDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_city, null);
        pick_Province = (LoopView) dialog.findViewById(R.id.pick_province);
        pick_City = (LoopView) dialog.findViewById(R.id.pick_city);

//        ToastUtils.show_allways(context,"province"+ provinceList.size()+provinceList.get(0));
//        List<String> LIST = positionMap.get(provinceList.get(15));
//        String s=LIST.get(0);
//        ToastUtils.show_allways(context,""+s);
//        pick_Province.setItems(provinceList);
//        pick_City.setItems(positionMap.get(provinceList.get(0)));
//
//        pick_Province.setTextSize(30);
//        pick_City.setTextSize(30);


//        TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
//        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        cityDialog = new Dialog(context, R.style.MyDialog);
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

        dialog.findViewById(R.id.tv_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打印结果
            }
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityDialog.isShowing()) {
                    cityDialog.dismiss();
                }
            }
        });
    }

    // 根据 share 存储值 修改性别
    private void changViewGender() {
        if (gender.equals("xb001")) {
            lin_gender_man.setBackgroundColor(getResources().getColor(R.color.dinglan_orange));
            lin_gender_woman.setBackgroundColor(getResources().getColor(R.color.up_bg_unselected));
        } else {
            lin_gender_man.setBackgroundColor(getResources().getColor(R.color.up_bg_unselected));
            lin_gender_woman.setBackgroundColor(getResources().getColor(R.color.dinglan_orange));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
        String dateTime = sdf.format(calendar.getTime());
        ToastUtils.show_allways(context, "选中的日期为" + dateTime);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    }
}
