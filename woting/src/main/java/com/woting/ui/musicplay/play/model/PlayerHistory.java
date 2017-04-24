package com.woting.ui.musicplay.play.model;

import java.io.Serializable;

/**
 * 播放历史的数据库表
 *
 * @author 辛龙
 *         2016年4月5日
 */
public class PlayerHistory implements Serializable {

    private String ContentID;
    private String PlayerName;             // 播放显示名称
    private String PlayerImage;            // 播放显示图片
    private String PlayerUrl;              // 播放路径
    private String PlayerMediaType;        // 播放类型，radio，audio，sequ
    private String PlayerAllTime;          // 播放文件总时长
    private String PlayerContentDescn;     // 播放文件介绍
    private String PlayContentShareUrl;
    private String PlayerUrI;
    private String ContentFavorite;
    private String ContentPub;
    private String ColumnNum;
    private String playTag;             // 标签<预留>
    private String ContentPlayType;     // 内容后缀
    private String IsPlaying;           // 正在播放的内容
    private String PlayCount;           // 播放次数

    private String seqName;             // 专辑名称
    private String seqImg;              // 专辑图片
    private String seqDescn;            // 专辑描述
    private String seqId;               // 专辑ID


    private String PlayerInTime;        // 此时播放时长
    private String PlayerZanType;       // String类型的true,false
    private String BJUserId;            //
    private String PlayerAddTime;       // 播放时间

    private int status;                 // 是否选中状态  0 未选中  1 选中
    private boolean isCheck;            // 是否可以选中

    public PlayerHistory(String ContentID, String PlayerName, String PlayerImage, String PlayerUrl,
                         String PlayerUrI, String PlayerMediaType, String PlayerAllTime,
                         String playTag, String PlayerContentDescn, String ContentPlayType, String IsPlaying,
                         String ColumnNum, String PlayContentShareUrl, String ContentFavorite, String PlayCount,
                         String seqName, String seqImg, String seqDescn, String seqId,
                         String PlayerInTime, String PlayerZanType, String PlayerAddTime, String BJUserId) {
        super();
        this.ContentID = ContentID;
        this.PlayerName = PlayerName;                     // 播放显示名称
        this.PlayerImage = PlayerImage;                   // 播放显示图片
        this.PlayerUrl = PlayerUrl;                       // 播放路径
        this.PlayerMediaType = PlayerMediaType;           // 播放类型，radio，audio，sequ
        this.PlayerAllTime = PlayerAllTime;               // 播放文件总时长
        this.PlayerContentDescn = PlayerContentDescn;     // 播放文件介绍
        this.PlayContentShareUrl = PlayContentShareUrl;
        this.PlayerUrI = PlayerUrI;
        this.ContentFavorite = ContentFavorite;
        this.ColumnNum = ColumnNum;
        this.playTag = playTag;                           // 标签<预留>
        this.ContentPlayType = ContentPlayType;           // 内容后缀
        this.IsPlaying = IsPlaying;                       // 正在播放的内容
        this.PlayCount = PlayCount;                       // 播放次数

        this.seqId = seqId;                               // 专辑ID
        this.seqImg = seqImg;                             // 专辑图片
        this.seqDescn = seqDescn;                         // 专辑描述
        this.seqName = seqName;                           // 专辑名称

        this.PlayerAddTime = PlayerAddTime;               // 播放时间
        this.PlayerInTime = PlayerInTime;                 // 此时播放时长
        this.PlayerZanType = PlayerZanType;               // String类型的true,false
        this.BJUserId = BJUserId;                         //
    }


    public String getContentID() {
        return ContentID;
    }

    public void setContentID(String contentID) {
        ContentID = contentID;
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String playerName) {
        PlayerName = playerName;
    }

    public String getPlayerImage() {
        return PlayerImage;
    }

    public void setPlayerImage(String playerImage) {
        PlayerImage = playerImage;
    }

    public String getPlayerUrl() {
        return PlayerUrl;
    }

    public void setPlayerUrl(String playerUrl) {
        PlayerUrl = playerUrl;
    }

    public String getPlayerMediaType() {
        return PlayerMediaType;
    }

    public void setPlayerMediaType(String playerMediaType) {
        PlayerMediaType = playerMediaType;
    }

    public String getPlayerAllTime() {
        return PlayerAllTime;
    }

    public void setPlayerAllTime(String playerAllTime) {
        PlayerAllTime = playerAllTime;
    }

    public String getPlayerContentDescn() {
        return PlayerContentDescn;
    }

    public void setPlayerContentDescn(String playerContentDescn) {
        PlayerContentDescn = playerContentDescn;
    }

    public String getPlayContentShareUrl() {
        return PlayContentShareUrl;
    }

    public void setPlayContentShareUrl(String playContentShareUrl) {
        PlayContentShareUrl = playContentShareUrl;
    }

    public String getPlayerUrI() {
        return PlayerUrI;
    }

    public void setPlayerUrI(String playerUrI) {
        PlayerUrI = playerUrI;
    }

    public String getContentFavorite() {
        return ContentFavorite;
    }

    public void setContentFavorite(String contentFavorite) {
        ContentFavorite = contentFavorite;
    }

    public String getContentPub() {
        return ContentPub;
    }

    public void setContentPub(String contentPub) {
        ContentPub = contentPub;
    }

    public String getColumnNum() {
        return ColumnNum;
    }

    public void setColumnNum(String columnNum) {
        ColumnNum = columnNum;
    }

    public String getPlayTag() {
        return playTag;
    }

    public void setPlayTag(String playTag) {
        this.playTag = playTag;
    }

    public String getContentPlayType() {
        return ContentPlayType;
    }

    public void setContentPlayType(String contentPlayType) {
        ContentPlayType = contentPlayType;
    }

    public String getIsPlaying() {
        return IsPlaying;
    }

    public void setIsPlaying(String isPlaying) {
        IsPlaying = isPlaying;
    }

    public String getPlayCount() {
        return PlayCount;
    }

    public void setPlayCount(String playCount) {
        PlayCount = playCount;
    }

    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    public String getSeqImg() {
        return seqImg;
    }

    public void setSeqImg(String seqImg) {
        this.seqImg = seqImg;
    }

    public String getSeqDescn() {
        return seqDescn;
    }

    public void setSeqDescn(String seqDescn) {
        this.seqDescn = seqDescn;
    }

    public String getSeqName() {
        return seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    public String getPlayerAddTime() {
        return PlayerAddTime;
    }

    public void setPlayerAddTime(String playerAddTime) {
        PlayerAddTime = playerAddTime;
    }

    public String getPlayerInTime() {
        return PlayerInTime;
    }

    public void setPlayerInTime(String playerInTime) {
        PlayerInTime = playerInTime;
    }

    public String getPlayerZanType() {
        return PlayerZanType;
    }

    public void setPlayerZanType(String playerZanType) {
        PlayerZanType = playerZanType;
    }

    public String getBJUserId() {
        return BJUserId;
    }

    public void setBJUserId(String BJUserId) {
        this.BJUserId = BJUserId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
