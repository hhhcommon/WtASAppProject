package com.woting.ui.musicplay.play.model;

import com.woting.ui.music.model.content;

import java.io.Serializable;
import java.util.List;

public class LanguageSearch implements Serializable{
	
	private String AllCount;
	private List<content> List;
	public String getAllCount() {
		return AllCount;
	}
	public void setAllCount(String allCount) {
		AllCount = allCount;
	}
	public List<content> getList() {
		return List;
	}
	public void setList(List<content> list) {
		List = list;
	}
	

}
