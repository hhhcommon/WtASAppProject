package com.woting.video;

public interface WtAudioPlay {

    /**
     * 播放
     */
    void play(String url);

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 继续播放
     */
    void continuePlay();

    /**
     * 是否播放
     */
    boolean isPlaying();

    /**
     * 获取音量
     */
    int getVolume();

    /**
     * 设置音量
     */
    int setVolume();

    /**
     * 设置播放时间
     */
    void setTime(long times);

    /**
     * 获取当前时间
     */
    long getTime();

    /**
     * 获取总结目时长
     */
    long getTotalTime();

    /**
     * 释放资源
     */
    void destroy();

    /**
     * 获取标志
     */
    String mark();
}
