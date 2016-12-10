package com.woting.video;

public interface WtAudioPlay {

	/**
     * 播放
     */
    public void play(String url);
	/**
     * 暂停播放
     */
    public void pause();
	/**
     * 停止播放
     */
    public void stop();
	/**
     * 继续播放
     */
    public void continuePlay();

    /**
     * 是否播放
     */
    public boolean isPlaying();

	/**
     * 获取音量
     */
    public int getVolume();

	/**
     * 设置音量
     */
    public  int setVolume();

	/**
     * 设置播放时间
     */
    public void setTime(long times);
	/**
     * 获取当前时间
     */
    public long getTime();
	/**
     * 获取总结目时长
     */
    public long getTotalTime();
	/**
     * 释放资源
     */
    public void destory();
	/**
     * 获取标志
     */
    public String mark();

}
