package com.woting.ui.interphone.chat.model;

import java.util.List;

public class ChatList {
	private String ReturnType;
	private String SessionId;
	private List<ChatListInside> HistoryList;
	
	public List<ChatListInside> getHistoryList() {
		return HistoryList;
	}
	public void setHistoryList(List<ChatListInside> historyList) {
		HistoryList = historyList;
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
