package com.woting.ui.interphone.commom.model;

import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.common.model.UserInfo;
import java.io.Serializable;
import java.util.List;

/**
 * author：辛龙 (xinLong)
 * 2016/12/20 21:46
 * 邮箱：645700751@qq.com
 * 在MainActivity中的4.4的组通知消息对象
 */

public class MessageForMainGroup implements Serializable {
    public String GroupId;//字符串
    public GroupInfo GroupInfo;
    public List<String> GroupEntryUserIds;
    public List<UserInfo> GroupUserList;

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public com.woting.ui.common.model.GroupInfo getGroupInfo() {
        return GroupInfo;
    }

    public void setGroupInfo(com.woting.ui.common.model.GroupInfo groupInfo) {
        GroupInfo = groupInfo;
    }

    public List<String> getGroupEntryUserIds() {
        return GroupEntryUserIds;
    }

    public void setGroupEntryUserIds(List<String> groupEntryUserIds) {
        GroupEntryUserIds = groupEntryUserIds;
    }

    public List<UserInfo> getGroupUserList() {
        return GroupUserList;
    }

    public void setGroupUserList(List<UserInfo> groupUserList) {
        GroupUserList = groupUserList;
    }
}
