package com.woting.ui.musicplay.play.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.model.share.ShareModel;

import java.util.List;

public class ImageAdapter extends BaseAdapter{
	private List<ShareModel> list;
	private Context context;
	private ShareModel lists;

	public ImageAdapter (Context context, List<ShareModel> list) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_shareitem, null);
			holder.tv_Name = (TextView) convertView.findViewById(R.id.tv_sharetext);// 名
			holder.img_share = (ImageView) convertView.findViewById(R.id.img_shareimg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		lists = list.get(position);
		holder.img_share.setImageResource(lists.getShareImageUrl());
		if(lists.getShareText()!=null&&!lists.getShareText().equals("")){
			holder.tv_Name.setText(lists.getShareText());
		}
		return convertView;
	}

	class ViewHolder {
		public ImageView img_share;
		public TextView tv_Name;
	}
}
