package com.woting.ui.home.program.diantai.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.home.program.fmlist.model.RankInfo;

import java.util.List;

public class CityNewAdapter extends BaseAdapter {
	private List<RankInfo> list;
	private Context context;

	public CityNewAdapter(Context context, List<RankInfo> list) {
		this.context = context;
		this.list = list;
	}
	public void changeData( List<RankInfo> list) {
		this.list = list;
		notifyDataSetChanged();
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
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_rankinfo, null);
			holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
			holder.textview_rankplaying = (TextView) convertView.findViewById(R.id.RankPlaying);// 正在播放的节目
			holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
			holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
			holder.lin_CurrentPlay = (LinearLayout) convertView.findViewById(R.id.lin_currentplay);
			holder.image_last = (ImageView) convertView.findViewById(R.id.image_last);//
			holder.image_num = (ImageView) convertView.findViewById(R.id.image_num);//
			holder.tv_last = (TextView) convertView.findViewById(R.id.tv_last);
			holder.image_last.setVisibility(View.GONE);
			holder.image_num.setVisibility(View.GONE);
			holder.tv_last.setVisibility(View.GONE);

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

				if(!TextUtils.isEmpty(lists.getIsPlaying())){
					holder.textview_rankplaying.setText(lists.getIsPlaying());
				}else{
					holder.textview_rankplaying.setText("暂无节目单");
				}

				if (lists.getContentImg() == null
						|| lists.getContentImg().equals("")
						|| lists.getContentImg().equals("null")
						|| lists.getContentImg().trim().equals("")) {
					Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx_d);
					holder.imageview_rankimage.setImageBitmap(bmp);
				} else {
					String url;
					if(lists.getContentImg().startsWith("http")){
						url =  lists.getContentImg();
					}else{
						url = GlobalConfig.imageurl + lists.getContentImg();
					}
                    String _url = AssembleImageUrlUtils.assembleImageUrl180(url);

                    // 加载图片
                    AssembleImageUrlUtils.loadImage(_url, url, holder.imageview_rankimage, IntegerConstant.TYPE_LIST);
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

                    final String _url = AssembleImageUrlUtils.assembleImageUrl180(url);
                    final String c_url = url;

                    // 加载图片
                    AssembleImageUrlUtils.loadImage(_url, c_url, holder.imageview_rankimage, IntegerConstant.TYPE_LIST);
				}
				holder.lin_CurrentPlay.setVisibility(View.INVISIBLE);
			}
		}
		if (lists.getPlayCount() == null
				|| lists.getPlayCount().equals("")
				|| lists.getPlayCount().equals("null")) {
			holder.mTv_number.setText("0");
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
		public ImageView image_num;
		public TextView tv_last;
		public ImageView image_last;
	}
}
