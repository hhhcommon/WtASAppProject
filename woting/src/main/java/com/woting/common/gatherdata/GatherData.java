package com.woting.common.gatherdata;

import android.util.Log;

import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.gatherdata.model.DataModel;
import com.woting.common.gatherdata.thread.GivenUploadDataThread;
import com.woting.common.gatherdata.thread.ImmUploadDataThread;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.PhoneMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

/**
 * 收集用户数据
 * Created by Administrator on 2017/4/11.
 */
public class GatherData {

    public static int uploadType;

    public static boolean isRun = false;

    public static int uploadCount = IntegerConstant.DATA_UPLOAD_COUNT;// 指定上传的数量

    public static SynchronousQueue<DataModel> immQueue = new SynchronousQueue<>();// 保存即时上传数据

    public static List<DataModel> givenList = new ArrayList<>();// 保存定时或定量上传的数据

    private GatherData() {

    }

    /**
     * 初始化 开启上传数据的线程
     */
    public static void initThread() {
        // 防止 application 创建多次
        if (!isRun) {
            isRun = true;

            // 定量上传数据线程
            GivenUploadDataThread givenUploadDataThread = new GivenUploadDataThread();
            givenUploadDataThread.start();

            // 即时上传数据线程
            ImmUploadDataThread immUploadDataThread = new ImmUploadDataThread();
            immUploadDataThread.start();
        }
    }

    /**
     * 设置数据
     */
    public static DataModel setData() {
        DataModel data = new DataModel();
        data.setUserId(CommonUtils.getSocketUserId());// 用户 ID
        data.setImei(PhoneMessage.imei);// IMEI
        data.setPcdType(String.valueOf(GlobalConfig.PCDType));
        data.setScreenSize(PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);// 手机屏幕大小
        data.setLongitude(PhoneMessage.longitude);// 经度
        data.setLatitude(PhoneMessage.latitude);// 纬度
        data.setRegion(GlobalConfig.Region);// 行政区划
        return data;
    }

    /**
     * 收集数据
     * <p>
     * uploadType == -1 定量上传
     * uploadType == 0 实时上传
     * uploadType == 时间 定时上传
     */
    public static void collectData(int type, int dataType) {
        uploadType = type;
        DataModel data;
        switch (uploadType) {
            case IntegerConstant.DATA_UPLOAD_TYPE_IMM:// 即时上传
                if (dataType == IntegerConstant.DATA_TYPE_OPEN) {
                    data = collectOpenData();
                } else if (dataType == IntegerConstant.DATA_TYPE_PLAY) {
                    data = collectPlayData();
                } else {
                    data = null;
                }
                if (data != null) immQueue.add(data);
                break;
            case IntegerConstant.DATA_UPLOAD_TYPE_GIVEN:// 定量上传
                if (dataType == IntegerConstant.DATA_TYPE_OPEN) {
                    data = collectOpenData();
                } else if (dataType == IntegerConstant.DATA_TYPE_PLAY) {
                    data = collectPlayData();
                } else {
                    data = null;
                }
                if (data != null) givenList.add(data);
                break;
        }
    }

    /**
     * 收集数据
     */
    public static void collectData(int type, DataModel data) {
        uploadType = type;
        switch (uploadType) {
            case IntegerConstant.DATA_UPLOAD_TYPE_IMM:// 即时上传
                immQueue.add(data);
                break;
            case IntegerConstant.DATA_UPLOAD_TYPE_GIVEN:// 定时或定量上传
                givenList.add(data);
                break;
        }
    }

    /**
     * 收集打开新界面数据
     */
    private static DataModel collectOpenData() {
        DataModel data = setData();

        // 其他数据
        // ...

        return data;
    }

    /**
     * 收集播放事件
     */
    private static DataModel collectPlayData() {
        DataModel data = setData();

        // 其他数据
        // ...

        return data;
    }

    /**
     * 销毁线程
     */
    public static void destroyThread() {
        GatherData.isRun = false;

        Log.v("TAG", "GatherData Thread interrupt");
    }
}
