package com.woting.ui.home.program.tuijian.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.home.program.tuijian.adapter.RecommendListAdapter;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 猜你喜欢  更多列表
 * @author woting11
 */
public class RecommendLikeListActivity extends BaseActivity implements OnClickListener {

	private LinearLayout head_left_btn;		// 返回
	private XListView mListView;			// 列表
	private Dialog dialog;					// 加载对话框
	protected List<RankInfo> RankList;
	private int page = 1;					// 页码
	private int RefreshType;				// refreshType 1为下拉加载 2为上拉加载更多
	private ArrayList<RankInfo> newList = new ArrayList<>();
	protected List<RankInfo> SubList;
	private int pageSizeNum;
	private RecommendLikeListActivity context;
	private SearchPlayerHistoryDao dbDao;	// 数据库
	private String ReturnType;
	private RecommendListAdapter adapterLikeList;
	private String tag = "RECOMMEND_LIKE_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	private int pageSize;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend_like_list_layout);
		context = this;
		RefreshType = 1;
		setView();
		setListener();
		initDao();
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
			sendRequest();
		} else {
			ToastUtils.show_short(this, "网络连接失败，请稍后重试");
		}
	}

	/*
	 * 请求网络数据
	 */
	private void sendRequest(){
		VolleyRequest.RequestPost(GlobalConfig.getContentUrl, tag, setParam(), new VolleyCallback() {
			private String ResultList;
			private String StringSubList;
			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				page++;
				try {
					ReturnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null) {
					if (ReturnType.equals("1001")) {
						// 获取列表
						try {
							ResultList = result.getString("ResultList");
							JSONTokener jsonParser = new JSONTokener(ResultList);
							JSONObject arg1 = (JSONObject) jsonParser.nextValue();
							StringSubList = arg1.getString("List");
							String pageSizeTemp = arg1.getString("PageSize");
							String AllCount = arg1.getString("AllCount");
							pageSizeNum = Integer.valueOf(pageSizeTemp);
							if(Integer.valueOf(pageSizeTemp) < 10){
								mListView.stopLoadMore();
								mListView.setPullLoadEnable(false);
							}else{
								mListView.setPullLoadEnable(true);
							}
							if (AllCount != null && !AllCount.equals("") && pageSizeTemp != null && !pageSizeTemp.equals("")) {
								int allcount = Integer.valueOf(AllCount);
								pageSize = Integer.valueOf(pageSizeTemp);
								// 先求余 如果等于0 最后结果不加1 如果不等于0 结果加一
								if (allcount % pageSize == 0) {
									pageSizeNum = allcount / pageSize;
								} else {
									pageSizeNum = allcount / pageSize + 1;
								}
							} else {
								ToastUtils.show_allways(context, "页码获取异常");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						SubList = new Gson().fromJson(StringSubList, new TypeToken<List<RankInfo>>() {}.getType());
						if (RefreshType == 1) {
							newList.clear();
							newList.addAll(SubList);
							if (adapterLikeList == null) {
								adapterLikeList = new RecommendListAdapter(context, newList, false);
								mListView.setAdapter(adapterLikeList);
							} else {
								adapterLikeList.notifyDataSetChanged();
							}
							mListView.stopRefresh();
						}
						if (RefreshType == 2) {
							mListView.stopLoadMore();
							newList.addAll(SubList);
							adapterLikeList.notifyDataSetChanged();
						}
						setOnItem();
					} else {
						if (ReturnType.equals("0000")) {
							ToastUtils.show_short(context, "无法获取相关的参数");
						} else if (ReturnType.equals("1002")) {
							ToastUtils.show_short(context, "无此分类信息");
						} else if (ReturnType.equals("1003")) {
							ToastUtils.show_short(context, "无法获得列表");
						} else if (ReturnType.equals("1011")) {
							ToastUtils.show_short(context, "列表为空（列表为空[size==0]");
						}

						// 无论何种返回值，都需要终止掉上拉刷新及下拉加载的滚动状态
						if (RefreshType == 1) {
							mListView.stopRefresh();
						} else {
							mListView.stopLoadMore();
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
			jsonObject.put("MediaType", "");
			jsonObject.put("CatalogType", "-1");// 001为一个结果 002为另一个
			jsonObject.put("CatalogId", "");
			jsonObject.put("Page", String.valueOf(page));
			jsonObject.put("PerSize", "3");
			jsonObject.put("ResultType", "3");
			jsonObject.put("PageSize", "10");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	private void setOnItem() {
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
						String playcontentshareurl=newList.get(position-1).getContentShareURL();
						String playermediatype = newList.get(position - 1).getMediaType();
						String plaplayeralltime = "0";
						String playerintime = "0";
						String playercontentdesc = newList.get(position - 1).getCurrentContent();
						String playernum = newList.get(position - 1).getWatchPlayerNum();
						String playerzantype = "0";
						String playerfrom = newList.get(position - 1).getContentPub();
						String playerfromid = "";
						String playerfromurl = "";
						String playeraddtime = Long.toString(System.currentTimeMillis());
						String bjuserid =CommonUtils.getUserId(context);
						String ContentFavorite= newList.get(position - 1).getContentFavorite();
						String ContentId= newList.get(position-1).getContentId();
						String localurl=newList.get(position-1).getLocalurl();

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

						HomeActivity.UpdateViewPager();
						PlayerFragment.SendTextRequest(newList.get(position - 1).getContentName(),context);
						finish();
					} else if (MediaType.equals("SEQU")) {
						Intent intent = new Intent(context, AlbumActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "radiolistactivity");
						bundle.putSerializable("list", newList.get(position - 1));
						intent.putExtras(bundle);
						startActivityForResult(intent, 1);
					} else {
						ToastUtils.show_short(context, "暂不支持的Type类型");
					}
				}
			}
		});
	}

	/*
	 * 初始化数据库命令执行对象
	 */
	private void initDao() {
		dbDao = new SearchPlayerHistoryDao(context);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 1:
				if (resultCode == 1) {
					finish();
				}
				break;
		}
	}

	/*
	 * 设置刷新、加载更多参数
	 */
	private void setListener() {
		head_left_btn.setOnClickListener(this);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					RefreshType = 1;
					page = 1;
					sendRequest();
				} else {
					ToastUtils.show_short(context, "网络失败，请检查网络");
				}
			}

			@Override
			public void onLoadMore() {
				if (page <= pageSizeNum) {
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						RefreshType = 2;
						sendRequest();
						ToastUtils.show_short(context, "正在请求" + page + "页信息");
					} else {
						ToastUtils.show_short(context, "网络失败，请检查网络");
					}
				} else {
					mListView.stopLoadMore();
					ToastUtils.show_short(context, "已经没有最新的数据了");
				}
			}
		});
	}

	/**
	 * 初始化界面
	 */
	private void setView() {
		mListView = (XListView) findViewById(R.id.listview_fm);
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.head_left_btn:
				finish();
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		head_left_btn = null;
		mListView = null;
		dialog = null;
		RankList = null;
		newList = null;
		SubList = null;
		context = null;
		ReturnType = null;
		adapterLikeList = null;
		tag = null;
		if(dbDao != null){
			dbDao.closedb();
			dbDao = null;
		}
		setContentView(R.layout.activity_null);
	}
}
