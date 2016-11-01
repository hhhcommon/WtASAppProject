package com.woting.activity.interphone.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.activity.interphone.chat.model.TalkListGP;
import com.woting.common.config.GlobalConfig;
import com.woting.util.TimeUtils;

import java.util.List;

public class ChatListAdapter extends BaseAdapter {
	private Context context;
	private OnListener onListener;
	private String url;
	private String id;
	private List<TalkListGP> list;
	private TalkListGP lists;

	public ChatListAdapter(Context context, List<TalkListGP> alllist, String ids) {
		super();
		this.list = alllist;
		this.id = ids;
		this.context = context;
	}

	public void ChangeDate(List<TalkListGP> list, String ids) {
		this.list = list;
		this.id = ids;
		this.notifyDataSetChanged();
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_talk_oldlist, null);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.textView_bg = (TextView) convertView.findViewById(R.id.textView_bg);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.lin_zhiding = (LinearLayout) convertView.findViewById(R.id.lin_zhiding);
			holder.imageView_touxiang = (ImageView) convertView.findViewById(R.id.image);
			holder.textGroupNumber = (TextView) convertView.findViewById(R.id.tv_group_number);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		lists = list.get(position);
		if (lists.getId().trim().equals(id)) {
			holder.textView_bg.setVisibility(View.VISIBLE);
		} else {
			holder.textView_bg.setVisibility(View.GONE);
		}
		if (lists.getGroupSignature() == null || lists.getGroupSignature().trim().equals("")) {
			holder.tv_content.setVisibility(View.GONE);
		} else {
			holder.tv_content.setVisibility(View.VISIBLE);
			holder.tv_content.setText(lists.getGroupSignature());
		}

		if (lists.getName() == null || lists.getName().equals("")) {
			holder.tv_name.setText("未知");
		} else {
			if (lists.getGroupCount() == null || lists.getGroupCount().equals("")) {
				holder.tv_name.setText(lists.getName());
				holder.textGroupNumber.setVisibility(View.GONE);
			} else {
				holder.textGroupNumber.setVisibility(View.VISIBLE);
				holder.tv_name.setText(lists.getName());
				holder.textGroupNumber.setText(" " + "(" + lists.getGroupCount() + "人)");
			}
		}
		if (lists.getAddTime() == null || lists.getAddTime().equals("")) {
			holder.tv_time.setText("未知");
		} else {
			holder.tv_time.setText(TimeUtils.converTime(Long.parseLong(lists.getAddTime())));
		}
		if (lists.getPortrait() == null || lists.getPortrait().equals("null") || lists.getPortrait().trim().equals("")) {
			if (lists.getTyPe().equals("user")) {
				holder.imageView_touxiang.setImageResource(R.mipmap.wt_image_tx_hy);
			} else {
				holder.imageView_touxiang.setImageResource(R.mipmap.wt_image_tx_qz);
			}
		} else {
			url = GlobalConfig.imageurl + lists.getPortrait();
			Picasso.with(context).load(url.replace("\\/", "/")).into(holder.imageView_touxiang);
		}
		holder.lin_zhiding.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onListener.zhiding(position);
			}
		});
		
		return convertView;
	}

	public interface OnListener {
		public void zhiding(int position);
	}

	class ViewHolder {
		public TextView tv_content;
		public TextView tv_time;
		public TextView textView_bg;
		public LinearLayout lin_zhiding;
		public ImageView imageView_touxiang;
		public TextView tv_name;
		public TextView textGroupNumber;
	}
}
