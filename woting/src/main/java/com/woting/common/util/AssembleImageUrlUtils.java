package com.woting.common.util;
/**
 * 图片路径组装工具
 * @author 辛龙
 *2016年8月5日
 */
public class AssembleImageUrlUtils {

	/** 
	 *根据设计组装实际使用的图片路径
	 * @param srcUrl
	 * @param size
	 * @return 
	 */  
	public static String assembleImageUrl(String srcUrl,String size){
		String result = srcUrl.substring(0, srcUrl.indexOf("."));
		String url=result+"."+size+".png";
		return url;
	}

}
