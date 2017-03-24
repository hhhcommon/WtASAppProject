package com.woting.ui.interphone.message.messagecenter.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.woting.common.database.SQLiteHelper;
import com.woting.common.util.CommonUtils;
import com.woting.ui.interphone.message.messagecenter.model.DBSubscriberMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * 对组通知消息列表的操作
 *
 * @author 辛龙
 *         2016年1月15日
 */
public class MessageSubscriberDao {
    private SQLiteHelper helper;
    private Context context;

    //构造方法
    public MessageSubscriberDao(Context context) {
        helper = new SQLiteHelper(context);
        this.context = context;
    }

    /**
     * 插入搜索历史表一条数据
     */
    public void addSubscriberMessage(DBSubscriberMessage message) {
        //通过helper的实现对象获取可操作的数据库db
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into message_subscriber(user_id,image_url,seq_name,seq_id,content_name,content_id,deal_time,add_time,biz_type,cmd_type,command,message_id) values(?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{
                        message.getUserId(), message.getImageUrl(),
                        message.getSeqName(), message.getSeqId(),
                        message.getContentName(), message.getContentId(), message.getDealTime(),
                        message.getAddTime(), message.getBizType(), message.getCmdType(),
                        message.getCommand(), message.getMessageId()
                });//sql语句
        db.close();//关闭数据库对象
    }


    /**
     * 查询数据库里的数据，无参查询语句 供特定使用
     */
    public List<DBSubscriberMessage> querySubscriberMessage() {
        List<DBSubscriberMessage> _list = new ArrayList<DBSubscriberMessage>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String _user_id = CommonUtils.getUserId(context);// 本机的ID
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("Select * from message_subscriber  where user_id=? order by add_time desc", new String[]{_user_id});
            while (cursor.moveToNext()) {
                String user_id = cursor.getString(1);
                String image_url = cursor.getString(2);
                String seq_name = cursor.getString(3);
                String seq_id = cursor.getString(4);
                String content_name = cursor.getString(5);
                String content_id = cursor.getString(6);
                String deal_time = cursor.getString(7);
                String add_time = cursor.getString(8);
                int biz_type = cursor.getInt(9);
                int cmd_type = cursor.getInt(10);
                int command = cursor.getInt(11);
                String message_id = cursor.getString(12);

                //把每个对象都放到history对象里
                DBSubscriberMessage h = new DBSubscriberMessage(user_id, image_url, seq_name, seq_id,
                        content_name, content_id, deal_time, add_time,biz_type,cmd_type,command,message_id);
                _list.add(h);
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
        return _list;
    }

    /**
     * 删除数据库表中的数据,专辑ID是唯一标示（seq_id）
     */
    public void deleteSubscriberMessage(String seq_id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String userId = CommonUtils.getUserId(context);
        db.execSQL("Delete from message_subscriber where seq_id=? and user_id=?",
                new String[]{seq_id, userId});
        db.close();
    }
}
