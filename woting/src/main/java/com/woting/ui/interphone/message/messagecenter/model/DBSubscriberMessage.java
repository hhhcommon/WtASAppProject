package com.woting.ui.interphone.message.messagecenter.model;

import java.io.Serializable;

public class DBSubscriberMessage implements Serializable {
    private String UserId;//本机userid
    private String ImageUrl;//图片路径
    private String SeqName;//专辑名称
    private String SeqId;//专辑ID
    private String ContentName;//节目名称
    private String ContentId;//节目ID
    private String DealTime;//服务端处理时间
    private String AddTime;//添加时间
    private int BizType;//BizType
    private int CmdType;//CmdType
    private int Command;//Command
    private String MessageId;//消息批次id
    private String Num;//展示更新条数


    public DBSubscriberMessage(String user_id, String image_url, String seq_name, String seq_id,
                               String content_name, String content_id, String deal_time, String add_time,
                               int biz_type, int cmd_type, int command, String message_id) {
        super();
        this.UserId = user_id;
        this.ImageUrl = image_url;
        this.SeqName = seq_name;
        this.SeqId = seq_id;
        this.ContentName = content_name;
        this.ContentId = content_id;
        this.DealTime = deal_time;
        this.AddTime = add_time;
        this.BizType = biz_type;
        this.CmdType = cmd_type;
        this.Command = command;
        this.MessageId = message_id;
    }

    public String getNum() {
        return Num;
    }

    public void setNum(String num) {
        Num = num;
    }

    public DBSubscriberMessage() {
        super();
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getSeqName() {
        return SeqName;
    }

    public void setSeqName(String seqName) {
        SeqName = seqName;
    }

    public String getSeqId() {
        return SeqId;
    }

    public void setSeqId(String seqId) {
        SeqId = seqId;
    }

    public String getContentName() {
        return ContentName;
    }

    public void setContentName(String contentName) {
        ContentName = contentName;
    }

    public String getContentId() {
        return ContentId;
    }

    public void setContentId(String contentId) {
        ContentId = contentId;
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

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }
}
