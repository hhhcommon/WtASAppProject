package com.woting.activity.home.program.fmlist.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.home.main.HomeActivity;
import com.woting.activity.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.activity.home.player.main.fragment.PlayerFragment;
import com.woting.activity.home.player.main.model.PlayerHistory;
import com.woting.activity.home.program.diantai.model.RadioPlay;
import com.woting.activity.home.program.fmlist.adapter.RankInfoAdapter;
import com.woting.activity.home.program.fmlist.model.RankInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.MyActivityManager;
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
 * 电台列表
 * @author 辛龙
 *2016年8月8日
 */
public class FMListActivity extends Activity implements OnClickListener {
	private LinearLayout head_left_btn;
	private XListView mlistview;
	private Dialog dialog;
	private TextView mtextview_head;
	private FMListActivity context;
	private int ViewType = 1;
	private int page = 1;
	private int RefreshType;// refreshtype 1为下拉加载 2为上拉加载更多
	private int pagesizenum;
	private String CatalogName;
//	private String CatalogType;
	private String CatalogId;
	private ArrayList<RankInfo> newlist = new ArrayList<RankInfo>();
	protected RankInfoAdapter adapter;
	protected List<RankInfo> SubList;
	private SearchPlayerHistoryDao dbdao;
	private SharedPreferences shared;
	private String tag = "FMLIST_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fmlist);
		context = this;
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(this);
		shared = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		RefreshType = 1;
		setview();
		setListener();
		HandleRequestType();
		initDao();
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(FMListActivity.this, "正在获取数据", dialog);
			sendRequest();
		} else {
			ToastUtils.show_allways(this, "网络连接失败，请稍后重试");
		}
	}

	private void sendRequest(){
		VolleyRequest.RequestPost(GlobalConfig.getContentUrl, tag, setParam(), new VolleyCallback() {
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
				page++;
				try {
					ReturnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						ResultList = result.getString("ResultList");
						JSONTokener jsonParser = new JSONTokener(ResultList);
						JSONObject arg1 = (JSONObject) jsonParser.nextValue();
						StringSubList = arg1.getString("List");
						String pagesize = arg1.getString("PageSize");
						pagesizenum = Integer.valueOf(pagesize);
						SubList = new Gson().fromJson(StringSubList, new TypeToken<List<RankInfo>>() {}.getType());
						if (RefreshType == 1) {
							mlistview.stopRefresh();
							newlist.clear();
							newlist.addAll(SubList);
							adapter = new RankInfoAdapter(FMListActivity.this, newlist);
							mlistview.setAdapter(adapter);
						}else if (RefreshType == 2) {
							mlistview.stopLoadMore();
							newlist.addAll(SubList);
							adapter.notifyDataSetChanged();
						}
						setListView();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					mlistview.stopLoadMore();
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
			/*	jsonObject.put("CatalogId", CatalogId);*/
			jsonObject.put("MediaType", "RADIO");
			String cityid = shared.getString(StringConstant.CITYID, "110000");
			if(ViewType==1){
				//获取当前城市下所有分类内容
				jsonObject.put("CatalogId", cityid);
				jsonObject.put("CatalogType", "2");//
			}else{
				//按照分类获取内容
				JSONObject js=new JSONObject();
				jsonObject.put("CatalogType","1");
				jsonObject.put("CatalogId", CatalogId);
				js.put("CatalogType","2");
				js.put("CatalogId",cityid);
				jsonObject.put("FilterData", js);
			}
			jsonObject.put("PerSize", "3");
			jsonObject.put("ResultType", "3");
			jsonObject.put("PageSize", "10");
			jsonObject.put("Page", String.valueOf(page));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	private void initDao() {// 初始化数据库命令执行对象
		dbdao = new SearchPlayerHistoryDao(context);
	}

	// 这里要改
	protected void setListView() {
		mlistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(newlist != null && newlist.get(position-1) != null && newlist.get(position-1).getMediaType() != null){
					String MediaType = newlist.get(position-1).getMediaType();
					if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
						String playername = newlist.get(position-1).getContentName();
						String playerimage = newlist.get(position-1).getContentImg();
						String playerurl = newlist.get(position-1).getContentPlay();
						String playerurI = newlist.get(position-1).getContentURI();
						String playcontentshareurl=newlist.get(position-1).getContentShareURL();
						String playermediatype = newlist.get(position-1).getMediaType();
						String plaplayeralltime = "0";
						String playerintime = "0";
						String playercontentdesc = newlist.get(position-1).getCurrentContent();
						String playernum = newlist.get(position-1).getWatchPlayerNum();
						String playerzantype = "0";
						String playerfrom = "";
						String playerfromid = "";
						String playerfromurl = "";
						String playeraddtime = Long.toString(System.currentTimeMillis());
						String bjuserid =CommonUtils.getUserId(context);
						String ContentFavorite=newlist.get(position-1).getContentFavorite();
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
						PlayerFragment.SendTextRequest(newlist.get(position-1).getContentName(),context);
						finish();
					} 
				}
			}
		});
	}

	private void HandleRequestType() {
		String type = this.getIntent().getStringExtra("fromtype");
		String Position=this.getIntent().getStringExtra("Position");
		if(Position==null||Position.trim().equals("")){
			ViewType=1;
		}else{
			ViewType=-1;
		}
		RadioPlay list;
		if (type != null && type.trim().equals("online")) {
			CatalogName = this.getIntent().getStringExtra("name");
//			CatalogType = this.getIntent().getStringExtra("type");
			CatalogId = this.getIntent().getStringExtra("id");
		} else {
			list = (RadioPlay) this.getIntent().getSerializableExtra("list");
			CatalogName = list.getCatalogName();
//			CatalogType = list.getCatalogType();
			CatalogId = list.getCatalogId();
		}
		mtextview_head.setText(CatalogName);
	}

	private void setview() {
		mlistview = (XListView) findViewById(R.id.listview_fm);
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
		mtextview_head = (TextView) findViewById(R.id.head_name_tv);
	}

	private void setListener() {
		head_left_btn.setOnClickListener(this);
		// 设置上下拉参数
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
					ToastUtils.show_short(FMListActivity.this, "网络失败，请检查网络");
				}
			}

			@Override
			public void onLoadMore() {
				if (page <= pagesizenum) {
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						RefreshType = 2;
						sendRequest();
					} else {
						ToastUtils.show_short(FMListActivity.this, "网络失败，请检查网络");
					}
				} else {
					mlistview.stopLoadMore();
					ToastUtils.show_short(FMListActivity.this, "已经没有最新的数据了");
				}
			}
		});
	}

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
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		head_left_btn = null;
		mlistview = null;
		dialog = null;
		mtextview_head = null;
		if(dbdao != null){
			dbdao.closedb();
			dbdao = null;
		}
		newlist.clear();
		newlist = null;
		if(SubList != null){
			SubList.clear();
			SubList = null;
		}
		adapter = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
