package com.woting.ui.home.program.radiolist.mode;

import java.io.Serializable;

public class Image implements Serializable {

    private String MediaType;
    private String ContentId;
    private String LoopImg;

    public String getMediaType() {
        return MediaType;
    }

    public void setMediaType(String mediaType) {
        MediaType = mediaType;
    }

    public String getContentId() {
        return ContentId;
    }

    public void setContentId(String contentId) {
        ContentId = contentId;
    }

    public String getLoopImg() {
        return LoopImg;
    }

    public void setLoopImg(String loopImg) {
        LoopImg = loopImg;
    }
}
