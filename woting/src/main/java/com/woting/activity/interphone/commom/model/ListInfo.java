package com.woting.activity.interphone.commom.model;

import java.io.Serializable;

public class ListInfo implements Serializable{
	
    public String UserId;
    public String UserName;
    public String Portrait;
    public String InnerPhoneNum;
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
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
	public String getInnerPhoneNum() {
		return InnerPhoneNum;
	}
	public void setInnerPhoneNum(String innerPhoneNum) {
		InnerPhoneNum = innerPhoneNum;
	}
   
    
}
