package com.woting.ui.home.program.album.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.home.program.album.model.ContentInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AlbumMainAdapter extends BaseAdapter {
	private List<ContentInfo> list;
	private Context context;
	private ContentInfo lists;
	private SimpleDateFormat format;

	public AlbumMainAdapter(Context context, List<ContentInfo> subList) {
		super();
		this.list = subList;
		this.context = context;
		 format = new SimpleDateFormat("yyyy-MM-dd");
	}

	public void ChangeDate(List<ContentInfo> list) {
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_album_main, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_playnum = (TextView) convertView.findViewById(R.id.tv_playnum);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.textTime = (TextView) convertView.findViewById(R.id.text_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		lists = list.get(position);
		if (lists.getContentName() == null || lists.getContentName().equals("")) {
			holder.tv_name.setText("未知");
		} else {
			holder.tv_name.setText(lists.getContentName());
		}
		if (lists.getPlayCount() == null || lists.getPlayCount().equals("")) {
			holder.tv_playnum.setText("000");
		} else {
			holder.tv_playnum.setText(lists.getPlayCount());
		}
		if (lists.getCTime() == null || lists.getCTime().equals("")) {
			holder.tv_time.setText("0000-00-00");
		} else {
			holder.tv_time.setText(format.format(new Date(Long.parseLong(lists.getCTime()))));
		}
		
		//节目时长
		if (lists.getContentTimes() == null
				|| lists.getContentTimes().equals("")
				|| lists.getContentTimes().equals("null")) {
			holder.textTime.setText(context.getString(R.string.play_time));
		} else {
			int minute = Integer.valueOf(lists.getContentTimes()) / (1000 * 60);
			int second = (Integer.valueOf(lists.getContentTimes()) / 1000) % 60;
			if(second < 10){
				holder.textTime.setText(minute + "\'" + " " + "0" + second + "\"");
			}else{
				holder.textTime.setText(minute + "\'" + " " + second + "\"");
			}
		}
		return convertView;
	}

	class ViewHolder {
		public TextView tv_time;
		public TextView tv_playnum;
		public ImageView imageView_touxiang;
		public TextView tv_name;
		public LinearLayout lin_onclick;
		public ImageView imageView_check;
		public TextView textTime;
	}
}
