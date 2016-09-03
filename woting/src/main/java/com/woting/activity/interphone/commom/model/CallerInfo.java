package com.woting.activity.interphone.commom.model;

import java.io.Serializable;

public class CallerInfo implements Serializable{
	
    public String UserName;
    public String Portrait;
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getPortrait() {
		return Portrait;
	}
	public void setPortrait(String portrait) {
		Portrait = portrait;
	}
    
}
