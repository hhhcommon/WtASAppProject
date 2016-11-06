package com.woting.ui.home.program.citylist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.home.program.fenlei.model.CatalogName;

import java.util.List;

/**
 * 通讯录好友适配器
 * @author 辛龙
 * 2016年3月25日
 */
public class CityListAdapter extends BaseAdapter implements SectionIndexer{
	private List<CatalogName> list;
	private Context context;
	private CatalogName lists;
	public CityListAdapter(Context context,List<CatalogName> list) {
		super();
		this.list = list;
		this.context = context;
	}
	public void ChangeDate(List<CatalogName> list){
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
		ViewHolder holder=null;
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_city, null);
			holder=new ViewHolder();
			holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);//名
			holder.indexLayut=(LinearLayout)convertView.findViewById(R.id.index);
			holder.contactLayut=(LinearLayout)convertView.findViewById(R.id.contactLayut);
			holder.indexTv = (TextView) convertView.findViewById(R.id.indexTv); 
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		lists=list.get(position);
		int section = getSectionForPosition(position);
		if (position == getPositionForSection(section)) {
			holder.indexLayut.setVisibility(View.VISIBLE);
			holder.indexTv.setText(list.get(position).getSortLetters());  
		} else {
			holder.indexLayut.setVisibility(View.GONE);
		}
		if(lists.getCatalogName()==null||lists.getCatalogName().equals("")){
			holder.tv_name.setText("未知");//名
		}else{
			holder.tv_name.setText(lists.getCatalogName());//名
		}
		return convertView;
	}

	class ViewHolder{
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

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}
	
	@Override
	public Object[] getSections() {
		return null;
	}
}
