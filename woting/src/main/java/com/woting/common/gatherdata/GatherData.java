package com.woting.common.gatherdata;

import android.util.Log;

import com.woting.common.constant.IntegerConstant;
import com.woting.common.gatherdata.model.DataModel;
import com.woting.common.gatherdata.thread.GivenUploadDataThread;
import com.woting.common.gatherdata.thread.ImmUploadDataThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

/**
 * 收集用户数据
 * Created by Administrator on 2017/4/11.
 */
public class GatherData {

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
     * 收集数据
     */
    public static void collectData(int uploadType, DataModel data) {
        switch (uploadType) {
            case IntegerConstant.DATA_UPLOAD_TYPE_IMM:// 即时上传
                immQueue.add(data);
                break;
            case IntegerConstant.DATA_UPLOAD_TYPE_GIVEN:// 定时检查上传
                givenList.add(data);
                break;
        }
    }

    /**
     * 销毁线程
     */
    public static void destroyThread() {
        GatherData.isRun = false;

        Log.v("TAG", "GatherData Thread interrupt");
    }
}
