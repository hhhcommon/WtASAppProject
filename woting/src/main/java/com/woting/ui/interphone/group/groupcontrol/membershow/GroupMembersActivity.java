package com.woting.ui.interphone.group.groupcontrol.membershow;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.woting.common.widgetui.HeightListView;
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.interphone.group.groupcontrol.groupnumdel.GroupMemberDelActivity;
import com.woting.ui.interphone.group.groupcontrol.grouppersonnews.GroupPersonNewsActivity;
import com.woting.ui.interphone.group.groupcontrol.memberadd.GroupMemberAddActivity;
import com.woting.ui.interphone.group.groupcontrol.membershow.adapter.CreateGroupMembersAdapter;
import com.woting.ui.interphone.group.groupcontrol.membershow.adapter.GroupMemberHeadAdapter;
import com.woting.ui.interphone.group.groupcontrol.personnews.TalkPersonNewsActivity;
import com.woting.ui.interphone.linkman.view.CharacterParser;
import com.woting.ui.interphone.linkman.view.PinyinComparator;
import com.woting.ui.interphone.linkman.view.SideBar;
import com.woting.ui.interphone.linkman.view.SideBar.OnTouchingLetterChangedListener;
import com.woting.ui.interphone.model.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 展示全部群成员
 *
 * @author 辛龙
 *         2016年4月13日
 */
public class GroupMembersActivity extends AppBaseActivity implements
        OnClickListener, TextWatcher, OnItemClickListener, OnTouchingLetterChangedListener, TipView.WhiteViewClick {

    private CharacterParser characterParser = CharacterParser.getInstance();// 实例化汉字转拼音类
    private PinyinComparator pinyinComparator = new PinyinComparator();
    private CreateGroupMembersAdapter adapter;
    private SideBar sideBar;
    private List<UserInfo> srcList;
    private List<UserInfo> userList = new ArrayList<>();

    private Dialog dialog;
    private TextView dialogs;
    private TextView textHeadName;
    private ListView listView;
    private EditText editSearchContent;
    private ImageView imageClear;

    private TipView tipView;// 没有数据、数据出错提示
    private TipView tipSearchNull;// 搜索没有数据提示

    private String groupId;
    private String tag = "GROUP_MEMBERS_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private ListView lv_manager;
    private TextView manager_num;
    private String[] managerList;
    private List<UserInfo> headList= new ArrayList<>();
    private String groupMaster;
    private Dialog managerDialog;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmembers);
        initView();
        initDialog();
    }

    private void initView() {
        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);
        tipSearchNull = (TipView) findViewById(R.id.tip_search_null);

        groupId = getIntent().getStringExtra("GroupId");
        managerList=(String [])getIntent().getSerializableExtra("GroupManager");
        groupMaster = getIntent().getStringExtra("GroupMaster");


        findViewById(R.id.head_right_btn).setOnClickListener(this);
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        editSearchContent = (EditText) findViewById(R.id.et_search);// 搜索控件
        editSearchContent.addTextChangedListener(this);

        imageClear = (ImageView) findViewById(R.id.image_clear);
        imageClear.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.country_lvcountry);
        listView.setOnItemClickListener(this);

        dialogs = (TextView) findViewById(R.id.dialog);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        sideBar.setTextView(dialogs);
        sideBar.setOnTouchingLetterChangedListener(this);

        textHeadName = (TextView) findViewById(R.id.head_name_tv);// 更新当前组员人数的控件

        View headView = LayoutInflater.from(context).inflate(R.layout.head_group_member, null);// headview
        lv_manager = (ListView) headView.findViewById(R.id.lv_manager);
        manager_num = (TextView) headView.findViewById(R.id.indexTv);  //管理员人数
        manager_num.setText("群主.管理员 ("+managerList.length+")");
        listView.addHeaderView(headView);

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
            case R.id.image_clear:
                imageClear.setVisibility(View.INVISIBLE);
                editSearchContent.setText("");
                tipSearchNull.setVisibility(View.GONE);
                break;
            case R.id.tv_add:
                //ToastUtils.show_always(context,"添加群成员");
                startToActivity(GroupMemberAddActivity.class);
                if(managerDialog!=null&&managerDialog.isShowing()){
                    managerDialog.dismiss();
                }
                break;
            case R.id.tv_del:
                startToActivity(GroupMemberDelActivity.class);
                //ToastUtils.show_always(context,"删除群成员");
                if(managerDialog!=null&&managerDialog.isShowing()){
                    managerDialog.dismiss();
                }
                break;
            case R.id.tv_cancel:
                if(managerDialog!=null&&managerDialog.isShowing()){
                    managerDialog.dismiss();
                }
                break;
            case R.id.head_right_btn:
                if(managerDialog!=null){
                    managerDialog.show();
                }
                break;
        }
    }

    // 初始化对话框
    private void initDialog() {
        View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_group_manager, null);
        dialog.findViewById(R.id.tv_add).setOnClickListener(this);
        dialog.findViewById(R.id.tv_del).setOnClickListener(this);
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(this);
        managerDialog = new Dialog(context, R.style.MyDialog);
        managerDialog .setContentView(dialog);
        managerDialog .setCanceledOnTouchOutside(true);
        managerDialog .getWindow().setBackgroundDrawableResource(R.color.dialog);
    }



    // 网络请求主函数
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.grouptalkUrl, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType == null || ReturnType.equals("")) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    } else if (ReturnType.equals("1001") || ReturnType.equals("1002")) {
                        try {
                            if(srcList!=null&&srcList.size()>0){
                                srcList.clear();
                            }
                            if(headList!=null&&headList.size()>0){
                                headList.clear();
                            }
                            srcList = new Gson().fromJson(result.getString("UserList"), new TypeToken<List<UserInfo>>() {
                            }.getType());

                            if (srcList != null && srcList.size() != 0) {
                                tipView.setVisibility(View.GONE);

                                //处理headview的数据，将id相同的数据从排序list里拿掉
                                if(managerList!=null&&managerList.length>0){
                                   for(int i =0;i<managerList.length;i++){
                                       for(int j=0;j<srcList.size();j++){
                                          if(managerList[i].equals(srcList.get(j).getUserId())){
                                              UserInfo userInfo = srcList.remove(j);
                                              String s=userInfo.getNickName();
                                              if(groupMaster.equals(userInfo.getUserId())){
                                                  //是群主
                                                  userInfo.setCheckType(2);//在此处2是群主
                                                  headList.add(0,userInfo);
                                              }else{
                                                  headList.add(userInfo);
                                              }

                                              break;
                                          }
                                       }
                                   }
                                }
                                    lv_manager.setAdapter(new GroupMemberHeadAdapter(context,headList));
                                    new HeightListView(context).setListViewHeightBasedOnChildren(lv_manager);

                                userList.clear();
                                userList.addAll(srcList);
                                filledData(userList);
                                Collections.sort(userList, pinyinComparator);
                                listView.setAdapter(adapter = new CreateGroupMembersAdapter(context, userList));
                            } else {
                                tipView.setVisibility(View.VISIBLE);
                                tipView.setTipView(TipView.TipStatus.NO_DATA, "群组中没有成员!");
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.IS_ERROR);
                        }

                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "群组中没有成员!");
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_DATA, "群组中没有成员!");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    private void filledData(List<UserInfo> person) {
        for (int i = 0; i < person.size(); i++) {
            try {
                person.get(i).setName(person.get(i).getNickName());
                String pinyin = characterParser.getSelling(person.get(i).getNickName());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {// 判断首字母是否是英文字母
                    person.get(i).setSortLetters(sortString.toUpperCase());
                } else {
                    person.get(i).setSortLetters("#");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 跳转到新的 Activity
    private void startToActivity(Class toClass) {
        Intent intent = new Intent(context, toClass);
        Bundle bundle = new Bundle();
        bundle.putString("GroupId", groupId);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialog(context);
                    send();
                } else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_NET);
                }
                setResult(1);
                break;
        }
    }

    // 根据输入框中的值来过滤数据并更新ListView
    private void search(String searchName) {
        List<UserInfo> filterDateList = new ArrayList<>();
        if (TextUtils.isEmpty(searchName)) {
            filterDateList = userList;
            tipSearchNull.setVisibility(View.GONE);
        } else {
            filterDateList.clear();
            for (UserInfo sortModel : userList) {
                String name = sortModel.getName();
                if (name.contains(searchName) || characterParser.getSelling(name).startsWith(searchName)) {
                    filterDateList.add(sortModel);
                }
            }
        }
        if (filterDateList.size() == 0) {
            tipSearchNull.setVisibility(View.VISIBLE);
            tipSearchNull.setTipView(TipView.TipStatus.NO_DATA, "没有找到该好友哟\n换个好友再试一次吧");
            listView.setVisibility(View.GONE);
        } else {
            tipSearchNull.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            Collections.sort(filterDateList, pinyinComparator);// 根据 a - z 进行排序
            adapter.ChangeDate(filterDateList);
            userList.clear();
            userList.addAll(filterDateList);
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = adapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            listView.setSelection(position);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean isFriend = false;
 /*       ToastUtils.show_always(context,""+position);*/
        if (userList.get(position-1).getUserId().equals(CommonUtils.getUserId(context))) {
            ToastUtils.show_always(context, "点击的是本人");
        } else {
            if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() != 0) {
                for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                    if (userList.get(position-1).getUserId().equals(GlobalConfig.list_person.get(i).getUserId())) {
                        isFriend = true;
                        break;
                    }
                }
            } else {
                isFriend = false;// 不是我的好友
            }
            if (isFriend) {
                UserInfo tp = new UserInfo();
                tp.setPortrait(userList.get(position-1).getPortrait());
                tp.setNickName(userList.get(position-1).getNickName());
                tp.setUserId(userList.get(position-1).getUserId());
                tp.setUserAliasName(userList.get(position-1).getUserAliasName());
                Intent intent = new Intent(context, TalkPersonNewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "GroupMemers");
                bundle.putString("id", groupId);
                bundle.putSerializable("data", tp);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, GroupPersonNewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "GroupMemers");
                bundle.putString("id", groupId);
                bundle.putSerializable("data", userList.get(position-1));
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String searchName = s.toString();
        if (searchName.trim().equals("")) {
            imageClear.setVisibility(View.INVISIBLE);
            tipSearchNull.setVisibility(View.GONE);
            if (srcList == null || srcList.size() == 0) {
                listView.setVisibility(View.GONE);
            } else {
                listView.setVisibility(View.VISIBLE);
                userList.clear();
                userList.addAll(srcList);
                filledData(userList);
                Collections.sort(userList, pinyinComparator);
                adapter = new CreateGroupMembersAdapter(context, userList);
                listView.setAdapter(adapter);
            }
        } else {
            userList.clear();
            userList.addAll(srcList);
            imageClear.setVisibility(View.VISIBLE);
            search(searchName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        adapter = null;
        sideBar = null;
        dialogs = null;
        listView = null;
        textHeadName = null;
        editSearchContent = null;
        imageClear = null;
        pinyinComparator = null;
        characterParser = null;
        setContentView(R.layout.activity_null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
