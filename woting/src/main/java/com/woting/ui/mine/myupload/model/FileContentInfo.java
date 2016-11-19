package com.woting.ui.mine.myupload.model;

/**
 * 上传的文件的内容信息
 * Created by Administrator on 11/19/2016.
 */
public class FileContentInfo {

    private String timeConsuming;// 耗费时间

    private String size;// 文件大小

    private String success;// 上传结果 TRUE OR FALSE

    private String orglFilename;// 文件名

    private String smallFilepath;// 小文件路径

    private String storeFilepath;// 文件路径

    private String uploadTime;// 上传时间

    private String FieldName;// 字段名称

    private String smallFilename;// 小文件名字

    private String storeFilename;// 路径文件名字

    public String getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(String timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getOrglFilename() {
        return orglFilename;
    }

    public void setOrglFilename(String orglFilename) {
        this.orglFilename = orglFilename;
    }

    public String getSmallFilepath() {
        return smallFilepath;
    }

    public void setSmallFilepath(String smallFilepath) {
        this.smallFilepath = smallFilepath;
    }

    public String getStoreFilepath() {
        return storeFilepath;
    }

    public void setStoreFilepath(String storeFilepath) {
        this.storeFilepath = storeFilepath;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getFieldName() {
        return FieldName;
    }

    public void setFieldName(String fieldName) {
        FieldName = fieldName;
    }

    public String getSmallFilename() {
        return smallFilename;
    }

    public void setSmallFilename(String smallFilename) {
        this.smallFilename = smallFilename;
    }

    public String getStoreFilename() {
        return storeFilename;
    }

    public void setStoreFilename(String storeFilename) {
        this.storeFilename = storeFilename;
    }
}
