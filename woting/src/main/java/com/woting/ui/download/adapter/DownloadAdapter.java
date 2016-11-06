package com.woting.ui.download.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.ui.download.model.FileInfo;
import com.woting.common.util.BitmapUtils;
import com.woting.common.widgetui.CircleProgress;

import java.text.DecimalFormat;
import java.util.List;

public class DownloadAdapter extends BaseAdapter {
	private List<FileInfo> list;
	private Context context;
	private DecimalFormat df;

	public DownloadAdapter(Context context, List<FileInfo> list) {
		this.context = context;
		this.list = list;
		df = new DecimalFormat("0.00");
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
	public View getView( int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_uncompelete, null);
			holder = new ViewHolder();
			holder.textview_ranktitle = (TextView)convertView.findViewById(R.id.RankTitle);		// 台名
			holder.imageview_rankimage = (ImageView)convertView.findViewById(R.id.img_touxiang);// 电台图标
//			holder.pro_bar = (ProgressBar) convertView.findViewById(R.id.pb_progress);
			holder.ncb=(CircleProgress)convertView.findViewById(R.id.roundBar2);
//			holder.ncb=(NumberCircleProgressBar)convertView.findViewById(R.id.numbercircleprogress_bar);
			holder.tv_start = (TextView)convertView.findViewById(R.id.download_start);
			holder.tv_end = (TextView)convertView.findViewById(R.id.download_end);
			holder.img_download_delete = (ImageView)convertView.findViewById(R.id.img_play);
			holder.tv_author = (TextView)convertView.findViewById(R.id.tv_author);
			holder.lin_board=(LinearLayout)convertView.findViewById(R.id.lin_downloadboard);
			holder.rv_download=(RelativeLayout)convertView.findViewById(R.id.rv_download);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FileInfo lists = list.get(position);
		if (lists.getFileName() == null || lists.getFileName().equals("")) {
			holder.textview_ranktitle.setText("未知");
		} else {
			holder.textview_ranktitle.setText(lists.getFileName());
		}
		if (lists.getImageurl() == null || lists.getImageurl().equals("")
				|| lists.getImageurl().equals("null") || lists.getImageurl().trim().equals("")) {
			Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_bg_noimage);
			holder.imageview_rankimage.setImageBitmap(bmp);
		} else {
			String url = lists.getImageurl();
			Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
		}

		if (lists.getAuthor() == null || lists.getAuthor().equals("")|| lists.getAuthor().equals("null")|| lists.getAuthor().trim().equals("")|| lists.getAuthor().trim().equals("author")) {
			holder.tv_author.setText("by 我听科技");
		} else {
			holder.tv_author.setText("by " + lists.getAuthor());
		}
		
		if (lists.getDownloadtype() == 0) {		// 未下载
			holder.img_download_delete.setImageResource(R.mipmap.wt_img_download_waiting);
			holder.lin_board.setVisibility(View.GONE);
			holder.rv_download.setVisibility(View.GONE);
			holder.img_download_delete.setVisibility(View.VISIBLE);
		}
//		else if (lists.getDownloadtype() == 1) {
//			//下载中
//			/*	holder.img_download_delete.setImageResource(R.drawable.wt_group_checked_new);*/
//			holder.img_download_delete.setVisibility(View.GONE);
//			holder.lin_board.setVisibility(View.VISIBLE);
//			holder.rv_download.setVisibility(View.VISIBLE);
//		} 
		else {									//暂停
			holder.img_download_delete.setVisibility(View.GONE);
			holder.lin_board.setVisibility(View.VISIBLE);
			holder.rv_download.setVisibility(View.VISIBLE);
			if (lists.getEnd() >= 0) {
//				holder.pro_bar.setMax(lists.getEnd());
				holder.tv_end.setText(df.format(lists.getEnd() / 1000.0 / 1000.0) + "MB");
			}else{
				holder.tv_end.setText(df.format(0/ 1000.0 / 1000.0) + "MB");
			}
			if (lists.getStart() >= 0) {
//				holder.pro_bar.setProgress(lists.getStart());
				float a = (float)lists.getStart();
//				Log.e("a", a+"");
				float b = (float)lists.getEnd();
//				Log.e("b", b+"");
				String c = df.format(a / b);
//				Log.e("c", c+"");
				int d = (int)(Float.parseFloat(c)*100);
				Log.e("d", d+"");
//				int progress = (lists.getStart()/lists.getEnd()*100.0);
//				Log.e("int",lists.getStart()+"*100/"+lists.getEnd()+"="+ progress+"");
				holder.ncb.setMainProgress(d);
				holder.tv_start.setText(df.format(lists.getStart() / 1000.0 / 1000.0)+ "MB/");
			}else{
				holder.ncb.setMainProgress(0);
				holder.tv_start.setText(df.format(0 / 1000.0 / 1000.0)+ "MB/");
			}
		}
		return convertView;
	}

	public void updateProgress(String url, int start, int end) {
		int id = 0;
		Log.e("测试下载功能", "list的大小"+list.size()+"");
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getUrl().trim().equals(url)) {
				id = i;
				Log.e("测试下载功能", "更新的单体名称"+list.get(i).getFileName()+"");
				break;
			}
		}
		if (list != null && list.size() != 0) {
			FileInfo fileInfo = list.get(id);
			fileInfo.setFinished(start / end);
			fileInfo.setStart(start);
			fileInfo.setEnd(end);
			notifyDataSetChanged();
		}
	}

	private class ViewHolder {
		public CircleProgress ncb;
//		public ProgressBar pro_bar;
		private ImageView imageview_rankimage;
		private TextView tv_author;
		private TextView textview_ranktitle;
		private TextView tv_start;
		private TextView tv_end;
//		private NumberCircleProgressBar ncb;
		private ImageView img_download_delete;
		private LinearLayout lin_board;
		private RelativeLayout rv_download;
	}
}
