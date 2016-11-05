package com.woting.activity.home.player.main.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.activity.home.player.main.model.PlayerHistory;
import com.woting.common.database.SqliteHelper;
import com.woting.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对播放历史表的操作
 * @author 辛龙
 *2016年1月15日
 */
public class SearchPlayerHistoryDao {
	private SqliteHelper helper;
	private Context context;

	//构造方法
	public SearchPlayerHistoryDao(Context context) {
		helper = new SqliteHelper(context);
		this.context=context;
	}

	/**
	 * 插入播放历史表一条数据
	 */
	public void addHistory(PlayerHistory playerhistory) {
		//通过helper的实现对象获取可操作的数据库db
		SQLiteDatabase db = helper.getWritableDatabase();
		String s=playerhistory.getPlayerFrom();
		db.execSQL("insert into playerhistory(playername,playerimage,playerurl,playerurI,playermediatype,playeralltime"
						+ ",playerintime,playercontentdesc,playernum,playerzantype,playerfrom,playerfromid,playeraddtime,bjuserid,playshareurl,playfavorite,contentid,localurl,sequname,sequimg,sequdesc,sequid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				new Object[] { playerhistory.getPlayerName(), playerhistory.getPlayerImage()
						, playerhistory.getPlayerUrl(),playerhistory.getPlayerUrI()
						,playerhistory.getPlayerMediaType()
						, playerhistory.getPlayerAllTime(), playerhistory.getPlayerInTime()//这里
						, playerhistory.getPlayerContentDesc(), playerhistory.getPlayerNum()
						, playerhistory.getPlayerZanType(), playerhistory.getPlayerFrom(), playerhistory.getPlayerFromId(), playerhistory.getPlayerAddTime()
						, playerhistory.getBJUserid(),playerhistory.getPlayContentShareUrl()
						,playerhistory.getContentFavorite(),playerhistory.getContentID(),playerhistory.getLocalurl()
						, playerhistory.getSequName(),playerhistory.getSequImg(),playerhistory.getSequDesc()
						,playerhistory.getSequId()});//sql语句
		db.close();//关闭数据库对象
	}

	/**
	 * 查询数据库里的数据
	 * @return
	 */
	public List<PlayerHistory> queryHistory(PlayerHistory playerhistory) {
		List<PlayerHistory> myList = new ArrayList<PlayerHistory>();
		SQLiteDatabase db = helper.getReadableDatabase();
		String s = playerhistory.getPlayerUrl();
		Cursor cursor = null;
		try {
			//执行查询语句 返回一个cursor对象
			cursor = db.rawQuery("Select * from playerhistory where playerurl =? order by _id desc ",
					new String[] { s });
			//循环遍历cursor中储存的键值对
			while (cursor.moveToNext()) {
				String playername = cursor.getString(cursor.getColumnIndex("playername"));
				String playerimage =cursor.getString(cursor.getColumnIndex("playerimage"));
				String playerurl = cursor.getString(cursor.getColumnIndex("playerurl"));
				String playerurI= cursor.getString(cursor.getColumnIndex("playerurI"));
				String playermediatype = cursor.getString(cursor.getColumnIndex("playermediatype"));
				String playeralltime =cursor.getString(cursor.getColumnIndex("playeralltime"));
				String playerintime =cursor.getString(cursor.getColumnIndex("playerintime "));
				String playercontentdesc =cursor.getString(cursor.getColumnIndex("playercontentdesc"));
				String playernum = cursor.getString(cursor.getColumnIndex("playernum"));
				String playerzantype = cursor.getString(cursor.getColumnIndex("playerzantype"));
				String playerfrom = cursor.getString(cursor.getColumnIndex("playerfrom"));
				String playerfromid =cursor.getString(cursor.getColumnIndex("playerfromid"));
				String playerfromurl =cursor.getString(cursor.getColumnIndex("playerfromurl"));
				String playeraddtime =cursor.getString(cursor.getColumnIndex("playeraddtime"));
				String bjuserid =cursor.getString(cursor.getColumnIndex("bjuserid"));
				String playcontentshareurl =cursor.getString(cursor.getColumnIndex("playshareurl"));
				String ContentFavorite =cursor.getString(cursor.getColumnIndex("playfavorite"));
				String ContentID=cursor.getString(cursor.getColumnIndex("contentid"));
				String localurl=cursor.getString(cursor.getColumnIndex("localurl"));
				String sequname=cursor.getString(cursor.getColumnIndex("sequname"));
				String sequid=cursor.getString(cursor.getColumnIndex("sequid"));
				String sequdesc=cursor.getString(cursor.getColumnIndex("sequdesc"));
				String sequimg=cursor.getString(cursor.getColumnIndex("sequimg"));


				PlayerHistory h = new PlayerHistory(playername, playerimage, playerurl,playerurI, playermediatype, playeralltime,
						playerintime, playercontentdesc, playernum, playerzantype, playerfrom, playerfromid,playerfromurl,playeraddtime,bjuserid,playcontentshareurl,ContentFavorite,ContentID
						,localurl,sequname,sequid,sequdesc,sequimg);


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
	 * 查询数据库里的数据，无参查询语句 供特定使用
	 */
	public List<PlayerHistory> queryHistory() {
		List<PlayerHistory> mylist = new ArrayList<PlayerHistory>();
		SQLiteDatabase db = helper.getReadableDatabase();
		String userid = CommonUtils.getUserId(context);
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("Select * from playerhistory where bjuserid =? order by playeraddtime desc ",  new String[]{userid});
			while (cursor.moveToNext()) {
				String playername = cursor.getString(1);
				String playerimage = cursor.getString(2);
				String playerurl = cursor.getString(3);
				String playerurI= cursor.getString(4);//iiiii
				String playermediatype = cursor.getString(5);
				String playeralltime =cursor.getString(cursor.getColumnIndex("playeralltime"));
				String playerintime =cursor.getString(cursor.getColumnIndex("playerintime"));
				String playercontentdesc = cursor.getString(8);
				String playernum = cursor.getString(9);
				String playerzantype = cursor.getString(10);
				String playerfrom = cursor.getString(11);
				String playerfromid = cursor.getString(12);
				String playerfromurl = cursor.getString(13);
				String playeraddtime = cursor.getString(14);
				String bjuserid = cursor.getString(15);
				String playcontentshareurl = cursor.getString(16);
				String ContentFavorite=cursor.getString(17);
				String ContentID=cursor.getString(18);
				String localurl=cursor.getString(19);
				String sequname=cursor.getString(cursor.getColumnIndex("sequname"));
				String sequid=cursor.getString(cursor.getColumnIndex("sequid"));
				String sequdesc=cursor.getString(cursor.getColumnIndex("sequdesc"));
				String sequimg=cursor.getString(cursor.getColumnIndex("sequimg"));
				PlayerHistory h = new PlayerHistory(playername, playerimage, playerurl,playerurI, playermediatype, playeralltime,
						playerintime, playercontentdesc, playernum, playerzantype, playerfrom, playerfromid,playerfromurl,playeraddtime,bjuserid,playcontentshareurl,ContentFavorite,ContentID
						,localurl,sequname,sequid,sequdesc,sequimg);
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
	 */

	public List<PlayerHistory> queryHistoryNoUserId() {
		List<PlayerHistory> mylist = new ArrayList<PlayerHistory>();
		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor cursor = null;
		try {
			cursor = db.rawQuery("Select * from playerhistory  order by playeraddtime desc ",null);
			while (cursor.moveToNext()) {
				String playername = cursor.getString(1);
				String playerimage = cursor.getString(2);
				String playerurl = cursor.getString(3);
				String playerurI= cursor.getString(4);//iiiii
				String playermediatype = cursor.getString(5);
				String playeralltime = cursor.getString(6);
				String playerintime = cursor.getString(7);
				String playercontentdesc = cursor.getString(8);
				String playernum = cursor.getString(9);
				String playerzantype = cursor.getString(10);
				String playerfrom = cursor.getString(11);
				String playerfromid = cursor.getString(12);
				String playerfromurl = cursor.getString(13);
				String playeraddtime = cursor.getString(14);
				String bjuserid = cursor.getString(15);
				String playcontentshareurl = cursor.getString(16);
				String ContentFavorite=cursor.getString(17);
				String ContentID=cursor.getString(18);
				String localurl=cursor.getString(19);
				String sequname=cursor.getString(cursor.getColumnIndex("sequname"));
				String sequid=cursor.getString(cursor.getColumnIndex("sequid"));
				String sequdesc=cursor.getString(cursor.getColumnIndex("sequdesc"));
				String sequimg=cursor.getString(cursor.getColumnIndex("sequimg"));
				PlayerHistory h = new PlayerHistory(playername, playerimage, playerurl,playerurI, playermediatype, playeralltime,
						playerintime, playercontentdesc, playernum, playerzantype, playerfrom, playerfromid,playerfromurl,playeraddtime,bjuserid,playcontentshareurl,ContentFavorite,ContentID
						,localurl,sequname,sequid,sequdesc,sequimg);
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
	 * 通过url删除数据库表中的数据
	 */
	public void deleteHistory(String url) {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.execSQL("Delete from playerhistory where playerurl like ?",new String[] { url });
		db.close();
	}

	/**
	 * 根据 contentid 删除数据库表中的数据
	 */
	public void deleteHistoryById(String id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.execSQL("Delete from playerhistory where contentid like ?",new String[] { id });
		db.close();
	}

	public void deleteHistoryAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		db.execSQL("Delete from playerhistory");
		db.close();
	}


	/**
	 * 修改某一个字段的数据
	 * @param url
	 * @param updatetype
	 * @param date
	 */
	public void updatefileinfo(String url,String updatetype,String date ) {
		SQLiteDatabase	db = helper.getWritableDatabase();
		db.execSQL("update playerhistory set "+updatetype+"=? where playerurl=?",
				new Object[] { date,url });
		db.close();
	}
	/**
	 * 修改数据库当中某个单体节目的喜欢类型
	 */
	public void updateFavorite(String url,String contentFavorite) {
		SQLiteDatabase	db = helper.getWritableDatabase();
		String userid=CommonUtils.getUserId(context);
		if(userid!=null&&!userid.equals("")){
			db.execSQL("update playerhistory set playfavorite=? where bjuserid=? and playerurl=?",new Object[]{contentFavorite,userid,url});
		}else{
			db.execSQL("update playerhistory set playfavorite=? where playerurl=?",new Object[]{contentFavorite,url});
		}
		db.close();
	}

	/**
	 * 修改数据库当中某个单体节目的喜欢类型
	 */
	public void updatePlayerInTime(String url,long CurrentTimes,long TotalTimes) {
		SQLiteDatabase	db = helper.getWritableDatabase();
		String userid=CommonUtils.getUserId(context);
		if(CurrentTimes>0&&TotalTimes>0){
		if(userid!=null&&!userid.equals("")){
			db.execSQL("update playerhistory set playerintime=? , playeralltime=? where bjuserid=? and playerurl=?",new Object[]{CurrentTimes,TotalTimes,userid,url});
		}else{
			db.execSQL("update playerhistory set playerintime=? ,playeralltime=? where playerurl=?",new Object[]{CurrentTimes,TotalTimes,url});
		}
		}else{
			db.execSQL("update playerhistory set playerintime=?,playeralltime=? where bjuserid=? and playerurl=?",new Object[]{"0","0",userid,url});
		}
		db.close();
	}


	/**
	 * 关闭目前打开的所有数据库对象
	 */
	public void closedb(){
		helper.close();
	}
}
