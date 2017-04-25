package com.woting.ui.music.citylist.citysmodel;

import java.io.Serializable;
import java.util.List;

/**
 * 城市分类一级数据
 */
public class stairCity implements Serializable{
	private String CatalogName;
	private List<secondaryCity> SubCata;
	private String CatalogType;
	public String getCatalogName() {
		return CatalogName;
	}
	public void setCatalogName(String catalogName) {
		CatalogName = catalogName;
	}
	public String getCatalogType() {
		return CatalogType;
	}
	public void setCatalogType(String catalogType) {
		CatalogType = catalogType;
	}
	public List<secondaryCity> getSubCata() {
		return SubCata;
	}
	public void setSubCata(List<secondaryCity> subCata) {
		SubCata = subCata;
	}
}
