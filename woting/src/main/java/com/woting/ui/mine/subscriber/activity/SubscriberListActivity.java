package com.woting.ui.mine.subscriber.activity;

import android.app.Dialog;
import android.content.Intent;
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
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.album.model.SubscriberInfo;
import com.woting.ui.home.program.radiolist.mode.ListInfo;
import com.woting.ui.mine.subscriber.adapter.SubscriberAdapter;

import org.json.JSONException;
import org.json.JSONObject;

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
    private Dialog dialog;
    protected SubscriberAdapter adapter;

    private int page = 1;
    private int refreshType = 1;// refreshType == 1 为下拉加载  == 2 为上拉加载更多

    private String tag = "SUBSCRIBER_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private ArrayList<SubscriberInfo> newList = new ArrayList<>();
    protected List<SubscriberInfo> subList;

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
        initView();
        initEvent();
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            sendRequest();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    // 初始化视图
    private void initView() {
        TextView textHead = (TextView) findViewById(R.id.head_name_tv);// 标题
        textHead.setText("订阅");

        findViewById(R.id.head_left_btn).setOnClickListener(this);// 返回

        mListView = (XListView) findViewById(R.id.listview_fm);
        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);
    }

    // 初始化点击事件
    private void initEvent() {
        setListView();

        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                if (CommonHelper.checkNetwork(context)) {
                    refreshType = 1;
                    page = 1;
                    sendRequest();
                } else {
                    mListView.stopRefresh();
                }
            }

            @Override
            public void onLoadMore() {
                if (CommonHelper.checkNetwork(context)) {
                    refreshType = 2;
                    sendRequest();
                } else {
                    mListView.stopLoadMore();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
        }
    }

    // 获取用户订阅列表
    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PageSize", "10");
            jsonObject.put("Page", String.valueOf(page));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getSubscribeList, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                page++;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        subList = new Gson().fromJson(result.getString("ResultList"), new TypeToken<List<SubscriberInfo>>() {}.getType());
                        if (subList != null && subList.size() < 10) mListView.setPullLoadEnable(false);
                        if (refreshType == 1) newList.clear();
                        newList.addAll(subList);
                        if (adapter == null) {
                            mListView.setAdapter(adapter = new SubscriberAdapter(context, newList));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        tipView.setVisibility(View.GONE);
                    } else {
                        mListView.setPullLoadEnable(false);
                        if (refreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "你还没有订阅哦\n赶紧去订阅一些精彩的节目吧~");
                        }
                    }
                } catch (Exception e) {
                    if (refreshType == 1) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    }
                }
                if (refreshType == 1) {
                    mListView.stopRefresh();
                } else {
                    mListView.stopLoadMore();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                if (refreshType == 1) {
                    mListView.stopRefresh();
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                } else {
                    mListView.stopLoadMore();
                }
            }
        });
    }

    // 这里要改 ?
    protected void setListView() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position - 1 < 0) return ;
                position = position - 1;
                ListInfo listInfo = new ListInfo();
                listInfo.setContentName(newList.get(position).getContentSeqName());
                listInfo.setContentDescn(newList.get(position).getContentMediaName());
                listInfo.setContentId(newList.get(position).getContentSeqId());

                Intent intent = new Intent(context, AlbumActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "radiolistactivity");
                bundle.putSerializable("list", listInfo);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        adapter = null;
        setContentView(R.layout.activity_null);
    }
}
