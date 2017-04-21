/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.woting.common.widgetui.xlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;

/**
 * 加载更多
 *
 * @author 辛龙
 *         2016年8月8日
 */
public class XListViewFooter extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;

    private Context mContext;

    private View mContentView;
    private View xlistview_footer_lin;
    private TextView mHintView;

    /**
     * 构造方法
     *
     * @param context
     */
    public XListViewFooter(Context context) {
        super(context);
        initView(context);
    }

    public XListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    // 初始化界面
    private void initView(Context context) {
        mContext = context;
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.xlistview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mContentView = moreView.findViewById(R.id.xlistview_footer_content);
        xlistview_footer_lin = moreView.findViewById(R.id.xlistview_footer_lin);
        mHintView = (TextView) moreView.findViewById(R.id.xlistview_footer_hint_textview);
    }

    /**
     * 高度足以调用加载
     *
     * @param state
     */
    public void setState(int state) {
        mHintView.setVisibility(View.INVISIBLE);
        xlistview_footer_lin.setVisibility(View.INVISIBLE);
        mHintView.setVisibility(View.INVISIBLE);
        if (state == STATE_READY) {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.xlistview_footer_hint_ready);
        } else if (state == STATE_LOADING) {
            xlistview_footer_lin.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.pull_to_refresh_load_more);
        } else {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.xlistview_footer_hint_normal);
            mHintView.setText(R.string.pull_to_refresh_footer_pull_label);
        }
    }

    /**
     * 样式改变 设置 bottomMargin
     *
     * @param height
     */
    public void setBottomMargin(int height) {
        if (height < 0) return;
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    /**
     * 获取bottomMargin
     */
    public int getBottomMargin() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        return lp.bottomMargin;
    }

    /**
     * normal status 正常状态
     */
    public void normal() {
        mHintView.setVisibility(View.VISIBLE);
        xlistview_footer_lin.setVisibility(View.GONE);
    }

    /**
     * loading status 加载状态
     */
    public void loading() {
        mHintView.setVisibility(View.GONE);
        xlistview_footer_lin.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏页脚当禁用加载更多
     * hide footer when disable pull load more
     */
    public void hide() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.height = 0;
        mContentView.setLayoutParams(lp);
    }

    /**
     * 展示页脚
     * show footer
     */
    public void show() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(lp);
    }

}
