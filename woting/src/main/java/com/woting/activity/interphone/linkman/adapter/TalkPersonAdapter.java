package com.woting.activity.interphone.linkman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.activity.interphone.linkman.model.TalkPersonInside;
import com.woting.common.config.GlobalConfig;

import java.util.List;
/**
 * 通讯录好友适配器
 * @author 辛龙
 *2016年3月25日
 */
public class TalkPersonAdapter extends BaseAdapter{
	private List<TalkPersonInside> list;
	private Context context;
	private OnListeners onListeners;
	private TalkPersonInside lists;
	public TalkPersonAdapter(Context context,List<TalkPersonInside> list) {
		super();
		this.list = list;
		this.context = context;
	}
	public void ChangeDate(List<TalkPersonInside> list){
		this.list = list;
		this.notifyDataSetChanged();
	}
	public void setOnListeners(OnListeners onListener) {
		this.onListeners = onListener;
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
			holder.indexLayut=(LinearLayout)convertView.findViewById(R.id.index);
			holder.lin_add=(LinearLayout)convertView.findViewById(R.id.lin_add);
			holder.contactLayut=(LinearLayout)convertView.findViewById(R.id.contactLayut);
			holder.indexTv = (TextView) convertView.findViewById(R.id.indexTv); 
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		lists=list.get(position);

		if (lists.getTruename().length() == 1) {  
			holder.indexLayut.setVisibility(View.VISIBLE);
			holder.contactLayut.setVisibility(View.GONE);
			holder.indexTv.setText(list.get(position).getTruename());  
		} else {  
			holder.indexLayut.setVisibility(View.GONE);
			holder.contactLayut.setVisibility(View.VISIBLE);
			if(lists.getUserName()==null||lists.getUserName().equals("")){
				holder.tv_name.setText("未知");//名
			}else{
				holder.tv_name.setText(lists.getUserName());//名
			}
			if(lists.getUserAliasName()==null||lists.getUserAliasName().equals("")){
				holder.tv_b_name.setVisibility(View.GONE);
			}else{
				holder.tv_b_name.setVisibility(View.VISIBLE);
				holder.tv_b_name.setText(lists.getUserAliasName());//名
			}
			if(lists.getPortraitMini()==null||lists.getPortraitMini().equals("")||lists.getPortraitMini().equals("null")||lists.getPortraitMini().trim().equals("")){
				holder.imageView_touxiang.setImageResource(R.mipmap.wt_image_tx_hy);
			}else{
				String url = GlobalConfig.imageurl+lists.getPortraitMini();
				Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageView_touxiang);
			}
		}		
		holder.lin_add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onListeners.add(position);
			}
		});	
		return convertView;
	}

	public interface OnListeners {
		public void add(int position);
	}
	
	class ViewHolder{
		public TextView tv_b_name;
		public LinearLayout lin_add;
		public LinearLayout contactLayut;
		public TextView indexTv;
		public LinearLayout indexLayut;
		public ImageView imageView_touxiang;
		public TextView tv_name;
	}
}
