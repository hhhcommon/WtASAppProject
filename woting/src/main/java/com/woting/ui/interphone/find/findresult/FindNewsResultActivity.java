package com.woting.ui.interphone.find.findresult;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.interphone.find.findresult.adapter.FindFriendResultAdapter;
import com.woting.ui.interphone.find.findresult.adapter.FindGroupResultAdapter;
import com.woting.ui.interphone.find.friendadd.FriendAddActivity;
import com.woting.ui.interphone.find.groupadd.GroupAddActivity;
import com.woting.ui.interphone.group.groupcontrol.groupnews.TalkGroupNewsActivity;
import com.woting.ui.interphone.group.groupcontrol.personnews.TalkPersonNewsActivity;
import com.woting.ui.interphone.model.UserInviteMeInside;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果页面
 * 辛龙
 * 2016年1月20日
 */
public class FindNewsResultActivity extends AppBaseActivity implements OnClickListener, TipView.WhiteViewClick {
    private XListView mxlistview;
    private int RefreshType;// 1，下拉刷新 2，加载更多
    private Dialog dialog;
    private String searchstr;

    private List<UserInviteMeInside> newList = new ArrayList<>();
    private List<GroupInfo> GroupList = new ArrayList<>();
    private String type;
    private int PageNum;
    private FindFriendResultAdapter adapter;
    private FindGroupResultAdapter adapters;
    private String tag = "FINDNEWS_RESULT_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private boolean isFriend=false;

    private TipView tipView;// 搜索没有数据提示

    @Override
    public void onWhiteViewClick() {
        searchFriendOrGroup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findnews_result);
        setView();
        setListener();
        searchstr = getIntent().getStringExtra("searchstr");
        type = getIntent().getStringExtra("type");
        if (!type.trim().equals("")) {
            searchFriendOrGroup();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.IS_ERROR);
        }
    }

    // 初始化 View
    private void setView() {
        mxlistview = (XListView) findViewById(R.id.listview_querycontact);
        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);
    }

    // 搜索好友或群组
    private void searchFriendOrGroup() {
        if (type.equals("friend")) {// 搜索好友
            if (!searchstr.trim().equals("")) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialog(context);
                    PageNum = 1;
                    RefreshType = 1;
                    getFriend();
                } else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_NET);
                }
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        } else if (type.equals("group")) {// 搜索群组
            if (!searchstr.trim().equals("")) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialog(context);
                    PageNum = 1;
                    RefreshType = 1;
                    getGroup();
                } else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_NET);
                }
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        }
    }

    // 设置对应的点击事件
    private void setListener() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        mxlistview.setPullRefreshEnable(true);
        mxlistview.setPullLoadEnable(true);
        mxlistview.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                if (type.equals("friend")) {// 获取刷新好友数据
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        RefreshType = 1;
                        PageNum = 1;
                        getFriend();
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_NET);
                    }
                } else if (type.equals("group")) {// 获取刷新群组数据
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        RefreshType = 1;
                        PageNum = 1;
                        getGroup();
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_NET);
                    }
                }
            }

            @Override
            public void onLoadMore() {
                if (!type.trim().equals("")) {
                    if (type.equals("friend")) {// 获取加载更多好友数据
                        if (CommonHelper.checkNetwork(context)) {
                            RefreshType = 2;
                            PageNum = PageNum + 1;
                            getFriend();
                        }
                    } else if (type.equals("group")) {// 获取加载更多群组数据
                        if (CommonHelper.checkNetwork(context)) {
                            RefreshType = 2;
                            PageNum = PageNum + 1;
                            getGroup();
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
                if (!type.trim().equals("")) {
                    if (type.equals("friend")) {
                        if (position > 0) {
                            if (newList != null && newList.size() > 0) {
                                if(GlobalConfig.list_person!=null&&GlobalConfig.list_person.size()>0&&newList.get(position - 1)!=null){
                                    for(int i=0;i<GlobalConfig.list_person.size();i++){
                                      if(GlobalConfig.list_person.get(i).getUserId().equals(newList.get(position - 1).getUserId())){
                                          isFriend=true;
                                      }
                                        if(isFriend){
                                            //好友
                                            Intent intent = new Intent(FindNewsResultActivity.this, TalkPersonNewsActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("contact", newList.get(position - 1));
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }else{
                                            //非好友
                                            Intent intent = new Intent(FindNewsResultActivity.this, FriendAddActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("contact", newList.get(position - 1));
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    }
                                }else{
                                    //Global通讯录保存数据异常时，所有用户都认为非好友
                                    Intent intent = new Intent(FindNewsResultActivity.this, FriendAddActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("contact", newList.get(position - 1));
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }

                            } else {
                                ToastUtils.show_always(context, "获取数据异常");
                            }
                        }
                    } else if (type.equals("group")) {
                        if (position > 0) {
                            if (GroupList != null && GroupList.size() > 0) {
                                if (GroupList.get(position - 1).getUserIds() != null &&
                                        !GroupList.get(position - 1).getUserIds().trim().equals("")) {
                                    if (isGroupUser(GroupList.get(position - 1).getUserIds())) {
                                        Intent intent = new Intent(context, TalkGroupNewsActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("data", GroupList.get(position - 1));
                                        bundle.putString("type", "groupaddactivity");
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(context, GroupAddActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("contact", GroupList.get(position - 1));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                }
                            } else {
                                ToastUtils.show_always(context, "获取数据异常");
                            }
                        }
                    }
                }
            }
        });
    }

    private boolean isGroupUser(String getUserIds) {
        // 另外一种写法
        // (","+userIds).indexOf(","+userId)!=-1

        boolean isTrue = false;
        String[] strArray;
        strArray = getUserIds.split(",");
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i].equals(CommonUtils.getUserId(context))) {
                isTrue = true;
                break;
            }
        }
        return isTrue;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
        }
    }

    // 获取好友数据
    protected void getFriend() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("Page", PageNum);
            jsonObject.put("PageSize", "20");
            jsonObject.put("SearchStr", searchstr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.searchStrangerUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            String ContactMeString = result.getString("UserList");
                            PageNum++;
                            ArrayList<UserInviteMeInside> UserList = new Gson().fromJson(ContactMeString, new TypeToken<List<UserInviteMeInside>>() {
                            }.getType());
                            if (UserList != null && UserList.size() > 0) {
                                tipView.setVisibility(View.GONE);
                                if (RefreshType == 1) {
                                    newList.clear();
                                    newList.addAll(UserList);
                                    mxlistview.setAdapter(adapter = new FindFriendResultAdapter(context, newList));
                                } else {
                                    newList.addAll(UserList);
                                    adapter.ChangeData(newList);
                                }
                                setItemListener();    // 设置 item 的点击事件
                                if(UserList.size()!=20){
                                    mxlistview.setPullLoadEnable(false);
                                }else{
                                    mxlistview.setPullLoadEnable(true);
                                }

                            } else {
                                if (RefreshType == 1) {
                                    tipView.setVisibility(View.VISIBLE);
                                    tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到该好友哟\n换个好友再试一次吧");
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        if (RefreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到该好友哟\n换个好友再试一次吧");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (RefreshType == 1) {
                    mxlistview.stopRefresh();
                } else {
                    mxlistview.stopLoadMore();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                if (RefreshType == 1) {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                }
            }
        });
    }

    // 获取群组数据
    protected void getGroup() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("Page", PageNum);
            jsonObject.put("PageSize", "20");
            jsonObject.put("SearchStr", searchstr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.searchStrangerGroupUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            String GroupMeString = result.getString("GroupList");
                            PageNum++;
                            ArrayList<GroupInfo> _groupList = new Gson().fromJson(GroupMeString, new TypeToken<List<GroupInfo>>() {
                            }.getType());
                            if (_groupList != null && _groupList.size() > 0) {
                                tipView.setVisibility(View.GONE);
                                if (RefreshType == 1) {
                                    GroupList.clear();
                                    GroupList.addAll(_groupList);
                                    mxlistview.setAdapter(adapters = new FindGroupResultAdapter(context, GroupList));
                                } else {
                                    adapters.ChangeData(GroupList);
                                }
                                setItemListener();    // 设置 item 的点击事件
                                if(_groupList.size()!=20){
                                    mxlistview.setPullLoadEnable(false);
                                }else{
                                    mxlistview.setPullLoadEnable(true);
                                }
                            } else {
                                tipView.setVisibility(View.VISIBLE);
                                tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到该群组哟\n换个群组再试一次吧");
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到该群组哟\n换个群组再试一次吧");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (RefreshType == 1) {
                    mxlistview.stopRefresh();
                } else {
                    mxlistview.stopLoadMore();
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
        newList = null;
        GroupList = null;
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
