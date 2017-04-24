package com.woting.ui.mine.set.preference.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.widgetui.MyGridView;
import com.woting.ui.music.classify.model.FenLei;
import com.woting.ui.mine.set.preference.activity.PreferenceActivity;

import java.util.List;


/**
 * 偏好设置的适配器
 * 作者：xinlong on 2016/10/20
 * 邮箱：645700751@qq.com
 */
public class PianHaoAdapter extends BaseAdapter {
    private List<FenLei> list;
    private Context context;
    private ViewHolder holder;
    private PreferGridAdapter adapters;
    private preferCheck preferCheck;
    private View.OnClickListener onClickListener;

    public PianHaoAdapter(Context context, List<FenLei> list) {
        super();
        this.list = list;
        this.context = context;
    }

    public void setOnListener(preferCheck friendcheck) {
        this.preferCheck = friendcheck;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_prefer_group, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_quanxuan = (TextView) convertView.findViewById(R.id.tv_Quan_Xuan);
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
            adapters = new PreferGridAdapter(context, list.get(position).getChildren());
            holder.gv.setAdapter(adapters);
            holder.gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int positions, long id) {
                    String s = list.get(position).getChildren().get(positions).getchecked();
                    if (list.get(position).getChildren().get(positions).getchecked().equals("false")) {
                        list.get(position).getChildren().get(positions).setchecked("true");
                        for (int i = 0; i < list.get(position).getChildren().size(); i++) {
                            if (list.get(position).getChildren().get(i).getchecked().equals("false")) {
                                break;
                            } else {
                                if (i == list.get(position).getChildren().size() - 1) {
                                    list.get(position).setTag(position);
                                    list.get(position).setTagType(1);
                                }
                            }
                        }
                    } else {
                        list.get(position).getChildren().get(positions).setchecked("false");
                        for (int i = 0; i < list.get(position).getChildren().size(); i++) {
                            if (list.get(position).getChildren().get(i).getchecked().equals("false")) {
                                list.get(position).setTag(position);
                                list.get(position).setTagType(0);
                            }
                        }
                    }
                    PreferenceActivity.RefreshView(list);
                /*ToastUtils.show_always( context,list.get(position).getChildren().get(positions).getName());*/
                }
            });
        }
        int a = list.get(position).getTag();
        int b = list.get(position).getTagType();
        if (list.get(position).getTag() == position) {
            if (list.get(position).getTagType() == 1) {
                holder.tv_quanxuan.setText("取消全选");
                holder.tv_quanxuan.setTextColor(context.getResources().getColor(R.color.gray));
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            } else {
                holder.tv_quanxuan.setText("全选");
                holder.tv_quanxuan.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.black));
            }
        } else {
            holder.tv_quanxuan.setText("全选");
            holder.tv_quanxuan.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.black));
        }
/*
        holder.tv_quanxuan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
           *//*     if(list.get(position).getChildren().get(0).getchecked().equals("false")){
                    for(int i=0;i<list.get(position).getChildren().size();i++){
                        list.get(position).getChildren().get(i).setchecked("true");
                        list.get(position).setTag(position);
                        list.get(position).setTagType(1);
                        Log.e("prefer","position"+position+list.get(position).getChildren().get(0).getName());
                    }
                }else{
                    for(int i=0;i<list.get(position).getChildren().size();i++){
                        list.get(position).getChildren().get(i).setchecked("false");
                        list.get(position).setTag(position);
                        list.get(position).setTagType(0);
                        Log.e("prefer","position"+position+list.get(position).getChildren().get(0).getName());
                    }

                }*//*
                PreferenceActivity.RefreshView(list);
            }
        });*/
      /*  Log.e("prefer","position"+position+list.get(position).getChildren().get(0).getName());*/
        holder.tv_quanxuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferCheck.clickPosition(position);
            }
        });
        return convertView;
    }

    public interface preferCheck {
        public void clickPosition(int position);
    }

    class ViewHolder {
        public TextView tv_name;
        public MyGridView gv;
        public TextView tv_quanxuan;
    }
}
