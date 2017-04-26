package com.woting.ui.musicplay.accuse.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.musicplay.accuse.model.Accuse;

import java.util.List;

public class AccuseAdapter extends BaseAdapter {
	private List<Accuse> list;
	private Context context;
	private Accuse lists;

	public AccuseAdapter(Context context, List<Accuse> list) {
		super();
		this.list = list;
		this.context = context;
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
		ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_accuse, parent,false);
			holder.tv_name=(TextView)convertView.findViewById(R.id.tv_name);
			holder.imageView_check=(ImageView)convertView.findViewById(R.id.img_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		lists= list.get(position);
		if(!TextUtils.isEmpty(lists.getCatalogName())){
			holder.tv_name.setText(lists.getCatalogName());

			if (lists.getCheckType() == 0) {
				holder.imageView_check.setVisibility(View.INVISIBLE);
			} else {
				holder.imageView_check.setVisibility(View.VISIBLE);
			}

		}
		return convertView;
	}

	class ViewHolder {
		public TextView tv_name;
		public ImageView imageView_check;
	}


}
