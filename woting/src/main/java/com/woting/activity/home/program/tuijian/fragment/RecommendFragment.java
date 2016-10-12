package com.woting.activity.home.program.tuijian.fragment;

import android.app.Dialog;
import android.content.Intent;
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
import com.woting.activity.home.program.radiolist.rollviewpager.RollPagerView;
import com.woting.activity.home.program.radiolist.rollviewpager.adapter.LoopPagerAdapter;
import com.woting.activity.home.program.radiolist.rollviewpager.hintview.IconHintView;
import com.woting.activity.home.program.tuijian.activity.RecommendLikeListActivity;
import com.woting.activity.home.program.tuijian.adapter.RecommendListAdapter;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.helper.ImageLoader;
import com.woting.util.CommonUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ToastUtils;
import com.woting.widgetui.xlistview.XListView;
import com.woting.widgetui.xlistview.XListView.IXListViewListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 节目页----推荐页
 * @author 辛龙
 *  2016年3月30日
 */
public class RecommendFragment extends Fragment {
	private FragmentActivity context;
	private View headview;
	private RecommendListAdapter adapter;
	private Dialog dialog;
	private String ReturnType;
	private List<RankInfo> SubList;
	private XListView mlistView;
	private int page = 1;
	private int RefreshType; // refreshtype 1为下拉加载 2为上拉加载更多
	private ArrayList<RankInfo> newlist = new ArrayList<RankInfo>();
	private boolean flag;
	private SearchPlayerHistoryDao dbdao;
	private View rootView;
	private String tag = "RECOMMEND_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	private RollPagerView mLoopViewPager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		flag = true; 	// 设置等待提示是否展示
		initDao(); 		// 初始化数据库命令执行对象
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
			mlistView = (XListView) rootView.findViewById(R.id.listView);
			headview = LayoutInflater.from(context).inflate(R.layout.headview_fragment_recommend, null);
			// SlideShowView slideshowView = (SlideShowView)
			// headview.findViewById(R.id.slideshowView);// slideshowView
			mlistView.addHeaderView(headview);
			mlistView.setSelector(new ColorDrawable(Color.TRANSPARENT));

			// 轮播图
			mLoopViewPager= (RollPagerView) headview.findViewById(R.id.slideshowView);
//	        mLoopViewPager.setPlayDelay(5000);
			mLoopViewPager.setAdapter(new LoopAdapter(mLoopViewPager));
			mLoopViewPager.setHintView(new IconHintView(context,R.mipmap.indicators_now,R.mipmap.indicators_default));
			
			// 发送网络请求
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				if (flag) {
					flag = false;
				}
				RefreshType = 1;
				page = 1;
				sendRequest();
			} else {
				ToastUtils.show_short(context, "网络失败，请检查网络");
			}

			headview.findViewById(R.id.linear_more).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, RecommendLikeListActivity.class);
					context.startActivity(intent);
				}
			});
		}
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setView();
	}

	/**
	 * 初始化数据库命令执行对象
	 */
	private void initDao() {
		dbdao = new SearchPlayerHistoryDao(context);
	}

	/**
	 * slideview的数据设置方法，可以通过本地发请求 直接给本页面所需的slideview设置图片的路径及其content资源
	 * 
	 * private void setslideshowView() { SlideShowView sv=new
	 * SlideShowView(context); sv.setImagesurl(); }
	 */
	private void setListener() {
		mlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (newlist != null && newlist.get(position - 2) != null && newlist.get(position - 2).getMediaType() != null) {
					String MediaType = newlist.get(position - 2).getMediaType();
					if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
						String playername = newlist.get(position - 2).getContentName();
						String playerimage = newlist.get(position - 2).getContentImg();
						String playerurl = newlist.get(position - 2).getContentPlay();
						String playerurI = newlist.get(position - 2).getContentURI();
						String playermediatype = newlist.get(position - 2).getMediaType();
						String playercontentshareurl = newlist.get(position - 2).getContentShareURL();
						String plaplayeralltime = "0";
						String playerintime = "0";
						String playercontentdesc = newlist.get(position - 2).getCurrentContent();
						String playernum = newlist.get(position - 2).getWatchPlayerNum();
						String playerzantype = "0";
						String playerfrom = "";
						String playerfromid = "";
						String playerfromurl = "";
						String playeraddtime = Long.toString(System.currentTimeMillis());
						String bjuserid = CommonUtils.getUserId(context);
						String ContentFavorite=newlist.get(position - 2).getContentFavorite();
						String ContentId= newlist.get(position-2).getContentId();
						String localurl=newlist.get(position-2).getLocalurl();
						
						//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
						PlayerHistory history = new PlayerHistory( 
								playername,  playerimage, playerurl, playerurI,playermediatype, 
								plaplayeralltime, playerintime, playercontentdesc, playernum,
								playerzantype,  playerfrom, playerfromid, playerfromurl,playeraddtime,bjuserid,playercontentshareurl,ContentFavorite,ContentId,localurl);	
						dbdao.deleteHistory(playerurl);
						dbdao.addHistory(history);
						HomeActivity.UpdateViewPager();
						PlayerFragment.SendTextRequest(newlist.get(position - 2).getContentName(), context);
					} else if (MediaType.equals("SEQU")) {
						Intent intent = new Intent(context, AlbumActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "recommend");
						bundle.putSerializable("list", newlist.get(position - 2));
						intent.putExtras(bundle);
						startActivity(intent);
					} else {
						ToastUtils.show_short(context, "暂不支持的Type类型");
					}
				}
			}
		});
	}

	private void setView() {
		mlistView.setPullRefreshEnable(true);
		mlistView.setPullLoadEnable(false);
		mlistView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					RefreshType = 1;
					page = 1;
					sendRequest();
				} else {
					mlistView.stopRefresh();
					ToastUtils.show_short(context, "网络失败，请检查网络");
				}
			}

			@Override
			public void onLoadMore() {
				
			}
		});
	}

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
						try {
							// 获取列表
							ResultList = result.getString("ResultList");
							JSONTokener jsonParser = new JSONTokener(ResultList);
							JSONObject arg1 = (JSONObject) jsonParser.nextValue();
							StringSubList = arg1.getString("List");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						SubList = new Gson().fromJson(StringSubList, new TypeToken<List<RankInfo>>() {}.getType());
						if (RefreshType == 1) {
							newlist.clear();
							for (int i = 0; i < SubList.size(); i++) {
								newlist.add(SubList.get(i));
								if (i == 4) {
									break;
								}
							}
							if (adapter == null) {
								adapter = new RecommendListAdapter(context, newlist, true);
								mlistView.setAdapter(adapter);
							} else {
								adapter.notifyDataSetChanged();
							}
							mlistView.stopRefresh();
							setListener();
						}
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
							mlistView.stopRefresh();
						} else {
							mlistView.stopLoadMore();
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
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (null != rootView) {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
	}
	
	private class LoopAdapter extends LoopPagerAdapter{
        public LoopAdapter(RollPagerView viewPager) {
			super(viewPager);
		}

		private int count = imgs.length;

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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		context = null;
		headview = null;
		adapter = null;
		dialog = null;
		ReturnType = null;
		SubList = null;
		mlistView = null;
		newlist = null;
		rootView = null;
		tag = null;
		if(dbdao != null){
			dbdao.closedb();
			dbdao = null;
		}
	}
}
