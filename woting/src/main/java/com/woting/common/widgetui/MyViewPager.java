package com.woting.common.widgetui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义 ViewPager 拦截 ViewPager 的滑动事件
 * @author woting11
 */
@SuppressLint("ClickableViewAccessibility")
public class MyViewPager extends ViewPager {
	
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean isMove = true;		//标记是否可以滑动

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (isMove) {
			return super.onInterceptTouchEvent(arg0);
		}else{
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (isMove) {
			return super.onTouchEvent(arg0);
		}else{
			return false;
		}
	}
}

