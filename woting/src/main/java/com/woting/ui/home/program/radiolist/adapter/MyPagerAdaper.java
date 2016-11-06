package com.woting.ui.home.program.radiolist.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

public class MyPagerAdaper extends FragmentStatePagerAdapter{
	private List<String> title;
	private List<Fragment> fragments;

	public MyPagerAdaper(FragmentManager fm, List<String> title, List<Fragment> fragments) {
		super(fm);
		this.title = title;
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return title.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return title.get(position);
	}
	
	@Override
	public int getItemPosition(Object object) {
		return PagerAdapter.POSITION_NONE;
	}
}
