package com.woting.activity.home.program.tuijian.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

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
import com.woting.activity.home.program.tuijian.adapter.RecommendListAdapter;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;
import com.woting.widgetui.xlistview.XListView;
import com.woting.widgetui.xlistview.XListView.IXListViewListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 猜你喜欢  更多列表
 * @author woting11
 */
public class RecommendLikeListActivity extends Activity implements OnClickListener {
	private LinearLayout head_left_btn;		// 返回
	private XListView mlistview;			// 列表
	private Dialog dialog;					// 加载对话框
	protected List<RankInfo> RankList;
	private int page = 1;					// 页码
	private int RefreshType;				// refreshtype 1为下拉加载 2为上拉加载更多
	private ArrayList<RankInfo> newlist = new ArrayList<RankInfo>();
	protected List<RankInfo> SubList;
	private int pagesizenum;
	private RecommendLikeListActivity context;
	private SearchPlayerHistoryDao dbdao;	// 数据库
	private String ReturnType;
	private RecommendListAdapter adapterLikeList;
	private String tag = "RECOMMENDLIKE_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	private int pageSize;
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend_like_list_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		context = this;
		RefreshType = 1;
		setview();
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
							String pagesize = arg1.getString("PageSize");
							String AllCount = arg1.getString("AllCount");
							pagesizenum = Integer.valueOf(pagesize);
							if(Integer.valueOf(pagesize) < 10){
								mlistview.stopLoadMore();
								mlistview.setPullLoadEnable(false);
							}else{
								mlistview.setPullLoadEnable(true);
							}
							if (AllCount != null && !AllCount.equals("") && pagesize != null && !pagesize.equals("")) {
								int allcount = Integer.valueOf(AllCount);
								pageSize = Integer.valueOf(pagesize);
								// 先求余 如果等于0 最后结果不加1 如果不等于0 结果加一
								if (allcount % pageSize == 0) {
									pagesizenum = allcount / pageSize;
								} else {
									pagesizenum = allcount / pageSize + 1;
								}
							} else {
								ToastUtils.show_allways(context, "页码获取异常");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						SubList = new Gson().fromJson(StringSubList, new TypeToken<List<RankInfo>>() {}.getType());
						if (RefreshType == 1) {
							newlist.clear();
							newlist.addAll(SubList);
							if (adapterLikeList == null) {
								adapterLikeList = new RecommendListAdapter(context, newlist, false);
								mlistview.setAdapter(adapterLikeList);
							} else {
								adapterLikeList.notifyDataSetChanged();
							}
							mlistview.stopRefresh();
						}
						if (RefreshType == 2) {
							mlistview.stopLoadMore();
							newlist.addAll(SubList);
							adapterLikeList.notifyDataSetChanged();
						}
						setonitem();
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
							mlistview.stopRefresh();
						} else {
							mlistview.stopLoadMore();
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
	
	private void setonitem() {
		mlistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(newlist != null && newlist.get(position - 1) != null && newlist.get(position - 1).getMediaType() != null){
					String MediaType = newlist.get(position - 1).getMediaType();
					if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
						String playername = newlist.get(position - 1).getContentName();
						String playerimage = newlist.get(position - 1).getContentImg();
						String playerurl = newlist.get(position - 1).getContentPlay();
						String playerurI = newlist.get(position - 1).getContentURI();
						String playcontentshareurl=newlist.get(position-1).getContentShareURL();
						String playermediatype = newlist.get(position - 1).getMediaType();
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
						String ContentFavorite= newlist.get(position - 1).getContentFavorite();
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
						
						HomeActivity.UpdateViewPager();
						PlayerFragment.SendTextRequest(newlist.get(position - 1).getContentName(),context);
						finish();
					} else if (MediaType.equals("SEQU")) {
						Intent intent = new Intent(context, AlbumActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "radiolistactivity");
						bundle.putSerializable("list", newlist.get(position - 1));
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
		dbdao = new SearchPlayerHistoryDao(context);
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
		mlistview.setPullLoadEnable(true);
		mlistview.setPullRefreshEnable(true);
		mlistview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mlistview.setXListViewListener(new IXListViewListener() {

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
				if (page <= pagesizenum) {
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						RefreshType = 2;
						sendRequest();
						ToastUtils.show_short(context, "正在请求" + page + "页信息");
					} else {
						ToastUtils.show_short(context, "网络失败，请检查网络");
					}
				} else {
					mlistview.stopLoadMore();
					ToastUtils.show_short(context, "已经没有最新的数据了");
				}
			}
		});
	}

	/**
	 * 初始化界面
	 */
	private void setview() {
		mlistview = (XListView) findViewById(R.id.listview_fm);
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
		mlistview = null;
		dialog = null;
		RankList = null;
		newlist = null;
		SubList = null;
		context = null;
		ReturnType = null;
		adapterLikeList = null;
		tag = null;
		if(dbdao != null){
			dbdao.closedb();
			dbdao = null;
		}
		setContentView(R.layout.activity_null);
	}
}
