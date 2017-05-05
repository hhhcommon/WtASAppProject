package com.woting.ui.musicplay.programme.model;

import java.io.Serializable;

/**
 * 作者：xinlong on 2016/12/1 14:43
 * 邮箱：645700751@qq.com
 */
public class programme implements Serializable{
    private String EndTime;
    private String Title;
    private String BeginTime;

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        BeginTime = beginTime;
    }
}
