package com.woting.ui.musicplay.anchor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.musicplay.anchor.model.PersonInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * Created by Administrator on 2016/12/27 0027.
 */
public class AnchorMainAdapter extends BaseAdapter {
    private List<PersonInfo> list;
    private Context context;
    private PersonInfo lists;
    private SimpleDateFormat format;

    public AnchorMainAdapter(Context context, List<PersonInfo> subList) {
        super();
        this.list = subList;
        this.context = context;
        format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    }

    public void ChangeDate(List<PersonInfo> list) {
        this.list = list;
        this.notifyDataSetChanged();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_album_main, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_playnum = (TextView) convertView.findViewById(R.id.tv_playnum);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.textTime = (TextView) convertView.findViewById(R.id.text_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        lists = list.get(position);
        if (lists.getContentName() == null || lists.getContentName().equals("")) {
            holder.tv_name.setText("未知");
        } else {
            holder.tv_name.setText(lists.getContentName());
        }
        if (lists.getPlayCount() == null || lists.getPlayCount().equals("")) {
            holder.tv_playnum.setText("0");
        } else {
            holder.tv_playnum.setText(lists.getPlayCount());
        }
        if (lists.getContentPubTime() == null || lists.getContentPubTime().equals("")) {
            holder.tv_time.setText("0000-00-00");
        } else {
            holder.tv_time.setText(format.format(new Date(Long.parseLong(lists.getContentPubTime()))));
        }

        //节目时长
        if (lists.getContentTime() == null
                || lists.getContentTime().equals("")
                || lists.getContentTime().equals("null")) {
            holder.textTime.setText(context.getString(R.string.play_time));
        } else {
            try{
                if(lists.getContentTime().contains(":")){
                    holder.textTime.setText(lists.getContentTime());
                } else{
                    int minute = Integer.valueOf(lists.getContentTime()) / (1000 * 60);
                    int second = (Integer.valueOf(lists.getContentTime()) / 1000) % 60;
                    if(second < 10){
                        holder.textTime.setText(minute + "\'" + " " + "0" + second + "\"");
                    }else{
                        holder.textTime.setText(minute + "\'" + " " + second + "\"");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return convertView;
    }

    class ViewHolder {
        public TextView tv_time;
        public TextView tv_playnum;
        public TextView tv_name;
        public TextView textTime;
    }
}
