package com.woting.ui.interphone.group.groupcontrol.groupnews.model;

import java.io.Serializable;

public class GroupTalkInside implements Serializable{
	private static final long serialVersionUID = 1503184577730080277L;
	private String UserId;
	private String UserName;
	private String UserAliasName;
	private String RealName;		//实名
	private String UserNum;			//用户码
	private String PhoneNum;		//用户主手机号
	private String Email;			//
	private String Descn;			//
	private String PortraitBig;		//
	private String PortraitMini;	//
	
	public String getUserAliasName() {
		return UserAliasName;
	}
	public void setUserAliasName(String userAliasName) {
		UserAliasName = userAliasName;
	}
	public String getRealName() {
		return RealName;
	}
	public void setRealName(String realName) {
		RealName = realName;
	}
	public String getUserNum() {
		return UserNum;
	}
	public void setUserNum(String userNum) {
		UserNum = userNum;
	}
	public String getPhoneNum() {
		return PhoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		PhoneNum = phoneNum;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getDescn() {
		return Descn;
	}
	public void setDescn(String descn) {
		Descn = descn;
	}
	public String getPortraitBig() {
		return PortraitBig;
	}
	public void setPortraitBig(String portraitBig) {
		PortraitBig = portraitBig;
	}
	public String getPortraitMini() {
		return PortraitMini;
	}
	public void setPortraitMini(String portraitMini) {
		PortraitMini = portraitMini;
	}
	private int Type=1;
	
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
}
