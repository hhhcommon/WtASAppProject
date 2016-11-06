package com.woting.ui.home.program.tuijian.model;

import java.util.List;

public class RecommendList {
	
	private String PageListSize;
	private String AllListSize;
	private String ZoneName;
	private String ZoneId;
	private List<RecommendListInside> SubList;
	public String getPageListSize() {
		return PageListSize;
	}
	public void setPageListSize(String pageListSize) {
		PageListSize = pageListSize;
	}
	public String getAllListSize() {
		return AllListSize;
	}
	public void setAllListSize(String allListSize) {
		AllListSize = allListSize;
	}
	public String getZoneName() {
		return ZoneName;
	}
	public void setZoneName(String zoneName) {
		ZoneName = zoneName;
	}
	public String getZoneId() {
		return ZoneId;
	}
	public void setZoneId(String zoneId) {
		ZoneId = zoneId;
	}
	public List<RecommendListInside> getSubList() {
		return SubList;
	}
	public void setSubList(List<RecommendListInside> subList) {
		SubList = subList;
	}
}
