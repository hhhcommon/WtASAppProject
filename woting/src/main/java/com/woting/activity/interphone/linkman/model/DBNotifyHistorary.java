package com.woting.activity.interphone.linkman.model;

import java.io.Serializable;

public class DBNotifyHistorary implements Serializable{
	private String BJUserId;//本机userid
	private String TyPe;//通知类型
	private String ImageUrl;//图片路径
	private String 	Title;//标题
	private String Content;//内容
	private String DealTime;//服务段处理时间
	private String AddTime;//添加时间
	public DBNotifyHistorary(String bjuserid, String type, 
			String imageurl,String content, String title, String dealtime,String addtime) {
		super();
		BJUserId = bjuserid;
		TyPe = type;
		ImageUrl = imageurl;
		Content = content;
		Title = title;
		DealTime = dealtime;
		AddTime = addtime;
	}
	
	public String getBJUserId() {
		return BJUserId;
	}
	public void setBJUserId(String bJUserId) {
		BJUserId = bJUserId;
	}
	public String getTyPe() {
		return TyPe;
	}
	public void setTyPe(String tyPe) {
		TyPe = tyPe;
	}
	public String getImageUrl() {
		return ImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getDealTime() {
		return DealTime;
	}
	public void setDealTime(String dealTime) {
		DealTime = dealTime;
	}
	public String getAddTime() {
		return AddTime;
	}
	public void setAddTime(String addTime) {
		AddTime = addTime;
	}
}
