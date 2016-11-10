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
    public static String assembleImageUrl150(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.indexOf("."));
//        return result + "." + "150_150.png";
        return srcUrl;
    }

    /**
     * 图片大小 180_180 的图片路径
     */
    public static String assembleImageUrl180(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.indexOf("."));
//        return result + "." + "180_180.png";
        return srcUrl;
    }

    /**
     * 图片大小 300_300 的图片路径
     */
    public static String assembleImageUrl300(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.indexOf("."));
//        return result + "." + "300_300.png";
        return srcUrl;
    }

    /**
     * 图片大小 450_450 的图片路径
     */
    public static String assembleImageUrl450(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.indexOf("."));
//        return result + "." + "450_450.png";
        return srcUrl;
    }

    /**
     * 图片大小自定义  size 的字符串格式: "**_**"
     */
    public static String assembleImageUrl(String srcUrl, String size) {
        String result = srcUrl.substring(0, srcUrl.indexOf("."));
//        return result + "." + size + ".png";
        return srcUrl;
    }

}
