package com.woting.ui.interphone.linkman.adapter;

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
import com.woting.common.config.GlobalConfig;
import com.woting.ui.common.model.GroupInfo;

import java.util.List;

/**
 * 群组适配器
 * @author 辛龙
 *2016年3月25日
 */
public class TalkGroupAdapter extends BaseAdapter{
	private List<GroupInfo> list;
	private Context context;
	private OnListener onListener;
	private GroupInfo lists;
	private String url;
	public TalkGroupAdapter(Context context,List<GroupInfo> list) {
		super();
		this.list = list;
		this.context = context;
	}
	public void ChangeDate(List<GroupInfo> list){
		this.list = list;
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
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_talk_person, null);
			holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);//名
			holder.tv_b_name = (TextView)convertView.findViewById(R.id.tv_b_name);//名
			holder.imageView_touxiang=(ImageView)convertView.findViewById(R.id.image);
			holder.lin_add=(LinearLayout)convertView.findViewById(R.id.lin_add);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		lists=list.get(position);
		if(lists.getGroupName()==null||lists.getGroupName().equals("")){
			holder.tv_name.setText("未知");//名
		}else{
			holder.tv_name.setText(lists.getGroupName());//名
		}
		if(lists.getGroupMyAlias()==null||lists.getGroupMyAlias().equals("")){
			holder.tv_b_name.setVisibility(View.GONE);
		}else{
			holder.tv_b_name.setVisibility(View.VISIBLE);
			holder.tv_b_name.setText(lists.getGroupMyAlias());//名
		}
		if(lists.getGroupImg()==null||lists.getGroupImg().equals("")||lists.getGroupImg().equals("null")||lists.getGroupImg().trim().equals("")){
			holder.imageView_touxiang.setImageResource(R.mipmap.wt_image_tx_qz);
		}else{
			if(lists.getGroupImg().startsWith("http:")){
				url=lists.getGroupImg();
			}else{
				url = GlobalConfig.imageurl+lists.getGroupImg();
			}
			Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageView_touxiang);

		}

		holder.lin_add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onListener.add(position);
			}
		});	
		return convertView;
	}

	public interface OnListener {
		public void add(int position);
	}

	class ViewHolder{
		public TextView tv_b_name;
		public LinearLayout lin_add;
		public ImageView imageView_touxiang;
		public TextView tv_name;

	}
}
