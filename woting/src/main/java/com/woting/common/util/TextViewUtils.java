package com.woting.common.util;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * textview 工具类
 * @author 辛龙
 *2016年8月5日
 */
public class TextViewUtils {
	/**
	 *  TextView中的文字显示不同颜色的方法
	 * @param textview 要改变颜色的TV
	 * @param text
	 * @param color
	 * @param start
	 * @param end
	 * @param flags
	 */
	public static void showDifferentColor(TextView textview,CharSequence text,int color,int start,int end,int flags){
		SpannableStringBuilder style=new SpannableStringBuilder(text);
		style.setSpan(new ForegroundColorSpan(color),start,end,flags);
		textview.setText(style);
	}
}
