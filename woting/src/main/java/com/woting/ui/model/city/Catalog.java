package com.woting.ui.model.city;

import java.io.Serializable;
import java.util.List;

/**
 * 城市分类一级数据
 */
public class Catalog implements Serializable{
	private String CatalogName;
	private List<CatalogName> SubCata;
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
	public List<CatalogName> getSubCata() {
		return SubCata;
	}
	public void setSubCata(List<CatalogName> subCata) {
		SubCata = subCata;
	}
}
