package com.woting.activity.home.player.main.model;

import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.Serializable;

public class ShareModel implements Serializable{
	
	private SHARE_MEDIA sharePlatform;
	private int shareImageUrl;
	private String ShareText;
		
	public SHARE_MEDIA getSharePlatform() {
		return sharePlatform;
	}
	public void setSharePlatform(SHARE_MEDIA sharePlatform) {
		this.sharePlatform = sharePlatform;
	}
	public int getShareImageUrl() {
		return shareImageUrl;
	}
	public void setShareImageUrl(int shareImageUrl) {
		this.shareImageUrl = shareImageUrl;
	}
	public String getShareText() {
		return ShareText;
	}
	public void setShareText(String shareText) {
		ShareText = shareText;
	}
}
