package com.woting.ui.music.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.music.search.model.History;

import java.util.List;

/**
 * 搜索历史数据展示
 */
public class SearchHistoryAdapter extends BaseAdapter{
	private Context context;
	private List<History> list;

	public SearchHistoryAdapter(Context context, List<History> list) {
		this.context=context;
		this.list=list;
	}
	public void changeData(List<History> list) {
		this.list=list;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_searchlike, null);
			holder = new ViewHolder();
			holder.tv=(TextView)convertView.findViewById(R.id.tv_search_like);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv.setText(list.get(position).getPlayName());		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv;
	}
}
