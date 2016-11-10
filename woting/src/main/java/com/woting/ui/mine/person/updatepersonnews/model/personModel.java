package com.woting.ui.mine.person.updatepersonnews.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/9 0009.
 */
public class personModel implements Serializable {

    private String nickName;
    private String birthday;
    private String starSign;
    private String region;
    private String userSign;
    private String gender;
    private String Email;

    public personModel(String nickName, String birthday, String starSign, String region, String userSign, String gender, String email) {
        this.nickName = nickName;
        this.birthday = birthday;
        this.starSign = starSign;
        this.region = region;
        this.userSign = userSign;
        this.gender = gender;
        Email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getStarSign() {
        return starSign;
    }

    public void setStarSign(String starSign) {
        this.starSign = starSign;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
