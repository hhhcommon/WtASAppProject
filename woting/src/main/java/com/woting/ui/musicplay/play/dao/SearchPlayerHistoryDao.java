package com.woting.ui.musicplay.play.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.common.constant.StringConstant;
import com.woting.ui.music.model.content;
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
        db.execSQL("insert into playHistory(contentId,playName,playImage,playUrl,playUrI,playMediaType,playAllTime,playTag,playContentDesc,"
                        + "contentPlayType,IsPlaying,ColumnNum,playShareUrl,playFavorite,playNum,albumName,albumImg,albumDesc,albumId,"
                        + "playInTime,playZanType,playAddTime,bjUserId) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{playerhistory.getContentID(), playerhistory.getPlayerName(), playerhistory.getPlayerImage()
                        , playerhistory.getPlayerUrl(), playerhistory.getPlayerUrI(), playerhistory.getPlayerMediaType()
                        , playerhistory.getPlayerAllTime(), playerhistory.getPlayTag(), playerhistory.getPlayerContentDescn()
                        , playerhistory.getContentPlayType(), playerhistory.getIsPlaying(), playerhistory.getColumnNum(), playerhistory.getPlayContentShareUrl()
                        , playerhistory.getContentFavorite(), playerhistory.getPlayCount(), playerhistory.getSeqName(), playerhistory.getSeqImg(), playerhistory.getSeqDescn()
                        , playerhistory.getSeqId(), playerhistory.getPlayerInTime(), playerhistory.getPlayerZanType(), playerhistory.getPlayerAddTime(), playerhistory.getBJUserId()});//sql语句
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

                String contentId = cursor.getString(cursor.getColumnIndex("contentId"));
                String playName = cursor.getString(cursor.getColumnIndex("playName"));
                String playImage = cursor.getString(cursor.getColumnIndex("playImage"));
                String playUrl = cursor.getString(cursor.getColumnIndex("playUrl"));
                String playUrI = cursor.getString(cursor.getColumnIndex("playUrI"));//
                String playMediaType = cursor.getString(cursor.getColumnIndex("playMediaType"));
                String playAllTime = cursor.getString(cursor.getColumnIndex("playAllTime"));
                String playTag = cursor.getString(cursor.getColumnIndex("playTag"));
                String playContentDesc = cursor.getString(cursor.getColumnIndex("playContentDesc"));
                String contentPlayType = cursor.getString(cursor.getColumnIndex("contentPlayType"));
                String IsPlaying = cursor.getString(cursor.getColumnIndex("IsPlaying"));
                String ColumnNum = cursor.getString(cursor.getColumnIndex("ColumnNum"));
                String playShareUrl = cursor.getString(cursor.getColumnIndex("playShareUrl"));
                String playFavorite = cursor.getString(cursor.getColumnIndex("playFavorite"));
                String playNum = cursor.getString(cursor.getColumnIndex("playNum"));

                String albumName = cursor.getString(cursor.getColumnIndex("albumName"));
                String albumImg = cursor.getString(cursor.getColumnIndex("albumImg"));
                String albumDesc = cursor.getString(cursor.getColumnIndex("albumDesc"));
                String albumId = cursor.getString(cursor.getColumnIndex("albumId"));

                String playInTime = cursor.getString(cursor.getColumnIndex("playInTime"));
                String playZanType = cursor.getString(cursor.getColumnIndex("playZanType"));
                String playAddTime = cursor.getString(cursor.getColumnIndex("playAddTime"));
                String bjUserId = cursor.getString(cursor.getColumnIndex("bjUserId"));

                PlayerHistory h = new PlayerHistory(contentId, playName, playImage, playUrl, playUrI, playMediaType, playAllTime,
                        playTag, playContentDesc, contentPlayType, IsPlaying, ColumnNum, playShareUrl, playFavorite, playNum,
                        albumName, albumImg, albumDesc, albumId, playInTime, playZanType, playAddTime, bjUserId);
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
     * 保存播放历史
     *
     * @param type
     * @param list
     * @param position
     */
    public void savePlayerHistory(String type, List<content> list, int position) {
        if (type != null && type.equals(StringConstant.TYPE_RADIO)) {
            // 电台
            String playerName;// 名称
            String playerImage;// 头像
            String playerUrl;//播放地址
            String playerUrI;//
            String playerMediaType;// 媒体类型
            String playerContentShareUrl;// 分享地址
            String playerInTime = "0";
            String playerZanType = "0";
            String seqName="" ;
            String playTag ;
            String seqId="" ;
            String seqDesc="" ;
            String seqImg="" ;
            String playerAllTime;// 总时长
            String playerContentDesc;// 描述
            String playerNum;// 播放次数
            String playerFrom;// 节目来源
            String ColumnNum;// 所处位置
            String ContentFavorite;// 喜欢
            String ContentId;// 节目ID
            String IsPlaying;// 正在直播
            String ContentPlayType;// 节目数据类型
            String playerAddTime = Long.toString(System.currentTimeMillis());
            String bjUserId = CommonUtils.getUserId(context);

            try {
                playerName = list.get(position).getContentName();
            } catch (Exception e) {
                e.printStackTrace();
                playerName = "";
            }
            try {
                playerImage = list.get(position).getContentImg();
            } catch (Exception e) {
                e.printStackTrace();
                playerImage = "";
            }
            try {
                playerUrl = list.get(position).getContentPlay();
            } catch (Exception e) {
                e.printStackTrace();
                playerUrl = "";
            }
            try {
                playerUrI = list.get(position).getContentURI();
            } catch (Exception e) {
                e.printStackTrace();
                playerUrI = "";
            }
            try {
                playTag = list.get(position).getContentKeyWord();
            } catch (Exception e) {
                e.printStackTrace();
                playTag = "";
            }
            try {
                playerMediaType = list.get(position).getMediaType();
            } catch (Exception e) {
                e.printStackTrace();
                playerMediaType = "";
            }
            try {
                playerContentShareUrl = list.get(position).getContentShareURL();
            } catch (Exception e) {
                e.printStackTrace();
                playerContentShareUrl = "";
            }
            try {
                playerAllTime = list.get(position).getContentTimes();
            } catch (Exception e) {
                e.printStackTrace();
                playerAllTime = "";
            }
            try {
                playerContentDesc = list.get(position).getContentDescn();
            } catch (Exception e) {
                e.printStackTrace();
                playerContentDesc = "";
            }
            try {
                playerNum = list.get(position).getPlayCount();
            } catch (Exception e) {
                e.printStackTrace();
                playerNum = "";
            }
            try {
                ColumnNum = list.get(position).getColumnNum();
            } catch (Exception e) {
                e.printStackTrace();
                ColumnNum = "";
            }
            try {
                ContentFavorite = list.get(position).getContentFavorite();
            } catch (Exception e) {
                e.printStackTrace();
                ContentFavorite = "";
            }
            try {
                ContentId = list.get(position).getContentId();
            } catch (Exception e) {
                e.printStackTrace();
                ContentId = "";
            }
            try {
                IsPlaying = list.get(position).getIsPlaying();
            } catch (Exception e) {
                e.printStackTrace();
                IsPlaying = "";
            }
            try {
                ContentPlayType = list.get(position).getContentPlayType();
            } catch (Exception e) {
                e.printStackTrace();
                ContentPlayType = "";
            }

            // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
            PlayerHistory history = new PlayerHistory(
                    ContentId, playerName, playerImage, playerUrl, playerUrI, playerMediaType, playerAllTime,
                    playTag, playerContentDesc, ContentPlayType, IsPlaying, ColumnNum, playerContentShareUrl, ContentFavorite, playerNum,
                    seqName, seqImg, seqDesc, seqId, playerInTime, playerZanType, playerAddTime, bjUserId);

            deleteHistory(playerUrl);
            addHistory(history);
        } else if (type != null && type.equals(StringConstant.TYPE_AUDIO)) {
            // 单体节目
            String playerName;// 名称
            String playerImage;// 头像
            String playerUrl;//播放地址
            String playerUrI;//
            String playerMediaType;// 媒体类型
            String playerContentShareUrl;// 分享地址
            String playerInTime = "0";
            String playerZanType = "0";
            String seqName ;
            String playTag ;
            String seqId ;
            String seqDesc ;
            String seqImg ;
            String playerAllTime;// 总时长
            String playerContentDesc;// 描述
            String playerNum;// 播放次数
            String playerFrom;// 节目来源
            String ColumnNum;// 所处位置
            String ContentFavorite;// 喜欢
            String ContentId;// 节目ID
            String IsPlaying;// 正在直播
            String ContentPlayType;// 节目数据类型
            String playerAddTime = Long.toString(System.currentTimeMillis());
            String bjUserId = CommonUtils.getUserId(context);

            try {
                playerName = list.get(position).getContentName();
            } catch (Exception e) {
                e.printStackTrace();
                playerName = "";
            }
            try {
                playerImage = list.get(position).getContentImg();
            } catch (Exception e) {
                e.printStackTrace();
                playerImage = "";
            }
            try {
                playerUrl = list.get(position).getContentPlay();
            } catch (Exception e) {
                e.printStackTrace();
                playerUrl = "";
            }
            try {
                playerUrI = list.get(position).getContentURI();
            } catch (Exception e) {
                e.printStackTrace();
                playerUrI = "";
            }
            try {
                playTag = list.get(position).getContentKeyWord();
            } catch (Exception e) {
                e.printStackTrace();
                playTag = "";
            }
            try {
                playerMediaType = list.get(position).getMediaType();
            } catch (Exception e) {
                e.printStackTrace();
                playerMediaType = "";
            }
            try {
                playerContentShareUrl = list.get(position).getContentShareURL();
            } catch (Exception e) {
                e.printStackTrace();
                playerContentShareUrl = "";
            }
            try {
                playerAllTime = list.get(position).getContentTimes();
            } catch (Exception e) {
                e.printStackTrace();
                playerAllTime = "";
            }
            try {
                playerContentDesc = list.get(position).getContentDescn();
            } catch (Exception e) {
                e.printStackTrace();
                playerContentDesc = "";
            }
            try {
                playerNum = list.get(position).getPlayCount();
            } catch (Exception e) {
                e.printStackTrace();
                playerNum = "";
            }
            try {
                ColumnNum = list.get(position).getColumnNum();
            } catch (Exception e) {
                e.printStackTrace();
                ColumnNum = "";
            }
            try {
                ContentFavorite = list.get(position).getContentFavorite();
            } catch (Exception e) {
                e.printStackTrace();
                ContentFavorite = "";
            }
            try {
                ContentId = list.get(position).getContentId();
            } catch (Exception e) {
                e.printStackTrace();
                ContentId = "";
            }
            try {
                IsPlaying = list.get(position).getIsPlaying();
            } catch (Exception e) {
                e.printStackTrace();
                IsPlaying = "";
            }
            try {
                ContentPlayType = list.get(position).getContentPlayType();
            } catch (Exception e) {
                e.printStackTrace();
                ContentPlayType = "";
            }
            try {
                seqName = list.get(position).getSeqInfo().getContentName();
            } catch (Exception e) {
                e.printStackTrace();
                seqName="";
            }
            try {
                seqId = list.get(position).getSeqInfo().getContentId();
            } catch (Exception e) {
                e.printStackTrace();
                seqId="";
            }
            try {
                seqDesc = list.get(position).getSeqInfo().getContentDescn();
            } catch (Exception e) {
                e.printStackTrace();
                seqDesc="";
            }
            try {
                seqImg = list.get(position).getSeqInfo().getContentImg();
            } catch (Exception e) {
                e.printStackTrace();
                seqImg="";
            }

            // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
            PlayerHistory history = new PlayerHistory(
                    ContentId, playerName, playerImage, playerUrl, playerUrI, playerMediaType, playerAllTime,
                    playTag, playerContentDesc, ContentPlayType, IsPlaying, ColumnNum, playerContentShareUrl, ContentFavorite, playerNum,
                    seqName, seqImg, seqDesc, seqId, playerInTime, playerZanType, playerAddTime, bjUserId);

            deleteHistory(playerUrl);
            addHistory(history);
        }else if (type != null && type.equals(StringConstant.TYPE_TTS)) {
            String playerName;// 名称
            String playerImage;// 头像
            String playerUrl;//播放地址
            String playerUrI;//
            String playerMediaType;// 媒体类型
            String playerContentShareUrl;// 分享地址
            String playerInTime = "0";
            String playerZanType = "0";
            String seqName ;
            String playTag ;
            String seqId ;
            String seqDesc ;
            String seqImg ;
            String playerAllTime;// 总时长
            String playerContentDesc;// 描述
            String playerNum;// 播放次数
            String playerFrom;// 节目来源
            String ColumnNum;// 所处位置
            String ContentFavorite;// 喜欢
            String ContentId;// 节目ID
            String IsPlaying;// 正在直播
            String ContentPlayType;// 节目数据类型
            String playerAddTime = Long.toString(System.currentTimeMillis());
            String bjUserId = CommonUtils.getUserId(context);

            try {
                playerName = list.get(position).getContentName();
            } catch (Exception e) {
                e.printStackTrace();
                playerName = "";
            }
            try {
                playerImage = list.get(position).getContentImg();
            } catch (Exception e) {
                e.printStackTrace();
                playerImage = "";
            }
            try {
                playerUrl = list.get(position).getContentPlay();
            } catch (Exception e) {
                e.printStackTrace();
                playerUrl = "";
            }
            try {
                playerUrI = list.get(position).getContentURI();
            } catch (Exception e) {
                e.printStackTrace();
                playerUrI = "";
            }
            try {
                playTag = list.get(position).getContentKeyWord();
            } catch (Exception e) {
                e.printStackTrace();
                playTag = "";
            }
            try {
                playerMediaType = list.get(position).getMediaType();
            } catch (Exception e) {
                e.printStackTrace();
                playerMediaType = "";
            }
            try {
                playerContentShareUrl = list.get(position).getContentShareURL();
            } catch (Exception e) {
                e.printStackTrace();
                playerContentShareUrl = "";
            }
            try {
                playerAllTime = list.get(position).getContentTimes();
            } catch (Exception e) {
                e.printStackTrace();
                playerAllTime = "";
            }
            try {
                playerContentDesc = list.get(position).getContentDescn();
            } catch (Exception e) {
                e.printStackTrace();
                playerContentDesc = "";
            }
            try {
                playerNum = list.get(position).getPlayCount();
            } catch (Exception e) {
                e.printStackTrace();
                playerNum = "";
            }
            try {
                ColumnNum = list.get(position).getColumnNum();
            } catch (Exception e) {
                e.printStackTrace();
                ColumnNum = "";
            }
            try {
                ContentFavorite = list.get(position).getContentFavorite();
            } catch (Exception e) {
                e.printStackTrace();
                ContentFavorite = "";
            }
            try {
                ContentId = list.get(position).getContentId();
            } catch (Exception e) {
                e.printStackTrace();
                ContentId = "";
            }
            try {
                IsPlaying = list.get(position).getIsPlaying();
            } catch (Exception e) {
                e.printStackTrace();
                IsPlaying = "";
            }
            try {
                ContentPlayType = list.get(position).getContentPlayType();
            } catch (Exception e) {
                e.printStackTrace();
                ContentPlayType = "";
            }
            try {
                seqName = list.get(position).getSeqInfo().getContentName();
            } catch (Exception e) {
                e.printStackTrace();
                seqName="";
            }
            try {
                seqId = list.get(position).getSeqInfo().getContentId();
            } catch (Exception e) {
                e.printStackTrace();
                seqId="";
            }
            try {
                seqDesc = list.get(position).getSeqInfo().getContentDescn();
            } catch (Exception e) {
                e.printStackTrace();
                seqDesc="";
            }
            try {
                seqImg = list.get(position).getSeqInfo().getContentImg();
            } catch (Exception e) {
                e.printStackTrace();
                seqImg="";
            }

            // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
            PlayerHistory history = new PlayerHistory(
                    ContentId, playerName, playerImage, playerUrl, playerUrI, playerMediaType, playerAllTime,
                    playTag, playerContentDesc, ContentPlayType, IsPlaying, ColumnNum, playerContentShareUrl, ContentFavorite, playerNum,
                    seqName, seqImg, seqDesc, seqId, playerInTime, playerZanType, playerAddTime, bjUserId);
            deleteHistoryById(ContentId);
            addHistory(history);
        }
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
     *
     * @param url          当前节目播放地址
     * @param CurrentTimes 当前播放了多久
     * @param TotalTimes   总时长
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
