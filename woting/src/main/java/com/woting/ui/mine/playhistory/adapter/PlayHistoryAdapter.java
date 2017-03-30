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

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.home.player.main.model.PlayerHistory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class PlayHistoryAdapter extends BaseAdapter {
	private List<PlayerHistory> list;
	private Context context;
	private PlayerHistory lists;
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
            holder.imageLast = (ImageView) convertView.findViewById(R.id.image_last);
            holder.imageNum = (ImageView) convertView.findViewById(R.id.image_num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		lists = list.get(position);

        String mediaType = list.get(position).getPlayerMediaType();
        if(mediaType != null) {
            if(mediaType.equals("RADIO") || mediaType.equals("TTS")) {
                holder.imageLast.setVisibility(View.GONE);
                holder.textView_PlayIntroduce.setVisibility(View.GONE);
            } else {
                holder.imageLast.setVisibility(View.VISIBLE);
                holder.textView_PlayIntroduce.setVisibility(View.VISIBLE);
            }
        }

		if (lists.getPlayerName() == null || lists.getPlayerName().equals("")) {
			holder.textView_playName.setText("未知");
		} else {
			holder.textView_playName.setText(lists.getPlayerName());
		}
		if (lists.getPlayerNum() == null || lists.getPlayerNum().equals("")) {
			holder.textNumber.setText("0");
		} else {
			holder.textNumber.setText(lists.getPlayerNum());
		}
		if (lists.getIsPlaying() == null || lists.getIsPlaying().equals("")) {
			holder.textRankContent.setText("暂无信息");
		} else {
			holder.textRankContent.setText("上次收听的节目:  "+lists.getIsPlaying());
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
			Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
			holder.imageView_playImage.setImageBitmap(bmp);
		} else {
			String url;
			if (lists.getPlayerImage().startsWith("http")) {
				url = lists.getPlayerImage();
			} else {
				url = GlobalConfig.imageurl + lists.getPlayerImage();
			}
            String _url = AssembleImageUrlUtils.assembleImageUrl180(url);

            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, url, holder.imageView_playImage, IntegerConstant.TYPE_LIST);
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
        public ImageView imageLast;
        public ImageView imageNum;
	}
}
