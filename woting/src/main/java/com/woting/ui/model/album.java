package com.woting.ui.model;

/**
 * 专辑的model
 * 作者：xinlong on 2016/8/23 21:18
 * 邮箱：645700751@qq.com
 */
  public class album extends commonContent{
	public String ContentKeyWord;    //
	public String ContentPubTime;    //
	public String ContentPlay;       // 播放地址 http://hls.qingting.fm/live/339.m3u8?bitrate=64&format=mpegts
	public anchor ContentPersons;    // 主播


	public String getContentKeyWord() {
		return ContentKeyWord;
	}

	public void setContentKeyWord(String contentKeyWord) {
		ContentKeyWord = contentKeyWord;
	}

	public String getContentPubTime() {
		return ContentPubTime;
	}

	public void setContentPubTime(String contentPubTime) {
		ContentPubTime = contentPubTime;
	}

	public String getContentPlay() {
		return ContentPlay;
	}

	public void setContentPlay(String contentPlay) {
		ContentPlay = contentPlay;
	}

	public anchor getContentPersons() {
		return ContentPersons;
	}

	public void setContentPersons(anchor contentPersons) {
		ContentPersons = contentPersons;
	}
}
