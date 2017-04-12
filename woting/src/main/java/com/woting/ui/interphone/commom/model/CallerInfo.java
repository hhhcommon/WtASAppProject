package com.woting.ui.interphone.commom.model;

import java.io.Serializable;

public class CallerInfo implements Serializable{
	
    public String NickName;
    public String Portrait;

	public String getNickName() {
		return NickName;
	}

	public void setNickName(String nickName) {
		NickName = nickName;
	}

	public String getPortrait() {
		return Portrait;
	}
	public void setPortrait(String portrait) {
		Portrait = portrait;
	}
    
}
