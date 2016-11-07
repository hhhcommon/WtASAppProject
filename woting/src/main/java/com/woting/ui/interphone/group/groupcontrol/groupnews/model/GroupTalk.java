package com.woting.ui.interphone.group.groupcontrol.groupnews.model;

import com.woting.ui.common.model.UserInfo;

import java.util.List;

public class GroupTalk {
	private String ReturnType;
	private String SessionId;
	private String GroupId;
	private List<UserInfo> UserList;
	
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
	public List<UserInfo> getUserList() {
		return UserList;
	}
	public void setUserList(List<UserInfo> userList) {
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
