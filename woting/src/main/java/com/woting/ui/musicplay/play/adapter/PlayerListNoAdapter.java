package com.woting.ui.musicplay.play.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PlayerListNoAdapter extends BaseAdapter{
	Context context;
	public PlayerListNoAdapter(Context context){
		this.context=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
}
