package com.woting.ui.music.classify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.music.classify.model.FenLeiName;

import java.util.List;

public class CatalogGridAdapter extends BaseAdapter {
	private List<FenLeiName> list;
	private Context context;
	private ViewHolder holder;

	public CatalogGridAdapter(Context context,List<FenLeiName> list) {
		super();
		this.context = context;
		this.list = list;
	}

	public void changeData(List<FenLeiName> list) {
		this.list = list;
		notifyDataSetChanged();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_fenlei_child_grid, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(list.get(position).getName()!=null&&!list.get(position).getName().trim().equals("")){
			holder.tv_name.setText(list.get(position).getName());
		}else{
			holder.tv_name.setText("未知");
		}
		return convertView;
	}

	class ViewHolder {
		public TextView tv_name;
	}
}
