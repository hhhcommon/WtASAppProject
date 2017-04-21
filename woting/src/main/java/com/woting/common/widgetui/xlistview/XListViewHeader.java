package com.woting.common.widgetui.xlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.util.TimeUtils;
/**
 * 下拉刷新
 *
 * @author 辛龙
 *         2016年8月8日
 */
public class XListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private TextView mHintTextView;
	private int mState = STATE_NORMAL;

	private final int ROTATE_ANIM_DURATION = 180;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	/**
	 * 构造方法
	 * @param context
     */
	public XListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	/**
	 * 初始化界面
	 * @param context
     */
	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xlistview_header, null);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,0);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView)findViewById(R.id.xlistview_header_arrow);
		mHintTextView = (TextView)findViewById(R.id.xlistview_header_hint_textview);
		mProgressBar = (ProgressBar)findViewById(R.id.xlistview_header_progressbar);
		// 转换动画
		Animation mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		// 转换动画
		Animation mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	/**
	 * 样式设置
	 * @param state
     */
	public void setState(int state) {
		if (state == mState) return ;

		if (state == STATE_REFRESHING) {	// 显示进度
			mArrowImageView.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else {	                        // 显示箭头图片
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}

		switch(state){
		case STATE_NORMAL:
			mHintTextView.setText(R.string.xlistview_header_hint_normal);
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				mHintTextView.setText(R.string.xlistview_header_hint_ready);
			}
			break;
		case STATE_REFRESHING:
			mHintTextView.setText(R.string.xlistview_header_hint_loading);
			break;
			default:
		}
		mState = state;
	}

	public void setVisibleHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisibleHeight() {
		return mContainer.getHeight();
	}
}
