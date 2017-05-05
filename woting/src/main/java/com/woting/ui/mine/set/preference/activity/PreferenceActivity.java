package com.woting.ui.mine.set.preference.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.music.classify.model.FenLei;
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
public class PreferenceActivity extends AppBaseActivity implements View.OnClickListener, TipView.WhiteViewClick {
    private static PianHaoAdapter adapter;
    private static List<FenLei> tempList = new ArrayList<>();
    private List<String> preferenceList = new ArrayList<>();

    private Dialog dialog;
    private ListView listPrefer;
    private TipView tipView;// 没有网络、请求发生错误提示

    private String tag = "PREFERENCE_SET_REQUEST_CANCEL_TAG"; // 取消网络请求标签
    private boolean isCancelRequest;

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        initView();
    }

    // 初始化视图
    private void initView() {
        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        findViewById(R.id.tv_save).setOnClickListener(this);// 保存
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        listPrefer = (ListView) findViewById(R.id.lv_prefer);

        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.tv_save:// 保存
                preferenceList.clear();
                try {
                    for (int i = 0; i < tempList.size(); i++) {
                        for (int j = 0; j < tempList.get(i).getChildren().size(); j++) {
                            if (tempList.get(i).getChildren().get(j).getchecked().equals("true")) {
                                String _s= (tempList.get(i).getChildren().get(j).getAttributes().getmId()!=null&&
                                        !tempList.get(i).getChildren().get(j).getAttributes().getmId().trim().equals(""))?
                                        tempList.get(i).getChildren().get(j).getAttributes().getmId():"-1";
                                String s = _s + "::"
                                        + tempList.get(i).getChildren().get(j).getAttributes().getId();
                                preferenceList.add(s);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (preferenceList.size() != 0) {
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialog(context);
                        sendRequest();
                    } else {
                        ToastUtils.show_always(context, "网络失败，请检查网络");
                    }
                }
                break;
        }
    }

    // 保存新的偏好设置
    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            String s = preferenceList.toString();
            jsonObject.put("PrefStr", s.substring(1, s.length() - 1));
            jsonObject.put("IsOnlyCata", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.setPreferenceUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    ToastUtils.show_always(context, "偏好设置保存成功！");
                } else {
                    ToastUtils.show_always(context, "保存失败，请稍候再试");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.show_always(context, "保存失败，请稍候再试");
            }
        });
    }

    // 发送网络请求
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
            jsonObject.put("IsAll", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.getPreferenceUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("PrefTree")).nextValue();
                        tempList = new Gson().fromJson(arg1.getString("children"), new TypeToken<List<FenLei>>() {}.getType());
                        if (tempList != null && tempList.size() > 0) {
                            tipView.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(CommonUtils.getUserIdNoImei(context))) {
                                sendTwice();
                            } else {
                                // 对每个返回的分类做设置 默认为全部未选中状态 此时获取的为是所有的列表内容
                                if (adapter == null) {
                                    listPrefer.setAdapter(adapter = new PianHaoAdapter(context, tempList));
                                } else {
                                    adapter.notifyDataSetChanged();
                                }
                                setInterface();
                            }
                        } else {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.IS_ERROR);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    }
                } else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    private void sendTwice() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("IsAll", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getPreferenceUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("PrefTree")).nextValue();
                        List<FenLei> mList = new Gson().fromJson(arg1.getString("children"), new TypeToken<List<FenLei>>() {}.getType());
                        try {
                            for (int i = 0; i < mList.size(); i++) {
                                for (int j = 0; j < tempList.size(); j++) {
                                    for (int k = 0; k < tempList.get(j).getChildren().size(); k++) {
                                        if (mList.get(i).getId().equals(tempList.get(j).getChildren().get(k).getId())) {
                                            tempList.get(j).getChildren().get(k).setchecked("true");
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (adapter == null) {
                            listPrefer.setAdapter(adapter = new PianHaoAdapter(context, tempList));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        setInterface();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (adapter == null) {
                        listPrefer.setAdapter(adapter = new PianHaoAdapter(context, tempList));
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    setInterface();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
            }
        });
    }

    private void setInterface() {
        adapter.setOnListener(new PianHaoAdapter.preferCheck() {
            @Override
            public void clickPosition(int position) {
                if (tempList.get(position).getChildren().get(0).getchecked().equals("false")) {
                    for (int i = 0; i < tempList.get(position).getChildren().size(); i++) {
                        tempList.get(position).getChildren().get(i).setchecked("true");
                    }
                    tempList.get(position).setTag(position);
                    tempList.get(position).setTagType(1);
                } else {
                    for (int i = 0; i < tempList.get(position).getChildren().size(); i++) {
                        tempList.get(position).getChildren().get(i).setchecked("false");
                    }
                    tempList.get(position).setTag(position);
                    tempList.get(position).setTagType(0);
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    public static void RefreshView(List<FenLei> list) {
        if (adapter != null) {
            tempList = list;
            adapter.notifyDataSetChanged();
        }
    }

    public static void allCheck(int position) {
        tempList.get(position).setTag(position);
        tempList.get(position).setTagType(1);
        RefreshView(tempList);
    }

    public static void allUnCheck(int position) {
        tempList.get(position).setTag(position);
        tempList.get(position).setTagType(0);
        RefreshView(tempList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 保存偏好设置页查看状态
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putString(StringConstant.PREFERENCE, "1");
        et.commit();
        setContentView(R.layout.activity_null);
        adapter = null;
        tempList = null;
    }
}
