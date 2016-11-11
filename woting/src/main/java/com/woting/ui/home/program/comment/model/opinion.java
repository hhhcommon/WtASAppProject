package com.woting.ui.home.program.comment.model;



import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class opinion implements Serializable {
    private String UserId;
    private String Discuss;
    private String Time;
    private String ContentImg;
    private String Id;
    private String UserName;
    private UserInfo UserInfo;

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
