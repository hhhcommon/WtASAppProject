package com.woting.common.widgetui;

import android.widget.GridView;

/**
 * 作者：xinlong on 2016/11/14 17:44
 * 邮箱：645700751@qq.com
 */
public class MyGridView extends GridView {
    public MyGridView(android.content.Context context,
                      android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
