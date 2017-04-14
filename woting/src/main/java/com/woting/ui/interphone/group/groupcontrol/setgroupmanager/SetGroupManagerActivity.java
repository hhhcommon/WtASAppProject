package com.woting.ui.interphone.group.groupcontrol.setgroupmanager;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseactivity.AppBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class SetGroupManagerActivity extends AppBaseActivity implements OnClickListener, TipView.WhiteViewClick {
    private Dialog dialog;
    private String groupId;
    private String tag = "SET_GROUP_MANAGER_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    private TipView tipView;// 没有网络没有数据提示
    private TipView tipSearchNull;// 搜索数据为空提示
    private TextView tv_head_right;
    private int textFlag=0;
    private TextView tv_sum;

    @Override
    public void onWhiteViewClick() {
        groupId = getIntent().getStringExtra("GroupId");
        if (groupId != null && !groupId.equals("")) {
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialogph(context, "正在获取群成员信息");
                send();
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
            }
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.IS_ERROR);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_set_manager);
        initView();
    }

    // 初始化界面
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);      // 返回
        findViewById(R.id.head_right_btn).setOnClickListener(this);     // 添加按钮
        findViewById(R.id.add_manager).setOnClickListener(this);        // 添加管理员

        tv_head_right=(TextView)findViewById(R.id.tv_head_right);       // 编辑
        tv_sum=(TextView)findViewById(R.id.tv_sum);                     // 当前具有该群管理权限的人数

        tipSearchNull = (TipView) findViewById(R.id.tip_search_null);
        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);


        groupId = getIntent().getStringExtra("GroupId");
        if (groupId != null && !groupId.equals("")) {
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialogph(context, "正在获取群成员信息");
                send();
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
            }
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.IS_ERROR);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.head_right_btn:
               if(textFlag==0){
                   textFlag=1;
                   tv_head_right.setText("删除");
               }else{
                   textFlag=0;
                   tv_head_right.setText("确定");
               }

                break;
        }
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.grouptalkUrl, tag, jsonObject, new VolleyCallback() {
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

                } else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_DATA, "群内没有其他成员了\n赶紧去邀请好友加入群组吧");
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        setContentView(R.layout.activity_null);
    }

}
