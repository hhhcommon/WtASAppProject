package com.woting.ui.mine.myupload.model;

/**
 * 音频信息
 * Created by Administrator on 2016/11/19.
 */
public class MediaStoreInfo {

    private String data;// 文件路径

    private String title;// 文件名称

    private String type;// 文件类型

    private int id;// 文件ID

    private long size;// 文件大小

    private long addTime;// 文件添加时间

    private long duration;// 时长

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }
}
