package com.woting.ui.mine.feedback.feedbacklist.activity;

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
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.mine.feedback.feedbacklist.adapter.FeedBackExpandAdapter;
import com.woting.ui.mine.feedback.feedbacklist.model.OpinionMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 意见反馈列表
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class FeedbackListActivity extends AppBaseActivity implements OnClickListener, OnGroupClickListener {
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
            ToastUtils.show_always(context, "网络连接失败，请稍后重试");
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

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            String list = result.getString("OpinionList");
                            List<OpinionMessage> OM = new Gson().fromJson(list, new TypeToken<List<OpinionMessage>>() {
                            }.getType());
                            if (OM == null || OM.size() == 0) {
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
