package com.woting.ui.interphone.group.groupcontrol.setgroupmanager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
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
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.common.model.UserInfo;
import com.woting.ui.interphone.group.groupcontrol.addgroupmanager.AddGroupManagerActivity;
import com.woting.ui.interphone.group.groupcontrol.setgroupmanager.adapter.SetGroupManagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SetGroupManagerActivity extends AppBaseActivity implements OnClickListener, TipView.WhiteViewClick {
    private Dialog dialog;
    private String groupId;
    private String tag = "SET_GROUP_MANAGER_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    private TipView tipView;// 没有网络没有数据提示
    private TipView tipSearchNull;// 搜索数据为空提示
    private TextView tv_head_right;

    private TextView tv_sum;
    private String[] managerList;
    private ListView lv_main;
    private List<UserInfo> mainList= new ArrayList<>();
    private List<GroupInfo> tempList;
    private List<String> resultList=new ArrayList<>();
    private SetGroupManagerAdapter adapter;
    private boolean firstFlag; //只在第一次执行的事件


    @Override
    public void onWhiteViewClick() {
     /*   groupId = getIntent().getStringExtra("GroupId");
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
        }*/
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
        lv_main=(ListView)findViewById(R.id.lv_main);                   // 群管理员列表

        tipSearchNull = (TipView) findViewById(R.id.tip_search_null);
        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        groupId = getIntent().getStringExtra("GroupId");
        managerList=(String [])getIntent().getSerializableExtra("GroupManager");
        tempList=(List<GroupInfo>)getIntent().getSerializableExtra("GroupManagerData");

        if(managerList!=null&&managerList.length>0){
            tv_sum.setText("管理员("+managerList.length+")");
            for(int i=0;i<tempList.size();i++){
                if(!TextUtils.isEmpty(tempList.get(i).getUserId())){
                UserInfo userInfo=new UserInfo();
                if(!TextUtils.isEmpty(tempList.get(i).getNickName())){
                userInfo.setNickName(tempList.get(i).getNickName());
                }else{
                    userInfo.setNickName(tempList.get(i).getName());
                }
                if(!TextUtils.isEmpty(tempList.get(i).getPortraitMini())){
                userInfo.setPortraitMini(tempList.get(i).getPortraitMini());
                }
                if(!TextUtils.isEmpty(tempList.get(i).getUserId())){
                userInfo.setUserId(tempList.get(i).getUserId());
                }
                    mainList.add(userInfo);
                }
            }
            if(mainList.size()>0){
            adapter = new SetGroupManagerAdapter(context,mainList);
            lv_main.setAdapter(adapter);
                initOnClick();
            }else{
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_DATA, "群内没有其他管理员了\n赶紧去设置一个");
            }
        }else{
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "群内没有其他管理员了\n赶紧去设置一个");
        }
    }

    private void initOnClick() {
        adapter.setOnListener(new SetGroupManagerAdapter.friendCheck() {
            @Override
            public void checkposition(int position) {
                if(mainList.get(position).getCheckType()==2){
                    mainList.get(position).setCheckType(1);
                }else{
                    mainList.get(position).setCheckType(2);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.head_right_btn:
                if(!firstFlag){
                    for(int i =0;i<mainList.size();i++){
                        mainList.get(i).setViewType(2);//ViewType=2 显示
                        mainList.get(i).setCheckType(1);//CheckType=2 勾选
                    }
                    adapter.notifyDataSetChanged();
                    tv_head_right.setText("删除");
                    firstFlag=true;
                }else{
                    resultList.clear();
                    if(mainList.size()==1){
                        ToastUtils.show_always(context,"群里只有您自己一个管理员，快去添加吧");
                        return;
                    }
                    for(int i=0;i<mainList.size();i++){
                        if(mainList.get(i).getCheckType()==2){
                            resultList.add(mainList.get(i).getUserId());
                        }
                    }
                    if(resultList.size()>0){
                        //调接口
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                            dialog = DialogUtils.Dialogph(context, "正在删除群成员");
                            send();
                        } else {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_NET);
                        }
                    }
                }

                break;
            case R.id.add_manager:
                if(!TextUtils.isEmpty(groupId)){
                    Intent intent=new Intent(this, AddGroupManagerActivity.class);
                    intent.putExtra("GroupId",groupId);
                    startActivity(intent);
                }else{
                    ToastUtils.show_always(context,"未获取到组Id信息，请检查网络或返回上一级页面重试");
                }

                break;
        }
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
            jsonObject.put("DelAdminUserIds", resultList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.setGroupAdminUrl, tag, jsonObject, new VolleyCallback() {
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
                    ToastUtils.show_always(context,"已经删除了该管理员");
                } else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_DATA, "");
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
