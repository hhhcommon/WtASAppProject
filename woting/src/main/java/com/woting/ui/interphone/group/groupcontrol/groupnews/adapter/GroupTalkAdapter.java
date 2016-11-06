package com.woting.ui.interphone.group.groupcontrol.groupnews.adapter;

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
import com.woting.ui.interphone.group.groupcontrol.groupnews.model.GroupTalkInside;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.BitmapUtils;

import java.util.List;

public class GroupTalkAdapter extends BaseAdapter{
	private List<GroupTalkInside> list;
	private Context context;
	private GroupTalkInside lists;
	private String url;
	
	public GroupTalkAdapter(Context context,List<GroupTalkInside> list) {
		super();
		this.list = list;
		this.context = context;
	}
	
	public void ChangeDate(List<GroupTalkInside> list){
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
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_grouptalk, null);
			holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);//名
			holder.imageView_touxiang = (ImageView)convertView.findViewById(R.id.image);
			holder.headFrame = (ImageView) convertView.findViewById(R.id.head_frame);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		lists = list.get(position);
		if(lists.getType() == 1){
			holder.tv_name.setVisibility(View.VISIBLE);
			if(lists.getUserAliasName()!=null){
				holder.tv_name.setText(lists.getUserAliasName());
			}else{
				if(lists.getUserName()==null||lists.getUserName().equals("")){
					holder.tv_name.setText("未知");//名
				}else{
					holder.tv_name.setText(lists.getUserName());//名
				}}
			if(lists.getPortraitBig()==null||lists.getPortraitBig().equals("")||lists.getPortraitBig().equals("null")||lists.getPortraitBig().trim().equals("")){
				holder.imageView_touxiang.setImageResource(R.mipmap.wt_image_tx_hy);
			}else{
				if(lists.getPortraitMini().startsWith("http:")){
					url=lists.getPortraitMini();
				}else{
					url = GlobalConfig.imageurl+lists.getPortraitMini();
				}
				Picasso.with(context).load(url.replace("\\/", "/")).into(holder.imageView_touxiang);
			}
		}else if(lists.getType() == 2){
			holder.tv_name.setText("添加");
			holder.headFrame.setVisibility(View.GONE);		// 08/04  头像的六边形边框  添加和剔除图标本身是六边形图标  所以隐藏其六边形边框
			Bitmap bp=BitmapUtils.readBitMap(context, R.mipmap.image_add);
	/*		holder.imageView_touxiang.setImageResource(R.drawable.image_add);*/
			holder.imageView_touxiang.setImageBitmap(bp);
		}else{
			holder.tv_name.setText("删除");
			holder.headFrame.setVisibility(View.GONE);		// 08/04  头像的六边形边框  添加和剔除图标本身是六边形图标  所以隐藏其六边形边框
			Bitmap bp=BitmapUtils.readBitMap(context, R.mipmap.image_tichu);
		/*	holder.imageView_touxiang.setImageResource(R.drawable.image_tichu);*/
			holder.imageView_touxiang.setImageBitmap(bp);
		}
		//		holder.lin_shanchu.setOnClickListener(new View.OnClickListener() {
		//
		//			public void onClick(View v) {
		//				onListener.shanchu(position);
		//			}
		//		});	
		//		
		return convertView;
	}

	//	public interface OnListener {
	//		public void shanchu(int position);
	//	}
	class ViewHolder{
		public ImageView imageView;
		public ImageView imageView_touxiang;
		public TextView tv_name;
		public ImageView headFrame;
	}
}
