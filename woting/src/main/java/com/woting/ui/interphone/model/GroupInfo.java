package com.woting.ui.interphone.model;

import java.io.Serializable;

public class GroupInfo implements Serializable {
    private String InviteTime;
    private String InviteCount;
    private String InviteMessage;
    private String NickName;
    private String UserId;
    private String PhoneNum;
    private String UserDescn;
    private String Email;
    private String GroupDescn;
    private String GroupName;
    private String GroupNum;        //群号
    private String GroupImg;
    private String GroupId;
    private String GroupType;   //组的类型
    private String GroupCreator;
    private String GroupManager;
    private String GroupCount;
    private String CreateTime;
    private String InnerPhoneNum;
    private String GroupOriDescn;
    private String GroupMyAlias;	//别名
    private String GroupSignature;   //签名
    private String GroupMyDescn;
    private int Type = 1;
    private String truename;
    private String Name;
    private String UserAliasName;
    private String Portrait;
    private String Id;
    private String TyPe;			// 类别 user，group
    private String AddTime;			// 添加时间
    private String UserNum;			// 用户码
    private String Descn;			//
    private String UserNames;
    private String UserIds;
    private String GroupMasterId;   // 群管理员
    private String InviteUserId ;   // 邀请进群的人
    private UserInfo inviteUserInfo;
    private String Sex;
    private String Region;
    private String UserSign;
    private String GroupPassword;

    public UserInfo getInviteUserInfo() {
        return inviteUserInfo;
    }

    public void setInviteUserInfo(UserInfo inviteUserInfo) {
        this.inviteUserInfo = inviteUserInfo;
    }

    public void setGroupPassword(String groupPassword) {
        GroupPassword = groupPassword;
    }

    public String getGroupPassword() {
        return GroupPassword;
    }

    public void setUserSign(String userSign) {
        UserSign = userSign;
    }

    public String getUserSign() {
        return UserSign;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }


    public String getInviteUserId() {
        return InviteUserId;
    }

    public void setInviteUserId(String inviteUserId) {
        InviteUserId = inviteUserId;
    }

    public void setGroupMasterId(String groupMasterId) {
        GroupMasterId = groupMasterId;
    }

    public String getGroupMasterId() {
        return GroupMasterId;
    }

    public String getUserIds() {
        return UserIds;
    }

    public void setUserIds(String userIds) {
        UserIds = userIds;
    }

    public String getUserNames() {
        return UserNames;
    }

    public void setUserNames(String userNames) {
        UserNames = userNames;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

//    public String getGroupDesc() {
//        return GroupDesc;
//    }
//
//    public void setGroupDesc(String groupDesc) {
//        GroupDesc = groupDesc;
//    }

    public String getTyPe() {
        return TyPe;
    }

    public void setTyPe(String tyPe) {
        TyPe = tyPe;
    }

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

    public String getInviteMessage() {
        return InviteMessage;
    }

    public void setInviteMessage(String inviteMessage) {
        InviteMessage = inviteMessage;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPortraitMini(String PortraitMini) {
        PortraitMini = PortraitMini;
    }

    public String getGroupDescn() {
        return GroupDescn;
    }

    public void setGroupDescn(String groupDescn) {
        GroupDescn = groupDescn;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
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

    public String getInnerPhoneNum() {
        return InnerPhoneNum;
    }

    public void setInnerPhoneNum(String innerPhoneNum) {
        InnerPhoneNum = innerPhoneNum;
    }

    public String getGroupOriDescn() {
        return GroupOriDescn;
    }

    public void setGroupOriDescn(String groupOriDescn) {
        GroupOriDescn = groupOriDescn;
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

    public String getGroupMyDescn() {
        return GroupMyDescn;
    }

    public void setGroupMyDescn(String groupMyDescn) {
        GroupMyDescn = groupMyDescn;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
}
