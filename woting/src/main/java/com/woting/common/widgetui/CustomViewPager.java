package com.woting.common.widgetui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {  
	  
    private boolean isCanScroll = true;  
  
    public CustomViewPager(Context context) {  
        super(context);  
    }  
  
    public CustomViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    if (isCanScroll == false) {
    return false;
    } else {
    return super.onTouchEvent(ev);
    }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (isCanScroll == false) {
    return false;
    } else {
    return super.onInterceptTouchEvent(ev);
    }

    }
    
    public void setScanScroll(boolean isCanScroll){  
        this.isCanScroll = isCanScroll;  
    }  
  
  
}