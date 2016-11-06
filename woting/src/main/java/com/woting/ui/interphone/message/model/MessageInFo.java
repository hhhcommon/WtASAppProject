package com.woting.ui.interphone.message.model;

import java.io.Serializable;

public class MessageInFo implements Serializable {
	public String MSType;
	//GroupInfo
	public String InviteTime;
	public String InviteCount;
	public String GroupType;
	public String GroupSignature;
	public String GroupName;
	public String InviteMessage;
	public String UserName;
	public String UserId;
	public String PhoneNum;
	public String UserDescn;
	public String GroupDescn;
	public String GroupNum;
	public String Email;
	public String ProtraitMini;
	public String GroupImg;
	public String GroupId;
	private String GroupCreator;
	private String GroupManager;
	//UserInviteMeInside
	public String InviteMesage;
	public int type=1;				//判断已接受状态的type 1=接受 2=已接受
	private String UserAliasName;
	private String RealName;		//实名
	private String UserNum;			//用户码
	private String Descn;			//
	private String PortraitBig;		//
	private String Portrait;		//
	private String PortraitMini;	//
	
	
	
	public String getMSType() {
		return MSType;
	}
	public void setMSType(String mSType) {
		MSType = mSType;
	}
	public String getInviteMesage() {
		return InviteMesage;
	}
	public void setInviteMesage(String inviteMesage) {
		InviteMesage = inviteMesage;
	}
	public String getUserAliasName() {
		return UserAliasName;
	}
	public void setUserAliasName(String userAliasName) {
		UserAliasName = userAliasName;
	}
	public String getRealName() {
		return RealName;
	}
	public void setRealName(String realName) {
		RealName = realName;
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
	public String getPortraitBig() {
		return PortraitBig;
	}
	public void setPortraitBig(String portraitBig) {
		PortraitBig = portraitBig;
	}
	public String getPortrait() {
		return Portrait;
	}
	public void setPortrait(String portrait) {
		Portrait = portrait;
	}
	public String getPortraitMini() {
		return PortraitMini;
	}
	public void setPortraitMini(String portraitMini) {
		PortraitMini = portraitMini;
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
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}

	public int Type=1;

	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}
	public String getInviteTime() {
		return InviteTime;
	}
	public void setInviteTime(String inviteTime) {
		InviteTime = inviteTime;
	}
	public String getInviteCount() {
		return InviteCount;
	}
	public void setInviteCount(String inviteCount) {
		InviteCount = inviteCount;
	}
	public String getGroupType() {
		return GroupType;
	}
	public void setGroupType(String groupType) {
		GroupType = groupType;
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
	public String getInviteMessage() {
		return InviteMessage;
	}
	public void setInviteMessage(String inviteMessage) {
		InviteMessage = inviteMessage;
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
	public String getPhoneNum() {
		return PhoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		PhoneNum = phoneNum;
	}
	public String getUserDescn() {
		return UserDescn;
	}
	public void setUserDescn(String userDescn) {
		UserDescn = userDescn;
	}
	public String getGroupDescn() {
		return GroupDescn;
	}
	public void setGroupDescn(String groupDescn) {
		GroupDescn = groupDescn;
	}
	public String getGroupNum() {
		return GroupNum;
	}
	public void setGroupNum(String groupNum) {
		GroupNum = groupNum;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getProtraitMini() {
		return ProtraitMini;
	}
	public void setProtraitMini(String protraitMini) {
		ProtraitMini = protraitMini;
	}
	public String getGroupImg() {
		return GroupImg;
	}
	public void setGroupImg(String groupImg) {
		GroupImg = groupImg;
	}
}
