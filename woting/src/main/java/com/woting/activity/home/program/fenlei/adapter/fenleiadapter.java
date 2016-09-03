package com.woting.activity.home.program.fenlei.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.home.program.diantai.model.RadioPlay;
import com.woting.activity.home.program.radiolist.activity.RadioListActivity;
import com.woting.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class fenleiadapter extends BaseExpandableListAdapter {
	private Context context;
	private List<RadioPlay> group;
	private static int num = 0;// Group的计数项
	private static int i = 0;
	private fenleigridAdapter adapter;
	private static int oldChildPosion = -1;
	private static int oldGroupPosion = -1;

	public fenleiadapter(Context context, List<RadioPlay> group) {
		this.context = context;
		this.group = group;
	}

	@Override
	public int getGroupCount() {
		return group.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return group.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return group.get(groupPosition).getList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	/**
	 * 显示：group
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_fenlei_group, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.lin_more = (LinearLayout) convertView
					.findViewById(R.id.lin_fenlei_group);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final RadioPlay lists = group.get(groupPosition);
		if (lists.getCatalogName() == null || lists.getCatalogName().equals("")) {
			holder.tv_name.setText("未知");
		} else {
			holder.tv_name.setText(lists.getCatalogName());
		}
		// 动态设置每个项目的颜色
		if (groupPosition != oldGroupPosion) {
			holder.lin_more.setBackgroundResource(getcolor());
			oldGroupPosion = groupPosition;
		}
		return convertView;
	}

	// 制作一个保存颜色的数组 通过一个环境公共变量给他们赋值，决定数组情况
	private int getcolor() {
		int[] imgs = { R.drawable.bg_fenlei_group, R.drawable.bg_fenlei_group1,
				R.drawable.bg_fenlei_group2, R.drawable.bg_fenlei_group3 };
		if (num >= imgs.length) {
			num = 0;
		}
		i = num;
		num++;
		return imgs[i];
	}

	/**
	 * 显示：child
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final boolean flag = true;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_fenlei_child, null);
			holder.mgridview = (GridView) convertView
					.findViewById(R.id.gv_fenlei);
			holder.mgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 只有adapter为空 或者两个groupPositon的位置发生变更时才会调用这个方法
		if (adapter == null || oldChildPosion != groupPosition) {
			final List<String> mylist = new ArrayList<String>();
			for (int i = 0; i < group.get(groupPosition).getList().size(); i++) {
				mylist.add(group.get(groupPosition).getList().get(i)
						.getContentName());
			}
			// 区分情况来处理
			oldChildPosion = groupPosition;
	/*		adapter = new fenleigridAdapter(context, mylist);*/
			holder.mgridview.setAdapter(adapter);
			holder.mgridview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					/*
					 * ToastUtil.show_short(context,
					 * "我是"+oldChildPosion+"的"+position+"");
					 */
					ToastUtils.show_short(context,mylist.get(position));
			/*		CatagoryName = requestBundle.getString("CatagoryName");
					CatagoryType = requestBundle.getString("CatagoryType");*/					
					Intent intent=new Intent(context,RadioListActivity.class);
					Bundle bundle=new Bundle();
					//测试数据
					bundle.putString("CatagoryName", mylist.get(position));
					bundle.putString("CatagoryType", "hhhh");
					intent.putExtras(bundle);
					context.startActivity(intent);	
				}
			});
		}
		return convertView;
	}

	class ViewHolder {
		public TextView textview_rankplayingnumber;
		public TextView textview_rankplaying;
		public TextView textview_ranktitle;
		public TextView tv_name;
		public LinearLayout lin_more;
		public TextView mTv_number;
		public GridView mgridview;
		/* TextView textView; */
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
