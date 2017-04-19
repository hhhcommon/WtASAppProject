package com.woting.ui.common.model;

import java.io.Serializable;

//用户信息实体类
public class UserInfo implements Serializable {
    private int Type = 1;           // 标记是否为最后一项的，新加入尾部的数据会设置type为2，在adapter里针对此属性设置对应的gridview
    private int CheckType = 1;      // 标记item是否被选中，1为未选中，2为选中
    private int ViewType=1;         // 默认不显示
    private String name;            // 显示的数据
    private String sortLetters;     // 显示数据拼音的首字母
    private String InnerPhoneNum;
    private String UserId;
    private String Email;
    private String PortraitBig;
    private String PhoneNum;        // 用户主手机号
    private String PortraitMini;
    private String pinYinName;
    private String UserAliasName;
    private String truename;
    private String check = "1";     // 1未选中2选中
    private String RealName;        // 实名
    private String UserNum;         // 用户码
    private String Descn;           //
    private String ApplyTime;
    private String Portrait;        // 个人头像
    private int OnLine=1;			// 该成员是否在线
    private String Url;

    private String UserSign;        // 用户签名
    private String Sex;             // 性别
    private String StarSign;        // 星座
    private String Region;          // 区域
    private String Birthday;        // 生日
    private String Age;             // 年龄
    private String NickName;        // 昵称
    private String PhoneNumIsPub;   // 是否允许手机号查找


    public void setViewType(int viewType) {
        ViewType = viewType;
    }

    public int getViewType() {
        return ViewType;
    }

    public String getPhoneNumIsPub() {
        return PhoneNumIsPub;
    }

    public void setPhoneNumIsPub(String phoneNumIsPub) {
        PhoneNumIsPub = phoneNumIsPub;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getUserSign() {
        return UserSign;
    }

    public void setUserSign(String userSign) {
        UserSign = userSign;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getStarSign() {
        return StarSign;
    }

    public void setStarSign(String starSign) {
        StarSign = starSign;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public int getOnLine() {
        return OnLine;
    }

    public void setOnLine(int onLine) {
        OnLine = onLine;
    }

    public String getPortrait() {
        return Portrait;
    }

    public void setPortrait(String portrait) {
        Portrait = portrait;
    }

    public String getApplyTime() {
        return ApplyTime;
    }

    public void setApplyTime(String applyTime) {
        ApplyTime = applyTime;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getUserNum() {
        return UserNum;
    }

    public void setUserNum(String userNum) {
        UserNum = userNum;
    }

    public String getDescn() {
        return Descn;
    }

    public void setDescn(String descn) {
        Descn = descn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getPortraitBig() {
        return PortraitBig;
    }

    public void setPortraitBig(String portraitBig) {
        PortraitBig = portraitBig;
    }

    public String getPortraitMini() {
        return PortraitMini;
    }

    public void setPortraitMini(String portraitMini) {
        PortraitMini = portraitMini;
    }

    public String getPinYinName() {
        return pinYinName;
    }

    public void setPinYinName(String pinYinName) {
        this.pinYinName = pinYinName;
    }

    public String getUserAliasName() {
        return UserAliasName;
    }

    public void setUserAliasName(String userAliasName) {
        UserAliasName = userAliasName;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public int getCheckType() {
        return CheckType;
    }

    public void setCheckType(int checkType) {
        CheckType = checkType;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getInnerPhoneNum() {
        return InnerPhoneNum;
    }

    public void setInnerPhoneNum(String innerPhoneNum) {
        InnerPhoneNum = innerPhoneNum;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }


}
