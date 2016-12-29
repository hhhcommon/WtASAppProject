package com.woting.ui.interphone.linkman.model;

import java.io.Serializable;

public class DBNotifyHistory implements Serializable {
    private String BJUserId;//本机userid
    private String TyPe;//通知类型
    private String ImageUrl;//图片路径
    private String Title;//标题
    private String Content;//内容
    private String DealTime;//服务端处理时间
    private String AddTime;//添加时间

    private String ShowType;//是否在消息界面展示
    private int BizType;//BizType
    private int CmdType;//CmdType
    private int Command;//Command
    private String TaskId;//消息批次id


    public DBNotifyHistory(String bjuserid, String type,
                           String imageurl, String content, String title, String dealtime, String addtime,
                           String showType, int bizType, int cmdType, int command, String taskId) {
        super();
        BJUserId = bjuserid;
        TyPe = type;
        ImageUrl = imageurl;
        Content = content;
        Title = title;
        DealTime = dealtime;
        AddTime = addtime;

        ShowType = showType;
        BizType = bizType;
        CmdType = cmdType;
        Command = command;
        TaskId = taskId;

    }

    public String getShowType() {
        return ShowType;
    }

    public void setShowType(String showType) {
        ShowType = showType;
    }

    public int getBizType() {
        return BizType;
    }

    public void setBizType(int bizType) {
        BizType = bizType;
    }

    public int getCmdType() {
        return CmdType;
    }

    public void setCmdType(int cmdType) {
        CmdType = cmdType;
    }

    public int getCommand() {
        return Command;
    }

    public void setCommand(int command) {
        Command = command;
    }

    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public String getBJUserId() {
        return BJUserId;
    }

    public void setBJUserId(String bJUserId) {
        BJUserId = bJUserId;
    }

    public String getTyPe() {
        return TyPe;
    }

    public void setTyPe(String tyPe) {
        TyPe = tyPe;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getDealTime() {
        return DealTime;
    }

    public void setDealTime(String dealTime) {
        DealTime = dealTime;
    }

    public String getAddTime() {
        return AddTime;
    }

    public void setAddTime(String addTime) {
        AddTime = addTime;
    }
}
