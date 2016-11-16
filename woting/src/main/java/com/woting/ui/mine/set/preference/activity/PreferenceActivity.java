package com.woting.ui.mine.set.preference.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.home.program.fenlei.model.FenLei;
import com.woting.ui.mine.set.preference.adapter.PianHaoAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 偏好设置界面
 * 作者：xinlong on 2016/9/5 17:36
 * 邮箱：645700751@qq.com
 */
public class PreferenceActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout head_left_btn;
    private String tag = "PREFERENCE_REQUEST_CANCEL_TAG"; // 取消网络请求标签
    private PreferenceActivity context;
    private Dialog dialog;
    private boolean isCancelRequest;
    private List<String> preferenceList=new ArrayList<>();
    private ListView lv_prefer;
    private static PianHaoAdapter adapter;
    private static List<FenLei> tempList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        context=this;
        initView();
        setListener();
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取信息");
            send();
        } else {
            ToastUtils.show_allways(context, "网络失败，请检查网络");
        }

    }

    private void initView() {
        head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
        lv_prefer=(ListView)findViewById(R.id.lv_prefer);
        findViewById(R.id.tv_save).setOnClickListener(this);//保存
    }

    private void setListener() {
        head_left_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.tv_save:
                //判断点选
                preferenceList.clear();
                try{
                for(int i=0;i<tempList.size();i++){
                    for(int j=0;j<tempList.get(i).getChildren().size();j++){
                        if(tempList.get(i).getChildren().get(j).getchecked().equals("true")){
                            String s=tempList.get(i).getChildren().get(j).getAttributes().getmId()+"::"
                                    +tempList.get(i).getChildren().get(j).getAttributes().getId()+"::"+tempList.get(i).getChildren().get(j).getName();
                            preferenceList.add(s);
                        }

                    }
                }
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(preferenceList.size()!=0){
                //发送网络请求
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    //dialog = DialogUtils.Dialogph(context, "通讯中...");
                    //send(); 还没有接口
                    ToastUtils.show_allways(context,"测试点击"+preferenceList.toString());
                } else {
                    ToastUtils.show_allways(context, "网络失败，请检查网络");
                }
                }else{
                    ToastUtils.show_allways(context,"您还没有选择偏好");
                }
                break;
        }
    }

    /**
     * 发送网络请求
     */
    private void send() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
            jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
            jsonObject.put("IMEI", PhoneMessage.imei);
            PhoneMessage.getGps(context);
            jsonObject.put("GPS-longitude", PhoneMessage.longitude);
            jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
            jsonObject.put("PCDType", GlobalConfig.PCDType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.getPreferenceUrl, tag, jsonObject, new VolleyCallback() {

            private String ReturnType;
            private String ResultList;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (isCancelRequest) {
                    return;
                }
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 根据返回值来对程序进行解析
                if (ReturnType != null) {
                    if (ReturnType.equals("1001")) {
                        try {

                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("PrefTree")).nextValue();
                            ResultList = arg1.getString("children");
                            tempList = new Gson().fromJson(ResultList, new TypeToken<List<FenLei>>() {
                            }.getType());
                            if ( tempList != null) {
                                if ( tempList.size() == 0) {
                                    ToastUtils.show_allways(context, "获取分类列表为空");
                                } else {
                                    //对每个返回的分类做设置 默认为全部未选中状态 此时获取的为是所有的列表内容
                                    if (adapter == null) {
                                        adapter = new PianHaoAdapter(context,tempList);
                                        lv_prefer.setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                ToastUtils.show_allways(context, "获取分类列表为空");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (ReturnType.equals("1002")) {
                        ToastUtils.show_allways(context, "无此分类信息");
                    } else if (ReturnType.equals("1003")) {
                        ToastUtils.show_allways(context, "分类不存在");
                    } else if (ReturnType.equals("1011")) {
                        ToastUtils.show_allways(context, "当前暂无分类");
                    } else if (ReturnType.equals("T")) {
                        ToastUtils.show_allways(context, "获取列表异常");
                    } else {
                        ToastUtils.show_allways(context, "获取列表异常");
                    }

                } else {
                    ToastUtils.show_allways(context, "数据获取异常，请稍候重试");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    public static void RefreshView(List<FenLei> list){

        if(adapter!=null){
            tempList=list;
            adapter.notifyDataSetChanged();
        }

    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //保存偏好设置页查看状态
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putString(StringConstant.PREFERENCE, "1");
        et.commit();
        setContentView(R.layout.activity_null);
    }
}
