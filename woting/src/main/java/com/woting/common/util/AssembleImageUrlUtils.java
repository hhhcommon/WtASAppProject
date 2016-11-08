package com.woting.common.util;

/**
 * 图片路径组装工具
 * @author 辛龙
 * 2016年8月5日
 */
public class AssembleImageUrlUtils {

    /**
     * 图片大小 150_150 的图片路径
     */
    public static String assembleImageUrlSmall(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.indexOf("."));
        return result + "." + "150_150.png";
    }

    /**
     * 图片大小 300_300 的图片路径
     */
    public static String assembleImageUrl(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.indexOf("."));
        return result + "." + "300_300.png";
    }

    /**
     * 图片大小 450_450 的图片路径
     */
    public static String assembleImageUrlBig(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.indexOf("."));
        return result + "." + "450_450.png";
    }

}
