package com.woting.ui.interphone.notice.messagecenter.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.common.database.SQLiteHelper;
import com.woting.common.util.CommonUtils;
import com.woting.ui.interphone.linkman.model.DBNotifyHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * 对系统消息列表的操作
 *
 * @author 辛龙
 *         2016年1月15日
 */
public class MessageSystemDao {
    private SQLiteHelper helper;
    private Context context;

    //构造方法
    public MessageSystemDao(Context context) {
        helper = new SQLiteHelper(context);
        this.context = context;
    }

    /**
     * 插入搜索历史表一条数据
     */
    public void addSystemNews(DBNotifyHistory message) {
        //通过helper的实现对象获取可操作的数据库db
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into message_system(user_id,image_url,person_name,person_id," +
                        "group_name,group_id,operator_name,operator_id," +
                        "show_type,message_type,deal_time,add_time," +
                        "biz_type,cmd_type,command,message_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
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
     */
    public List<DBNotifyHistory> querySystemNews() {
        List<DBNotifyHistory> my_list = new ArrayList<DBNotifyHistory>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String _user_id = CommonUtils.getUserId(context);
        Cursor cursor = null;
        try {
//            cursor = db.rawQuery("Select * from message_notify  where bjuserid=? and showtype=? order by addtime desc", new String[]{userid,"true"});
            cursor = db.rawQuery("Select * from message_system  where user_id=?  order by add_time desc", new String[]{_user_id});
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
                String message= cursor.getString(17);

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
     * 删除数据库表中的数据,
     *
     * @param MessageId 消息ID
     */
    public void deleteSystemNews(String MessageId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String user_id = CommonUtils.getUserId(context);
        db.execSQL("Delete from message_system where message_id=? and user_id=?",
                new String[]{MessageId, user_id});
        db.close();
    }

    /**
     * 更改展示状态，把true变成false
     *
     * @param MessageId 消息ID
     */
    public void updataSystemNews(String MessageId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update message_system set show_type=? where message_id=?",
                new Object[]{"false", MessageId});
        db.close();
    }
}




