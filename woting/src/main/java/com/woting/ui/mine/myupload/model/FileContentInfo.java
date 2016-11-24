package com.woting.ui.mine.myupload.model;

/**
 * 上传的文件的内容信息
 * Created by Administrator on 11/19/2016.
 */
public class FileContentInfo {

    private String timeConsuming;// 耗费时间

    private String FileSize;// 文件大小

    private String success;// 上传结果 TRUE OR FALSE

    private String FilePath;// 文件路径

    public String getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(String timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getSuccess() {
        return success;
    }

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String fileSize) {
        FileSize = fileSize;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
