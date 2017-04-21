package com.woting.ui.model;

/**
 * 电台的model
 * 作者：xinlong on 2016/8/23 21:18
 * 邮箱：645700751@qq.com
 */
public class radio extends commonContent{
	public String IsPlaying;         //  正在直播 大城小事
	public String ContentPlay;       // 播放地址 http://hls.qingting.fm/live/339.m3u8?bitrate=64&format=mpegts
	public String ContentFreqs;      //
	public String ContentURIS;       //

	public String getIsPlaying() {
		return IsPlaying;
	}

	public void setIsPlaying(String isPlaying) {
		IsPlaying = isPlaying;
	}

	public String getContentPlay() {
		return ContentPlay;
	}

	public void setContentPlay(String contentPlay) {
		ContentPlay = contentPlay;
	}

	public String getContentFreqs() {
		return ContentFreqs;
	}

	public void setContentFreqs(String contentFreqs) {
		ContentFreqs = contentFreqs;
	}

	public String getContentURIS() {
		return ContentURIS;
	}

	public void setContentURIS(String contentURIS) {
		ContentURIS = contentURIS;
	}
}
