package com.woting.ui.interphone.model;
import com.woting.ui.interphone.model.UserInfo;
import com.woting.ui.interphone.model.GroupInfo;
import java.util.List;

/**
 * 具体处理内容
 * @author 辛龙
 *2016年5月4日
 */
public class Data {
	private String TalkId;
	private String SeqNum;//文件编号
	private String AudioData;//音频文件
	private String GroupId;
	private String BizType;
	private String TalkUserId;
	private String GroupPhoneNum;
	private List<UserInfo> InGroupUsers;
	private UserInfo UserInfo;
	private String CallerId;//呼叫者Id
	private String CallederId;//被叫者Id
	private String CallId;//呼叫Id，类似于组对讲中的TalkId
	private String DialType;//1正常呼叫，2仅通知
	private String OnLineType;//被叫者在线状态1在线；2不在线
	public String ACKType;//=1是可以通话，=2拒绝通话，=31被叫客户端超时应答，=32长时间不接听，服务器超时；
	//////////////////////////////////////////
	private UserInfo InviteUserInfo;
	private UserInfo BeInvitedUserInfo;
	///////////////////////////
	private String InviteUserId;//邀请者
	private String BeInvitedUserId;//被邀请者
	private String RefuseMsg;//
	private long DealTime;//处理时间，毫秒数
	private long InviteTime;//处理时间，毫秒数
	private String DealType;
	private String FriendId;
	private GroupInfo GroupInfo;
	private UserInfo ApplyUserInfo;
	private long ApplyTime;
	private UserInfo CallerInfo;
	private UserInfo CallederInfo;

	public List<com.woting.ui.interphone.model.UserInfo> getInGroupUsers() {
		return InGroupUsers;
	}

	public void setInGroupUsers(List<com.woting.ui.interphone.model.UserInfo> inGroupUsers) {
		InGroupUsers = inGroupUsers;
	}

	public com.woting.ui.interphone.model.UserInfo getUserInfo() {
		return UserInfo;
	}

	public void setUserInfo(com.woting.ui.interphone.model.UserInfo userInfo) {
		UserInfo = userInfo;
	}

	public com.woting.ui.interphone.model.UserInfo getInviteUserInfo() {
		return InviteUserInfo;
	}

	public void setInviteUserInfo(com.woting.ui.interphone.model.UserInfo inviteUserInfo) {
		InviteUserInfo = inviteUserInfo;
	}

	public com.woting.ui.interphone.model.UserInfo getBeInvitedUserInfo() {
		return BeInvitedUserInfo;
	}

	public void setBeInvitedUserInfo(com.woting.ui.interphone.model.UserInfo beInvitedUserInfo) {
		BeInvitedUserInfo = beInvitedUserInfo;
	}

	public com.woting.ui.interphone.model.GroupInfo getGroupInfo() {
		return GroupInfo;
	}

	public void setGroupInfo(com.woting.ui.interphone.model.GroupInfo groupInfo) {
		GroupInfo = groupInfo;
	}

	public com.woting.ui.interphone.model.UserInfo getApplyUserInfo() {
		return ApplyUserInfo;
	}

	public void setApplyUserInfo(com.woting.ui.interphone.model.UserInfo applyUserInfo) {
		ApplyUserInfo = applyUserInfo;
	}

	public com.woting.ui.interphone.model.UserInfo getCallerInfo() {
		return CallerInfo;
	}

	public void setCallerInfo(com.woting.ui.interphone.model.UserInfo callerInfo) {
		CallerInfo = callerInfo;
	}

	public com.woting.ui.interphone.model.UserInfo getCallederInfo() {
		return CallederInfo;
	}

	public void setCallederInfo(com.woting.ui.interphone.model.UserInfo callederInfo) {
		CallederInfo = callederInfo;
	}

	public long getApplyTime() {
		return ApplyTime;
	}
	public void setApplyTime(long applyTime) {
		ApplyTime = applyTime;
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


}
