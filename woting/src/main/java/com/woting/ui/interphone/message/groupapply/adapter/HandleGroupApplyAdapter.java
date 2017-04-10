package com.woting.ui.interphone.message.groupapply.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.common.model.UserInfo;
import com.woting.ui.interphone.message.reviewednews.model.CheckInfo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class HandleGroupApplyAdapter extends BaseAdapter {
	private List<UserInfo> list;
	private Context context;
	private SimpleDateFormat format;
	protected OnListener onListener;

	public HandleGroupApplyAdapter(Context context, List<UserInfo> list) {
		super();
		this.list = list;
		this.context = context;
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}

	public void setOnListener(OnListener onListener) {
		this.onListener = onListener;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_messagenews, null);
			holder.Image = (ImageView) convertView.findViewById(R.id.image);
			holder.tv_news = (TextView) convertView.findViewById(R.id.tv_news);
			holder.tv_jieshao = (TextView) convertView.findViewById(R.id.tv_jieshao);
			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_res = (TextView) convertView.findViewById(R.id.tv_res);
			holder.tv_acc = (TextView) convertView.findViewById(R.id.tv_acc);
			holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
			Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
			holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserInfo lists = list.get(position);

		if (lists.getNickName() == null || lists.getNickName().equals("")) {
			holder.tv_news.setText("未知");
		} else {
			holder.tv_news.setText(lists.getNickName());
		}
		if (lists.getApplyTime() == null || lists.getApplyTime().equals("")) {
			holder.tv_jieshao.setText("申请进入该群");
		} else {
			String time = format.format(new Date(Long.parseLong(lists.getApplyTime())));

			holder.tv_jieshao.setText("于" + time + "申请进入该群");
		}
		if (lists.getPortraitMini() == null || lists.getPortraitMini().equals("")
				|| lists.getPortraitMini().equals("null") || lists.getPortraitMini().trim().equals("")) {
			Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_tx_qz);
			holder.Image.setImageBitmap(bmp);
		} else {
			String url;
			if (lists.getPortraitMini().startsWith("http:")) {
				url = lists.getPortraitMini();
			} else {
				url = GlobalConfig.imageurl + lists.getPortraitMini();
			}
			url = AssembleImageUrlUtils.assembleImageUrl150(url);
			Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.Image);
		}


		holder.tv_acc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onListener.tongyi(position);
			}
		});
		holder.tv_res.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onListener.jujue(position);
			}
		});
		return convertView;
	}

	class ViewHolder {
		public TextView tv_jieshao;
		public TextView tv_acc;
		public TextView tv_res;
		public TextView time;
		public TextView tv_news;
		public ImageView Image;
		public ImageView img_zhezhao;
	}

	public interface OnListener {
		public void tongyi(int position);

		public void jujue(int position);
	}
}
