package com.woting.ui.home.program.accuse.model;

import java.io.Serializable;

public class Accuses implements Serializable{
	private static final long serialVersionUID = 4523673229526198349L;

	private String CatalogType;
	private String CatalogName;
	private String CatalogId;
	private int CheckType=0;                                       //=0时 不显示图片 =1时显示

	public String getCatalogId() {
		return CatalogId;
	}

	public String getCatalogName() {
		return CatalogName;
	}

	public String getCatalogType() {
		return CatalogType;
	}

	public void setCatalogId(String catalogId) {
		CatalogId = catalogId;
	}

	public void setCatalogName(String catalogName) {
		CatalogName = catalogName;
	}

	public void setCatalogType(String catalogType) {
		CatalogType = catalogType;
	}

	public int getCheckType() {
		return CheckType;
	}

	public void setCheckType(int checkType) {
		CheckType = checkType;
	}
}

