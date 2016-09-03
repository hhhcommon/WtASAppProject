package com.woting.activity.home.program.radiolist.mode;

import java.io.Serializable;
import java.util.List;

public class ListInfo implements Serializable{
	private static final long serialVersionUID = -7915567429691969155L;

	private String ContentURI;
	private List<ContentCatalogs> ContentCatalogs;
	private String PlayCount;
	private String CTime;
	private String ContentKeyWord;
	private String ContentSubjectWord;
	private String ContentTimes;
	private String ContentName;
	private String ContentPubTime;
	private String ContentPub;
	private String ContentPlay;
	private String MediaType;
	private String ContentId;
	private String ContentShareURL;
	private String ContentDesc;
	private String ContentImg;
	private String ContentFavorite;
	private String Localurl;

	public String getLocalurl() {
		return Localurl;
	}
	public void setLocalurl(String localurl) {
		Localurl = localurl;
	}
	public String getContentURI() {
		return ContentURI;
	}
	public void setContentURI(String contentURI) {
		ContentURI = contentURI;
	}
	public List<ContentCatalogs> getContentCatalogs() {
		return ContentCatalogs;
	}
	public void setContentCatalogs(List<ContentCatalogs> contentCatalogs) {
		ContentCatalogs = contentCatalogs;
	}
	public String getPlayCount() {
		return PlayCount;
	}
	public void setPlayCount(String playCount) {
		PlayCount = playCount;
	}
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
	public String getContentPlay() {
		return ContentPlay;
	}
	public void setContentPlay(String contentPlay) {
		ContentPlay = contentPlay;
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
	public String getContentShareURL() {
		return ContentShareURL;
	}
	public void setContentShareURL(String contentShareURL) {
		ContentShareURL = contentShareURL;
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
	public String getContentFavorite() {
		return ContentFavorite;
	}
	public void setContentFavorite(String contentFavorite) {
		ContentFavorite = contentFavorite;
	}
}
