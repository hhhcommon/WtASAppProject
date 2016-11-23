package com.woting.ui.interphone.find.findresult;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.interphone.find.findresult.adapter.FindFriendResultAdapter;
import com.woting.ui.interphone.find.findresult.adapter.FindGroupResultAdapter;
import com.woting.ui.interphone.find.friendadd.FriendAddActivity;
import com.woting.ui.interphone.find.groupadd.GroupAddActivity;
import com.woting.ui.interphone.group.groupcontrol.groupnews.TalkGroupNewsActivity;
import com.woting.ui.interphone.model.UserInviteMeInside;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果页面
 *
 * @author 辛龙
 *         2016年1月20日
 */
public class FindNewsResultActivity extends AppBaseActivity implements OnClickListener {
    private LinearLayout lin_left;
    private XListView mxlistview;
    private int RefreshType;        // 1，下拉刷新 2，加载更多
    private Dialog dialog;
    private String searchstr;
    private ArrayList<UserInviteMeInside> UserList;
    private ArrayList<GroupInfo> GroupList;
    private String type;
    private int PageNum;
    private FindFriendResultAdapter adapter;
    private FindGroupResultAdapter adapters;
    private String tag = "FINDNEWS_RESULT_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findnews_result);
        setView();
        setListener();
        searchstr = this.getIntent().getStringExtra("searchstr");
        type = this.getIntent().getStringExtra("type");
        if (type.trim() != null && !type.trim().equals("")) {
            if (type.equals("friend")) {
                // 搜索好友
                if (searchstr.trim() != null && !searchstr.trim().equals("")) {
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialogph(FindNewsResultActivity.this, "正在获取数据");
                        PageNum = 1;
                        RefreshType = 1;
                        getFriend();
                    } else {
                        ToastUtils.show_always(FindNewsResultActivity.this, "网络连接失败，请稍后重试");
                    }
                } else {
                    // 如果当前界面没有接收到数据就给以友好提示
                    ToastUtils.show_always(FindNewsResultActivity.this, "网络连接失败，请稍后重试");
                }
            } else if (type.equals("group")) {
                // 搜索群组
                if (searchstr.trim() != null && !searchstr.trim().equals("")) {
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialogph(FindNewsResultActivity.this, "正在获取数据");
                        PageNum = 1;
                        RefreshType = 1;
                        getGroup();
                    } else {
                        ToastUtils.show_always(FindNewsResultActivity.this, "网络连接失败，请稍后重试");
                    }
                } else {
                    // 如果当前界面没有接收到数据就给以友好提示
                    ToastUtils.show_always(FindNewsResultActivity.this, "网络连接失败，请稍后重试");
                }
            }
        } else {
            // 如果当前界面没有接收到搜索类型数据就给以友好提示
            ToastUtils.show_always(FindNewsResultActivity.this, "网络连接失败，请稍后重试");
        }
    }

    private void setView() {
        lin_left = (LinearLayout) findViewById(R.id.head_left_btn);
        mxlistview = (XListView) findViewById(R.id.listview_querycontact);
    }

    private void setListener() {// 设置对应的点击事件
        lin_left.setOnClickListener(this);
        mxlistview.setPullRefreshEnable(false);
        mxlistview.setPullLoadEnable(false);
        mxlistview.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                // 数据请求
                if (type.trim() != null && !type.trim().equals("")) {
                    if (type.equals("friend")) {
                        // 获取刷新好友数据
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                            RefreshType = 1;
                            PageNum = 1;
                            getFriend();
                        } else {
                            ToastUtils.show_always(FindNewsResultActivity.this, "网络连接失败，请稍后重试");
                        }
                    } else if (type.equals("group")) {
                        // 获取刷新群组数据
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                            RefreshType = 1;
                            PageNum = 1;
                            getGroup();
                        } else {
                            ToastUtils.show_always(FindNewsResultActivity.this, "网络连接失败，请稍后重试");
                        }
                    }
                }
            }

            @Override
            public void onLoadMore() {
                if (type.trim() != null && !type.trim().equals("")) {
                    if (type.equals("friend")) {
                        // 获取加载更多好友数据
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                            RefreshType = 2;
                            PageNum = PageNum + 1;
                            getFriend();
                        } else {
                            ToastUtils.show_always(FindNewsResultActivity.this, "网络连接失败，请稍后重试");
                        }
                    } else if (type.equals("group")) {
                        // 获取加载更多群组数据
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                            RefreshType = 2;
                            PageNum = PageNum + 1;
                            getGroup();
                        } else {
                            ToastUtils.show_always(FindNewsResultActivity.this, "网络连接失败，请稍后重试");
                        }
                    }
                }
            }
        });
    }

    private void setItemListener() {// 设置item对应的点击事件
        mxlistview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (type.trim() != null && !type.trim().equals("")) {
                    if (type.equals("friend")) {
                        if (position > 0) {
                            if (UserList != null && UserList.size() > 0) {
                                Intent intent = new Intent(FindNewsResultActivity.this, FriendAddActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("contact", UserList.get(position - 1));
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else {
                                ToastUtils.show_always(FindNewsResultActivity.this, "获取数据异常");
                            }
                        }
                    } else if (type.equals("group")) {
                        if (position > 0) {
                            if (GroupList != null && GroupList.size() > 0) {
                                if (GroupList.get(position - 1).getGroupCreator().equals(CommonUtils.getUserId(context))) {
                                    Intent intent = new Intent(FindNewsResultActivity.this, TalkGroupNewsActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("data", GroupList.get(position - 1));
                                    bundle.putString("type", "groupaddactivity");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(FindNewsResultActivity.this, GroupAddActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("contact", GroupList.get(position - 1));
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            } else {
                                ToastUtils.show_always(FindNewsResultActivity.this, "获取数据异常");
                            }
                        }
                    }
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

    /*
     * 获取好友数据
     */
    protected void getFriend() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("Page", PageNum);
            jsonObject.put("SearchStr", searchstr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.searchStrangerUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;
            private String ContactMeString;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (isCancelRequest) {
                    return;
                }
                try {
                    ReturnType = result.getString("ReturnType");
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
                    UserList = new Gson().fromJson(ContactMeString, new TypeToken<List<UserInviteMeInside>>() {
                    }.getType());
                    if (UserList != null && UserList.size() > 0) {
                        if (RefreshType == 1) {
                            adapter = new FindFriendResultAdapter(FindNewsResultActivity.this, UserList);
                            mxlistview.setAdapter(adapter);
                            mxlistview.stopRefresh();
                        } else {
                            adapter.ChangeData(UserList);
                            mxlistview.stopLoadMore();
                        }
                        setItemListener();    // 设置item的点击事件
                    } else {
                        ToastUtils.show_always(FindNewsResultActivity.this, "数据获取失败，请稍候再试");    // json解析失败
                    }
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    if (RefreshType == 1) {
                        mxlistview.stopRefresh();
                    } else {
                        mxlistview.stopLoadMore();
                    }
                    // 获取数据失败
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_always(FindNewsResultActivity.this, Message);
                    } else {
                        ToastUtils.show_always(FindNewsResultActivity.this, "数据获取失败，请稍候再试");
                    }
                } else {
                    if (RefreshType == 1) {
                        mxlistview.stopRefresh();
                    } else {
                        mxlistview.stopLoadMore();
                    }
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_always(FindNewsResultActivity.this, Message);
                    } else {
                        ToastUtils.show_always(FindNewsResultActivity.this, "数据获取失败，请稍候再试");
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

    /*
     * 获取群组数据
     */
    protected void getGroup() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            // 模块属性
            jsonObject.put("Page", PageNum);
            jsonObject.put("SearchStr", searchstr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.searchStrangerGroupUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;
            private String GroupMeString;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (isCancelRequest) {
                    return;
                }
                try {
                    ReturnType = result.getString("ReturnType");
                    Message = result.getString("Message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    GroupMeString = result.getString("GroupList");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    GroupList = new Gson().fromJson(GroupMeString, new TypeToken<List<GroupInfo>>() {
                    }.getType());
                    if (GroupList != null && GroupList.size() > 0) {
                        if (RefreshType == 1) {
                            adapters = new FindGroupResultAdapter(FindNewsResultActivity.this, GroupList);
                            mxlistview.setAdapter(adapters);
                            mxlistview.stopRefresh();
                        } else {
                            adapters.ChangeData(GroupList);
                            mxlistview.stopLoadMore();
                        }
                        setItemListener();    // 设置item的点击事件
                    } else {
                        ToastUtils.show_always(FindNewsResultActivity.this, "数据获取失败，请稍候再试");    // json解析失败
                    }
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    if (RefreshType == 1) {
                        mxlistview.stopRefresh();
                    } else {
                        mxlistview.stopLoadMore();
                    }
                    // 获取数据失败
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_always(FindNewsResultActivity.this, Message);
                    } else {
                        ToastUtils.show_always(FindNewsResultActivity.this, "数据获取失败，请稍候再试");
                    }
                } else {
                    if (RefreshType == 1) {
                        mxlistview.stopRefresh();
                    } else {
                        mxlistview.stopLoadMore();
                    }
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_always(FindNewsResultActivity.this, Message);
                    } else {
                        ToastUtils.show_always(FindNewsResultActivity.this, "数据获取失败，请稍候再试");
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
        UserList = null;
        GroupList = null;
        lin_left = null;
        mxlistview = null;
        adapter = null;
        adapters = null;
        context = null;
        dialog = null;
        searchstr = null;
        type = null;
        tag = null;
        setContentView(R.layout.activity_null);
    }
}
