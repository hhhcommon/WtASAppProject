package com.woting.activity.interphone.find.findresult.model;

import java.io.Serializable;

public class FindGroupNews implements Serializable{
	private String GroupName;   
	private String GroupType;
	private String GroupCreator;
	private String GroupManager;
	private String GroupImg;
	private String GroupNum;		//群号
	private String GroupId;
	private String GroupOriDesc;
	private String GroupMyAlias;	//别名
	private String GroupSignature;	//签名
	
	public String getGroupOriDesc() {
		return GroupOriDesc;
	}
	public void setGroupOriDesc(String groupOriDesc) {
		GroupOriDesc = groupOriDesc;
	}
	public String getGroupMyAlias() {
		return GroupMyAlias;
	}
	public void setGroupMyAlias(String groupMyAlias) {
		GroupMyAlias = groupMyAlias;
	}
	public String getGroupSignature() {
		return GroupSignature;
	}
	public void setGroupSignature(String groupSignature) {
		GroupSignature = groupSignature;
	}
	public String getGroupName() {
		return GroupName;
	}
	public void setGroupName(String groupName) {
		GroupName = groupName;
	}
	public String getGroupType() {
		return GroupType;
	}
	public void setGroupType(String groupType) {
		GroupType = groupType;
	}
	public String getGroupCreator() {
		return GroupCreator;
	}
	public void setGroupCreator(String groupCreator) {
		GroupCreator = groupCreator;
	}
	public String getGroupManager() {
		return GroupManager;
	}
	public void setGroupManager(String groupManager) {
		GroupManager = groupManager;
	}
	public String getGroupImg() {
		return GroupImg;
	}
	public void setGroupImg(String groupImg) {
		GroupImg = groupImg;
	}
	public String getGroupNum() {
		return GroupNum;
	}
	public void setGroupNum(String groupNum) {
		GroupNum = groupNum;
	}
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
}
