package com.woting.activity.home.program.diantai.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.home.main.HomeActivity;
import com.woting.activity.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.activity.home.player.main.fragment.PlayerFragment;
import com.woting.activity.home.player.main.model.PlayerHistory;
import com.woting.activity.home.program.album.activity.AlbumActivity;
import com.woting.activity.home.program.citylist.activity.CityListActivity;
import com.woting.activity.home.program.diantai.adapter.citynewsadapter;
import com.woting.activity.home.program.diantai.adapter.onlineAdapter;
import com.woting.activity.home.program.diantai.model.RadioPlay;
import com.woting.activity.home.program.fmlist.activity.FMListActivity;
import com.woting.activity.home.program.fmlist.model.RankInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.CommonUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ToastUtils;
import com.woting.widgetui.pulltorefresh.PullToRefreshLayout;
import com.woting.widgetui.pulltorefresh.PullToRefreshLayout.OnRefreshListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 电台主页
 * 
 * @author 辛龙 2016年2月26日
 */
public class OnLineFragment extends Fragment {
	private FragmentActivity context;
	private View rootView;
	private String ReturnType;
	private onlineAdapter adapter;
	/* private ExpandableListView listView_main; */
	private List<RadioPlay> mainlist;
	private ExpandableListView listView_main;
	private int RefreshType;// refreshtype 1为下拉加载 2为上拉加载更多
	private int page = 1;// 数的问题
	private ArrayList<RadioPlay> newlist = new ArrayList<RadioPlay>();
	//	private int pagesizenum;
	private String BeginCatalogId;
	private View headview;
	private LinearLayout lin_address;
	private TextView tv_name;
	private LinearLayout lin_head_more;
	private GridView gridView;
	private List<RankInfo> mainlists;
	private SharedPreferences shared;
	private SearchPlayerHistoryDao dbdao;
	private String cityid;
	private String tag = "ONLINE_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	private PullToRefreshLayout mPullToRefreshLayout;
	private MessageReceiver Receiver;
	private String cityname;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		shared = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		initDao();// 初始化数据库命令执行对象

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_radio, container, false);
			listView_main = (ExpandableListView) rootView.findViewById(R.id.listView_main);
			headview = LayoutInflater.from(context).inflate(R.layout.head_online, null);
			lin_address = (LinearLayout) headview.findViewById(R.id.lin_address);
			tv_name = (TextView) headview.findViewById(R.id.tv_name);
			lin_head_more = (LinearLayout) headview.findViewById(R.id.lin_head_more);
			gridView = (GridView) headview.findViewById(R.id.gridView);
			// 取消默认selector
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			mPullToRefreshLayout=(PullToRefreshLayout)rootView.findViewById(R.id.refresh_view);
			setView();
			listView_main.addHeaderView(headview);
			if(Receiver==null){
				Receiver = new MessageReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction(BroadcastConstants.CITY_CHANGE);
				context.registerReceiver(Receiver, filter);
			}	
		}
		return rootView;
	}
	class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastConstants.CITY_CHANGE)) {
				if(GlobalConfig.CityName!=null){
					cityname=GlobalConfig.CityName;
				}
				tv_name.setText(cityname);
				page=1;
				BeginCatalogId="";
				RefreshType=1;
				getcity();
				send();
				Editor et = shared.edit();
				et.putString(StringConstant.CITYTYPE, "false");
				et.commit();
			}
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 发送网络请求
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			RefreshType = 1;
			BeginCatalogId = "";
			String cityname = shared.getString(StringConstant.CITYNAME, "北京");
			tv_name.setText(cityname);
			getcity();
			send();
		} else {
			//listView_main.stopLoadMore();
			mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
			ToastUtils.show_short(context, "网络失败，请检查网络");
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (null != rootView) {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
	}

	/**
	 * 初始化数据库命令执行对象
	 */
	private void initDao() {
		dbdao = new SearchPlayerHistoryDao(context);
	}

	private void setView() {
		lin_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, CityListActivity.class);
				startActivityForResult(intent, 0);
			}
		});

		lin_head_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mainlists != null) {
					String cityid = shared.getString(StringConstant.CITYID, "110000");
					String cityname = shared.getString(StringConstant.CITYNAME, "北京");
					Intent intent = new Intent(context, FMListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("fromtype", "online");
					bundle.putString("name", cityname);
					bundle.putString("type", "2");
					bundle.putString("id", cityid);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

		listView_main.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});

		listView_main.setGroupIndicator(null);
		mPullToRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					RefreshType = 1;
					page = 1;
					BeginCatalogId = "";
					send();
				} else {
					ToastUtils.show_short(context, "网络失败，请检查网络");
				}
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				//if (page <= pagesizenum) {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					RefreshType = 2;
					send();
					ToastUtils.show_short(context, "正在请求" + page + "页信息");
				} else {
					ToastUtils.show_short(context, "网络失败，请检查网络");
				}
			} 

		});

		listView_main.setSelector(new ColorDrawable(Color.TRANSPARENT));
	}

	private void getcity() {
		// 此处在splashActivity中refreshB设置成true
		cityid = shared.getString(StringConstant.CITYID, "110000");
		if(GlobalConfig.AdCode!=null&&!GlobalConfig.AdCode.equals("")){
			cityid=GlobalConfig.AdCode;
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			jsonObject.put("PCDType", GlobalConfig.PCDType);
			// 模块属性
			jsonObject.put("UserId", CommonUtils.getUserId(context));
			jsonObject.put("MediaType", "RADIO");
			jsonObject.put("CatalogType", "2");
			jsonObject.put("CatalogId", cityid);
			jsonObject.put("Page", "1");
			jsonObject.put("PerSize", "3");
			jsonObject.put("ResultType", "3");
			jsonObject.put("PageSize", "3");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.getContentUrl, tag, jsonObject, new VolleyCallback() {
			private citynewsadapter adapters;

			@Override
			protected void requestSuccess(JSONObject result) {
				if(isCancelRequest){
					return ;
				}
				try {
					ReturnType = result.getString("ReturnType");
					if (ReturnType != null && ReturnType.equals("1001")) {
						// 获取列表
						String ResultList = result.getString("ResultList");
						JSONTokener jsonParser = new JSONTokener(ResultList);
						JSONObject arg1 = (JSONObject) jsonParser.nextValue();
						String MainList = arg1.getString("List"); 
						mainlists = new Gson().fromJson(MainList, new TypeToken<List<RankInfo>>() {}.getType());
						if (adapters == null) {
							adapters = new citynewsadapter(context, mainlists);
							gridView.setAdapter(adapters);
						} else {
							adapters.notifyDataSetChanged();
						}
						gridlistener();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				// 请求错误信息已经在方法中统一打印了  这里就不需要重复打印
			}
		});
	}

	protected void gridlistener() {
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mainlists != null && mainlists.get(position) != null && mainlists.get(position).getMediaType() != null) {
					String MediaType = mainlists.get(position).getMediaType();
					if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
						String playername = mainlists.get(position).getContentName();
						String playerimage = mainlists.get(position).getContentImg();
						String playerurl = mainlists.get(position).getContentPlay();
						String playerurI = mainlists.get(position).getContentURI();
						String playermediatype = mainlists.get(position).getMediaType();
						String playcontentshareurl = mainlists.get(position).getContentShareURL();
						String plaplayeralltime = "0";
						String playerintime = "0";
						String playercontentdesc = mainlists.get(position).getCurrentContent();
						String playernum = mainlists.get(position).getWatchPlayerNum();
						String playerzantype = "0";
						String playerfrom = "";
						String playerfromid = "";
						String playerfromurl = "";
						String playeraddtime = Long.toString(System.currentTimeMillis());
						String bjuserid =CommonUtils.getUserId(context);
						String ContentFavorite=mainlists.get(position).getContentFavorite();
						String ContentId= mainlists.get(position).getContentId();
						String localurl=mainlists.get(position).getLocalurl();

						//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
						PlayerHistory history = new PlayerHistory( 
								playername,  playerimage, playerurl, playerurI,playermediatype, 
								plaplayeralltime, playerintime, playercontentdesc, playernum,
								playerzantype,  playerfrom, playerfromid,playerfromurl, playeraddtime,bjuserid,playcontentshareurl,ContentFavorite,ContentId,localurl);	
						dbdao.deleteHistory(playerurl);
						dbdao.addHistory(history);
						PlayerFragment.SendTextRequest(mainlists.get(position).getContentName(), context);
						HomeActivity.UpdateViewPager();
					}
				}
			}
		});
	}

	private void send() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(context);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			jsonObject.put("PCDType", GlobalConfig.PCDType);
			// 模块属性
			jsonObject.put("UserId", CommonUtils.getUserId(context));
			jsonObject.put("MediaType", "RADIO");
			jsonObject.put("CatalogType", "1");// 按地区分类
			JSONObject js=new JSONObject();
			js.put("CatalogType","2");
			js.put("CatalogId",cityid);
			jsonObject.put("FilterData", js);
			jsonObject.put("BeginCatalogId", BeginCatalogId);
			jsonObject.put("Page", String.valueOf(page));
			jsonObject.put("PerSize", "3");
			jsonObject.put("ResultType", "1");
			jsonObject.put("PageSize", "10");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.getContentUrl, tag, jsonObject, new VolleyCallback() {
			private String MainList;

			@Override
			protected void requestSuccess(JSONObject result) {
				if(isCancelRequest){
					return ;
				}
				String s=""+result.toString();
				page++;
				try {
					ReturnType = result.getString("ReturnType");
					if (ReturnType != null ) {
						if(ReturnType.equals("1001")){
							// 获取列表
							String ResultList = result.getString("ResultList");
							JSONTokener jsonParser = new JSONTokener(ResultList);
							JSONObject arg1 = (JSONObject) jsonParser.nextValue();
							MainList = arg1.getString("List");
							BeginCatalogId = arg1.getString("BeginCatalogId");
							mainlist = new Gson().fromJson(MainList, new TypeToken<List<RadioPlay>>() {}.getType());
							if (RefreshType == 1) {
								mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
								newlist.clear();
								newlist.addAll(mainlist);
								if (adapter == null) {
									adapter = new onlineAdapter(context, newlist);
									listView_main.setAdapter(adapter);
								} else {
									adapter.notifyDataSetChanged();
								}
							}
							if (RefreshType == 2) {
								mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
								newlist.addAll(mainlist);
								adapter.notifyDataSetChanged();
							}
							for (int i = 0; i < newlist.size(); i++) {
								listView_main.expandGroup(i);
							}
							setItemListener();
						}else{

						}
					} else {

						ToastUtils.show_allways(context, "暂无数据");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void requestError(VolleyError error) {

			}
		});
	}

	// 初始一号位置为0,0
	protected void setItemListener() {
		listView_main.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				if (newlist != null && newlist.get(groupPosition).getList().get(childPosition) != null
						&& newlist.get(groupPosition).getList().get(childPosition).getMediaType() != null) {
					String MediaType = newlist.get(groupPosition).getList().get(childPosition).getMediaType();
					if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
						String playername = newlist.get(groupPosition).getList().get(childPosition).getContentName();
						String playerimage = newlist.get(groupPosition).getList().get(childPosition).getContentImg();
						String playerurl = newlist.get(groupPosition).getList().get(childPosition).getContentPlay();
						String playerurI = newlist.get(groupPosition).getList().get(childPosition).getContentURI();
						String playermediatype = newlist.get(groupPosition).getList().get(childPosition).getMediaType();
						String playcontentshareurl = newlist.get(groupPosition).getList().get(childPosition).getContentShareURL();
						String plaplayeralltime = "0";
						String playerintime = "0";
						String playercontentdesc = newlist.get(groupPosition).getList().get(childPosition).getCurrentContent();
						String playernum = newlist.get(groupPosition).getList().get(childPosition).getWatchPlayerNum();
						String playerzantype = "0";
						String playerfrom = "";
						String playerfromid = "";
						String playerfromurl = "";
						String playeraddtime = Long.toString(System.currentTimeMillis());
						String bjuserid =CommonUtils.getUserId(context);
						String ContentFavorite=newlist.get(groupPosition).getList().get(childPosition).getContentFavorite();
						String ContentId=newlist.get(groupPosition).getList().get(childPosition).getContentId();
						String localurl=newlist.get(groupPosition).getList().get(childPosition).getLocalurl();

						//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
						PlayerHistory history = new PlayerHistory( 
								playername,  playerimage, playerurl, playerurI,playermediatype, 
								plaplayeralltime, playerintime, playercontentdesc, playernum,
								playerzantype,  playerfrom, playerfromid,playerfromurl, playeraddtime,bjuserid,playcontentshareurl,ContentFavorite,ContentId,localurl);	
						dbdao.deleteHistory(playerurl);
						dbdao.addHistory(history);
						HomeActivity.UpdateViewPager();
						PlayerFragment.SendTextRequest(newlist.get(groupPosition).getList().get(childPosition).getContentName(), context);

					} else if (MediaType.equals("SEQU")) {
						Intent intent = new Intent(context, AlbumActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "recommend");
						bundle.putSerializable("list", (Serializable) newlist.get(groupPosition).getList());
						intent.putExtras(bundle);
						startActivity(intent);
					} else {
						ToastUtils.show_short(context, "暂不支持的Type类型");
					}
				}
				return false;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		String citytype = shared.getString(StringConstant.CITYTYPE, "false");
		cityname = shared.getString(StringConstant.CITYNAME, "北京");
		cityid = shared.getString(StringConstant.CITYID, "110000");
		if(GlobalConfig.CityName!=null){
			cityname=GlobalConfig.CityName;
		}
		if (citytype != null && citytype.equals("true")) {
			tv_name.setText(cityname);
			page=1;
			BeginCatalogId="";
			RefreshType=1;
			getcity();
			send();
			Editor et = shared.edit();
			et.putString(StringConstant.CITYTYPE, "false");
			et.commit();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		context.unregisterReceiver(Receiver);
	}
}
