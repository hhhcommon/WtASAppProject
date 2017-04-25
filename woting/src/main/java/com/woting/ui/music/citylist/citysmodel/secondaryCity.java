package com.woting.ui.music.citylist.citysmodel;

import java.io.Serializable;
import java.util.List;
/**
 * 城市分类二级数据
 */
public class secondaryCity implements Serializable{
	private String name;         //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	private String pinYinName;
	private String CatalogName;
	private String CatalogId;
	private String CatalogType;
	private String truename;
	private List<secondaryCity> SubCata;
	
	public List<secondaryCity> getSubCata() {
		return SubCata;
	}
	public void setSubCata(List<secondaryCity> subCata) {
		SubCata = subCata;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getTruename() {
		return truename;
	}
	public void setTruename(String truename) {
		this.truename = truename;
	}
	public String getPinYinName() {
		return pinYinName;
	}
	public void setPinYinName(String pinYinName) {
		this.pinYinName = pinYinName;
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
	public String getCatalogType() {
		return CatalogType;
	}
	public void setCatalogType(String catalogType) {
		CatalogType = catalogType;
	}
}
