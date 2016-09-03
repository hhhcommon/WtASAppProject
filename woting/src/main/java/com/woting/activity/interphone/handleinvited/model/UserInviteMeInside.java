package com.woting.activity.interphone.handleinvited.model;

import java.io.Serializable;

public class UserInviteMeInside implements Serializable {
	public String Portrait;
	public String InviteMesage;
	public String UserName;
	public String UserId;
	public int type=1;		//判断已接受状态的type 1=接受 2=已接受
	
	public String getPortrait() {
		return Portrait;
	}
	public void setPortrait(String portrait) {
		Portrait = portrait;
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
