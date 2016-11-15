package com.woting.ui.home.program.fenlei.model;

import java.io.Serializable;

public class FenLeiName implements Serializable{
	private String name;   //显示的数据
	private Attributes attributes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}
}
