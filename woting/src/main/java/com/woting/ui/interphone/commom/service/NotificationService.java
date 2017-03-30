package com.woting.ui.interphone.commom.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.JsonEncloseUtils;
import com.woting.ui.interphone.commom.message.MessageUtils;
import com.woting.ui.interphone.commom.message.MsgNormal;
import com.woting.ui.interphone.commom.message.content.MapContent;
import com.woting.ui.interphone.commom.model.ApplyUserInfo;
import com.woting.ui.interphone.commom.model.BeInvitedUserInfo;
import com.woting.ui.interphone.commom.model.GroupInfo;
import com.woting.ui.interphone.commom.model.InviteUserInfo;
import com.woting.ui.interphone.commom.model.SeqMediaInfo;
import com.woting.ui.interphone.commom.model.UserInfo;
import com.woting.ui.interphone.linkman.model.DBNotifyHistory;
import com.woting.ui.interphone.message.messagecenter.dao.MessageSubscriberDao;
import com.woting.ui.interphone.message.messagecenter.dao.MessageNotifyDao;
import com.woting.ui.interphone.message.messagecenter.dao.MessageSystemDao;
import com.woting.ui.interphone.message.messagecenter.model.DBSubscriberMessage;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Notification
 *
 * @author 辛龙
 *         2016年4月27日
 */
public class NotificationService extends Service {
    private MessageReceiver Receiver;
    private NotificationService context;
    private MessageNotifyDao dbDaoNotify;
    private MessageSubscriberDao dbDaoSubscriber;
    private MessageSystemDao dbDaoSystem;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initDao();
        if (Receiver == null) {
            Receiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH_NOTIFY);
            registerReceiver(Receiver, filter);
        }
    }

    private void initDao() {// 初始化数据库命令执行对象
        dbDaoNotify = new MessageNotifyDao(context); // 通知消息
        dbDaoSubscriber = new MessageSubscriberDao(context);// 订阅消息
        dbDaoSystem = new MessageSystemDao(context);// 系统消息
    }

    /**
     * 添加通知消息到数据库
     *
     * @param image_url
     * @param person_name
     * @param person_id
     * @param group_name
     * @param group_id
     * @param operator_name
     * @param operator_id
     * @param show_type
     * @param message_type
     * @param deal_time
     * @param biz_type
     * @param cmd_type
     * @param command
     * @param message_id
     */
    private void addNotifyMessage(String image_url, String person_name, String person_id, String group_name, String group_id,
                                  String operator_name, String operator_id, String show_type, String message_type, String deal_time,
                                  int biz_type, int cmd_type, int command, String message_id, String message) {
        String addTime = Long.toString(System.currentTimeMillis());
        String bjUserId = CommonUtils.getUserId(context);
        DBNotifyHistory history = new DBNotifyHistory(bjUserId, image_url,
                person_name, person_id, group_name, group_id, operator_name,
                operator_id, show_type, message_type, deal_time, addTime,
                biz_type, cmd_type, command, message_id, message);
        dbDaoNotify.addNotifyMessage(history);
    }

    /**
     * 添加订阅消息到数据库
     *
     * @param image_url
     * @param seq_name
     * @param seq_id
     * @param content_name
     * @param content_id
     * @param deal_time
     * @param biz_type
     * @param cmd_type
     * @param command
     * @param message_id
     */
    private void addSubscriberMessage(String image_url, String seq_name, String seq_id,
                                      String content_name, String content_id, String deal_time,
                                      int biz_type, int cmd_type, int command, String message_id) {
        String add_time = Long.toString(System.currentTimeMillis());
        String bjUserId = CommonUtils.getUserId(context);
        DBSubscriberMessage history = new DBSubscriberMessage(bjUserId, image_url, seq_name, seq_id,
                content_name, content_id, deal_time, add_time, biz_type, cmd_type, command, message_id);
        dbDaoSubscriber.addSubscriberMessage(history);
    }

    /**
     * 添加系统消息到数据库
     *
     * @param image_url
     * @param person_name
     * @param person_id
     * @param group_name
     * @param group_id
     * @param operator_name
     * @param operator_id
     * @param show_type
     * @param message_type
     * @param deal_time
     * @param biz_type
     * @param cmd_type
     * @param command
     * @param message_id
     */
    private void addSystemMessage(String image_url, String person_name, String person_id, String group_name, String group_id,
                                  String operator_name, String operator_id, String show_type, String message_type, String deal_time,
                                  int biz_type, int cmd_type, int command, String message_id, String message) {
        String addTime = Long.toString(System.currentTimeMillis());
        String bjUserId = CommonUtils.getUserId(context);
        DBNotifyHistory history = new DBNotifyHistory(bjUserId, image_url,
                person_name, person_id, group_name, group_id, operator_name,
                operator_id, show_type, message_type, deal_time, addTime,
                biz_type, cmd_type, command, message_id, message);
        dbDaoSystem.addSystemNews(history);
    }


    /*
     * 接收socket的数据进行处理
     */
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.PUSH_NOTIFY)) {
                byte[] bt = intent.getByteArrayExtra("outMessage");
                try {
                    Log.e("Notification接收器中数据", Arrays.toString(bt) + "");
                    Log.e("Notification接收器中数据", JsonEncloseUtils.btToString(bt) + "");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

                try {
                    MsgNormal message = (MsgNormal) MessageUtils.buildMsgByBytes(bt);
                    if (message != null) {
                        int cmdType = message.getCmdType();
                        switch (cmdType) {
                            case 1:
                                int command = message.getCommand();
                                if (command == 1) {
                                    //介绍：当某用户被另一用户邀请为好友后，服务器向被邀请用户发送的消息。
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);

                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                        String invite_user_info = arg1.getString("InviteUserInfo");
                                        InviteUserInfo user_info = new Gson().fromJson(invite_user_info, new TypeToken<InviteUserInfo>() {
                                        }.getType());// 邀请者信息

                                        String person_id = user_info.getUserId();// 邀请者ID
                                        String person_name = user_info.getUserName();// 邀请者名称
                                        String image_url = user_info.getPortraitMini();// 邀请者头像

                                        String deal_time = data.get("InviteTime") + "";// 邀请时间
                                        String message_id = message.getMsgId();// 消息ID

                                        // 生成notification消息
                                        String news;
                                        String title;
                                        if (person_name == null || person_name.trim().equals("")) {
                                            title = "我听";
                                        } else {
                                            title = person_name;
                                        }
                                        news = "有人邀请你加为好友，快去看看吧~";
                                        setNewMessageNotification(context, news, title);

                                        // 更改通讯录新的朋友按钮展示
                                        Intent p = new Intent(BroadcastConstants.PUSH_NEWPERSON);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("outMessage", "好友消息");
                                        p.putExtras(bundle);
                                        context.sendBroadcast(p);

                                        // 删除本地相同的业务数据，不管收到多少条数据都以最新的为主
                                        dbDaoNotify.upDataNotifyForDuplicate("p1",person_id,"");

                                        // 加入到通知数据库
                                        addNotifyMessage(image_url, person_name, person_id, "", "", "",
                                                "", "true", "p1", deal_time, 4, 1, 1, message_id, news);
                                    } catch (Exception e) {
                                        Log.e("通知消息==", "添加好友消息解析出错了");
                                    }

                                } else if (command == 3) {
                                    //好友邀请被接受或拒绝(Server->App)
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);

                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                        String be_invited_user_info = arg1.getString("BeInvitedUserInfo");
                                        BeInvitedUserInfo user_info = new Gson().fromJson(be_invited_user_info, new TypeToken<BeInvitedUserInfo>() {
                                        }.getType());

                                        String message_id = message.getMsgId();// 消息ID
                                        String deal_time = data.get("DealTime") + "";// 消息处理时间
                                        String deal_type = data.get("DealType") + "";// 处理类型 1，接受 2，拒绝
                                        String person_name = user_info.getUserName();// 邀请者名称
                                        String person_id = user_info.getUserId();// 邀请者ID
                                        String image_url = user_info.getPortraitMini();// 邀请者头像

                                        if (deal_type != null) {
                                            if (deal_type.equals("1")) {
                                                // 生成notification消息

                                                String news = null;
                                                String title ;
                                                if (person_name == null || person_name.trim().equals("")) {
                                                    title = "我听";
                                                    if (deal_type != null && deal_type.equals("1")) {
                                                        news = "我们成为好友了";
                                                    }
                                                    // else {
                                                    //    news = "有人拒绝您添加为好友";
                                                    // }
                                                } else {
                                                    title = person_name;
                                                    if (deal_type != null && deal_type.equals("1")) {
                                                        news = "我们成为好友了";
                                                    }
                                                    // else {
                                                    //    news = name + "拒绝添加您为好友";
                                                    // }
                                                }
                                                setNewMessageNotification(context, news, title);

                                                // 加入到通知数据库
                                                addNotifyMessage(image_url, person_name, person_id, "", "", "",
                                                        "", "false", "p2", deal_time, 4, 1, 3, message_id, news);
                                                // 更新通讯录
                                                context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                                            }
                                        } else {
                                            Log.e("通知消息==", "好友邀请被接受或拒绝消息解析出错了==没有dealtype处理类型 ");
                                        }
                                    } catch (Exception e) {
                                        Log.e("通知消息==", "好友邀请被接受或拒绝消息解析出错了");
                                    }
                                    // 好友拒绝的消息不需要处理
                                    //  else {
                                    //  setNewMessageNotification(context, news, "我听");
                                    //  addNotifyMessage("Ub3", imageurl, news, "好友邀请信息", dealtime, "false", 0, 0, 0, "");
                                    //  }
                                } else if (command == 5) {
                                    // A与B原为好友，A把B从自己的好友中删除后，向B发送A已删除自己为好友的信息。
                                    // Data data = message.getData();
                                    // setNewMessageNotification(context, "测试：《该提示不显示》删除好友通知", "我听");
                                    context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                                }
                                break;
                            case 2:
                                int command2 = message.getCommand();
                                if (command2 == 1) {
                                    //当某用户被自己好友邀请进某个组时，服务器向某用户发送的消息。
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        String person_id = data.get("FriendId") + "";
                                        String person_name = null;
                                        String image_url = null;
                                        String operator_name = null;

                                        // 通过id获取邀请者名称跟头像
                                        if (person_id != null && !person_id.trim().equals("") && GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                                            for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                                                if (GlobalConfig.list_person.get(i).getUserId().equals(person_id)) {
                                                    person_name = GlobalConfig.list_person.get(i).getUserName();
                                                    image_url = GlobalConfig.list_person.get(i).getPortraitMini();
                                                }
                                            }
                                        }

                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);

                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                        String groupInfo = arg1.getString("GroupInfo");
                                        GroupInfo user_info = new Gson().fromJson(groupInfo, new TypeToken<GroupInfo>() {
                                        }.getType());

                                        // 通过OperatorId获取操作者名称
                                        String operator_id = arg1.getString("OperatorId");
                                        if (operator_id != null && !operator_id.trim().equals("") && GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                                            for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                                                if (GlobalConfig.list_person.get(i).getUserId().equals(operator_id)) {
                                                    operator_name = GlobalConfig.list_person.get(i).getUserName();
                                                }
                                            }
                                        }

                                        String message_id = message.getMsgId();// 消息ID
                                        String deal_time = data.get("InviteTime") + "";// 消息处理时间
                                        String group_name = user_info.getGroupName();
                                        String group_id = user_info.getGroupId();

                                        String news;
                                        String title;
                                        if (person_name == null || person_name.trim().equals("")) {
                                            title = "我听";
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "邀请您加入对讲组,快去看看吧~";
                                            } else {
                                                news = "有人邀请您加入对讲组:" + group_name + ",快去看看吧~";
                                            }
                                            setNewMessageNotification(context, news, "我听");
                                        } else {
                                            title = person_name;
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "邀请您加入对讲组,快去看看吧~";
                                            } else {
                                                news = "邀请您加入对讲组:" + group_name + ",快去看看吧~";
                                            }

                                        }
                                        setNewMessageNotification(context, news, title);

                                        // 更改通讯录新的朋友按钮展示
                                        Intent push_intent = new Intent(BroadcastConstants.PUSH_NEWPERSON);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("outMessage", "群组消息");
                                        push_intent.putExtras(bundle);
                                        context.sendBroadcast(push_intent);

                                        // 更改重复数据为不可见状态
                                        dbDaoNotify.upDataNotifyForDuplicate("g1",group_id,person_id);

                                        // 加入到通知数据库
                                        addNotifyMessage(image_url, person_name, person_id, group_name, group_id, operator_name,
                                                operator_id, "true", "g1", deal_time, 4, 2, 1, message_id, news);
                                    } catch (Exception e) {
                                        Log.e("消息接收服务中G1的异常", e.toString());
                                    }
                                } else if (command2 == 2) {
                                    //当某用户申请加入组后，向组管理员发送有用户申请的消息
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);
                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();

                                        String group_info = arg1.getString("GroupInfo");
                                        GroupInfo user_info = new Gson().fromJson(group_info, new TypeToken<GroupInfo>() {
                                        }.getType());

                                        String apply_user_info = arg1.getString("ApplyUserInfo");
                                        ApplyUserInfo applyuserinfo = new Gson().fromJson(apply_user_info, new TypeToken<ApplyUserInfo>() {
                                        }.getType());

                                        String message_id = message.getMsgId();// 消息ID
                                        String deal_time = data.get("ApplyTime") + "";
                                        String group_name = user_info.getGroupName();
                                        String group_id = user_info.getGroupId();

                                        String person_name = applyuserinfo.getUserName();// 申请者名称
                                        String person_id = applyuserinfo.getUserId();// 申请者ID
                                        String image_url = null;// 申请者头像

                                        // 通过person_id获取被邀请者头像
                                        if (person_id != null && !person_id.trim().equals("") && GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                                            for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                                                if (GlobalConfig.list_person.get(i).getUserId().equals(person_id)) {
                                                    image_url = GlobalConfig.list_person.get(i).getPortraitMini();
                                                }
                                            }
                                        }

                                        // 生成notification消息
                                        String news;
                                        String title;
                                        if (person_name == null || person_name.trim().equals("")) {
                                            title = "我听";
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "有人申请加入对讲组,快去看看吧~";
                                            } else {
                                                news = "有人申请加入对讲组:" + group_name + ",快去看看吧~";
                                            }
                                        } else {
                                            title = person_name;
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "申请加入对讲组,快去看看吧~";
                                            } else {
                                                news = "申请加入对讲组:" + group_name + ",快去看看吧~";
                                            }
                                        }
                                        setNewMessageNotification(context, news, title);

                                        // 更改重复数据为不可见状态
                                        dbDaoNotify.upDataNotifyForDuplicate("g2",group_id,person_id);

                                        // 加入到通知数据库
                                        addNotifyMessage(image_url, person_name, person_id, group_name, group_id, "",
                                                "", "true", "g2", deal_time, 4, 2, 2, message_id, news);
                                    } catch (Exception e) {
                                        Log.e("消息接收服务中G2的异常", e.toString());
                                    }
                                } else if (command2 == 3) {
                                    //当某用户被邀请入组或申请入组的请求
                                    //被管理员或其他有权限的人员处理（接受或拒绝）后，向该用户发送处理结果的消息。
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);

                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                        String group_info = arg1.getString("GroupInfo");
                                        GroupInfo user_info = new Gson().fromJson(group_info, new TypeToken<GroupInfo>() {
                                        }.getType());

                                        String message_id = message.getMsgId();// 消息ID
                                        String group_name = user_info.getGroupName();
                                        String group_id = user_info.getGroupId();
                                        String deal_type = data.get(" ") + "";// 处理类型 1，接受 2，拒绝
                                        String deal_time = data.get("ApplyTime") + "";
                                        String person_id = message.getUserId();
                                        String image_url = null;
                                        String operator_name = null;
                                        String person_name = null;

                                        // 通过id获取被邀请者名称跟头像
                                        if (person_id != null && !person_id.trim().equals("") && GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                                            for (int i = 0; i < GlobalConfig.list_person.size(); i++) {

                                                if (GlobalConfig.list_person.get(i).getUserId().equals(person_id)) {
                                                    person_name = GlobalConfig.list_person.get(i).getUserName();
                                                    image_url = GlobalConfig.list_person.get(i).getPortraitMini();
                                                }
                                            }
                                        }

                                        // 通过OperatorId获取操作者名称
                                        String operator_id = arg1.getString("OperatorId");
                                        if (operator_id != null && !operator_id.trim().equals("") && GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                                            for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                                                if (GlobalConfig.list_person.get(i).getUserId().equals(operator_id)) {
                                                    operator_name = GlobalConfig.list_person.get(i).getUserName();
                                                }
                                            }
                                        }

                                        String InType = data.get("InType") + "";// 消息类型 1被邀请入组 2主动申请入组
                                        if (InType != null && !InType.trim().equals("")) {
                                            if (InType.trim().equals("1")) {
                                                // 被邀请入组
                                                if (deal_type != null && !deal_type.trim().equals("")) {
                                                    // 生成notification消息
                                                    if (deal_type.equals("1")) {
                                                        //同意
                                                        String news;
                                                        String title;
                                                        if (person_name == null || person_name.trim().equals("")) {
                                                            title = "我听";
                                                            if (group_name == null || group_name.trim().equals("")) {
                                                                news = "有一个新的入组邀请已经通过" + ",快去看看吧~";
                                                            } else {
                                                                news = "有人加入了对讲组:" + group_name + ",快去看看吧~";
                                                            }
                                                        } else {
                                                            title = person_name;
                                                            if (group_name == null || group_name.trim().equals("")) {
                                                                news = "加入对讲组" + ",快去看看吧~";
                                                            } else {
                                                                news = "加入对讲组:" + group_name + ",快去看看吧~";
                                                            }
                                                        }
                                                        setNewMessageNotification(context, news, title);

                                                        // 更新通讯录
                                                        Intent push_intent = new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN);
                                                        context.sendBroadcast(push_intent);

                                                        // 加入到通知数据库
                                                        addNotifyMessage(image_url, person_name, person_id, group_name, group_id, operator_name,
                                                                operator_id, "false", "g31", deal_time, 4, 2, 3, message_id, news);
                                                    }
                                                    // 拒绝消息不展示
                                                    // else {
                                                    //拒绝
                                                    // }
                                                } else {
                                                    Log.e("组加入消息异常", "deal_type没有获取到");
                                                }
                                            } else if (InType.trim().equals("2")) {
                                                // 主动申请入组
                                                if (deal_type != null && !deal_type.trim().equals("")) {
                                                    // 生成notification消息
                                                    if (deal_type.equals("1")) {
                                                        //同意
                                                        String news;
                                                        if (group_name == null || group_name.trim().equals("")) {
                                                            news = "您入组成功";
                                                            setNewMessageNotification(context, news, "我听");
                                                        } else {
                                                            news = "同意了您的入组申请";
                                                            setNewMessageNotification(context, news, group_name);
                                                        }

                                                        // 更新通讯录
                                                        Intent push_intent = new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN);
                                                        context.sendBroadcast(push_intent);

                                                        // 加入到通知数据库
                                                        addNotifyMessage(image_url, person_name, person_id, group_name, group_id, operator_name,
                                                                operator_id, "false", "g32", deal_time, 4, 2, 3, message_id, news);
                                                    }
                                                    // 拒绝消息不展示
                                                    // else {
                                                    //拒绝
                                                    // }
                                                } else {
                                                    Log.e("组加入消息异常", "deal_type没有获取到");
                                                }
                                            }
                                        } else {
                                            Log.e("组加入消息异常", "InType没有获取到");
                                        }
                                    } catch (Exception e) {
                                        Log.e("消息接收服务中G3的异常", e.toString());
                                    }
                                } else if (command2 == 4) {
                                    //当有某人加入组后，向组内成员发送这个消息。
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);
                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                        String user_info = arg1.getString("UserInfo");

                                        UserInfo userinfo = new Gson().fromJson(user_info, new TypeToken<UserInfo>() {
                                        }.getType());
                                        String person_name = userinfo.getUserName();// 进入组的人的名称
                                        String person_id = userinfo.getUserId();// 进入组的人的ID

                                        String group_id = data.get("GroupId") + "";// 组ID
                                        String group_name = null;
                                        String image_url = null;
                                        // 通过group_id获取组的名称跟头像
                                        if (group_id != null && !group_id.trim().equals("") && GlobalConfig.list_group != null && GlobalConfig.list_group.size() > 0) {
                                            for (int i = 0; i < GlobalConfig.list_group.size(); i++) {
                                                if (GlobalConfig.list_group.get(i).getGroupId().equals(group_id)) {
                                                    group_name = GlobalConfig.list_group.get(i).getGroupName();
                                                    image_url = GlobalConfig.list_group.get(i).getGroupImg();
                                                }
                                            }
                                        }

                                        String message_id = message.getMsgId();// 消息ID
                                        String deal_time = String.valueOf(message.getSendTime());// 消息处理时间

                                        String news;
                                        if (person_name == null || person_name.trim().equals("")) {
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "有一个人加入到您所在的对讲组" + ",快去看看吧~";
                                            } else {
                                                news = "有一个人加入到对讲组:" + group_name + ",快去看看吧~";
                                            }
                                            setNewMessageNotification(context, news, "我听");
                                        } else {
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "加入到您所在的对讲组" + ",快去看看吧~";
                                            } else {
                                                news = "加入到对讲组:" + group_name + ",快去看看吧~";
                                            }
                                            setNewMessageNotification(context, news, person_name);
                                        }

                                        // 加入到通知数据库
                                        addNotifyMessage(image_url, person_name, person_id, group_name, group_id, "",
                                                "", "false", "g4", deal_time, 4, 2, 4, message_id, news);
                                    } catch (Exception e) {
                                        Log.e("消息接收服务中G4的异常", e.toString());
                                    }
                                } else if (command2 == 5) {
                                    //当有某人退出组后（包括主动退出和被管理员踢出），向组内成员发送这个消息。
                                    String person_name;
                                    String person_id = null;
                                    String news;
                                    String group_id;
                                    String group_name = null;
                                    String image_url = null;
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);
                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();

                                        UserInfo userinfo = null;
                                        try {
                                            String user_info = arg1.getString("UserInfo");
                                            userinfo = new Gson().fromJson(user_info, new TypeToken<UserInfo>() {
                                            }.getType());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        List<UserInfo> user_list = null;
                                        try {
                                            String _user_list = arg1.getString("UserList");
                                            user_list = new Gson().fromJson(_user_list, new TypeToken<List<UserInfo>>() {
                                            }.getType());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        if (userinfo != null && !userinfo.getUserName().equals("")) {
                                            person_name = userinfo.getUserName();
                                            person_id = userinfo.getUserId();

                                        } else if (user_list != null && user_list.size() > 0) {
                                            StringBuffer login_name = new StringBuffer();
                                            for (int i = 0; i < user_list.size(); i++) {
                                                if (user_list.get(i).getUserName() != null && !user_list.get(i).getUserName().equals("")) {
                                                    login_name.append(user_list.get(i).getUserName());
                                                }
                                            }
                                            person_name = login_name.toString() + ",";
                                            person_name = person_name.substring(0, person_name.length() - 1);
                                        } else {
                                            person_name = "";
                                        }

                                        // 通过group_id获取组的名称跟头像
                                        group_id = data.get("GroupId") + "";
                                        if (group_id != null && !group_id.trim().equals("") && GlobalConfig.list_group != null && GlobalConfig.list_group.size() > 0) {
                                            for (int i = 0; i < GlobalConfig.list_group.size(); i++) {
                                                if (GlobalConfig.list_group.get(i).getGroupId().equals(group_id)) {
                                                    group_name = GlobalConfig.list_group.get(i).getGroupName();
                                                    image_url = GlobalConfig.list_group.get(i).getGroupImg();
                                                }
                                            }
                                        }

                                        String message_id = message.getMsgId();// 消息ID
                                        String deal_time = String.valueOf(message.getSendTime());// 消息处理时间

                                        // 生成notification消息
                                        if (person_name == null || person_name.trim().equals("")) {
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "有人退出您所在的对讲组"+ ",快去看看吧~";
                                            } else {
                                                news = "有人退出对讲组:" + group_name+ ",快去看看吧~";
                                            }
                                            setNewMessageNotification(context, news, "我听");
                                        } else {
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news =   "退出您所在的对讲组"+ ",快去看看吧~";
                                            } else {
                                                news =  "退出对讲组:" + group_name+ ",快去看看吧~";
                                            }
                                            setNewMessageNotification(context, news, person_name);
                                        }

                                        // 加入到通知数据库
                                        addNotifyMessage(image_url, person_name, person_id, group_name, group_id, "",
                                                "", "false", "g5", deal_time, 4, 2, 5, message_id,news);
                                    } catch (Exception e) {
                                        Log.e("消息接收服务中G5的异常", e.toString());
                                    }
                                } else if (command2 == 6) {
                                    //当组被管理员解散后，发送此消息。
                                    MapContent data = (MapContent) message.getMsgContent();
                                    String group_name = null;
                                    String image_url = null;
                                    String group_id = data.get("GroupId") + "";
                                    if (group_id != null && !group_id.trim().equals("") && GlobalConfig.list_group != null && GlobalConfig.list_group.size() > 0) {
                                        for (int i = 0; i < GlobalConfig.list_group.size(); i++) {
                                            if (GlobalConfig.list_group.get(i).getGroupId().equals(group_id)) {
                                                group_name = GlobalConfig.list_group.get(i).getGroupName();
                                                image_url = GlobalConfig.list_group.get(i).getGroupImg();
                                            }
                                        }
                                    }

                                    String message_id = message.getMsgId();// 消息ID
                                    String deal_time = String.valueOf(message.getSendTime());// 消息处理时间

                                    String news;
                                    if (group_name == null || group_name.trim().equals("")) {
                                        news = "有一个您所在的对讲组被解散";
                                    } else {
                                        news = "您所在的对讲组:" + group_name + "被群主解散";
                                    }
                                    setNewMessageNotification(context, news, "我听");

                                    // 加入到通知数据库
                                    addNotifyMessage(image_url, "", "", group_name, group_id, "",
                                            "", "false", "g6", deal_time, 4, 2, 6, message_id,news);

                                    //刷新通讯录
                                    context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                                } else if (command2 == 7) {
                                    //当管理员把某组的权限移交给另一个人时，向组内所有成员发送新管理员Id。
                                    String group_name = null;
                                    String image_url = null;
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);
                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                        String new_admin_info = arg1.getString("NewAdminInfo");
                                        UserInfo userinfo = new Gson().fromJson(new_admin_info, new TypeToken<UserInfo>() {
                                        }.getType());

                                        String group_id = arg1.getString("GroupId");
                                        if (group_id != null && !group_id.trim().equals("") && GlobalConfig.list_group != null && GlobalConfig.list_group.size() > 0) {
                                            for (int i = 0; i < GlobalConfig.list_group.size(); i++) {
                                                if (GlobalConfig.list_group.get(i).getGroupId().equals(group_id)) {
                                                    group_name = GlobalConfig.list_group.get(i).getGroupName();
                                                    image_url = GlobalConfig.list_group.get(i).getGroupImg();
                                                }
                                            }
                                        }
                                        String message_id = message.getMsgId();// 消息ID
                                        String deal_time = String.valueOf(message.getSendTime());// 消息处理时间
                                        String user_name = userinfo.getUserName();
                                        String user_id = userinfo.getUserId();
                                        String news;
                                        if (user_name == null || user_name.trim().equals("")) {
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "有一个您所在的对讲组的群主改变了";
                                            } else {
                                                news = group_name + "的群主改变了";
                                            }
                                        } else {
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "有一个您所在的对讲组的群主变成" + user_name;
                                            } else {
                                                news = user_name + "成为组：" + group_name + "的群主";
                                            }
                                        }
                                        setNewMessageNotification(context, news, "我听");

                                        // 加入到通知数据库
                                        addNotifyMessage(image_url, user_name, user_id, group_name, group_id, "",
                                                "", "false", "g7", deal_time, 4, 2, 7, message_id,news);

                                        //如果管理员权限移交给自己，则需要刷新通讯录
                                        if (user_id != null && !user_id.equals("") && CommonUtils.getUserId(context) != null &&
                                                CommonUtils.getUserId(context).equals(user_id)) {
                                            context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                                        }
                                    } catch (Exception e) {
                                        Log.e("消息接收服务中G7的异常", e.toString());
                                    }
                                } else if (command2 == 8) {
                                    //当管理员审核某个邀请后，只有当拒绝时，才把审核的消息发给邀请者。
                                    String news = null;
                                    String group_name = null;
                                    String image_url = null;
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);
                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();

                                        String group_info = arg1.getString("GroupInfo");
                                        GroupInfo user_info = new Gson().fromJson(group_info, new TypeToken<GroupInfo>() {
                                        }.getType());

                                        String group_id = user_info.getGroupId();
                                        if (group_id != null && !group_id.trim().equals("") && GlobalConfig.list_group != null && GlobalConfig.list_group.size() > 0) {
                                            for (int i = 0; i < GlobalConfig.list_group.size(); i++) {
                                                if (GlobalConfig.list_group.get(i).getGroupId().equals(group_id)) {
                                                    group_name = GlobalConfig.list_group.get(i).getGroupName();
                                                    image_url = GlobalConfig.list_group.get(i).getGroupImg();
                                                }
                                            }
                                        }

                                        //	String inviteuserinfos= arg1.getString("InviteUserInfo");
                                        String be_invite_user_info = arg1.getString("BeInvitedUserInfo");
                                        //	UserInfo inviteuserinfo    = new Gson().fromJson(inviteuserinfos, new TypeToken<UserInfo>() {}.getType());
                                        UserInfo beinviteuserinfo = new Gson().fromJson(be_invite_user_info, new TypeToken<UserInfo>() {
                                        }.getType());
                                        String user_name = beinviteuserinfo.getLoginName();
                                        String user_id = beinviteuserinfo.getUserId();

                                        String deal_time = data.get("InviteTime") + "";
                                        String message_id = message.getMsgId();// 消息ID

                                        if (user_name == null || user_name.trim().equals("")) {
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "您邀请别人进入对讲组的请求被群主拒绝";
                                            } else {
                                                news = "您邀请别人进入" + group_name + "的请求被群主拒绝";
                                            }
                                        } else {
                                            if (group_name == null || group_name.trim().equals("")) {
                                                news = "您邀请" + user_name + "进入对讲组的请求被群主拒绝";
                                            } else {
                                                news = "您邀请" + user_name + "进入" + group_name + "的请求被群主拒绝";
                                            }
                                        }
                                        setNewMessageNotification(context, news, "我听");

                                        // 加入到通知数据库
                                        addNotifyMessage(image_url, user_name, user_id, group_name, group_id, "",
                                                "", "false", "g8", deal_time, 4, 2, 8, message_id,news);
                                    } catch (Exception e) {
                                        Log.e("消息接收服务中G8的异常", e.toString());
                                    }
                                } else if (command2 == 9) {
                                    //当组管理员修改组信息后，向组内所有成员发送更新消息。
                                    String news = null;
                                    String group_id = null;
                                    String group_name = null;
                                    String image_url = null;
                                    try {
                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);
                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();

                                        String group_info = arg1.getString("GroupInfo");
                                        GroupInfo user_info = new Gson().fromJson(group_info, new TypeToken<GroupInfo>() {
                                        }.getType());

                                        group_id = user_info.getGroupId();
                                        if (group_id != null && !group_id.trim().equals("") && GlobalConfig.list_group != null && GlobalConfig.list_group.size() > 0) {
                                            for (int i = 0; i < GlobalConfig.list_group.size(); i++) {
                                                if (GlobalConfig.list_group.get(i).getGroupId().equals(group_id)) {
                                                    group_name = GlobalConfig.list_group.get(i).getGroupName();
                                                    image_url = GlobalConfig.list_group.get(i).getGroupImg();
                                                }
                                            }
                                        }

                                        String message_id = message.getMsgId();// 消息ID
                                        String deal_time = String.valueOf(message.getSendTime());// 消息处理时间

                                        if (group_name == null || group_name.trim().equals("")) {
                                            news = "有一个您所在的对讲组修改了组信息";
                                        } else {
                                            news = "您所在的:" + group_name + "修改了组信息";
                                        }
                                        setNewMessageNotification(context, news, "我听");

                                        // 加入到通知数据库
                                        addNotifyMessage(image_url, "", "", group_name, group_id, "",
                                                "", "false", "g9", deal_time, 4, 2, 8, message_id,news);

                                        //刷新通讯录
                                        context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                                    } catch (Exception e) {
                                        Log.e("消息接收服务中Gb9的异常", e.toString());
                                    }
                                }
                                break;
                            case 3:
//                                int command3 = message.getCommand();
//                                if (command3 == 1) {
//                                    // 订阅消息
//                                    try {
//                                        MapContent data = (MapContent) message.getMsgContent();
//                                        // 添加两个数据库中
//                                    } catch (Exception e) {
//                                        Log.e("消息接收服务中的异常", e.toString());
//                                    }
//                                }
                                break;
                            case 4:// 订阅消息
                                String news;
                                int command4 = message.getCommand();
                                if (command4 == 1) {
                                    try {
                                        String message_id = message.getMsgId();

                                        MapContent data = (MapContent) message.getMsgContent();
                                        Map<String, Object> map = data.getContentMap();
                                        String msg = new Gson().toJson(map);
                                        JSONTokener jsonParser = new JSONTokener(msg);
                                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();

                                        String NewMediaList = arg1.getString("NewMediaList");
                                        List<SeqMediaInfo> MediaList = new Gson().fromJson(NewMediaList, new TypeToken<List<SeqMediaInfo>>() {
                                        }.getType());
                                        SeqMediaInfo _media = MediaList.get(0);
                                        String content_name = _media.getContentName();
                                        String content_id = _media.getContentId();
                                        String deal_time = _media.getContentPubTime();

                                        String seqMediaInfo = arg1.getString("SeqMediaInfo");
                                        SeqMediaInfo seqInfo = new Gson().fromJson(seqMediaInfo, new TypeToken<SeqMediaInfo>() {
                                        }.getType());


                                        String image_url = seqInfo.getContentImg();
                                        String seq_name = seqInfo.getContentName();
                                        String seq_id = seqInfo.getContentId();


                                        if (seq_name == null || seq_name.equals("")) {
                                            news = "您订阅的专辑有新的更新了，快去查看吧";
                                        } else {
                                            news = "您订阅的专辑《" + seq_name + "》有新的更新了，快去查看吧";
                                        }


                                        // 发送通知
                                        setNewMessageNotification(context, news, "订阅更新");
                                        // 加入数据库
                                        addSubscriberMessage(image_url, seq_name, seq_id,
                                                content_name, content_id, deal_time, 4, 4, 1, message_id);
                                    } catch (Exception e) {
                                        Log.e("消息接收服务中的异常", e.toString());
                                    }
                                }
                                break;
                        }
                    }
                    // 如果此时消息中心的界面在打开状态，则发送广播刷新消息中心界面
                    context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESHNEWS));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

//	private void setNewMessageNotification(Context ctx, String msg,String title) {
//		String ns = Context.NOTIFICATION_SERVICE;
//		NotificationManager nm = (NotificationManager) ctx.getSystemService(ns);
//		Intent pushaintent=new Intent("pushnnn");
//		Notification notification = new Notification(R.mipmap.app_logo, msg,System.currentTimeMillis());
//		notification.sound = null;
//		String ringNotify = "content://settings/system/notification_sound";
//		Uri soundUri = TextUtils.isEmpty(ringNotify) ? null : Uri.parse(ringNotify);
//		notification.audioStreamType = AudioManager.STREAM_RING; // tried
//		notification.sound = soundUri;
//		long[] vibrate = new long[] { 1000, 1000 };
//		notification.vibrate = vibrate;
//		notification.defaults=Notification.DEFAULT_LIGHTS;//点亮屏幕
//		PendingIntent in = PendingIntent.getBroadcast(ctx, 2, pushaintent,PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
//		notification.setLatestEventInfo(ctx,title , msg, in);
//		nm.notify(0, notification);
//	}

    private void setNewMessageNotification(Context mContext, String message, String title) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent pushIntent = new Intent(BroadcastConstants.PUSH_NOTIFICATION);
//        Intent pushIntent = new Intent(mContext, NotifyNewActivity.class);
//        PendingIntent in = PendingIntent.getActivity(mContext, 0, pushIntent, 0);
        PendingIntent in = PendingIntent.getBroadcast(mContext, 2, pushIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle(title)// 设置通知栏标题
                .setContentText(message)// 设置通知栏显示内容
                .setContentIntent(in)// 设置通知栏点击意图
                .setWhen(System.currentTimeMillis())// 通知产生时间
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setAutoCancel(true)// 设置点击通知消息时通知栏的通知自动消失
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)// 通知声音、闪灯和振动方式为使用当前的用户默认设置
//		Notification.DEFAULT_VIBRATE //添加默认震动提醒 需要 VIBRATE permission
//		Notification.DEFAULT_SOUND // 添加默认声音提醒
//		Notification.DEFAULT_LIGHTS// 添加默认三色灯提醒
//		Notification.DEFAULT_ALL// 添加默认以上3种全部提醒
                .setSmallIcon(R.mipmap.app_logo);// 设置通知图标
        mNotificationManager.notify(0, mBuilder.build());
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Receiver != null) {
            unregisterReceiver(Receiver);
            Receiver = null;
        }
    }

}