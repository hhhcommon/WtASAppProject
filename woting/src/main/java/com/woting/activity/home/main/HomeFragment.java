package com.woting.activity.home.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.home.player.main.fragment.PlayerFragment;
import com.woting.activity.home.program.main.ProgramFragment;
import com.woting.common.adapter.MyFragmentChildPagerAdapter;

import java.util.ArrayList;

/**
 * 内容主页
 * 
 * @author 辛龙
 * 
 * 2016年2月2日
 */
public class HomeFragment extends Fragment {
	private static FragmentActivity context;
	private View rootView;
	private TextView view1;
	private TextView view2;
	private ViewPager mPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_wt_home, container, false);
		context = this.getActivity();
		InitTextView();
		InitViewPager();
		return rootView;
	}

	private void InitTextView() {
		view1 = (TextView) rootView.findViewById(R.id.tv_guid1);
		view2 = (TextView) rootView.findViewById(R.id.tv_guid2);
		view1.setOnClickListener(new txListener(0));
		view2.setOnClickListener(new txListener(1));
	}

	public class txListener implements View.OnClickListener {
		private int index = 0;
		public txListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
			if (index == 0) {
				view1.setTextColor(context.getResources().getColor(R.color.WHITE));
				view2.setTextColor(context.getResources().getColor(R.color.orangered));
				view1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_orange));
				view2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home));
			} else if (index == 1) {
				view1.setTextColor(context.getResources().getColor(R.color.orangered));
				view2.setTextColor(context.getResources().getColor(R.color.WHITE));
				view1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home));
				view2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_orange));
			}
		}
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager() {
		mPager = (ViewPager) rootView.findViewById(R.id.viewpager);
		mPager.setOffscreenPageLimit(1);
		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
		PlayerFragment playfragment = new PlayerFragment();
		ProgramFragment newsfragment = new ProgramFragment();
		fragmentList.add(playfragment);
		fragmentList.add(newsfragment);
		mPager.setAdapter(new MyFragmentChildPagerAdapter(getFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页mPager
	}
}
