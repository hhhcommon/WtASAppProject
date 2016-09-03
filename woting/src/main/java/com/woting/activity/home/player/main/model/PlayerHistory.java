package com.woting.activity.home.player.main.model;

import java.io.Serializable;

/**
 * 播放历史的数据库表
 * @author 辛龙
 *2016年4月5日
 */
public class PlayerHistory implements Serializable{
	private String PlayerName;			//播放显示名称
	private String PlayerImage;			//播放显示图片
	private String PlayerUrl;			//播放路径
	private String PlayerMediaType;		//播放类型，radio，audio，sequ
	private String PlayerAllTime;		//播放文件总时长
	private String PlayerInTime;		//此时播放时长
	private String PlayerContentDesc;	//播放文件介绍
	private String PlayerNum;			//播放次数
	private String PlayerZanType;		//String类型的true,false
	private String PlayerFrom;			//预留字段
	private String PlayerFromId;		//预留字段
	private String PlayerAddTime;		//播放时间
	private String PlayerFromUrl;		//
	private String BJUserid;			//
	private String PlayContentShareUrl;
	private String PlayerUrI;
	private String ContentFavorite;
	private String ContentID;
	private String ContentPub;

	private String localurl;
	
	public String getLocalurl() {
		return localurl;
	}

	public void setLocalurl(String localurl) {
		this.localurl = localurl;
	}
	private int status;		//是否选中状态  0 未选中  1 选中
	private boolean isCheck;//是否可以选中
	
	@Override
	public String toString(){
		return "播放路径:" + getPlayerUrl() + ", 播放类型:" + getPlayerMediaType() 
				+ ", 播放显示名称:" + getPlayerName() + ", PlayerFromUrl:" + getPlayerFromUrl()
				+ ", 此时播放时长:" + getPlayerInTime() + ", 播放次数:" + getPlayerNum()
				+ ", ContentID:" + getContentID() + ", 播放时间:" + getPlayerAllTime()
				+ ", 播放类型:" + getPlayerMediaType();
	}
	
	public String getContentPub() {
		return ContentPub;
	}

	public void setContentPub(String contentPub) {
		ContentPub = contentPub;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public void setStatus(int status){
		this.status = status;
	}
	
	public int getStatus(){
		return this.status;
	}
 
	public String getContentID() {
		return ContentID;
	}

	public void setContentID(String contentID) {
		ContentID = contentID;
	}

	public String getContentFavorite() {
		return ContentFavorite;
	}

	public void setContentFavorite(String contentFavorite) {
		ContentFavorite = contentFavorite;
	}

	public String getPlayerUrI() {
		return PlayerUrI;
	}

	public void setPlayerUrI(String playerUrI) {
		PlayerUrI = playerUrI;
	}

	public String getPlayContentShareUrl() {
		return PlayContentShareUrl;
	}

	public void setPlayContentShareUrl(String playContentShareUrl) {
		PlayContentShareUrl = playContentShareUrl;
	}

	public String getPlayerFromUrl() {
		return PlayerFromUrl;
	}

	public void setPlayerFromUrl(String playerFromUrl) {
		PlayerFromUrl = playerFromUrl;
	}

	public String getBJUserid() {
		return BJUserid;
	}

	public void setBJUserid(String bJUserid) {
		BJUserid = bJUserid;
	}
	
	public PlayerHistory(String playername,String  playerimage,String playerurl,String playerurI,String playermediatype, 
			String playeralltime,String playerintime,String playercontentdesc,String playernum,
			String playerzantype,String  playerfrom,String playerfromid,String Playerfromurl,
			String playeraddtime,String bjuserid,String playcontentshareurl,String ContentFavorite,String ContentID,String localurl) {
		super();
		PlayerName = playername;
		PlayerImage = playerimage;
		PlayerUrl = playerurl;
		PlayerUrI = playerurI;
		PlayerMediaType = playermediatype;
		PlayerAllTime = playeralltime;
		PlayerInTime = playerintime;
		PlayerContentDesc = playercontentdesc;
		PlayerNum = playernum;
		PlayerZanType = playerzantype;
		PlayerFrom = playerfrom;
		PlayerFromId = playerfromid;
		PlayerFromUrl=Playerfromurl;
		PlayerAddTime=playeraddtime;
		BJUserid=bjuserid;
		PlayContentShareUrl=playcontentshareurl;
		this.ContentFavorite=ContentFavorite;
		this.ContentID=ContentID;
		this.localurl=localurl;
	}

	public String getPlayerAddTime() {
		return PlayerAddTime;
	}

	public void setPlayerAddTime(String playerAddTime) {
		PlayerAddTime = playerAddTime;
	}

	public String getPlayerName() {
		return PlayerName;
	}
	public void setPlayerName(String playerName) {
		PlayerName = playerName;
	}
	public String getPlayerImage() {
		return PlayerImage;
	}
	public void setPlayerImage(String playerImage) {
		PlayerImage = playerImage;
	}
	public String getPlayerUrl() {
		return PlayerUrl;
	}
	public void setPlayerUrl(String playerUrl) {
		PlayerUrl = playerUrl;
	}
	public String getPlayerMediaType() {
		return PlayerMediaType;
	}
	public void setPlayerMediaType(String playerMediaType) {
		PlayerMediaType = playerMediaType;
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
	public String getPlayerContentDesc() {
		return PlayerContentDesc;
	}
	public void setPlayerContentDesc(String playerContentDesc) {
		PlayerContentDesc = playerContentDesc;
	}
	public String getPlayerNum() {
		return PlayerNum;
	}
	public void setPlayerNum(String playerNum) {
		PlayerNum = playerNum;
	}
	public String getPlayerZanType() {
		return PlayerZanType;
	}
	public void setPlayerZanType(String playerZanType) {
		PlayerZanType = playerZanType;
	}
	public String getPlayerFrom() {
		return PlayerFrom;
	}
	public void setPlayerFrom(String playerFrom) {
		PlayerFrom = playerFrom;
	}
	public String getPlayerFromId() {
		return PlayerFromId;
	}
	public void setPlayerFromId(String playerFromId) {
		PlayerFromId = playerFromId;
	}
}
