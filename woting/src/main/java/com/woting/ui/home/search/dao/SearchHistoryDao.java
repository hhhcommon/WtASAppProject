package com.woting.ui.home.search.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.ui.home.search.model.History;
import com.woting.common.database.SQLitesHelper;
import com.woting.common.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对搜索历史表的操作
 * @author 辛龙
 * 2016年1月15日
 */
public class SearchHistoryDao {
	private SQLitesHelper helper;
	private Context context;

	//构造方法
	public SearchHistoryDao(Context contexts) {
		helper = new SQLitesHelper(contexts);
		context=contexts;
	}

	/**
	 * 插入搜索历史表一条数据
	 * @param history
	 */
	public void addHistory(History history) {
		//通过helper的实现对象获取可操作的数据库db
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into history(userid,playname) values(?,?)",
				new Object[] { history.getUserId(), history.getPlayName()});//sql语句
		db.close();//关闭数据库对象
	}

	/**
	 * 查询数据库里的数据
	 * @param history
	 * @return
	 */
	public List<History> queryHistory(History history) {
		List<History> mylist = new ArrayList<History>();
		SQLiteDatabase db = helper.getReadableDatabase();
		String s = history.getUserId();
		Cursor cursor = null;
		try {
			//执行查询语句 返回一个cursor对象
			cursor = db.rawQuery("Select * from history where userid like ? ",
					new String[] { s });
			//循环遍历cursor中储存的键值对
			while (cursor.moveToNext()) {
				//获取表中数据第2列
				String userid = cursor.getString(1);
				//获取表中数据第3列
				String playname = cursor.getString(2);
				//把每个对象都放到history对象里
				History h = new History(userid, playname);			
				//网mylist里储存每个history对象
				mylist.add(h);
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
		return mylist;
	}

	/**
	 * 查询数据库里的数据，无参查询语句 供特定使用
	 * @return
	 */
	public List<History> queryHistory() {
		List<History> mylist = new ArrayList<History>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("Select * from history ", null);
			while (cursor.moveToNext()) {
				String userid = cursor.getString(1);
				String playname = cursor.getString(2);
				History h = new History(userid, playname);
				mylist.add(h);
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
		return mylist;
	}

	/**
	 * 删除数据库表中的数据
	 * @return
	 */
	public void deleteHistory(String news) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String userid = CommonUtils.getUserId(context);
		db.execSQL("Delete from history where userid like ?and playname=?",
				new String[] { userid ,news});
		db.close();
	}
	/**
	 * 全部删除数据库表中的数据
	 * @return
	 */
	public void deleteHistoryall(String news) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String userid = CommonUtils.getUserId(context);
		db.execSQL("Delete from history where userid like ?",
				new String[] {userid});
		db.close();
	}
}
