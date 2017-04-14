package com.woting.ui.interphone.chat.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.manager.MyActivityManager;
import com.woting.common.service.SubclassService;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.JsonEncloseUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.util.VibratorUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.MyGridView;
import com.woting.common.widgetui.MyLinearLayout;
import com.woting.common.widgetui.TipView;
import com.woting.ui.common.login.LoginActivity;
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.common.model.UserInfo;
import com.woting.ui.interphone.alert.CallAlertActivity;
import com.woting.ui.interphone.chat.adapter.ChatListAdapter;
import com.woting.ui.interphone.chat.adapter.ChatListAdapter.OnListener;
import com.woting.ui.interphone.chat.adapter.GroupPersonAdapter;
import com.woting.ui.interphone.chat.dao.SearchTalkHistoryDao;
import com.woting.ui.interphone.chat.model.DBTalkHistorary;
import com.woting.ui.interphone.commom.message.MessageUtils;
import com.woting.ui.interphone.commom.message.MsgNormal;
import com.woting.ui.interphone.commom.message.content.MapContent;
import com.woting.ui.interphone.commom.model.ListInfo;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
import com.woting.ui.interphone.commom.service.VoiceStreamRecordService;
import com.woting.ui.interphone.group.groupcontrol.groupnews.TalkGroupNewsActivity;
import com.woting.ui.interphone.group.groupcontrol.grouppersonnews.GroupPersonNewsActivity;
import com.woting.ui.interphone.group.groupcontrol.personnews.TalkPersonNewsActivity;
import com.woting.ui.interphone.linkman.model.LinkMan;
import com.woting.ui.interphone.main.DuiJiangActivity;
import com.woting.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 对讲机-获取联系列表，包括群组跟个人
 *
 * @author 辛龙
 *         2016年1月18日
 */
public class ChatFragment extends Fragment implements TipView.TipViewClick {
    public static FragmentActivity context;
    private static ChatListAdapter adapter;
    private MessageReceiver Receiver;
    private static SearchTalkHistoryDao dbDao;
    private static Gson gson = new Gson();
    private SharedPreferences shared = BSApplication.SharedPreferences;

    private static ListView mListView;
    private static ImageView image_persontx, image_grouptx, imageView_answer;

    private static TextView tv_groupname, tv_num, tv_grouptype, tv_allnum, tv_personname;
    private TextView talkingName;
    private TextView talking_news;
    private static TextView gridView_tv;
    private ImageView image_personvoice, image_group_persontx, image_voice;

    private Button image_button;
    private View rootView;
    private static MyGridView gridView_person;
    private Dialog dialog;
    private static Dialog confirmDialog;

    private RelativeLayout Relative_listview;
    public static MyLinearLayout lin_foot;
    public static LinearLayout lin_head, lin_notalk, lin_personhead;
    public static TipView tipView;

    private AnimationDrawable draw, draw_group;
    private String UserName;
    private static String groupId;
    public static String interPhoneType = "";
    public static String interPhoneId;
    private static String phoneId;
    private String tag = "TALKOLDLIST_VOLLEY_REQUEST_CANCEL_TAG";
    private long Vibrate = 100;
    private static int enterGroupType;
    private static int dialogType;
    public static boolean isCallingForGroup = false;//是否是在通话状态;
    public static boolean isCallingForUser = false;//是否是在通话状态;

    private boolean isCancelRequest;
    private boolean isTalking = false;
    private static List<UserInfo> groupPersonList = new ArrayList<>();//组成员
    private static ArrayList<UserInfo> groupPersonListS = new ArrayList<>();
    private static ArrayList<GroupInfo> allList = new ArrayList<>();//所有数据库数据
    private static List<DBTalkHistorary> historyDataBaseList;//list里边的数据
    private static List<ListInfo> listInfo;

    private String oldTalkId;
    private Handler handler;
    private Runnable run;
    private ImageView imageview;
    private TextView tv_name;
    private LinearLayout lin_call;
    private LinearLayout lin_guaduan;
    private LinearLayout lin_two_call;
    private String callId;
    private String callerId;
    private LinearLayout lin_press;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        handler = new Handler();
        initDao();      // 初始化数据库
        setReceiver();  // 注册广播接收socketService的数据
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_talkoldlist, container, false);
        setView();//设置界面
        setOnResumeView();
        return rootView;
    }

    @Override
    public void onTipViewClick() {
        // 没有数据时候的操作监听
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener();
        Dialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        setOnResumeView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    getGridViewPerson(interPhoneId);//获取群成员
                }
                break;
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
    }

    // 注册广播接收socketService的数据
    private void setReceiver() {
        if (Receiver == null) {
            Receiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH);
            filter.addAction(BroadcastConstants.PUSH_NOTIFY);
            filter.addAction(BroadcastConstants.UP_DATA_GROUP);
            filter.addAction(BroadcastConstants.PUSH_ALLURL_CHANGE);
            filter.addAction(BroadcastConstants.PUSH_VOICE_IMAGE_REFRESH);
            filter.addAction(BroadcastConstants.PUSH_CALL_CALLALERT);
            filter.addAction(BroadcastConstants.PUSH_CALL_CHAT);

            context.registerReceiver(Receiver, filter);

            IntentFilter f = new IntentFilter();
            f.addAction(BroadcastConstants.PUSH_BACK);
            f.setPriority(1000);
            context.registerReceiver(Receiver, f);

            ToastUtils.show_short(context, "注册了广播接收器");
        }
    }


    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchTalkHistoryDao(context);
    }

    private void setView() {
        lin_two_call = (LinearLayout) rootView.findViewById(R.id.lin_two_call);                 //
        imageview = (ImageView) rootView.findViewById(R.id.image);                              //
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);                               //
        lin_call = (LinearLayout) rootView.findViewById(R.id.lin_call);                         //
        lin_guaduan = (LinearLayout) rootView.findViewById(R.id.lin_guaduan);
        // 以上为被呼叫界面
        lin_notalk = (LinearLayout) rootView.findViewById(R.id.lin_notalk);                     // 没有对讲时候的界面
        lin_personhead = (LinearLayout) rootView.findViewById(R.id.lin_personhead);             // 有个人对讲时候的界面
        tv_personname = (TextView) rootView.findViewById(R.id.tv_personname);                   // 个人对讲时候的好友名字
        image_persontx = (ImageView) rootView.findViewById(R.id.image_persontx);                // 个人对讲时候的好友头像
        image_personvoice = (ImageView) rootView.findViewById(R.id.image_personvoice);          // 个人对讲声音波
        lin_head = (LinearLayout) rootView.findViewById(R.id.lin_head);                         // 有群组对讲时候的界面
        image_grouptx = (ImageView) rootView.findViewById(R.id.image_grouptx);                  // 群组对讲时候群组头像
        tv_groupname = (TextView) rootView.findViewById(R.id.tv_groupname);                     // 群组对讲时候的群名
        tv_grouptype = (TextView) rootView.findViewById(R.id.tv_grouptype);                     // 群组对讲时候的群类型名
        tv_num = (TextView) rootView.findViewById(R.id.tv_num);                                 // 群组对讲时候的群在线人数
        tv_allnum = (TextView) rootView.findViewById(R.id.tv_allnum);                           // 群组对讲时候的群所有成员人数
        talkingName = (TextView) rootView.findViewById(R.id.talkingname);                       // 群组对讲时候对讲人姓名
        image_group_persontx = (ImageView) rootView.findViewById(R.id.image_group_persontx);    // 群组对讲时候对讲人头像
        gridView_person = (MyGridView) rootView.findViewById(R.id.gridView_person);             // 群组对讲时候对讲成员展示
        gridView_person.setSelector(new ColorDrawable(Color.TRANSPARENT));                      // 取消GridView的默认背景色
        gridView_tv = (TextView) rootView.findViewById(R.id.gridView_tv);                       // 群组对讲时候通话解释
        image_voice = (ImageView) rootView.findViewById(R.id.image_voice);                      // 群组对讲声音波
        talking_news = (TextView) rootView.findViewById(R.id.talking_news);                     // 群组对讲时候通话解释
        mListView = (ListView) rootView.findViewById(R.id.listView);                            //
        lin_foot = (MyLinearLayout) rootView.findViewById(R.id.lin_foot);                       // 对讲按钮
        lin_press = (LinearLayout) rootView.findViewById(R.id.lin_press);                       // 说话那妞
        imageView_answer = (ImageView) rootView.findViewById(R.id.imageView_answer);            //
        image_button = (Button) rootView.findViewById(R.id.image_button);                       //
        Relative_listview = (RelativeLayout) rootView.findViewById(R.id.Relative_listview);     //
        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setTipClick(this);

        image_personvoice.setBackgroundResource(R.drawable.talk_show);
        draw = (AnimationDrawable) image_personvoice.getBackground();

        image_personvoice.setVisibility(View.INVISIBLE);
        image_voice.setBackgroundResource(R.drawable.talk_show);
        draw_group = (AnimationDrawable) image_voice.getBackground();
        image_voice.setVisibility(View.INVISIBLE);

        talkingName.setVisibility(View.INVISIBLE);
    }

    private void listener() {
        lin_call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SubclassService.isallow = true;
                InterPhoneControl.PersonTalkAllow(context, callId, callerId);//接收应答
                if (SubclassService.musicPlayer != null) {
                    SubclassService.musicPlayer.stop();
                    SubclassService.musicPlayer = null;
                }
                isCallingForUser = true;
                addUser(callerId,callId);
                if (lin_two_call.getVisibility() == View.VISIBLE) {
                    lin_two_call.setVisibility(View.GONE);
                }
            }
        });
        lin_guaduan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SubclassService.isallow = true;
                InterPhoneControl.PersonTalkOver(context, callId, callerId);//拒绝应答
                if (SubclassService.musicPlayer != null) {
                    SubclassService.musicPlayer.stop();
                    SubclassService.musicPlayer = null;
                }
                if (lin_two_call.getVisibility() == View.VISIBLE) {
                    lin_two_call.setVisibility(View.GONE);
                }
                if(lin_press.getVisibility()==View.VISIBLE){
                    GlobalConfig.interPhoneType=3;
                }else{
                    GlobalConfig.interPhoneType=0;
                }
            }
        });
        image_grouptx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看群成员
                checkGroup();
            }
        });
        imageView_answer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //挂断
                hangUp();
            }
        });

        image_button.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("按钮操作", "按下");
                        press();//按下状态
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("按钮操作", "松手");
                        jack();//抬起手后的操作
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.e("按钮操作", "取消");
                        jack();//抬起手后的操作
                        break;
                }
                return false;
            }
        });
    }

    private void addUser(String id,String  callid) {
        String addTime = Long.toString(System.currentTimeMillis());    // 获取最新激活状态的数据
        String bjuserid = CommonUtils.getUserId(context);
        dbDao.deleteHistory(id);                                       // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
        DBTalkHistorary history = new DBTalkHistorary(bjuserid, "user", id, addTime);
        dbDao.addTalkHistory(history);
        InterPhoneControl.bdcallid = callid;
        zhiDingPerson();
    }

    /**
     * 设置对讲组为激活状态,此时没有组在对讲状态
     */
    public static void zhiDingGroupSS(final String groupIdS) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(BroadcastConstants.UP_DATA_GROUP);
                context.sendBroadcast(intent);
                enterGroupType = 1;
                groupId = groupIdS;
                tv_num.setText("1");
                listInfo = null;
                InterPhoneControl.Enter(context, groupId);//发送进入组的数据，socket
                getGridViewPerson(groupId);//获取群成员
            }
        }, 300);

    }

    /**
     * 设置对讲组为激活状态,此时没有组在对讲状态
     */
    public static void zhiDingGroup(final GroupInfo talkGroupInside) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(BroadcastConstants.UP_DATA_GROUP);
                context.sendBroadcast(intent);
                enterGroupType = 1;
                groupId = talkGroupInside.getGroupId();
                tv_num.setText("1");
                listInfo = null;
                InterPhoneControl.Enter(context, talkGroupInside.getGroupId());//发送进入组的数据，socket
                getGridViewPerson(talkGroupInside.getGroupId());//获取群成员
            }
        }, 300);
    }

    /**
     * 设置对讲组2为激活状态,此时存在组在对讲状态
     */
    public static void zhiDingGroupS(final GroupInfo talkGroupInside) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(BroadcastConstants.UP_DATA_GROUP);
                context.sendBroadcast(intent);
                enterGroupType = 2;
                groupId = talkGroupInside.getGroupId();
                tv_num.setText("1");
                listInfo = null;
                InterPhoneControl.Enter(context, talkGroupInside.getGroupId());//发送进入组的数据，socket
                getGridViewPerson(talkGroupInside.getGroupId());//获取群成员
            }
        }, 300);
    }

    /**
     * 设置个人为激活状态/设置第一条为激活状态
     */
    public static void zhiDingPerson() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((isCallingForUser || isCallingForGroup) && interPhoneType != null) {
                    //此时有对讲状态
                    if (interPhoneType.equals("user")) {
//                Log.e("上次通话ID", InterPhoneControl.bdcallid + "");
//                Log.e("上次通话ID222", GlobalConfig.oldBCCallId + "");
//                Log.e("新的来电ID", callid + "");
                        InterPhoneControl.PersonTalkHangUp(context, GlobalConfig.oldBCCallId);
                    } else {
                        InterPhoneControl.Quit(context, interPhoneId);//退出小组
                    }
                }
                try {
                    historyDataBaseList = dbDao.queryHistory();//得到数据库里边数据
                    getList();
                    setDatePerson();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show_always(context, "数据出错了，请您稍后再试");
                }
            }
        }, 300);
    }

    public static void setDatePerson() {
        //设置个人为激活状态
        isCallingForUser = true;
        GroupInfo firstdate = allList.remove(0);
        interPhoneType = firstdate.getTyPe();//
        interPhoneId = firstdate.getId();//
        Log.e("aaa=====callerid======", interPhoneId + "");
        lin_notalk.setVisibility(View.GONE);
        lin_personhead.setVisibility(View.VISIBLE);
        lin_head.setVisibility(View.GONE);
        lin_foot.setVisibility(View.VISIBLE);
        GlobalConfig.interPhoneType=3;
        GlobalConfig.isActive = true;
        tipView.setVisibility(View.GONE);
        tv_personname.setText(firstdate.getName());
        if (gridView_person.getVisibility() == View.VISIBLE) {
            gridView_person.setVisibility(View.GONE);
            gridView_tv.setVisibility(View.GONE);
        }
        if (firstdate.getPortrait() == null || firstdate.getPortrait().equals("") || firstdate.getPortrait().trim().equals("")) {
            image_persontx.setImageResource(R.mipmap.wt_image_tx_qz);
        } else {
            String url;
            if (firstdate.getPortrait().startsWith("http")) {
                url = firstdate.getPortrait();
            } else {
                url = GlobalConfig.imageurl + firstdate.getPortrait();
            }
            final String _url = AssembleImageUrlUtils.assembleImageUrl150(url);
            final String c_url = url;

            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, c_url, image_grouptx, IntegerConstant.TYPE_GROUP);
        }
        if (allList.size() == 0) {
            if (adapter == null) {
                adapter = new ChatListAdapter(context, allList, "0");
                mListView.setAdapter(adapter);
            } else {
                adapter.ChangeDate(allList, "0");
            }
        } else {
            if (adapter == null) {
                adapter = new ChatListAdapter(context, allList, allList.get(allList.size() - 1).getId());
                mListView.setAdapter(adapter);
            } else {
                adapter.ChangeDate(allList, allList.get(allList.size() - 1).getId());
            }
        }

        setListener();
    }

    protected static void call(String id) {
        Intent it = new Intent(context, CallAlertActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        it.putExtras(bundle);
        context.startActivity(it);
    }

    public void getTXL() {
        //第一次获取群成员跟组
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            JSONObject jsonObject = VolleyRequest.getJsonObject(context);
            try {
                jsonObject.put("Page", "1");
                jsonObject.put("PageSize", "10000");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            VolleyRequest.requestPost(GlobalConfig.gettalkpersonsurl, tag, jsonObject, new VolleyCallback() {
                @Override
                protected void requestSuccess(JSONObject result) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (isCancelRequest) {
                        return;
                    }
                    try {
                        LinkMan list = new Gson().fromJson(result.toString(), new TypeToken<LinkMan>() {
                        }.getType());
                        try {
                            GlobalConfig.list_group = list.getGroupList().getGroups();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        try {
                            GlobalConfig.list_person = list.getFriendList().getFriends();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //获取到群成员后
                    update(context);//第一次进入该界面
                }

                @Override
                protected void requestError(VolleyError error) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    //获取到群成员后
                    update(context);//第一次进入该界面
                }
            });
        } else {
            ToastUtils.show_always(context, "网络失败，请检查网络");
        }
    }

    /*
     * 第一次进入该界面
     */
    private void update(Context context) {
        //得到数据库里边数据
        historyDataBaseList = dbDao.queryHistory();
        //得到真实的数据
        getList();
        if (allList == null || allList.size() == 0) {
            //此时数据库里边没有数据，界面不变
            isCallingForUser = false;
            isCallingForGroup = false;
        } else {
            // 此处数据需要处理，第一条数据为激活状态组
            //第一条数据的状态
            //			String type = alllist.get(0).getTyPe();//对讲类型，个人跟群组
            //			String id = alllist.get(0).getId();//对讲组：groupid
            //			if(type!=null&&!type.equals("")&&type.equals("user")){
            //若上次退出前的通话状态是单对单通话则不处理
            isCallingForUser = false;
            isCallingForGroup = false;
            if (adapter == null) {
                adapter = new ChatListAdapter(context, allList, allList.get(allList.size() - 1).getId());
                mListView.setAdapter(adapter);
            } else {
                adapter.ChangeDate(allList, allList.get(allList.size() - 1).getId());
            }
            setListener();
        }
        if (MainActivity.groupInfo != null && MainActivity.groupInfo.getGroupId() != null
                && !MainActivity.groupInfo.getGroupId().equals("")) {
            String id = MainActivity.groupInfo.getGroupId();
            dbDao.deleteHistory(id);
            groupId = id;
            if (MainActivity.groupEntryNum != null && !MainActivity.groupEntryNum.trim().equals("")) {
                tv_num.setText(MainActivity.groupEntryNum + "");
            } else {
                tv_num.setText("1");
            }
            isCallingForGroup = true;
            interPhoneType = "group";
            MainActivity.groupEntryNum = "";
            addGroup(id);//加入到数据库
            setDateGroup();
            getGridViewPerson(id);
            MainActivity.groupInfo = null;
        }

        if (MainActivity.talkdb != null) {
            zhiDingPerson();
            MainActivity.talkdb = null;
        }
    }

    public void addGroup(String id) {
        //获取最新激活状态的数据
        String groupid = id;
        String type = "group";
        String addtime = Long.toString(System.currentTimeMillis());
        String bjuserid = CommonUtils.getUserId(context);
        //如果该数据已经存在数据库则删除原有数据，然后添加最新数据
        DBTalkHistorary history = new DBTalkHistorary(bjuserid, type, groupid, addtime);
        dbDao.addTalkHistory(history);
        historyDataBaseList = dbDao.queryHistory();//得到数据库里边数据
        getList();
    }

    public void setDateGroup() {
        //设置组为激活状态
        lin_notalk.setVisibility(View.GONE);
        lin_personhead.setVisibility(View.GONE);
        lin_head.setVisibility(View.VISIBLE);
        lin_foot.setVisibility(View.VISIBLE);
        GlobalConfig.interPhoneType=3;
        GlobalConfig.isActive = true;
        tipView.setVisibility(View.GONE);
        GroupInfo firstdate = allList.remove(0);
        interPhoneType = firstdate.getTyPe();//对讲类型，个人跟群组
        interPhoneId = firstdate.getId();//对讲组：groupid
        tv_groupname.setText(firstdate.getName());
        if (firstdate.getGroupType() == null || firstdate.getGroupType().equals("") || firstdate.getGroupType().equals("1")) {
            tv_grouptype.setText("公开群");
        } else if (firstdate.getGroupType().equals("0")) {
            tv_grouptype.setText("审核群");
        } else if (firstdate.getGroupType().equals("2")) {
            tv_grouptype.setText("密码群");
        }
        if (firstdate.getPortrait() == null || firstdate.getPortrait().equals("") || firstdate.getPortrait().trim().equals("")) {
            image_grouptx.setImageResource(R.mipmap.wt_image_tx_qz);
        } else {
            String url;
            if (firstdate.getPortrait().startsWith("http")) {
                url = firstdate.getPortrait();
            } else {
                url = GlobalConfig.imageurl + firstdate.getPortrait();
            }
            final String _url = AssembleImageUrlUtils.assembleImageUrl150(url);
            final String c_url = url;

            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, c_url, image_grouptx, IntegerConstant.TYPE_GROUP);
        }
        if (allList.size() == 0) {
            if (adapter == null) {
                adapter = new ChatListAdapter(context, allList, "0");
                mListView.setAdapter(adapter);
            } else {
                adapter.ChangeDate(allList, "0");
            }
        } else {
            if (adapter == null) {
                adapter = new ChatListAdapter(context, allList, allList.get(allList.size() - 1).getId());
                mListView.setAdapter(adapter);
            } else {
                adapter.ChangeDate(allList, allList.get(allList.size() - 1).getId());
            }
        }
        setListener();
    }


    // 获取群成员
    private static void getGridViewPerson(String id) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.grouptalkUrl, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                String UserList = null;
                try {
                    UserList = result.getString("UserList");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (groupPersonList != null) {
                    groupPersonList.clear();
                } else {
                    groupPersonList = new ArrayList<UserInfo>();
                }
                try {
                    groupPersonList = gson.fromJson(UserList, new TypeToken<List<UserInfo>>() {
                    }.getType());
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (groupPersonList != null && groupPersonList.size() > 0) {
                    tv_allnum.setText("/" + groupPersonList.size());
                } else {
                    tv_allnum.setText("/1");
                }
                if (ChatFragment.context != null) {
                    if (groupPersonList != null && groupPersonList.size() != 0) {
                        groupPersonListS.clear();
                        if (listInfo != null && listInfo.size() > 0) {
                            for (int j = 0; j < listInfo.size(); j++) {
                                String id = listInfo.get(j).getUserId().trim();
                                if (id != null && !id.equals("")) {
                                    for (int i = 0; i < groupPersonList.size(); i++) {
                                        String ids = groupPersonList.get(i).getUserId();
                                        if (id.equals(ids)) {
                                            Log.e("ids", ids + "=======" + i);
                                            groupPersonList.get(i).setOnLine(2);
                                            groupPersonListS.add(groupPersonList.get(i));
                                        }
                                    }
                                }
                            }
                        } else {
                            String id = CommonUtils.getUserId(context);
                            if (id != null && !id.equals("")) {
                                for (int i = 0; i < groupPersonList.size(); i++) {
                                    String ids = groupPersonList.get(i).getUserId();
                                    if (id.equals(ids)) {
                                        Log.e("ids", ids + "=======" + i);
                                        groupPersonList.get(i).setOnLine(2);
                                        groupPersonListS.add(groupPersonList.get(i));
                                    }
                                }
                            }
                        }
                        for (int h = 0; h < groupPersonList.size(); h++) {
                            if (groupPersonList.get(h).getOnLine() != 2) {
                                groupPersonListS.add(groupPersonList.get(h));
                            }
                        }
                        GroupPersonAdapter adapter = new GroupPersonAdapter(context, groupPersonListS);
                        gridView_person.setAdapter(adapter);
                        checkGroupListener();// adapter的适配器监听
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
            }
        });
    }

    // 接听新的来电弹出框-----提示是否挂断上次通话而开始一段新的通话
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
                InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
                if (dialogType == 1) {
                    InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
                    isCallingForUser = false;
                    isCallingForGroup = false;
                    lin_notalk.setVisibility(View.VISIBLE);
                    lin_personhead.setVisibility(View.GONE);
                    lin_head.setVisibility(View.GONE);
                    lin_foot.setVisibility(View.GONE);
                    GlobalConfig.interPhoneType=0;
                    image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.talknormal));
                    GlobalConfig.isActive = false;
                    call(phoneId);
                    confirmDialog.dismiss();
                } else {
                    InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
                    isCallingForUser = false;
                    isCallingForGroup = false;
                    lin_notalk.setVisibility(View.VISIBLE);
                    lin_personhead.setVisibility(View.GONE);
                    lin_head.setVisibility(View.GONE);
                    lin_foot.setVisibility(View.GONE);
                    GlobalConfig.interPhoneType=0;
                    image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.talknormal));
                    GlobalConfig.isActive = false;
                    zhiDingGroupSS(groupId);
                    //对讲主页界面更新
                    DuiJiangActivity.update();
                    confirmDialog.dismiss();
                }
            }
        });
    }

    // 重置界面
    private void setOnResumeView() {
        //此处在splashActivity中refreshB设置成true
        UserName = shared.getString(StringConstant.NICK_NAME, "");
        String p = shared.getString(StringConstant.PERSONREFRESHB, "false");
        String l = shared.getString(StringConstant.ISLOGIN, "false");
        if (l.equals("true")) {
            if (p.equals("true")) {
                Relative_listview.setVisibility(View.VISIBLE);
                //显示此时没有人通话界面
                lin_notalk.setVisibility(View.VISIBLE);
                lin_personhead.setVisibility(View.GONE);
                lin_head.setVisibility(View.GONE);
                lin_foot.setVisibility(View.GONE);
                GlobalConfig.interPhoneType=0;
                image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.talknormal));
                GlobalConfig.isActive = false;
                tipView.setVisibility(View.GONE);
                getTXL();
                Editor et = shared.edit();
                et.putString(StringConstant.PERSONREFRESHB, "false");
                et.commit();

            }
        } else {
            //显示未登录
            Relative_listview.setVisibility(View.GONE);
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_LOGIN);
        }
        if (lin_foot.getVisibility() == View.VISIBLE) {
            GlobalConfig.interPhoneType=3;
        }else{
            GlobalConfig.interPhoneType=0;
        }
    }

    // 挂断电话
    private void hangUp() {
        if (interPhoneType.equals("user")) {
            isCallingForUser = false;
            isCallingForGroup = false;
            InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
            historyDataBaseList = dbDao.queryHistory();//得到数据库里边数据
        } else {
            isCallingForUser = false;
            isCallingForGroup = false;
            InterPhoneControl.Quit(context, interPhoneId);//退出小组
            historyDataBaseList = dbDao.queryHistory();//得到数据库里边数据
        }
        getList();
        if (allList.size() == 0) {
            if (adapter == null) {
                adapter = new ChatListAdapter(context, allList, "0");
                mListView.setAdapter(adapter);
            } else {
                adapter.ChangeDate(allList, "0");
            }
        } else {
            if (adapter == null) {
                adapter = new ChatListAdapter(context, allList, allList.get(allList.size() - 1).getId());
                mListView.setAdapter(adapter);
            } else {
                adapter.ChangeDate(allList, allList.get(allList.size() - 1).getId());
            }
        }
        setListener();
        setImageViewForGroup(2, "", "");
        lin_notalk.setVisibility(View.VISIBLE);
        lin_personhead.setVisibility(View.GONE);
        lin_head.setVisibility(View.GONE);

        if (lin_two_call.getVisibility() == View.VISIBLE) {
           lin_press.setVisibility(View.GONE);
            GlobalConfig.interPhoneType=3;
        }else{
            lin_foot.setVisibility(View.GONE);
            GlobalConfig.interPhoneType=0;
        }

        image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.talknormal));
        GlobalConfig.isActive = false;
        gridView_person.setVisibility(View.GONE);
        gridView_tv.setVisibility(View.GONE);
    }

    // 组装需要展示的list
    private static void getList() {
        allList.clear();
        try {
            if (historyDataBaseList != null && historyDataBaseList.size() > 0) {
                for (int i = 0; i < historyDataBaseList.size(); i++) {
                    if (historyDataBaseList.get(i).getTyPe().equals("user")) {
                        if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() != 0) {
                            for (int j = 0; j < GlobalConfig.list_person.size(); j++) {
                                String id = historyDataBaseList.get(i).getID();
                                if (id != null && !id.equals("") && id.equals(GlobalConfig.list_person.get(j).getUserId())) {
                                    GroupInfo ListGP = new GroupInfo();
                                    ListGP.setTruename(GlobalConfig.list_person.get(j).getTruename());
                                    ListGP.setId(GlobalConfig.list_person.get(j).getUserId());
                                    ListGP.setName(GlobalConfig.list_person.get(j).getNickName());
                                    ListGP.setUserAliasName(GlobalConfig.list_person.get(j).getUserAliasName());
                                    ListGP.setPortrait(GlobalConfig.list_person.get(j).getPortraitBig());
                                    ListGP.setAddTime(historyDataBaseList.get(i).getAddTime());
                                    ListGP.setTyPe(historyDataBaseList.get(i).getTyPe());
                                    ListGP.setDescn(GlobalConfig.list_person.get(j).getDescn());
                                    ListGP.setUserNum(GlobalConfig.list_person.get(j).getUserNum());
                                    allList.add(ListGP);
                                    break;
                                }
                            }
                        }
                    } else {
                        if (GlobalConfig.list_group != null && GlobalConfig.list_group.size() != 0) {
                            for (int j = 0; j < GlobalConfig.list_group.size(); j++) {
                                String id = historyDataBaseList.get(i).getID();
                                if (id != null && !id.equals("") && id.equals(GlobalConfig.list_group.get(j).getGroupId())) {
                                    GroupInfo ListGP = new GroupInfo();
                                    ListGP.setCreateTime(GlobalConfig.list_group.get(j).getCreateTime());
                                    ListGP.setGroupCount(GlobalConfig.list_group.get(j).getGroupCount());
                                    ListGP.setGroupCreator(GlobalConfig.list_group.get(j).getGroupCreator());
                                    ListGP.setGroupDescn(GlobalConfig.list_group.get(j).getGroupDescn());
                                    ListGP.setId(GlobalConfig.list_group.get(j).getGroupId());
                                    ListGP.setPortrait(GlobalConfig.list_group.get(j).getGroupImg());
                                    ListGP.setGroupManager(GlobalConfig.list_group.get(j).getGroupManager());
                                    ListGP.setGroupMyAlias(GlobalConfig.list_group.get(j).getGroupMyAlias());
                                    ListGP.setName(GlobalConfig.list_group.get(j).getGroupName());
                                    ListGP.setGroupNum(GlobalConfig.list_group.get(j).getGroupNum());
                                    ListGP.setGroupSignature(GlobalConfig.list_group.get(j).getGroupSignature());
                                    ListGP.setGroupType(GlobalConfig.list_group.get(j).getGroupType());
                                    ListGP.setAddTime(historyDataBaseList.get(i).getAddTime());
                                    ListGP.setTyPe(historyDataBaseList.get(i).getTyPe());
                                    allList.add(ListGP);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("getlist异常", e.toString());
        }
    }

    // 设置listView的监听已经adapter上按钮的监听
    private static void setListener() {
        adapter.setOnListener(new OnListener() {
            @Override
            public void zhiding(int position) {
                groupId = allList.get(position).getId();
                if (isCallingForGroup || isCallingForUser) {
                    //此时有对讲状态
                    if (interPhoneType.equals("user")) {
                        //对讲状态为个人时，弹出框展示
                        String t = allList.get(position).getTyPe();
                        if (t != null && !t.equals("") && t.equals("user")) {
                            dialogType = 1;
                            phoneId = allList.get(position).getId();
                        } else {
                            dialogType = 2;
                        }
                        confirmDialog.show();
                    } else {
                        InterPhoneControl.Quit(context, interPhoneId);//退出小组
                        String t = allList.get(position).getTyPe();
                        if (t != null && !t.equals("") && t.equals("user")) {
                            call(allList.get(position).getId());
                        } else {
                            zhiDingGroupSS(groupId);
                        }
                    }
                } else {
                    String t = allList.get(position).getTyPe();
                    if (t != null && !t.equals("") && t.equals("user")) {
                        String id = allList.get(position).getId();
                        if (id != null && !id.equals("")) {
                            call(id);
                        }
                    } else {
                        zhiDingGroupSS(groupId);
                    }
                }
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = allList.get(position).getTyPe();
                if (type != null && type.equals("group")) {
                    //跳转到群组详情页面
                    Intent intent = new Intent(context, TalkGroupNewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "talkoldlistfragment");
                    bundle.putString("activationid", interPhoneId);
                    bundle.putSerializable("data", allList.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    // 跳转到详细信息界面
                    Intent intent = new Intent(context, TalkPersonNewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "talkoldlistfragment");
                    bundle.putSerializable("data", allList.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
    }

    //设置(组)有人说话时候界面友好交互
    private void setImageViewForGroup(int i, String userName, String url) {
        if (i == 1) {
            if (userName == null) {
                userName = "未知";
            }
            Log.e("userName===============", userName + "");
            talkingName.setVisibility(View.VISIBLE);
            if (userName.equals(UserName)) {
                talkingName.setText("我");
            } else {
                talkingName.setText(userName);
            }
            talking_news.setText("正在通话");
            image_voice.setVisibility(View.VISIBLE);
            if (url == null || url.equals("") || url.equals("null") || url.trim().equals("")) {
                image_group_persontx.setImageResource(R.mipmap.wt_image_tx_hy);
            } else {
                String urls;
                if (url.startsWith("http")) {
                    urls = url;
                } else {
                    urls = GlobalConfig.imageurl + url;
                }
                final String _url = AssembleImageUrlUtils.assembleImageUrl150(urls);
                final String c_url = url;

                // 加载图片
                AssembleImageUrlUtils.loadImage(_url, c_url, image_group_persontx, IntegerConstant.TYPE_PERSON);
            }
            if (draw_group.isRunning()) {
            } else {
                draw_group.start();
            }
        } else {
            talkingName.setVisibility(View.INVISIBLE);
            talking_news.setText("无人通话");
            if (draw_group.isRunning()) {
                draw_group.stop();
            }
            image_group_persontx.setImageResource(R.mipmap.wt_image_tx_hy);
            image_voice.setVisibility(View.INVISIBLE);
        }
    }

    //设置(person)有人说话时候界面友好交互
    private void setImageViewForUser(int i, String userName, String url) {
        if (i == 1) {
            if (userName == null) {
                userName = "未知";
            }
            Log.e("userName===============", userName + "");
            if (userName.equals(UserName)) {
                tv_personname.setText("我");
            } else {
                tv_personname.setText(userName);
            }
            image_personvoice.setVisibility(View.VISIBLE);
            if (draw.isRunning()) {
            } else {
                draw.start();
            }
            if (url == null || url.equals("") || url.equals("null") || url.trim().equals("")) {
                image_persontx.setImageResource(R.mipmap.wt_image_tx_hy);
            } else {
                String urls;
                if (url.startsWith("http")) {
                    urls = url;
                } else {
                    urls = GlobalConfig.imageurl + url;
                }
                final String _url = AssembleImageUrlUtils.assembleImageUrl150(urls);
                final String c_url = url;

                // 加载图片
                AssembleImageUrlUtils.loadImage(_url, c_url, image_persontx, IntegerConstant.TYPE_PERSON);
            }
        } else {
            image_personvoice.setVisibility(View.INVISIBLE);
            tv_personname.setText("无人通话");
            if (draw.isRunning()) {
                draw.stop();
            }
            image_persontx.setImageResource(R.mipmap.wt_image_tx_hy);
        }
    }

    // 查看群成员
    private void checkGroup() {
        if (gridView_person.getVisibility() == View.VISIBLE) {
            gridView_person.setVisibility(View.GONE);
            gridView_tv.setVisibility(View.GONE);
        } else {
            gridView_person.setVisibility(View.VISIBLE);
            gridView_tv.setVisibility(View.VISIBLE);
        }
    }

    // 查看群成员适配器的监听
    private static void checkGroupListener() {
        gridView_person.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isFriend = false;
                if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() != 0) {
                    for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                        if (groupPersonListS.get(position).getUserId().equals(GlobalConfig.list_person.get(i).getUserId())) {
                            isFriend = true;
                            break;
                        }
                    }
                } else {
                    //不是我的好友
                    isFriend = false;
                }
                if (isFriend) {
                    Intent intent = new Intent(context, TalkPersonNewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "talkoldlistfragment_p");
                    bundle.putSerializable("data", groupPersonListS.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, GroupPersonNewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "talkoldlistfragment_p");
                    bundle.putString("id", interPhoneId);
                    bundle.putSerializable("data", groupPersonListS.get(position));
                    intent.putExtras(bundle);
                    context.startActivityForResult(intent, 1);
                }
            }
        });
    }

    // 抬手后的操作
    protected void jack() {
//        if (isTalking) {
        if (interPhoneType.equals("group")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    VoiceStreamRecordService.stop();
                    InterPhoneControl.Loosen(context, interPhoneId);//发送取消说话控制
                    image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.talknormal));
                    Log.e("对讲页面====", "录音机停止+发送取消说话控制+延时0.30秒");
                }
            }, 300);
        } else {//此处处理个人对讲的逻辑
            VoiceStreamRecordService.stop();
            InterPhoneControl.PersonTalkPressStop(context);//发送取消说话控制
            image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.talknormal));
        }
//        } else {
//            VoiceStreamRecordService.stop();
//        }
    }

    // 按下说话按钮的动作
    protected void press() {
        if (interPhoneType.equals("group")) {
            // 此处处理组对讲的逻辑
            InterPhoneControl.Press(context, interPhoneId);                 // 发送说话请求
            VoiceStreamRecordService.stop();                                     // 停止可能存在的录音服务
            VoiceStreamRecordService.start(context, interPhoneId, "group"); // 开始录音
        } else {
            //此处处理个人对讲的逻辑
            InterPhoneControl.PersonTalkPressStart(context);                 // 发送说话请求
            VoiceStreamRecordService.stop();                                 // 停止可能存在的录音服务
            VoiceStreamRecordService.start(context, interPhoneId, "person"); // 开始录音
        }
    }

    // 接收socket的数据进行处理
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.PUSH)) {
                byte[] bt = intent.getByteArrayExtra("outMessage");
                try {
                    Log.e("chat的的push", JsonEncloseUtils.btToString(bt) + "");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                try {
                    MsgNormal message = (MsgNormal) MessageUtils.buildMsgByBytes(bt);

                    if (message != null) {
                        int biztype = message.getBizType();
                        if (biztype == 1) {
                            int cmdType = message.getCmdType();
                            if (cmdType == 2) {
                                int command = message.getCommand();
                                if (command == 9) {
                                    int returnType = message.getReturnType();
                                    switch (returnType) {
                                        case 0xff://TTT
                                            //请求通话出异常了
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "请求通话—出异常了");
                                            break;
                                        case 0x00:
                                            //没有有效登录用户
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "没有有效登录用户");
                                            break;
                                        case 0x02:                                            //无法获取用户组
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "无法获取用户组");
                                            break;
                                        case 0x01:
                                            //用户可以通话了
                                            isTalking = true;
                                            ToastUtils.show_short(context, "可以说话");
                                            image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.wt_duijiang_button_pressed));
                                            // headview中展示自己的头像
                                            // String url = BSApplication.SharedPreferences.getString(StringConstant.IMAGEURL, "");
                                            // setImageViewForGroup(1, UserName, url);
                                            VoiceStreamRecordService.send();
                                            break;
                                        case 0x04:
                                            //用户不在所指定的组
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "用户不在所指定的组");
                                            break;
                                        case 0x05:
                                            //进入组的人员不足两人
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "进入组的人员不足两人");
                                            break;
                                        case 0x08:
                                            //有人在说话，无权通话
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "有人在说话");
                                            break;
                                        case 0x90:
                                            //用户在电话通话
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "用户在电话通话");
                                            break;
                                        default:
                                            break;
                                    }
                                } else if (command == 0x0a) {
                                    int returnType = message.getReturnType();
                                    switch (returnType) {
                                        case 0xff://TTT
                                            //结束对讲出异常
                                            isTalking = false;
                                            setImageViewForGroup(2, "", "");
                                            ToastUtils.show_short(context, "结束对讲—出异常");
                                            break;
                                        case 0x00:
                                            //没有有效登录用户
                                            isTalking = false;
                                            setImageViewForGroup(2, "", "");
                                            ToastUtils.show_always(context, "数据出错，请注销后重新登录账户");
                                            break;
                                        case 0x02:
                                            //无法获取用户组
                                            isTalking = false;
                                            setImageViewForGroup(2, "", "");
                                            ToastUtils.show_always(context, "无法获取用户组");
                                            break;
                                        case 0x01:
                                            //成功结束对讲
                                            isTalking = false;
                                            setImageViewForGroup(2, "", "");
                                            ToastUtils.show_short(context, "结束对讲—成功");
                                            break;
                                        case 0x04:
                                            //	用户不在组
                                            isTalking = false;
                                            setImageViewForGroup(2, "", "");
                                            ToastUtils.show_short(context, "结束对讲");
                                            break;
                                        case 0x05:
                                            //	对讲人不是你，无需退出
                                            isTalking = false;
                                            setImageViewForGroup(2, "", "");
                                            ToastUtils.show_short(context, "对讲人不是你，无需退出");
                                            break;

                                        default:
                                            break;
                                    }
                                } else if (command == 0x10) {
                                    //组内有人说话
                                    ToastUtils.show_short(context, "组内人有人说话，有人按下说话钮");
//                                    MapContent data = (MapContent) message.getMsgContent();
//                                    //说话人
//                                    String talkUserId = data.get("SpeakerId") + "";
//                                    Log.i("talkUserId", talkUserId + "");
//                                    if (groupPersonList != null && groupPersonList.size() != 0) {
//                                        for (int i = 0; i < groupPersonList.size(); i++) {
//                                            if (groupPersonList.get(i).getUserId().equals(talkUserId)) {
//                                                setImageViewForGroup(1, groupPersonList.get(i).getUserName(), groupPersonList.get(i).getPortraitMini());
//                                            }
//                                        }
//                                    }
                                } else if (command == 0x20) {
                                    //组内人说话完毕，有人松手
//                                    setImageViewForGroup(2, "", "");
                                    ToastUtils.show_short(context, "组内人说话完毕，有人松手");
                                }
                            } else if (cmdType == 1) {
                                int command = message.getCommand();
                                if (command == 9) {
                                    int returnType = message.getReturnType();
                                    switch (returnType) {
                                        case 0xff://TT
                                            //进入组出异常
                                            isCallingForGroup = false;
                                            ToastUtils.show_short(context, "进入组—出异常");
                                            break;
                                        case 0x00:
                                            //没有有效登录用户
                                            isCallingForGroup = false;
                                            ToastUtils.show_always(context, "数据出错，请注销后重新登录账户");
                                            break;
                                        case 0x01:
                                            //进入组成功
                                            isCallingForGroup = true;
                                            ToastUtils.show_short(context, "进入组—成功");
                                            if (enterGroupType == 2) {
                                                InterPhoneControl.Quit(context, interPhoneId);//退出小组
                                                String id = groupId;//对讲组：groupid
                                                dbDao.deleteHistory(id);
                                                addGroup(id);//加入到数据库
                                                setDateGroup();
                                            } else {
                                                String id = groupId;//对讲组：groupid
                                                dbDao.deleteHistory(id);
                                                addGroup(id);//加入到数据库
                                                setDateGroup();
                                            }
                                            break;
                                        case 0x02:
                                            //无法获取用户组
                                            isCallingForGroup = false;
                                            ToastUtils.show_short(context, "无法获取用户组");
                                            break;
                                        case 0x04:
                                            //用户不在该组
                                            isCallingForGroup = false;
                                            ToastUtils.show_short(context, "进入组—用户不在该组");
                                            break;
                                        case 0x08:
                                            //用户已在组
                                            isCallingForGroup = true;
                                            if (enterGroupType == 2) {
                                                InterPhoneControl.Quit(context, interPhoneId);//退出小组
                                                String id = groupId;//对讲组：groupid
                                                dbDao.deleteHistory(id);
                                                addGroup(id);//加入到数据库
                                                setDateGroup();
                                            } else {
                                                String id = groupId;//对讲组：groupid
                                                dbDao.deleteHistory(id);
                                                addGroup(id);//加入到数据库
                                                setDateGroup();
                                            }
                                            ToastUtils.show_short(context, "进入组—用户已在组");
                                            break;
                                        default:
                                            break;
                                    }
                                } else if (command == 0x0a) {
                                    int returnType = message.getReturnType();
                                    switch (returnType) {
                                        case 0xff://TT
                                            //退出租出异常
                                            jack();
                                            ToastUtils.show_short(context, "退出租—出异常");
                                            isCallingForGroup = false;
                                            break;
                                        case 0x00:
                                            //没有有效登录用户
                                            jack();
                                            isCallingForGroup = false;
                                            ToastUtils.show_always(context, "数据出错，请注销后重新登录账户");
                                            break;
                                        case 0x01:
                                            //退出租成功
                                            jack();
                                            ToastUtils.show_short(context, "退出组—成功");
                                            isCallingForGroup = false;
                                            break;
                                        case 0x02:
                                            //退出租成功
                                            jack();
                                            isCallingForGroup = false;
                                            ToastUtils.show_short(context, "无法获取用户组");
                                            break;
                                        case 0x04:
                                            //用户不在该组
                                            jack();
                                            ToastUtils.show_short(context, "退出租—用户不在该组");
                                            isCallingForGroup = false;
                                            break;
                                        case 0x08:
                                            //用户已退出组
                                            jack();
                                            ToastUtils.show_short(context, "退出租—用户已退出组");
                                            isCallingForGroup = false;
                                            break;
                                        default:
                                            jack();
                                            ToastUtils.show_short(context, "退出租—用户已退出组");
                                            isCallingForGroup = false;
                                            break;
                                    }

                                } else if (command == 0x10) {
                                    //服务端发来的组内成员的变化
                                    ToastUtils.show_always(context, "服务端发来的组内成员的变化");
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String news = new Gson().toJson(map);
                                        JSONTokener jsonParser = new JSONTokener(news);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                        String ingroupusers = arg1.getString("InGroupUsers");

                                        listInfo = new Gson().fromJson(ingroupusers, new TypeToken<List<ListInfo>>() {
                                        }.getType());
                                        //组内所有在线成员
                                        //组内有人说话时，根据这个list数据，得到该成员信息啊：头像，昵称等

                                        String groupids = data.get("GroupId") + "";
                                        if (groupids != null && !groupids.trim().equals("") &&
                                                isCallingForGroup == true && groupId != null &&
                                                groupId.trim().equals(groupids)) {
                                            Log.i("组内成员人数", listInfo.size() + "");
                                            tv_num.setText(listInfo.size() + "");
                                            getGridViewPerson(groupids);
                                            //有人加入组
                                            ToastUtils.show_short(context, "有人进入组");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else if (biztype == 2) {
                            int cmdType = message.getCmdType();
                            if (cmdType == 2) {
                                int command = message.getCommand();
                                if (command == 9) {
                                    int returnType = message.getReturnType();
                                    switch (returnType) {
                                        case 0xff://TT
                                            //请求通话出异常了
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "请求通话—出异常了");
                                            break;
                                        case 0x02:
                                            //无权通话
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "当前有人在说话");
                                            break;
                                        case 0x01:
                                            //用户可以通话了
                                            isTalking = true;
                                            ToastUtils.show_short(context, "可以说话");
                                            image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.wt_duijiang_button_pressed));
                                            VoiceStreamRecordService.send();
                                            break;
                                        case 0x04:
                                            //用户无权通话
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "不能对讲，有人在说话");
                                            break;
                                        case 0x05:
                                            //无权通话
                                            VibratorUtils.Vibrate(ChatFragment.context, Vibrate);
                                            VoiceStreamRecordService.stop();
                                            ToastUtils.show_always(context, "不能对讲，状态错误");
                                            break;
                                        default:
                                            break;
                                    }
                                } else if (command == 0x0a) {
                                    int returnType = message.getReturnType();
                                    switch (returnType) {
                                        case 0xff://TT
                                            //结束对讲出异常
                                            isTalking = false;
                                            ToastUtils.show_short(context, "结束对讲—出异常");
                                            break;
                                        case 0x02:
                                            //	无法获取用户
                                            isTalking = false;
                                            ToastUtils.show_short(context, "无法获取用户");
                                            break;
                                        case 0x01:
                                            //成功结束对讲
                                            isTalking = false;
                                            ToastUtils.show_short(context, "结束对讲—成功");
                                            break;
                                        case 0x04:
                                            //	清除者和当前通话者不同，无法处理
                                            isTalking = false;
                                            ToastUtils.show_short(context, "清除者和当前通话者不同，无法处理");
                                            break;
                                        case 0x05:
                                            //	状态错误
                                            isTalking = false;
                                            ToastUtils.show_short(context, "状态错误");
                                            break;
                                        default:
                                            break;
                                    }
                                } else if (command == 0x10) {
                                    //有人说话
                                    Log.e("有人说话", "有人说话");
                                } else if (command == 0x20) {
                                    //说话完毕，有人松手
                                    Log.e("有人松手", "有人松手");
                                }
                            }
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else if (action.equals(BroadcastConstants.UP_DATA_GROUP)) {
                if (gridView_person != null) {
                    gridView_person.setVisibility(View.GONE);
                }
            } else if (action.equals(BroadcastConstants.PUSH_ALLURL_CHANGE)) {
                setOnResumeView();
            } else if (action.equals(BroadcastConstants.PUSH_BACK)) {
                byte[] bt = intent.getByteArrayExtra("outMessage");
                Log.e("chat的的push_back", Arrays.toString(bt) + "");
                try {
                    Log.e("chat的的push_back", JsonEncloseUtils.btToString(bt) + "");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                try {
                    MsgNormal message = (MsgNormal) MessageUtils.buildMsgByBytes(bt);
                    if (message != null) {
                        int cmdType = message.getCmdType();
                        if (cmdType == 1) {
                            int command = message.getCommand();
                            if (command == 0x30) {

                                try {
                                    MapContent data = (MapContent) message.getMsgContent();
                                    Map<String, Object> map = data.getContentMap();
                                    String callId = String.valueOf(map.get("CallId"));
                                    Log.e("chat的的CallId", callId + "");
                                    if (isCallingForGroup || isCallingForUser) {
                                        //此时有对讲状态
                                        if (interPhoneType.equals("user") && InterPhoneControl.bdcallid.equals(callId)) {
                                            //挂断电话的数据处理
                                            isCallingForUser = false;
                                            isCallingForGroup = false;
                                            historyDataBaseList = dbDao.queryHistory();//得到数据库里边数据
                                            getList();
                                            if (allList.size() == 0) {
                                                if (adapter == null) {
                                                    adapter = new ChatListAdapter(context, allList, "0");
                                                    mListView.setAdapter(adapter);
                                                } else {
                                                    adapter.ChangeDate(allList, "0");
                                                }
                                            } else {
                                                if (adapter == null) {
                                                    adapter = new ChatListAdapter(context, allList, allList.get(allList.size() - 1).getId());
                                                    mListView.setAdapter(adapter);
                                                } else {
                                                    adapter.ChangeDate(allList, allList.get(allList.size() - 1).getId());
                                                }
                                            }
                                            setListener();
                                            if (draw.isRunning()) {
                                                draw.stop();
                                            }
                                            image_personvoice.setVisibility(View.INVISIBLE);
                                            lin_notalk.setVisibility(View.VISIBLE);
                                            lin_personhead.setVisibility(View.GONE);
                                            lin_head.setVisibility(View.GONE);
                                            lin_foot.setVisibility(View.GONE);
                                            GlobalConfig.interPhoneType=0;
                                            image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.talknormal));
                                            gridView_person.setVisibility(View.GONE);
                                            GlobalConfig.isActive = false;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (action.equals(BroadcastConstants.PUSH_VOICE_IMAGE_REFRESH)) {
                // int seqNum = intent.getIntExtra("seqNum", -1);
                String imageUrl = null, userName = null;
                String talkId = intent.getStringExtra("talkId");
                if (talkId != null && !talkId.trim().equals("")) {
                    if (oldTalkId == null || oldTalkId.trim().equals("")) {
                        if (interPhoneType.equals("group")) {
                            if (groupPersonListS != null && groupPersonListS.size() > 0) {
                                for (int i = 0; i < groupPersonListS.size(); i++) {
                                    if (groupPersonListS.get(i).getUserId().trim().equals(talkId)) {
                                        userName = groupPersonListS.get(i).getNickName();
                                        imageUrl = groupPersonListS.get(i).getPortraitBig();
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                                for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                                    if (GlobalConfig.list_person.get(i).getUserId().trim().equals(talkId)) {
                                        userName = GlobalConfig.list_person.get(i).getNickName();
                                        imageUrl = GlobalConfig.list_person.get(i).getPortraitBig();
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        if (!talkId.trim().equals(oldTalkId.trim())) {
                            if (interPhoneType.equals("group")) {
                                if (groupPersonListS != null && groupPersonListS.size() > 0) {
                                    for (int i = 0; i < groupPersonListS.size(); i++) {
                                        if (groupPersonListS.get(i).getUserId().trim().equals(talkId)) {
                                            userName = groupPersonListS.get(i).getNickName();
                                            imageUrl = groupPersonListS.get(i).getPortraitBig();
                                            break;
                                        }
                                    }
                                }
                            } else {
                                if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                                    for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                                        if (GlobalConfig.list_person.get(i).getUserId().trim().equals(talkId)) {
                                            userName = GlobalConfig.list_person.get(i).getNickName();
                                            imageUrl = GlobalConfig.list_person.get(i).getPortraitBig();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                oldTalkId = talkId;
                if (run != null) {
                    handler.removeCallbacks(run);
                }

                if (interPhoneType.equals("group")) {
                    setImageViewForGroup(1, userName, imageUrl);
                } else {
                    //此处设置个人界面
                    setImageViewForUser(1, userName, imageUrl);
                }
                run = new Runnable() {
                    @Override
                    public void run() {
                        if (interPhoneType.equals("group")) {
                            setImageViewForGroup(0, null, null);
                        } else {
                            //此处处理个人对讲的逻辑
                            setImageViewForUser(0, null, null);
                        }
                        handler.removeCallbacks(run);
                    }
                };
                handler.postDelayed(run, 500);
            } else if (action.equals(BroadcastConstants.PUSH_NOTIFY)) {
                byte[] bt = intent.getByteArrayExtra("outMessage");
                Log.e("chat的PUSH_NOTIFY", Arrays.toString(bt) + "");
                try {
                    Log.e("chat的PUSH_NOTIFY", JsonEncloseUtils.btToString(bt) + "");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                try {
                    MsgNormal message = (MsgNormal) MessageUtils.buildMsgByBytes(bt);
                    if (message != null) {
                        int cmdType = message.getCmdType();
                        switch (cmdType) {
                            case 2:
                                int command2 = message.getCommand();
                                if (command2 == 4) {
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String news = new Gson().toJson(map);

                                        JSONTokener jsonParser = new JSONTokener(news);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                        String userinfos = arg1.getString("UserInfo");

                                        try {
                                            ListInfo userinfo = new Gson().fromJson(userinfos, new TypeToken<ListInfo>() {
                                            }.getType());
                                            listInfo.add(userinfo);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        String groupids = data.get("GroupId") + "";
                                        if (groupids != null && !groupids.trim().equals("") &&
                                                isCallingForGroup == true && groupId != null &&
                                                groupId.trim().equals(groupids)) {
                                            Log.i("组内成员人数", listInfo.size() + "");
                                            tv_num.setText(listInfo.size() + "");
                                            getGridViewPerson(groupids);
                                            //有人加入组
                                            ToastUtils.show_short(context, "有人加入组");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (command2 == 5) {
                                    //有人退出组
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String news = new Gson().toJson(map);

                                        JSONTokener jsonParser = new JSONTokener(news);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                        String groupids = data.get("GroupId") + "";
                                        if (groupids != null && !groupids.trim().equals("") &&
                                                isCallingForGroup == true && groupId != null &&
                                                groupId.trim().equals(groupids)) {
                                            String userinfos = arg1.getString("UserInfo");
                                            String userinfoid;
                                            try {
                                                ListInfo userinfo = new Gson().fromJson(userinfos, new TypeToken<ListInfo>() {
                                                }.getType());
                                                userinfoid = userinfo.getUserId();
                                                for (int i = 0; i < listInfo.size(); i++) {
                                                    if (listInfo.get(i).getUserId().equals(userinfoid)) {
                                                        listInfo.remove(i);
                                                    }
                                                }
                                                Log.i("组内成员人数", listInfo.size() + "");
                                                tv_num.setText(listInfo.size() + "");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            getGridViewPerson(groupids);
                                            ToastUtils.show_short(context, "有人退出组");
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            default:
                                break;
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else if (action.equals(BroadcastConstants.PUSH_CALL_CHAT)) {
                // 收到新的别人呼叫
                String type = intent.getStringExtra("type");
                callId = intent.getStringExtra("callId");
                callerId = intent.getStringExtra("callerId");
                if (type != null && !type.trim().equals("") && type.trim().equals("call")) {
                    if (lin_two_call.getVisibility() == View.VISIBLE) {
                        //  此时已经有人在通话了，再次收到会拒接
                        InterPhoneControl.PersonTalkOver(context, callId, callerId);//拒绝应答
                    } else {
                        String _name = null;
                        String _image = null;
                        try {
                            if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                                for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                                    if (callerId.equals(GlobalConfig.list_person.get(i).getUserId())) {
                                        _image = GlobalConfig.list_person.get(i).getPortraitBig();
                                        _name = GlobalConfig.list_person.get(i).getNickName();
                                        break;
                                    }
                                }
                            } else {
                                _image = null;
                                _name = "我听科技";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            _image = null;
                            _name = "我听科技";
                        }

                        //适配好友展示信息
                        tv_name.setText(_name);
                        if (_image == null || _image.equals("") || _image.equals("null") || _image.trim().equals("")) {
                            imageview.setImageResource(R.mipmap.wt_image_tx_hy);
                        } else {
                            String url = GlobalConfig.imageurl + _image;
                            final String _url = AssembleImageUrlUtils.assembleImageUrl150(url);
                            // 加载图片
                            AssembleImageUrlUtils.loadImage(_url, url, imageview, IntegerConstant.TYPE_LIST);
                        }
                        // 超时拒接后隐藏界面
                        if (lin_two_call.getVisibility() == View.VISIBLE) {
                            lin_two_call.setVisibility(View.GONE);
                        }else{
                            lin_two_call.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    // 超时拒接后隐藏界面
                    if (lin_two_call.getVisibility() == View.VISIBLE) {
                        lin_two_call.setVisibility(View.GONE);
                    }
                }
            }else if (action.equals(BroadcastConstants.PUSH_CALL_CALLALERT)) {
                String type = intent.getStringExtra("type");
                if (type != null && !type.trim().equals("") && type.trim().equals("back")) {
                    // 超时拒接后隐藏界面
                    if (lin_two_call.getVisibility() == View.VISIBLE) {
                        lin_two_call.setVisibility(View.GONE);
                    }
                }
            }
        }
    }


}
