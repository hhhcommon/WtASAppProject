package com.woting.common.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.woting.common.config.GlobalConfig;

/**
 * 创建数据库表
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	public SQLiteHelper(Context paramContext) {
		super(paramContext, "woting.db", null, GlobalConfig.dbVersionCode);
	}

	public void onCreate(SQLiteDatabase db) {
		// 搜索历史表
		db.execSQL("CREATE TABLE IF NOT EXISTS history(_id Integer primary key autoincrement, "
				+ "userid varchar(50),playname varchar(50))");
		// talkhistory对讲历史，暂缺对讲结束时间
		//bjuserid用户id    type对讲类型group，person   id对讲id  addtime对讲开始时间
		db.execSQL("CREATE TABLE IF NOT EXISTS talkhistory(_id Integer primary key autoincrement, "
				+ "bjuserid varchar(50),type varchar(50),id varchar(50),addtime varchar(50))");
		//notifyhistory消息通知表
		db.execSQL("CREATE TABLE IF NOT EXISTS notifyhistory(_id Integer primary key autoincrement, "
				+ "bjuserid varchar(50),type varchar(50),imageurl varchar(100),content varchar(100),"
				+ "title varchar(50),dealtime varchar(50),addtime varchar(50)," +
				"showtype varchar(10),biztype varchar(10),cmdtype varchar(10),command varchar(10),taskid varchar(50))");
		// player播放历史,暂时缺本机userid
		//		PlayerName播放显示名称PlayerImage播放显示图片PlayerUrl播放路径PlayerMediaType播放类型，radio，audio，seq,PlayerAllTime播放文件总时长
		//		PlayerInTime此时播放时长PlayerContentDesc播放文件介绍PlayerNum播放次数PlayerZanTypeString类型的true,false
		//		PlayerFrom预留字段PlayerFromId预留字段
		db.execSQL("CREATE TABLE IF NOT EXISTS playerhistory(_id Integer primary key autoincrement, "
				+ "playername varchar(50),playerimage varchar(50),playerurl varchar(50),playerurI varchar(5000),playermediatype varchar(50),"
				+ "playeralltime varchar(50),playerintime varchar(50),playercontentdesc varchar(1500),playernum varchar(50),"
				+ "playerzantype varchar(50),playerfrom varchar(50),playerfromid varchar(50),playerfromurl varchar(50)," +
				"playeraddtime varchar(50),bjuserid varchar(50),playshareurl varchar(100),playfavorite varchar(100),contentid varchar(50),localurl varchar(100)," +
				"sequname varchar(50),sequimg varchar(200),sequdesc varchar(150),sequid varchar(50),playtag varchar(100),contentplaytype varchar(20))");
		//playtag,contentplaytype
		// 线程表
		db.execSQL("create table IF NOT EXISTS thread_info(_id integer primary key autoincrement,"
				+ "thread_id integer, url varchar(100), start integer, end integer, finished integer)");
		// 文件数据
		db.execSQL("create table IF NOT EXISTS fileinfo(_id integer primary key autoincrement,"
				+ "start integer,end integer,url varchar(200),imageurl varchar(200), finished varchar(10),"
				+ "author varchar(50),playcontent varchar(50),filename varchar(50),localurl varchar(100),"
				+ "sequname varchar(50),sequimgurl varchar(200),sequdesc varchar(150),sequid varchar(50)," +
				"userid varchar(50),downloadtype varchar(10),playshareurl varchar(100),playfavorite varchar(100),contentid varchar(50)," +
				"playeralltime varchar(50),playerfrom varchar(50),playcount varchar(50),contentdescn varchar(100),playtag varchar(100),contentplaytype varchar(20))");

		// 城市表
		db.execSQL("create table IF NOT EXISTS cityinfo(_id integer primary key autoincrement,"
				+ "adcode varchar(20), cityname varchar(50))");
		// 专辑表
		//		db.execSQL("create table IF NOT EXISTS sequinfo(_id integer primary key autoincrement,"
		//				+ "sequimgurl varchar(200),sequdesc varchar(150),sequname varchar(50))");
		// 记录的电台
		//		db.execSQL("CREATE TABLE IF NOT EXISTS fmhistory(_id Integer primary key autoincrement, "
		//				+ "userid varchar(50),auther varchar(50),name varchar(50),image varchar(50),url varchar(50),"
		//				+ "content varchar(50),bftype varchar(50),addtime varchar(50))");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS history");
		db.execSQL("DROP TABLE IF EXISTS talkhistory");
		db.execSQL("DROP TABLE IF EXISTS notifyhistory");
		db.execSQL("DROP TABLE IF EXISTS thread_info");
		db.execSQL("DROP TABLE IF EXISTS fileinfo");
		db.execSQL("DROP TABLE IF EXISTS playerhistory");
		db.execSQL("DROP TABLE IF EXISTS cityinfo");
		//		db.execSQL("DROP TABLE IF EXISTS fmhistory");
		onCreate(db);
	}
}