package com.woting.ui.home.program.fenlei.model;

import java.io.Serializable;
import java.util.List;

/**
 * 城市分类
 */
public class FenLei implements Serializable{
	private String name;
	private List<FenLeiName> children;
	private int tag; //存全选tag的位置
	private int tagType ; //存全选的类型

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getTagType() {
		return tagType;
	}

	public void setTagType(int tagType) {
		this.tagType = tagType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FenLeiName> getChildren() {
		return children;
	}

	public void setChildren(List<FenLeiName> children) {
		this.children = children;
	}
}
