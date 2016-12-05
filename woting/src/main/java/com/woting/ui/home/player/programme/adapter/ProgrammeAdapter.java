package com.woting.ui.home.player.programme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.home.player.programme.model.program;

import java.util.List;

/**
 * 节目单的适配器
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class ProgrammeAdapter extends BaseAdapter {
    private List<program> list;
    private Context context;

    public ProgrammeAdapter(Context context, List<program> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_program, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);         // 节目名称
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);         // 节目时间
            holder.lin_show = (LinearLayout) convertView.findViewById(R.id.lin_show);   // 台名
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        program lists = list.get(position);


        if (lists.getTitle() == null || lists.getTitle().equals("") || lists.getTitle().equals("null")) {
            holder.tv_name.setText("未知");
        } else {
            holder.tv_name.setText(lists.getTitle());
        }

        return convertView;
    }

    class ViewHolder {
        public TextView tv_name;
        public TextView tv_time;
        public LinearLayout lin_show;
    }
}
