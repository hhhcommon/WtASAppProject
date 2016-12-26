package com.woting.ui.home.program.comment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;

import java.io.IOException;
import java.util.List;

public class FaceGVAdapter extends BaseAdapter {
	private List<String> list;
	private Context mContext;

	public FaceGVAdapter(List<String> list, Context mContext) {
		this.list = list;
		this.mContext = mContext;
	}

	public void clear() {
		this.mContext = null;
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
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.face_image, parent, false);
			holder.iv = (ImageView) convertView.findViewById(R.id.face_img);
			holder.tv = (TextView) convertView.findViewById(R.id.face_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			Bitmap mBitmap = BitmapFactory.decodeStream(mContext.getAssets().open("face/png/" + list.get(position)));
			holder.iv.setImageBitmap(mBitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		holder.tv.setText("face/png/" + list.get(position));
		return convertView;
	}

	class ViewHolder {
		ImageView iv;
		TextView tv;
	}
}
