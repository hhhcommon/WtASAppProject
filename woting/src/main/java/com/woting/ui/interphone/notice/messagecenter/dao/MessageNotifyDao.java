package com.woting.ui.interphone.notice.messagecenter.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.ui.interphone.linkman.model.DBNotifyHistory;
import com.woting.common.database.SQLiteHelper;
import com.woting.common.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对通知列表的操作
 *
 * @author 辛龙
 *         2016年1月15日
 */
public class MessageNotifyDao {
    private SQLiteHelper helper;
    private Context context;

    //构造方法
    public MessageNotifyDao(Context context) {
        helper = new SQLiteHelper(context);
        this.context = context;
    }

    /**
     * 插入搜索历史表一条数据
     */
    public void addNotifyMessage(DBNotifyHistory message) {
        //通过helper的实现对象获取可操作的数据库db
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into message_notify(user_id,image_url,person_name,person_id," +
                        "group_name,group_id,operator_name,operator_id," +
                        "show_type,message_type,deal_time,add_time," +
                        "biz_type,cmd_type,command,message_id,message) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{
                        message.getUserId(), message.getImageUrl(),
                        message.getPersonName(), message.getPersonId(),
                        message.getGroupName(), message.getGroupId(),
                        message.getOperatorName(), message.getOperatorId(),
                        message.getShowType(), message.getMessageType(), message.getDealTime(),
                        message.getAddTime(), message.getBizType(), message.getCmdType(),
                        message.getCommand(), message.getMessageId(), message.getMessage()
                });//sql语句
        db.close();//关闭数据库对象
    }

    /**
     * 查询数据库里的数据，无参查询语句 供特定使用
     * 踢出展示重复的数据
     */
    public List<DBNotifyHistory> queryNotifyMessageNoOther() {
        List<DBNotifyHistory> my_list = new ArrayList<DBNotifyHistory>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String _user_id = CommonUtils.getUserId(context);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("Select * from message_notify  where user_id=? and (show_type= ? or show_type= ? )order by add_time desc",
                    new String[]{_user_id, "true", "false"});
//            cursor = db.rawQuery("Select * from message_notify  where user_id=? and (show_type <> ?) order by add_time desc",
//                    new String[]{_user_id,"other"});
            while (cursor.moveToNext()) {
                String user_id = cursor.getString(1);
                String image_url = cursor.getString(2);
                String person_name = cursor.getString(3);
                String person_id = cursor.getString(4);
                String group_name = cursor.getString(5);
                String group_id = cursor.getString(6);
                String operator_name = cursor.getString(7);
                String operator_id = cursor.getString(8);
                String show_type = cursor.getString(9);
                String message_type = cursor.getString(10);
                String deal_time = cursor.getString(11);
                String add_time = cursor.getString(12);

                int biz_type = cursor.getInt(13);
                int cmd_type = cursor.getInt(14);
                int command = cursor.getInt(15);
                String message_id = cursor.getString(16);
                String message = cursor.getString(17);
                //把每个对象都放到history对象里
                DBNotifyHistory h = new DBNotifyHistory(user_id, image_url,
                        person_name, person_id, group_name, group_id, operator_name,
                        operator_id, show_type, message_type, deal_time, add_time,
                        biz_type, cmd_type, command, message_id, message);
                my_list.add(h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return my_list;
    }

    /**
     * 查询数据库里的数据
     */
    public List<DBNotifyHistory> queryNotifyMessage() {
        List<DBNotifyHistory> my_list = new ArrayList<DBNotifyHistory>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String _user_id = CommonUtils.getUserId(context);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("Select * from message_notify  where user_id=? order by add_time desc",
                    new String[]{_user_id});
            while (cursor.moveToNext()) {
                String user_id = cursor.getString(1);
                String image_url = cursor.getString(2);
                String person_name = cursor.getString(3);
                String person_id = cursor.getString(4);
                String group_name = cursor.getString(5);
                String group_id = cursor.getString(6);
                String operator_name = cursor.getString(7);
                String operator_id = cursor.getString(8);
                String show_type = cursor.getString(9);
                String message_type = cursor.getString(10);
                String deal_time = cursor.getString(11);
                String add_time = cursor.getString(12);

                int biz_type = cursor.getInt(13);
                int cmd_type = cursor.getInt(14);
                int command = cursor.getInt(15);
                String message_id = cursor.getString(16);
                String message = cursor.getString(17);
                //把每个对象都放到history对象里
                DBNotifyHistory h = new DBNotifyHistory(user_id, image_url,
                        person_name, person_id, group_name, group_id, operator_name,
                        operator_id, show_type, message_type, deal_time, add_time,
                        biz_type, cmd_type, command, message_id, message);
                my_list.add(h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return my_list;
    }

    /**
     * 查询该业务消息的MessageId
     *
     * @param MessageType 业务消息类型
     * @param GroupId     组ID
     * @param PersonId    个人ID
     */
    public List<String> queryNotifyMessageId(String MessageType, String GroupId, String PersonId) {
        List<String> my_list = new ArrayList<String>();
        MessageType = (MessageType == null || MessageType.trim().equals("")) ? "other" : MessageType;
        if (MessageType.equals("p1")) {
            // 个人邀请消息
            PersonId = (PersonId == null || PersonId.trim().equals("")) ? "other" : PersonId;
            if (!PersonId.equals("other")) {

                SQLiteDatabase db = helper.getReadableDatabase();
                String _user_id = CommonUtils.getUserId(context);
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery("Select * from message_notify  where message_type=? and person_id=?and user_id=? ",
                            new String[]{MessageType, PersonId, _user_id});
                    while (cursor.moveToNext()) {
                        String message_id = cursor.getString(16);
                        my_list.add(message_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    if (db != null) {
                        db.close();
                    }
                }
            }
        } else if (MessageType.equals("g1")) {
            // 组邀请消息
            GroupId = (GroupId == null || GroupId.trim().equals("")) ? "other" : GroupId;
            if (!GroupId.equals("other")) {
                SQLiteDatabase db = helper.getReadableDatabase();
                String _user_id = CommonUtils.getUserId(context);
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery("Select * from message_notify  where message_type=? and group_id=?and user_id=?  ",
                            new String[]{MessageType, GroupId, _user_id});
                    while (cursor.moveToNext()) {
                        String message_id = cursor.getString(16);
                        my_list.add(message_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    if (db != null) {
                        db.close();
                    }
                }
            }
        } else if (MessageType.equals("g2")) {
            // 申请入组消息
            GroupId = (GroupId == null || GroupId.trim().equals("")) ? "other" : GroupId;
            PersonId = (PersonId == null || PersonId.trim().equals("")) ? "other" : PersonId;
            if (!GroupId.equals("other") && !PersonId.equals("other")) {
                SQLiteDatabase db = helper.getReadableDatabase();
                String _user_id = CommonUtils.getUserId(context);
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery("Select * from message_notify  where message_type=? and group_id=?and person_id=?and user_id=?  ",
                            new String[]{MessageType, GroupId, PersonId, _user_id});
                    while (cursor.moveToNext()) {
                        String message_id = cursor.getString(16);
                        my_list.add(message_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    if (db != null) {
                        db.close();
                    }
                }
            }
        }
        return my_list;
    }

    /**
     * 删除数据库表中的数据,
     *
     * @param MessageId 消息ID
     */
    public void deleteNotifyMessage(String MessageId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String user_id = CommonUtils.getUserId(context);
        db.execSQL("Delete from message_notify where message_id=? and user_id=?",
                new String[]{MessageId, user_id});
        db.close();
    }

    /**
     * 得到该reMessageId相同的数据
     *
     * @param msgId ID
     */
    public List<DBNotifyHistory> queryNotifyMessageForMessageId(String msgId) {
        List<DBNotifyHistory> my_list = new ArrayList<DBNotifyHistory>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String _user_id = CommonUtils.getUserId(context);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("Select * from message_notify  where user_id=? and message_id=? order by add_time desc",
                    new String[]{_user_id, msgId});
            while (cursor.moveToNext()) {
                String user_id = cursor.getString(1);
                String image_url = cursor.getString(2);
                String person_name = cursor.getString(3);
                String person_id = cursor.getString(4);
                String group_name = cursor.getString(5);
                String group_id = cursor.getString(6);
                String operator_name = cursor.getString(7);
                String operator_id = cursor.getString(8);
                String show_type = cursor.getString(9);
                String message_type = cursor.getString(10);
                String deal_time = cursor.getString(11);
                String add_time = cursor.getString(12);

                int biz_type = cursor.getInt(13);
                int cmd_type = cursor.getInt(14);
                int command = cursor.getInt(15);
                String message_id = cursor.getString(16);
                String message = cursor.getString(17);
                //把每个对象都放到history对象里
                DBNotifyHistory h = new DBNotifyHistory(user_id, image_url,
                        person_name, person_id, group_name, group_id, operator_name,
                        operator_id, show_type, message_type, deal_time, add_time,
                        biz_type, cmd_type, command, message_id, message);
                my_list.add(h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return my_list;
    }

    /**
     * 删除数据库表中的好友邀请业务重复数据,
     *
     * @param personId ID
     */
    public void deleteNotifyMessageForInvite(String personId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String user_id = CommonUtils.getUserId(context);
        db.execSQL("Delete from message_notify where message_type=? and user_id=? and person_id =?",
                new String[]{"p1", user_id, personId});
        db.close();
    }

    /**
     * 删除数据库中群组邀请消息
     *
     * @param type
     * @param group_id
     * @param person_id
     */
    public void deleteNotifyMessageForGroupInvite(String type, String group_id, String person_id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String user_id = CommonUtils.getUserId(context);
        db.execSQL("Delete from message_notify where message_type=? and user_id=? and group_id=? and person_id =?",
                new String[]{type, user_id, group_id, person_id});
        db.close();
    }

    /**
     * 更改展示状态，把true变成false
     *
     * @param MessageId 消息ID
     */
    public void upDataNotifyMessage(String MessageId, String message) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String user_id = CommonUtils.getUserId(context);
        db.execSQL("update message_notify set show_type=?,message=? where message_id=?and user_id=? and show_type=?",
                new Object[]{"false", message, MessageId, user_id, "true"});
        db.close();
    }

    /**
     * 删除该业务消息重复数据
     *
     * @param MessageType 业务消息类型
     * @param GroupId     组ID
     * @param PersonId    个人ID
     */
    public void upDataNotifyForDuplicate(String MessageType, String GroupId, String PersonId) {
        MessageType = MessageType == null || MessageType.trim().equals("") ? "other" : MessageType;
        if (MessageType.equals("p1")) {
            // 个人邀请消息
            PersonId = PersonId == null || PersonId.trim().equals("") ? "other" : PersonId;
            if (!PersonId.equals("other")) {
                SQLiteDatabase db = helper.getReadableDatabase();
                String user_id = CommonUtils.getUserId(context);
                db.execSQL("update message_notify set show_type=? where message_type=? and person_id=?and user_id=?",
                        new String[]{"other", MessageType, PersonId, user_id});
                db.close();
            }
        } else if (MessageType.equals("g1")) {
            // 组邀请消息
            GroupId = GroupId == null || GroupId.trim().equals("") ? "other" : GroupId;
            PersonId = PersonId == null || PersonId.trim().equals("") ? "other" : PersonId;
            if (!GroupId.equals("other") && !PersonId.equals("other")) {
                SQLiteDatabase db = helper.getReadableDatabase();
                String user_id = CommonUtils.getUserId(context);
                db.execSQL("update message_notify set show_type=? where message_type=? and group_id=?and person_id=?and user_id=?",
                        new String[]{"other", MessageType, GroupId, PersonId, user_id});
                db.close();
            }
        } else if (MessageType.equals("g2")) {
            // 用户申请消息
            GroupId = GroupId == null || GroupId.trim().equals("") ? "other" : GroupId;
            PersonId = PersonId == null || PersonId.trim().equals("") ? "other" : PersonId;
            if (!GroupId.equals("other") && !PersonId.equals("other")) {
                SQLiteDatabase db = helper.getReadableDatabase();
                String user_id = CommonUtils.getUserId(context);
                db.execSQL("update message_notify set show_type=? where message_type=? and group_id=?and person_id=?and user_id=?",
                        new String[]{"other", MessageType, GroupId, PersonId, user_id});
                db.close();
            }
        }


    }
}
