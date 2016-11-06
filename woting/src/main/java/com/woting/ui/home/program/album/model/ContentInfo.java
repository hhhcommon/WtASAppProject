package com.woting.ui.home.program.album.model;

import java.io.Serializable;
import java.util.List;

public class ContentInfo implements Serializable {
	private static final long serialVersionUID = 1231243519566434965L;
	
	private String ContentURI;
	private String ContentPersons;
	private String CTime;
	private String ContentKeyWord;
	private String ContentSubjectWord;
	private String ContentTimes;
	private String ContentName;
	
	private String ContentPubTime;
	private String ContentPub;
	private String ContentSource;
	private String ContentURIS;
	private String MediaType;
	private String ContentDesc;
	private String ContentImg;
	//以上全部内容针对内容
	private String sequname;
	private String sequimgurl;
	private String sequdesc;
	private String ContentPlay;
	private String PlayCount;
	private String author;
	//2016.4.23新增字段
	private String sequid;
	private String userid;
	private String downloadtype;
	private String ContentShareURL;
	private String ContentFavorite;
	private String ContentId;
	private String localurl;
	
	public String getLocalurl() {
		return localurl;
	}

	public void setLocalurl(String localurl) {
		this.localurl = localurl;
	}

	private List<ContentCatalogs> ContentCatalogs;

	public List<ContentCatalogs> getContentCatalogs() {
		return ContentCatalogs;
	}

	public void setContentCatalogs(List<ContentCatalogs> contentCatalogs) {
		ContentCatalogs = contentCatalogs;
	}

	public String getContentFavorite() {
		return ContentFavorite;
	}

	public void setContentFavorite(String contentFavorite) {
		ContentFavorite = contentFavorite;
	}

	public String getContentShareURL() {
		return ContentShareURL;
	}

	public void setContentShareURL(String contentShareURL) {
		ContentShareURL = contentShareURL;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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

	public String getDownloadtype() {
		return downloadtype;
	}

	public void setDownloadtype(String downloadtype) {
		this.downloadtype = downloadtype;
	}

	private int Type = 1;// 标记是否为最后一项的，新加入尾部的数据会设置type为2，在adapter里针对此属性设置对应的gridview
	private int CheckType = 1;// 标记item是否被选中，1为未选中，2为选中,3已经下载过

	public String getPlayCount() {
		return PlayCount;
	}

	public void setPlayCount(String playCount) {
		PlayCount = playCount;
	}

	public String getContentPlay() {
		return ContentPlay;
	}

	public void setContentPlay(String contentPlay) {
		ContentPlay = contentPlay;
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

	public String getContentURI() {
		return ContentURI;
	}

	public void setContentURI(String contentURI) {
		ContentURI = contentURI;
	}

	public String getContentPersons() {
		return ContentPersons;
	}

	public void setContentPersons(String contentPersons) {
		ContentPersons = contentPersons;
	}

	//	public String getContentCatalogs() {
	//		return ContentCatalogs;
	//	}
	//
	//	public void setContentCatalogs(String contentCatalogs) {
	//		ContentCatalogs = contentCatalogs;
	//	}

	public String getCTime() {
		return CTime;
	}

	public void setCTime(String cTime) {
		CTime = cTime;
	}

	public String getContentKeyWord() {
		return ContentKeyWord;
	}

	public void setContentKeyWord(String contentKeyWord) {
		ContentKeyWord = contentKeyWord;
	}

	public String getContentSubjectWord() {
		return ContentSubjectWord;
	}

	public void setContentSubjectWord(String contentSubjectWord) {
		ContentSubjectWord = contentSubjectWord;
	}

	public String getContentTimes() {
		return ContentTimes;
	}

	public void setContentTimes(String contentTimes) {
		ContentTimes = contentTimes;
	}

	public String getContentName() {
		return ContentName;
	}

	public void setContentName(String contentName) {
		ContentName = contentName;
	}

	public String getContentPubTime() {
		return ContentPubTime;
	}

	public void setContentPubTime(String contentPubTime) {
		ContentPubTime = contentPubTime;
	}

	public String getContentPub() {
		return ContentPub;
	}

	public void setContentPub(String contentPub) {
		ContentPub = contentPub;
	}

	public String getContentSource() {
		return ContentSource;
	}

	public void setContentSource(String contentSource) {
		ContentSource = contentSource;
	}

	public String getContentURIS() {
		return ContentURIS;
	}

	public void setContentURIS(String contentURIS) {
		ContentURIS = contentURIS;
	}

	public String getMediaType() {
		return MediaType;
	}

	public void setMediaType(String mediaType) {
		MediaType = mediaType;
	}

	public String getContentId() {
		return ContentId;
	}

	public void setContentId(String contentId) {
		ContentId = contentId;
	}

	public String getContentDesc() {
		return ContentDesc;
	}

	public void setContentDesc(String contentDesc) {
		ContentDesc = contentDesc;
	}

	public String getContentImg() {
		return ContentImg;
	}

	public void setContentImg(String contentImg) {
		ContentImg = contentImg;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public int getCheckType() {
		return CheckType;
	}

	public void setCheckType(int checkType) {
		CheckType = checkType;
	}

}
