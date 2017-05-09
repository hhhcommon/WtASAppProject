package com.woting.ui.interphone.chat.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.ui.interphone.chat.model.DBTalkHistory;
import com.woting.common.database.SQLiteHelper;
import com.woting.common.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对聊天历史列表的操作
 * @author 辛龙
 *2016年1月15日
 */
public class SearchTalkHistoryDao {
	private SQLiteHelper helper;
	private Context context;

	//构造方法
	public SearchTalkHistoryDao(Context context) {
		helper = new SQLiteHelper(context);
		this.context=context;
	}

	/**
	 * 插入搜索历史表一条数据
	 * @param history
	 */
	public void addTalkHistory(DBTalkHistory history) {
		//通过helper的实现对象获取可操作的数据库db
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into talkHistory(bjUserId,type,id,addTime) values(?,?,?,?)",
				new Object[] {history.getBJUserId(),history.getTyPe(), history.getID(),history.getAddTime()});//sql语句
		db.close();//关闭数据库对象
	}


	/**
	 * 查询数据库里的数据，无参查询语句 供特定使用
	 * @return
	 */
	public List<DBTalkHistory> queryHistory() {
		List<DBTalkHistory> myList = new ArrayList<DBTalkHistory>();
		SQLiteDatabase db = helper.getReadableDatabase();
		String userId = CommonUtils.getUserId(context);
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("Select * from talkHistory  where bjUserId=? order by addTime desc", new String[]{userId});
			while (cursor.moveToNext()) {
				String bjUserId = cursor.getString(1);
				String type = cursor.getString(2);
				String id = cursor.getString(3);
				String addTime = cursor.getString(4);
				//把每个对象都放到history对象里
				DBTalkHistory h = new DBTalkHistory(bjUserId,  type,  id, addTime);
				myList.add(h);
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
		return myList;
	}

	/**
	 * 删除数据库表中的数据
	 * @param id
	 * @return
	 */
	public void deleteHistory(String id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String userId = CommonUtils.getUserId(context);
		String uid = id;
		db.execSQL("Delete from talkHistory where id=? and bjUserId=?",
				new String[] { uid ,userId});
		db.close();
	}
}
