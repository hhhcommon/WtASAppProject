package com.woting.activity.interphone.commom.model;

import java.io.Serializable;
/**
 * socket最外层数据
 * @author 辛龙
 *2016年5月4日
 */
public class MessageInfo implements Serializable{
	public String MsgId;//字符串	消息Id，如果NeedAffirm=1，则MsgId必须有
	public String ReMsgId;
	public String MsgType;//字符串	=1主动发出的消息
	public String BizType;
	public String ReturnType;
	public String CmdType;//字符串	这里是通知的类型
	public long SendTime;//Long	当前时间毫秒数
	public String Command;//字符串	由于是服务器发送的，因此都要由b开头
	public Data Data;//Json	具体的消息内容

	//    		NeedAffirm	字符串	是否需要App确认回复

	public long getSendTime() {
		return SendTime;
	}
	public void setSendTime(long sendTime) {
		SendTime = sendTime;
	}
	public String getMsgId() {
		return MsgId;
	}
	public void setMsgId(String msgId) {
		MsgId = msgId;
	}
	public String getReMsgId() {
		return ReMsgId;
	}
	public void setReMsgId(String reMsgId) {
		ReMsgId = reMsgId;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	public String getBizType() {
		return BizType;
	}
	public void setBizType(String bizType) {
		BizType = bizType;
	}
	public String getReturnType() {
		return ReturnType;
	}
	public void setReturnType(String returnType) {
		ReturnType = returnType;
	}
	public Data getData() {
		return Data;
	}
	public void setData(Data data) {
		Data = data;
	}
	public String getCmdType() {
		return CmdType;
	}
	public void setCmdType(String cmdType) {
		CmdType = cmdType;
	}
	public String getCommand() {
		return Command;
	}
	public void setCommand(String command) {
		Command = command;
	}



}
