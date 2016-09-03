package com.woting.activity.home.program.fenlei.model;

import java.io.Serializable;
import java.util.List;

/**
 * 城市分类
 */
public class fenlei implements Serializable{
	private String CatalogName;
	private List<fenleiname> SubCata;
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
	public List<fenleiname> getSubCata() {
		return SubCata;
	}
	public void setSubCata(List<fenleiname> subCata) {
		SubCata = subCata;
	}
}
