package com.woting.activity.home.program.fmlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.home.program.fmlist.model.RankInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.helper.ImageLoader;

import java.util.List;

//这个代码写完了 然后要求对应着代码加载看看
public class RankInfoAdapter extends BaseAdapter   {
	private List<RankInfo> list;
	private Context context;
	private ImageLoader imageLoader;
//	private RankInfo rank;
//	private String url;

	public RankInfoAdapter(Context context, List<RankInfo> list) {
		super();
		this.list = list;
		this.context = context;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_rankinfo, null);
			holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
			holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
			holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
			holder.textview_rankplaying=(TextView)convertView.findViewById(R.id.RankPlaying);
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
		if(lists.getContentPub()== null|| lists.getContentPub().equals("")){
			holder.textview_rankplaying.setText("未知");
		}else{
			holder.textview_rankplaying.setText(lists.getContentPub());
		}
		if (lists.getContentImg() == null || lists.getContentImg().equals("")
				|| lists.getContentImg().equals("null") || lists.getContentImg().trim().equals("")) {
			holder.imageview_rankimage.setImageResource(R.mipmap.wt_image_playertx);
		} else {
			String url;
			if(lists.getContentImg().startsWith("http")){
				 url =  lists.getContentImg();
			}else{
				 url = GlobalConfig.imageurl + lists.getContentImg();
			}
			imageLoader.DisplayImage(url.replace("\\/", "/"),holder.imageview_rankimage, false, false, null, null);
		}
		if (lists.getWatchPlayerNum() == null
				|| lists.getWatchPlayerNum().equals("") || lists.getWatchPlayerNum().equals("null")) {
			holder.mTv_number.setText("8000");
		} else {
			holder.mTv_number.setText(lists.getWatchPlayerNum());
		}
		return convertView;
	}

	class ViewHolder {
		public ImageView imageview_rankimage;
		public TextView textview_ranktitle;
		public TextView mTv_number;
		public TextView textview_rankplaying;
	}
}
