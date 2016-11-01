package com.woting.activity.home.search.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.home.main.HomeActivity;
import com.woting.activity.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.activity.home.player.main.fragment.PlayerFragment;
import com.woting.activity.home.player.main.model.PlayerHistory;
import com.woting.activity.home.program.fmlist.model.RankInfo;
import com.woting.activity.home.search.activity.SearchLikeActivity;
import com.woting.activity.main.MainActivity;
import com.woting.activity.person.favorite.adapter.FavorListAdapter;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class SoundFragment extends Fragment {
	private FragmentActivity context;
	private Dialog dialog;
	private List<RankInfo> SubList;
	private ListView mListView;
	private ArrayList<RankInfo> newList = new ArrayList<RankInfo>();

	private View rootView;
	protected FavorListAdapter adapter;

	private SearchPlayerHistoryDao dbDao;
	protected Integer pageSize;
	protected String searchStr;
	private String tag = "SOUND_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	private Intent mIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		//数据变化后广播
		mIntent = new Intent();
		mIntent .setAction(SearchLikeActivity.SEARCH_VIEW_UPDATE);
		initDao();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_search_sound, container, false);
			mListView = (ListView) rootView.findViewById(R.id.listView);
			mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			IntentFilter mFilter = new IntentFilter();
			mFilter.addAction(SearchLikeActivity.SEARCH_VIEW_UPDATE);
			context.registerReceiver(mBroadcastReceiver, mFilter);
		}
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	private void initDao() {
		dbDao = new SearchPlayerHistoryDao(context);
	}

	private void setListener() {		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(newList != null && newList.get(position - 1) != null && newList.get(position - 1).getMediaType() != null){
					String MediaType = newList.get(position - 1).getMediaType();
					if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
						String playername = newList.get(position - 1).getContentName();
						String playerimage = newList.get(position - 1).getContentImg();
						String playerurl = newList.get(position - 1).getContentPlay();
						String playerurI = newList.get(position - 1).getContentURI();
						String playermediatype = newList.get(position - 1).getMediaType();
						String playcontentshareurl =newList.get(position - 1).getContentShareURL();
						String plaplayeralltime = "0";
						String playerintime = "0";
						String playercontentdesc = newList.get(position - 1).getCurrentContent();
						String playernum =newList.get(position - 1).getWatchPlayerNum();
						String playerzantype = "0";
						String playerfrom = "";
						String playerfromid = "";
						String playerfromurl = "";
						String playeraddtime = Long.toString(System.currentTimeMillis());
						String bjuserid = CommonUtils.getUserId(context);
						String ContentFavorite = newList.get(position - 1).getContentFavorite();
						String ContentId = newList.get(position-1).getContentId();
						String localurl = newList.get(position-1).getLocalurl();

						String sequName=newList.get(position-1).getSequName();
						String sequId=newList.get(position-1).getSequId();
						String sequDesc=newList.get(position-1).getSequDesc();
						String sequImg=newList.get(position-1).getSequImg();

						//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
						PlayerHistory history = new PlayerHistory(
								playername,  playerimage, playerurl, playerurI,playermediatype,
								plaplayeralltime, playerintime, playercontentdesc, playernum,
								playerzantype,  playerfrom, playerfromid,playerfromurl, playeraddtime,bjuserid,playcontentshareurl,
								ContentFavorite,ContentId,localurl,sequName,sequId,sequDesc,sequImg);
						dbDao.deleteHistory(playerurl);
						dbDao.addHistory(history);
						MainActivity.change();
						HomeActivity.UpdateViewPager();
						PlayerFragment.SendTextRequest(newList.get(position - 1).getContentName(),context);
						context.finish();
					}  else {
						ToastUtils.show_short(context, "暂不支持的Type类型");
					}
				}
			}
		});
	}

	private void sendRequest(){
		VolleyRequest.RequestPost(GlobalConfig.getSearchByText, tag, setParam(), new VolleyCallback() {
			private String ResultList;
			private String StringSubList;
			private String ReturnType;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				try {
					ReturnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null) {
					if (ReturnType.equals("1001")) {
						try {
							// 获取列表
							ResultList = result.getString("ResultList");
							JSONTokener jsonParser = new JSONTokener(ResultList);
							JSONObject arg1 = (JSONObject) jsonParser.nextValue();
							StringSubList = arg1.getString("List");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						SubList = new Gson().fromJson(StringSubList,new TypeToken<List<RankInfo>>() {}.getType());
						newList.clear();
						newList.addAll(SubList);
						if(adapter == null){
							adapter = new FavorListAdapter(context, newList);
							mListView.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
						setListener();			
					} else {
						if (ReturnType.equals("0000")) {
							ToastUtils.show_short(context, "无法获取相关的参数");
						} else if (ReturnType.equals("1002")) {
							ToastUtils.show_short(context, "无此分类信息");
						} else if (ReturnType.equals("1003")) {
							ToastUtils.show_short(context, "无法获得列表");
						} else if (ReturnType.equals("1011")) {
							ToastUtils.show_short(context, "无数据");
							mListView.setVisibility(View.GONE);
						}
					}
				} else {
					ToastUtils.show_short(context, "ReturnType不能为空");
				}
			}
			
			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}
	
	private JSONObject setParam(){
		JSONObject jsonObject =VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("MediaType", "AUDIO");
			if(searchStr!=null&&!searchStr.equals("")){
				jsonObject.put("SearchStr", searchStr);
			}
		} catch (JSONException e) {
			e.printStackTrace();  
		}
		return jsonObject;
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(SearchLikeActivity.SEARCH_VIEW_UPDATE)) {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					searchStr=intent.getStringExtra("SearchStr");
					if(searchStr!=null&&!searchStr.equals("")){
						dialog = DialogUtils.Dialogph(context, "通讯中", dialog);
						sendRequest();
					}else{

					}
					
				} else {
					ToastUtils.show_allways(context, "网络失败，请检查网络");
				}
			} 
		}
		};

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
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		mListView = null;
		context.unregisterReceiver(mBroadcastReceiver);
		context = null;
		dialog = null;
		SubList = null;
		newList = null;
		rootView = null;
		adapter = null;
		mIntent = null;
		pageSize = null;
		searchStr = null;
		tag = null;
		if(dbDao != null){
			dbDao.closedb();
			dbDao = null;
		}
	}
}
