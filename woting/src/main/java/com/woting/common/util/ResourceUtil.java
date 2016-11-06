package com.woting.common.util;

/**
 * 作者：xinlong on 2016/10/21 12:37
 * 邮箱：645700751@qq.com
 */

import android.content.Context;

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
}
