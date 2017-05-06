package com.woting.ui.interphone.notice.groupapply;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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
import com.woting.ui.interphone.model.UserInfo;
import com.woting.common.service.InterPhoneControl;
import com.woting.ui.interphone.notice.groupapply.adapter.HandleGroupApplyAdapter;
import com.woting.ui.interphone.notice.messagecenter.dao.MessageNotifyDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 加组消息
 *
 * @author 辛龙
 *         2016年4月13日
 */
public class HandleGroupApplyActivity extends AppBaseActivity implements OnClickListener, TipView.WhiteViewClick {
    private HandleGroupApplyAdapter adapter;
    private List<UserInfo> userList = new ArrayList<>();// 存储服务器返回值的 list

    private Dialog delDialog;
    private Dialog dialog;
    private ListView listGroupMember;

    protected int sum = 0;
    private int dealType;// == 1 接受  == 2 拒绝
    private int Position;
    private String groupId;
    private String tag = "HANDLE_GROUP_APPLY_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    private TipView tipView;// 没有网络、没有数据提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handlegroupapply);
        delDialog();
        initView();
        getData();
    }

    // 设置界面
    private void initView() {
        groupId = getIntent().getStringExtra("GroupId");
        findViewById(R.id.head_left_btn).setOnClickListener(this);

        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        listGroupMember = (ListView) findViewById(R.id.lv_groupmembers);
    }

    // 获取数据
    private void getData() {
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
            case R.id.tv_cancle:
                // 取消弹出框
                delDialog.dismiss();
                break;
            case R.id.tv_confirm:
                // 发送确认拒绝的消息
                delDialog.dismiss();
                dealType = 2;
                sendRequest();
                break;
        }
    }

    // 初始化对话框
    private void delDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("确定拒绝?");

        delDialog = new Dialog(context, R.style.MyDialog);
        delDialog.setContentView(dialog1);
        delDialog.setCanceledOnTouchOutside(false);
        delDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    // 重新获取数据
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

    // 主网络请求
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.JoinGroupListUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            userList = new Gson().fromJson(result.getString("UserList"), new TypeToken<List<UserInfo>>() {
                            }.getType());
                            adapter = new HandleGroupApplyAdapter(context, userList);
                            listGroupMember.setAdapter(adapter);
                            setListener();
                            tipView.setVisibility(View.GONE);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.IS_ERROR);
                        }
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "没有需要处理的消息哦~~");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_DATA, "没有需要处理的消息哦~~");
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

    // 发送应答请求
    private void sendRequest() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            ToastUtils.show_always(context, "网络连接失败，请检查网络设置!");
            return;
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("DealType", dealType);
            jsonObject.put("ApplyUserId", userList.get(Position).getUserId());
            jsonObject.put("GroupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.applyDealUrl, tag, jsonObject, new VolleyCallback() {
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
                    // 此处的消息处理一定要放到list的remove之前，否则获取当前position的数据不正确
                    if(dealType==1){
                        setControlMessage("true",userList.get(Position).getUserId(),groupId);
                    }else{
                        setControlMessage("false",userList.get(Position).getUserId(),groupId);
                    }
                    userList.remove(Position);
                    adapter.notifyDataSetChanged();
                    setResult(1);
                } else if (ReturnType != null && ReturnType.equals("1002")) {
//                    ToastUtils.show_always(context, "无法获取用户Id");
                    Log.e("加群消息", "无法获取用户Id");
                    ToastUtils.show_always(context, "消息出错了，请您稍后再试");
                } else if (ReturnType != null && ReturnType.equals("T")) {
//                    ToastUtils.show_always(context, "异常返回值");
                    Log.e("加群消息", "异常返回值");
                    ToastUtils.show_always(context, "消息出错了，请您稍后再试");
                } else if (ReturnType != null && ReturnType.equals("200")) {
//                    ToastUtils.show_always(context, "尚未登录");
                    Log.e("加群消息", "尚未登录");
                    ToastUtils.show_always(context, "消息出错了，请您稍后再试");
                } else if (ReturnType != null && ReturnType.equals("1003")) {
//                    ToastUtils.show_always(context, "异常返回值");
                    Log.e("加群消息", "异常返回值");
                    ToastUtils.show_always(context, "消息出错了，请您稍后再试");
                } else if (ReturnType != null && ReturnType.equals("10031")) {
//                    ToastUtils.show_always(context, "用户组不是验证群，不能采取这种方式邀请");
                    Log.e("加群消息", "用户组不是验证群，不能采取这种方式邀请");
                    ToastUtils.show_always(context, "消息出错了，请您稍后再试");
                } else if (ReturnType != null && ReturnType.equals("0000")) {
//                    ToastUtils.show_always(context, "无法获取用户ID");
                    Log.e("加群消息", "无法获取用户ID");
                    ToastUtils.show_always(context, "消息出错了，请您稍后再试");
                } else if (ReturnType != null && ReturnType.equals("1004")) {
//                    ToastUtils.show_always(context, "被邀请人不存在");
                    Log.e("加群消息", "被邀请人不存在");
                    ToastUtils.show_always(context, "消息出错了，请您稍后再试");
                } else if (ReturnType != null && ReturnType.equals("1011")) {
//                    ToastUtils.show_always(context, "没有待您审核的消息");
                    Log.e("加群消息", "没有待您审核的消息");
                    ToastUtils.show_always(context, "消息出错了，请您稍后再试");
                } else {
                    try {
                         String   Message = result.getString("Message");
                        if (Message != null && !Message.trim().equals("")) {
//                        ToastUtils.show_always(context, Message + "");
                            Log.e("加群消息", Message + "");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ToastUtils.show_always(context, "消息出错了，请您稍后再试");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                ToastUtils.showVolleyError(context);
            }
        });
    }

    private void setListener() {
        adapter.setOnListener(new HandleGroupApplyAdapter.OnListener() {
            @Override
            public void tongyi(int position) {
                // 发送确认请求
                Position = position;
                dealType = 1;
                sendRequest();
            }

            @Override
            public void jujue(int position) {
                delDialog.show();
                Position = position;
            }
        });
    }


    /*
     * 发送业务控制回复
     */
    private void setControlMessage(String replyType, String GroupId, String PersonId) {
        replyType = replyType != null && !replyType.equals("") ? replyType : "other";
        // 群组邀请
        MessageNotifyDao dbDao = new MessageNotifyDao(context);
        List<String> message_list = dbDao.queryNotifyMessageId("g2", GroupId, PersonId);
        if (replyType.equals("true")) {
            // 同意邀请
            for (int i = 0; i < message_list.size(); i++) {
                try {
                    dbDao.upDataNotifyMessage(message_list.get(i), "您同意了他的入组申请");
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (replyType.equals("false")) {
            // 拒绝邀请
            for (int i = 0; i < message_list.size(); i++) {
                try {
                    dbDao.upDataNotifyMessage(message_list.get(i), "您拒绝了他的入组申请");
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // 发送业务回复
        for (int i = 0; i < message_list.size(); i++) {
            try {
                InterPhoneControl.universalControlReply(message_list.get(i), 4, 2, 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        userList = null;
        listGroupMember = null;
        setContentView(R.layout.activity_null);
    }
}
