package com.woting.ui.home.program.diantai.fragment;

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

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.citylist.activity.CityListActivity;
import com.woting.ui.home.program.diantai.adapter.CityNewAdapter;
import com.woting.ui.home.program.diantai.adapter.OnLinesAdapter;
import com.woting.ui.home.program.diantai.model.RadioPlay;
import com.woting.ui.home.program.fmlist.activity.FMListActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.widgetui.pulltorefresh.PullToRefreshLayout;
import com.woting.common.widgetui.pulltorefresh.PullToRefreshLayout.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 电台主页
 *
 * @author 辛龙 2016年2月26日
 */
public class OnLineFragment extends Fragment {
	private FragmentActivity context;
	private View rootView;
	private String ReturnType;
	private OnLinesAdapter adapter;
	private List<RadioPlay> mainList;
	private ExpandableListView listView_Main;
	private int RefreshType;// refreshType 1为下拉加载 2为上拉加载更多
	private int page = 1;// 数的问题
	private ArrayList<RadioPlay> newList = new ArrayList<>();
	private String BeginCatalogId;
	private View headView;
	private LinearLayout lin_address;
	private TextView tv_Name;
	private LinearLayout lin_head_more;
	private GridView gridView;
	private List<RankInfo> mainLists;
	private SharedPreferences shared;
	private SearchPlayerHistoryDao dbDao;
	private String cityId;
	private String tag = "ONLINE_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	private PullToRefreshLayout mPullToRefreshLayout;
	private MessageReceiver Receiver;
	private String cityName;

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
			listView_Main = (ExpandableListView) rootView.findViewById(R.id.listView_main);
			headView = LayoutInflater.from(context).inflate(R.layout.head_online, null);
			lin_address = (LinearLayout) headView.findViewById(R.id.lin_address);
			tv_Name = (TextView) headView.findViewById(R.id.tv_name);
			lin_head_more = (LinearLayout) headView.findViewById(R.id.lin_head_more);
			gridView = (GridView) headView.findViewById(R.id.gridView);
			// 取消默认selector
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			mPullToRefreshLayout=(PullToRefreshLayout)rootView.findViewById(R.id.refresh_view);
			setView();
			listView_Main.addHeaderView(headView);
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
					cityName=GlobalConfig.CityName;
				}
				tv_Name.setText(cityName);
				page=1;
				BeginCatalogId="";
				RefreshType=1;
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					send();
					getCity();
				}else{
					ToastUtils.show_allways(context,"网络异常");
				}
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
			String cityName = shared.getString(StringConstant.CITYNAME, "北京");
			tv_Name.setText(cityName);
			getCity();
			send();
		} else {
		/*	mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);*/
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
		dbDao = new SearchPlayerHistoryDao(context);
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
				if (mainLists != null) {
					String cityId = shared.getString(StringConstant.CITYID, "110000");
					String cityName = shared.getString(StringConstant.CITYNAME, "北京");
					Intent intent = new Intent(context, FMListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("fromtype", "online");
					bundle.putString("name", cityName);
					bundle.putString("type", "2");
					bundle.putString("id", cityId);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

		listView_Main.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});

		listView_Main.setGroupIndicator(null);
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
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					RefreshType = 2;
					send();
					ToastUtils.show_short(context, "正在请求" + page + "页信息");
				} else {
					ToastUtils.show_short(context, "网络失败，请检查网络");
				}
			}

		});

		listView_Main.setSelector(new ColorDrawable(Color.TRANSPARENT));
	}

	private void getCity() {
		// 此处在splashActivity中refreshB设置成true
		cityId = shared.getString(StringConstant.CITYID, "110000");
		if(GlobalConfig.AdCode!=null&&!GlobalConfig.AdCode.equals("")){
			cityId=GlobalConfig.AdCode;
		}
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("MediaType", "RADIO");
			jsonObject.put("CatalogType", "2");
			jsonObject.put("CatalogId", cityId);
			jsonObject.put("Page", "1");
			jsonObject.put("PerSize", "3");
			jsonObject.put("ResultType", "3");
			jsonObject.put("PageSize", "3");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.getContentUrl, tag, jsonObject, new VolleyCallback() {
			private CityNewAdapter adapters;

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
						mainLists = new Gson().fromJson(MainList, new TypeToken<List<RankInfo>>() {}.getType());
						if (adapters == null) {
							adapters = new CityNewAdapter(context, mainLists);
							gridView.setAdapter(adapters);
						} else {
							adapters.notifyDataSetChanged();
						}
						gridListener();
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

	protected void gridListener() {
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mainLists != null && mainLists.get(position) != null && mainLists.get(position).getMediaType() != null) {
					String MediaType = mainLists.get(position).getMediaType();
					if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
						String playName =mainLists.get(position).getContentName();
						String playImage = mainLists.get(position).getContentImg();
						String playUrl = mainLists.get(position).getContentPlay();
						String playUri = mainLists.get(position).getContentURI();
						String playMediaType = mainLists.get(position).getMediaType();
						String playContentShareUrl = mainLists.get(position).getContentShareURL();
						String playAllTime = "0";
						String playInTime = "0";
						String playContentDesc = mainLists.get(position).getCurrentContent();
						String playerNum =mainLists.get(position).getPlayCount();
						String playZanType = "0";
						String playFrom = "";
						String playFromId = "";
						String playFromUrl = "";
						String playAddTime = Long.toString(System.currentTimeMillis());
						String bjUserId =CommonUtils.getUserId(context);
						String ContentFavorite=mainLists.get(position).getContentFavorite();
						String ContentId=mainLists.get(position).getContentId();
						String localUrl=mainLists.get(position).getLocalurl();

						String sequName=mainLists.get(position).getSequName();
						String sequId=mainLists.get(position).getSequId();
						String sequDesc=mainLists.get(position).getSequDesc();
						String sequImg=mainLists.get(position).getSequImg();

						//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
						PlayerHistory history = new PlayerHistory(
								playName, playImage,playUrl,playUri,playMediaType,
								playAllTime, playInTime, playContentDesc,playerNum,
								playZanType,playFrom,playFromId,playFromUrl,playAddTime,bjUserId,playContentShareUrl,
								ContentFavorite,ContentId,localUrl,sequName,sequId,sequDesc,sequImg);
						dbDao.deleteHistory(playUrl);
						dbDao.addHistory(history);
						PlayerFragment.SendTextRequest(mainLists.get(position).getContentName(), context);
						HomeActivity.UpdateViewPager();
					}
				}
			}
		});
	}

	private void send() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("MediaType", "RADIO");
			jsonObject.put("CatalogType", "1");// 按地区分类
			JSONObject js=new JSONObject();
			js.put("CatalogType","2");
			js.put("CatalogId",cityId);
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
							mainList = new Gson().fromJson(MainList, new TypeToken<List<RadioPlay>>() {}.getType());
							if (RefreshType == 1) {
								mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
								newList.clear();
								newList.addAll(mainList);
								if (adapter == null) {
									adapter = new OnLinesAdapter(context, newList);
									listView_Main.setAdapter(adapter);
								} else {
									adapter.notifyDataSetChanged();
								}
							}
							if (RefreshType == 2) {
								mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
								newList.addAll(mainList);
								adapter.notifyDataSetChanged();
							}
							for (int i = 0; i < newList.size(); i++) {
								listView_Main.expandGroup(i);
							}
							setItemListener();
						}else{
							ToastUtils.show_allways(context, "暂无数据");
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
		listView_Main.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				if (newList != null && newList.get(groupPosition).getList().get(childPosition) != null
						&& newList.get(groupPosition).getList().get(childPosition).getMediaType() != null) {
					String MediaType = newList.get(groupPosition).getList().get(childPosition).getMediaType();
					if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
						String playName = newList.get(groupPosition).getList().get(childPosition).getContentName();
						String playImage = newList.get(groupPosition).getList().get(childPosition).getContentImg();
						String playUrl = newList.get(groupPosition).getList().get(childPosition).getContentPlay();
						String playUri = newList.get(groupPosition).getList().get(childPosition).getContentURI();
						String playMediaType = newList.get(groupPosition).getList().get(childPosition).getMediaType();
						String playContentShareUrl = newList.get(groupPosition).getList().get(childPosition).getContentShareURL();
						String playAllTime = "0";
						String playInTime = "0";
						String playContentDesc = newList.get(groupPosition).getList().get(childPosition).getCurrentContent();
						String playerNum = newList.get(groupPosition).getList().get(childPosition).getPlayCount();
						String playZanType = "0";
						String playFrom = newList.get(groupPosition).getList().get(childPosition).getContentPub();
						String playFromId = "";
						String playFromUrl = "";
						String playAddTime = Long.toString(System.currentTimeMillis());
						String bjUserId =CommonUtils.getUserId(context);
						String ContentFavorite=newList.get(groupPosition).getList().get(childPosition).getContentFavorite();
						String ContentId=newList.get(groupPosition).getList().get(childPosition).getContentId();
						String localUrl=newList.get(groupPosition).getList().get(childPosition).getLocalurl();

						String sequName=newList.get(groupPosition).getList().get(childPosition).getSequName();
						String sequId=newList.get(groupPosition).getList().get(childPosition).getSequId();
						String sequDesc=newList.get(groupPosition).getList().get(childPosition).getSequDesc();
						String sequImg=newList.get(groupPosition).getList().get(childPosition).getSequImg();

						//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
						PlayerHistory history = new PlayerHistory(
								playName, playImage,playUrl,playUri,playMediaType,
								playAllTime, playInTime, playContentDesc,playerNum,
								playZanType,playFrom,playFromId,playFromUrl,playAddTime,bjUserId,playContentShareUrl,
								ContentFavorite,ContentId,localUrl,sequName,sequId,sequDesc,sequImg);

						dbDao.deleteHistory(playUrl);
						dbDao.addHistory(history);
						HomeActivity.UpdateViewPager();
						PlayerFragment.SendTextRequest(newList.get(groupPosition).getList().get(childPosition).getContentName(), context);

					} else if (MediaType.equals("SEQU")) {
						Intent intent = new Intent(context, AlbumActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "recommend");
						bundle.putSerializable("list", (Serializable) newList.get(groupPosition).getList());
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
		String cityType = shared.getString(StringConstant.CITYTYPE, "false");
		cityName = shared.getString(StringConstant.CITYNAME, "北京");
		cityId = shared.getString(StringConstant.CITYID, "110000");
		if(GlobalConfig.CityName!=null){
			cityName=GlobalConfig.CityName;
		}
		if (cityType != null && cityType.equals("true")) {
			tv_Name.setText(cityName);
			page=1;
			BeginCatalogId="";
			RefreshType=1;
			getCity();
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
