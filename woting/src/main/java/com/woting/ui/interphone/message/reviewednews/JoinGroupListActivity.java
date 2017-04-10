package com.woting.ui.interphone.message.reviewednews;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

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
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.interphone.message.reviewednews.adapter.JoinGroupAdapter;
import com.woting.ui.interphone.message.reviewednews.model.CheckInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 审核消息
 * @author 辛龙
 * 2016年4月13日
 */
public class JoinGroupListActivity extends AppBaseActivity implements OnClickListener, TipView.WhiteViewClick {
    protected JoinGroupAdapter adapter;
    private List<CheckInfo> userList;
    private List<GroupInfo> list;

    private Dialog delDialog;
    private Dialog dialog;
    private ListView joinListView;
    
    private int dealType = 1;// == 1 接受  == 2 拒绝
    private int delPosition;
    private int onClickTv;
    
    private String groupId;
    private String tag = "JOIN_GROUP_LIST_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    private TipView tipView;// 没有网路、没有数据提示

    @Override
    public void onWhiteViewClick() {
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
        setContentView(R.layout.activity_joingrouplist);

        handleIntent();
        setView();
        delDialog();

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

    private void setView() {
        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        findViewById(R.id.head_left_btn).setOnClickListener(this);
        joinListView = (ListView) findViewById(R.id.lv_jiaqun);
    }

    private void delDialog() {
        final View dialog1 = LayoutInflater.from(this).inflate(R.layout.dialog_exit_confirm, null);
        TextView tv_cancel = (TextView) dialog1.findViewById(R.id.tv_cancle);
        TextView tv_title = (TextView) dialog1.findViewById(R.id.tv_title);
        TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
        tv_title.setText("确定拒绝?");
        delDialog = new Dialog(this, R.style.MyDialog);
        delDialog.setContentView(dialog1);
        delDialog.setCanceledOnTouchOutside(false);
        delDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delDialog.dismiss();
            }
        });

        tv_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    delDialog.dismiss();
                    dealType = 2;
                    sendRequest();
                } else {
                    ToastUtils.show_always(context, "网络失败，请检查网络");
                }
            }
        });
    }

    private void handleIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupId = bundle.getString("GroupId");
        list = (ArrayList<GroupInfo>) bundle.getSerializable("userlist");
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.checkVertifyUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        userList = new Gson().fromJson(result.getString("InviteUserList"), new TypeToken<List<CheckInfo>>() {}.getType());
                        // userList 未包含用户名信息，此时从上一个页面中获取
                        for (int i = 0; i < userList.size(); i++) {
                            for (int j = 0; j < list.size(); j++) {
                                if (userList.get(i).getInviteUserId() != null && userList.get(i).getInviteUserId().equals(list.get(j).getUserId())) {
                                    userList.get(i).setInvitedUserName(list.get(j).getNickName());
                                }
                            }
                        }
                        tipView.setVisibility(View.GONE);
                        adapter = new JoinGroupAdapter(context, userList);
                        joinListView.setAdapter(adapter);
                        setListener();

                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "暂时没有加群消息需要处理~~");
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
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    private void setListener() {
        adapter.setOnListener(new JoinGroupAdapter.OnListener() {
            @Override
            public void tongyi(int position) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialogph(context, "正在获取数据");
                    sendRequest();
                } else {
                    ToastUtils.show_always(context, "网络连接失败，请稍后重试");
                }
            }

            @Override
            public void jujue(int position) {
                delDialog.show();
                delPosition = position;
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

    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("DealType", dealType);
            if (dealType == 1) {
                jsonObject.put("InviteUserId", userList.get(onClickTv).getInviteUserId());
                jsonObject.put("BeInvitedUserId", userList.get(onClickTv).getBeInviteUserId());
            } else {
                jsonObject.put("InviteUserId", userList.get(delPosition).getInviteUserId());
                jsonObject.put("BeInvitedUserId", userList.get(delPosition).getBeInviteUserId());
            }
            jsonObject.put("GroupId", groupId);            // groupid由上一个界面传递而来
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.checkDealUrl, tag, jsonObject, new VolleyCallback() {
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
                    if (dealType == 1) {
                        userList.get(onClickTv).setCheckType(2);
                    } else {
                        userList.remove(delPosition);
                    }
                    adapter.notifyDataSetChanged();
                    dealType = 1;
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_always(context, "无法获取用户Id");
                } else if (ReturnType != null && ReturnType.equals("T")) {
                    ToastUtils.show_always(context, "异常返回值");
                } else if (ReturnType != null && ReturnType.equals("200")) {
                    ToastUtils.show_always(context, "尚未登录");
                } else if (ReturnType != null && ReturnType.equals("1003")) {
                    ToastUtils.show_always(context, "异常返回值");
                } else if (ReturnType != null && ReturnType.equals("10031")) {
                    ToastUtils.show_always(context, "用户组不是验证群，不能采取这种方式邀请");
                } else if (ReturnType != null && ReturnType.equals("0000")) {
                    ToastUtils.show_always(context, "无法获取用户ID");
                } else if (ReturnType != null && ReturnType.equals("1004")) {
                    ToastUtils.show_always(context, "被邀请人不存在");
                } else if (ReturnType != null && ReturnType.equals("1011")) {
                    ToastUtils.show_always(context, "没有待您审核的消息");
                } else {
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_always(context, Message + "");
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                    dealType = 1;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        userList = null;
        list = null;
        adapter = null;
        joinListView = null;
        setContentView(R.layout.activity_null);
    }
}
