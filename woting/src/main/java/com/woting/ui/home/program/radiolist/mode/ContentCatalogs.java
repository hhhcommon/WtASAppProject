package com.woting.ui.home.program.radiolist.mode;

import java.io.Serializable;

public class ContentCatalogs implements Serializable{
	private static final long serialVersionUID = 595268456371648879L;
	
	private String CataDId;
	private String CataMName;
	private String CataTitle;
	private String CataMId;

	public String getCataDId() {
		return CataDId;
	}
	public void setCataDId(String cataDId) {
		CataDId = cataDId;
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
