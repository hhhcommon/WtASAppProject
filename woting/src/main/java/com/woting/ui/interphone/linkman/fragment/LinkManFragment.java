package com.woting.ui.interphone.linkman.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.HeightListView;
import com.woting.common.widgetui.TipView;
import com.woting.ui.common.login.LoginActivity;
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.common.model.UserInfo;
import com.woting.ui.interphone.alert.CallAlertActivity;
import com.woting.ui.interphone.chat.fragment.ChatFragment;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
import com.woting.ui.interphone.group.groupcontrol.groupnews.TalkGroupNewsActivity;
import com.woting.ui.interphone.group.groupcontrol.personnews.TalkPersonNewsActivity;
import com.woting.ui.interphone.linkman.adapter.SortGroupMemberAdapter;
import com.woting.ui.interphone.linkman.adapter.SortGroupMemberAdapter.OnListeners;
import com.woting.ui.interphone.linkman.adapter.TalkGroupAdapter;
import com.woting.ui.interphone.linkman.adapter.TalkGroupAdapter.OnListener;
import com.woting.ui.interphone.linkman.adapter.TalkPersonNoAdapter;
import com.woting.ui.interphone.linkman.model.LinkMan;
import com.woting.ui.interphone.linkman.view.CharacterParser;
import com.woting.ui.interphone.linkman.view.PinyinComparator;
import com.woting.ui.interphone.linkman.view.SideBar;
import com.woting.ui.interphone.linkman.view.SideBar.OnTouchingLetterChangedListener;
import com.woting.ui.interphone.main.DuiJiangActivity;
import com.woting.ui.interphone.message.activity.NewsActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 最新联系人排序
 * @author 辛龙
 * 2016年5月12日
 */
public class LinkManFragment extends Fragment implements SectionIndexer, OnClickListener, TipView.TipViewClick {
    private SortGroupMemberAdapter adapter;
    private TalkGroupAdapter adapter_group;
    private CharacterParser characterParser;// 汉字转换成拼音的类
    private PinyinComparator pinyinComparator;
    private FragmentActivity context;
    private MessageReceiver Receiver;
    private SharedPreferences sharedPreferences;
    private boolean isCancelRequest;
    private boolean firstEntry = true;
    private Dialog confirmDialog;
    private Dialog dialogs;
    private int type = 1;// 1.个人  2.组
    private GroupInfo group;
    private List<GroupInfo> groupList = new ArrayList<>();
    private List<GroupInfo> srclist_g;
    private List<UserInfo> srclist_p;
    private List<UserInfo> list;
    private String isLogin;
    private String id;
    private String tag = "FRIENDS_VOLLEY_REQUEST_CANCEL_TAG";
    private View rootView;
    private View headView;
    private SideBar sideBar;
    private EditText et_search;
    private ImageView image_clear;
    private ListView listView_group;
    private ListView sortListView;
    private TextView tvDialog;
    private TextView tv_newpersons;
    private LinearLayout lin_grouplist;
    private LinearLayout lin_news_message;
    private LinearLayout relative;

    private TipView tipView;
    private TipView headViewNoFriendTip;// 没有好友以及没有加入群组提示
    private TipView tipSearchNull;// 搜索没有结果提示

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        characterParser = CharacterParser.getInstance();// 实例化汉字转拼音类
        pinyinComparator = new PinyinComparator();
        if (Receiver == null) {// 注册广播接收socketService的数据
            Receiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH_REFRESH_LINKMAN);
            filter.addAction(BroadcastConstants.PUSH_NEWPERSON);
            filter.addAction(BroadcastConstants.PUSH_ALLURL_CHANGE);
            context.registerReceiver(Receiver, filter);
        }
        Dialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_add_friends, container, false);
            initViews();// 设置界面
            setEditListener();
        }
        return rootView;
    }

    /**
     * 1.判断是否登录
     * 2.登录了若 personRefresh 为 "true",则刷新数据，否则不处理
     * 3.若未登录，则隐藏 listView 界面，展示咖啡的界面
     */
    @Override
    public void onResume() {
        super.onResume();
        setOnResumeView();
    }

    private void setOnResumeView() {
        sharedPreferences = BSApplication.SharedPreferences;
        isLogin = sharedPreferences.getString(StringConstant.ISLOGIN, "false");
        if (isLogin.equals("true")) {
            tipView.setVisibility(View.GONE);
            relative.setVisibility(View.VISIBLE);
            if (firstEntry) {
                send();
                firstEntry = false;
            }
        } else {
            firstEntry = false;
            tipView.setTipView(TipView.TipStatus.NO_LOGIN);
            tipView.setVisibility(View.VISIBLE);
            relative.setVisibility(View.GONE);
        }
    }

    // 初始化视图
    private void initViews() {
        tipSearchNull = (TipView) rootView.findViewById(R.id.tip_search_null);

        relative = (LinearLayout) rootView.findViewById(R.id.relative);
        sideBar = (SideBar) rootView.findViewById(R.id.sidrbar);
        tvDialog = (TextView) rootView.findViewById(R.id.dialog);
        sideBar.setTextView(tvDialog);
        headView = LayoutInflater.from(context).inflate(R.layout.head_talk_person, null);// 头部 view
        lin_news_message = (LinearLayout) headView.findViewById(R.id.news_message);
        lin_grouplist = (LinearLayout) headView.findViewById(R.id.lin_grouplist);
        tv_newpersons = (TextView) headView.findViewById(R.id.tv_newpersons);
        listView_group = (ListView) headView.findViewById(R.id.listView_group);
        headViewNoFriendTip = (TipView) headView.findViewById(R.id.tip_view);

        sortListView = (ListView) rootView.findViewById(R.id.country_lvcountry);
        et_search = (EditText) rootView.findViewById(R.id.et_search);
        image_clear = (ImageView) rootView.findViewById(R.id.image_clear);
        sortListView.addHeaderView(headView);// 添加头部 view
        lin_news_message.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsActivity.class);
                startActivity(intent);
            }
        });

        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setTipClick(this);
    }

    // 为 ListView 填充数据
    private void filledData(List<UserInfo> person) {
        for (int i = 0; i < person.size(); i++) {
            person.get(i).setName(person.get(i).getUserName());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(person.get(i).getUserName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                person.get(i).setSortLetters(sortString.toUpperCase());
            } else {
                person.get(i).setSortLetters("#");
            }
        }
    }

    // 根据输入框中的值来过滤数据并更新 ListView
    private List<UserInfo> filterData(String filterStr) {
        List<UserInfo> filterDateList = new ArrayList<>();
        filterDateList.clear();
        for (UserInfo sortModel : srclist_p) {
            String name = sortModel.getName();
            if (name.contains(filterStr) || characterParser.getSelling(name).startsWith(filterStr)) {
                filterDateList.add(sortModel);
            }
        }
        // 根据 a - z 进行排序
        Collections.sort(filterDateList, pinyinComparator);
        return filterDateList;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    // 根据 ListView 的当前位置获取分类的首字母的 Char ascii 值
    public int getSectionForPosition(int position) {
        return srclist_p.get(position).getSortLetters().charAt(0);
    }

    // 根据分类的首字母的 Char ascii 值获取其第一次出现该首字母的位置
    public int getPositionForSection(int section) {
        for (int i = 0; i < srclist_p.size(); i++) {
            String sortStr = srclist_p.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onTipViewClick() {
        if (isLogin.equals("true")) {
            send();
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    class MessageReceiver extends BroadcastReceiver {
        private String message;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.PUSH_REFRESH_LINKMAN)) {
                send();
//				ToastUtils.show_always(context, "重新获取了新数据");
            } else if (action.equals(BroadcastConstants.PUSH_NEWPERSON)) {
                String messages = intent.getStringExtra("outMessage");
                if (messages != null && !messages.equals("")) {
                    message = messages;
                } else {
                    message = "新的朋友";
                }
                tv_newpersons.setText(message);
            } else if (action.equals(BroadcastConstants.PUSH_ALLURL_CHANGE)) {
                setOnResumeView();
            }
        }
    }

    private void Dialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_talk_person_del, null);
        TextView tv_cancel = (TextView) dialog1.findViewById(R.id.tv_cancle);
        TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(true);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });

        tv_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
                    ChatFragment.isCallingForGroup = false;
                    ChatFragment.isCallingForUser = false;
                    ChatFragment.lin_notalk.setVisibility(View.VISIBLE);
                    ChatFragment.lin_personhead.setVisibility(View.GONE);
                    ChatFragment.lin_head.setVisibility(View.GONE);
                    ChatFragment.lin_foot.setVisibility(View.GONE);
                    call(id);
                    confirmDialog.dismiss();
                } else {
                    InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
                    ChatFragment.isCallingForGroup = false;
                    ChatFragment.isCallingForUser = false;
                    ChatFragment.lin_notalk.setVisibility(View.VISIBLE);
                    ChatFragment.lin_personhead.setVisibility(View.GONE);
                    ChatFragment.lin_head.setVisibility(View.GONE);
                    ChatFragment.lin_foot.setVisibility(View.GONE);
                    ChatFragment.zhiDingGroup(group);
                    // 对讲主页界面更新
                    DuiJiangActivity.update();
                    confirmDialog.dismiss();
                }
            }
        });
    }

    // 对讲呼叫
    protected void call(String id) {
        Intent it = new Intent(context, CallAlertActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        it.putExtras(bundle);
        startActivity(it);
    }

    public void send() {
        // 第一次获取群成员跟组
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            if (!isVisible()) {
                dialogs = DialogUtils.Dialogph(context, "正在获取数据");
            }
            JSONObject jsonObject = VolleyRequest.getJsonObject(context);

            VolleyRequest.RequestPost(GlobalConfig.gettalkpersonsurl, tag, jsonObject, new VolleyCallback() {
                @Override
                protected void requestSuccess(JSONObject result) {
                    if (dialogs != null) dialogs.dismiss();
                    if (isCancelRequest) return;
                    try {
                        LinkMan list = new Gson().fromJson(result.toString(), new TypeToken<LinkMan>() {}.getType());
                        try {
                            GlobalConfig.list_group = srclist_g = list.getGroupList().getGroups();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        try {
                            GlobalConfig.list_person = srclist_p = list.getFriendList().getFriends();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (list == null) {
                            relative.setVisibility(View.GONE);
                        } else {
                            relative.setVisibility(View.VISIBLE);
                            if (srclist_g != null && srclist_g.size() != 0) {
                                groupList.clear();
                                groupList.addAll(srclist_g);
                                if (adapter_group == null) {
                                    adapter_group = new TalkGroupAdapter(context, groupList);
                                    listView_group.setAdapter(adapter_group);
                                    new HeightListView(context).setListViewHeightBasedOnChildren(listView_group);
                                } else {
                                    adapter_group.notifyDataSetChanged();
                                    new HeightListView(context).setListViewHeightBasedOnChildren(listView_group);
                                }
                                setGroupListViewListener();
                                lin_grouplist.setVisibility(View.VISIBLE);
                            } else {
                                lin_grouplist.setVisibility(View.GONE);
                            }
                            if (srclist_p == null || srclist_p.size() == 0) {
                                TalkPersonNoAdapter adapters = new TalkPersonNoAdapter(context);
                                sortListView.setAdapter(adapters);
                            } else {
                                // 根据 a - z 进行排序源数据
                                filledData(srclist_p);
                                Collections.sort(srclist_p, pinyinComparator);
                                adapter = new SortGroupMemberAdapter(context, srclist_p);
                                sortListView.setAdapter(adapter);
                            }
                            if((srclist_g == null || srclist_g.size() <= 0) && (srclist_p == null || srclist_p.size() <= 0)) {
                                headViewNoFriendTip.setVisibility(View.VISIBLE);
                                headViewNoFriendTip.setTipView(TipView.TipStatus.NO_DATA, "您还没有聊天对象哟\n快去找好友们聊天吧");
                            } else {
                                headViewNoFriendTip.setVisibility(View.GONE);
                            }
                            setListViewListener();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void requestError(VolleyError error) {
                    if (dialogs != null) dialogs.dismiss();
                    sortListView.setAdapter(new TalkPersonNoAdapter(context));
                    headViewNoFriendTip.setVisibility(View.VISIBLE);
                    headViewNoFriendTip.setTipView(TipView.TipStatus.IS_ERROR);
                }
            });
        } else {
            sortListView.setAdapter(new TalkPersonNoAdapter(context));
            headViewNoFriendTip.setVisibility(View.VISIBLE);
            headViewNoFriendTip.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_clear:
                image_clear.setVisibility(View.INVISIBLE);
                et_search.setText("");
                break;
        }
    }

    // 根据输入框输入值的改变来过滤搜索
    private void setEditListener() {
        image_clear.setOnClickListener(this);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search_name = s.toString();
                if (search_name.trim().equals("")) {
                    image_clear.setVisibility(View.INVISIBLE);
                    tipSearchNull.setVisibility(View.GONE);// 关键词为空
                    sortListView.setVisibility(View.VISIBLE);
                    if (srclist_g != null && srclist_g.size() != 0) {
                        groupList.clear();
                        groupList.addAll(srclist_g);
                        if (adapter_group == null) {
                            adapter_group = new TalkGroupAdapter(context, groupList);
                            listView_group.setAdapter(adapter_group);
                        } else {
                            adapter_group.ChangeDate(groupList);
                        }
                        new HeightListView(context).setListViewHeightBasedOnChildren(listView_group);
                        setGroupListViewListener();
                        lin_grouplist.setVisibility(View.VISIBLE);
                    } else {
                        lin_grouplist.setVisibility(View.GONE);
                    }
                    if (srclist_p == null || srclist_p.size() == 0) {
                        TalkPersonNoAdapter adapters = new TalkPersonNoAdapter(context);
                        sortListView.setAdapter(adapters);
                    } else {
                        adapter = new SortGroupMemberAdapter(context, srclist_p);
                        sortListView.setAdapter(adapter);
                    }
                } else {// 关键词不为空
                    image_clear.setVisibility(View.VISIBLE);
                    groupList.clear();
                    search(search_name);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void search(final String search_name) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int iiii = msg.what;
                switch (iiii) {
                    case 0:            // 此时没有数据
                        tipSearchNull.setVisibility(View.VISIBLE);
                        tipSearchNull.setTipView(TipView.TipStatus.NO_DATA, "没有找到该好友或群组哟\n换个好友或群组再试一次吧");
                        sortListView.setVisibility(View.GONE);
                        break;
                    case 1:            // 此时个人有数据
                        tipSearchNull.setVisibility(View.GONE);
                        sortListView.setVisibility(View.VISIBLE);
                        lin_grouplist.setVisibility(View.GONE);
                        if (adapter == null) {
                            adapter = new SortGroupMemberAdapter(context, list);
                            sortListView.setAdapter(adapter);
                        } else {
                            adapter.updateListView(list);
                        }
                        break;
                    case 2:            // 此时群组有数据
                        tipSearchNull.setVisibility(View.GONE);
                        sortListView.setVisibility(View.VISIBLE);
                        if (adapter_group == null) {
                            adapter_group = new TalkGroupAdapter(context, groupList);
                            listView_group.setAdapter(adapter_group);
                        } else {
                            adapter_group.notifyDataSetChanged();
                        }
                        new HeightListView(context).setListViewHeightBasedOnChildren(listView_group);
                        TalkPersonNoAdapter adapters = new TalkPersonNoAdapter(context);
                        sortListView.setAdapter(adapters);
                        break;
                    case 3:            // 此时群组、个人都有数据
                        tipSearchNull.setVisibility(View.GONE);
                        sortListView.setVisibility(View.VISIBLE);
                        if (adapter_group == null) {
                            adapter_group = new TalkGroupAdapter(context, groupList);
                            listView_group.setAdapter(adapter_group);
                        } else {
                            adapter_group.notifyDataSetChanged();
                        }
                        new HeightListView(context).setListViewHeightBasedOnChildren(listView_group);
                        adapter = new SortGroupMemberAdapter(context, list);
                        sortListView.setAdapter(adapter);
                        break;
                }
            }
        };

        new Thread() {
            @Override
            public void run() {
                super.run();
                if (srclist_g == null || srclist_g.size() == 0) {
                    // 此时没有群组数据
                    if (srclist_p == null || srclist_p.size() == 0) {
                        // 此时没有好友》》》没有搜索数据
                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    } else {
                        // 此时有好友》》》有搜索数据
                        list = filterData(search_name);
                        if (list.size() == 0) {
                            // 此时没有数据
                            Message msg = new Message();
                            msg.what = 0;
                            handler.sendMessage(msg);
                        } else {
                            // 此时个人有数据
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }
                    }
                } else {
                    // 此时有群组数据
                    for (int i = 0; i < srclist_g.size(); i++) {
                        if (srclist_g.get(i).getGroupName().contains(search_name)) {
                            groupList.add(srclist_g.get(i));
                        }
                    }
                    if (groupList.size() == 0) {
                        // 群组没有匹配数据
                        if (srclist_p == null || srclist_p.size() == 0) {
                            // 此时没有好友数据
                            Message msg = new Message();
                            msg.what = 0;
                            handler.sendMessage(msg);
                        } else {
                            // 此时有好友数据
                            list = filterData(search_name);
                            if (list.size() == 0) {
                                // 此时没有数据
                                Message msg = new Message();
                                msg.what = 0;
                                handler.sendMessage(msg);
                            } else {
                                // 此时个人有数据
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                        }
                    } else {
                        // 此时群组有数据
                        if (srclist_p == null || srclist_p.size() == 0) {
                            // 此时群组有数据
                            Message msg = new Message();
                            msg.what = 2;
                            handler.sendMessage(msg);
                        } else {
                            list = filterData(search_name);
                            if (list.size() == 0) {
                                // 此时群组有数据
                                Message msg = new Message();
                                msg.what = 2;
                                handler.sendMessage(msg);
                            } else {
                                // 此时群组。个人都有数据
                                Message msg = new Message();
                                msg.what = 3;
                                handler.sendMessage(msg);
                            }
                        }
                    }
                }
            }
        }.start();
    }

    // listView 的监听
    private void setListViewListener() {
        adapter.setOnListeners(new OnListeners() {
            @Override
            public void add(int position) {
                id = ((UserInfo) adapter.getItem(position)).getUserId();
                // 此时的对讲状态
                if ((ChatFragment.isCallingForGroup||ChatFragment.isCallingForUser)) {
                    if (ChatFragment.interPhoneType.equals("user")) {
                        type = 1;
                        confirmDialog.show();
                    } else {
                        call(id);
                    }
                } else {
                    call(id);
                }
            }
        });

        sortListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 跳转到详细信息界面
                Intent intent = new Intent(context, TalkPersonNewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "talkpersonfragment");
                bundle.putSerializable("data", srclist_p.get(position - 1));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });
    }

    // 组 listView 监听
    private void setGroupListViewListener() {
        adapter_group.setOnListener(new OnListener() {
            @Override
            public void add(int position) {
                group = groupList.get(position);
                Log.e("组名称", group.getGroupName());
                if ((ChatFragment.isCallingForGroup||ChatFragment.isCallingForUser)) {
                    if (ChatFragment.interPhoneType.equals("user")) {
                        type = 2;
                        confirmDialog.show();
                    } else {
                        // 这是 zhidinggroups，不是 zhidinggroup；
                        ChatFragment.zhiDingGroupS(group);
                        // 对讲主页界面更新
                        DuiJiangActivity.update();
                    }
                } else {
                    ChatFragment.zhiDingGroup(group);
                    // 对讲主页界面更新
                    DuiJiangActivity.update();
                }
            }
        });

        listView_group.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 跳转到群组详情页面
                Intent intent = new Intent(context, TalkGroupNewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "talkpersonfragment");
                bundle.putSerializable("data", groupList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        if (Receiver != null) {
            context.unregisterReceiver(Receiver);
            Receiver = null;
        }
        sortListView = null;
        sideBar = null;
        adapter = null;
        characterParser = null;
        pinyinComparator = null;
        context = null;
        sharedPreferences = null;
        isLogin = null;
        confirmDialog = null;
        dialogs = null;
        group = null;
        adapter_group = null;
        groupList = null;
        srclist_g = null;
        srclist_p = null;
        id = null;
        et_search = null;
        image_clear = null;
        rootView = null;
        headView = null;
        listView_group = null;
        relative = null;
        list = null;
        isLogin = null;
        tag = null;
    }
}
