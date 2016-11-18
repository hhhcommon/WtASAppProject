package com.woting.ui.mine.model;
/**
 * 个人信息公共对象
 * 作者：xinlong on 2016/7/19 21:18
 * 邮箱：645700751@qq.com
 */
public class UserPortaitInside {
	private String SessionId;
	private String ReturnType;
	private String success;
	private String MiniUri;
	private String BigUri;
	private String PortraitMini;
	private String PortraitBig;
	private String GroupImg;
	
	public String getGroupImg() {
		return GroupImg;
	}
	public void setGroupImg(String groupImg) {
		GroupImg = groupImg;
	}
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
}
