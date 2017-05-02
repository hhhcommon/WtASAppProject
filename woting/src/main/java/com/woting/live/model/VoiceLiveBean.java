package com.woting.live.model;

import java.io.Serializable;

public class VoiceLiveBean implements Serializable {
    /**
     * audience_count : 0
     * audience_ids : ["","b94ed091470b"],b94ed091470b
     * background : http://nanzhu.oss-cn-shanghai.aliyuncs.com/voice-live-background.jpg
     * begin_at : 2017-04-30
     * channel : {}
     * cover :
     * created_at : 2017-04-30 13:15:56
     * diff_from_real_begin : 0
     * enable_push : 0
     * enable_save : 0
     * had_ended : true
     * had_started : true
     * id : 65
     * keywords : {}
     * live_number : 1000065
     * lives_count : 3
     * owner : {"avatar":"##userimg##user_4e4d.jpg","fans_count":0,"had_real_name_cert":false,"id":"b94ed091470b","idols_count":0,"name":"Amine"}
     * real_begin_at : {"date":"2017-04-30 16:17:35.000000","timezone":"Asia/Shanghai","timezone_type":3}
     * title : 不不不不
     * updated_at : 2017-04-30 16:17:35
     * user_id : b94ed091470b
     */

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
    public String id;
    public int live_number;
    public int lives_count;
    public OwnerBean owner;
    public String title;
    public String updated_at;
    public String user_id;


    public static class OwnerBean implements Serializable {

        public String avatar;
        public int fans_count;
        public boolean had_real_name_cert;
        public String id;
        public int idols_count;
        public String name;

        public String getAvatar() {
            if (avatar == null)
                return "https://";
            return avatar;
        }
    }
}