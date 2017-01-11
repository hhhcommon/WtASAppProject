package com.woting.ui.home.player.main.model;

import java.io.Serializable;

public class LanguageSearchInside implements Serializable{
	private String Type="1";
	private String ContentURI;
//	private String ContentPersons;
	private String ContentKeyWord;
	private String cTime;
	private String ContentSubjectWord;
	private String ContentTimes;
	private String ContentName;
	private String ContentPubTime;
	private String ContentPub;
	private String ContentPlay;
	private String MediaType;
	private String ContentId;
	private String ContentDescn;
	private String ContentImg;
	private String PlayerAllTime;
	private String PlayerInTime;
	private String PlayCount;
	private SequInside SeqInfo;
	private String ContentShareURL;
	private String ContentFavorite;
	private String localurl;
	private String sequId;//专辑ID
	private String sequImg;//专辑图片
	private String sequDesc;//专辑描述
	private String sequName;//专辑名称
	private String playTag;         // 标签<预留>
	private String ContentPlayType; // 内容后缀
	private String IsPlaying;

	public String getIsPlaying() {
		return IsPlaying;
	}

	public void setIsPlaying(String isPlaying) {
		IsPlaying = isPlaying;
	}

	public String getContentPlayType() {
		return ContentPlayType;
	}

	public String getPlayTag() {
		return playTag;
	}

	public void setContentPlayType(String contentPlayType) {
		ContentPlayType = contentPlayType;
	}

	public void setPlayTag(String playTag) {
		this.playTag = playTag;
	}

	public String getSequId() {
		return sequId;
	}

	public void setSequId(String sequId) {
		this.sequId = sequId;
	}

	public String getSequImg() {
		return sequImg;
	}

	public void setSequImg(String sequImg) {
		this.sequImg = sequImg;
	}

	public String getSequDesc() {
		return sequDesc;
	}

	public void setSequDesc(String sequDesc) {
		this.sequDesc = sequDesc;
	}

	public String getSequName() {
		return sequName;
	}

	public void setSequName(String sequName) {
		this.sequName = sequName;
	}

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
	public SequInside getSeqInfo() {
		return SeqInfo;
	}
	public void setSeqInfo(SequInside seqInfo) {
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
//	public String getContentPersons() {
//		return ContentPersons;
//	}
//	public void setContentPersons(String contentPersons) {
//		ContentPersons = contentPersons;
//	}
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
	public String getContentDescn() {
		return ContentDescn;
	}
	public void setContentDescn(String contentDesc) {
		ContentDescn = contentDesc;
	}
	public String getContentImg() {
		return ContentImg;
	}
	public void setContentImg(String contentImg) {
		ContentImg = contentImg;
	}
}
