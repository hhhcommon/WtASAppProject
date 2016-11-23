package com.woting.ui.mine.feedback.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.mine.feedback.feedbacklist.activity.FeedbackListActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 提交意见反馈
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class FeedbackActivity extends AppBaseActivity implements OnClickListener {
    private Dialog dialog;
    private EditText mEditContent;// 输入 提交内容

    private String sEditContent;// 提交内容
    private String tag = "FEEDBACK_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setView();
    }

    // 初始化视图
    private void setView() {
        mEditContent = (EditText) findViewById(R.id.edit_feedback_content);
        findViewById(R.id.head_left_btn).setOnClickListener(this);// 返回
        findViewById(R.id.head_right_btn).setOnClickListener(this);// 历史记录
        findViewById(R.id.submit_button).setOnClickListener(this);// 提交
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_button:// 提交
                checkData();
                break;
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.head_right_btn:// 历史记录
                startActivity(new Intent(this, FeedbackListActivity.class));
                break;
        }
    }

    private void checkData() {
        sEditContent = mEditContent.getText().toString().trim();
        if ("".equalsIgnoreCase(sEditContent)) {
            Toast.makeText(context, "请您输入您的意见", Toast.LENGTH_LONG).show();
        } else {
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialogph(context, "反馈中");
                send();
            } else {
                ToastUtils.show_short(context, "网络失败，请检查网络");
            }
        }
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("Opinion", sEditContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.FeedBackUrl, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        ToastUtils.show_always(getApplicationContext(), "提交成功");
                        Intent intent = new Intent(FeedbackActivity.this, FeedbackListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.show_always(FeedbackActivity.this, "提交失败,请稍后再试!");
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
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        mEditContent = null;
        dialog = null;
        sEditContent = null;
        tag = null;
        mEditContent = null;
        setContentView(R.layout.activity_null);
    }
}