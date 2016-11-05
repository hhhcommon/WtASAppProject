package com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel.adapter.CreateGroupMembersDelAdapter;
import com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel.adapter.CreateGroupMembersDelAdapter.friendCheck;
import com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel.model.UserInfo;
import com.woting.activity.interphone.linkman.view.CharacterParser;
import com.woting.activity.interphone.linkman.view.PinyinComparator_b;
import com.woting.activity.interphone.linkman.view.SideBar;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 管理员删除群成员界面
 * @author 辛龙
 * 2016年3月25日
 */
public class GroupMemberDelActivity extends BaseActivity implements OnClickListener, TextWatcher {
    private CharacterParser characterParser = CharacterParser.getInstance();// 实例化汉字转拼音类
    private PinyinComparator_b pinyinComparator = new PinyinComparator_b();
    private CreateGroupMembersDelAdapter adapter;
    private List<UserInfo> userList;
    private List<UserInfo> userList2 = new ArrayList<>();
    private List<String> delList = new ArrayList<>();
    private SideBar sideBar;
    
    private Dialog dialog;
    private ListView listView;
    private EditText editSearchContent;
    private TextView textHeadName;
    private TextView textNoFriend;
    private TextView textHeadRight;
    private TextView dialogs;
    private ImageView imageClear;

    private String groupId;
    private String tag = "GROUP_MEMBER_DEL_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmembers_add);

        initView();
    }

    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        findViewById(R.id.head_right_btn).setOnClickListener(this);// 添加按钮

        editSearchContent = (EditText) findViewById(R.id.et_search);// 搜索控件
        editSearchContent.addTextChangedListener(this);

        imageClear = (ImageView) findViewById(R.id.image_clear);
        imageClear.setOnClickListener(this);

        dialogs = (TextView) findViewById(R.id.dialog);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        sideBar.setTextView(dialogs);

        listView = (ListView) findViewById(R.id.country_lvcountry);
        textNoFriend = (TextView) findViewById(R.id.title_layout_no_friends);
        textHeadRight = (TextView) findViewById(R.id.tv_head);
        textHeadName = (TextView) findViewById(R.id.head_name_tv);
        textHeadName.setText("删除群成员");

        groupId = getIntent().getStringExtra("GroupId");
        if (groupId != null && !groupId.equals("")) {
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialogph(context, "正在获取群成员信息", dialog);
                send();
            } else {
                ToastUtils.show_allways(context, "网络失败，请检查网络");
            }
        } else {
            ToastUtils.show_allways(context, "获取数据异常请返回重试!");
        }
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.grouptalkUrl, tag, jsonObject, new VolleyCallback() {
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
                    try {
                        userList = new Gson().fromJson(result.getString("UserList"), new TypeToken<List<UserInfo>>() {}.getType());
                        if(userList == null || userList.size() == 0) {
                            ToastUtils.show_allways(context, "当前组内已经没有其他联系人了");
                            return ;
                        }
                        String userId = CommonUtils.getUserId(context);// 从返回的 list 当中去掉用户自己
                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i).getUserId().equals(userId)) {
                                userList.remove(i);
                            }
                        }
                        userList2.clear();
                        userList2.addAll(userList);
                        filledData(userList2);
                        Collections.sort(userList2, pinyinComparator);
                        listView.setAdapter(adapter = new CreateGroupMembersDelAdapter(context, userList2));
                        setInterface();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_allways(context, "无法获取组Id");
                } else if (ReturnType != null && ReturnType.equals("T")) {
                    ToastUtils.show_allways(context, "异常返回值");
                } else if (ReturnType != null && ReturnType.equals("1011")) {
                    ToastUtils.show_allways(context, "组中无成员");
                } else {
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_allways(context, Message + "");
                    }
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
            person.get(i).setName(person.get(i).getUserName());
            String pinyin = characterParser.getSelling(person.get(i).getUserName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {// 判断首字母是否是英文字母
                person.get(i).setSortLetters(sortString.toUpperCase());
            } else {
                person.get(i).setSortLetters("#");
            }
        }
    }

    protected void setInterface() {
        adapter.setOnListener(new friendCheck() {
            @Override
            public void checkposition(int position) {
                if (userList2.get(position).getCheckType() == 1) {
                    userList2.get(position).setCheckType(2);
                } else {
                    userList2.get(position).setCheckType(1);
                }
                adapter.notifyDataSetChanged();
                int num = 0;
                for (int i = 0; i < userList2.size(); i++) {
                    if (userList2.get(i).getCheckType() == 2) {
                        num++;
                    }
                }
                textHeadRight.setText("确定(" + String.valueOf(num) + ")");
            }
        });
    }

    private void search(String search_name) {
        List<UserInfo> filterDateList = new ArrayList<>();
        if (TextUtils.isEmpty(search_name)) {
            filterDateList = userList2;
            textNoFriend.setVisibility(View.GONE);
        } else {
            filterDateList.clear();
            for (UserInfo sortModel : userList2) {
                String name = sortModel.getName();
                if (name.contains(search_name)|| characterParser.getSelling(name).startsWith(search_name)) {
                    filterDateList.add(sortModel);
                }
            }
        }
        Collections.sort(filterDateList, pinyinComparator);// 根据 a - z 进行排序
        adapter.ChangeDate(filterDateList);
        userList2.clear();
        userList2.addAll(filterDateList);
        if (filterDateList.size() == 0) {
            textNoFriend.setVisibility(View.VISIBLE);
        } else {
            textNoFriend.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.head_right_btn:
                if (userList2 != null && userList2.size() > 0) {
                    for (int i = 0; i < userList2.size(); i++) {
                        if (userList2.get(i).getCheckType() == 2) {
                            delList.add(userList2.get(i).getUserId());
                        }
                    }
                }
                if (delList != null && delList.size() > 0) {
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialogph(context, "正在发送邀请", dialog);
                        sendMemberDelete();
                    } else {
                        ToastUtils.show_allways(context, "网络失败，请检查网络");
                    }
                } else {
                    ToastUtils.show_allways(context, "请您勾选您要删除的成员");
                }
                break;
            case R.id.image_clear:
                imageClear.setVisibility(View.INVISIBLE);
                editSearchContent.setText("");
                textNoFriend.setVisibility(View.GONE);
                break;
        }
    }

    private void sendMemberDelete() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            String s = delList.toString().replaceAll(" ", "");
            jsonObject.put("UserIds", s.substring(1, s.length() - 1));
            jsonObject.put("GroupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.KickOutMembersUrl, tag, jsonObject, new VolleyCallback() {
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
                    ToastUtils.show_allways(context, "群成员已经成功删除");
                    setResult(1);
                    finish();
                } else {
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_allways(context, Message + "");
                    }
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
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String search_name = s.toString();
        if (search_name.trim().equals("")) {
            imageClear.setVisibility(View.INVISIBLE);
            textNoFriend.setVisibility(View.GONE);
            if (userList == null || userList.size() == 0) {
                listView.setVisibility(View.GONE);
            } else {
                listView.setVisibility(View.VISIBLE);
                userList2.clear();
                userList2.addAll(userList);
                filledData(userList2);
                Collections.sort(userList2, pinyinComparator);
                listView.setAdapter(adapter = new CreateGroupMembersDelAdapter(context, userList2));
                setInterface();
            }
        } else {
            userList2.clear();
            userList2.addAll(userList);
            imageClear.setVisibility(View.VISIBLE);
            search(search_name);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        textNoFriend = null;
        sideBar = null;
        dialogs = null;
        listView = null;
        editSearchContent = null;
        imageClear = null;
        textHeadRight = null;
        textHeadName = null;
        pinyinComparator = null;
        userList2.clear();
        userList2 = null;
        userList = null;
        adapter = null;
        pinyinComparator = null;
        setContentView(R.layout.activity_null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
