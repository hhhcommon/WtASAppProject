package com.woting.common.gatherdata.model;

import com.woting.common.config.GlobalConfig;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.PhoneMessage;

/**
 * 数据
 * Created by Administrator on 2017/4/11.
 */
public class DataModel {

    /**
     * 播放事件 == DATA_TYPE_PLAY
     * 打开新界面 == DATA_TYPE_OPEN
     */
    private int dataType;

    /**
     * 只有打开页面需要采集 播放事件不需要此数据
     */
    private ReqParam reqParam;
    private String deviceType;

    /**
     * 数据采集点：打开时间
     *
     * 播放时间：事件的时间
     */
    private String beginTime;

    /**
     * 只有播放事件
     * 当前播放事件所涉及的节目的播放或暂停时间点。这个时间是相对于节目的开始时间的。
     */
    private String endTime;

    /**
     * 数据采集点：是一个固定的值：L-open
     *
     * 播放时间：是一个枚举值：E-play;E-pause;E-close;
     */
    private String apiName;

    /**
     * 数据采集点（打开页面）:
     * 这里是打开信息的分类，分类见上一节“数据采集点”类似于MediaType
     * AUDIO:节目详情
     * SEQU:专辑详情
     * RADIO:电台详情
     * USER:详细信息
     * GROUP:用户组详细信息
     * ANCHOR:主播详细信息
     *
     * 播放情况（事件）:
     * 播放声音的类型，类似MediaType：目前仅有——AUDIO;RADIO;
     * 今后还会有主播——ANCHOR
     */
    private String objType;

    /**
     * 数据采集点：请求对象的Id
     *
     * 播放时间：节目ID、电台ID。
     * 今后还会有主播房间号。
     * 注意：房间号是和具体主播相关联的。
     */
    private String objId;

    /**
     * 公共数据
     */
    private String userId;// 用户 ID

    private String imei;// IMEI

    private String pcdType;// TYPE

    private String screenSize;

    private String longitude;// GPS 经度

    private String latitude;// GPS 纬度

    private String region;// 行政区划

    public DataModel() {

    }

    /**
     * 打开新界面
     */
    public DataModel(String beginTime, String apiName, String objType, ReqParam reqParam, String objId) {
        this.beginTime = beginTime;
        this.apiName = apiName;
        this.objType = objType;
        this.reqParam = reqParam;
        this.objId = objId;

        this.userId = CommonUtils.getSocketUserId();
        this.imei = PhoneMessage.imei;
        this.deviceType = "";
        this.pcdType = String.valueOf(GlobalConfig.PCDType);
        this.screenSize = PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight;
        this.longitude = PhoneMessage.longitude;
        this.latitude = PhoneMessage.latitude;
        this.region = "北京朝阳";
    }

    /**
     * 播放事件
     */
    public DataModel(String beginTime, String endTime, String apiName, String objType, String objId) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.apiName = apiName;
        this.objType = objType;
        this.objId = objId;

        this.userId = CommonUtils.getSocketUserId();
        this.imei = PhoneMessage.imei;
        this.pcdType = String.valueOf(GlobalConfig.PCDType);
        this.screenSize = PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight;
        this.longitude = PhoneMessage.longitude;
        this.latitude = PhoneMessage.latitude;
        this.region = "北京朝阳";
    }

    public ReqParam getReqParam() {
        return reqParam;
    }

    public void setReqParam(ReqParam reqParam) {
        this.reqParam = reqParam;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPcdType() {
        return pcdType;
    }

    public void setPcdType(String pcdType) {
        this.pcdType = pcdType;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

}
