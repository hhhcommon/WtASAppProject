package com.woting.ui.musicplay.accuse.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.musicplay.more.PlayerMoreOperationActivity;
import com.woting.ui.musicplay.accuse.adapter.AccuseAdapter;
import com.woting.ui.musicplay.accuse.model.Accuse;
import com.woting.ui.music.search.main.SearchLikeActivity;
import com.woting.ui.mine.main.MineActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 举报
 */
public class AccuseFragment extends Fragment implements OnClickListener {
    private Context context;
    private Dialog dialog;

    private String tag = "FMLIST_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    private View rootView;
    private List<Accuse> allList;
    private ListView mListView;
    private EditText et_InputReason;
    private String ContentId;
    private Boolean IsDataOk;
    private AccuseAdapter adapter;
    private String MediaType;
    private String SelReasons;

    private int fromType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        HandleIntent();
    }

    // 从上一个界面传入的contentId
    private void HandleIntent() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        fromType = bundle.getInt(StringConstant.FROM_TYPE);
        ContentId = bundle.getString("ContentId");
        MediaType = bundle.getString("MediaType");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_accuse, container, false);
            setView();// 设置界面
            getData();// 获取数据
        }
        return rootView;
    }

    // 设置界面
    private void setView() {
        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);                 //  返回
        rootView.findViewById(R.id.head_right_btn).setOnClickListener(this);                //  提交
        mListView = (ListView) rootView.findViewById(R.id.lv_main);                            //  主listView
//        mListView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // 点击空白处隐藏键盘
//                mListView.setFocusable(true);
//                mListView.setFocusableInTouchMode(true);
//                mListView.requestFocus();
//                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(mListView.getWindowToken(), 0);        // 隐藏键盘
//                return true;
//            }
//        });
        View footView = LayoutInflater.from(context).inflate(R.layout.accuse_footer, null);
        et_InputReason = (EditText) footView.findViewById(R.id.et_InputReason);               //  举报原因
        mListView.addFooterView(footView);
    }

    // 获取数据
    private void getData() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            sendRequest();
        } else {
           ToastUtils.show_always(context,"网络连接失败,请稍后再试!");
        }
    }

    //获取举报列表
    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", "12");
            jsonObject.put("ResultType", "2");
            jsonObject.put("RelLevel", "1");
            jsonObject.put("Page", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getCatalogUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null ) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null) {
                        if (ReturnType.equals("1001")) {
                            try {
                                String ResultList = result.getString("CatalogData");
                                allList = new Gson().fromJson(ResultList, new TypeToken<List<Accuse>>() {
                                }.getType());
                                if (allList != null && allList.size() > 0) {
                                    setListViewData();
                                } else {
                                    ToastUtils.show_always(context, "出错了,请您稍后再试!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtils.show_always(context, "出错了,请您稍后再试!");
                            }
                        } else {
                            ToastUtils.show_always(context, "出错了,请您稍后再试!");
                        }
//                        if (ReturnType.equals("1002")) {
//                            ToastUtils.show_short(context, "无此分类信息");
//                        } else if (ReturnType.equals("1003")) {
//                            ToastUtils.show_short(context, "分类不存在");
//                        } else if (ReturnType.equals("1011")) {
//                            ToastUtils.show_short(context, "当前暂无分类");
//                        } else if (ReturnType.equals("T")) {
//                            ToastUtils.show_short(context, "获取列表异常");
//                        }
                    } else {
                        ToastUtils.show_always(context, "出错了,请您稍后再试!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.show_always(context, "出错了,请您稍后再试!");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    //设置listview的数据内容
    private void setListViewData() {
        if (mListView != null && allList != null && allList.size() > 0) {
            adapter = new AccuseAdapter(context, allList);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (allList.get(position).getCheckType() == 1) {
                        allList.get(position).setCheckType(0);
                    } else {
                        for (int i = 0; i < allList.size(); i++) {
                            if (allList.get(i).getCheckType() == 1) {
                                allList.get(i).setCheckType(0);
                            }
                        }
                        allList.get(position).setCheckType(1);
                    }
                    adapter.notifyDataSetChanged();
                }

            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:            // 返回
                if (fromType == IntegerConstant.TAG_HOME) {
                    HomeActivity.close();
                } else if (fromType == IntegerConstant.TAG_MORE) {
                    PlayerMoreOperationActivity.close();
                } else if (fromType == IntegerConstant.TAG_SEARCH) {
                    SearchLikeActivity.close();
                } else if (fromType == IntegerConstant.TAG_MINE) {
                    MineActivity.close();
                }
                break;
            case R.id.head_right_btn:
                if (!TextUtils.isEmpty(ContentId)) {
                    if (!handleData()) {
                        ToastUtils.show_always(context, "请至少选择一项举报理由");
                        return;
                    }
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialog(context);
                        sendAccuse();
                    }
                } else {
                    ToastUtils.show_always(context, "发生错误啦，请返回上一界面重试");
                }
                break;
        }
    }

    private boolean handleData() {
        // 单选策略
        for (int i = 0; i < allList.size(); i++) {
            if (allList.get(i).getCheckType() == 1) {
                IsDataOk = true;
                SelReasons = allList.get(i).getCatalogId() + "::" + allList.get(i).getCatalogName();
            }
        }
        return IsDataOk;
    }

    private void sendAccuse() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("ContentId", ContentId);

            if (!TextUtils.isEmpty(MediaType)) {
                jsonObject.put("MediaType", MediaType);
            }

            if (!TextUtils.isEmpty(SelReasons)) {
                jsonObject.put("SelReasons", SelReasons);
            }

            if (!TextUtils.isEmpty(et_InputReason.getText().toString().trim())) {
                jsonObject.put("InputReason", et_InputReason.getText().toString().trim());//   文字
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.presentAccuseUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null ) dialog.dismiss();
                if (isCancelRequest) return;
                IsDataOk = false;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null) {
                        if (ReturnType.equals("1001")) {
                            try {
                                ToastUtils.show_always(context, "举报成功，我们会尽快处理");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // 举报成功之后再停留一秒自动关闭界面
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (fromType == IntegerConstant.TAG_HOME) {
                                        HomeActivity.close();
                                    } else if (fromType == IntegerConstant.TAG_MORE) {
                                        PlayerMoreOperationActivity.close();
                                    } else if (fromType == IntegerConstant.TAG_SEARCH) {
                                        SearchLikeActivity.close();
                                    } else if (fromType == IntegerConstant.TAG_MINE) {
                                        MineActivity.close();
                                    }
                                }
                            }, 1000);
                        } else if (ReturnType.equals("1002")) {
                            ToastUtils.show_short(context, "无此分类信息");
                        } else if (ReturnType.equals("1003")) {
                            ToastUtils.show_short(context, "分类不存在");
                        } else if (ReturnType.equals("1011")) {
                            ToastUtils.show_short(context, "当前暂无分类");
                        } else if (ReturnType.equals("T")) {
                            ToastUtils.show_short(context, "获取列表异常");
                        }
                    } else {
                        ToastUtils.show_short(context, "数据获取异常，请稍候重试");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                IsDataOk = false;
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }

}
