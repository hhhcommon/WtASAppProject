package com.woting.common.widgetui.photocut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.woting.common.util.PhoneMessage;

/**
 * 裁剪内页
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class ClipImageBorderView extends View {

    private int mHorizontalPadding;                           // 水平方向与View的边距
    private int mBorderWidth = 1;                             // 边框的宽度 单位dp
    private Paint mPaint;//

    public ClipImageBorderView(Context context) {
        this(context, null);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 转化成标准dp
        mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 用来防止边缘的锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.parseColor("#aa000000"));
        mPaint.setStyle(Style.FILL);
//        // 绘制左边1
//        canvas.drawRect(0, mHorizontalPadding, 0, getHeight()-mHorizontalPadding, mPaint);
//        // 绘制右边2
//        canvas.drawRect(getWidth(), mHorizontalPadding, getWidth(), getHeight()-mHorizontalPadding, mPaint);
        // 绘制上边3
        canvas.drawRect(0, 0, PhoneMessage.ScreenWidth,mHorizontalPadding, mPaint);
        // 绘制下边4
        canvas.drawRect(0, PhoneMessage.ScreenHeight - mHorizontalPadding,PhoneMessage.ScreenWidth, PhoneMessage.ScreenHeight, mPaint);
        // 绘制外边框
        mPaint.setColor( Color.parseColor("#66000000"));
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Style.STROKE);
        canvas.drawRect(0, mHorizontalPadding, PhoneMessage.ScreenWidth, PhoneMessage.ScreenHeight-mHorizontalPadding, mPaint);

    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

}
