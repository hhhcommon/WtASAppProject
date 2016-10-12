package com.woting.activity.home.search.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.home.main.HomeActivity;
import com.woting.activity.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.activity.home.player.main.fragment.PlayerFragment;
import com.woting.activity.home.player.main.model.PlayerHistory;
import com.woting.activity.home.program.album.activity.AlbumActivity;
import com.woting.activity.home.program.fmlist.model.RankInfo;
import com.woting.activity.home.search.activity.SearchLikeAcitvity;
import com.woting.activity.home.search.adapter.SearchContentAdapter;
import com.woting.activity.home.search.model.SuperRankInfo;
import com.woting.activity.main.MainActivity;
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

public class TotalFragment extends Fragment {
	private View rootView;
	private FragmentActivity context;
//	private ListView mlistview;
	private Dialog dialog;
//	private boolean flag;
	private ExpandableListView ex_listview;
	private ArrayList<RankInfo> playlist;// 节目list
	private ArrayList<RankInfo> sequlist;// 专辑list
	private ArrayList<RankInfo> ttslist;//tts
	private ArrayList<RankInfo> radiolist;//radio
	private ArrayList<SuperRankInfo> list = new ArrayList<SuperRankInfo>();// 返回的节目list，拆分之前的list
	private List<RankInfo> SubList;
	private SearchContentAdapter searchadapter;
	private SearchPlayerHistoryDao dbdao;
	private Intent mintent;
	protected String searchstr;
	private String tag = "TOTAL_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
//		flag = true;
		initDao();
		mintent = new Intent();
		mintent.setAction(SearchLikeAcitvity.SEARCH_VIEW_UPDATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_favorite_total, container, false);
			ex_listview=(ExpandableListView)rootView.findViewById(R.id.ex_listview);
			// 去除indicator
			ex_listview.setGroupIndicator(null);
			setListener();
			IntentFilter myfileter = new IntentFilter();
			myfileter.addAction(SearchLikeAcitvity.SEARCH_VIEW_UPDATE);
			context.registerReceiver(mBroadcastReceiver, myfileter);
		}
		return rootView;
	}

	private void initDao() {
		dbdao = new SearchPlayerHistoryDao(context);
	}

	private void setListener() {
		//屏蔽group点击事件
		ex_listview.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {		
				SearchLikeAcitvity.updateviewpageer(list.get(groupPosition).getKey());
				return true;
			}
		});	
	}

	private void sendRequest(){
		VolleyRequest.RequestPost(GlobalConfig.getSearchByText, tag, setParam(), new VolleyCallback() {
//			private String SessionId;
			private String ReturnType;
			private String Message;
			private String resultlist;
			private JSONObject arg1;
			private String StringSubList;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				try {
//					SessionId = result.getString("SessionId");
					ReturnType = result.getString("ReturnType");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						resultlist = result.getString("ResultList");
						JSONTokener jsonParser = new JSONTokener(resultlist);
						arg1 = (JSONObject) jsonParser.nextValue();
						StringSubList = arg1.getString("List");
						SubList = new Gson().fromJson(StringSubList,new TypeToken<List<RankInfo>>() {}.getType());
					} catch (Exception e) {

						e.printStackTrace();
					}
					list.clear();
					if (playlist != null) {
						playlist.clear();
					}
					if (sequlist != null) {
						sequlist.clear();
					}
					if (SubList.size() >= 0) {
						for (int i = 0; i < SubList.size(); i++) {
							if (SubList.get(i).getMediaType() != null && !SubList.get(i).getMediaType().equals("")) {
								if (SubList.get(i).getMediaType().equals("AUDIO")) {
									if (playlist == null) {
										playlist = new ArrayList<RankInfo>();
										playlist.add(SubList.get(i));
									} else {
										if(playlist.size()<3){
											playlist.add(SubList.get(i));
										}
									}
								} else if (SubList.get(i).getMediaType().equals("SEQU")) {
									if (sequlist == null) {
										sequlist = new ArrayList<RankInfo>();
										sequlist.add(SubList.get(i));
									} else {
										if(sequlist.size()<3){
											sequlist.add(SubList.get(i));
										}
									}
								}else if (SubList.get(i).getMediaType().equals("TTS")) {
									if (ttslist == null) {
										ttslist = new ArrayList<RankInfo>();
										ttslist.add(SubList.get(i));
									} else {
										if(ttslist.size()<3){
											ttslist.add(SubList.get(i));
										}
									}
								}else if (SubList.get(i).getMediaType().equals("RADIO")) {
									if (radiolist == null) {
										radiolist = new ArrayList<RankInfo>();
										radiolist.add(SubList.get(i));
									} else {
										if(radiolist.size()<3){
											radiolist.add(SubList.get(i));
										}

									}
								}

							}
						}
						if (playlist != null && !playlist.equals("") && playlist.size() != 0) {
							SuperRankInfo mSuperRankInfo = new SuperRankInfo();
							mSuperRankInfo.setKey(playlist.get(0).getMediaType());
							//							if (playlist.size() > 3) {
							//								List<RankInfo> list = new ArrayList<RankInfo>();
							//								for (int i = 0; i < 3; i++) {
							//									list.add(playlist.get(i));
							//								}
							//								mSuperRankInfo.setList(list);
							//							} else {
							//								mSuperRankInfo.setList(playlist);
							//							}
							mSuperRankInfo.setList(playlist);
							list.add(mSuperRankInfo);
						}
						if (sequlist != null && !sequlist.equals("")&& sequlist.size() != 0) {
							SuperRankInfo mSuperRankInfo1= new SuperRankInfo();
							mSuperRankInfo1.setKey(sequlist.get(0).getMediaType());							
							//不加限制
							//							if (sequlist.size() > 3) {
							//								List<RankInfo> list = new ArrayList<RankInfo>();
							//								for (int i = 0; i < 3; i++) {
							//									list.add(sequlist.get(i));
							//								}
							//								mSuperRankInfo1.setList(list);
							//							} else {
							//								mSuperRankInfo1.setList(sequlist);
							//							}
							mSuperRankInfo1.setList(sequlist);
							list.add(mSuperRankInfo1);
						}
						if (ttslist != null && !ttslist.equals("") && ttslist.size() != 0) {
							SuperRankInfo mSuperRankInfo1= new SuperRankInfo();
							mSuperRankInfo1.setKey(ttslist.get(0).getMediaType());							
							//不加限制
							//							if (ttslist.size() > 3) {
							//								List<RankInfo> list = new ArrayList<RankInfo>();
							//								for (int i = 0; i < 3; i++) {
							//									list.add(ttslist.get(i));
							//								}
							//								mSuperRankInfo1.setList(list);
							//							} else {
							//								mSuperRankInfo1.setList(ttslist);
							//							}
							mSuperRankInfo1.setList(ttslist);
							list.add(mSuperRankInfo1);
						}
						if (radiolist != null && !radiolist.equals("") && radiolist.size() != 0) {
							SuperRankInfo mSuperRankInfo1= new SuperRankInfo();
							mSuperRankInfo1.setKey(radiolist.get(0).getMediaType());							
							//不加限制
							//							if (radiolist.size() > 3) {
							//								List<RankInfo> list = new ArrayList<RankInfo>();
							//								for (int i = 0; i < 3; i++) {
							//									list.add(radiolist.get(i));
							//								}
							//								mSuperRankInfo1.setList(list);
							//							} else {
							//								mSuperRankInfo1.setList(radiolist);
							//							}
							mSuperRankInfo1.setList(radiolist);
							list.add(mSuperRankInfo1);
						}
						if (list.size() != 0) {
							searchadapter = new SearchContentAdapter(context, list);
							ex_listview.setAdapter(searchadapter);
							for (int i = 0; i < list.size(); i++) {
								ex_listview.expandGroup(i);
							}
							setitemListener();
						} else {
							ToastUtils.show_short(context, "没有数据");
						}
					} else {
						ToastUtils.show_short(context, "数据获取异常");
					}
				}else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, ""+ Message);
				} else if (ReturnType != null && ReturnType.equals("1011")) {
					ToastUtils.show_allways(context, ""+ Message);
					ex_listview.setVisibility(View.GONE);
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_allways(context,Message + "");
					}
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
			jsonObject.put("PageSize","12");
			if(searchstr != null && !searchstr.equals("")){
				jsonObject.put("SearchStr", searchstr);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	protected void setitemListener() {
		ex_listview.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
				String MediaType = null;
				try {
					MediaType = list.get(groupPosition).getList().get(childPosition).getMediaType();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (MediaType!=null&&MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
					String playername = list.get(groupPosition).getList().get(childPosition).getContentName();
					String playerimage = list.get(groupPosition).getList().get(childPosition).getContentImg();
					String playerurl = list.get(groupPosition).getList().get(childPosition).getContentPlay();
					String playerurI= list.get(groupPosition).getList().get(childPosition).getContentURI();
					String playermediatype = list.get(groupPosition).getList().get(childPosition).getMediaType();
					String plaplayeralltime = "0";
					String playerintime = "0";
					String playercontentdesc = list.get(groupPosition).getList().get(childPosition).getCurrentContent();
					String playernum = list.get(groupPosition).getList().get(childPosition).getWatchPlayerNum();
					String playerzantype = "0";
					String playerfrom = "";
					String playerfromid = "";
					String playerfromurl = "";
					String playeraddtime = Long.toString(System.currentTimeMillis());
					String bjuserid =CommonUtils.getUserId(context);
					String playcontentshareurl=list.get(groupPosition).getList().get(childPosition).getContentShareURL();
					String ContentFavorite=list.get(groupPosition).getList().get(childPosition).getContentFavorite();
					String ContentId=list.get(groupPosition).getList().get(childPosition).getContentId();
					String localurl=list.get(groupPosition).getList().get(childPosition).getLocalurl();
					//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
					PlayerHistory history = new PlayerHistory( 
							playername,  playerimage, playerurl,playerurI, playermediatype, 
							plaplayeralltime, playerintime, playercontentdesc, playernum,
							playerzantype,  playerfrom, playerfromid, playerfromurl,playeraddtime,bjuserid,playcontentshareurl,ContentFavorite,ContentId,localurl);	
					dbdao.deleteHistory(playerurl);
					dbdao.addHistory(history);
					MainActivity.change();
					HomeActivity.UpdateViewPager();
					PlayerFragment.SendTextRequest(list.get(groupPosition).getList().get(childPosition).getContentName(),context.getApplicationContext());
			     	context.finish();
				} else if (MediaType!=null&&MediaType.equals("SEQU")) {	
					Intent intent = new Intent(context, AlbumActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("type", "search");
					bundle.putSerializable("list", list.get(groupPosition).getList().get(childPosition));
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					ToastUtils.show_short(context, "暂不支持的Type类型");
				}
				return true;
			}
		});
	}

	// 广播接收器
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
		super.onDestroy();
		if (null != rootView) {
			((ViewGroup) rootView.getParent()).removeView(rootView);   
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		ex_listview = null;
		dbdao = null;
		context.unregisterReceiver(mBroadcastReceiver);
		rootView = null;
		context = null;
		dialog = null;
		playlist = null;
		sequlist = null;
		ttslist = null;
		radiolist = null;
		list = null;
		SubList = null;
		searchadapter = null;
		mintent = null;
		searchstr = null;
		tag = null;
	}
}
