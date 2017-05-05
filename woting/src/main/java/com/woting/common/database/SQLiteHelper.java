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

        // talkhistory对讲历史，暂缺对讲结束时间
        //bjuserid用户id    type对讲类型group，person   id对讲id  addtime对讲开始时间
        db.execSQL("CREATE TABLE IF NOT EXISTS talkHistory(_id Integer primary key autoincrement, "
                + "bjUserId varchar(50),type varchar(50),id varchar(50),addTime varchar(50))");

        // 线程表
        db.execSQL("create table IF NOT EXISTS thread_info(_id integer primary key autoincrement,"
                + "thread_id integer, url varchar(300), start integer, end integer, finished integer)");
        // 文件数据
        db.execSQL("create table IF NOT EXISTS fileInfo(_id integer primary key autoincrement,"
                + "start integer,end integer,url varchar(200),imageUrl varchar(300), finished integer,"
                + "author varchar(50),playContent varchar(50),fileName varchar(50),localUrl varchar(300),"
                + "albumName varchar(50),albumImgUrl varchar(300),albumDesc varchar(2000),albumId varchar(50)," +
                "userId varchar(50),downloadType varchar(10),playShareUrl varchar(100),playFavorite varchar(100),contentId varchar(50)," +
                "playAllTime varchar(50),playFrom varchar(50),playCount varchar(50),contentDesc varchar(2000),playTag varchar(100),contentPlayType varchar(20),IsPlaying varchar(50),ColumnNum varchar(100))");

        // 城市表
        db.execSQL("create table IF NOT EXISTS cityInfo(_id integer primary key autoincrement,"
                + "adCode varchar(20), cityName varchar(50))");


        // 通知消息库表
        db.execSQL("CREATE TABLE IF NOT EXISTS message_notify(_id Integer primary key autoincrement, "
                + "user_id varchar(50),image_url varchar(300),person_name varchar(50),person_id varchar(50),"
                + "group_name varchar(50),group_id varchar(50),operator_name varchar(50),operator_id varchar(50),"
                + "show_type varchar(50),message_type varchar(50),deal_time varchar(50),add_time varchar(50),"
                + "biz_type varchar(10),cmd_type varchar(10),command varchar(10),message_id varchar(50),message varchar(50))");

        // 系统消息库表
        db.execSQL("CREATE TABLE IF NOT EXISTS message_system(_id Integer primary key autoincrement, "
                + "user_id varchar(50),image_url varchar(300),person_name varchar(50),person_id varchar(50),"
                + "group_name varchar(50),group_id varchar(50),operator_name varchar(50),operator_id varchar(50),"
                + "show_type varchar(50),message_type varchar(50),deal_time varchar(50),add_time varchar(50),"
                + "biz_type varchar(10),cmd_type varchar(10),command varchar(10),message_id varchar(50),message varchar(50))");

        // 订阅消息
        db.execSQL("CREATE TABLE IF NOT EXISTS message_subscriber(_id Integer primary key autoincrement, "
                + "user_id varchar(50),image_url varchar(300),seq_name varchar(100),"
                + "seq_id varchar(50),content_name varchar(100),content_id varchar(50),"
                + "deal_time varchar(50),add_time varchar(50)," +
                "biz_type varchar(10),cmd_type varchar(10),command varchar(10),message_id varchar(50))");

        // 搜索历史表
        db.execSQL("CREATE TABLE IF NOT EXISTS history(_id Integer primary key autoincrement, "
                + "user_id varchar(50),playName varchar(50))");

        //   PlayerName播放显示名称PlayerImage播放显示图片PlayerUrl播放路径PlayerMediaType播放类型，radio，audio，seq,PlayerAllTime播放文件总时长
        //	 PlayerInTime此时播放时长PlayerContentDesc播放文件介绍PlayerNum播放次数PlayerZanTypeString类型的true,false
        db.execSQL("CREATE TABLE IF NOT EXISTS playHistory(_id Integer primary key autoincrement, "
                + "contentId varchar(100),playName varchar(50),playImage varchar(300),playUrl varchar(300),playUrI varchar(500),playMediaType varchar(50)," +
                "playAllTime varchar(50),playTag varchar(100),playContentDesc varchar(3000),contentPlayType varchar(50),IsPlaying varchar(300)," +
                "ColumnNum varchar(50),playShareUrl varchar(300),playFavorite varchar(50),playNum varchar(50),albumName varchar(100),albumImg varchar(300)," +
                "albumDesc varchar(3000),albumId varchar(100),playInTime varchar(50),playZanType varchar(50),playAddTime varchar(50),bjUserId varchar(50))");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL("DROP TABLE IF EXISTS talkHistory");
        db.execSQL("DROP TABLE IF EXISTS message_notify");
        db.execSQL("DROP TABLE IF EXISTS message_system");
        db.execSQL("DROP TABLE IF EXISTS message_subscriber");
        db.execSQL("DROP TABLE IF EXISTS thread_info");
        db.execSQL("DROP TABLE IF EXISTS fileInfo");
        db.execSQL("DROP TABLE IF EXISTS playHistory");
        db.execSQL("DROP TABLE IF EXISTS cityInfo");
        //		db.execSQL("DROP TABLE IF EXISTS fmhistory");
        onCreate(db);
    }
}