package com.woting.ui.interphone.find.findresult.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.ui.interphone.find.findresult.model.FindGroupNews;
import com.woting.common.config.GlobalConfig;

import java.util.List;

public class FindGroupResultAdapter extends BaseAdapter {
	private List<FindGroupNews> list;
	private Context context;
	private String url;

	public FindGroupResultAdapter(Context context, List<FindGroupNews> list) {
		super();
		this.list = list;
		this.context = context;
	}

	public void ChangeData(List<FindGroupNews> list) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_contactquery, null);
			holder = new ViewHolder();
			holder.textview_invitename = (TextView) convertView.findViewById(R.id.RankTitle);		// 人名
			holder.textview_invitemessage = (TextView) convertView.findViewById(R.id.RankContent);	// 介绍
			holder.imageview_inviteimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);	// 该人头像
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FindGroupNews Inviter = list.get(position);
		if (Inviter.getGroupName() == null || Inviter.getGroupName().equals("")) {
			 holder.textview_invitename.setText("未知"); 
		} else {
			holder.textview_invitename.setText(Inviter.getGroupName());
		}
		if (Inviter.getGroupOriDesc()== null || Inviter.getGroupOriDesc().equals("")) {
			 holder.textview_invitemessage.setVisibility(View.GONE);
		} else {
			holder.textview_invitemessage.setVisibility(View.VISIBLE);
		holder.textview_invitemessage.setText(Inviter.getGroupOriDesc());
		}
		if (Inviter.getGroupImg() == null || Inviter.getGroupImg().equals("")
				|| Inviter.getGroupImg().equals("null") || Inviter.getGroupImg().trim().equals("")) {
			holder.imageview_inviteimage.setImageResource(R.mipmap.wt_image_tx_qz);
		} else {
			if(Inviter.getGroupImg().startsWith("http:")){
				url = Inviter.getGroupImg();
			}else{
				url = GlobalConfig.imageurl+Inviter.getGroupImg();
			}
			Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_inviteimage);
		}
		return convertView;
	}

	class ViewHolder {
		public TextView textview_invitename;
		public TextView textview_invitemessage;
		public ImageView imageview_inviteimage;
	}
}
