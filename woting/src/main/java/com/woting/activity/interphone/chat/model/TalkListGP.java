package com.woting.activity.interphone.chat.model;

import java.io.Serializable;

public class TalkListGP implements Serializable{
	private String truename;
	private String Name;
	private String UserAliasName;
	private String Portrait;
	private String Id;
	private String GroupNum;
	private String GroupManager;
	private String CreateTime;
	private String GroupCreator;
	private String GroupImg;
	private String GroupDesc;
	private String GroupCount;
	private String GroupType;		// 组的类型
	private String GroupMyAlias;	// 别名
	private String GroupSignature;	// 签名
	private String TyPe;			// 类别 user，group
	private String AddTime;			// 添加时间
	private String UserNum;			// 用户码
	private String Descn;			//

	public String getAddTime() {
		return AddTime;
	}
	public void setAddTime(String addTime) {
		AddTime = addTime;
	}
	public String getUserNum() {
		return UserNum;
	}
	public void setUserNum(String userNum) {
		UserNum = userNum;
	}
	public String getDescn() {
		return Descn;
	}
	public void setDescn(String descn) {
		Descn = descn;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getTyPe() {
		return TyPe;
	}
	public void setTyPe(String tyPe) {
		TyPe = tyPe;
	}
	public String getTruename() {
		return truename;
	}
	public void setTruename(String truename) {
		this.truename = truename;
	}
	public String getUserAliasName() {
		return UserAliasName;
	}
	public void setUserAliasName(String userAliasName) {
		UserAliasName = userAliasName;
	}
	public String getPortrait() {
		return Portrait;
	}
	public void setPortrait(String portrait) {
		Portrait = portrait;
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
}
