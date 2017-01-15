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
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.home.model.Catalog;
import com.woting.ui.home.model.CatalogName;
import com.woting.ui.mine.person.updatepersonnews.model.UpdatePerson;
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
 * 修改个人信息
 * 作者：xinlong on 2016/7/19 21:18
 * 邮箱：645700751@qq.com
 */
public class UpdatePersonActivity extends AppBaseActivity implements
        OnClickListener, DatePicker.OnDateChangedListener, DatePickerDialog.OnDateSetListener {

    private List<String> yearList;
    private List<String> monthList;
    private List<String> dateList;
    private List<CatalogName> myList = new ArrayList<>(); // 存储临时组装的 list 数据
    private List<String> provinceList; // 一级菜单 list
    private Map<String, List<CatalogName>> tempMap;
    private Map<String, List<String>> positionMap = new HashMap<>(); // 主数据 Map

    private Dialog cityDialog;// 选择城市 Dialog
    private Dialog dateDialog;// 选择生日 Dialog
    private View genderMan;// 性别  男
    private View genderWoman;// 性别 女
    private View viewArea;// 地区

    private TextView textAge;// 年龄
    private TextView textStarSign;// 星座
    private TextView textAccount;// 账号
    private TextView textRegion;// 地区
    private EditText textName;// 昵称
    private EditText textEmail;// 邮箱
    private EditText textSignature;// 签名

    private LoopView pickDay;
    private LoopView pickCity;

    private String year;// 年
    private String month;// 月
    private String day;// 日
    private String nickName;// 昵称
    private String birthday;// 生日
    private String starSign;// 星座
    private String region;// 地区
    private String regionId;// 所选择的地区 ID  提交服务器需要
    private String email;// 邮箱
    private String userSign;// 签名
    private String gender;// 性别
    private String tag = "UPDATE_PERSON_VOLLEY_REQUEST_CANCEL_TAG";

    private boolean isCancelRequest;
    private int screenWidth;
    private int pYear;
    private int pMonth;
    private int pDay;
    private int wheelTypeYear = -1;
    private int wheelTypeMonth = -1;
    private int wheelTypeDay = -1;
    private int provinceIndex;        // 选中的省级角标
    private int cityIndex;            // 选中的市级角标

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateperson);
        initView();
        setValueByPrefer();
        if (GlobalConfig.CityCatalogList != null && GlobalConfig.CityCatalogList.size() > 0) {
            handleCityList(GlobalConfig.CityCatalogList);
        } else if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            send();
        }
    }

    private void setValueByPrefer() {
        // 账号
        String userCount = BSApplication.SharedPreferences.getString(StringConstant.PHONENUMBER, "");
        if (userCount.equals("")) {
            userCount = BSApplication.SharedPreferences.getString(StringConstant.USERNAME, "");
        } else {
            userCount = userCount.replaceAll("(\\d{3})\\d{6}(\\d{2})", "$1 * * * * * * $2");
        }
        textAccount.setText(userCount);

        // 昵称
        nickName = BSApplication.SharedPreferences.getString(StringConstant.NICK_NAME, "");
        textName.setText(nickName);

        // 性别
        gender = BSApplication.SharedPreferences.getString(StringConstant.GENDERUSR, "xb001");
        changViewGender();

        // 生日
        birthday = BSApplication.SharedPreferences.getString(StringConstant.BIRTHDAY, "");
        textAge.setText(TimeUtils.timeStamp2Date(birthday));

        // 星座
        starSign = BSApplication.SharedPreferences.getString(StringConstant.STAR_SIGN, "");
        textStarSign.setText(starSign);

        // 地区
        region = BSApplication.SharedPreferences.getString(StringConstant.REGION, "");
        textRegion.setText(region);

        // 邮箱
        email = BSApplication.SharedPreferences.getString(StringConstant.EMAIL, "");
        textEmail.setText(email);

        // 个性签名
        userSign = BSApplication.SharedPreferences.getString(StringConstant.USER_SIGN, "");
        textSignature.setText(userSign);
    }


    // 设置界面
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        findViewById(R.id.lin_age).setOnClickListener(this);
        viewArea = findViewById(R.id.lin_area);

        genderMan = findViewById(R.id.lin_gender_man);
        genderMan.setOnClickListener(this);

        genderWoman = findViewById(R.id.lin_gender_woman);
        genderWoman.setOnClickListener(this);

        textAccount = (TextView) findViewById(R.id.tv_zhanghu);
        textName = (EditText) findViewById(R.id.tv_name);
        textAge = (TextView) findViewById(R.id.tv_age);
        textStarSign = (TextView) findViewById(R.id.tv_xingzuo);
        textRegion = (TextView) findViewById(R.id.tv_region);
        textEmail = (EditText) findViewById(R.id.tv_mail);
        textSignature = (EditText) findViewById(R.id.tv_signature);

        datePickerDialog();
    }

    // 获取地理位置
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", "2");
            jsonObject.put("ResultType", "1");
            jsonObject.put("RelLevel", "3");
            jsonObject.put("Page", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.getCatalogUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    Log.v("ReturnType", "ReturnType -- > > " + ReturnType);
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        Catalog subListAll = new Gson().fromJson(result.getString("CatalogData"), new TypeToken<Catalog>() {}.getType());
                        List<CatalogName> catalogNameList = subListAll.getSubCata();
                        GlobalConfig.CityCatalogList = catalogNameList;
                        handleCityList(catalogNameList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                ToastUtils.showVolleyError(context);
            }
        });
    }

    private void handleCityList(List<CatalogName> catalogNameList) {
        if (catalogNameList != null && catalogNameList.size() > 0) {
            tempMap = new HashMap<>();
            provinceList = new ArrayList<>();
            for (int i = 0; i < catalogNameList.size(); i++) {
                if (!TextUtils.isEmpty(catalogNameList.get(i).getCatalogId()) && !TextUtils.isEmpty(catalogNameList.get(i).getCatalogName())) {
                    if (catalogNameList.get(i).getSubCata() != null && catalogNameList.get(i).getSubCata().size() > 0) {
                        // 所返回的 list 有下一级的且不为 0
                        if(catalogNameList.get(i).getSubCata().get(0)!=null&&
                                catalogNameList.get(i).getSubCata().get(0).getCatalogName()!=null) {
                            if (!catalogNameList.get(i).getSubCata().get(0).getCatalogName().equals("市辖区")) {
                                // 不是直辖市
                                provinceList.add(catalogNameList.get(i).getCatalogName());
                                myList = catalogNameList.get(i).getSubCata();
                                tempMap.put(catalogNameList.get(i).getCatalogName(), myList);
                            } else {
                                // 直辖市
                                List<CatalogName> myList1 = new ArrayList<>();
                                provinceList.add(catalogNameList.get(i).getCatalogName());
                                try {
                                    myList1.addAll(catalogNameList.get(i).getSubCata().get(0).getSubCata());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    myList1.addAll(catalogNameList.get(i).getSubCata().get(1).getSubCata());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                tempMap.put(catalogNameList.get(i).getCatalogName(), myList1);

                            }
                        }
                    } else {
                        // 港澳台
                        List<CatalogName> myList1 = new ArrayList<>();
                        for (int t = 0; t < 4; t++) {
                            CatalogName mCatalog = new CatalogName();
                            if(catalogNameList.get(i).getCatalogId()!=null&&
                                    !catalogNameList.get(i).getCatalogId().trim().equals("")){
                            mCatalog.setCatalogId(catalogNameList.get(i).getCatalogId());}
                            mCatalog.setCatalogName(" ");
                            myList1.add(mCatalog);
                        }
                        if(catalogNameList.get(i).getCatalogId()!=null) {
                            if (catalogNameList.get(i).getCatalogId().equals("710000")) {
                                provinceList.add("台湾");
                                tempMap.put("台湾", myList1);
                            } else if (catalogNameList.get(i).getCatalogId().equals("810000")) {
                                provinceList.add("香港");
                                tempMap.put("香港", myList1);
                            } else if (catalogNameList.get(i).getCatalogId().equals("820000")) {
                                provinceList.add("澳门");
                                tempMap.put("澳门", myList1);
                            }
                        }
                    }
                }
            }
            if (tempMap!=null&&tempMap.size() > 0&&provinceList!=null&&provinceList.size()>0) {
                for (int i = 0; i < provinceList.size(); i++) {
                    List<CatalogName> mList = tempMap.get(provinceList.get(i));
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
            viewArea.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    cityDialog.show();
                }
            });
        } else {
            Log.e("", "获取城市列表为空");
        }
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
        }
    }

    // 此方法用来保存当前页面的数据
    private void saveData() {
        nickName = textName.getText().toString().trim();// 昵称
        starSign = textStarSign.getText().toString();// 星座
        region = textRegion.getText().toString().trim();// 地区
        email = textEmail.getText().toString().trim();// 邮箱
        userSign = textSignature.getText().toString().trim();// 签名

        Intent intent = new Intent();
        UpdatePerson pM = new UpdatePerson(nickName, birthday, starSign, region, userSign, gender, email);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", pM);
        bundle.putString("regionId", regionId);
        intent.putExtras(bundle);
        setResult(1, intent);
    }

    // 日期选择框
    private void datePickerDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_datepicker, null);
        LoopView pickYear = (LoopView) dialog.findViewById(R.id.pick_year);
        LoopView pickMonth = (LoopView) dialog.findViewById(R.id.pick_month);
        pickDay = (LoopView) dialog.findViewById(R.id.pick_day);

        yearList = DateUtil.getYearList();
        monthList = DateUtil.getMonthList();
        dateList = DateUtil.getDayList31();

        pickYear.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                wheelTypeYear = 1;
                pYear = index;
                if (wheelTypeMonth == 1) {
                    if (monthList.get(pMonth).equals(" 2月")) {// 判断是不是闰年
                        if (wheelTypeYear == -1) {// 说明没变过，还是 1989 年这年不是闰年
                            dateList = DateUtil.getDayList28();
                            pickDay.setItems(dateList);
                        } else {
                            String year = yearList.get(pYear).trim();
                            int yearInt = Integer.valueOf(year.substring(0, 4));
                            if (yearInt % 4 == 0 && yearInt % 100 != 0 || yearInt % 400 == 0) {// 是闰年
                                dateList = DateUtil.getDayList29();
                                pickDay.setItems(dateList);
                            } else {
                                dateList = DateUtil.getDayList28();
                                pickDay.setItems(dateList);
                            }
                        }
                    }
                }
            }
        });

        pickMonth.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                wheelTypeMonth = 1;
                pMonth = index;
                if (monthList.get(pMonth).equals(" 2月")) {// 判断是不是闰年
                    if (wheelTypeYear == -1) {// 说明没变过，还是 1989 年这年不是闰年
                        dateList = DateUtil.getDayList28();
                        pickDay.setItems(dateList);
                    } else {
                        String year = yearList.get(pYear).trim();
                        int yearInt = Integer.valueOf(year.substring(0, 4));
                        if (yearInt % 4 == 0 && yearInt % 100 != 0 || yearInt % 400 == 0) {// 是闰年
                            dateList = DateUtil.getDayList29();
                            pickDay.setItems(dateList);
                        } else {
                            dateList = DateUtil.getDayList28();
                            pickDay.setItems(dateList);
                        }
                    }
                } else if (monthList.get(pMonth).equals(" 1月") || monthList.get(pMonth).equals(" 3月")
                        || monthList.get(pMonth).equals(" 5月") || monthList.get(pMonth).equals(" 7月")
                        || monthList.get(pMonth).equals(" 8月") || monthList.get(pMonth).equals("10月")
                        || monthList.get(pMonth).equals("12月")) {   // 31 天
                    dateList = DateUtil.getDayList31();
                    pickDay.setItems(dateList);
                } else {// 30 天
                    dateList = DateUtil.getDayList30();
                    pickDay.setItems(dateList);
                }
            }
        });

        pickDay.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                wheelTypeDay = 1;
                pDay = index;
            }
        });

        pickYear.setItems(yearList);
        pickMonth.setItems(monthList);
        pickDay.setItems(dateList);

        pickYear.setInitPosition(59);
        pickMonth.setInitPosition(4);
        pickDay.setInitPosition(24);

        pickYear.setTextSize(20);
        pickMonth.setTextSize(20);
        pickDay.setTextSize(20);

        dateDialog = new Dialog(context, R.style.MyDialog);
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

        dialog.findViewById(R.id.tv_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wheelTypeYear == 1) {
                    year = yearList.get(pYear);
                } else {
                    year = "1989年";
                }
                if (wheelTypeMonth == 1) {
                    month = monthList.get(pMonth);
                } else {
                    month = "5月";
                }
                if (wheelTypeDay == 1) {
                    day = dateList.get(pDay);
                } else {
                    day = "25日";
                }

                String Constellation = DateUtil.getConstellation(Integer.valueOf(month.substring(0, month.length() - 1).trim()),
                        Integer.valueOf(day.substring(0, day.length() - 1).trim()));

                textStarSign.setText(Constellation);
                birthday = TimeUtils.date2TimeStamp(year + month + day);
                textAge.setText(year + month + day);

                dateDialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateDialog.isShowing()) {
                    dateDialog.dismiss();
                }
            }
        });
    }


    // 城市选择框
    private void cityPickerDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_city, null);
        LoopView pickProvince = (LoopView) dialog.findViewById(R.id.pick_province);
        pickCity = (LoopView) dialog.findViewById(R.id.pick_city);

        pickProvince.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                provinceIndex = index;
                List<String> tempList1 = positionMap.get(provinceList.get(index));
                pickCity.setItems(tempList1);
                pickCity.setInitPosition(0);
            }
        });
        pickCity.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                cityIndex = index;
            }
        });
        pickProvince.setItems(provinceList);
        List<String> tempList = positionMap.get(provinceList.get(4));

        pickCity.setItems(tempList);

        pickProvince.setInitPosition(4);
        pickProvince.setTextSize(15);
        pickCity.setTextSize(15);
        cityDialog = new Dialog(context, R.style.MyDialog);
        cityDialog.setContentView(dialog);
        Window window = cityDialog.getWindow();
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
                region = tempMap.get(provinceList.get(provinceIndex)).get(cityIndex).getCatalogId();
                regionId = tempMap.get(provinceList.get(provinceIndex)).get(cityIndex).getCatalogId();
                textRegion.setText(provinceList.get(provinceIndex) + " " + tempMap.get(provinceList.get(provinceIndex)).get(cityIndex).getCatalogName());
                cityDialog.dismiss();
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
            genderMan.setBackgroundColor(getResources().getColor(R.color.dinglan_orange));
            genderWoman.setBackgroundColor(getResources().getColor(R.color.up_bg_unselected));
        } else {
            genderMan.setBackgroundColor(getResources().getColor(R.color.up_bg_unselected));
            genderWoman.setBackgroundColor(getResources().getColor(R.color.dinglan_orange));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        tempMap.clear();
        tempMap=null;
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
        ToastUtils.show_always(context, "选中的日期为" + dateTime);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    }
}
