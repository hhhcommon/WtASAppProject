package com.woting.common.widgetui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.woting.R;

/**
 * 提示 View 的管理
 * Created by Administrator on 2016/12/22.
 */
public class TipView extends RelativeLayout {
    private String tipString;
    private float tipStringSize;
    private int tipStringColor;
    private Drawable drawableTipTop;
    private float drawableTipPadding;

    private View viewBackground;// View 的背景
    private TextView textTip;// 提示  包含文字和图片
    private LayoutParams viewLayoutParams;

    private WhiteViewClick mWhiteViewClick;
    private TipViewClick mTipViewClick;

    /**
     * 提示类型
     */
    public enum TipStatus {
        NO_DATA, // 没有数据
        NO_NET, // 没有网络
        NO_LOGIN, // 没有登录
        IS_ERROR, // 加载错误
    }

    public TipView(Context context) {
        this(context, null);
    }

    public TipView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TipView);
        tipString = array.getString(R.styleable.TipView_tipText);
        tipStringSize = array.getDimension(R.styleable.TipView_tipTextSize, 16);
        tipStringColor = array.getColor(R.styleable.TipView_tipColor, Color.parseColor("#969696"));
        drawableTipTop = array.getDrawable(R.styleable.TipView_drawableTop);
        drawableTipPadding = array.getDimension(R.styleable.TipView_drawablePadding, 20);
        array.recycle();

        viewBackground = new View(context);
        viewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(viewBackground, viewLayoutParams);

        textTip = new TextView(context);
        textTip.setText(tipString);
        textTip.setTextSize(tipStringSize);
        textTip.setTextColor(tipStringColor);
        textTip.setCompoundDrawablesWithIntrinsicBounds(null, drawableTipTop, null, null);
        textTip.setCompoundDrawablePadding((int) drawableTipPadding);
        textTip.setGravity(Gravity.CENTER);

        viewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewLayoutParams.addRule(CENTER_IN_PARENT, TRUE);
        addView(textTip, viewLayoutParams);

        // 点击空白处
        viewBackground.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWhiteViewClick != null) {
                    mWhiteViewClick.onWhiteViewClick();
                }
            }
        });

        // 点击提示处
        textTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTipViewClick != null) {
                    mTipViewClick.onTipViewClick();
                }
            }
        });
    }

    /**
     * 设置提示和空白处点击事件
     */
    public void setWhiteClick(WhiteViewClick whiteViewClick) {
        this.mWhiteViewClick = whiteViewClick;
    }

    /**
     * 设置提示和提示处点击事件
     */
    public void setTipClick(TipViewClick tipViewClick) {
        this.mTipViewClick = tipViewClick;
    }

    /**
     * 设置提示
     */
    public void setTipView(TipStatus tipStatus) {
        setTipView(tipStatus, null);
    }

    /**
     * 设置提示类型
     * @param tipStatus enum 提示类型
     */
    public void setTipView(TipStatus tipStatus, String tipString) {
        switch (tipStatus) {
            case NO_DATA:// 没有数据
                if(tipString != null) textTip.setText(tipString);
                else textTip.setText("没有数据");
                textTip.setGravity(Gravity.CENTER);
                textTip.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_tip_no_data), null, null);
                break;
            case NO_NET:// 没有网络
                if(tipString != null) textTip.setText(tipString);
                else textTip.setText("~网络偷懒了~\n点击屏幕再试一次吧");
                textTip.setGravity(Gravity.CENTER);
                textTip.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_tip_no_net), null, null);
                break;
            case NO_LOGIN:// 没有登录
                if(tipString != null) textTip.setText(tipString);
                else textTip.setText("想使用享讲功能，快去登录呦");
                textTip.setGravity(Gravity.CENTER);
                textTip.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_tip_no_login), null, null);
                break;
            case IS_ERROR:// 加载错误
                if(tipString != null) textTip.setText(tipString);
                else textTip.setText("数据出错了\n点击界面重新获取数据哟");
                textTip.setGravity(Gravity.CENTER);
                textTip.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_tip_error), null, null);
                break;
        }
        invalidate();
    }

    public interface WhiteViewClick {
        /**
         * 点击空白 View 监听
         */
        void onWhiteViewClick();
    }

    public interface TipViewClick {
        /**
         * 点击提示 View 监听
         */
        void onTipViewClick();
    }
}
