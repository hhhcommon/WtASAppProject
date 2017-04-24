package com.woting.ui.model;

import com.woting.ui.musicplay.play.model.PlayerHistory;

import java.util.List;

/**
 * 组装搜索结果的对象
 */
public class SuperRankInfo {
	private String Key;
	private List<content> list;
	private List<PlayerHistory> historyList;
	
	public List<PlayerHistory> getHistoryList() {
		return historyList;
	}
	public void setHistoryList(List<PlayerHistory> historyList) {
		this.historyList = historyList;
	}
	public String getKey() {
		return Key;
	}
	public void setKey(String key) {
		Key = key;
	}
	public List<content> getList() {
		return list;
	}
	public void setList(List<content> list) {
		this.list = list;
	}
}
