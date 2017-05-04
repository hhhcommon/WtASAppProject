package com.woting.ui.musicplay.download.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.common.util.CommonUtils;
import com.woting.ui.model.content;
import com.woting.ui.musicplay.download.model.FileInfo;
import com.woting.ui.musicplay.download.service.DownloadService;
import com.woting.common.database.SQLiteHelper;
import com.woting.common.util.SequenceUUID;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地文件存储：存储要下载到本地的文件url，图片url等信息，已经下载过的程序标记finished="true"
 * 未下载的程序标记finished="false" 1：查詢部份已經完成，目前僅需要查詢未完成的列表，后续可扩展提供已完成的下载
 * 2：添加部分已经完成，目前支持传递入一个可下载的URL地址进行下载，后续可传入一个包含aurhor或者其他信息的对象，表中已经预留字段
 * 3：修改功能已经完成，目前支持根据文件名对完成状态进行修改 4:删除功能本业务暂不涉及，未处理
 */
public class FileInfoDao {
    private SQLiteHelper helper;
    private Context context;


    // 构造方法
    public FileInfoDao(Context context) {
        this.context = context;
        helper = new SQLiteHelper(context);
    }

    /**
     * 传递进来的下载地址 对下载地址进行处理使之变成一个list，对其进行保存，默认的finished设置为false；
     */
    public List<FileInfo> queryFileInfo(String s, String userId) {
        List<FileInfo> m = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            // 执行查询语句 返回一个cursor对象
            cursor = db.rawQuery("Select * from fileInfo where finished like ? and userId like ? order by _id desc", new String[]{s, userId});
            // 循环遍历cursor中储存的键值对
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String author = cursor.getColumnName(cursor.getColumnIndex("author"));
                String filename = cursor.getString(cursor.getColumnIndex("fileName"));
                String seqimageurl = cursor.getString(cursor.getColumnIndex("albumImgUrl"));
                String downloadtype = cursor.getString(cursor.getColumnIndex("downloadType"));
                String userid = cursor.getString(cursor.getColumnIndex("userId"));
                String sequid = cursor.getString(cursor.getColumnIndex("albumId"));
                String imagurl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                int start = cursor.getInt(1);
                int end = cursor.getInt(2);
                String playcontentshareurl = cursor.getString(cursor.getColumnIndex("playShareUrl"));
                String playfavorite = cursor.getString(cursor.getColumnIndex("playFavorite"));
                String contentid = cursor.getString(cursor.getColumnIndex("contentId"));
                String playAllTime = cursor.getString(cursor.getColumnIndex("playAllTime"));
                String playfrom = cursor.getString(cursor.getColumnIndex("playFrom"));
                String contentDescn = cursor.getString(cursor.getColumnIndex("contentDesc"));
                String playcount = cursor.getString(cursor.getColumnIndex("playCount"));
                String contentplaytype = cursor.getString(cursor.getColumnIndex("contentPlayType"));
                String localUrl = cursor.getString(cursor.getColumnIndex("localUrl"));

                // 把每个对象都放到history对象里
                FileInfo h = new FileInfo(url, filename, id, seqimageurl);
                h.setAuthor(author);
                h.setStart(start);
                h.setImageurl(imagurl);
                h.setDownloadtype(Integer.valueOf(downloadtype));
                h.setEnd(end);
                h.setUserid(userid);
                h.setSequid(sequid);
                h.setContentShareURL(playcontentshareurl);
                h.setContentFavorite(playfavorite);
                h.setContentId(contentid);
                h.setPlayAllTime(playAllTime);
                h.setPlayFrom(playfrom);
                h.setContentDescn(contentDescn);
                h.setPlayCount(playcount);
                h.setContentPlayType(contentplaytype);
                h.setLocalurl(localUrl);
                // 往m里储存每个history对象
                m.add(h);
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
        return m;
    }

    //type无意义可传任意int数，存在为实现重载
    public List<FileInfo> queryFileInfo(String sequid, String userid, int type) {
        List<FileInfo> m = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            // 执行查询语句 返回一个cursor对象
            cursor = db.rawQuery(
                    "Select * from fileInfo where finished='true'and albumId=? and userId=?",
                    new String[]{sequid, userid});
            // 循环遍历cursor中储存的键值对
            while (cursor.moveToNext()) {
                String localurl = cursor.getString(cursor.getColumnIndex("localUrl"));
                String author = cursor.getColumnName(cursor
                        .getColumnIndex("author"));
                String filename = cursor.getString(cursor
                        .getColumnIndex("fileName"));
                String sequimgurl = cursor.getString(cursor.getColumnIndex("albumImgUrl"));
                String imgurl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                int start = cursor.getInt(1);
                int end = cursor.getInt(2);
                String playcontentshareurl = cursor.getString(cursor.getColumnIndex("playShareUrl"));
                String playfavorite = cursor.getString(cursor.getColumnIndex("playFavorite"));
                String contentid = cursor.getString(cursor.getColumnIndex("contentId"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String playAllTime = cursor.getString(cursor.getColumnIndex("playAllTime"));
                String playfrom = cursor.getString(cursor.getColumnIndex("playFrom"));
                String contentDescn = cursor.getString(cursor.getColumnIndex("contentDesc"));
                String playcount = cursor.getString(cursor.getColumnIndex("playCount"));
                String contentplaytype = cursor.getString(cursor.getColumnIndex("contentPlayType"));
                String localUrl = cursor.getString(cursor.getColumnIndex("localUrl"));
                // 把每个对象都放到history对象里
                FileInfo h = new FileInfo();
                h.setLocalurl(localurl);
                h.setUrl(url);
                h.setFileName(filename);
                h.setImageurl(imgurl);
                h.setSequimgurl(sequimgurl);
                h.setEnd(end);
                h.setContentShareURL(playcontentshareurl);
                h.setContentFavorite(playfavorite);
                ;
                h.setContentId(contentid);
                h.setPlayAllTime(playAllTime);
                h.setPlayFrom(playfrom);
                h.setContentDescn(contentDescn);
                h.setPlayCount(playcount);
                h.setContentPlayType(contentplaytype);
                h.setLocalurl(localUrl);
                // 往m里储存每个history对象
                m.add(h);
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
        return m;
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public List<FileInfo> queryFileInfoAll(String userid) {
        List<FileInfo> m = new ArrayList<FileInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            // 执行查询语句 返回一个cursor对象
            cursor = db.rawQuery("Select * from fileInfo where userId like ? ", new String[]{userid});
            // 循环遍历cursor中储存的键值对
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String filename = cursor.getString(cursor.getColumnIndex("fileName"));
                String seqimageurl = cursor.getString(cursor.getColumnIndex("albumImgUrl"));
                // 把每个对象都放到history对象里
                FileInfo h = new FileInfo(url, filename, id, seqimageurl);
                // 网m里储存每个history对象
                m.add(h);
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
        return m;
    }


    public void insertFileInfo(List<content> urlList) {
        SQLiteDatabase db = helper.getWritableDatabase();
        // 通过helper的实现对象获取可操作的数据库db
        for (urlList.size(); urlList.size() > 0; ) {
            content contents = urlList.remove(0);
            if (contents != null) {
                String playName;
                String seqId = "";
                String contentPlay = "";
                String contentImg = "";
                String albumName = "";
                String albumImg = "";
                String albumDescn = "";
                String downloadType = "";
                String perName = "";
                String contentShareURL = "";


                try {
                    contentPlay = contents.getContentPlay();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    contentImg = contents.getContentImg();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    seqId = contents.getSeqInfo().getContentId();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    String name = contents.getContentName();
                    if (name == null || name.trim().equals("")) {
                        playName = SequenceUUID.getUUIDSubSegment(0) + ".mp3";
                    } else {
                        playName = name.replaceAll(
                                "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]",
                                "") + ".mp3";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    playName = SequenceUUID.getUUIDSubSegment(0) + ".mp3";
                }

                try {
                    albumName = contents.getSeqInfo().getContentName();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    albumImg = contents.getSeqInfo().getContentImg();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    albumDescn = contents.getSeqInfo().getContentDescn();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    downloadType = contents.getDownloadtype();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    perName = contents.getContentPersons().get(0).getPerName();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    contentShareURL = contents.getContentShareURL();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                db.execSQL("insert into fileInfo(url,imageUrl,fileName,albumName,albumImgUrl,albumDesc,finished,albumId,userId,downloadType,author," +
                        "playShareUrl,playFavorite,contentId,playAllTime,playFrom,playCount,contentDesc,playTag,contentPlayType) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{
                        contentPlay, contentImg, playName, albumName, albumImg, albumDescn, "false", seqId, CommonUtils.getUserId(context), downloadType,
                        perName, contentShareURL,
                        contents.getContentFavorite(), contents.getContentId(), contents.getContentTimes(), contents.getContentPub(),
                        contents.getPlayCount(), contents.getContentDescn(), contents.getPlayTag(), contents.getContentPlayType()});// sql语句
            }
        }
        db.close();// 关闭数据库对象
    }

    // 改
    public void updataFileInfo(String filename) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String localUrl = DownloadService.DOWNLOAD_PATH + filename;
        db.execSQL("update fileInfo set finished=?,localUrl=? where fileName=?",
                new Object[]{"true", localUrl, filename});
        db.close();
    }

    /**
     * 更改数据库中下载数据库中用户的下载状态值
     *
     * @param url 文件下载url
     * @param url 下载状态 0为未下载 1为下载中 2为等待
     */
    public void updataDownloadStatus(String url, String downloadtype) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update fileInfo set downloadType=? where url=?",
                new Object[]{downloadtype, url});
        db.close();
    }

    /**
     * 保存关于该url的起始跟结束
     *
     * @param url
     * @param start
     * @param end
     */
    public void updataFileProgress(String url, int start, int end) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update fileInfo set start=?,end =? where url=?", new Object[]{start, end, url});
        db.close();
    }

    /**
     * 删实现两个方法 一种依据url删除 一种依据完成状态删除
     */
    public void deleteFileByUserId(String userid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from fileInfo where finished='false' and userId=?", new Object[]{userid});
        db.close();
    }

    //删除已经不存在的项目
    public void deleteFileInfo(String localurl, String userid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from fileInfo where finished='true' and localUrl=? and userId=?", new Object[]{localurl, userid});
        db.close();
    }

    //删除专辑信息
    public void deleteSequ(String sequname, String userid) {
        SQLiteDatabase db = helper.getWritableDatabase();
    /*	db.execSQL("delete from fileinfo where finished='true' and sequname=? and userid=?",new Object[]{sequname,userid});*/
        db.execSQL("delete from fileInfo where albumName=? and userId=?", new Object[]{sequname, userid});
        db.close();
    }


    //对表中标记ture的数据进行分组
    public List<FileInfo> GroupFileInfoAll(String userid) {
        List<FileInfo> m = new ArrayList<FileInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            // 执行查询语句 返回一个cursor对象
            cursor = db.rawQuery("Select count(fileName),sum(end),albumName,albumImgUrl,albumDesc,albumId,fileName,author,playFrom from fileInfo where finished='true' and userId =? group by albumId ", new String[]{userid});
            // 循环遍历cursor中储存的键值对
            while (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                int sum = cursor.getInt(1);
                String sequname = cursor.getString(cursor.getColumnIndex("albumName"));
                String sequimgurl = cursor.getString(cursor.getColumnIndex("albumImgUrl"));
                String sequdesc = cursor.getString(cursor.getColumnIndex("albumDesc"));
                String sequid = cursor.getString(cursor.getColumnIndex("albumId"));
                String filename = cursor.getString(cursor.getColumnIndex("fileName"));
                String author = cursor.getString(cursor.getColumnIndex("author"));
                String playerfrom = cursor.getString(cursor.getColumnIndex("playFrom"));
                // 把每个对象都放到history对象里
                FileInfo h = new FileInfo();
                h.setSequname(sequname);
                h.setSequimgurl(sequimgurl);
                h.setSequdesc(sequdesc);
                h.setSequid(sequid);
                h.setFileName(filename);
                h.setAuthor(author);
                h.setCount(count);
                h.setSum(sum);
                h.setPlayFrom(playerfrom);
                // 网m里储存每个history对象
                m.add(h);
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
        return m;
    }

    /*
     *关闭目前打开的所有数据库对象
     */
    public void closeDB() {
        helper.close();
    }
}
