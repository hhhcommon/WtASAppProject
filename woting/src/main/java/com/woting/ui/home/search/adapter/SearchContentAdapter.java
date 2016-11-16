package com.woting.ui.home.search.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.home.search.model.SuperRankInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.BitmapUtils;

import java.util.List;

public class SearchContentAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<SuperRankInfo> mSuperRankInfo;

	public SearchContentAdapter(Context context,List<SuperRankInfo> mSuperRankInfo) {
		this.context = context;
		this.mSuperRankInfo = mSuperRankInfo;
	}

    public void setList(List<SuperRankInfo> mSuperRankInfo) {
        this.mSuperRankInfo = mSuperRankInfo;
        notifyDataSetChanged();
    }

	@Override
	public int getGroupCount() {
		return mSuperRankInfo.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mSuperRankInfo.get(groupPosition).getList().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mSuperRankInfo.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mSuperRankInfo.get(groupPosition).getList().get(childPosition);
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

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_fragment_radio_list, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.lin_more = (LinearLayout) convertView.findViewById(R.id.lin_head_more);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String key = mSuperRankInfo.get(groupPosition).getKey();
		if (key != null && !key.equals("")) {
			if (key.equals("AUDIO")) {
				holder.tv_name.setText("声音");
			} else if (key.equals("RADIO")) {
				holder.tv_name.setText("电台");
			} else if (key.equals("SEQU")) {
				holder.tv_name.setText("专辑");
			} else if (key.equals("TTS")){
				holder.tv_name.setText("TTS");
			}
		} else {
			holder.tv_name.setText("我听");
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,boolean isLastChild, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_rankinfo, null);
			holder = new ViewHolder();
			holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
			holder.textview_rankplaying = (TextView) convertView.findViewById(R.id.RankPlaying);// 正在播放的节目
			holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
			holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
			holder.lin_CurrentPlay = (LinearLayout) convertView.findViewById(R.id.lin_currentplay);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RankInfo lists = mSuperRankInfo.get(groupPosition).getList().get(childPosition);
		if (lists.getMediaType().equals("RADIO")) {
			if (lists.getContentName() == null|| lists.getContentName().equals("")) {
				holder.textview_ranktitle.setText("未知");
			} else {
				holder.textview_ranktitle.setText(lists.getContentName());
			}
			if (lists.getCurrentContent() == null|| lists.getCurrentContent().equals("")) {
				holder.textview_rankplaying.setText("未知");
			} else {
				holder.textview_rankplaying.setText(lists.getCurrentContent());
			}
			if (lists.getContentImg() == null|| lists.getContentImg().equals("")|| lists.getContentImg().equals("null")
					|| lists.getContentImg().trim().equals("")) {
				Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
				holder.imageview_rankimage.setImageBitmap(bmp);
			} else {
				String url1;
				if(lists.getContentImg().startsWith("http")){
					url1 =  lists.getContentImg();
				}else{
					url1 = GlobalConfig.imageurl + lists.getContentImg();
				}
				Picasso.with(context).load(url1.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
			}
		} else if(lists.getMediaType().equals("AUDIO")){
			if (lists.getContentName() == null|| lists.getContentName().equals("")) {
				holder.textview_ranktitle.setText("未知");
			} else {
				holder.textview_ranktitle.setText(lists.getContentName());
			}
			if (lists.getCurrentContent() == null|| lists.getCurrentContent().equals("")) {
				holder.textview_rankplaying.setText("未知");
			} else {
				holder.textview_rankplaying.setText(lists.getCurrentContent());
			}
			if (lists.getContentImg() == null|| lists.getContentImg().equals("")|| lists.getContentImg().equals("null")
					|| lists.getContentImg().trim().equals("")) {
				Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
				holder.imageview_rankimage.setImageBitmap(bmp);
			} else {
				String url1;
				if(lists.getContentImg().startsWith("http")){
					url1 =  lists.getContentImg();
				}else{
					url1 = GlobalConfig.imageurl + lists.getContentImg();
				}
				Picasso.with(context).load(url1.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
			}
		}else if(lists.getMediaType().equals("SEQU")){// 判断mediatype==sequ的情况
			if (lists.getContentName() == null|| lists.getContentName().equals("")) {
				holder.textview_ranktitle.setText("未知");
			} else {
				holder.textview_ranktitle.setText(lists.getContentName());
			}
			if (lists.getContentImg() == null|| lists.getContentImg().equals("")|| lists.getContentImg().equals("null")
					|| lists.getContentImg().trim().equals("")) {
				Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
				holder.imageview_rankimage.setImageBitmap(bmp);
			} else {
				String url;
				if(lists.getContentImg().startsWith("http")){
					url=  lists.getContentImg();
				}else{
					url= GlobalConfig.imageurl + lists.getContentImg();
				}
				Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
			}
			holder.lin_CurrentPlay.setVisibility(View.INVISIBLE);
		}else if(lists.getMediaType().equals("TTS")){
			if (lists.getContentName() == null|| lists.getContentName().equals("")) {
				holder.textview_ranktitle.setText("未知");
			} else {
				holder.textview_ranktitle.setText(lists.getContentName());
			}
			if (lists.getContentImg() == null|| lists.getContentImg().equals("")|| lists.getContentImg().equals("null")
					|| lists.getContentImg().trim().equals("")) {
				Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
				holder.imageview_rankimage.setImageBitmap(bmp);
			} else {
				String url;
				if(lists.getContentImg().startsWith("http")){
					url=  lists.getContentImg();
				}else{
					url= GlobalConfig.imageurl + lists.getContentImg();
				}
				Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
			}
			holder.lin_CurrentPlay.setVisibility(View.INVISIBLE);
		}
		if (lists.getWatchPlayerNum() == null|| lists.getWatchPlayerNum().equals("")|| lists.getWatchPlayerNum().equals("null")) {
			holder.mTv_number.setText("8000");
		} else {
			holder.mTv_number.setText(lists.getWatchPlayerNum());
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	class ViewHolder {
		public ImageView imageview_rankimage;
		public TextView textview_rankplayingnumber;
		public TextView textview_rankplaying;
		public TextView textview_ranktitle;
		public TextView tv_name;
		public LinearLayout lin_more;
		public TextView mTv_number;
		public LinearLayout lin_CurrentPlay;
	}
}
