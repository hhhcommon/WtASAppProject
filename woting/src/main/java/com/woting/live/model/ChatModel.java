package com.woting.live.model;

import java.io.Serializable;

/**
 * Created by amine on 2017/4/25.
 */

public class ChatModel implements Serializable {
    public String name;
    public String uid;
    public String chatContent;
    //1 代表文本说话，2代表谁进入房间
    public int type;
}
