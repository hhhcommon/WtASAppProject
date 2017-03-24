package com.woting.ui.interphone.linkman.model;

import java.io.Serializable;

public class DBNotifyHistory implements Serializable {
    private String UserId;//本机userid
    private String ImageUrl;//图片路径

    private String PersonName;//个人名称
    private String PersonId;//个人ID
    private String GroupName;//组名称
    private String GroupId;//组ID
    private String OperatorName;//操作者名称
    private String OperatorId;//操作者ID

    private String ShowType;//是否在消息界面展示
    private String MessageType;//消息类型
    private String DealTime;//服务端处理时间
    private String AddTime;//添加时间
    private int BizType;//BizType
    private int CmdType;//CmdType
    private int Command;//Command
    private String MessageId;//消息批次id
    private String Message;//消息内容

    public DBNotifyHistory(String user_id, String image_url,
                           String person_name, String person_id, String group_name, String group_id, String operator_name,
                           String operator_id, String show_type, String message_type, String deal_time, String add_time,
                           int biz_type, int cmd_type, int command, String message_id, String message) {
        super();
        UserId = user_id;
        ImageUrl = image_url;
        PersonName = person_name;
        PersonId = person_id;
        GroupName = group_name;
        GroupId = group_id;
        OperatorName = operator_name;
        OperatorId = operator_id;
        ShowType = show_type;
        MessageType = message_type;
        DealTime = deal_time;
        AddTime = add_time;
        BizType = biz_type;
        CmdType = cmd_type;
        Command = command;
        MessageId = message_id;
        Message=message;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public DBNotifyHistory() {
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

    public String getPersonName() {
        return PersonName;
    }

    public void setPersonName(String personName) {
        PersonName = personName;
    }

    public String getPersonId() {
        return PersonId;
    }

    public void setPersonId(String personId) {
        PersonId = personId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getOperatorName() {
        return OperatorName;
    }

    public void setOperatorName(String operatorName) {
        OperatorName = operatorName;
    }

    public String getOperatorId() {
        return OperatorId;
    }

    public void setOperatorId(String operatorId) {
        OperatorId = operatorId;
    }

    public String getShowType() {
        return ShowType;
    }

    public void setShowType(String showType) {
        ShowType = showType;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
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
