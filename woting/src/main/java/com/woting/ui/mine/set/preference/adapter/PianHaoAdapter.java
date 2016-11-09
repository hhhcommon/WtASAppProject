package com.woting.ui.mine.set.preference.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.mine.set.preference.model.pianhao;

import java.util.List;


/**
 * 偏好设置的适配器
 * 作者：xinlong on 2016/10/20
 * 邮箱：645700751@qq.com
 */
public class PianHaoAdapter extends BaseAdapter {
    private List<pianhao> list;
    private Context context;

    public PianHaoAdapter(Context context, List<pianhao> list) {
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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_pianhao, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);//
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);//
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list.get(position).getType() == 1) {
            holder.imageView.setVisibility(View.INVISIBLE);
            holder.tv_name.setBackgroundResource(R.drawable.pianhao_gray);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.tv_name.setBackgroundResource(R.drawable.pianhao_orange);
        }

        holder.tv_name.setText(list.get(position).getName());
        return convertView;
    }

    class ViewHolder {
        public ImageView imageView;
        public TextView tv_name;
    }
}
