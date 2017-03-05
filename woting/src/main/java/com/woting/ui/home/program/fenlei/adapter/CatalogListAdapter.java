package com.woting.ui.home.program.fenlei.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.widgetui.MyGridView;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.program.fenlei.model.FenLei;
import com.woting.ui.home.program.radiolist.activity.RadioListFragment;

import java.util.List;

public class CatalogListAdapter extends BaseAdapter {
    private List<FenLei> list;
    private Context context;
    private ViewHolder holder;
    private CatalogGridAdapter adapters;


    public CatalogListAdapter(Context context, List<FenLei> list) {
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_fenlei_group, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.gv = (MyGridView) convertView.findViewById(R.id.gridView);
            holder.gv.setSelector(new ColorDrawable(Color.TRANSPARENT));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(list.get(position).getName()!=null&&!list.get(position).getName().trim().equals("")){
            holder.tv_name.setText(list.get(position).getName());
        }else{
            holder.tv_name.setText("未知");
        }

        if (list.get(position).getChildren() != null && list.get(position).getChildren().size() > 0) {
        adapters = new CatalogGridAdapter(context, list.get(position).getChildren());
        holder.gv.setAdapter(adapters);
        holder.gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int positions, long id) {
                RadioListFragment radioListFragment = new RadioListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "fenLeiAdapter");
                bundle.putSerializable("Catalog", list.get(position).getChildren().get(positions));
                radioListFragment.setArguments(bundle);
                HomeActivity.open(radioListFragment);
            }
        });
    }
        return convertView;
    }

    class ViewHolder {
        public TextView tv_name;
        public MyGridView gv;
    }
}
