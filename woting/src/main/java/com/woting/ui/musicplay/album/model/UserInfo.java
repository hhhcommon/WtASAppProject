package com.woting.ui.musicplay.album.model;

import java.io.Serializable;

//用户信息实体类
public class UserInfo implements Serializable {

	private String InnerPhoneNum;
	private String UserName;
	private String UserId;
	private String Email;
	private String ProtraitBig;
	private String PhoneNum;
	private String ProtraitMini;
	private String Url;
    private int Type=1;//标记是否为最后一项的，新加入尾部的数据会设置type为2，在adapter里针对此属性设置对应的gridview
    private int CheckType=1;//标记item是否被选中，1为未选中，2为选中
    
	public String getUrl() {
		return Url;
	}
	public void setUrl(String url) {
		Url = url;
	}
	public int getCheckType() {
		return CheckType;
	}
	public void setCheckType(int checkType) {
		CheckType = checkType;
	}
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}

	public String getInnerPhoneNum() {
		return InnerPhoneNum;
	}

	public void setInnerPhoneNum(String innerPhoneNum) {
		InnerPhoneNum = innerPhoneNum;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getProtraitBig() {
		return ProtraitBig;
	}

	public void setProtraitBig(String protraitBig) {
		ProtraitBig = protraitBig;
	}

	public String getPhoneNum() {
		return PhoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		PhoneNum = phoneNum;
	}

	public String getProtraitMini() {
		return ProtraitMini;
	}

	public void setProtraitMini(String protraitMini) {
		ProtraitMini = protraitMini;
	}

}
