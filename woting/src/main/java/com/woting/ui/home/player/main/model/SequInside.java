package com.woting.ui.home.player.main.model;

import com.woting.ui.home.program.album.model.PersonInfo;

import java.io.Serializable;
import java.util.List;

public class SequInside implements Serializable{

	private String ContentURI;
	private List<PersonInfo> ContentPersons;
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
	private String ContentDesc;
	private String ContentImg;
	private String PlayerAllTime;
	private String PlayerInTime;

	public String getContentURI() {
		return ContentURI;
	}
	public void setContentURI(String contentURI) {
		ContentURI = contentURI;
	}

	public List<PersonInfo> getContentPersons() {
		return ContentPersons;
	}

	public void setContentPersons(List<PersonInfo> contentPersons) {
		ContentPersons = contentPersons;
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
	public String getPlayerAllTime() {
		return PlayerAllTime;
	}
	public void setPlayerAllTime(String playerAllTime) {
		PlayerAllTime = playerAllTime;
	}
	public String getPlayerInTime() {
		return PlayerInTime;
	}
	public void setPlayerInTime(String playerInTime) {
		PlayerInTime = playerInTime;
	}

}
