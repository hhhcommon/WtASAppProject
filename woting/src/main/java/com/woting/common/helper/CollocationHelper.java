package com.woting.common.helper;

import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.CollocationConstant;

/**
 * 配置设置
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class CollocationHelper {

    private static String _PCDType = "1";
    private static String _socketPort = "16789";
    private static String _uploadBaseUrl = "http://123.56.254.75:908/CM/";
//    private static String _uploadBaseUrl = "http://182.92.175.134:908/CM/";
//    private static String _socketUrl = "182.92.175.134";//生产服务器地址
    private static String _socketUrl = "123.56.254.75";//测试服务器地址
    //	private static String _socketUrl = "192.168.5.17";//
//    private static String _baseUrl = "http://182.92.175.134:808/";//生产服务器地址
    private static String _baseUrl = "http://123.56.254.75:808/";//测试服务器地址
    //  private static String _baseUrl = "http://192.168.5.17:808/";

    public static void setCollocation() {
        // 是不是读取配置文件
        if (GlobalConfig.isCollocation) {
            // 是否弹出提示框，0提示，1不提示
            String isToast = BSApplication.SharedPreferences.getString(CollocationConstant.isToast, "1");
            //PersonClientDevice(个人客户端设备) 终端类型1=app,2=设备，3=pc
            String PCDType = BSApplication.SharedPreferences.getString(CollocationConstant.PCDType, "1");
            //socket请求路径
            String socketUrl = BSApplication.SharedPreferences.getString(CollocationConstant.socketUrl, "");
            //socket端口号
            String socketPort = BSApplication.SharedPreferences.getString(CollocationConstant.socketPort, "");
            //http请求总url
            String baseUrl = BSApplication.SharedPreferences.getString(CollocationConstant.baseUrl, "");
            //上传文件总接口路径
            String uploadBaseUrl = BSApplication.SharedPreferences.getString(CollocationConstant.uploadBaseUrl, "");

            if (isToast != null && !isToast.equals("") && !isToast.equals("isToast") && isToast.trim().equals("0")) {
                GlobalConfig.isToast = true;
            } else {
                GlobalConfig.isToast = false;
            }

            if (PCDType != null && !PCDType.equals("")) {
                GlobalConfig.PCDType = Integer.parseInt(PCDType);
            } else {
                GlobalConfig.PCDType = Integer.parseInt(_PCDType);
            }

            if (socketUrl != null && !socketUrl.equals("")) {
                GlobalConfig.socketUrl = socketUrl;
            } else {
                GlobalConfig.socketUrl = _socketUrl;
            }

            if (socketPort != null && !socketPort.equals("")) {
                GlobalConfig.socketPort = Integer.parseInt(socketPort);
            } else {
                GlobalConfig.socketPort = Integer.parseInt(_socketPort);
            }

            if (baseUrl != null && !baseUrl.equals("")) {
                GlobalConfig.baseUrl = baseUrl;
                GlobalConfig.imageurl = baseUrl + "wt/";
            } else {
                GlobalConfig.baseUrl = _baseUrl;
                GlobalConfig.imageurl = _baseUrl + "wt/";
            }

            if (uploadBaseUrl != null && !uploadBaseUrl.equals("")) {
                GlobalConfig.uploadBaseUrl = uploadBaseUrl;
            } else {
                GlobalConfig.uploadBaseUrl = _uploadBaseUrl;
            }
        } else {
            GlobalConfig.isToast = false;
            GlobalConfig.PCDType = Integer.parseInt(_PCDType);
            GlobalConfig.socketUrl = _socketUrl;
            GlobalConfig.socketPort = Integer.parseInt(_socketPort);
            GlobalConfig.baseUrl = _baseUrl;
            GlobalConfig.uploadBaseUrl = _uploadBaseUrl;
            GlobalConfig.imageurl = _baseUrl + "wt/";
        }
    }
}
