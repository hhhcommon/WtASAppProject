package com.woting.live.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amine on 2017/4/30.
 */

public class LiveInfoUser implements Serializable {


    public DataBean data;
    public String msg;
    public int ret;


    public static class DataBean implements Serializable {
        public List<UsersBean> users;


        public static class UsersBean implements Serializable {

            public String birthday;
            public String descn;
            public String homepage;
            public String loginName;
            public String mailAddress;
            public String mainPhoneNum;
            public String nickName;
            public String portraitBig;
            public String portraitMini;
            public String starSign;
            public String userName;
            public String userNum;
            public String userSign;
            public String cTime;
            public String id;
            public String lmTime;
            public String password;
            public String phoneNumIsPub;
            public String userClass;
            public String userState;
            public String userType;

        }
    }
}
