package com.woting.ui.home.program.fmlist.model;

import java.io.Serializable;
import java.util.List;

public class RankInfo implements Serializable {
	private String MediaType;
	private String CurrentContent;
	private String WatchPlayerNum;
	//以下为media=audio时解析实体类 为SEQU时数据类似 会直接跳转到一个相关的类表项里
	private String ContentId;
	private String ContentName;
	private String Actor;
	private String ContentImg;
	private String ContentURI;
	private String ContentSubjectWord;
	private String ContentPlay;
	private String ContentShareURL;
	private String ContentFavorite;
	private String ContentDescn;
	private String localurl;
	private String ContentTimes;
	private String ContentPub;
	private String ContentSubCount;
    private String ContentSeqId;
	private String ColumnNum;

	public String getColumnNum() {
		return ColumnNum;
	}

	public void setColumnNum(String columnNum) {
		ColumnNum = columnNum;
	}

	private List<ContentPersons> ContentPersons;// 主播信息

    public List<ContentPersons> getContentPersons() {
        return ContentPersons;
    }

    public void setContentPersons(List<ContentPersons> contentPersons) {
        ContentPersons = contentPersons;
    }

    public String getContentSeqId() {
        return ContentSeqId;
    }

    public void setContentSeqId(String contentSeqId) {
        ContentSeqId = contentSeqId;
    }

    private String PlayCount;
	private String sequId;//专辑ID
	private String sequImg;//专辑图片
	private String sequDesc;//专辑描述
	private String sequName;//专辑名称

	//界面展示状态
	private int viewtype=0;//界面决定组件 1为显示点选框 0是没有
	private int checktype=0;//点选框被选中为1 未被选中时为0

	private String IsPlaying;

	private String playTag;         // 标签<预留>
	private String ContentPlayType; // 内容后缀


	public String getContentPlayType() {
		return ContentPlayType;
	}

	public String getPlayTag() {
		return playTag;
	}

	public void setContentPlayType(String contentPlayType) {
		ContentPlayType = contentPlayType;
	}

	public void setPlayTag(String playTag) {
		this.playTag = playTag;
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
	public String getContentSubCount() {
		return ContentSubCount;
	}
	public void setContentSubCount(String contentSubCount) {
		ContentSubCount = contentSubCount;
	}

	public String getLocalurl() {
		return localurl;
	}
	public void setLocalurl(String localurl) {
		this.localurl = localurl;
	}

	public String getContentPub() {
		return ContentPub;
	}
	public void setContentPub(String contentPub) {
		ContentPub = contentPub;
	}
	public String getContentTimes() {
		return ContentTimes;
	}
	public void setContentTimes(String contentTimes) {
		ContentTimes = contentTimes;
	}

	public int getViewtype() {
		return viewtype;
	}
	public void setViewtype(int viewtype) {
		this.viewtype = viewtype;
	}
	public int getChecktype() {
		return checktype;
	}
	public void setChecktype(int checktype) {
		this.checktype = checktype;
	}
	public String getContentDescn() {
		return ContentDescn;
	}
	public void setContentDescn(String contentDesc) {
		ContentDescn = contentDesc;
	}
	public String getContentFavorite() {
		return ContentFavorite;
	}
	public void setContentFavorite(String contentFavorite) {
		ContentFavorite = contentFavorite;
	}
	public String getContentURI() {
		return ContentURI;
	}
	public void setContentURI(String contentURI) {
		ContentURI = contentURI;
	}
	public String getContentShareURL() {
		return ContentShareURL;
	}
	public void setContentShareURL(String contentShareURL) {
		ContentShareURL = contentShareURL;
	}
	public String getContentPlay() {
		return ContentPlay;
	}
	public void setContentPlay(String contentPlay) {
		ContentPlay = contentPlay;
	}
	public String getContentSubjectWord() {
		return ContentSubjectWord;
	}
	public void setContentSubjectWord(String contentSubjectWord) {
		ContentSubjectWord = contentSubjectWord;
	}
	private int type=1;//判断播放状态的type 1=播放 2=暂停

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMediaType() {
		return MediaType;
	}
	public void setMediaType(String mediaType) {
		MediaType = mediaType;
	}

	public String getCurrentContent() {
		return CurrentContent;
	}
	public void setCurrentContent(String currentContent) {
		CurrentContent = currentContent;
	}

	public String getWatchPlayerNum() {
		return WatchPlayerNum;
	}
	public void setWatchPlayerNum(String watchPlayerNum) {
		WatchPlayerNum = watchPlayerNum;
	}
	public String getContentId() {
		return ContentId;
	}
	public void setContentId(String contentId) {
		ContentId = contentId;
	}
	public String getContentName() {
		return ContentName;
	}
	public void setContentName(String contentName) {
		ContentName = contentName;
	}
	public String getActor() {
		return Actor;
	}
	public void setActor(String actor) {
		Actor = actor;
	}
	public String getContentImg() {
		return ContentImg;
	}
	public void setContentImg(String contentImg) {
		ContentImg = contentImg;
	}

	public String getSequId() {
		return sequId;
	}

	public void setSequId(String sequId) {
		this.sequId = sequId;
	}

	public String getSequImg() {
		return sequImg;
	}

	public void setSequImg(String sequImg) {
		this.sequImg = sequImg;
	}

	public String getSequDesc() {
		return sequDesc;
	}

	public void setSequDesc(String sequDesc) {
		this.sequDesc = sequDesc;
	}

	public String getSequName() {
		return sequName;
	}

	public void setSequName(String sequName) {
		this.sequName = sequName;
	}
}
