package com.woting.ui.home.program.diantai.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.ui.home.program.diantai.model.RadioPlay;
import com.woting.ui.home.program.fmlist.activity.FMListActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.ToastUtils;

import java.util.List;

/**
 * expandableListView适配器
 */
public class onlineAdapter extends BaseExpandableListAdapter  {
	private Context context;
	private List<RadioPlay> group;

	public onlineAdapter(Context context, List<RadioPlay> group) {
		this.context = context;
		this.group = group;
 	}

	@Override
	public int getGroupCount() {
		return group.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return group.get(groupPosition).getList().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return group.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return group.get(groupPosition).getList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	/**
	 * 显示：group
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_fragment_radio_list, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.lin_more = (LinearLayout) convertView.findViewById(R.id.lin_head_more);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final RadioPlay lists = group.get(groupPosition);
		if (lists.getCatalogName() == null || lists.getCatalogName().equals("")) {
			holder.tv_name.setText("未知");
		} else {
			holder.tv_name.setText(lists.getCatalogName());
		}

		// 判断回调对象决定是哪个fragment的对象调用的词adapter 从而实现多种布局
		holder.lin_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, FMListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("Position", "GROUP");
				bundle.putSerializable("list", lists);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	/**
	 * 显示：child
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_rankinfo, null);
			holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
			holder.textview_rankplaying = (TextView) convertView.findViewById(R.id.RankPlaying);// 正在播放的节目
			holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
			holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
			//			holder.mImg_play = (ImageView) convertView.findViewById(R.id.img_play);
			holder.lin_CurrentPlay = (LinearLayout) convertView.findViewById(R.id.lin_currentplay);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RankInfo lists = group.get(groupPosition).getList().get(childPosition);
		if(lists!=null){
			if(lists.getMediaType()!=null&&!lists.getMediaType().equals("")){
				if (lists.getMediaType().equals("RADIO")) {
					if (lists.getContentName() == null|| lists.getContentName().equals("")) {
						holder.textview_ranktitle.setText("未知");
					} else {
						holder.textview_ranktitle.setText(lists.getContentName());
					}
					if (lists.getContentPub() == null|| lists.getContentPub().equals("")) {
						holder.textview_rankplaying.setText("未知");
					} else {
						holder.textview_rankplaying.setText(lists.getContentPub());
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
						Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
					}
				} else {// 判断mediatype==AUDIO的情况
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
						Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
					}
					holder.lin_CurrentPlay.setVisibility(View.INVISIBLE);
				}
			}else{
				ToastUtils.show_allways(context, "服务器返回数据MediaType为空");
			}
			if (lists.getWatchPlayerNum() == null
					|| lists.getWatchPlayerNum().equals("")
					|| lists.getWatchPlayerNum().equals("null")) {
				holder.mTv_number.setText("8000");
			} else {
				holder.mTv_number.setText(lists.getWatchPlayerNum());
			}
		}
		//		if (lists.getType() == 1) {
		//			holder.mImg_play.setImageResource(R.drawable.album_play);
		//		} else {
		//			holder.mImg_play.setImageResource(R.drawable.album_pause);
		//		}
		//		holder.mImg_play.setOnClickListener(this);
		//		holder.mImg_play.setTag(R.id.tag_groupname, groupPosition);
		//		holder.mImg_play.setTag(R.id.tag_childname, childPosition);
		return convertView;

	}

	// 定义的接口
	public interface CallBackOnline {
		public void click(View v);
	}


	class ViewHolder {
		public ImageView imageview_rankimage;
		//		public ImageView mImg_play;
		public TextView textview_rankplayingnumber;
		public TextView textview_rankplaying;
		public TextView textview_ranktitle;
		public TextView tv_name;
		public LinearLayout lin_more;
		public TextView mTv_number;
		public LinearLayout lin_CurrentPlay;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
