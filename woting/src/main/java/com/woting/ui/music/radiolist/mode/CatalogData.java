package com.woting.ui.music.radiolist.mode;

import java.io.Serializable;
import java.util.List;

public class CatalogData implements Serializable{
	private static final long serialVersionUID = 4523673229526198349L;
	
	private String CatalogType;
	private String CatalogName;
	private List<SubCata> SubCata;
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
	public List<SubCata> getSubCata() {
		return SubCata;
	}
	public void setSubCata(List<SubCata> subCata) {
		SubCata = subCata;
	}
	public String getCatalogId() {
		return CatalogId;
	}
	public void setCatalogId(String catalogId) {
		CatalogId = catalogId;
	}
}
