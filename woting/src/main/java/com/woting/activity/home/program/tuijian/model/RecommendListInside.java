package com.woting.activity.home.program.tuijian.model;

import java.io.Serializable;

public class RecommendListInside implements Serializable{
	private String RadioId;			//播放id
	private String MediaType;		//播放类型
	private String RadioName;		//播放名称
	private String CurrentContent;	//播放简介
	private String RadioImg;		//播放图片
	private String RadioURI;		//播放路径
	private String RadioNum;		//播放数量
	
	public String getRadioNum() {
		return RadioNum;
	}
	public void setRadioNum(String radioNum) {
		RadioNum = radioNum;
	}
	public String getRadioId() {
		return RadioId;
	}
	public void setRadioId(String radioId) {
		RadioId = radioId;
	}
	public String getMediaType() {
		return MediaType;
	}
	public void setMediaType(String mediaType) {
		MediaType = mediaType;
	}
	public String getRadioName() {
		return RadioName;
	}
	public void setRadioName(String radioName) {
		RadioName = radioName;
	}
	public String getCurrentContent() {
		return CurrentContent;
	}
	public void setCurrentContent(String currentContent) {
		CurrentContent = currentContent;
	}
	public String getRadioImg() {
		return RadioImg;
	}
	public void setRadioImg(String radioImg) {
		RadioImg = radioImg;
	}
	public String getRadioURI() {
		return RadioURI;
	}
	public void setRadioURI(String radioURI) {
		RadioURI = radioURI;
	}
}
