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
import com.woting.activity.home.search.activity.SearchLikeAcitvity;
import com.woting.activity.main.MainActivity;
import com.woting.activity.person.favorite.adapter.FavorListAdapter;
import com.woting.activity.person.favorite.adapter.FavorListAdapter.favorCheck;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;
import com.woting.widgetui.xlistview.XListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class TTSFragment extends Fragment {
	private FragmentActivity context;
	private Dialog dialog;
	private List<RankInfo> SubList;
	private ListView mlistView;
	private ArrayList<RankInfo> newlist = new ArrayList<RankInfo>();
	private View rootView;
	protected FavorListAdapter adapter;
//	private boolean flag;
//	private List<String> dellist;
//	private String ReturnType;
	private Intent mintent;
	private SearchPlayerHistoryDao dbdao;
	protected Integer pagesize;
	protected String searchstr;
	private String tag = "TTS_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
//		flag = true;// 设置等待提示是否展示
		//数据变化后广播
		mintent = new Intent();
		mintent.setAction(SearchLikeAcitvity.SEARCH_VIEW_UPDATE);
		initDao();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_search_sound, container, false);
			mlistView = (XListView) rootView.findViewById(R.id.listView);
			mlistView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			IntentFilter myfileter = new IntentFilter();
			myfileter.addAction(SearchLikeAcitvity.SEARCH_VIEW_UPDATE);
			context.registerReceiver(mBroadcastReceiver, myfileter);
		}
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	private void initDao() {
		dbdao = new SearchPlayerHistoryDao(context);
	}

	private void setListener() {		
		adapter.setOnListener(new favorCheck() {
			@Override
			public void checkposition(int position) {
				if (newlist.get(position).getChecktype() == 0) {
					newlist.get(position).setChecktype(1);
				} else {
					newlist.get(position).setChecktype(0);
				}
				adapter.notifyDataSetChanged();
			}
		});

		mlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(newlist != null && newlist.get(position - 1) != null && newlist.get(position - 1).getMediaType() != null){
					String MediaType = newlist.get(position - 1).getMediaType();
					if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
						String playername = newlist.get(position - 1).getContentName();
						String playerimage = newlist.get(position - 1).getContentImg();
						String playerurl = newlist.get(position - 1).getContentPlay();
						String playerurI = newlist.get(position - 1).getContentURI();
						String playermediatype = newlist.get(position - 1).getMediaType();
						String playcontentshareurl = newlist.get(position - 1).getContentShareURL();
						String plaplayeralltime = "0";
						String playerintime = "0";
						String playercontentdesc = newlist.get(position - 1).getCurrentContent();
						String playernum = newlist.get(position - 1).getWatchPlayerNum();
						String playerzantype = "0";
						String playerfrom = "";
						String playerfromid = "";
						String playerfromurl = "";
						String playeraddtime = Long.toString(System.currentTimeMillis());
						String bjuserid =CommonUtils.getUserId(context);
						String ContentFavorite=newlist.get(position - 1).getContentFavorite();
						String ContentId= newlist.get(position-1).getContentId();
						String localurl=newlist.get(position-1).getLocalurl();

						String sequName=newlist.get(position-1).getSequName();
						String sequId=newlist.get(position-1).getSequId();
						String sequDesc=newlist.get(position-1).getSequDesc();
						String sequImg=newlist.get(position-1).getSequImg();

						//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
						PlayerHistory history = new PlayerHistory(
								playername,  playerimage, playerurl, playerurI,playermediatype,
								plaplayeralltime, playerintime, playercontentdesc, playernum,
								playerzantype,  playerfrom, playerfromid,playerfromurl, playeraddtime,bjuserid,playcontentshareurl,
								ContentFavorite,ContentId,localurl,sequName,sequId,sequDesc,sequImg);
						dbdao.deleteHistory(playerurl);
						dbdao.addHistory(history);
						MainActivity.change();
						HomeActivity.UpdateViewPager();
						PlayerFragment.SendTextRequest(newlist.get(position - 1).getContentName(),context);
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
							SubList = new Gson().fromJson(StringSubList,new TypeToken<List<RankInfo>>() {}.getType());
							newlist.clear();
							newlist.addAll(SubList);
							if(adapter == null){
								adapter = new FavorListAdapter(context, newlist);
								mlistView.setAdapter(adapter);
							}else{
								adapter.notifyDataSetChanged();
							}
						} catch (JSONException e) {
							e.printStackTrace();
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
							mlistView.setVisibility(View.GONE);
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
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("MediaType", "TTS");
			if(searchstr != null && !searchstr.equals("")){
				jsonObject.put("SearchStr", searchstr);
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
			if (action.equals(SearchLikeAcitvity.SEARCH_VIEW_UPDATE)) {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					searchstr=intent.getStringExtra("SearchStr");
					if(searchstr!=null&&!searchstr.equals("")){
						dialog = DialogUtils.Dialogph(context, "通讯中", dialog);
						sendRequest();
					}else{
					/*	ToastUtil.show_allways(context, "搜索字符串获取异常");*/
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
		context.unregisterReceiver(mBroadcastReceiver);
		mlistView = null;
		context = null;
		dialog = null;
		SubList = null;
		newlist = null;
		rootView = null;
		adapter = null;
		mintent = null;
		pagesize = null;
		searchstr = null;
		tag = null;
		if(dbdao != null){
			dbdao.closedb();
			dbdao = null;
		}
	}
}
