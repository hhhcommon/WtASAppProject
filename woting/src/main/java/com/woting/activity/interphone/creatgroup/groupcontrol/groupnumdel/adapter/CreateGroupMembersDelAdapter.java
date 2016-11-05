package com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel.model.UserInfo;
import com.woting.common.config.GlobalConfig;

import java.util.List;

public class CreateGroupMembersDelAdapter extends BaseAdapter implements SectionIndexer{
	private List<UserInfo> list;
	private Context context;
	private UserInfo lists;
	private String url;
	private friendCheck friendcheck;

	public CreateGroupMembersDelAdapter(Context context, List<UserInfo> list) {
		super();
		this.list = list;
		this.context = context;
	}

	public void ChangeDate(List<UserInfo> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}

	public void setOnListener(friendCheck friendcheck) {
		this.friendcheck = friendcheck;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_group_membersadd, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);// 名
			holder.imageView_touxiang = (ImageView) convertView.findViewById(R.id.image);
			holder.imageView_check = (ImageView) convertView.findViewById(R.id.img_check);
			/*holder.lin_onclick = (LinearLayout) convertView.findViewById(R.id.lin_check);*/
			holder.indexLayut=(LinearLayout)convertView.findViewById(R.id.index);
			holder.contactLayut=(LinearLayout)convertView.findViewById(R.id.contactLayut);
			holder.indexTv = (TextView) convertView.findViewById(R.id.indexTv); 
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		lists=list.get(position);
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			holder.indexLayut.setVisibility(View.VISIBLE);
			holder.indexTv.setText(list.get(position).getSortLetters());  
		} else {
			holder.indexLayut.setVisibility(View.GONE);
		}
		if (lists.getUserName() == null || lists.getUserName().equals("")) {
			holder.tv_name.setText("未知");// 名
		} else {
			holder.tv_name.setText(lists.getUserName());// 名
		}
		if (lists.getPortraitMini() == null
				|| lists.getPortraitMini().equals("")
				|| lists.getPortraitMini().equals("null")
				|| lists.getPortraitMini().trim().equals("")) {
			holder.imageView_touxiang.setImageResource(R.mipmap.wt_image_tx_hy);
		} else {
			if(lists.getPortraitMini().startsWith("http:")){
				url=lists.getPortraitMini();
			}else{
				url = GlobalConfig.imageurl+lists.getPortraitMini();
			}
			Picasso.with(context).load(url.replace("\\/", "/")).into(holder.imageView_touxiang);
		}
		if (lists.getCheckType() == 2) {
			holder.imageView_check.setImageResource(R.mipmap.image_all_check);
		} else {
			holder.imageView_check.setImageResource(R.mipmap.image_not_all_check);
		}
		holder.imageView_check.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				friendcheck.checkposition(position);
			}
		});


		return convertView;
	}

	public interface friendCheck {
        void checkposition(int position);
	}

	class ViewHolder {
		public TextView indexTv;
		public LinearLayout indexLayut;
		public LinearLayout contactLayut;
		public ImageView imageView_touxiang;
		public TextView tv_name;
/*		public LinearLayout lin_onclick;*/
		public ImageView imageView_check;
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}
	@Override
	public Object[] getSections() {
		return null;
	}
}
