package com.woting.ui.interphone.group.groupcontrol.membershow;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.common.model.UserInfo;
import com.woting.ui.interphone.group.groupcontrol.grouppersonnews.GroupPersonNewsActivity;
import com.woting.ui.interphone.group.groupcontrol.membershow.adapter.CreateGroupMembersAdapter;
import com.woting.ui.interphone.group.groupcontrol.personnews.TalkPersonNewsActivity;
import com.woting.ui.interphone.linkman.view.CharacterParser;
import com.woting.ui.interphone.linkman.view.PinyinComparator;
import com.woting.ui.interphone.linkman.view.SideBar;
import com.woting.ui.interphone.linkman.view.SideBar.OnTouchingLetterChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 展示全部群成员
 * @author 辛龙
 * 2016年4月13日
 */
public class GroupMembersActivity extends AppBaseActivity implements
        OnClickListener, TextWatcher, OnItemClickListener, OnTouchingLetterChangedListener {

    private CharacterParser characterParser = CharacterParser.getInstance();// 实例化汉字转拼音类
    private PinyinComparator pinyinComparator = new PinyinComparator();
    private CreateGroupMembersAdapter adapter;
    private SideBar sideBar;
    private List<UserInfo> srcList;
    private List<UserInfo> userList = new ArrayList<>();

    private Dialog dialog;
    private TextView textNoFriend;
    private TextView dialogs;
    private TextView textHeadName;
    private ListView listView;
    private EditText editSearchContent;
    private ImageView imageClear;

    private String groupId;
    private String tag = "GROUP_MEMBERS_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmembers);

        initView();
    }

    private void initView() {
        groupId = getIntent().getStringExtra("GroupId");
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

        textNoFriend = (TextView) findViewById(R.id.title_layout_no_friends);// 搜索没有结果显示
        textHeadName = (TextView) findViewById(R.id.head_name_tv);// 更新当前组员人数的控件

        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取群成员信息");
            send();
        } else {
            ToastUtils.show_always(context, "网络失败，请检查网络");
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
                textNoFriend.setVisibility(View.GONE);
                break;
        }
    }

    // 网络请求主函数
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
                    Log.v("ReturnType", "ReturnType > > " + ReturnType + " == Message > > " + Message);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if(ReturnType == null || ReturnType.equals("")) {
                    return ;
                }
                if (ReturnType.equals("1001") || ReturnType.equals("1002")) {
                    try {
                        srcList = new Gson().fromJson(result.getString("UserList"), new TypeToken<List<UserInfo>>() {}.getType());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (srcList != null && srcList.size() != 0) {
                        int sum = srcList.size();
                        textHeadName.setText("全部成员(" + sum + ")");
                        userList.clear();
                        userList.addAll(srcList);
                        filledData(userList);
                        Collections.sort(userList, pinyinComparator);
                        listView.setAdapter(adapter = new CreateGroupMembersAdapter(context, userList));
                    }
                } else if (ReturnType != null && ReturnType.equals("T")) {
                    ToastUtils.show_always(context, "异常返回值");
                } else if (ReturnType != null && ReturnType.equals("1011")) {
                    ToastUtils.show_always(context, "组中无成员");
                } else {
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_always(context, Message + "");
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

    // 根据输入框中的值来过滤数据并更新ListView
    private void search(String searchName) {
        List<UserInfo> filterDateList = new ArrayList<>();
        if (TextUtils.isEmpty(searchName)) {
            filterDateList = userList;
            textNoFriend.setVisibility(View.GONE);
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
            textNoFriend.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            textNoFriend.setVisibility(View.GONE);
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
        if (userList.get(position).getUserId().equals(CommonUtils.getUserId(context))) {
            ToastUtils.show_always(context, "点击的是本人");
        } else {
            if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() != 0) {
                for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                    if (userList.get(position).getUserId().equals(GlobalConfig.list_person.get(i).getUserId())) {
                        isFriend = true;
                        break;
                    }
                }
            } else {
                isFriend = false;// 不是我的好友
            }
            if (isFriend) {
                UserInfo tp = new UserInfo();
                tp.setPortraitBig(userList.get(position).getPortraitBig());
                tp.setPortraitMini(userList.get(position).getPortraitMini());
                tp.setUserName(userList.get(position).getUserName());
                tp.setUserId(userList.get(position).getUserId());
                tp.setUserAliasName(userList.get(position).getUserAliasName());
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
                bundle.putSerializable("data", userList.get(position));
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
            textNoFriend.setVisibility(View.GONE);
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
        textNoFriend = null;
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
