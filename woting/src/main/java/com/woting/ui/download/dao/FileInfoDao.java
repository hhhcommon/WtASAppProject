package com.woting.ui.download.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.ui.download.model.FileInfo;
import com.woting.ui.download.service.DownloadService;
import com.woting.ui.home.program.album.model.ContentInfo;
import com.woting.common.database.SQLiteHelpe;
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
	private SQLiteHelpe helper;
	private String url = null;
	private ContentInfo content;
	private Context context;

	// 构造方法
	public FileInfoDao(Context context) {
		helper = new SQLiteHelpe(context);
		this.context=context;
	}

	/**
	 *  传递进来的下载地址 对下载地址进行处理使之变成一个list，对其进行保存，默认的finished设置为false；
	 */
	public List<FileInfo> queryFileinfo(String s, String useridnow) {
		List<FileInfo> mylist = new ArrayList<>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		try {
			// 执行查询语句 返回一个cursor对象
			cursor = db.rawQuery(
					"Select * from fileinfo where finished like ? and userid like ? order by _id desc",
					new String[] { s ,useridnow});
			// 循环遍历cursor中储存的键值对
			while (cursor.moveToNext()) {
				int id=cursor.getInt(0);
				String url = cursor.getString(cursor.getColumnIndex("url"));
				String author = cursor.getColumnName(cursor.getColumnIndex("author"));
				String filename = cursor.getString(cursor.getColumnIndex("filename"));
				String seqimageurl = cursor.getString(cursor.getColumnIndex("sequimgurl"));
				String downloadtype = cursor.getString(cursor.getColumnIndex("downloadtype"));
				String userid= cursor.getString(cursor.getColumnIndex("userid"));
				String sequid=cursor.getString(cursor.getColumnIndex("sequid"));
				String imagurl=cursor.getString(cursor.getColumnIndex("imageurl"));
				int start=cursor.getInt(1);
				int end=cursor.getInt(2);
				String playcontentshareurl=cursor.getString(cursor.getColumnIndex("playshareurl"));
				String playfavorite=cursor.getString(cursor.getColumnIndex("playfavorite"));
				String contentid=cursor.getString(cursor.getColumnIndex("contentid"));
				// 把每个对象都放到history对象里
				FileInfo h = new FileInfo(url, filename,id,seqimageurl);
				/*	h.setId(id);*/
				h.setAuthor(author);
				//h.setContentPub(author);
				h.setStart(start);
				h.setImageurl(imagurl);
				h.setDownloadtype(Integer.valueOf(downloadtype));
				h.setEnd(end);
				h.setUserid(userid);
				h.setSequid(sequid);
				h.setContentShareURL(playcontentshareurl);
				h.setContentFavorite(playfavorite);;
				h.setContentId(contentid);
				/*	h.setFinished(finished);*/
				// 网mylist里储存每个history对象
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

	//type无意义可传任意int数，存在为实现重载
	public List<FileInfo> queryFileinfo(String sequid,String userid,int type) {
		List<FileInfo> mylist = new ArrayList<>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		try {
			// 执行查询语句 返回一个cursor对象
			cursor = db.rawQuery(
					"Select * from fileinfo where finished='true'and sequid=? and userid=?",
					new String[] { sequid, userid});
			// 循环遍历cursor中储存的键值对
			while (cursor.moveToNext()) {
				String localurl = cursor.getString(cursor.getColumnIndex("localurl"));
				String author = cursor.getColumnName(cursor
						.getColumnIndex("author"));
				String filename = cursor.getString(cursor
						.getColumnIndex("filename"));
				String sequimgurl=cursor.getString(cursor.getColumnIndex("sequimgurl"));
				String imgurl=cursor.getString(cursor.getColumnIndex("imageurl"));
				int start=cursor.getInt(1);
				int end=cursor.getInt(2);
				String playcontentshareurl=cursor.getString(cursor.getColumnIndex("playshareurl"));
				String playfavorite=cursor.getString(cursor.getColumnIndex("playfavorite"));
				String contentid=cursor.getString(cursor.getColumnIndex("contentid"));
				String url=cursor.getString(cursor.getColumnIndex("url"));
				// 把每个对象都放到history对象里
				FileInfo h = new FileInfo();
				h.setLocalurl(localurl);
				h.setUrl(url);
				h.setFileName(filename);
				h.setImageurl(imgurl);
				h.setSequimgurl(sequimgurl);
				h.setEnd(end);
				h.setContentShareURL(playcontentshareurl);
				h.setContentFavorite(playfavorite);;
				h.setContentId(contentid);
				// 网mylist里储存每个history对象
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
	 * 查询所有数据
	 * @return
	 */
	public List<FileInfo> queryFileinfoAll(String userid) {
		List<FileInfo> mylist = new ArrayList<FileInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		try {
			// 执行查询语句 返回一个cursor对象
			cursor = db.rawQuery("Select * from fileinfo where userid like ? ", new String[] {userid});
			// 循环遍历cursor中储存的键值对
			while (cursor.moveToNext()) {
				int id=cursor.getInt(0);
				String url = cursor.getString(cursor.getColumnIndex("url"));
				String filename = cursor.getString(cursor.getColumnIndex("filename"));
				String seqimageurl = cursor.getString(cursor.getColumnIndex("sequimgurl"));
				// 把每个对象都放到history对象里
				FileInfo h = new FileInfo(url, filename,id,seqimageurl);
				// 网mylist里储存每个history对象
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


	public void insertfileinfo(List<ContentInfo> urllist) {
		SQLiteDatabase db = helper.getWritableDatabase();
		// 通过helper的实现对象获取可操作的数据库db
		for (urllist.size(); urllist.size() > 0;) {
			content = urllist.remove(0);
			//			String playname = content.getContentName() + ".mp3".replaceAll("/", "").replaceAll(" ", "").replaceAll("", "");
			String name = content.getContentName();
			String playname;
			String sequid=content.getSequid();

			if(sequid==null||sequid.trim().equals("")){
				sequid="woting";
			}else{

			}
			if(name==null||name.trim().equals("")){
				playname = SequenceUUID.getUUIDSubSegment(0)+".mp3";
			}else{
				playname =name.replaceAll(
						"[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]",
						"")+".mp3";
			}

			db.execSQL("insert into fileinfo(url,imageurl,filename,sequname,sequimgurl,sequdesc,finished,sequid,userid,downloadtype,author,playshareurl,playfavorite,contentid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[] { content.getContentPlay(),
					content.getContentImg(), playname,
					content.getSequname(), content.getSequimgurl(),
					content.getSequdesc(), "false",sequid,content.getUserid(),content.getDownloadtype(),content.getAuthor(),content.getContentShareURL(),content.getContentFavorite(),content.getContentId()});// sql语句
		}
		db.close();// 关闭数据库对象
	}

	// 改
	public void updatefileinfo(String filename) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String localurl= DownloadService.DOWNLOAD_PATH+filename;
		db.execSQL("update fileinfo set finished=?,localurl=? where filename=?",
				new Object[] {"true",localurl,filename});
		db.close();
	}

	/**
	 * 更改数据库中下载数据库中用户的下载状态值
	 * @param  url 文件下载url
	 *  @param  url 下载状态 0为未下载 1为下载中 2为等待
	 */
	public void updatedownloadstatus(String url,String downloadtype) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("update fileinfo set downloadtype=? where url=?",
				new Object[] {downloadtype,url});
		db.close();
	}
	/**
	 * 保存关于该url的起始跟结束
	 * @param url
	 * @param start
	 * @param end
	 */
	public void updatefileprogress(String url,int start,int end){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("update fileinfo set start=?,end =? where url=?",
				new Object[] { start,end,url});
		db.close();
	}

	/**
	 *  删实现两个方法 一种依据url删除 一种依据完成状态删除
	 */
	public void deletefilebyuserid(String userid) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from fileinfo where finished='false' and userid=?",new Object[]{userid});
		db.close();
	}
	//删除已经不存在的项目
	public void deletefileinfo(String localurl,String userid) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from fileinfo where finished='true' and localurl=? and userid=?",new Object[]{localurl,userid});
		db.close();
	}
	//删除专辑信息
	public void deletesequ(String sequname,String userid) {
		SQLiteDatabase db = helper.getWritableDatabase();
	/*	db.execSQL("delete from fileinfo where finished='true' and sequname=? and userid=?",new Object[]{sequname,userid});*/
		db.execSQL("delete from fileinfo where sequname=? and userid=?",new Object[]{sequname,userid});
		db.close();
	}


	//对表中标记ture的数据进行分组
	public List<FileInfo> GroupFileinfoAll(String userid) {
		List<FileInfo> mylist = new ArrayList<FileInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		try {
			// 执行查询语句 返回一个cursor对象
			/*cursor = db.rawQuery("Select * from fileinfo where finished='true' and userid =? group by sequid ", new String[]{userid});*/

			cursor = db.rawQuery("Select count(filename),sum(end),sequname,sequimgurl,sequdesc,sequid,filename,author from fileinfo where finished='true' and userid =? group by sequid ", new String[]{userid});
			// 循环遍历cursor中储存的键值对
			while (cursor.moveToNext()) {
				int count=cursor.getInt(0);
				int sum=cursor.getInt(1);
				String sequname = cursor.getString(cursor.getColumnIndex("sequname"));
				String sequimgurl = cursor.getString(cursor.getColumnIndex("sequimgurl"));
				String sequdesc = cursor.getString(cursor.getColumnIndex("sequdesc"));
				String sequid = cursor.getString(cursor.getColumnIndex("sequid"));
				String filename= cursor.getString(cursor.getColumnIndex("filename"));
				String author= cursor.getString(cursor.getColumnIndex("author"));
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
				// 网mylist里储存每个history对象
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

	/*
	 *关闭目前打开的所有数据库对象
	 *
	 */
	public void closedb(){
		helper.close();
	}
}
