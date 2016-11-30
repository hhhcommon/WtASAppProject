package com.woting.ui.mine.myupload.util;

import android.os.Environment;

/**
 * 录音文件相关信息
 * Created by Administrator on 2016/11/28.
 */
public class Constants {

    /**
     * 根目录
     */
    public static String fish_saying_root = "/com.woting.record";

    /**
     * 获取音频保存目录
     */
    public static String AUDIO_DIRECTORY = Environment.getExternalStorageDirectory() +
            fish_saying_root + "/audio_record";

    /**
     * 录制音频初始后缀
     */
    public static final String PCM_SUFFIX = ".pcm";

    /**
     * 转换成aac音频后缀
     */
    public static final String AAC_SUFFIX = ".aac";

    /**
     * 转换成m4a音频后缀
     */
    public static final String M4A_SUFFIX = ".m4a";
}
