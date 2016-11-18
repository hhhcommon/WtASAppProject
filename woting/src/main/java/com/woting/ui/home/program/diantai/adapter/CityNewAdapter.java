package com.woting.ui.home.program.diantai.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.ToastUtils;
import com.woting.ui.home.program.fmlist.model.RankInfo;

import java.util.List;

public class CityNewAdapter extends BaseAdapter {
	private List<RankInfo> list;
	private Context context;

	public CityNewAdapter(Context context, List<RankInfo> list) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_rankinfo, null);
			holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
			holder.textview_rankplaying = (TextView) convertView.findViewById(R.id.RankPlaying);// 正在播放的节目
			holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
			holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
			holder.lin_CurrentPlay = (LinearLayout) convertView.findViewById(R.id.lin_currentplay);
			holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
			Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
			holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RankInfo lists = list.get(position);
		if(lists.getMediaType()!=null&&!lists.getMediaType().equals("")){
			if (lists.getMediaType().equals("RADIO")) {
				if (lists.getContentName() == null|| lists.getContentName().equals("")) {
					holder.textview_ranktitle.setText("未知");
				} else {
					holder.textview_ranktitle.setText(lists.getContentName());
				}

//				if (lists.getContentPub() == null|| lists.getContentPub().equals("")) {
//					holder.textview_rankplaying.setText("未知");
//				} else {
//					holder.textview_rankplaying.setText(lists.getContentPub());
//				}

				holder.textview_rankplaying.setText("测试-无节目单数据");

				if (lists.getContentImg() == null
						|| lists.getContentImg().equals("")
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
					url=AssembleImageUrlUtils.assembleImageUrl150(url);
					Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
				}
			} else {
				// 判断mediatype==AUDIO的情况
				if (lists.getContentName() == null|| lists.getContentName().equals("")) {
					holder.textview_ranktitle.setText("未知");
				} else {
					holder.textview_ranktitle.setText(lists.getContentName());
				}
				if (lists.getContentImg() == null
						|| lists.getContentImg().equals("")
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
					url=AssembleImageUrlUtils.assembleImageUrl150(url);
					Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
				}
				holder.lin_CurrentPlay.setVisibility(View.INVISIBLE);
			}
		}else{
			ToastUtils.show_allways(context, "服务器返回数据MediaType为空");
		}
		if (lists.getPlayCount() == null
				|| lists.getPlayCount().equals("")
				|| lists.getPlayCount().equals("null")) {
			holder.mTv_number.setText("8000");
		} else {
			holder.mTv_number.setText(lists.getPlayCount());
		}
		return convertView;
	}

	private class ViewHolder {
		public TextView textview_ranktitle,mTv_number,textview_rankplaying;
		public ImageView imageview_rankimage;
		public LinearLayout lin_CurrentPlay;
		public ImageView img_zhezhao;
	}
}
