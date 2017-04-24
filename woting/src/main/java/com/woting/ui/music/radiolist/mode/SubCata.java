package com.woting.ui.music.radiolist.mode;

import java.io.Serializable;

public class SubCata implements Serializable{
	private static final long serialVersionUID = -6488018514092854196L;
	
	private String CatalogType;
	private String CatalogName;
	private String CatalogId;
	
	public String getCatalogType() {
		return CatalogType;
	}
	public void setCatalogType(String catalogType) {
		CatalogType = catalogType;
	}
	public String getCatalogName() {
		return CatalogName;
	}
	public void setCatalogName(String catalogName) {
		CatalogName = catalogName;
	}
	public String getCatalogId() {
		return CatalogId;
	}
	public void setCatalogId(String catalogId) {
		CatalogId = catalogId;
	}
}
