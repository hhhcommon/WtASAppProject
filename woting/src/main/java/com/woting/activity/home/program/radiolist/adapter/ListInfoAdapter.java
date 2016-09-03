package com.woting.activity.home.program.radiolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.home.program.radiolist.mode.ListInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.helper.ImageLoader;

import java.util.List;

public class ListInfoAdapter extends BaseAdapter  {
	private List<ListInfo> list;
	private Context context;
	private ImageLoader imageLoader;

	public ListInfoAdapter(Context context, List<ListInfo> list) {
		this.context = context;
		this.list = list;
		imageLoader = new ImageLoader(context);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_item_radiolist, null);
			holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
			holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
			holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
			holder.textTime = (TextView) convertView.findViewById(R.id.tv_time);
			holder.textRankPlaying = (TextView) convertView.findViewById(R.id.RankPlaying);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ListInfo lists = list.get(position);
		if (lists.getContentName() == null || lists.getContentName().equals("")) {
			holder.textview_ranktitle.setText("未知");
		} else {
			holder.textview_ranktitle.setText(lists.getContentName());
		}
		if (lists.getContentImg() == null || lists.getContentImg().equals("")
				|| lists.getContentImg().equals("null")
				|| lists.getContentImg().trim().equals("")) {

		} else {
			String url;
			if(lists.getContentImg().startsWith("http")){
				 url =  lists.getContentImg();
			}else{
				 url = GlobalConfig.imageurl + lists.getContentImg();
			}
			imageLoader.DisplayImage(url.replace("\\/", "/"),holder.imageview_rankimage, false, false, null, null);
		}
		if (lists.getPlayCount() == null
				|| lists.getPlayCount().equals("")
				|| lists.getPlayCount().equals("null")) {
			holder.mTv_number.setText("8000");
		} else {
			holder.mTv_number.setText(lists.getPlayCount());
		}

		if (lists.getContentPub() == null
				|| lists.getContentPub().equals("")
				|| lists.getContentPub().equals("null")) {
			holder.textRankPlaying.setText("未知");
		} else {
			holder.textRankPlaying.setText(lists.getContentPub());
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

    private class ViewHolder {
		public ImageView imageview_rankimage;
		public TextView textview_ranktitle;
		public TextView mTv_number;
		public TextView textTime;
		public TextView textRankPlaying;
    }
}
