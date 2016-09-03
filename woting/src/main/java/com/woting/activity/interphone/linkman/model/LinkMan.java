package com.woting.activity.interphone.linkman.model;

import java.io.Serializable;

public class LinkMan implements Serializable{
	private String SessionId;
	private TalkPerson FriendList;
	private TalkGroup GroupList;
	
	public String getSessionId() {
		return SessionId;
	}
	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
	public TalkPerson getFriendList() {
		return FriendList;
	}
	public void setFriendList(TalkPerson friendList) {
		FriendList = friendList;
	}
	public TalkGroup getGroupList() {
		return GroupList;
	}
	public void setGroupList(TalkGroup groupList) {
		GroupList = groupList;
	}
}
