package com.woting.activity.home.player.main.model;

import java.io.Serializable;
import java.util.List;

public class LanguageSearch implements Serializable{
	
	private String AllCount;
	private List<LanguageSearchInside> List;
	public String getAllCount() {
		return AllCount;
	}
	public void setAllCount(String allCount) {
		AllCount = allCount;
	}
	public List<LanguageSearchInside> getList() {
		return List;
	}
	public void setList(List<LanguageSearchInside> list) {
		List = list;
	}
	

}
