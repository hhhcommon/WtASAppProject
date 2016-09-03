package com.woting.activity.login.forgetpassword.model;

public class UserInfo {
	private String ReturnType;
	private String SessionId;
	private String Phone;
	private String Email;
	private String Message;
	
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public String getReturnType() {
		return ReturnType;
	}
	public String getPhone() {
		return Phone;
	}
	public void setPhone(String phone) {
		Phone = phone;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public void setReturnType(String returnType) {
		ReturnType = returnType;
	}
	public String getSessionId() {
		return SessionId;
	}
	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
}
