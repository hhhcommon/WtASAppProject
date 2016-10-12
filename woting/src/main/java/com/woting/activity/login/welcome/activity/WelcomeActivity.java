package com.woting.activity.login.welcome.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;

import com.woting.R;
import com.woting.activity.login.welcome.fragment.WelcomeaFragment;
import com.woting.activity.login.welcome.fragment.WelcomebFragment;
import com.woting.activity.login.welcome.fragment.WelcomecFragment;
import com.woting.common.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 引导页
 * @author 辛龙
 * 2016年4月27日
 */
public class WelcomeActivity extends FragmentActivity {
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private ImageView[] imageViews;
	private ImageView imageView;
	private ViewGroup group;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcomes);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		imageViews = new ImageView[3];		//设置引导页下标小红点
		group = (ViewGroup)findViewById(R.id.viewGroup);  
		for (int i = 0; i < 3; i++) {  
			imageView = new ImageView(this);  
			imageView.setLayoutParams(new LayoutParams(20,20));  
			imageView.setPadding(20, 0, 20, 0);  
			imageViews[i] = imageView;  
			if (i == 0) {  
				//默认选中第一张图片
				imageViews[i].setBackgroundResource(R.mipmap.page_indicator_focused);
			} else {  
				imageViews[i].setBackgroundResource(R.mipmap.page_indicator);
			}  
			group.addView(imageViews[i]);  
		}  
		InitViewPager();
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewpager);
		mPager.setOffscreenPageLimit(1);
		fragmentList = new ArrayList<Fragment>();
		Fragment btFragmenta = new WelcomeaFragment();
		Fragment btFragmentb = new WelcomebFragment();
		Fragment btFragmentc = new WelcomecFragment();
		fragmentList.add(btFragmenta);
		fragmentList.add(btFragmentb);
		fragmentList.add(btFragmentc);
		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
		mPager.setOnPageChangeListener(new GuidePageChangeListener()); 
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
	}

	// 指引页面更改事件监听器
	class GuidePageChangeListener implements OnPageChangeListener {  
		@Override  
		public void onPageScrollStateChanged(int arg0) {  
		}
		
		@Override  
		public void onPageScrolled(int arg0, float arg1, int arg2) {  
		}
		
		@Override  
		public void onPageSelected(int arg0) {  
			for (int i = 0; i < imageViews.length; i++) {  
				imageViews[arg0].setBackgroundResource(R.mipmap.page_indicator_focused);
				if (arg0 != i) {  
					imageViews[i].setBackgroundResource(R.mipmap.page_indicator);
				}  
			}
		}  
	} 
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
