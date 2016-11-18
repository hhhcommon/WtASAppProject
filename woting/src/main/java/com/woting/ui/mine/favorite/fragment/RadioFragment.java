package com.woting.ui.mine.favorite.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.favorite.activity.FavoriteActivity;
import com.woting.ui.mine.favorite.adapter.FavorListAdapter;
import com.woting.ui.mine.favorite.adapter.FavorListAdapter.favorCheck;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 我喜欢的 电台界面
 */
public class RadioFragment extends Fragment {
	private FragmentActivity context;
    private FavorListAdapter adapter;
    private SearchPlayerHistoryDao dbDao;
    private List<RankInfo> subList;
    private List<String> delList;
    private ArrayList<RankInfo> newList = new ArrayList<>();
    
	private Dialog dialog;
    private View rootView;
    private View linearNull;
	private XListView mListView;
    
	private int page = 1;
	private int refreshType = 1;	// refreshType == 1 为下拉加载  == 2 为上拉加载更多
	private int pageSizeNum = -1;	// 先求余 如果等于 0 最后结果不加 1  如果不等于 0 结果加 1
	private String tag = "RADIO_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
        initDao();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(FavoriteActivity.VIEW_UPDATE);
        mFilter.addAction(FavoriteActivity.SET_NOT_LOAD_REFRESH);
        mFilter.addAction(FavoriteActivity.SET_LOAD_REFRESH);
        context.registerReceiver(mBroadcastReceiver, mFilter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_favorite_sound, container, false);
			linearNull = rootView.findViewById(R.id.linear_null);
            mListView = (XListView) rootView.findViewById(R.id.listView);
            mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            setView();
            send();
		}
		return rootView;
	}
	
	// 设置 View 隐藏
	public void setViewHint(){
		linearNull.setVisibility(View.GONE);
	}
	
	// 设置 View 可见  解决全选 Dialog 挡住 ListView 最底下一条 Item 问题
	public void setViewVisibility(){
		linearNull.setVisibility(View.VISIBLE);
	}

	// 初始化数据库
	private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
	}

	private void setListener() {
		adapter.setOnListener(new favorCheck() {
			@Override
			public void checkPosition(int position) {
				if (newList.get(position).getChecktype() == 0) {
					newList.get(position).setChecktype(1);
				} else {
					newList.get(position).setChecktype(0);
				}
				ifAll();
				adapter.notifyDataSetChanged();
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(FavoriteActivity.isEdit){
					if (newList.get(position - 1).getChecktype() == 0) {
						newList.get(position - 1).setChecktype(1);
					} else {
						newList.get(position - 1).setChecktype(0);
					}
					ifAll();
					adapter.notifyDataSetChanged();
				}else{
					if (newList != null && newList.get(position - 1) != null && newList.get(position - 1).getMediaType() != null) {
						String MediaType = newList.get(position - 1).getMediaType();
						if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
							String playername = newList.get(position - 1).getContentName();
							String playerimage = newList.get(position - 1).getContentImg();
							String playerurl = newList.get(position - 1).getContentPlay();
							String playerurI = newList.get(position - 1).getContentURI();
							String playermediatype = newList.get(position - 1).getMediaType();
							String playcontentshareurl = newList.get(position - 1).getContentShareURL();
							String plaplayeralltime = "0";
							String playerintime = "0";
							String playercontentdesc = newList.get(position - 1).getCurrentContent();
							String playernum = newList.get(position - 1).getWatchPlayerNum();
							String playerzantype = "0";
							String playerfrom = newList.get(position - 1).getContentPub();
							String playerfromid = "";
							String playerfromurl = "";
							String playeraddtime = Long.toString(System.currentTimeMillis());
							String bjuserid = CommonUtils.getUserId(context);
							String ContentFavorite = newList.get(position - 1).getContentFavorite();
							String ContentId = newList.get(position - 1).getContentId();
							String localurl = newList.get(position - 1).getLocalurl();

							String sequName= newList.get(position - 1).getSequName();
							String sequId= newList.get(position - 1).getSequId();
							String sequDesc= newList.get(position - 1).getSequDesc();
							String sequImg= newList.get(position - 1).getSequImg();

							// 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
							PlayerHistory history = new PlayerHistory(
									playername,  playerimage, playerurl, playerurI,playermediatype,
									plaplayeralltime, playerintime, playercontentdesc, playernum,
									playerzantype,  playerfrom, playerfromid,playerfromurl, playeraddtime,bjuserid,playcontentshareurl,
									ContentFavorite,ContentId,localurl,sequName,sequId,sequDesc,sequImg);
                            dbDao.deleteHistory(playerurl);
                            dbDao.addHistory(history);

							if (PlayerFragment.context != null) {
								MainActivity.change();
								HomeActivity.UpdateViewPager();
								PlayerFragment.SendTextRequest(newList.get(position - 1).getContentName(), context);
								getActivity().finish();
							} else {
								Editor et = BSApplication.SharedPreferences.edit();
								et.putString(StringConstant.PLAYHISTORYENTER, "true");
								et.putString(StringConstant.PLAYHISTORYENTERNEWS, newList.get(position - 1).getContentName());
								if(!et.commit()) {
                                    Log.w("commit", "数据 commit 失败!");
                                }
								MainActivity.change();
								HomeActivity.UpdateViewPager();
								getActivity().finish();
							}
						}
					}
				}
			}
		});
	}

	private void setView() {
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
                refreshType = 1;
                page = 1;
                send();
			}
			
			@Override
			public void onLoadMore() {
				if (page <= pageSizeNum) {
                    refreshType = 2;
                    send();
				} else {
					mListView.stopLoadMore();
					mListView.setPullLoadEnable(false);
					ToastUtils.show_allways(context, "已经是最后一页了");
				}
			}
		});
	}

	// 发送网络请求
	private void send() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            ToastUtils.show_allways(context, "网络连接失败，请检查网络连接!");
            if(refreshType == 1) {
                mListView.stopRefresh();
            } else {
                mListView.stopLoadMore();
            }
            return ;
        }
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("MediaType", "RADIO");
			jsonObject.put("Page", String.valueOf(page));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.getFavoriteListUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) dialog.dismiss();
				if(isCancelRequest) return ;
				page++;
				try {
					ReturnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
                    if(isDel){
                        ToastUtils.show_allways(context, "已删除");
                        isDel = false;
                    }
                    try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        subList = new Gson().fromJson(arg1.getString("FavoriteList"), new TypeToken<List<RankInfo>>() {}.getType());
                        try {
                            String allCountString = arg1.getString("AllCount");
                            String pageSizeString = arg1.getString("PageSize");
                            if (allCountString != null && !allCountString.equals("") && pageSizeString != null && !pageSizeString.equals("")) {
                                int allCountInt = Integer.valueOf(allCountString);
                                int pageSizeInt = Integer.valueOf(pageSizeString);
                                if(pageSizeInt < 10 || allCountInt < 10){
                                    mListView.stopLoadMore();
                                    mListView.setPullLoadEnable(false);
                                }else{
                                    mListView.setPullLoadEnable(true);
                                    if (allCountInt % pageSizeInt == 0) {
                                        pageSizeNum = allCountInt / pageSizeInt;
                                    } else {
                                        pageSizeNum = allCountInt / pageSizeInt + 1;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        
                        if (refreshType == 1) {
                            newList.clear();
                        }
                        newList.addAll(subList);
                        if (adapter == null) {
                            mListView.setAdapter(adapter = new FavorListAdapter(context, newList));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        setListener();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
				} else {
                    ToastUtils.show_allways(context, "获取列表失败，请检查网络或稍后重试!");
                }

                // 无论何种返回值，都需要终止掉上拉刷新及下拉加载的滚动状态
                if (refreshType == 1) {
                    mListView.stopRefresh();
                } else {
                    mListView.stopLoadMore();
                }
			}
			
			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
			}
		});
	}

	// 广播接收器
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
            switch (action) {
                case FavoriteActivity.VIEW_UPDATE:
                    page = 1;
                    send();
                    break;
                case FavoriteActivity.SET_NOT_LOAD_REFRESH:
                    if(isVisible()){
                        mListView.setPullRefreshEnable(false);
                        mListView.setPullLoadEnable(false);
                    }
                    break;
                case FavoriteActivity.SET_LOAD_REFRESH:
                    if(isVisible()){
                        mListView.setPullRefreshEnable(true);
                        if(newList.size() >= 10){
                            mListView.setPullLoadEnable(true);
                        }
                    }
                    break;
            }
		}
	};

	// 更改界面的view布局 让每个item都可以显示点选框
	public boolean changeviewtype(int type) {
		if (newList != null && newList.size() != 0) {
			for (int i = 0; i < newList.size(); i++) {
				newList.get(i).setViewtype(type);
			}
			if (type == 0) {
				for (int i = 0; i < newList.size(); i++) {
					newList.get(i).setChecktype(0);
				}
			}
			adapter.notifyDataSetChanged();
			return true;
		} else {
			return false;
		}
	}

	// 点击全选时的方法
	public void changechecktype(int type) {
		if (adapter != null) {
			for (int i = 0; i < newList.size(); i++) {
				newList.get(i).setChecktype(type);
			}
			adapter.notifyDataSetChanged();
		}
	}

	// 获取当前页面选中的为选中的数目
	public int getdelitemsum() {
		int sum = 0;
		for (int i = 0; i < newList.size(); i++) {
			if (newList.get(i).getChecktype() == 1) {
				sum++;
			}
		}
		return sum;
	}
	
	// 判断是否全部选择
	public void ifAll(){
		if(getdelitemsum() == newList.size()){
			Intent intentAll = new Intent();
			intentAll.setAction(FavoriteActivity.SET_ALL_IMAGE);
			context.sendBroadcast(intentAll);
		}else{
			Intent intentNotAll = new Intent();
			intentNotAll.setAction(FavoriteActivity.SET_NOT_ALL_IMAGE);
			context.sendBroadcast(intentNotAll);
		}
	}

	// 删除
	public void delitem() {
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在删除");
			for (int i = 0; i < newList.size(); i++) {
				if (newList.get(i).getChecktype() == 1) {
					if (delList == null) {
                        delList = new ArrayList<>();
						String type = newList.get(i).getMediaType();
						String contentid = newList.get(i).getContentId();
                        delList.add(type + "::" + contentid);
					} else {
						String type = newList.get(i).getMediaType();
						String contentid = newList.get(i).getContentId();
                        delList.add(type + "::" + contentid);
					}
				}
			}
            refreshType = 1;
            sendRequest();
		} else {
			ToastUtils.show_allways(context, "网络连接失败，请检查网络!");
		}
	}
	
	private boolean isDel;

	// 执行删除单条喜欢的方法
	protected void sendRequest() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			String s = delList.toString();
			jsonObject.put("DelInfos", s.substring(1, s.length() - 1).replaceAll(" ", ""));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.delFavoriteListUrl, tag, jsonObject, new VolleyCallback() {
            private String returnType;

			@Override
			protected void requestSuccess(JSONObject result) {
				isDel = true;
                delList.clear();
				if(isCancelRequest) return ;
				try {
                    returnType = result.getString("ReturnType");
                    String message = result.getString("Message");
                    Log.v("ReturnType", "ReturnType -- > " + returnType + " === Message -- > " + message);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (returnType != null && returnType.equals("1001")) {
					context.sendBroadcast(new Intent(FavoriteActivity.VIEW_UPDATE));
				} else {
                    ToastUtils.show_allways(context, "删除失败，请检查网络或稍后重试!");
				}
			}
			
			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                delList.clear();
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (null != rootView) {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		context.unregisterReceiver(mBroadcastReceiver);
		mListView = null;
		context = null;
		dialog = null;
        subList = null;
		newList = null;
		rootView = null;
		adapter = null;
        delList = null;
		linearNull = null;
		tag = null;
		if(dbDao != null){
            dbDao.closedb();
            dbDao = null;
		}
	}
}
