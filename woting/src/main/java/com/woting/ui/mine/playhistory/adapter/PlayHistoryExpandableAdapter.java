package com.woting.ui.mine.playhistory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.search.model.SuperRankInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class PlayHistoryExpandableAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<SuperRankInfo> mSuperRankInfo;
	private SimpleDateFormat format;
	private Object a;
	private String url;
	private PlayHistoryCheck playCheck;

	public PlayHistoryExpandableAdapter(Context context,List<SuperRankInfo> mSuperRankInfo) {
		this.context = context;
		this.mSuperRankInfo = mSuperRankInfo;
	}
	
	public void setOnClick(PlayHistoryCheck playCheck) {
		this.playCheck = playCheck;
	}
	
	public void changeDate(List<SuperRankInfo> list) {
		this.mSuperRankInfo = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		return mSuperRankInfo.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mSuperRankInfo.get(groupPosition).getHistoryList().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mSuperRankInfo.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mSuperRankInfo.get(groupPosition).getHistoryList().get(childPosition);
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
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_play_history, null);
			holder.textView_playName = (TextView) convertView.findViewById(R.id.RankTitle);// 节目名称
			holder.textView_PlayIntroduce = (TextView) convertView.findViewById(R.id.tv_last);// 上次播放时长
			holder.imageView_playImage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 节目图片
			holder.textNumber = (TextView) convertView.findViewById(R.id.text_number);
			holder.textRankContent = (TextView) convertView.findViewById(R.id.RankContent);
			//holder.lin_clear = (LinearLayout) convertView.findViewById(R.id.lin_clear);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PlayerHistory lists = mSuperRankInfo.get(groupPosition).getHistoryList().get(childPosition);
		if (lists.getPlayerMediaType().equals("RADIO")) {
			if (lists.getPlayerName() == null || lists.getPlayerName().equals("")) {
				holder.textView_playName.setText("未知");
			} else {
				holder.textView_playName.setText(lists.getPlayerName());
			}
			if (lists.getPlayerNum() == null || lists.getPlayerNum().equals("")) {
				holder.textNumber.setText("8888");
			} else {
				holder.textNumber.setText(lists.getPlayerNum());
			}
			if (lists.getContentPub() == null || lists.getContentPub().equals("")) {
				holder.textRankContent.setText("我听科技");
			} else {
				holder.textRankContent.setText(lists.getContentPub());
			}
			if (lists.getPlayerInTime() == null | lists.getPlayerInTime().equals("")) {
				holder.textView_PlayIntroduce.setText("未知");
			} else {
				format = new SimpleDateFormat("mm:ss");
				format.setTimeZone(TimeZone.getTimeZone("GMT"));
				a = Integer.valueOf(lists.getPlayerInTime());
				String s = format.format(a);
				holder.textView_PlayIntroduce.setText("上次播放至" + s);
			}
			if (lists.getPlayerImage() == null || lists.getPlayerImage().equals("") 
					|| lists.getPlayerImage().equals("null") || lists.getPlayerImage().trim().equals("")) {
				holder.imageView_playImage.setImageResource(R.mipmap.wt_image_playertx);
			} else {
				url = lists.getPlayerImage();
				Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageView_playImage);
			}
		} else if(lists.getPlayerMediaType().equals("AUDIO")){
			if (lists.getPlayerName() == null || lists.getPlayerName().equals("")) {
				holder.textView_playName.setText("未知");
			} else {
				holder.textView_playName.setText(lists.getPlayerName());
			}
			if (lists.getPlayerNum() == null || lists.getPlayerNum().equals("")) {
				holder.textNumber.setText("8888");
			} else {
				holder.textNumber.setText(lists.getPlayerNum());
			}
			if (lists.getPlayerFrom() == null || lists.getPlayerFrom().equals("")) {
				holder.textRankContent.setText("我听科技");
			} else {
				holder.textRankContent.setText(lists.getPlayerFrom());
			}
			if (lists.getPlayerInTime() == null | lists.getPlayerInTime().equals("")) {
				holder.textView_PlayIntroduce.setText("未知");
			} else {
				format = new SimpleDateFormat("mm:ss");
				format.setTimeZone(TimeZone.getTimeZone("GMT"));
				a = Integer.valueOf(lists.getPlayerInTime());
				String s = format.format(a);
				holder.textView_PlayIntroduce.setText("上次播放至" + s);
			}
			if (lists.getPlayerImage() == null || lists.getPlayerImage().equals("") 
					|| lists.getPlayerImage().equals("null") || lists.getPlayerImage().trim().equals("")) {
				holder.imageView_playImage.setImageResource(R.mipmap.wt_image_playertx);
			} else {
				url = lists.getPlayerImage();
				Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageView_playImage);
			}
			
		}/*else if(lists.getPlayerMediaType().equals("SEQU")){// 判断mediatype==sequ的情况
			if (lists.getPlayerName() == null || lists.getPlayerName().equals("")) {
				holder.textView_playName.setText("未知");
			} else {
				holder.textView_playName.setText(lists.getPlayerName());
			}
			if (lists.getPlayerNum() == null || lists.getPlayerNum().equals("")) {
				holder.textNumber.setText("8888");
			} else {
				holder.textNumber.setText(lists.getPlayerNum());
			}
			if (lists.getContentPub() == null || lists.getContentPub().equals("")) {
				holder.textRankContent.setText("我听科技");
			} else {
				holder.textRankContent.setText(lists.getContentPub());
			}
			if (lists.getPlayerInTime() == null | lists.getPlayerInTime().equals("")) {
				holder.textView_PlayIntroduce.setText("未知");
			} else {
				format = new SimpleDateFormat("mm:ss");
				format.setTimeZone(TimeZone.getTimeZone("GMT"));
				a = Integer.valueOf(lists.getPlayerInTime());
				String s = format.format(a);
				holder.textView_PlayIntroduce.setText("上次播放至" + s);
			}
			if (lists.getPlayerImage() == null || lists.getPlayerImage().equals("") 
					|| lists.getPlayerImage().equals("null") || lists.getPlayerImage().trim().equals("")) {
				holder.imageView_playImage.setImageResource(R.drawable.wt_image_playertx_80);
			} else {
				// GlobalConfig.imageurl+ 
				url = lists.getPlayerImage();
				imageLoader.DisplayImage(url.replace("\\/", "/"), holder.imageView_playImage, false, false, null);
			}
		}*/else if(lists.getPlayerMediaType().equals("TTS")){
			if (lists.getPlayerName() == null || lists.getPlayerName().equals("")) {
				holder.textView_playName.setText("未知");
			} else {
				holder.textView_playName.setText(lists.getPlayerName());
			}
			if (lists.getPlayerNum() == null || lists.getPlayerNum().equals("")) {
				holder.textNumber.setText("8888");
			} else {
				holder.textNumber.setText(lists.getPlayerNum());
			}
			if (lists.getContentPub() == null || lists.getContentPub().equals("")) {
				holder.textRankContent.setText("我听科技");
			} else {
				holder.textRankContent.setText(lists.getContentPub());
			}
			if (lists.getPlayerInTime() == null | lists.getPlayerInTime().equals("")) {
				holder.textView_PlayIntroduce.setText("未知");
			} else {
				format = new SimpleDateFormat("mm:ss");
				format.setTimeZone(TimeZone.getTimeZone("GMT"));
				a = Integer.valueOf(lists.getPlayerInTime());
				String s = format.format(a);
				holder.textView_PlayIntroduce.setText("上次播放至" + s);
			}
			if (lists.getPlayerImage() == null || lists.getPlayerImage().equals("") 
					|| lists.getPlayerImage().equals("null") || lists.getPlayerImage().trim().equals("")) {
				holder.imageView_playImage.setImageResource(R.mipmap.wt_image_playertx);
			} else {
				url = lists.getPlayerImage();
				Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageView_playImage);
			}
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public interface PlayHistoryCheck {
		void checkPosition(int position);
	}

	class ViewHolder {
		private TextView tv_name;
		public LinearLayout lin_more;
		public TextView textView_playName;
		public TextView textView_PlayIntroduce;
		public ImageView imageView_playImage;
		public TextView textNumber;
		public TextView textRankContent;
		//public LinearLayout lin_clear;
	}
}
