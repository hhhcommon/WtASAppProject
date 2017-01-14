package com.woting.ui.home.program.fmlist.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.home.program.fmlist.model.RankInfo;

import java.util.List;

//这个代码写完了 然后要求对应着代码加载看看
public class RankInfoAdapter extends BaseAdapter   {
	private List<RankInfo> list;
	private Context context;
//	private RankInfo rank;
//	private String url;

	public RankInfoAdapter(Context context, List<RankInfo> list) {
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
		ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_rankinfo, null);
			holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
			holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
			holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
			holder.textview_rankplaying=(TextView)convertView.findViewById(R.id.RankPlaying);

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

		if (lists.getContentName() == null || lists.getContentName().equals("")) {
			holder.textview_ranktitle.setText("未知");
		} else {
			holder.textview_ranktitle.setText(lists.getContentName());
		}

//		if(lists.getContentPub()== null|| lists.getContentPub().equals("")){
//			holder.textview_rankplaying.setText("未知");
//		}else{
//			holder.textview_rankplaying.setText(lists.getContentPub());
//		}

		if(!TextUtils.isEmpty(lists.getIsPlaying())){
			holder.textview_rankplaying.setText("正在播放"+""+lists.getIsPlaying());
		}else {
			holder.textview_rankplaying.setText("暂无节目单");
		}
		if (lists.getContentImg() == null || lists.getContentImg().equals("")
				|| lists.getContentImg().equals("null") || lists.getContentImg().trim().equals("")) {
			Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx_d);
			holder.imageview_rankimage.setImageBitmap(bmp);
		} else {
			String url;
			if(lists.getContentImg().startsWith("http")){
				 url =  lists.getContentImg();
			}else{
				 url = GlobalConfig.imageurl + lists.getContentImg();
			}
			url= AssembleImageUrlUtils.assembleImageUrl180(url);
			Picasso.with(context).load(url.replace("\\/", "/")).into(holder.imageview_rankimage);
		}
		if (lists.getPlayCount() == null
				|| lists.getPlayCount().equals("") || lists.getPlayCount().equals("null")) {
			holder.mTv_number.setText("0");
		} else {
			holder.mTv_number.setText(lists.getPlayCount());
		}
		return convertView;
	}

	class ViewHolder {
		public ImageView imageview_rankimage;
		public TextView textview_ranktitle;
		public TextView mTv_number,tv_last;
		public TextView textview_rankplaying;
		public ImageView img_zhezhao;
		public ImageView image_last;
		public ImageView image_num;
	}
}
