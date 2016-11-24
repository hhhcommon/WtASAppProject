package com.woting.ui.mine.myupload.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.mine.myupload.model.TagInfo;

import java.util.List;

/**
 * 我的标签
 * Created by Administrator on 2016/11/24.
 */
public class MyTagGridAdapter extends BaseAdapter {
    private Context context;
    private List<TagInfo> list;

    public MyTagGridAdapter(Context context, List<TagInfo> list) {
        this.context = context;
        this.list = list;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_searchlike, parent, false);
            holder = new ViewHolder();
            holder.textTagName = (TextView) convertView.findViewById(R.id.tv_search_like);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textTagName.setText(list.get(position).getTagName());
        return convertView;
    }

    class ViewHolder {
        TextView textTagName;
    }
}
