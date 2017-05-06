package com.woting.ui.musicplay.comment.model;

import com.woting.ui.interphone.model.UserInfo;

import java.io.Serializable;

/**
 * 评论的对象
 */
public class opinion implements Serializable {
    private String UserId;
    private String Discuss;
    private String Time;
    private String ContentImg;
    private String Id;
    private String UserName;
    private com.woting.ui.interphone.model.UserInfo UserInfo;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public UserInfo getUserInfo() {
        return UserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        UserInfo = userInfo;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getDiscuss() {
        return Discuss;
    }

    public void setDiscuss(String discuss) {
        Discuss = discuss;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getContentImg() {
        return ContentImg;
    }

    public void setContentImg(String contentImg) {
        ContentImg = contentImg;
    }
}
