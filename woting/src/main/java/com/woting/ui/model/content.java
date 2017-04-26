package com.woting.ui.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 单体节目的model
 * 作者：xinlong on 2016/8/23 21:18
 * 邮箱：645700751@qq.com
 */
public class content extends commonContent implements Serializable {
    public String ContentKeyWord;    //
    public String ContentPubTime;    //
    public String ContentPlay;       // 播放地址 http://hls.qingting.fm/live/339.m3u8?bitrate=64&format=mpegts
    public ArrayList<anchor> ContentPersons;    // 主播
    public album SeqInfo;            // 专辑
    public String ColumnNum="0";     //
    public String IsPlaying;         //  正在直播 大城小事
    public String ContentFreqs;      //
    public String ContentURIS;       //
    public String PlayerAllTime;       //
    private String PlayerInTime;        // 此时播放时长

    public String Userid;            //

    public String getPlayerInTime() {
        return PlayerInTime;
    }

    public void setPlayerInTime(String playerInTime) {
        PlayerInTime = playerInTime;
    }

    public String getPlayerAllTime() {
        return PlayerAllTime;
    }

    public void setPlayerAllTime(String playerAllTime) {
        PlayerAllTime = playerAllTime;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getIsPlaying() {
        return IsPlaying;
    }

    public void setIsPlaying(String isPlaying) {
        IsPlaying = isPlaying;
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

    public String getColumnNum() {
        return ColumnNum;
    }

    public void setColumnNum(String columnNum) {
        ColumnNum = columnNum;
    }

    public album getSeqInfo() {
        return SeqInfo;
    }

    public void setSeqInfo(album seqInfo) {
        SeqInfo = seqInfo;
    }

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

    public ArrayList<anchor> getContentPersons() {
        return ContentPersons;
    }

    public void setContentPersons(ArrayList<anchor> contentPersons) {
        ContentPersons = contentPersons;
    }
}
