package com.woting.ui.model;


import java.io.Serializable;

/**
 * 公共节目的model
 * 作者：xinlong on 2016/8/23 21:18
 * 邮箱：645700751@qq.com
 */
  public class commonContent  implements Serializable{
	public String PlayCount;         // 播放数量 0
	public String CTime;             // 创建时间 1469250320000
	public String ContentName;       // 电台名称 北京新闻广播
	public String ContentPub;        // 电台所属 北京人民广播电台
	public String ContentSource;     // 数据来源 蜻蜓FM
	public String ContentDescn;      // 节目描述  北京新闻广播以“新闻谈话台”为频道定位。。。
	public String MediaType;         // 节目类型 RADIO
	public String ContentId;         // 节目ID 83dc3045c7d3
	public String ContentShareURL;   // 分享地址 http://www.wotingfm.com/share/mweb/dt/83dc3045c7d3/content.html
	public String ContentImg;        // 节目头像 http://pic.qingting.fm/2015/0514/2015051417385422.jpg!200
	public String ContentFavorite;   // 喜欢人数 0
	public String ContentURI;        //


	//界面展示状态
	private int type = 1;            // 判断播放状态的type 1=播放 2=暂停
	private int viewtype = 0;        // 界面决定组件 1为显示点选框 0是没有
	private int checktype = 0;       // 点选框被选中为1 未被选中时为0
	private String playTag;          // 标签<预留>
	private String ContentPlayType;  // 内容后缀
	private String localurl;
	// 文件上传
	public String ContentSubCount;   // 集数
	public String WatchPlayerNum;    // 收听次数
	public String ContentTimes;      // 收听次数
	public String Downloadtype;      //

	public String getDownloadtype() {
		return Downloadtype;
	}

	public void setDownloadtype(String downloadtype) {
		Downloadtype = downloadtype;
	}

	public String getContentURI() {
		return ContentURI;
	}

	public void setContentURI(String contentURI) {
		ContentURI = contentURI;
	}

	public String getContentTimes() {
		return ContentTimes;
	}

	public void setContentTimes(String contentTimes) {
		ContentTimes = contentTimes;
	}

	public String getWatchPlayerNum() {
		return WatchPlayerNum;
	}

	public void setWatchPlayerNum(String watchPlayerNum) {
		WatchPlayerNum = watchPlayerNum;
	}

	public String getContentSubCount() {
		return ContentSubCount;
	}

	public void setContentSubCount(String contentSubCount) {
		ContentSubCount = contentSubCount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public String getLocalurl() {
		return localurl;
	}

	public void setLocalurl(String localurl) {
		this.localurl = localurl;
	}

	public String getPlayCount() {
		return PlayCount;
	}

	public void setPlayCount(String playCount) {
		PlayCount = playCount;
	}

	public String getCTime() {
		return CTime;
	}

	public void setCTime(String CTime) {
		this.CTime = CTime;
	}

	public String getContentName() {
		return ContentName;
	}

	public void setContentName(String contentName) {
		ContentName = contentName;
	}

	public String getContentPub() {
		return ContentPub;
	}

	public void setContentPub(String contentPub) {
		ContentPub = contentPub;
	}

	public String getContentSource() {
		return ContentSource;
	}

	public void setContentSource(String contentSource) {
		ContentSource = contentSource;
	}

	public String getContentDescn() {
		return ContentDescn;
	}

	public void setContentDescn(String contentDescn) {
		ContentDescn = contentDescn;
	}

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

	public String getContentShareURL() {
		return ContentShareURL;
	}

	public void setContentShareURL(String contentShareURL) {
		ContentShareURL = contentShareURL;
	}

	public String getContentImg() {
		return ContentImg;
	}

	public void setContentImg(String contentImg) {
		ContentImg = contentImg;
	}

	public String getContentFavorite() {
		return ContentFavorite;
	}

	public void setContentFavorite(String contentFavorite) {
		ContentFavorite = contentFavorite;
	}
}
