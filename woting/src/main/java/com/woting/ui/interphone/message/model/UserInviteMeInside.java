package com.woting.ui.interphone.message.model;

import java.io.Serializable;

public class UserInviteMeInside implements Serializable {
	public String InviteTime;
	public String InviteMesage;
	public String UserName;
	public String UserId;
	public int type=1;				//判断已接受状态的type 1=接受 2=已接受
	private String UserAliasName;
	private String RealName;		//实名
	private String UserNum;			//用户码
	private String PhoneNum;		//用户主手机号
	private String Email;			//
	private String Descn;			//
	private String PortraitBig;		//
	private String Portrait;		//
	private String PortraitMini;	//
	
	public String getInviteTime() {
		return InviteTime;
	}
	public void setInviteTime(String inviteTime) {
		InviteTime = inviteTime;
	}
	public String getPortrait() {
		return Portrait;
	}
	public void setPortrait(String portrait) {
		Portrait = portrait;
	}
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getInviteMesage() {
		return InviteMesage;
	}
	public void setInviteMesage(String inviteMesage) {
		InviteMesage = inviteMesage;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
}
