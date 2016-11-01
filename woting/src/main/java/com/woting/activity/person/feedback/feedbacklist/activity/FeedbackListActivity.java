package com.woting.activity.person.feedback.feedbacklist.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.activity.person.feedback.feedbacklist.adapter.FeedBackExpandAdapter;
import com.woting.activity.person.feedback.feedbacklist.model.OpinionMessage;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 意见反馈列表
 * @author 辛龙
 *         2016年8月1日
 */
public class FeedbackListActivity extends BaseActivity implements OnClickListener, OnGroupClickListener {
    protected Dialog dialog;
    private ExpandableListView mListView;
    private String tag = "FEEDBACKLIST_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacklistex);
        setView();
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            send();
        } else {
            ToastUtils.show_allways(context, "网络连接失败，请稍后重试");
        }
    }

    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        mListView = (ExpandableListView) findViewById(R.id.exlv_opinionlist);
        mListView.setGroupIndicator(null);
        mListView.setOnGroupClickListener(this);
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("Page", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.FeedBackListUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                    Message = result.getString("Message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    try {
                        List<OpinionMessage> OM = new Gson().fromJson(result.getString("OpinionList"), new TypeToken<List<OpinionMessage>>() {}.getType());
                        if (OM == null || OM.size() == 0) {
                            ToastUtils.show_allways(context, "数据获取异常请重试");
                            return;
                        }
                        FeedBackExpandAdapter adapter = new FeedBackExpandAdapter(context, OM);
                        mListView.setAdapter(adapter);
                        for (int i = 0; i < adapter.getGroupCount(); i++) {
                            mListView.expandGroup(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_short(getApplicationContext(), Message + "提交意见反馈失败");
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        dialog = null;
        mListView = null;
        setContentView(R.layout.activity_null);
    }
}
