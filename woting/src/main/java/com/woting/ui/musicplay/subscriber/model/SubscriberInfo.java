package com.woting.ui.musicplay.subscriber.model;

/**
 * 订阅信息
 * Created by Administrator on 2017/2/10.
 */
public class SubscriberInfo {
    private int UpdateCount;// 更新数量

    private String ContentSeqId;// 专辑 ID

    private String ContentSeqName;// 专辑名

    private String ContentSeqImg;// 专辑封面

    private String ContentMediaName;

    private String ContentMediaId;

    private long ContentPubTime;// 更新时间

    public long getContentPubTime() {
        return ContentPubTime;
    }

    public void setContentPubTime(long contentPubTime) {
        ContentPubTime = contentPubTime;
    }

    public int getUpdateCount() {
        return UpdateCount;
    }

    public void setUpdateCount(int updateCount) {
        UpdateCount = updateCount;
    }

    public String getContentSeqId() {
        return ContentSeqId;
    }

    public void setContentSeqId(String contentSeqId) {
        ContentSeqId = contentSeqId;
    }

    public String getContentSeqName() {
        return ContentSeqName;
    }

    public void setContentSeqName(String contentSeqName) {
        ContentSeqName = contentSeqName;
    }

    public String getContentSeqImg() {
        return ContentSeqImg;
    }

    public void setContentSeqImg(String contentSeqImg) {
        ContentSeqImg = contentSeqImg;
    }

    public String getContentMediaName() {
        return ContentMediaName;
    }

    public void setContentMediaName(String contentMediaName) {
        ContentMediaName = contentMediaName;
    }

    public String getContentMediaId() {
        return ContentMediaId;
    }

    public void setContentMediaId(String contentMediaId) {
        ContentMediaId = contentMediaId;
    }
}
