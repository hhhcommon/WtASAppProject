package com.woting.activity.interphone.notify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.interphone.linkman.model.DBNotifyHistorary;
import com.woting.common.config.GlobalConfig;
import com.woting.helper.ImageLoader;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class NotifyNewsAdapter extends BaseAdapter {
	private List<DBNotifyHistorary> list;
	private Context context;
	private ImageLoader imageLoader;
	private DBNotifyHistorary lists;
	private SimpleDateFormat format;
	public NotifyNewsAdapter(Context context,List<DBNotifyHistorary> list) {
		super();
		this.list = list;
		this.context = context;
		imageLoader=new ImageLoader(context);
		format = new SimpleDateFormat("yy-MM-dd HH:mm");
	}

	public void ChangeDate(List<DBNotifyHistorary> list){
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
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_notifynews, null);
			holder.time = (TextView)convertView.findViewById(R.id.time);
			holder.tile = (TextView)convertView.findViewById(R.id.title);
			holder.content = (TextView)convertView.findViewById(R.id.content);
			holder.Image = (ImageView)convertView.findViewById(R.id.Image);
			convertView.setTag(holder); 
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		lists = list.get(position);
		if(lists!=null){
			if(lists.getTitle() == null || lists.getTitle().equals("")){
				holder.tile.setText("新信息");
			}else{
				holder.tile.setText(lists.getTitle());
			}
			if(lists.getContent() == null || lists.getContent().equals("")){
				holder.content.setText("未知消息");
			}else{
				holder.content.setText(lists.getContent());
			}
			if(lists.getDealTime() == null || lists.getDealTime().equals("")|| lists.getDealTime().equals("null")){
				holder.time.setText( format.format(new Date(System.currentTimeMillis())));
			}else{
				holder.time.setText( format.format(new Date(Long.parseLong(lists.getDealTime()))));
			}
			if(lists.getImageUrl() == null || lists.getImageUrl().equals("") 
					|| lists.getImageUrl().equals("null") || lists.getImageUrl().trim().equals("")){
				holder.Image.setImageResource(R.mipmap.wt_linkman_news);
			}else{
				String url = GlobalConfig.imageurl+lists.getImageUrl();
				imageLoader.DisplayImage(url.replace( "\\/", "/"), holder.Image, false, false,null, null);
			}
		}
		return convertView;
	}

	class ViewHolder{
		public ImageView Image;
		public TextView content;
		public TextView tile;
		public TextView time;
	}
}
