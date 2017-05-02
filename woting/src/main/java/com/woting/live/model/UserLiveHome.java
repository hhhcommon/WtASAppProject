package com.woting.live.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amine on 2017/4/30.
 */

public class UserLiveHome implements Serializable {


    public String msg;
    public int ret;
    public List<DataBeanX> data;


    public static class DataBeanX implements Serializable {
        /**
         * data : [{"audience_count":1,"audience_ids":",b94ed091470b","background":"http://nanzhu.oss-cn-shanghai.aliyuncs.com/voice-live-background.jpg","begin_at":"2017-04-30 15:38:33","cover":"","created_at":"2017-04-30 15:38:33","diff_from_real_begin":10083000,"enable_push":0,"enable_save":0,"had_ended":false,"had_started":true,"id":88,"live_number":"1000088","lives_count":1,"real_begin_at":"2017-04-30 15:38:34","title":"先吃饭吃饭","updated_at":"2017-04-30 15:41:51","user_id":"104a0a149b20"}]
         * title : 热门直播
         */

        public String title;
        public List<DataBean> data;


        public static class DataBean implements Serializable {
            public int audience_count;
            public String audience_ids;
            public String background;
            public String begin_at;
            public String cover;
            public String created_at;
            public int diff_from_real_begin;
            public int enable_push;
            public int enable_save;
            public boolean had_ended;
            public boolean had_started;
            public int id;
            public String live_number;
            public int lives_count;
            public String real_begin_at;
            public String title;
            public String updated_at;
            public String user_id;

        }
    }
}
