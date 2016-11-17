package com.woting.ui.mine.playhistory.adapter;

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

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.home.player.main.model.PlayerHistory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class PlayHistoryAdapter extends BaseAdapter {
	private List<PlayerHistory> list;
	private Context context;
	private PlayerHistory lists;
	private String url;
	private SimpleDateFormat format;
	private Object a;
	private playhistorycheck playcheck;

	public PlayHistoryAdapter(Context context, List<PlayerHistory> list) {
		super();
		this.list = list;
		this.context = context;
	}

	public void ChangeDate(List<PlayerHistory> list) {
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

	public void setonclick(playhistorycheck playcheck) {
		this.playcheck = playcheck;
	};

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_play_history, null);
			holder.textView_playName = (TextView) convertView.findViewById(R.id.RankTitle);			// 节目名称
			holder.textView_PlayIntroduce = (TextView) convertView.findViewById(R.id.tv_last);
			holder.imageView_playImage = (ImageView) convertView.findViewById(R.id.RankImageUrl);	// 节目图片
			holder.imageCheck = (LinearLayout) convertView.findViewById(R.id.lin_check);			//是否选中  清除
			holder.layoutCheck = (LinearLayout) convertView.findViewById(R.id.layout_check);
			holder.check = (ImageView) convertView.findViewById(R.id.img_check);
			holder.textNumber = (TextView) convertView.findViewById(R.id.text_number);
			holder.textRankContent = (TextView) convertView.findViewById(R.id.RankContent);
			holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
			Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
			holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
			//holder.lin_clear = (LinearLayout) convertView.findViewById(R.id.lin_clear);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		lists = list.get(position);
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
		if (lists.getPlayerInTime() == null || lists.getPlayerInTime().equals("")) {
			holder.textView_PlayIntroduce.setText("未知");
		} else {
			format = new SimpleDateFormat("mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("GMT"));
			a = Integer.valueOf(lists.getPlayerInTime());
			holder.textView_PlayIntroduce.setText("上次播放至" + format.format(a));
		}
		if (lists.getPlayerImage() == null || lists.getPlayerImage().equals("") 
				|| lists.getPlayerImage().equals("null") || lists.getPlayerImage().trim().equals("")) {
			holder.imageView_playImage.setImageResource(R.mipmap.wt_image_playertx);
		} else {
			url = lists.getPlayerImage();
			Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageView_playImage);
		}
		if(lists.isCheck()){
			holder.imageCheck.setVisibility(View.VISIBLE);
			if(lists.getStatus() == 0){
				holder.check.setImageResource(R.mipmap.wt_group_nochecked);	//未点击状态
			}else if(lists.getStatus() == 1){
				holder.check.setImageResource(R.mipmap.wt_group_checked);		//点击状态
			}
		}else{
			holder.imageCheck.setVisibility(View.GONE);
		}
		holder.imageCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playcheck.checkposition(position);
			}
		});
		return convertView;
	}

	public interface playhistorycheck {
		void checkposition(int position);
	}

	class ViewHolder {
		public TextView textView_playName;
		public TextView textView_PlayIntroduce;
		public ImageView imageView_playImage;
		private ImageView check;
		public LinearLayout imageCheck;
		public LinearLayout layoutCheck;
		public TextView textNumber;
		public TextView textRankContent;
		public ImageView img_zhezhao;
	}
}
