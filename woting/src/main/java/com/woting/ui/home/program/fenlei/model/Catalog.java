package com.woting.ui.home.program.fenlei.model;

import java.io.Serializable;
import java.util.List;

/**
 * 城市分类
 */
public class Catalog implements Serializable{
	private String CatalogName;
	private List<com.woting.ui.home.program.fenlei.model.CatalogName> SubCata;
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
	public List<com.woting.ui.home.program.fenlei.model.CatalogName> getSubCata() {
		return SubCata;
	}
	public void setSubCata(List<com.woting.ui.home.program.fenlei.model.CatalogName> subCata) {
		SubCata = subCata;
	}
}
