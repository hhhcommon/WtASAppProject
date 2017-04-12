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
    private ReqParam ReqParam;
    private String DeviceType;

    /**
     * 数据采集点：打开时间
     *
     * 播放时间：事件的时间
     */
    private String BeginTime;

    /**
     * 只有播放事件
     * 当前播放事件所涉及的节目的播放或暂停时间点。这个时间是相对于节目的开始时间的。
     */
    private String EndTime;

    /**
     * 数据采集点：是一个固定的值：L-open
     *
     * 播放时间：是一个枚举值：E-play;E-pause;E-close;
     */
    private String ApiName;

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
    private String ObjType;

    /**
     * 数据采集点：请求对象的Id
     *
     * 播放时间：节目ID、电台ID。
     * 今后还会有主播房间号。
     * 注意：房间号是和具体主播相关联的。
     */
    private String ObjId;

    /**
     * 公共数据
     */
    private String UserId;// 用户 ID

    private String IMEI;// IMEI

    private String PCDType;// TYPE

    private String ScreenSize;// 屏幕大小

    private String longitude;// GPS 经度

    private String latitude;// GPS 纬度

    private String Region;// 行政区划

    public DataModel() {

    }

    /**
     * 打开新界面
     */
    public DataModel(String beginTime, String apiName, String objType, ReqParam reqParam, String objId) {
        this.BeginTime = beginTime;
        this.ApiName = apiName;
        this.ObjType = objType;
        this.ReqParam = reqParam;
        this.ObjId = objId;

        this.UserId = CommonUtils.getSocketUserId();
        this.IMEI = PhoneMessage.imei;
        this.DeviceType = "";
        this.PCDType = String.valueOf(GlobalConfig.PCDType);
        this.ScreenSize = PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight;
        this.longitude = PhoneMessage.longitude;
        this.latitude = PhoneMessage.latitude;
        this.Region = "北京朝阳";
    }

    /**
     * 播放事件
     */
    public DataModel(String beginTime, String endTime, String apiName, String objType, String objId) {
        this.BeginTime = beginTime;
        this.EndTime = endTime;
        this.ApiName = apiName;
        this.ObjType = objType;
        this.ObjId = objId;

        this.UserId = CommonUtils.getSocketUserId();
        this.IMEI = PhoneMessage.imei;
        this.PCDType = String.valueOf(GlobalConfig.PCDType);
        this.ScreenSize = PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight;
        this.longitude = PhoneMessage.longitude;
        this.latitude = PhoneMessage.latitude;
        this.Region = "北京朝阳";
    }

    public ReqParam getReqParam() {
        return ReqParam;
    }

    public void setReqParam(ReqParam reqParam) {
        this.ReqParam = reqParam;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        this.DeviceType = deviceType;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        this.BeginTime = beginTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        this.EndTime = endTime;
    }

    public String getApiName() {
        return ApiName;
    }

    public void setApiName(String apiName) {
        this.ApiName = apiName;
    }

    public String getObjType() {
        return ObjType;
    }

    public void setObjType(String objType) {
        this.ObjType = objType;
    }

    public String getObjId() {
        return ObjId;
    }

    public void setObjId(String objId) {
        this.ObjId = objId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getImei() {
        return IMEI;
    }

    public void setImei(String imei) {
        this.IMEI = imei;
    }

    public String getPcdType() {
        return PCDType;
    }

    public void setPcdType(String pcdType) {
        this.PCDType = pcdType;
    }

    public String getScreenSize() {
        return ScreenSize;
    }

    public void setScreenSize(String screenSize) {
        this.ScreenSize = screenSize;
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
        return Region;
    }

    public void setRegion(String region) {
        this.Region = region;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

}
