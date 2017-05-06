package com.woting.ui.interphone.notice.newfriend.model;

import com.woting.ui.interphone.model.UserInfo;

import java.io.Serializable;

/**
 * 新的朋友的消息对象
 */
public class MessageInFo extends UserInfo implements Serializable {
	public String MSType;
	public String InviteTime;
	public String InviteCount;
	public String GroupType;
	public String GroupSignature;
	public String GroupName;
	public String InviteMessage;
	public String GroupDescn;
	public String GroupNum;
	public String GroupImg;
	public String GroupId;
	private String GroupCreator;
	private String GroupManager;
	public int type=1;				//判断已接受状态的type 1=接受 2=已接受

	public String getMSType() {
		return MSType;
	}

	public void setMSType(String MSType) {
		this.MSType = MSType;
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

	public String getGroupImg() {
		return GroupImg;
	}

	public void setGroupImg(String groupImg) {
		GroupImg = groupImg;
	}

	public String getGroupId() {
		return GroupId;
	}

	public void setGroupId(String groupId) {
		GroupId = groupId;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
