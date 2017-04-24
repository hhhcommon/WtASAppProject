package com.woting.ui.musicplay.programme.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

public class MyPagerAdapter extends FragmentStatePagerAdapter{
	private List<Fragment> fragments;
	private List<String> title;
	public MyPagerAdapter(FragmentManager fm,   List<String>  arr,List<Fragment> fragments) {
		super(fm);
		this.title = arr;
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
