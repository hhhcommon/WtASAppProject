package com.woting.activity.interphone.chat.model;

import java.io.Serializable;

public class DBTalkHistorary implements Serializable{
	private String BJUserId;	//本机userid
	private String TyPe;		//类别 User，Group
	private String ID;			//
	private String AddTime;		//添加时间
	public DBTalkHistorary(String bjuserid, String type, String id,String addtime) {
		super();
		BJUserId = bjuserid;
		TyPe = type;
		ID = id;
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
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getAddTime() {
		return AddTime;
	}
	public void setAddTime(String addTime) {
		AddTime = addTime;
	}
	
	@Override
	public String toString(){
		return "添加时间：" + getAddTime();
	}
}
