package com.woting.activity.download.activity;//package com.wotingfm.activity.download.activity;
//
//import java.util.ArrayList;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.woting.R;
//import com.wotingfm.activity.download.fragment.DownLoadCompleted;
//import com.wotingfm.activity.download.fragment.DownLoadUnCompleted;
//import com.wotingfm.adapter.MyFragmentPagerAdapter;
//import com.wotingfm.main.commonactivity.BaseFragment;
//
//
//public class DownloadFragment extends BaseFragment {
//	private View rootView;
//	private TextView tv_completed;
//	private TextView tv_uncompleted;
//	private FragmentActivity context;
//	private ArrayList<Fragment> fragmentList;
//	private Boolean lin_flag = false;// 防止连点点击
//	private ViewPager vp_download;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		context = this.getActivity();
//		rootView = inflater.inflate(R.layout.fragment_download, container,false);
//		tv_completed = (TextView) rootView.findViewById(R.id.tv_completed);
//		tv_uncompleted = (TextView) rootView.findViewById(R.id.tv_uncompleted);
//		vp_download = (ViewPager)rootView.findViewById(R.id.viewpager);
//		initViewPager();
//		return rootView;
//	}
//
//	private void initViewPager() {
//		vp_download.setOffscreenPageLimit(0);
//		fragmentList = new ArrayList<Fragment>();
//		Fragment mDownLoadFragment = new DownLoadCompleted();
//		Fragment mDownLoadUnFragment = new DownLoadUnCompleted();
//		fragmentList.add(mDownLoadFragment);
//		fragmentList.add(mDownLoadUnFragment);
////		vp_download.setAdapter(new MyFragmentChildPagerAdapter(
////				getChildFragmentManager(), fragmentList));
//		vp_download.setAdapter(new MyFragmentPagerAdapter(
//				getFragmentManager(), fragmentList));
//		vp_download.setOnPageChangeListener(new MyOnPageChangeListener());
//		vp_download.setCurrentItem(0);
//		tv_completed.setOnClickListener(new DownloadClickListener(0));
//		tv_uncompleted.setOnClickListener(new DownloadClickListener(1));
//
//	}
//
//	public class MyOnPageChangeListener implements OnPageChangeListener {
//		private int currIndex;
//
//		@Override
//		public void onPageScrolled(int arg0, float arg1, int arg2) {
//		}
//
//		@Override
//		public void onPageScrollStateChanged(int arg0) {
//		}
//
//		@Override
//		public void onPageSelected(int arg0) {
//			// TODO Auto-generated method stub
//			currIndex = arg0;
//			if (currIndex == 0) {
//				if (lin_flag == true) {
//					lin_flag = false;
//					tv_completed.setTextColor(context.getResources().getColor(
//							R.color.WHITE));
//					tv_completed
//							.setBackgroundResource(R.drawable.color_wt_circle_home_orange);
//					tv_uncompleted.setTextColor(context.getResources()
//							.getColor(R.color.download_unchecked));
//					tv_uncompleted
//							.setBackgroundResource(R.drawable.color_wt_circle_home);
//				}
//				} else if (currIndex == 1) {
//					if (lin_flag == false) {
//						lin_flag = true;
//						tv_uncompleted.setTextColor(context.getResources()
//								.getColor(R.color.WHITE));
//						tv_uncompleted
//								.setBackgroundResource(R.drawable.color_wt_circle_home_orange);
//						tv_completed.setTextColor(context.getResources()
//								.getColor(R.color.download_unchecked));
//						tv_completed
//								.setBackgroundResource(R.drawable.color_wt_circle_home);
//					}
//				}
//				// ToastUtil.show_short(SearchLikeAcitvity.this, "I am "+arg0);
//			}
//	}
//
//	public class DownloadClickListener implements OnClickListener {
//		private int index = 0;
//
//		public DownloadClickListener(int i) {
//			index = i;
//		}
//
//		@Override
//		public void onClick(View v) {
//			// 界面切换字体的改变
//			if (index == 0) {
//				if (lin_flag == true) {
//					vp_download.setCurrentItem(index);
//					lin_flag = false;
//					tv_completed.setTextColor(context.getResources().getColor(
//							R.color.WHITE));
//					tv_completed
//							.setBackgroundResource(R.drawable.color_wt_circle_home_orange);
//					tv_uncompleted.setTextColor(context.getResources()
//							.getColor(R.color.download_unchecked));
//					tv_uncompleted
//							.setBackgroundResource(R.drawable.color_wt_circle_home);
//				}
//			} else if (index == 1) {
//				if (lin_flag == false) {
//					vp_download.setCurrentItem(index);
//					lin_flag = true;
//					tv_uncompleted.setTextColor(context.getResources()
//							.getColor(R.color.WHITE));
//					tv_uncompleted
//							.setBackgroundResource(R.drawable.color_wt_circle_home_orange);
//					tv_completed.setTextColor(context.getResources().getColor(
//							R.color.download_unchecked));
//					tv_completed
//							.setBackgroundResource(R.drawable.color_wt_circle_home);
//				}
//			}
//		}
//	}
//}