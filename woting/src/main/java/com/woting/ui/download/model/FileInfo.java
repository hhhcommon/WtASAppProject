package com.woting.ui.download.model;

import java.io.Serializable;

public class FileInfo implements Serializable {
	private int id;
	private String url;
	private String fileName;
	private int length;
	private int finished;
	private String imageurl;
	private String author;
	private String playcontent;
	private String playname;
	private String sequname;
	private String sequimgurl;
	private String sequdesc;
	private int start=-1;
	private int end=-1;
	private String localurl;
	private String mediatype;

	// 2016.4.23新增字段
	private String sequid;
	private String userid;
	private int downloadtype;// 0为未下载 1为下载中,2暂停状态
	
	//界面展示状态
	private int viewtype=0;//界面决定组件 1为显示点选框 0是没有
	private int checktype=0;//点选框被选中为1 未被选中时为0
	//集数和文件大小
	private int count=-1;//计数项
	private int sum=-1;//总和
	//
	private String ContentShareURL;
	private String ContentFavorite;
	private String ContentId;
	private String playAllTime;
	private String playFrom;

	public String getPlayFrom() {
		return playFrom;
	}

	public void setPlayFrom(String playFrom) {
		this.playFrom = playFrom;
	}

	public String getPlayAllTime() {
		return playAllTime;
	}

	public void setPlayAllTime(String playAllTime) {
		this.playAllTime = playAllTime;
	}

	public String getContentShareURL() {
		return ContentShareURL;
	}

	public void setContentShareURL(String contentShareURL) {
		ContentShareURL = contentShareURL;
	}

	public String getContentFavorite() {
		return ContentFavorite;
	}

	public void setContentFavorite(String contentFavorite) {
		ContentFavorite = contentFavorite;
	}

	public String getContentId() {
		return ContentId;
	}

	public void setContentId(String contentId) {
		ContentId = contentId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public int getViewtype() {
		return viewtype;
	}

	public void setViewtype(int viewtype) {
		this.viewtype = viewtype;
	}

	public int getChecktype() {
		return checktype;
	}

	public void setChecktype(int checktype) {
		this.checktype = checktype;
	}

	public String getSequid() {
		return sequid;
	}

	public void setSequid(String sequid) {
		this.sequid = sequid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public int getDownloadtype() {
		return downloadtype;
	}

	public void setDownloadtype(int downloadtype) {
		this.downloadtype = downloadtype;
	}

	public String getMediatype() {
		return mediatype;
	}

	public void setMediatype(String mediatype) {
		this.mediatype = mediatype;
	}

	public String getLocalurl() {
		return localurl;
	}

	public void setLocalurl(String localurl) {
		this.localurl = localurl;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public FileInfo() {

	}

	public FileInfo(int id, String url, String fileName, int length,
			int finished) {
		this.id = id;
		this.url = url;
		this.fileName = fileName;
		this.length = length;
		this.finished = finished;
	}

	public String getSequname() {
		return sequname;
	}

	public void setSequname(String sequname) {
		this.sequname = sequname;
	}

	public String getSequimgurl() {
		return sequimgurl;
	}

	public void setSequimgurl(String sequimgurl) {
		this.sequimgurl = sequimgurl;
	}

	public String getSequdesc() {
		return sequdesc;
	}

	public void setSequdesc(String sequdesc) {
		this.sequdesc = sequdesc;
	}

	public FileInfo(String url, String fileName, int id, String seqimageurl) {
		super();
		this.url = url;
		this.fileName = fileName;
		this.id = id;
		this.sequimgurl = seqimageurl;
	}

	public FileInfo(String sequname, String sequimgurl, String sequdesc) {
		this.sequdesc = sequname;
		this.sequimgurl = sequimgurl;
		this.sequdesc = sequdesc;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPlaycontent() {
		return playcontent;
	}

	public void setPlaycontent(String playcontent) {
		this.playcontent = playcontent;
	}

	public String getPlayname() {
		return playname;
	}

	public void setPlayname(String playname) {
		this.playname = playname;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "FileInfo [downloadtype=" + downloadtype + ", url=" + url + ", fileName=" + fileName
				+ ", userid=" + userid + ", sequid=" + sequid +", length=" + length
				+ ", finished=" + finished + "]";
	}
}
