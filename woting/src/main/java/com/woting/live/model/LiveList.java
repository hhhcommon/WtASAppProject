package com.woting.live.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amine on 2017/4/29.
 */

public class LiveList implements Serializable {


    public DataBean data;
    public String msg;
    public int ret;


    public static class DataBean implements Serializable {
        public List<VoiceLivesBean> voiceLives;


        public static class VoiceLivesBean implements Serializable {
            /**
             * audience_count : 0
             * begin_at : 2017-04-24 18:00:28
             * cover :
             * created_at : 2017-04-24 18:00:28
             * had_ended : 0
             * had_started : 1
             * id : 6
             * live_number : 100006
             * title : Prof.
             * updated_at : 2017-04-24 18:00:28
             * user_id : 01f24bf3b0a2
             */

            public int audience_count;
            public String begin_at;
            public String cover;
            public String created_at;
            public int had_ended;
            public int had_started;
            public int id;
            public String live_number;
            public String title;
            public String updated_at;
            public String user_id;


        }
    }
}
