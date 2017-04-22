package com.woting.ui.musicplay.play.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.woting.ui.musicplay.play.model.PlayerHistory;
import com.woting.common.database.SQLiteHelper;
import com.woting.common.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对播放历史表的操作
 *
 * @author 辛龙
 *         2016年1月15日
 */
public class SearchPlayerHistoryDao {
    private SQLiteHelper helper;
    private Context context;

    //构造方法
    public SearchPlayerHistoryDao(Context context) {
        helper = new SQLiteHelper(context);
        this.context = context;
    }

    /**
     * 插入播放历史表一条数据
     */
    public void addHistory(PlayerHistory playerhistory) {
        //通过helper的实现对象获取可操作的数据库db
        SQLiteDatabase db = helper.getWritableDatabase();
//        String s = playerhistory.getPlayerNum();
//        String s1 = playerhistory.getPlayerName();
//        String s2 = playerhistory.getPlayerUrl();
//        String s3 = playerhistory.getIsPlaying();
//        Log.e("加库地址======", "" + s2);

        db.execSQL("insert into playHistory(playName,playImage,playUrl,playUrI,playMediaType,playAllTime"
                        + ",playInTime,playContentDesc,playNum,playZanType,playFrom,playFromId,"
                        + "playAddTime,bjUserId,playShareUrl,playFavorite,"
                        + "contentId,localUrl,albumName,albumImg,albumDesc,albumId,playTag,contentPlayType,IsPlaying,ColumnNum) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{playerhistory.getPlayerName(), playerhistory.getPlayerImage()
                        , playerhistory.getPlayerUrl(), playerhistory.getPlayerUrI()
                        , playerhistory.getPlayerMediaType()
                        , playerhistory.getPlayerAllTime(), playerhistory.getPlayerInTime()//这里
                        , playerhistory.getPlayerContentDescn(), playerhistory.getPlayerNum()
                        , playerhistory.getPlayerZanType(), playerhistory.getPlayerFrom(), playerhistory.getPlayerFromId(), playerhistory.getPlayerAddTime()
                        , playerhistory.getBJUserid(), playerhistory.getPlayContentShareUrl()
                        , playerhistory.getContentFavorite(), playerhistory.getContentID(), playerhistory.getLocalurl()
                        , playerhistory.getSequName(), playerhistory.getSequImg(), playerhistory.getSequDesc()
                        , playerhistory.getSequId(), playerhistory.getPlayTag(), playerhistory.getContentPlayType(), playerhistory.getIsPlaying(), playerhistory.getColumnNum()});//sql语句
        db.close();//关闭数据库对象
    }

    /**
     * 查询数据库里的数据，无参查询语句 供特定使用
     */
    public List<PlayerHistory> queryHistory() {
        List<PlayerHistory> _p = new ArrayList<PlayerHistory>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String user_id = CommonUtils.getUserId(context);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("Select * from playHistory where bjUserId =? order by playAddTime desc ", new String[]{user_id});
            while (cursor.moveToNext()) {
                String playerName = cursor.getString(1);
                String playerImage = cursor.getString(2);
                String playerUrl = cursor.getString(3);
                String playerUrI = cursor.getString(4);//
                String playerMediaType = cursor.getString(5);
                String playerAllTime = cursor.getString(cursor.getColumnIndex("playAllTime"));
                String playerInTime = cursor.getString(cursor.getColumnIndex("playInTime"));
                String playerContentDesc = cursor.getString(8);
                String playerNum = cursor.getString(cursor.getColumnIndex("playNum"));
                String playerZanType = cursor.getString(10);
                String playerFrom = cursor.getString(11);
                String playerFromId = cursor.getString(12);
                String playerFromUrl = cursor.getString(13);
                String playerAddTime = cursor.getString(14);
                String bjUserId = cursor.getString(15);
                String playContentShareUrl = cursor.getString(16);
                String ContentFavorite = cursor.getString(17);
                String ContentID = cursor.getString(18);
                String localUrl = cursor.getString(19);
                String seqName = cursor.getString(cursor.getColumnIndex("albumName"));
                String seqId = cursor.getString(cursor.getColumnIndex("albumId"));
                String seqDesc = cursor.getString(cursor.getColumnIndex("albumDesc"));
                String seqImg = cursor.getString(cursor.getColumnIndex("albumImg"));
                String contentPlayType = cursor.getString(cursor.getColumnIndex("contentPlayType"));
                String IsPlaying = cursor.getString(cursor.getColumnIndex("IsPlaying"));
                String ColumnNum = cursor.getString(cursor.getColumnIndex("ColumnNum"));

                PlayerHistory h = new PlayerHistory(playerName, playerImage, playerUrl, playerUrI, playerMediaType, playerAllTime,
                        playerInTime, playerContentDesc, playerNum, playerZanType, playerFrom, playerFromId, playerFromUrl, playerAddTime, bjUserId, playContentShareUrl, ContentFavorite, ContentID
                        , localUrl, seqName, seqId, seqDesc, seqImg, contentPlayType, IsPlaying, ColumnNum);
                _p.add(h);
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
        return _p;
    }

    /**
     * 通过url删除数据库表中的数据
     */
    public void deleteHistory(String url) {
        SQLiteDatabase db = helper.getReadableDatabase();
        db.execSQL("Delete from playHistory where playUrl like ?", new String[]{url});
        db.close();
    }

    /**
     * 根据 contentId 删除数据库表中的数据
     */
    public void deleteHistoryById(String id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        db.execSQL("Delete from playHistory where contentId like ?", new String[]{id});
        db.close();
    }

    public void deleteHistoryAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        db.execSQL("Delete from playHistory");
        db.close();
    }


    /**
     * 修改某一个字段的数据
     *
     * @param url
     * @param updateType
     * @param date
     */
    public void updateFileInfo(String url, String updateType, String date) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update playHistory set " + updateType + "=? where playUrl=?",
                new Object[]{date, url});
        db.close();
    }

    /**
     * 修改数据库当中某个单体节目的喜欢类型
     */
    public void updateFavorite(String url, String contentFavorite) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String user_id = CommonUtils.getUserId(context);
        if (user_id != null && !user_id.equals("")) {
            db.execSQL("update playHistory set playFavorite=? where bjUserId=? and playUrl=?", new Object[]{contentFavorite, user_id, url});
        } else {
            db.execSQL("update playHistory set playFavorite=? where playUrl=?", new Object[]{contentFavorite, url});
        }
        db.close();
    }

    /**
     * 更新播放时间
     * @param url 当前节目播放地址
     * @param CurrentTimes 当前播放了多久
     * @param TotalTimes 总时长
     */
    public void updatePlayerInTime(String url, long CurrentTimes, long TotalTimes) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String user_id = CommonUtils.getUserId(context);
        if (CurrentTimes > 0 && TotalTimes > 0) {
            if (user_id != null && !user_id.equals("")) {
                db.execSQL("update playHistory set playInTime=? , playAllTime=? where bjUserId=? and playUrl=?", new Object[]{CurrentTimes, TotalTimes, user_id, url});
            } else {
                db.execSQL("update playHistory set playInTime=? , playAllTime=? where playUrl=?", new Object[]{CurrentTimes, TotalTimes, url});
            }
        } else {
            db.execSQL("update playHistory set playInTime=? , playAllTime=? where bjUserId=? and playUrl=?", new Object[]{"0", "0", user_id, url});
        }
        db.close();
    }

}
