package com.woting.video;

import android.content.Context;

/**
 * 集成播放器
 * 作者：xinlong on 2016/11/29 15:54
 * 邮箱：645700751@qq.com
 */
public class IntegrationPlayer {

    private static IntegrationPlayer wtIPlayer;        // 集合播放器
    private static Context contexts;
    private VPlayer vlcPlayer;                         // VLC播放器
    private KSYPlayer ksyPlayer;                       // 金山云播放器
    private TPlayer ttsPlayer;                         // TTS播放器
    private int oldPType;                              // 上次内容播放器类型  1=vlc,2=ksy,3=tts
    private int newptype;                              // 最新内容播放器类型  1=vlc,2=ksy,3=tts

    private IntegrationPlayer() {
        if (vlcPlayer == null) {
            vlcPlayer = VPlayer.getInstance();
        }
        if (ksyPlayer == null) {
            ksyPlayer = KSYPlayer.getInstance();
        }
        if (ttsPlayer == null) {
            ttsPlayer = TPlayer.getInstance(contexts);
        }
    }

    /**
     * 初始化集合播放器
     *
     * @param context tts初始化的时候需要的上下文对象
     * @return 集合播放器
     */
    public static IntegrationPlayer getInstance(Context context) {
        contexts = context;
        if (wtIPlayer == null) {
            wtIPlayer = new IntegrationPlayer();
        }
        return wtIPlayer;
    }

    /**
     * 每个节目的第一次播放
     *
     * @param url  播放路径
     * @param type 节目播放类型
     */
    public void startPlay(String url, String type) {

    }

    /**
     * 暂停播放
     */
    public void pousePlay() {
        if (newptype == 1) {

        } else if (newptype == 2) {

        } else {

        }
    }

    /**
     * 继续播放
     */
    public void continuePlay() {
        if (newptype == 1) {

        } else if (newptype == 2) {

        } else {

        }
    }


    /**
     * 停止播放
     */
    public void stopPlay() {
        if (newptype == 1) {

        } else if (newptype == 2) {

        } else {

        }
    }


    /**
     * 设置播放进度
     *
     * @param time 此时的播放进度
     */
    public void setTime(Long time) {

    }


    /**
     * 获取此时播放时间
     */
    public long getTime() {


        return 0;
    }

    /**
     * 获取总时长
     */
    public long getTotalTime() {

        return 0;
    }

}
