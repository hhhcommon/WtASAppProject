package com.woting.ui.mine.feedback.feedbacklist.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.base.baseactivity.AppBaseActivity;
import com.woting.ui.mine.feedback.feedbacklist.adapter.FeedBackExpandAdapter;
import com.woting.ui.mine.feedback.feedbacklist.model.OpinionMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 意见反馈列表
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class FeedbackListActivity extends AppBaseActivity implements OnClickListener, OnGroupClickListener, TipView.WhiteViewClick {
    protected Dialog dialog;
    private ExpandableListView mListView;
    private String tag = "FEEDBACKLIST_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    private TipView tipView;// 无网络、无数据提示
    private EditText mEditContent;
    private List<OpinionMessage> OM = new ArrayList<>();
    private FeedBackExpandAdapter adapter;


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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.send_sms:
                checkData();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacklistex);
        setView();
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        findViewById(R.id.send_sms).setOnClickListener(this);
        mEditContent = (EditText) findViewById(R.id.input_sms);

        mListView = (ExpandableListView) findViewById(R.id.exlv_opinionlist);
        mListView.setGroupIndicator(null);
        mListView.setOnGroupClickListener(this);

        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        adapter = new FeedBackExpandAdapter(context, OM);
        mListView.setAdapter(adapter);
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("Page", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.FeedBackListUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        OM = new Gson().fromJson(result.getString("OpinionList"), new TypeToken<List<OpinionMessage>>() {}.getType());
                        if (OM == null || OM.size() == 0) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有对我们进行反馈哟\n留下您的宝贵意见和建议，我们将努力改进");
                            return;
                        }
                        tipView.setVisibility(View.GONE);
                        adapter.changeData(OM);
                        for (int i = 0; i < adapter.getGroupCount(); i++) {
                            mListView.expandGroup(i);
                        }
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有对我们进行反馈哟\n留下您的宝贵意见和建议，我们将努力改进");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return true;
    }

    private void checkData() {
        String sEditContent = mEditContent.getText().toString().trim();
        if ("".equalsIgnoreCase(sEditContent)) {
            Toast.makeText(context, "请您输入您的意见", Toast.LENGTH_LONG).show();
        } else {
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialog(context);
                sendNews(sEditContent);
            } else {
                ToastUtils.show_short(context, "网络失败，请检查网络");
            }
        }
    }

    private void sendNews(String sEditContent) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("Opinion", sEditContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.FeedBackUrl, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        ToastUtils.show_always(getApplicationContext(), "提交成功");
                        mEditContent.setText("");
                        send();
                    } else {
                        ToastUtils.show_always(getApplicationContext(), "提交失败，请您稍后再试！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.show_always(getApplicationContext(), "提交失败，请您稍后再试！");
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
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        dialog = null;
        mListView = null;
        setContentView(R.layout.activity_null);
    }
}
