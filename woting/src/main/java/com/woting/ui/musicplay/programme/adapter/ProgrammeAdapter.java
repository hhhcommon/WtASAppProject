package com.woting.ui.musicplay.programme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.model.programme.programme;

import java.util.List;

/**
 * 节目单的适配器
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class ProgrammeAdapter extends BaseAdapter {
    private final int onTime;
    private List<programme> list;
    private Context context;
    private boolean isT;
    private String eTime;
    private String bTime;

    public ProgrammeAdapter(Context context, List<programme> list, boolean isT, int onTime) {
        this.context = context;
        this.list = list;
        this.isT = isT;
        this.onTime = onTime;
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
        programme lists = list.get(position);


        if (lists.getTitle() == null || lists.getTitle().equals("") || lists.getTitle().equals("null")) {
            holder.tv_name.setText("未知");
        } else {
            holder.tv_name.setText(lists.getTitle());
        }


        if (lists.getBeginTime() == null || lists.getBeginTime().equals("") || lists.getBeginTime().equals("null")) {
            bTime = "";
        } else {
            bTime = lists.getBeginTime().substring(0, lists.getBeginTime().length() - 3);
        }

        if (lists.getEndTime() == null || lists.getEndTime().equals("") || lists.getEndTime().equals("null")) {
            eTime = "";
        } else {
            eTime = lists.getEndTime().substring(0, lists.getEndTime().length() - 3);
        }
        holder.tv_time.setText(bTime + "-" + eTime);
        try {
            if (isT) {

                int bT = Integer.parseInt(lists.getBeginTime().substring(0, 2)) * 60 + Integer.parseInt(lists.getBeginTime().substring(3, 5));
                int eT = Integer.parseInt(lists.getEndTime().substring(0, 2)) * 60 + Integer.parseInt(lists.getEndTime().substring(3, 5));

                if (bT <= onTime && onTime < eT) {
                    holder.tv_time.setTextColor(context.getResources().getColor(R.color.black));
                    holder.tv_name.setTextColor(context.getResources().getColor(R.color.black));
                    holder.lin_show.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_time.setTextColor(context.getResources().getColor(R.color.darkgray));
                    holder.tv_name.setTextColor(context.getResources().getColor(R.color.darkgray));
                    holder.lin_show.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.tv_time.setTextColor(context.getResources().getColor(R.color.darkgray));
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.darkgray));
                holder.lin_show.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.darkgray));
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.darkgray));
            holder.lin_show.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        public TextView tv_name;
        public TextView tv_time;
        public LinearLayout lin_show;
    }
}
