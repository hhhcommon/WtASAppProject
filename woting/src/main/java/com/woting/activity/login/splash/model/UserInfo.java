package com.woting.activity.login.splash.model;
/**
 * splash的model
 * @author 辛龙
 *2016年8月8日
 */
public class UserInfo {
	public String UserId;//用户id
	public String UserName;//用户名
	public String PortraitMini;//头像缩略图
	public String PortraitBig;//头像大图

	public String getPortraitMini() {
		return PortraitMini;
	}
	public void setPortraitMini(String portraitMini) {
		PortraitMini = portraitMini;
	}
	public String getPortraitBig() {
		return PortraitBig;
	}
	public void setPortraitBig(String portraitBig) {
		PortraitBig = portraitBig;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
}
