package com.woting.ui.music.search.model;

/**
 * 自己的搜索记录对象
 */
public class History {
	private String Id;
	private String UserId;
	private String PlayName;

	public History(String userId, String playName) {
		super();
		UserId = userId;
		PlayName = playName;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getPlayName() {
		return PlayName;
	}
	public void setPlayName(String playName) {
		PlayName = playName;
	}
}
