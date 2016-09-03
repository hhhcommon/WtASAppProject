package com.woting.activity.download.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.woting.R;
import com.woting.activity.download.fragment.DownLoadCompleted;
import com.woting.activity.download.fragment.DownLoadUnCompleted;
import com.woting.activity.home.search.activity.SearchLikeAcitvity;
import com.woting.common.adapter.MyFragmentPagerAdapter;
import com.woting.util.ToastUtils;

import java.util.ArrayList;

/**
 * 下载主页
 * @author 辛龙
 * 2016年4月1日
 */
public class DownloadActivity extends FragmentActivity implements OnClickListener {
	private TextView tv_completed;
	private TextView tv_uncompleted;
	private ViewPager vp_download;
	private DownloadActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_download);
		context = this;
		setview();
		initViewPager();
	}

	/**
	 * 设置界面
	 */
	private void setview() {
		tv_completed = (TextView) findViewById(R.id.tv_completed);
		tv_uncompleted = (TextView) findViewById(R.id.tv_uncompleted);
		vp_download = (ViewPager)findViewById(R.id.viewpager);
		LinearLayout lin_news = (LinearLayout) findViewById(R.id.lin_news);
		lin_news.setOnClickListener(this);
		LinearLayout lin_find = (LinearLayout) findViewById(R.id.lin_find);
		lin_find.setOnClickListener(this);
	}

	private void initViewPager() {
		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
		Fragment mDownLoadFragment = new DownLoadCompleted();
		Fragment mDownLoadUnFragment = new DownLoadUnCompleted();
		fragmentList.add(mDownLoadFragment);
		fragmentList.add(mDownLoadUnFragment);
		// vp_download.setAdapter(new MyFragmentChildPagerAdapter(getChildFragmentManager(), fragmentList));
		vp_download.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
		vp_download.setOnPageChangeListener(new MyOnPageChangeListener());
		vp_download.setCurrentItem(0); 
		vp_download.setOffscreenPageLimit(1);
		tv_completed.setOnClickListener(new DownloadClickListener(0));
		tv_uncompleted.setOnClickListener(new DownloadClickListener(1));
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				tv_completed.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
				tv_completed.setBackgroundResource(R.drawable.color_wt_circle_home_white);
				tv_uncompleted.setTextColor(context.getResources().getColor(R.color.white));
				tv_uncompleted.setBackgroundResource(R.drawable.color_wt_circle_orange);
			} else if (arg0 == 1) {
				tv_uncompleted.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
				tv_uncompleted.setBackgroundResource(R.drawable.color_wt_circle_home_white);
				tv_completed.setTextColor(context.getResources().getColor(R.color.white));
				tv_completed.setBackgroundResource(R.drawable.color_wt_circle_orange);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_news:			// 跳转到新消息界面
			// startActivity(new Intent(context, HandleMessageActivity.class));
			break;
		case R.id.lin_find:			// 跳转到搜索界面
			Intent intent = new Intent(context, SearchLikeAcitvity.class);
			startActivity(intent);
			break;
		}
	}

	public class DownloadClickListener implements OnClickListener {
		private int index = 0;
		public DownloadClickListener(int i) {
			index = i;
		}
		
		@Override
		public void onClick(View v) {
			vp_download.setCurrentItem(index);		// 界面切换字体的改变
			if (index == 0) {
				tv_completed.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
				tv_completed.setBackgroundResource(R.drawable.color_wt_circle_home_white);
				tv_uncompleted.setTextColor(context.getResources().getColor(R.color.white));
				tv_uncompleted.setBackgroundResource(R.drawable.color_wt_circle_orange);
			} else if (index == 1) {
				tv_uncompleted.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
				tv_uncompleted	.setBackgroundResource(R.drawable.color_wt_circle_home_white);
				tv_completed.setTextColor(context.getResources().getColor(R.color.white));
				tv_completed.setBackgroundResource(R.drawable.color_wt_circle_orange);
			}
		}
	}
	
	long waitTime = 2000L;
	long touchTime = 0;
//	private String userid;
//	private List<FileInfo> filoinfolist;
	
	/**
	 * 手机实体返回按键的处理  与onbackpress同理
	 * @param keyCode
	 * @param event
	 * @return
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				ToastUtils.show_allways(DownloadActivity.this, "再按一次退出");
				touchTime = currentTime;
			} else {
//				//当程序结束时将所有符合条件的数据全部设置为待下载状态
//			    FileInfoDao FID=new FileInfoDao(context);
//			    userid=Utils.getUserId(context);
//				filoinfolist = FID.queryFileinfo("false",userid);// 查询表中未完成的任务
//				for (int i = 0; i < filoinfolist.size(); i++) {
//					FID.updatedownloadstatus(filoinfolist.get(i).getUrl(), "0");
//				}
				MobclickAgent.onKillProcess(this);
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();		
		tv_completed = null;
		tv_uncompleted = null;
		vp_download = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
