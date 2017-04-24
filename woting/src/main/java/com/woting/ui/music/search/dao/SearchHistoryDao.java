package com.woting.ui.music.search.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.ui.music.search.model.History;
import com.woting.common.database.SQLiteHelper;
import com.woting.common.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对搜索历史表的操作
 * @author 辛龙
 * 2016年1月15日
 */
public class SearchHistoryDao {
	private SQLiteHelper helper;
	private Context context;

	//构造方法
	public SearchHistoryDao(Context contexts) {
		helper = new SQLiteHelper(contexts);
		context=contexts;
	}

	/**
	 * 插入搜索历史表一条数据
	 * @param news
	 */
	public void addHistory(String news) {
		//通过helper的实现对象获取可操作的数据库db
		SQLiteDatabase db = helper.getWritableDatabase();
		String id=CommonUtils.getUserId(context);
		db.execSQL("insert into history(user_id,playName) values(?,?)",
				new Object[] { id, news});//sql语句
		db.close();//关闭数据库对象
	}

	/**
	 * 查询数据库里的数据
	 * @return
	 */
	public List<History> queryHistory() {
		List<History> _m = new ArrayList<History>();
		SQLiteDatabase db = helper.getReadableDatabase();
		String id=CommonUtils.getUserId(context);
		Cursor cursor = null;
		try {
			//执行查询语句 返回一个cursor对象
			cursor = db.rawQuery("Select * from history where user_id like ? order by _id desc ",new String[] { id });
			//循环遍历cursor中储存的键值对
			while (cursor.moveToNext()) {
				//获取表中数据第2列
				String user_id = cursor.getString(1);
				//获取表中数据第3列
				String playName = cursor.getString(2);
				//把每个对象都放到history对象里
				History h = new History(user_id, playName);
				//网_m里储存每个history对象
				_m.add(h);
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
		return _m;
	}

	/**
	 * 删除数据库表中的数据
	 * @return
	 */
	public void historyDeleteOne(String news) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String user_id = CommonUtils.getUserId(context);
		db.execSQL("Delete from history where user_id like ?and playName=?",
				new String[] { user_id ,news});
		db.close();
	}

	/**
	 * 全部删除数据库表中的数据
	 * @return
	 */
	public void historyDeleteAll() {
		SQLiteDatabase db = helper.getReadableDatabase();
		String user_id = CommonUtils.getUserId(context);
		db.execSQL("Delete from history where user_id like ?",
				new String[] {user_id});
		db.close();
	}
}
