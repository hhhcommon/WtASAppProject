package com.woting.live.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amine on 2017/4/29.
 */

public class LiveInfo implements Serializable {

    /**
     * ret : 0
     * msg : success
     * data : {"voice_live":{"id":31,"user_id":"b94ed091470b","title":"测试","cover":"","begin_at":"2017-04-29 10:31:28","had_started":false,"audience_count":0,"created_at":"2017-04-29 10:31:28","updated_at":"2017-04-29 10:31:28","had_ended":false,"enable_save":0,"audience_ids":null,"background":"http://nanzhu.oss-cn-shanghai.aliyuncs.com/voice-live-background.jpg","enable_push":0,"live_number":"1000031","owner":{"id":"b94ed091470b","avatar":"##userimg##user_4e4d.jpg","name":"Amine","fans_count":0,"idols_count":0,"had_real_name_cert":false},"keywords":[],"channel":{}}}
     */

    public int ret;
    public String msg;
    public DataBean data;

    public static class DataBean implements Serializable {
        /**
         * voice_live : {"id":31,"user_id":"b94ed091470b","title":"测试","cover":"","begin_at":"2017-04-29 10:31:28","had_started":false,"audience_count":0,"created_at":"2017-04-29 10:31:28","updated_at":"2017-04-29 10:31:28","had_ended":false,"enable_save":0,"audience_ids":null,"background":"http://nanzhu.oss-cn-shanghai.aliyuncs.com/voice-live-background.jpg","enable_push":0,"live_number":"1000031","owner":{"id":"b94ed091470b","avatar":"##userimg##user_4e4d.jpg","name":"Amine","fans_count":0,"idols_count":0,"had_real_name_cert":false},"keywords":[],"channel":{}}
         */

        public VoiceLiveBean voice_live;

    }
}
