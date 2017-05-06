package com.woting.ui.musicplay.album.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.music.model.content;

import java.util.List;

public class AlbumAdapter extends BaseAdapter {
	private List<content> list;
	private Context context;
	private content lists;

	public AlbumAdapter(Context context, List<content> list) {
		this.list = list;
		this.context = context;
	}

	public void ChangeDate(List<content> list) {
		this.list = list;
		this.notifyDataSetChanged();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_album_download, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_playname);// 名
			holder.imageView_check = (ImageView) convertView.findViewById(R.id.img_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		lists = list.get(position);
		if (lists.getContentName() == null || lists.getContentName().equals("")) {
			holder.tv_name.setText("未知");// 名
		} else {
			holder.tv_name.setText(lists.getContentName());// 名
		}
		if (lists.getChecktype() == 3) {
			holder.imageView_check.setImageResource(R.mipmap.wt_group_checkedpress);
		}else if (lists.getChecktype() == 2) {
			holder.imageView_check.setImageResource(R.mipmap.wt_group_checked);
		}  else {
			holder.imageView_check.setImageResource(R.mipmap.wt_group_nochecked);
		}
		return convertView;
	}

	class ViewHolder {
		public TextView tv_name;
		public ImageView imageView_check;
	}
}
