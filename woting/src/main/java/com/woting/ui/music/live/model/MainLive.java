package com.woting.ui.music.live.model;


import java.io.Serializable;
import java.util.ArrayList;
/**
 * live的主对象
 * 作者：xinLong on 2017/5/2 10:47
 * 邮箱：645700751@qq.com
 */
public class MainLive implements Serializable {
	private String title;         // 标题
	private String type;          // 类型
	private ArrayList<live> data; // 列表数据

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<live> getData() {
		return data;
	}

	public void setData(ArrayList<live> data) {
		this.data = data;
	}
}