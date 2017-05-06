package com.woting.ui.interphone.model;


import java.io.Serializable;

public class UserInviteMeInside extends UserInfo implements Serializable {
	public String InviteTime;
	public String InviteMessage;
	public int type=1;				//判断已接受状态的type 1=接受 2=已接受

	public String getInviteTime() {
		return InviteTime;
	}

	public void setInviteTime(String inviteTime) {
		InviteTime = inviteTime;
	}

	public String getInviteMessage() {
		return InviteMessage;
	}

	public void setInviteMessage(String inviteMessage) {
		InviteMessage = inviteMessage;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
