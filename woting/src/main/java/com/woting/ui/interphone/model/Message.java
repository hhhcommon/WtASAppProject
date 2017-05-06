package com.woting.ui.interphone.model;

import java.io.Serializable;

/**
 * 应用内的消息对象
 */
public class Message implements Serializable {
	public String Src;
	public String Url;
	public String Type;
	public Message(String Src,String  Url,String Type) {
		super();
		this.Src=Src;
		this.Url=Url;
		this.Type=Type;
	}
	public String getSrc() {
		return Src;
	}

	public void setSrc(String src) {
		Src = src;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}
}
