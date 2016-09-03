package com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel.model;

import java.io.Serializable;

//用户信息实体类
public class UserInfo implements Serializable {
	private String name;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	private String InnerPhoneNum;
	private String UserName;
	private String UserId;
	private String Email;
	private String PortraitBig;
	private String PhoneNum;
	private String PortraitMini;
    private int Type=1;//标记是否为最后一项的，新加入尾部的数据会设置type为2，在adapter里针对此属性设置对应的gridview
    private int CheckType=1;//标记item是否被选中，1为未选中，2为选中
    private String pinYinName;
	private String UserAliasName;
	private String truename;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getPortraitBig() {
		return PortraitBig;
	}
	public void setPortraitBig(String portraitBig) {
		PortraitBig = portraitBig;
	}
	public String getPortraitMini() {
		return PortraitMini;
	}
	public void setPortraitMini(String portraitMini) {
		PortraitMini = portraitMini;
	}
	public String getPinYinName() {
		return pinYinName;
	}
	public void setPinYinName(String pinYinName) {
		this.pinYinName = pinYinName;
	}
	public String getUserAliasName() {
		return UserAliasName;
	}
	public void setUserAliasName(String userAliasName) {
		UserAliasName = userAliasName;
	}
	public String getTruename() {
		return truename;
	}
	public void setTruename(String truename) {
		this.truename = truename;
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

	public String getPhoneNum() {
		return PhoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		PhoneNum = phoneNum;
	}


}
