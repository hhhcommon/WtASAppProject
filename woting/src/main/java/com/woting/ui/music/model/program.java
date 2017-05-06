package com.woting.ui.music.model;

import java.util.ArrayList;

/**
 * 单体节目的model
 * 作者：xinlong on 2016/8/23 21:18
 * 邮箱：645700751@qq.com
 */
public class program extends commonContent {
    public String ContentKeyWord;    //
    public String ContentPubTime;    //
    public String ContentPlay;       // 播放地址 http://hls.qingting.fm/live/339.m3u8?bitrate=64&format=mpegts
    public ArrayList<anchor> ContentPersons;    // 主播
    public album SeqInfo;            // 专辑
    private String ColumnNum;        //

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
