package com.woting.ui.common.scanning.model;

import com.woting.ui.model.GroupInfo;
import com.woting.ui.interphone.model.UserInviteMeInside;
import java.io.Serializable;

/**
 * 扫描结果
 * @author 辛龙
 * 2016年5月5日
 */
public class MessageInfo implements Serializable{
	public String Type;
	public UserInviteMeInside UserInviteMeInside;
	public GroupInfo FindGroupNews;
	
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public UserInviteMeInside getUserInviteMeInside() {
		return UserInviteMeInside;
	}
	public void setUserInviteMeInside(UserInviteMeInside userInviteMeInside) {
		UserInviteMeInside = userInviteMeInside;
	}
	public GroupInfo getFindGroupNews() {
		return FindGroupNews;
	}
	public void setFindGroupNews(GroupInfo findGroupNews) {
		FindGroupNews = findGroupNews;
	}
}
