package com.woting.activity.interphone.chat.model;

import java.io.Serializable;

public class ChatListInside implements Serializable{
	private String ObjType;			//类别 User，Group
	private String UserId;			//
	private String GroupId;			//
	private String UserName;		//
	private String GroupName;		//
	private String Portrait;		//个人头像
	private String InnerPhoneNum;	//
	private String GroupImg;		//小组图片
	private String CreateUserId;	//创建者userid
	private String GroupCount;
	private String CreateTime;
	public String getInnerPhoneNum() {
		return InnerPhoneNum;
	}
	public void setInnerPhoneNum(String innerPhoneNum) {
		InnerPhoneNum = innerPhoneNum;
	}
	public String getObjType() {
		return ObjType;
	}
	public void setObjType(String objType) {
		ObjType = objType;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getGroupName() {
		return GroupName;
	}
	public void setGroupName(String groupName) {
		GroupName = groupName;
	}
	public String getPortrait() {
		return Portrait;
	}
	public void setPortrait(String portrait) {
		Portrait = portrait;
	}
	public String getGroupImg() {
		return GroupImg;
	}
	public void setGroupImg(String groupImg) {
		GroupImg = groupImg;
	}
	public String getCreateUserId() {
		return CreateUserId;
	}
	public void setCreateUserId(String createUserId) {
		CreateUserId = createUserId;
	}
	public String getGroupCount() {
		return GroupCount;
	}
	public void setGroupCount(String groupCount) {
		GroupCount = groupCount;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
}