package com.woting.common.widgetui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView
 * Created by Administrator on 2017/1/3.
 */
public class MarqueeTextView extends TextView {

    public MarqueeTextView(Context con) {
        super(con);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
