package com.woting.activity.home.program.diantai.model;

import com.woting.activity.home.program.fmlist.model.RankInfo;

import java.io.Serializable;
import java.util.List;

public class RadioPlay implements Serializable{
    private String CatalogId;
	private String CatalogImg;
	private String CatalogName;
	private String PageSize;
	private String CatalogType;
	private String AllListSize;
	private List<RankInfo> List;

	public List<RankInfo> getList() {
		return List;
	}
	public void setList(List<RankInfo> list) {
		List = list;
	}
	public String getCatalogId() {
		return CatalogId;
	}
	public void setCatalogId(String catalogId) {
		CatalogId = catalogId;
	}
	public String getCatalogImg() {
		return CatalogImg;
	}
	public void setCatalogImg(String catalogImg) {
		CatalogImg = catalogImg;
	}
	public String getCatalogName() {
		return CatalogName;
	}
	public void setCatalogName(String catalogName) {
		CatalogName = catalogName;
	}
	public String getPageSize() {
		return PageSize;
	}
	public void setPageSize(String pageSize) {
		PageSize = pageSize;
	}
	public String getCatalogType() {
		return CatalogType;
	}
	public void setCatalogType(String catalogType) {
		CatalogType = catalogType;
	}
	public String getAllListSize() {
		return AllListSize;
	}
	public void setAllListSize(String allListSize) {
		AllListSize = allListSize;
	}
}
