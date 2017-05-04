package com.woting.ui.musicplay.favorite.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.model.content;
import com.woting.ui.music.adapter.ContentForCheckAdapter;
import com.woting.ui.musicplay.more.PlayerMoreOperationActivity;
import com.woting.ui.musicplay.album.main.AlbumFragment;
import com.woting.ui.musicplay.favorite.main.FavoriteFragment;
import com.woting.ui.mine.main.MineActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 我喜欢的专辑界面
 */
public class SequFragment extends Fragment implements TipView.WhiteViewClick {
	private FragmentActivity context;
    private ContentForCheckAdapter adapter;
    private List<content> subList;
    private List<String> delList;
    private List<content> newList = new ArrayList<>();
    
	private Dialog dialog;
    private View rootView;
    private View linearNull;
	private XListView mListView;
    private TipView tipView;// 没有网络、没有数据提示
    
    private int page = 1;
    private int refreshType = 1;// refreshType == 1 为下拉加载  == 2 为上拉加载更多
    private String tag = "SEQU_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private boolean isDel;
    public static boolean isData;// 是否有数据

    @Override
    public void onWhiteViewClick() {
        send();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(FavoriteFragment.VIEW_UPDATE);
        mFilter.addAction(FavoriteFragment.SET_NOT_LOAD_REFRESH);
        mFilter.addAction(FavoriteFragment.SET_LOAD_REFRESH);
        context.registerReceiver(mBroadcastReceiver, mFilter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_favorite_sound, container, false);

            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);

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

    private void setListener() {
		adapter.setOnListener(new ContentForCheckAdapter.favorCheck() {
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
				if (FavoriteFragment.isEdit) {
					if (newList.get(position - 1).getChecktype() == 0) {
						newList.get(position - 1).setChecktype(1);
					} else {
						newList.get(position - 1).setChecktype(0);
					}
					ifAll();
					adapter.notifyDataSetChanged();
				} else {
					if (newList != null && newList.get(position - 1) != null && newList.get(position - 1).getMediaType() != null) {
                        AlbumFragment fragment = new AlbumFragment();
						Bundle bundle = new Bundle();
                        bundle.putInt(StringConstant.FROM_TYPE, FavoriteFragment.type);
						bundle.putString("id", newList.get(position-1).getContentId());
                        fragment.setArguments(bundle);
                        if (FavoriteFragment.type == IntegerConstant.TAG_MINE) {// Mine
                            MineActivity.open(fragment);
                        } else if (FavoriteFragment.type == IntegerConstant.TAG_MORE) {// PlayMore
                            PlayerMoreOperationActivity.open(fragment);
                        }
					}
				}
			}
		});
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode == 1){
                    getActivity().finish();
                }
                break;
        }
    }

	// 初始化视图
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
                refreshType = 2;
                send();
			}
		});
	}

	// 发送网络请求
	private void send() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if(dialog != null) dialog.dismiss();
            if(refreshType == 1) {
                mListView.stopRefresh();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
                isData = false;
            } else {
                mListView.stopLoadMore();
            }
            return ;
        }
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("MediaType", "SEQU");
			jsonObject.put("Page", String.valueOf(page));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.requestPost(GlobalConfig.getFavoriteListUrl, tag, jsonObject, new VolleyCallback() {
			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) dialog.dismiss();
				if(isCancelRequest) return ;
				try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        if(isDel){
                            ToastUtils.show_always(context, "已删除");
                            isDel = false;
                        }
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        subList = new Gson().fromJson(arg1.getString("FavoriteList"), new TypeToken<List<content>>() {}.getType());
                        if (subList != null && subList.size() >= 9) {
                            page++;
                            mListView.setPullLoadEnable(true);
                        } else {
                            mListView.setPullLoadEnable(false);
                        }
                        if (refreshType == 1) newList.clear();
                        newList.addAll(subList);
                        if (adapter == null) {
                            mListView.setAdapter(adapter = new ContentForCheckAdapter(context, newList));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        setListener();
                        tipView.setVisibility(View.GONE);
                        isData = true;
                    } else {
                        if(refreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有喜欢的节目\n快去收听喜欢的节目吧");
                            isData = false;
                        }
                    }
				} catch (Exception e) {
					e.printStackTrace();
                    if(refreshType == 1) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                        isData = false;
                    }
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
                if(refreshType == 1) {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    isData = false;
                } else {
                    ToastUtils.showVolleyError(context);
                }
			}
		});
	}

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
			intentAll.setAction(FavoriteFragment.SET_ALL_IMAGE);
			context.sendBroadcast(intentAll);
		}else{
			Intent intentNotAll = new Intent();
			intentNotAll.setAction(FavoriteFragment.SET_NOT_ALL_IMAGE);
			context.sendBroadcast(intentNotAll);
		}
	}

	// 删除
	public void delitem() {
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialog(context);
			for (int i = 0; i < newList.size(); i++) {
				if (newList.get(i).getChecktype() == 1) {
					if (delList == null) {
                        delList = new ArrayList<>();
					}
                    String type = newList.get(i).getMediaType();
                    String contentId = newList.get(i).getContentId();
                    delList.add(type + "::" + contentId);
				}
			}
            refreshType = 1;
            sendRequest();
		} else {
			ToastUtils.show_always(context, "网络失败，请检查网络");
		}
	}

	// 删除单条喜欢
	protected void sendRequest() {
		JSONObject jsonObject =VolleyRequest.getJsonObject(context);
		try {
			String s = delList.toString();
			jsonObject.put("DelInfos", s.substring(1, s.length() - 1).replaceAll(" ", ""));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.requestPost(GlobalConfig.delFavoriteListUrl, tag, jsonObject, new VolleyCallback() {
            private String returnType;

			@Override
			protected void requestSuccess(JSONObject result) {
				isDel = true;
                delList.clear();
				if(isCancelRequest) return ;
				try {
                    returnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (returnType != null && returnType.equals("1001")) {
					context.sendBroadcast(new Intent(FavoriteFragment.VIEW_UPDATE));
				} else {
					ToastUtils.show_always(context, "删除失败，请检查网络或稍后重试!");
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

	// 广播接收器
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
            switch (action) {
                case FavoriteFragment.VIEW_UPDATE:
                    page = 1;
                    send();
                    break;
                case FavoriteFragment.SET_NOT_LOAD_REFRESH:
                    if (isVisible()) {
                        mListView.setPullRefreshEnable(false);
                        mListView.setPullLoadEnable(false);
                    }
                    break;
                case FavoriteFragment.SET_LOAD_REFRESH:
                    if (isVisible()) {
                        mListView.setPullRefreshEnable(true);
                        if (newList.size() >= 10) {
                            mListView.setPullLoadEnable(true);
                        }
                    }
                    break;
            }
		}
	};

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
		mListView = null;
		context.unregisterReceiver(mBroadcastReceiver);
		context = null;
		dialog = null;
        subList = null;
		newList = null;
		rootView = null;
		adapter = null;
        delList = null;
		linearNull = null;
        isData = false;
		tag = null;
	}
}
