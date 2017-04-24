package com.woting.ui.music.citylist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.music.citylist.citymodel.secondaryCity;

import java.util.List;

/**
 * 城市列表适配器
 *
 * @author 辛龙
 *         2016年3月25日
 */
public class CityListAdapter extends BaseAdapter implements SectionIndexer {
    private List<secondaryCity> list;
    private Context context;
    private secondaryCity lists;

    public CityListAdapter(Context context, List<secondaryCity> list) {
        this.list = list;
        this.context = context;
    }

    public void ChangeDate(List<secondaryCity> list) {
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_city, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);//名
            holder.indexLayut = (LinearLayout) convertView.findViewById(R.id.index);
            holder.contactLayut = (LinearLayout) convertView.findViewById(R.id.contactLayut);
            holder.indexTv = (TextView) convertView.findViewById(R.id.indexTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        lists = list.get(position);
        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            holder.indexLayut.setVisibility(View.VISIBLE);
            holder.indexTv.setText(list.get(position).getSortLetters());
        } else {
            holder.indexLayut.setVisibility(View.GONE);
        }
        if (lists.getCatalogName() == null || lists.getCatalogName().equals("")) {
            holder.tv_name.setText("未知");//名
        } else {
            holder.tv_name.setText(lists.getCatalogName());//名
        }
        return convertView;
    }

    class ViewHolder {
        public LinearLayout contactLayut;
        public TextView indexTv;
        public LinearLayout indexLayut;
        public TextView tv_name;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
