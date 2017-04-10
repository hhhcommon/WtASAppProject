package com.woting.ui.interphone.commom.model;

import java.io.Serializable;

public class UserInfo implements Serializable{
	
    public String userId;
    public String NickName;
    public String LoginName;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return NickName;
	}

	public void setNickName(String nickName) {
		NickName = nickName;
	}

	public String getLoginName() {
		return LoginName;
	}
	public void setLoginName(String loginName) {
		LoginName = loginName;
	}
    
    
}
