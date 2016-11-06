package com.woting.ui.home.program.album.model;

import java.io.Serializable;

public class ContentCatalogs implements Serializable{
	private static final long serialVersionUID = 6574221647400233914L;
	
	private String CataDid;
	private String CataMName;
	private String CataTitle;
	private String CataMId;
	public String getCataDid() {
		return CataDid;
	}
	public void setCataDid(String cataDid) {
		CataDid = cataDid;
	}
	public String getCataMName() {
		return CataMName;
	}
	public void setCataMName(String cataMName) {
		CataMName = cataMName;
	}
	public String getCataTitle() {
		return CataTitle;
	}
	public void setCataTitle(String cataTitle) {
		CataTitle = cataTitle;
	}
	public String getCataMId() {
		return CataMId;
	}
	public void setCataMId(String cataMId) {
		CataMId = cataMId;
	}
}
