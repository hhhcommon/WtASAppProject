package com.woting.common.util;

/**
 * 作者：xinlong on 2016/10/21 12:37
 * 邮箱：645700751@qq.com
 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.woting.common.config.GlobalConfig;

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
     * @return
     */
    public static String getLocalUrlForKsy() {
        String fileUrl;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileUrl = GlobalConfig.playCacheDirO+GlobalConfig.ksyPlayCache;
        } else {
            fileUrl = GlobalConfig.playCacheDirI+GlobalConfig.ksyPlayCache;
        }
        Log.e("获取金山云播放器的缓存地址",fileUrl+"");
        return fileUrl;
    }

    /**
     * 获取软件更新下载安装的地址
     * @return
     */
    public static String getLocalUrlForUpload() {
        String fileUrl;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileUrl = GlobalConfig.playCacheDirO+GlobalConfig.upLoadCache;
        } else {
            fileUrl = GlobalConfig.playCacheDirI+GlobalConfig.upLoadCache;
        }
        Log.e("获取软件更新下载安装的地址",fileUrl+"");
        return fileUrl;
    }



}
