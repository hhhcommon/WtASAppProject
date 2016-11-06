package com.woting.ui.home.search.fragment;

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
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.home.search.activity.SearchLikeActivity;
import com.woting.ui.home.search.adapter.SearchContentAdapter;
import com.woting.ui.home.search.model.SuperRankInfo;
import com.woting.ui.main.MainActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class TotalFragment extends Fragment {
	private View rootView;
	private FragmentActivity context;
	private Dialog dialog;
	private ExpandableListView ex_ListView;
	private ArrayList<RankInfo> playList;// 节目list
	private ArrayList<RankInfo> sequList;// 专辑list
	private ArrayList<RankInfo> ttsList;//tts
	private ArrayList<RankInfo> radioList;//radio
	private ArrayList<SuperRankInfo> list = new ArrayList<>();// 返回的节目list，拆分之前的list
	private List<RankInfo> SubList;
	private SearchContentAdapter searchAdapter;
	private SearchPlayerHistoryDao dbDao;
	private Intent mIntent;
	protected String searchStr;
	private String tag = "TOTAL_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		initDao();
		mIntent = new Intent();
		mIntent.setAction(SearchLikeActivity.SEARCH_VIEW_UPDATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_favorite_total, container, false);
			ex_ListView=(ExpandableListView)rootView.findViewById(R.id.ex_listview);
			// 去除indicator
			ex_ListView.setGroupIndicator(null);
			setListener();
			IntentFilter mFilter = new IntentFilter();
			mFilter.addAction(SearchLikeActivity.SEARCH_VIEW_UPDATE);
			context.registerReceiver(mBroadcastReceiver,mFilter);
		}
		return rootView;
	}

	private void initDao() {
		dbDao = new SearchPlayerHistoryDao(context);
	}

	private void setListener() {
		//屏蔽group点击事件
		ex_ListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				SearchLikeActivity.updateViewPage(list.get(groupPosition).getKey());
				return true;
			}
		});
	}

	private void sendRequest(){
		VolleyRequest.RequestPost(GlobalConfig.getSearchByText, tag, setParam(), new VolleyCallback() {
			private String ReturnType;
			private String Message;
			private String resultList;
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
					ReturnType = result.getString("ReturnType");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						resultList = result.getString("ResultList");
						JSONTokener jsonParser = new JSONTokener(resultList);
						arg1 = (JSONObject) jsonParser.nextValue();
						StringSubList = arg1.getString("List");
						SubList = new Gson().fromJson(StringSubList,new TypeToken<List<RankInfo>>() {}.getType());
					} catch (Exception e) {

						e.printStackTrace();
					}
					list.clear();
					if (playList != null) {
						playList.clear();
					}
					if (sequList != null) {
						sequList.clear();
					}
					if (SubList.size() >= 0) {
						for (int i = 0; i < SubList.size(); i++) {
							if (SubList.get(i).getMediaType() != null && !SubList.get(i).getMediaType().equals("")) {
								if (SubList.get(i).getMediaType().equals("AUDIO")) {
									if (playList == null) {
										playList = new ArrayList<>();
										playList.add(SubList.get(i));
									} else {
										if(playList.size()<3){
											playList.add(SubList.get(i));
										}
									}
								} else if (SubList.get(i).getMediaType().equals("SEQU")) {
									if (sequList == null) {
										sequList = new ArrayList<>();
										sequList.add(SubList.get(i));
									} else {
										if(sequList.size()<3){
											sequList.add(SubList.get(i));
										}
									}
								}else if (SubList.get(i).getMediaType().equals("TTS")) {
									if (ttsList == null) {
										ttsList = new ArrayList<>();
										ttsList.add(SubList.get(i));
									} else {
										if(ttsList.size()<3){
											ttsList.add(SubList.get(i));
										}
									}
								}else if (SubList.get(i).getMediaType().equals("RADIO")) {
									if (radioList == null) {
										radioList = new ArrayList<>();
										radioList.add(SubList.get(i));
									} else {
										if(radioList.size()<3){
											radioList.add(SubList.get(i));
										}

									}
								}

							}
						}
						if (playList != null && !playList.equals("") && playList.size() != 0) {
							SuperRankInfo mSuperRankInfo = new SuperRankInfo();
							mSuperRankInfo.setKey(playList.get(0).getMediaType());
							//							if (playList.size() > 3) {
							//								List<RankInfo> list = new ArrayList<RankInfo>();
							//								for (int i = 0; i < 3; i++) {
							//									list.add(playList.get(i));
							//								}
							//								mSuperRankInfo.setList(list);
							//							} else {
							//								mSuperRankInfo.setList(playList);
							//							}
							mSuperRankInfo.setList(playList);
							list.add(mSuperRankInfo);
						}
						if (sequList != null && !sequList.equals("")&& sequList.size() != 0) {
							SuperRankInfo mSuperRankInfo1= new SuperRankInfo();
							mSuperRankInfo1.setKey(sequList.get(0).getMediaType());
							//不加限制
							//							if (sequList.size() > 3) {
							//								List<RankInfo> list = new ArrayList<RankInfo>();
							//								for (int i = 0; i < 3; i++) {
							//									list.add(sequList.get(i));
							//								}
							//								mSuperRankInfo1.setList(list);
							//							} else {
							//								mSuperRankInfo1.setList(sequList);
							//							}
							mSuperRankInfo1.setList(sequList);
							list.add(mSuperRankInfo1);
						}
						if (ttsList != null && !ttsList.equals("") && ttsList.size() != 0) {
							SuperRankInfo mSuperRankInfo1= new SuperRankInfo();
							mSuperRankInfo1.setKey(ttsList.get(0).getMediaType());
							//不加限制
							//							if (ttsList.size() > 3) {
							//								List<RankInfo> list = new ArrayList<RankInfo>();
							//								for (int i = 0; i < 3; i++) {
							//									list.add(ttsList.get(i));
							//								}
							//								mSuperRankInfo1.setList(list);
							//							} else {
							//								mSuperRankInfo1.setList(ttsList);
							//							}
							mSuperRankInfo1.setList(ttsList);
							list.add(mSuperRankInfo1);
						}
						if (radioList != null && !radioList.equals("") && radioList.size() != 0) {
							SuperRankInfo mSuperRankInfo1= new SuperRankInfo();
							mSuperRankInfo1.setKey(radioList.get(0).getMediaType());
							//不加限制
							//							if (radioList.size() > 3) {
							//								List<RankInfo> list = new ArrayList<RankInfo>();
							//								for (int i = 0; i < 3; i++) {
							//									list.add(radioList.get(i));
							//								}
							//								mSuperRankInfo1.setList(list);
							//							} else {
							//								mSuperRankInfo1.setList(radioList);
							//							}
							mSuperRankInfo1.setList(radioList);
							list.add(mSuperRankInfo1);
						}
						if (list.size() != 0) {
							searchAdapter = new SearchContentAdapter(context, list);
							ex_ListView.setAdapter(searchAdapter);
							for (int i = 0; i < list.size(); i++) {
								ex_ListView.expandGroup(i);
							}
							setItemListener();
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
					ex_ListView.setVisibility(View.GONE);
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
			if(searchStr != null && !searchStr.equals("")){
				jsonObject.put("SearchStr", searchStr);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	protected void setItemListener() {
		ex_ListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
				String MediaType = null;
				try {
					MediaType = list.get(groupPosition).getList().get(childPosition).getMediaType();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (MediaType!=null&&MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
					String playName=list.get(groupPosition).getList().get(childPosition).getContentName();
					String playImage =list.get(groupPosition).getList().get(childPosition).getContentImg();
					String playUrl = list.get(groupPosition).getList().get(childPosition).getContentPlay();
					String playUri =list.get(groupPosition).getList().get(childPosition).getContentURI();
					String playMediaType =list.get(groupPosition).getList().get(childPosition).getMediaType();
					String playContentShareUrl = list.get(groupPosition).getList().get(childPosition).getContentShareURL();
					String playAllTime = "0";
					String playInTime = "0";
					String playContentDesc = list.get(groupPosition).getList().get(childPosition).getCurrentContent();
					String playerNum =list.get(groupPosition).getList().get(childPosition).getWatchPlayerNum();
					String playZanType = "0";
					String playFrom =list.get(groupPosition).getList().get(childPosition).getContentPub();
					String playFromId = "";
					String playFromUrl = "";
					String playAddTime = Long.toString(System.currentTimeMillis());
					String bjUserId =CommonUtils.getUserId(context);
					String ContentFavorite=list.get(groupPosition).getList().get(childPosition).getContentFavorite();
					String ContentId=list.get(groupPosition).getList().get(childPosition).getContentId();
					String localUrl=list.get(groupPosition).getList().get(childPosition).getLocalurl();

					String sequName=list.get(groupPosition).getList().get(childPosition).getSequName();
					String sequId=list.get(groupPosition).getList().get(childPosition).getSequId();
					String sequDesc=list.get(groupPosition).getList().get(childPosition).getSequDesc();
					String sequImg=list.get(groupPosition).getList().get(childPosition).getSequImg();

					//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
					PlayerHistory history = new PlayerHistory(
							playName, playImage,playUrl,playUri,playMediaType,
							playAllTime, playInTime, playContentDesc,playerNum,
							playZanType,playFrom,playFromId,playFromUrl,playAddTime,bjUserId,playContentShareUrl,
							ContentFavorite,ContentId,localUrl,sequName,sequId,sequDesc,sequImg);
					dbDao.deleteHistory(playUrl);
					dbDao.addHistory(history);
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
			if (action.equals(SearchLikeActivity.SEARCH_VIEW_UPDATE)) {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					searchStr=intent.getStringExtra("searchStr");
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
		super.onDestroy();
		if (null != rootView) {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		ex_ListView = null;
		dbDao = null;
		context.unregisterReceiver(mBroadcastReceiver);
		rootView = null;
		context = null;
		dialog = null;
		playList = null;
		sequList = null;
		ttsList = null;
		radioList = null;
		list = null;
		SubList = null;
		searchAdapter = null;
		mIntent = null;
		searchStr = null;
		tag = null;
	}
}
