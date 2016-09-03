package com.woting.activity.download.downloadlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.download.model.FileInfo;
import com.woting.helper.ImageLoader;

import java.util.List;

public class DownLoadListAdapter extends BaseAdapter {
	private List<FileInfo> list;
	private Context context;
	private ImageLoader imageLoader;
	private downloadlist downloadlist;

	public DownLoadListAdapter(Context context, List<FileInfo> list) {
		this.context = context;
		this.list = list;
		imageLoader = new ImageLoader(context);
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

	public void setonListener(downloadlist downloadlist) {
		this.downloadlist = downloadlist;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_downloadlist, null);
			holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
			holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
			holder.tv_RankContent = (TextView) convertView.findViewById(R.id.RankContent);
			holder.lin_delete = (LinearLayout) convertView.findViewById(R.id.lin_clear);
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
				|| lists.getImageurl().equals("null")
				|| lists.getImageurl().trim().equals("")) {
			if (lists.getSequimgurl() == null
					|| lists.getSequimgurl().equals("")
					|| lists.getSequimgurl().equals("null")
					|| lists.getSequimgurl().trim().equals("")) {
				holder.imageview_rankimage
						.setImageResource(R.mipmap.wt_bg_noimage);
			}else{
				String url = /* GlobalConfig.imageurl + */lists.getSequimgurl();
				imageLoader.DisplayImage(url.replace("\\/", "/"),
						holder.imageview_rankimage, false, false, null, null);
			}
		} else {
			String url = /* GlobalConfig.imageurl + */lists.getImageurl();
			imageLoader.DisplayImage(url.replace("\\/", "/"),
					holder.imageview_rankimage, false, false, null, null);
		}
		if (lists.getAuthor() == null || lists.getAuthor().equals("")) {
			holder.tv_RankContent.setText("我听科技");
		} else {
			holder.tv_RankContent.setText(lists.getAuthor());
//			holder.tv_RankContent.setVisibility(View.GONE);
		}

		holder.lin_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				downloadlist.checkposition(position);
			}
		});
		return convertView;
	}

	public interface downloadlist {
		public void checkposition(int position);
	}

	private class ViewHolder {
		public ImageView imageview_rankimage;
		public TextView textview_ranktitle;
		public TextView tv_RankContent;
		public LinearLayout lin_delete;
	}
}
