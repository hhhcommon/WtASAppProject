package com.woting.ui.interphone.commom.model;

/**
 * 专辑信息
 * Created by Administrator on 2017/2/23.
 */
public class SeqMediaInfo {

    private String ContentName;// 专辑名

    private String ContentPub;// 来源

    private String ContentPubTime;//**节目发布时间，此处代表单体数据
    private String ContentURI;
    private String CTime;// 发布时间

    private String MediaType;// 媒体类型

    private String ContentId;

    private String ContentImg;

    public String getContentPubTime() {
        return ContentPubTime;
    }

    public void setContentPubTime(String contentPubTime) {
        ContentPubTime = contentPubTime;
    }

    public String getContentName() {
        return ContentName;
    }

    public void setContentName(String contentName) {
        ContentName = contentName;
    }

    public String getContentPub() {
        return ContentPub;
    }

    public void setContentPub(String contentPub) {
        ContentPub = contentPub;
    }

    public String getContentURI() {
        return ContentURI;
    }

    public void setContentURI(String contentURI) {
        ContentURI = contentURI;
    }

    public String getCTime() {
        return CTime;
    }

    public void setCTime(String CTime) {
        this.CTime = CTime;
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
}
