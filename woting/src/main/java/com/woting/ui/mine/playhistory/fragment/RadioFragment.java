package com.woting.ui.mine.playhistory.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.woting.R;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.playhistory.activity.PlayHistoryActivity;
import com.woting.ui.mine.playhistory.adapter.PlayHistoryAdapter;
import com.woting.ui.mine.playhistory.adapter.PlayHistoryAdapter.playhistorycheck;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放历史记录  电台界面
 * 
 * @author woting11
 */
public class RadioFragment extends Fragment{
	private View rootView;
	private SearchPlayerHistoryDao dbdao;
	private Context context;
	private ListView listView;
	private ArrayList<PlayerHistory> playList;	// 节目list
	private List<PlayerHistory> subList;		// 播放历史数据
	private PlayHistoryAdapter adapter;
	private List<PlayerHistory> deleteList;		// 删除数据列表
	private List<PlayerHistory> checkList;		// 选中数据列表
	public static boolean isData;				// 是否有数据
	public static boolean isLoad;				// 是否加载过
	private LinearLayout linearNull;			// linear_null
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		initDao();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_playhistory_radio_layout, container, false);
			listView = (ListView) rootView.findViewById(R.id.list_view);
			linearNull = (LinearLayout) rootView.findViewById(R.id.linear_null);
			getData();
			isLoad = true;
		}
		return rootView;
	}
	
	/**
	 * 初始化数据库命令执行对象
	 */
	private void initDao() {
		dbdao = new SearchPlayerHistoryDao(context);
	}
	
	/**
	 * 获取数据
	 */
	public void getData(){
		listView.setVisibility(View.GONE);
		isData = false;
		subList = dbdao.queryHistory();
		playList = null;
		if (subList != null && subList.size() > 0) {
			for (int i = 0; i < subList.size(); i++) {
				if (subList.get(i).getPlayerMediaType() != null && !subList.get(i).getPlayerMediaType().equals("")) {
					if (subList.get(i).getPlayerMediaType().equals("RADIO")) {
						if (playList == null) {
							playList = new ArrayList<>();
						}
						playList.add(subList.get(i));
						isData = true;
					}
				}
			}
			if(playList == null){
				playList = new ArrayList<>();
			}
			adapter = new PlayHistoryAdapter(context, playList);
			listView.setAdapter(adapter);
			setInterface();
			listView.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 解决重复加载问题以及没有历史播放记录时向用户进行友好提示
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
//		if(isVisibleToUser && isLoad && !isData){
//			ToastUtils.show_allways(context, "没有历史播放记录");
//		}
		if(isVisibleToUser && TotalFragment.isDeleteRadio){
			getData();
			TotalFragment.isDeleteRadio = false;
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
		
		/**
		 * ListView Item 点击监听
		 * 
		 * 编辑状态下时点击为选中  不是编辑状态下时点击则跳转到播放界面
		 */
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!PlayHistoryActivity.isEdit){
					if(playList.get(position).getStatus() == 0){
						playList.get(position).setStatus(1);
					}else if(playList.get(position).getStatus() == 1){
						playList.get(position).setStatus(0);
					}
					adapter.notifyDataSetChanged();
					ifAll();
				}else{
					if(playList != null && playList.get(position) != null){
						String playername = playList.get(position).getPlayerName();
						String playerimage = playList.get(position).getPlayerImage();
						String playerurl = playList.get(position).getPlayerUrl();
						String playerurI = playList.get(position).getPlayerUrI();
						String playermediatype = playList.get(position).getPlayerMediaType();
						String plaplayeralltime = "0";
						String playerintime = "0";
						String playercontentdesc = playList.get(position).getPlayerContentDescn();
						String playernum = playList.get(position).getPlayerNum();
						String playerzantype = "0";
						String playerfrom = playList.get(position).getPlayerFrom();
						String playerfromid = "";
						String playerfromurl = playList.get(position).getPlayerFromUrl();
						String playeraddtime = Long.toString(System.currentTimeMillis());
						String bjuserid =CommonUtils.getUserId(context);
						String ContentFavorite=playList.get(position).getContentFavorite();
						String playcontentshareurl=playList.get(position).getPlayContentShareUrl();
						String ContentId= playList.get(position).getContentID();
						String localurl=playList.get(position).getLocalurl();

						String sequName= playList.get(position).getSequName();
						String sequId= playList.get(position).getSequId();
						String sequDesc= playList.get(position).getSequDesc();
						String sequImg=playList.get(position).getSequImg();

						PlayerHistory history = new PlayerHistory(
								playername,  playerimage, playerurl, playerurI,playermediatype,
								plaplayeralltime, playerintime, playercontentdesc, playernum,
								playerzantype,  playerfrom, playerfromid,playerfromurl, playeraddtime,bjuserid,playcontentshareurl,
								ContentFavorite,ContentId,localurl,sequName,sequId,sequDesc,sequImg);
						dbdao.deleteHistory(playerurl);
						dbdao.addHistory(history);
						
						if(PlayerFragment.context != null){
							MainActivity.change();
							HomeActivity.UpdateViewPager();
							String s = playList.get(position).getPlayerName();
							PlayerFragment.TextPage=1;
							PlayerFragment.SendTextRequest(s, context);
							getActivity().finish();
						}else{
							SharedPreferences sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
							Editor et = sp.edit();
							et.putString(StringConstant.PLAYHISTORYENTER, "true");
							et.putString(StringConstant.PLAYHISTORYENTERNEWS, subList.get(position).getPlayerName());
							et.commit();
							MainActivity.change();
							HomeActivity.UpdateViewPager();
							getActivity().finish();
						}
					}
				}
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
				deleteList = new ArrayList<PlayerHistory>();
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
			adapter.notifyDataSetChanged();
			deleteList.clear();
			getData();
		}
		return number;
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
