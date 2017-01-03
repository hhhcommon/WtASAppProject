package com.woting.common.util;

/**
 * 作者：xinlong on 2016/10/21 12:37
 * 邮箱：645700751@qq.com
 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.woting.common.config.GlobalConfig;

import java.io.File;

public class ResourceUtil {
    public ResourceUtil() {
    }

    public static int getLayoutId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "layout", paramContext.getPackageName());
    }

    public static int getStringId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "string", paramContext.getPackageName());
    }

    public static int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "drawable", paramContext.getPackageName());
    }

    public static int getStyleId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "style", paramContext.getPackageName());
    }

    public static int getId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "id", paramContext.getPackageName());
    }

    public static int getColorId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "color", paramContext.getPackageName());
    }

    public static int getRawId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "raw", paramContext.getPackageName());
    }

    public static int getStyleableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "styleable", paramContext.getPackageName());
    }

    /**
     * 获取金山云播放器的缓存地址
     */
    public static String getLocalUrlForKsy() {
        String fileUrl;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileUrl = GlobalConfig.playCacheDirO + GlobalConfig.ksyPlayCache;
        } else {
            fileUrl = getSDCardPath()[0] + GlobalConfig.ksyPlayCache;
        }
        Log.e("获取金山云播放器的缓存地址", fileUrl + "");
        return fileUrl;
    }

    /**
     * 获取软件更新下载安装的地址
     */
    public static String getLocalUrlForUpload() {
        String filePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD 卡
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + GlobalConfig.upLoadCache;
        } else {// 没有 SD 卡
            filePath = getSDCardPath()[0] + GlobalConfig.upLoadCache;
        }
        Log.e("TAG", filePath + "");
        return filePath;
    }

    /**
     * 得到 sdcard 的路径
     * @return 返回一个字符串数组   下标 0:内置 sdcard   下标 1:外置 sdcard
     */
    private static String[] getSDCardPath() {
        String[] sdCardPath = new String[2];
        File sdFile = Environment.getExternalStorageDirectory();
        File[] files = sdFile.getParentFile().listFiles();
        for (File file : files) {
            if (file.getAbsolutePath().equals(sdFile.getAbsolutePath())) {// 外置 sdcard
                sdCardPath[1] = sdFile.getAbsolutePath();
            } else if (file.getAbsolutePath().contains("sdcard")) {// 内置 sdcard
                sdCardPath[0] = file.getAbsolutePath();
            }
        }
        return sdCardPath;
    }
}
