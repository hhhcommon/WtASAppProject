package com.woting.ui.mine.myupload.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * 文件操作类
 * Created by Administrator on 2016/11/28.
 */
public class FileUtils {
    public static String getAudioRecordFilePath() {
        if (isSDCardAvaliable()) {
            File mFile = new File(Constants.AUDIO_DIRECTORY);
            if (!mFile.exists()) {
                try {
                    if(!mFile.mkdirs()) {
                        Log.v("mkdirs", "mkdirs");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return mFile.getAbsolutePath();
        }
        return null;
    }

    public static boolean isSDCardAvaliable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getAAcFilePath(String audioRecordFileName) {
        return getAudioRecordFilePath() + "/" + audioRecordFileName + Constants.AAC_SUFFIX;
    }

    public static String getPcmFilePath(String audioRecordFileName) {
        return getAudioRecordFilePath() + "/" + audioRecordFileName + Constants.PCM_SUFFIX;
    }

    public static String getM4aFilePath(String audioRecordFileName) {
        return getAudioRecordFilePath() + "/" + audioRecordFileName + Constants.M4A_SUFFIX;
    }
}
