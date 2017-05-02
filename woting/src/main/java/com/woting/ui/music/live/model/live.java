package com.woting.ui.music.live.model;

/**
 * live的对象
 * 作者：xinLong on 2017/5/2 10:47
 * 邮箱：645700751@qq.com
 */
public class live {
    private String id;                      // 标题
    private String title;                   // 标题
    private String cover;                   // 标题
    private String audience_count;          // 标题   0
    private String live_number;             // 标题   10000101
    private String begin_at;                // 标题   2017-05-02 10:37:00
    private owner owner;                    // 标题
    private channel channel;                // 标题


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAudience_count() {
        return audience_count;
    }

    public void setAudience_count(String audience_count) {
        this.audience_count = audience_count;
    }

    public String getLive_number() {
        return live_number;
    }

    public void setLive_number(String live_number) {
        this.live_number = live_number;
    }

    public String getBegin_at() {
        return begin_at;
    }

    public void setBegin_at(String begin_at) {
        this.begin_at = begin_at;
    }

    public com.woting.ui.music.live.model.owner getOwner() {
        return owner;
    }

    public void setOwner(com.woting.ui.music.live.model.owner owner) {
        this.owner = owner;
    }

    public com.woting.ui.music.live.model.channel getChannel() {
        return channel;
    }

    public void setChannel(com.woting.ui.music.live.model.channel channel) {
        this.channel = channel;
    }
}
