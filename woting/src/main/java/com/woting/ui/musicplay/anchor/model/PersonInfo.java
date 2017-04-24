package com.woting.ui.musicplay.anchor.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/27 0027.
 */
public class PersonInfo implements Serializable {

    private String ContentName; //专辑名
    private String PlayCount;
    private String MediaType;
    private String NewMedia;
    private String ContentId;
    private String ContentImg;
    private String ContentPubTime;
    private String ContentTime;

    public String getContentName() {
        return ContentName;
    }

    public void setContentName(String contentName) {
        ContentName = contentName;
    }

    public String getPlayCount() {
        return PlayCount;
    }

    public void setPlayCount(String playCount) {
        PlayCount = playCount;
    }

    public String getNewMedia() {
        return NewMedia;
    }

    public void setNewMedia(String newMedia) {
        NewMedia = newMedia;
    }

    public String getMediaType() {
        return MediaType;
    }

    public void setMediaType(String mediaType) {
        MediaType = mediaType;
    }

    public String getContentId() {
        return ContentId;
    }

    public void setContentId(String contentId) {
        ContentId = contentId;
    }

    public String getContentImg() {
        return ContentImg;
    }

    public void setContentImg(String contentImg) {
        ContentImg = contentImg;
    }

    public String getContentPubTime() {
        return ContentPubTime;
    }

    public void setContentPubTime(String contentPubTime) {
        ContentPubTime = contentPubTime;
    }

    public String getContentTime() {
        return ContentTime;
    }

    public void setContentTime(String contentTime) {
        ContentTime = contentTime;
    }
}
