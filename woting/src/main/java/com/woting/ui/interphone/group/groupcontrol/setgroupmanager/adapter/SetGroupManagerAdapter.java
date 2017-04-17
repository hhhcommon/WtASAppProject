package com.woting.ui.interphone.group.groupcontrol.setgroupmanager.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.woting.ui.common.model.UserInfo;

import java.util.List;

public class SetGroupManagerAdapter extends BaseAdapter{
	private List<UserInfo> list;
	private Context context;
	private UserInfo lists;

	private friendCheck friendcheck;

	public SetGroupManagerAdapter(Context context, List<UserInfo> list) {
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
		ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_group_membersadd, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);// 名
			holder.imageView_touxiang = (ImageView) convertView.findViewById(R.id.image);
			holder.imageView_check = (ImageView) convertView.findViewById(R.id.img_check);
			holder.contactLayut=(LinearLayout)convertView.findViewById(R.id.contactLayut);
			holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
			Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
			holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		lists=list.get(position);

			
		if (lists.getNickName() == null || lists.getNickName().equals("")) {
			holder.tv_name.setText("未知");// 名
		} else {
			holder.tv_name.setText(lists.getNickName());// 名
		}
		if (lists.getPortraitMini() == null
				|| lists.getPortraitMini().equals("")
				|| lists.getPortraitMini().equals("null")
				|| lists.getPortraitMini().trim().equals("")) {
			holder.imageView_touxiang.setImageResource(R.mipmap.wt_image_tx_hy);
		} else {
			String url;
			if(lists.getPortraitMini().startsWith("http:")){
				url=lists.getPortraitMini();
			}else{
				url = GlobalConfig.imageurl+lists.getPortraitMini();
			}
            String _url = AssembleImageUrlUtils.assembleImageUrl150(url);

            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, url, holder.imageView_touxiang, IntegerConstant.TYPE_PERSON);
		}
		if(lists.getViewType()==1){
			holder.imageView_check.setVisibility(View.GONE);
		}else{
			holder.imageView_check.setVisibility(View.VISIBLE);
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
		}
		return convertView;
	}

	public interface friendCheck {
		public void checkposition(int position);
	}

	class ViewHolder {
		public TextView indexTv;
		public LinearLayout contactLayut;
		public LinearLayout indexLayut;
		public ImageView imageView_touxiang;
		public TextView tv_name;
		public ImageView imageView_check;
		public ImageView img_zhezhao;
	}
	

}
