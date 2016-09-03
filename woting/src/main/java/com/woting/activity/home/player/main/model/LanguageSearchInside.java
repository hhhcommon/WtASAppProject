package com.woting.activity.home.player.main.model;

import java.io.Serializable;

public class LanguageSearchInside implements Serializable{
	private String Type="1";
	private String ContentURI;
	private String ContentPersons;  
/*	private String ContentCatalogs;*/
	private String ContentKeyWord;
	private String cTime;
	private String ContentSubjectWord;
	private String ContentTimes;
	private String ContentName;  
	private String ContentPubTime;
	private String ContentPub;
//	private String ContentSource;
	private String ContentPlay;
	private String MediaType;
	private String ContentId;
	private String ContentDesc;
	private String ContentImg;
	private String PlayerAllTime;
	private String PlayerInTime;
	private String PlayCount;
	private sequinside SeqInfo;
	private String ContentShareURL;
	private String ContentFavorite;
	private String localurl;
	
	public String getPlayCount() {
		return PlayCount;
	}
	public void setPlayCount(String playCount) {
		PlayCount = playCount;
	}
	public String getLocalurl() {
		return localurl;
	}
	public void setLocalurl(String localurl) {
		this.localurl = localurl;
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
	public sequinside getSeqInfo() {
		return SeqInfo;
	}
	public void setSeqInfo(sequinside seqInfo) {
		SeqInfo = seqInfo;
	}
	public String getPlayerInTime() {
		return PlayerInTime;
	}
	public void setPlayerInTime(String playerInTime) {
		PlayerInTime = playerInTime;
	}
	public String getPlayerAllTime() {
		return PlayerAllTime;
	}
	public void setPlayerAllTime(String playerAllTime) {
		PlayerAllTime = playerAllTime;
	}
	public String getContentPersons() {
		return ContentPersons;
	}
	public void setContentPersons(String contentPersons) {
		ContentPersons = contentPersons;
	}
/*	public String getContentCatalogs() {
		return ContentCatalogs;
	}
	public void setContentCatalogs(String contentCatalogs) {
		ContentCatalogs = contentCatalogs;
	}*/
	public String getContentPlay() {
		return ContentPlay;
	}
	public void setContentPlay(String contentPlay) {
		ContentPlay = contentPlay;
	}
	public String getContentURI() {
		return ContentURI;
	}
	public void setContentURI(String contentURI) {
		ContentURI = contentURI;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getContentKeyWord() {
		return ContentKeyWord;
	}
	public void setContentKeyWord(String contentKeyWord) {
		ContentKeyWord = contentKeyWord;
	}
	public String getcTime() {
		return cTime;
	}
	public void setcTime(String cTime) {
		this.cTime = cTime;
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

	
	
	
}
