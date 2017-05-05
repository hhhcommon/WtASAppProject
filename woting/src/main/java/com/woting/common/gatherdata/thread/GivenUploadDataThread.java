package com.woting.common.gatherdata.thread;

import android.util.Log;

import com.woting.common.gatherdata.GatherData;
import com.woting.common.gatherdata.model.DataModel;
import com.woting.common.util.JsonEncloseUtils;
import com.woting.common.volley.VolleyRequest;

import java.util.ArrayList;

/**
 * 定时、定量上传数据线程
 * Created by Administrator on 2017/4/11.
 */
public class GivenUploadDataThread extends Thread {
    private volatile Object Lock = new Object();            // 锁

    @Override
    public void run() {
        while (GatherData.isRun) {
            Log.v("TAG", "Gather Data Thread start");
            try {
                if (GatherData.givenList.size() >= GatherData.uploadCount) {
                    synchronized (Lock) {
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < GatherData.givenList.size(); i++) {
                            DataModel n = GatherData.givenList.take();
                            String jsonStr = JsonEncloseUtils.btToString(n);
                            list.add(jsonStr);
                            Log.v("TAG", "Gather Data -- > " + i);
                            if(i==GatherData.uploadCount) break;
                        }

                        String jsonStr = JsonEncloseUtils.jsonEnclose(list).toString();
                        if (jsonStr != null) {
                            Log.v("TAG", "GIVEN jsonStr -- > > " + jsonStr);
                            // 上传数据
                            VolleyRequest.updateData(jsonStr);
                            list.clear();
                        }
                    }

                }

                Thread.sleep(2 * 1000 * 60);// 一定时间检查一次  如果有数据则上传
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
