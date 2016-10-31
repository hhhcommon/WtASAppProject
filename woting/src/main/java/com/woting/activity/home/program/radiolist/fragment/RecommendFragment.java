package com.woting.activity.home.program.radiolist.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

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
import com.woting.activity.home.program.radiolist.activity.RadioListActivity;
import com.woting.activity.home.program.radiolist.adapter.RadioListAdapter;
import com.woting.activity.home.program.radiolist.rollviewpager.RollPagerView;
import com.woting.activity.home.program.radiolist.rollviewpager.adapter.LoopPagerAdapter;
import com.woting.activity.home.program.radiolist.rollviewpager.hintview.IconHintView;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.helper.ImageLoader;
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
 * 分类推荐列表
 * @author woting11
 */
public class RecommendFragment extends Fragment{
	private View rootView;
	private Context context;
	private XListView mlistview;			// 列表
	private Dialog dialog;					// 加载对话框
	private int page = 1;					// 页码
	private ArrayList<RankInfo> newlist = new ArrayList<RankInfo>();
	private int pagesizenum;
	private SearchPlayerHistoryDao dbdao;	// 数据库
	protected List<RankInfo> SubList;
	protected RadioListAdapter adapter;
	private int RefreshType;				// refreshtype 1为下拉加载 2为上拉加载更多
	private View headview;					// 头部视图
	private RollPagerView mLoopViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		initDao();
		RefreshType = 1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_radio_list_layout, container, false);
			headview = LayoutInflater.from(context).inflate(R.layout.headview_acitivity_radiolist, null);
			// 轮播图
			mLoopViewPager= (RollPagerView) headview.findViewById(R.id.slideshowView);
			//			mLoopViewPager.setPlayDelay(1000);
			mLoopViewPager.setAdapter(new LoopAdapter(mLoopViewPager));
			mLoopViewPager.setHintView(new IconHintView(context,R.mipmap.indicators_now,R.mipmap.indicators_default));
			mlistview = (XListView) rootView.findViewById(R.id.listview_fm);
			mlistview.addHeaderView(headview);
			setListener();
		}
		return rootView;
	}

	private boolean isFirst = true;

	/**
	 * 与onActivityCreated()方法 解决预加载问题 
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if(isVisibleToUser && adapter == null && getActivity() != null){
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				if(!isFirst){
					dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
				}
				sendRequest();
				isFirst = false;
			} else {
				ToastUtils.show_short(context, "网络连接失败，请稍后重试");
			}
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setUserVisibleHint(getUserVisibleHint());
	}

	private int pageSize;

	/**
	 * 请求网络数据
	 */
	public void sendRequest(){
		VolleyRequest.RequestPost(GlobalConfig.getContentUrl, setParam(), new VolleyCallback() {
			private String ResultList;
			private String StringSubList;
			private String ReturnType;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				((RadioListActivity)getActivity()).closeDialog();
				page++;
				try {
					ReturnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						// 获取列表
						ResultList = result.getString("ResultList");
						JSONTokener jsonParser = new JSONTokener(ResultList);
						JSONObject arg1 = (JSONObject) jsonParser.nextValue();
						StringSubList = arg1.getString("List");
						String pagesize = arg1.getString("PageSize");
						String Allcount = arg1.getString("AllCount");
						if(Integer.valueOf(pagesize) < 10){
							mlistview.stopLoadMore();
							mlistview.setPullLoadEnable(false);
						}else{
							mlistview.setPullLoadEnable(true);
						}
						if (Allcount != null && !Allcount.equals("") && pagesize != null && !pagesize.equals("")) {
							int allcount = Integer.valueOf(Allcount);
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
						mlistview.stopRefresh();
						newlist.clear();
						newlist.addAll(SubList);
						adapter = new RadioListAdapter(context, newlist);
						mlistview.setAdapter(adapter);
					} else if (RefreshType == 2) {
						mlistview.stopLoadMore();
						newlist.addAll(SubList);
						adapter.notifyDataSetChanged();
					}
					setonitem();
				} else {
					ToastUtils.show_allways(context, "暂没有该分类数据");
				}				
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) {
					dialog.dismiss();
				}
				((RadioListActivity)getActivity()).closeDialog();
			}
		});
	}

	private JSONObject setParam(){
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("MediaType", "");
			jsonObject.put("CatalogType", RadioListActivity.CatagoryType);
			jsonObject.put("CatalogId", RadioListActivity.id);
			jsonObject.put("Page", String.valueOf(page));
			jsonObject.put("PerSize", "3");
			jsonObject.put("ResultType", "2");
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
				if(newlist != null &&position>=2){
					if( newlist.get(position - 2) != null && newlist.get(position - 2).getMediaType() != null){
						String MediaType = newlist.get(position - 2).getMediaType();
						if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
							String playername = newlist.get(position - 2).getContentName();
							String playerimage = newlist.get(position - 2).getContentImg();
							String playerurl = newlist.get(position - 2).getContentPlay();
							String playerurI = newlist.get(position - 2).getContentURI();
							String playcontentshareurl=newlist.get(position - 2).getContentShareURL();
							String playermediatype = newlist.get(position - 2).getMediaType();
							String plaplayeralltime = "0";
							String playerintime = "0";
							String playercontentdesc = newlist.get(position - 2).getCurrentContent();
							String playernum = newlist.get(position - 2).getWatchPlayerNum();
							String playerzantype = "0";
							String playerfrom = "";
							String playerfromid = "";
							String playerfromurl = "";
							String playeraddtime = Long.toString(System.currentTimeMillis());
							String bjuserid =CommonUtils.getUserId(context);
							String ContentFavorite= newlist.get(position - 2).getContentFavorite();
							String ContentId= newlist.get(position - 2).getContentId();
							String localurl=newlist.get(position - 2).getLocalurl();
							String sequName=newlist.get(position-2).getSequName();
							String sequId=newlist.get(position-2).getSequId();
							String sequDesc=newlist.get(position-2).getSequDesc();
							String sequImg=newlist.get(position-2).getSequImg();

							//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
							PlayerHistory history = new PlayerHistory(
									playername,  playerimage, playerurl, playerurI,playermediatype,
									plaplayeralltime, playerintime, playercontentdesc, playernum,
									playerzantype,  playerfrom, playerfromid,playerfromurl, playeraddtime,bjuserid,playcontentshareurl,
									ContentFavorite,ContentId,localurl,sequName,sequId,sequDesc,sequImg);
							dbdao.deleteHistory(playerurl);
							dbdao.addHistory(history);

							HomeActivity.UpdateViewPager();
							PlayerFragment.SendTextRequest(newlist.get(position - 2).getContentName(),context);
							getActivity().finish();
						} else if (MediaType.equals("SEQU")) {
							Intent intent = new Intent(context, AlbumActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("type", "radiolistactivity");
							bundle.putSerializable("list", newlist.get(position - 2));
							intent.putExtras(bundle);
							startActivityForResult(intent, 1);
						} else {
							ToastUtils.show_short(context, "暂不支持的Type类型");
						}
					}
				}

			}
		});
	}

	/**
	 * 设置刷新、加载更多参数
	 */
	private void setListener() {
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
					mlistview.setPullLoadEnable(false);
					ToastUtils.show_short(context, "已经没有最新的数据了");
				}
			}
		});
	}

	/**
	 * 初始化数据库命令执行对象
	 */
	private void initDao() {
		dbdao = new SearchPlayerHistoryDao(context);
	}

	@Override
	public void onDestroyView() {
		super .onDestroyView(); 
		if (null != rootView) {
			((ViewGroup) rootView.getParent()).removeView(rootView);   
		}
	}

	private class LoopAdapter extends LoopPagerAdapter{
		private int count = imgs.length;

		public LoopAdapter(RollPagerView viewPager){
			super(viewPager);
		}

		@Override
		public View getView(ViewGroup container, int position) {
			ImageView view = new ImageView(container.getContext());
			view.setScaleType(ImageView.ScaleType.FIT_XY);
			view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			new ImageLoader(context).DisplayImage(imgs[position%count],view, false, false, null, null);
			return view;
		}

		@Override
		public int getRealCount() {
			return count;
		}
	}

	public String[] imgs = {
			"http://pic.500px.me/picurl/vcg5da48ce9497b91f9c81c17958d4f882e?code=e165fb4d228d4402",
					"http://pic.500px.me/picurl/49431365352e4e94936d4562a7fbc74a---jpg?code=647e8e97cd219143",
					"http://pic.500px.me/picurl/vcgd5d3cfc7257da293f5d2686eec1068d1?code=2597028fc68bd766",
					"http://pic.500px.me/picurl/vcg1aa807a1b8bd1369e4f983e555d5b23b?code=c0c4bb78458e5503",
	};
}
