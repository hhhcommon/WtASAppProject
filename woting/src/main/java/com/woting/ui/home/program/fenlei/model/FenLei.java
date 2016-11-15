package com.woting.ui.home.program.fenlei.model;

import java.io.Serializable;
import java.util.List;

/**
 * 城市分类
 */
public class FenLei implements Serializable{
	private String name;
	private List<FenLeiName> children;

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
