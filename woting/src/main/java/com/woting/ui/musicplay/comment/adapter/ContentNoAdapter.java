package com.woting.ui.musicplay.comment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.woting.R;


public class ContentNoAdapter extends BaseAdapter {
	private Context context;

	public ContentNoAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_nocontent, null);
		return convertView;
	}


}
