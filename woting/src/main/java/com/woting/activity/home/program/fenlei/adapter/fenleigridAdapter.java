package com.woting.activity.home.program.fenlei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.home.program.fenlei.model.fenleiname;

import java.util.List;

public class fenleigridAdapter extends BaseAdapter {
	private List<fenleiname> list;
	private Context context;
	private ViewHolder holder;

	public fenleigridAdapter(Context context, List<fenleiname> list) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_fenlei_child_grid, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);// 台名
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_name.setText(list.get(position).getCatalogName());
		return convertView;
	}

	class ViewHolder {
		public TextView tv_name;
	}
}
