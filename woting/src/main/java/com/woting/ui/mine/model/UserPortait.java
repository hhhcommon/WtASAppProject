package com.woting.ui.mine.model;

import java.util.List;

public class UserPortait {
	private String SessionId;
	private String ReturnType;
	private String success;
	private String MiniUri;
	private String BigUri;
	private String PortraitMini;
	private String PortraitBig;
	private List<UserPortaitInside> data;
	private String jsonType;

	public String getPortraitMini() {
		return PortraitMini;
	}
	public void setPortraitMini(String portraitMini) {
		PortraitMini = portraitMini;
	}
	public String getPortraitBig() {
		return PortraitBig;
	}
	public void setPortraitBig(String portraitBig) {
		PortraitBig = portraitBig;
	}
	public String getSessionId() {
		return SessionId;
	}
	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
	public String getReturnType() {
		return ReturnType;
	}
	public void setReturnType(String returnType) {
		ReturnType = returnType;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getMiniUri() {
		return MiniUri;
	}
	public void setMiniUri(String miniUri) {
		MiniUri = miniUri;
	}
	public String getBigUri() {
		return BigUri;
	}
	public void setBigUri(String bigUri) {
		BigUri = bigUri;
	}
	public List<UserPortaitInside> getData() {
		return data;
	}
	public void setData(List<UserPortaitInside> data) {
		this.data = data;
	}
	public String getJsonType() {
		return jsonType;
	}
	public void setJsonType(String jsonType) {
		this.jsonType = jsonType;
	}
}
