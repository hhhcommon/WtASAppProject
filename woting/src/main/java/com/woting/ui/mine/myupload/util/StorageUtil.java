package com.woting.ui.mine.myupload.util;

import android.os.Environment;
import android.util.Log;

/**
 * SD
 * Created by Administrator on 2016/12/5.
 */
public class StorageUtil {
    private static String TAG = StorageUtil.class.getName();

    /**
     * SD卡是否正常
     */
    public static boolean isStorageAvailable() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测SD是否可用
            Log.v(TAG, "SD卡不可用");
            return false;
        }
        return true;
    }

    public static String getSDPath(){
        if(isStorageAvailable()){
            return Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        }else{
            return null;
        }
    }
}
