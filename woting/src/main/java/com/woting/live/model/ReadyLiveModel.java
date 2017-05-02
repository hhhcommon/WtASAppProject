package com.woting.live.model;

import java.io.Serializable;

/**
 * Created by amine on 2017/4/25.
 */

public class ReadyLiveModel implements Serializable {

    /**
     * data : {"voiceLive":{"begin_at":"2017-04-12 12:12:12","cover":"we","created_at":"2017-04-25 14:30:21","enable_save":"0","id":12,"live_number":"1000012","title":"上天","updated_at":"2017-04-25 14:30:21","user_id":"123"}}
     * msg : succss
     * ret : 0
     */

    public DataBean data;
    public String msg;
    public int ret;


    public static class DataBean implements Serializable {
        /**
         * voiceLive : {"begin_at":"2017-04-12 12:12:12","cover":"we","created_at":"2017-04-25 14:30:21","enable_save":"0","id":12,"live_number":"1000012","title":"上天","updated_at":"2017-04-25 14:30:21","user_id":"123"}
         */

        public VoiceLiveBean voiceLive;

    }
}
