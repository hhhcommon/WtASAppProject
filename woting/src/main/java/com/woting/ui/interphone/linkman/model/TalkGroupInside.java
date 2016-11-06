package com.woting.ui.interphone.linkman.model;

import java.io.Serializable;

public class TalkGroupInside implements Serializable{
	private String GroupId;
	private String GroupNum;
	private String GroupManager;
	private String CreateTime;
	private String GroupCreator;
	private String GroupImg;
	private String GroupDesc;
	private String GroupCount;
	private String GroupType;		//组的类型
	private String GroupName;		//组名
	private String GroupMyAlias;	//别名
	private String GroupSignature;	//签名
	private String GroupMyDesc;
	private String GroupOriDescn;
	
	public String getGroupMyDesc() {
		return GroupMyDesc;
	}
	public void setGroupMyDesc(String groupMyDesc) {
		GroupMyDesc = groupMyDesc;
	}
	public String getGroupOriDescn() {
		return GroupOriDescn;
	}
	public void setGroupOriDescn(String groupOriDescn) {
		GroupOriDescn = groupOriDescn;
	}
	public String getGroupSignature() {
		return GroupSignature;
	}
	public void setGroupSignature(String groupSignature) {
		GroupSignature = groupSignature;
	}
	public String getGroupMyAlias() {
		return GroupMyAlias;
	}
	public void setGroupMyAlias(String groupMyAlias) {
		GroupMyAlias = groupMyAlias;
	}
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
	public String getGroupNum() {
		return GroupNum;
	}
	public void setGroupNum(String groupNum) {
		GroupNum = groupNum;
	}
	public String getGroupManager() {
		return GroupManager;
	}
	public void setGroupManager(String groupManager) {
		GroupManager = groupManager;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getGroupCreator() {
		return GroupCreator;
	}
	public void setGroupCreator(String groupCreator) {
		GroupCreator = groupCreator;
	}
	public String getGroupImg() {
		return GroupImg;
	}
	public void setGroupImg(String groupImg) {
		GroupImg = groupImg;
	}
	public String getGroupDesc() {
		return GroupDesc;
	}
	public void setGroupDesc(String groupDesc) {
		GroupDesc = groupDesc;
	}
	public String getGroupCount() {
		return GroupCount;
	}
	public void setGroupCount(String groupCount) {
		GroupCount = groupCount;
	}
	public String getGroupType() {
		return GroupType;
	}
	public void setGroupType(String groupType) {
		GroupType = groupType;
	}
	public String getGroupName() {
		return GroupName;
	}
	public void setGroupName(String groupName) {
		GroupName = groupName;
	}
}
