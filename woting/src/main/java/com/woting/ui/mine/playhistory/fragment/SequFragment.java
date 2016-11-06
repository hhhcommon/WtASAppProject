package com.woting.ui.mine.playhistory.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.woting.R;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.mine.playhistory.activity.PlayHistoryActivity;
import com.woting.ui.mine.playhistory.adapter.PlayHistoryAdapter;
import com.woting.ui.mine.playhistory.adapter.PlayHistoryAdapter.playhistorycheck;
import com.woting.common.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放历史记录  专辑界面
 * 
 * @author woting11
 */
public class SequFragment extends Fragment{
	private View rootView;
	private SearchPlayerHistoryDao dbdao;
	private Context context;
	private ListView listView;
	private ArrayList<PlayerHistory> playList;	// 节目list
	private List<PlayerHistory> subList;		// 播放历史数据
	private PlayHistoryAdapter adapter;
	private List<PlayerHistory> deleteList;		// 删除数据列表
	private List<PlayerHistory> checkList;		// 选中数据列表
	public static boolean isData;				// 标记是否有数据
	private LinearLayout linearNull;			// linear_null
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		initDao();					// 初始化数据库
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_playhistory_sequ_layout, container, false);
			listView = (ListView) rootView.findViewById(R.id.list_view);
			linearNull = (LinearLayout) rootView.findViewById(R.id.linear_null);
		}
		getData();
		return rootView;
	}
	
	/**
	 * 获取数据
	 */
	public void getData(){
		listView.setVisibility(View.INVISIBLE);
		SequFragment.isData = false;
		subList = dbdao.queryHistory();
		playList = null;
		if (subList != null && subList.size() > 0) {
			for (int i = 0; i < subList.size(); i++) {
				if (subList.get(i).getPlayerMediaType()!=null && !subList.get(i).getPlayerMediaType().equals("")) {
					if (subList.get(i).getPlayerMediaType().equals("SEQU")) {
						if (playList == null) {
							playList = new ArrayList<PlayerHistory>();
						}
						playList.add(subList.get(i));
						SequFragment.isData = true;
					}
				}
			}
			if(playList != null){
				adapter = new PlayHistoryAdapter(context, playList);
				listView.setAdapter(adapter);
				setInterface();
			}
			listView.setVisibility(View.VISIBLE);
		} else{
			ToastUtils.show_allways(context, "没有历史播放记录");
		}
	}
	
	/**
	 * 设置 View 隐藏
	 */
	public void setLinearHint(){
		linearNull.setVisibility(View.GONE);
	}
	
	/**
	 * 设置 View 可见  解决全选 Dialog 挡住 ListView 最底下一条 Item 问题
	 */
	public void setLinearVisibility(){
		linearNull.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 实现接口  设置点击事件
	 */
	private void setInterface() {
		adapter.setonclick(new playhistorycheck() {
			
			@Override
			public void checkposition(int position) {
				if(playList.get(position).getStatus() == 0){
					playList.get(position).setStatus(1);
				}else if(playList.get(position).getStatus() == 1){
					playList.get(position).setStatus(0);
				}
				adapter.notifyDataSetChanged();
				ifAll();
			}
		});
	}
	
	/**
	 * 更新是否全选状态
	 */
	private void ifAll(){
		if(checkList == null){
			checkList = new ArrayList<>();
		}
		for(int i=0; i<playList.size(); i++){
			if(playList.get(i).getStatus() == 1 && !checkList.contains(playList.get(i))){
				checkList.add(playList.get(i));
			}else if(playList.get(i).getStatus() == 0 && checkList.contains(playList.get(i))){
				checkList.remove(playList.get(i));
			}
		}
		if(checkList.size() == playList.size()){
			Intent intentAll = new Intent();
			intentAll.setAction(PlayHistoryActivity.UPDATE_ACTION_ALL);
			context.sendBroadcast(intentAll);
		}else{
			Intent intentNoCheck = new Intent();
			intentNoCheck.setAction(PlayHistoryActivity.UPDATE_ACTION_CHECK);
			context.sendBroadcast(intentNoCheck);
		}
	}
	
	/**
	 * 设置可选状态
	 */
	public void setCheck(boolean checkStatus){
		if(playList!= null && playList.size() > 0){
			for(int i=0; i<playList.size(); i++){
				playList.get(i).setCheck(checkStatus);
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 设置是否选中
	 */
	public void setCheckStatus(int status){
		if(playList!= null && playList.size() > 0){
			for(int i=0; i<playList.size(); i++){
				playList.get(i).setStatus(status);
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 删除数据
	 */
	public int deleteData(){
		int number = 0;
		for(int i=0; i<playList.size(); i++){
			if(deleteList == null){
				deleteList = new ArrayList<>();
			}
			if(playList.get(i).getStatus() == 1){
				deleteList.add(playList.get(i));
			}
			number = deleteList.size();
		}
		if(deleteList.size() > 0){
			for(int i=0; i<deleteList.size(); i++){
				String url = deleteList.get(i).getPlayerUrl();
				dbdao.deleteHistory(url);
			}
			if(checkList != null && checkList.size() > 0){
				checkList.clear();
			}
			getData();
			deleteList.clear();
			adapter.notifyDataSetChanged();
		}
		return number;
	}
	
	/**
	 * 初始化数据库命令执行对象
	 */
	private void initDao() {
		dbdao = new SearchPlayerHistoryDao(context);
	}
	
	@Override
	public void onDestroyView() {
		super .onDestroyView(); 
		if (null != rootView) {
			((ViewGroup) rootView.getParent()).removeView(rootView);  
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		rootView = null;
		context = null;
		listView = null;
		playList = null;
		subList = null;
		adapter = null;
		deleteList = null;
		checkList = null;
		linearNull = null;
		if(dbdao != null){
			dbdao.closedb();
			dbdao = null;
		}
	}
}
