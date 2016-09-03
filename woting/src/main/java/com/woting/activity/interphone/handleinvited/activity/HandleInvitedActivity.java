package com.woting.activity.interphone.handleinvited.activity;

/**
 * 作者：xinlong on 2016/9/2 16:51
 * 邮箱：645700751@qq.com
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.interphone.handleinvited.adapter.ContactAddAdapter;
import com.woting.activity.interphone.handleinvited.model.UserInviteMeInside;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.MyActivityManager;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 接受好友邀请
 * 作者：xinlong on 2016/9/2 16:47
 * 邮箱：645700751@qq.com
 */
public class HandleInvitedActivity extends Activity implements View.OnClickListener, ContactAddAdapter.Callback {
    private LinearLayout lin_left;
    private ListView mlistview;
    private Dialog dialog;
    private int RequestType = 1;// 1为获取列表 2为添加好友 3为删除好友
    protected ArrayList<UserInviteMeInside> UserList;
    private Integer onclicktv;
    //	private boolean flag = false;// 判断接受好友返回值的flag
    private ContactAddAdapter adapter;
    private Dialog updatedialog;
    private HandleInvitedActivity context;
    private String tag = "HANDLE+INVITED_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);
        context=this;
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(context);
        setView();
        setListener();
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
            send();
        } else {
            ToastUtils.show_allways(this, "网络连接失败，请稍后重试");
        }
    }

    private void send() {
        JSONObject jsonObject = new JSONObject();
        try {
            // 公共请求属性
            jsonObject.put("SessionId", CommonUtils.getSessionId(this));
            jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
            jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
            jsonObject.put("IMEI", PhoneMessage.imei);
            PhoneMessage.getGps(this);
            jsonObject.put("GPS-longitude", PhoneMessage.longitude);
            jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
            // 模块属性
            jsonObject.put("UserId", CommonUtils.getUserId(this));
            jsonObject.put("CatelogId", "001");	// 此处定改 不要写死
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.getInvitedMeListUrl, tag, jsonObject, new VolleyCallback() {
            private String ContactMeString;
            //			private String SessionId;
            private String ReturnType;
            private String Message;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if(isCancelRequest){
                    return ;
                }
                try {
                    ReturnType = result.getString("ReturnType");
//					SessionId = result.getString("SessionId");
                    Message = result.getString("Message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    ContactMeString = result.getString("UserList");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    UserList = new Gson().fromJson(ContactMeString, new TypeToken<List<UserInviteMeInside>>() {}.getType());
                    adapter = new ContactAddAdapter(context, UserList, context);
                    mlistview.setAdapter(adapter);
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_allways(context, "页面加载失败，失败原因" + Message);
                } else if (ReturnType != null && ReturnType.equals("1011")) {
                    ToastUtils.show_allways(context, "所有的邀请信息都已经处理完毕");
                } else if (Message != null && !Message.trim().equals("")) {
                    ToastUtils.show_allways(context, Message + "页面加载失败，请稍后重试");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void setView() {
        lin_left = (LinearLayout) findViewById(R.id.head_left_btn);
        mlistview = (ListView) findViewById(R.id.listView_contact_add);
        UserList = new ArrayList<UserInviteMeInside>();
    }

    private void setListener() {
        lin_left.setOnClickListener(this);
        mlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                onclicktv = position;
                calldialog();		// 弹出dialog方法
                return false;
            }
        });
    }

    protected void calldialog() {
        View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
        TextView text_contnt = (TextView) dialog.findViewById(R.id.text_contnt);
        text_contnt.setText("是否删除");
        TextView tv_update = (TextView) dialog.findViewById(R.id.tv_update);
        TextView tv_qx = (TextView) dialog.findViewById(R.id.tv_qx);
        tv_update.setOnClickListener(this);
        tv_update.setText("确定");
        tv_qx.setOnClickListener(this);
        updatedialog = new Dialog(this, R.style.MyDialog);
        updatedialog.setContentView(dialog);
        updatedialog.setCanceledOnTouchOutside(true);
        updatedialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        updatedialog.show();

        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    // 长按时调用requesttype=2
                    RequestType = 2;
                    sendrequest();
                } else {
                    ToastUtils.show_allways(context, "网络连接失败，请稍后重试");
                }
                updatedialog.dismiss();
            }
        });

        tv_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedialog.dismiss();
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

    /**
     * callback 接口回调方法
     */
    @Override
    public void click(View v) {
        onclicktv = (Integer) v.getTag();			// 获取当前点击的位置
        RequestType = 1;
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
            sendrequest();
        } else {
            ToastUtils.show_allways(this, "网络连接失败，请稍后重试");
        }
    }

    private void sendrequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            // 公共请求属性
            jsonObject.put("SessionId", CommonUtils.getSessionId(this));
            jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
            jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
            jsonObject.put("IMEI", PhoneMessage.imei);
            PhoneMessage.getGps(this);
            jsonObject.put("GPS-longitude", PhoneMessage.longitude);
            jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
            jsonObject.put("PCDType", 1);
            // 模块属性
            jsonObject.put("UserId", CommonUtils.getUserId(this));
            jsonObject.put("InviteUserId", UserList.get(onclicktv).getUserId());
            if (RequestType == 1) {
                jsonObject.put("DealType", "1");
            } else if (RequestType == 2) {
                jsonObject.put("DealType", "2");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.InvitedDealUrl, tag, jsonObject, new VolleyCallback() {
            //			private String SessionId;
            private String ReturnType;
            private String Message;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if(isCancelRequest){
                    return ;
                }
                try {
                    ReturnType = result.getString("ReturnType");
//					SessionId = result.getString("SessionId");
                    Message = result.getString("Message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (RequestType == 1) {
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        ToastUtils.show_allways(context, "添加成功");
                        // 设置该属性
                        UserList.get(onclicktv).setType(2);
                        adapter.notifyDataSetChanged();
                        Intent pushintent = new Intent("push_refreshlinkman");
                        context.sendBroadcast(pushintent);
                    }
                    if (ReturnType != null && ReturnType.equals("1002")) {
                        ToastUtils.show_allways(context, "好友添加失败，失败原因" + Message);
                    } else {
                        if (Message != null && !Message.trim().equals("")) {
                            ToastUtils.show_allways(context, Message + "页面加载失败，请稍后重试");
                        }
                    }
                } else {
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        ToastUtils.show_allways(context, "删除成功");
                        UserList.remove(onclicktv);
                        adapter.notifyDataSetChanged();
                    }
                    if (ReturnType != null && ReturnType.equals("1002")) {
                        ToastUtils.show_allways(context, "好友删除失败，失败原因" + Message);
                    } else {
                        if (Message != null && !Message.trim().equals("")) {
                            ToastUtils.show_allways(context, Message + "页面加载失败，请稍后重试");
                        }
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.popOneActivity(context);
        lin_left = null;
        mlistview = null;
        UserList = null;
        onclicktv = null;
        adapter = null;
        updatedialog = null;
        context = null;
        tag = null;
        setContentView(R.layout.activity_null);
    }
}
