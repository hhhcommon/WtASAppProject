package com.woting.activity.interphone.commom.model;

import java.util.List;
/**
 * 具体处理内容
 * @author 辛龙
 *2016年5月4日
 */
public class Data {
	public String TalkId;
	public String SeqNum;//文件编号
	public String AudioData;//音频文件
	public String GroupId;
	public String BizType;
	public String TalkUserId;
	public String GroupPhoneNum;
	public List<ListInfo> InGroupUsers;
	public CallerInfo CallerInfo;
	public ListInfo UserInfo;
	public String CallerId;//呼叫者Id
	public String CallederId;//被叫者Id
	public String CallId;//呼叫Id，类似于组对讲中的TalkId
	public String DialType;//1正常呼叫，2仅通知
	public String OnLineType;//被叫者在线状态1在线；2不在线
	public String ACKType;//=1是可以通话，=2拒绝通话，=31被叫客户端超时应答，=32长时间不接听，服务器超时；
	//////////////////////////////////////////
	public InviteUserInfo InviteUserInfo;
	public BeInvitedUserInfo BeInvitedUserInfo;
	///////////////////////////
	public String InviteUserId;//邀请者
	public String BeInvitedUserId;//被邀请者
	public String RefuseMsg;//
	public long DealTime;//处理时间，毫秒数
	public long InviteTime;//处理时间，毫秒数
	public String DealType;
	public String FriendId;
	public GroupInfo GroupInfo;
	public ApplyUserInfo ApplyUserInfo;
	public long ApplyTime;
	
	
	
	public ApplyUserInfo getApplyUserInfo() {
		return ApplyUserInfo;
	}
	public void setApplyUserInfo(ApplyUserInfo applyUserInfo) {
		ApplyUserInfo = applyUserInfo;
	}
	public long getApplyTime() {
		return ApplyTime;
	}
	public void setApplyTime(long applyTime) {
		ApplyTime = applyTime;
	}
	public GroupInfo getGroupInfo() {
		return GroupInfo;
	}
	public void setGroupInfo(GroupInfo groupInfo) {
		GroupInfo = groupInfo;
	}
	public String getFriendId() {
		return FriendId;
	}
	public void setFriendId(String friendId) {
		FriendId = friendId;
	}
	public String getDealType() {
		return DealType;
	}
	public void setDealType(String dealType) {
		DealType = dealType;
	}
	public long getInviteTime() {
		return InviteTime;
	}
	public void setInviteTime(long inviteTime) {
		InviteTime = inviteTime;
	}
	public String getInviteUserId() {
		return InviteUserId;
	}
	public void setInviteUserId(String inviteUserId) {
		InviteUserId = inviteUserId;
	}
	public String getBeInvitedUserId() {
		return BeInvitedUserId;
	}
	public void setBeInvitedUserId(String beInvitedUserId) {
		BeInvitedUserId = beInvitedUserId;
	}
	public String getRefuseMsg() {
		return RefuseMsg;
	}
	public void setRefuseMsg(String refuseMsg) {
		RefuseMsg = refuseMsg;
	}
	public long getDealTime() {
		return DealTime;
	}
	public void setDealTime(long dealTime) {
		DealTime = dealTime;
	}
	public BeInvitedUserInfo getBeInvitedUserInfo() {
		return BeInvitedUserInfo;
	}
	public void setBeInvitedUserInfo(BeInvitedUserInfo beInvitedUserInfo) {
		BeInvitedUserInfo = beInvitedUserInfo;
	}
	public InviteUserInfo getInviteUserInfo() {
		return InviteUserInfo;
	}
	public void setInviteUserInfo(InviteUserInfo inviteUserInfo) {
		InviteUserInfo = inviteUserInfo;
	}
	public ListInfo getUserInfo() {
		return UserInfo;
	}
	public void setUserInfo(ListInfo userInfo) {
		UserInfo = userInfo;
	}
	public CallerInfo getCallerInfo() {
		return CallerInfo;
	}
	public void setCallerInfo(CallerInfo callerInfo) {
		CallerInfo = callerInfo;
	}
	public String getACKType() {
		return ACKType;
	}
	public void setACKType(String aCKType) {
		ACKType = aCKType;
	}
	public String getOnLineType() {
		return OnLineType;
	}
	public void setOnLineType(String onLineType) {
		OnLineType = onLineType;
	}

	public String getCallerId() {
		return CallerId;
	}
	public void setCallerId(String callerId) {
		CallerId = callerId;
	}
	public String getCallederId() {
		return CallederId;
	}
	public void setCallederId(String callederId) {
		CallederId = callederId;
	}
	public String getCallId() {
		return CallId;
	}
	public void setCallId(String callId) {
		CallId = callId;
	}
	public String getDialType() {
		return DialType;
	}
	public void setDialType(String dialType) {
		DialType = dialType;
	}


	public String getTalkId() {
		return TalkId;
	}
	public void setTalkId(String talkId) {
		TalkId = talkId;
	}
	public String getSeqNum() {
		return SeqNum;
	}
	public void setSeqNum(String seqNum) {
		SeqNum = seqNum;
	}
	public String getAudioData() {
		return AudioData;
	}
	public void setAudioData(String audioData) {
		AudioData = audioData;
	}
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
	public String getBizType() {
		return BizType;
	}
	public void setBizType(String bizType) {
		BizType = bizType;
	}
	public String getTalkUserId() {
		return TalkUserId;
	}
	public void setTalkUserId(String talkUserId) {
		TalkUserId = talkUserId;
	}
	public String getGroupPhoneNum() {
		return GroupPhoneNum;
	}
	public void setGroupPhoneNum(String groupPhoneNum) {
		GroupPhoneNum = groupPhoneNum;
	}
	public List<ListInfo> getInGroupUsers() {
		return InGroupUsers;
	}
	public void setInGroupUsers(List<ListInfo> inGroupUsers) {
		InGroupUsers = inGroupUsers;
	}


}
