package com.woting.ui.music.classify.model;

import java.io.Serializable;

public class FenLeiName implements Serializable{
	private String name;   //显示的数据
	private Attributes attributes;
	private String checked="false";
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getchecked() {
		return checked;
	}

	public void setchecked(String checked) {
		this.checked = checked;
	}
}
