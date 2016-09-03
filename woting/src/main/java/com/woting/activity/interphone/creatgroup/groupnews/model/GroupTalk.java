package com.woting.activity.interphone.creatgroup.groupnews.model;

import java.util.List;

public class GroupTalk {
	private String ReturnType;
	private String SessionId;
	private String GroupId;
	private List<GroupTalkInside> UserList;
	
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
	public List<GroupTalkInside> getUserList() {
		return UserList;
	}
	public void setUserList(List<GroupTalkInside> userList) {
		UserList = userList;
	}
	public String getReturnType() {
		return ReturnType;
	}
	public void setReturnType(String returnType) {
		ReturnType = returnType;
	}
	public String getSessionId() {
		return SessionId;
	}
	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
}
