package com.woting.ui.mine.subscriber.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.mine.subscriber.adapter.SubscriberAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅
 * author：辛龙 (xinLong)
 * 2017/1/10 12:24
 * 邮箱：645700751@qq.com
 */
public class SubscriberListActivity extends AppBaseActivity implements OnClickListener, TipView.WhiteViewClick {
    private XListView mListView;
    private TextView mTextView_Head;
    private Dialog dialog;
    protected SubscriberAdapter adapter;

    private int ViewType = 1;
    private int page = 1;
    private int RefreshType = 1;// refreshType 1为下拉加载 2为上拉加载更多

    private String tag = "SUBSCRIBER_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private ArrayList<RankInfo> newList = new ArrayList<>();
    protected List<RankInfo> SubList;

    private TipView tipView;// 没有网络、没有数据、加载错误提示

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            sendRequest();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fmlist);
        setView();
        setListener();
//        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
//            dialog = DialogUtils.Dialogph(context, "正在获取数据");
//            sendRequest();
//        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
//        }
    }


    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        mListView = (XListView) findViewById(R.id.listview_fm);
        mTextView_Head = (TextView) findViewById(R.id.head_name_tv);
        mTextView_Head.setText("订阅");
        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);
    }

    private void setListener() {
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                if (CommonHelper.checkNetwork(context)) {
                    RefreshType = 1;
                    page = 1;
                    sendRequest();
                } else {
                    mListView.stopRefresh();
                }
            }

            @Override
            public void onLoadMore() {
                if (CommonHelper.checkNetwork(context)) {
                    RefreshType = 2;
                    sendRequest();
                } else {
                    mListView.stopLoadMore();
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
        }
    }

    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getSubscribeList, tag, jsonObject, new VolleyCallback() {
            private String StringSubList;
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                page++;
                try {
                    ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            String rList = result.getString("ResultList");
                            try {
                                JSONObject arg1 = (JSONObject) new JSONTokener(rList).nextValue();
                                try {
                                    StringSubList = arg1.getString("List");
                                    try {
                                        SubList = new Gson().fromJson(StringSubList, new TypeToken<List<RankInfo>>() {
                                        }.getType());
                                        if (RefreshType == 1) {
                                            mListView.stopRefresh();
                                            newList.clear();
                                            newList.addAll(SubList);
                                            adapter = new SubscriberAdapter(context, newList);
                                            mListView.setAdapter(adapter);
                                        } else if (RefreshType == 2) {
                                            mListView.stopLoadMore();
                                            newList.addAll(SubList);
                                            adapter.notifyDataSetChanged();
                                        }
                                        tipView.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        if (RefreshType == 1) {
                                            tipView.setVisibility(View.VISIBLE);
                                            tipView.setTipView(TipView.TipStatus.IS_ERROR);
                                        }
                                    }
                                    setListView();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (RefreshType == 1) {
                                    tipView.setVisibility(View.VISIBLE);
                                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        mListView.stopLoadMore();
                        mListView.setPullLoadEnable(false);
                        if (RefreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n换个电台试试吧");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                if (RefreshType == 1) {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                }
            }
        });
    }

    // 这里要改
    protected void setListView() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        adapter = null;
        setContentView(R.layout.activity_null);
    }
}
