package com.woting.ui.home.program.radiolist.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.common.config.GlobalConfig;

import java.util.List;

public class RadioListAdapter extends BaseAdapter  {
	private List<RankInfo> list;
	private Context context;
	private ViewHolder holder;

	public RadioListAdapter(Context context, List<RankInfo> list) {
		this.context = context;
		this.list = list;
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_item_radiolist, null);
			holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
			holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
			holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
			holder.textTime = (TextView) convertView.findViewById(R.id.tv_time);
			holder.textRankPlaying = (TextView) convertView.findViewById(R.id.RankPlaying);
			holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
			Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
			holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RankInfo lists = list.get(position);
		if (lists.getContentName() == null || lists.getContentName().equals("")) {
			holder.textview_ranktitle.setText("未知");
		} else {
			holder.textview_ranktitle.setText(lists.getContentName());
		}
		if (lists.getContentImg() == null || lists.getContentImg().equals("")
				|| lists.getContentImg().equals("null")
				|| lists.getContentImg().trim().equals("")) {
			Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
			holder.imageview_rankimage.setImageBitmap(bmp);
		} else {
			String url;
			if(lists.getContentImg().startsWith("http")){
				 url =  lists.getContentImg();
			}else{
				 url = GlobalConfig.imageurl + lists.getContentImg();
			}
			url= AssembleImageUrlUtils.assembleImageUrl150(url);
			Picasso.with(context).load(url.replace("\\/", "/")).into(holder.imageview_rankimage);
		}
		if (lists.getPlayCount() == null
				|| lists.getPlayCount().equals("")
				|| lists.getPlayCount().equals("null")) {
			holder.mTv_number.setText("0");
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
		public ImageView img_zhezhao;
	}
}
