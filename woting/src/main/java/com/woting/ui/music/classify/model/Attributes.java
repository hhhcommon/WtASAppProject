package com.woting.ui.music.classify.model;

import java.io.Serializable;

/**
 * 作者：xinlong on 2016/11/15 17:40
 * 邮箱：645700751@qq.com
 */
public class Attributes implements Serializable{
    private String nodeName;
    private String mId;
    private String id;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
