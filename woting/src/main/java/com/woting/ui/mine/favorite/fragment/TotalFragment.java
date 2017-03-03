package com.woting.ui.mine.favorite.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.home.search.adapter.SearchContentAdapter;
import com.woting.ui.home.search.model.SuperRankInfo;
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.favorite.activity.FavoriteActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 我喜欢的 全部界面
 */
public class TotalFragment extends Fragment implements OnClickListener, TipView.WhiteViewClick {
	private FragmentActivity context;
    private SearchContentAdapter searchAdapter;
    private SearchPlayerHistoryDao dbDao;

	private ArrayList<RankInfo> playList; 	// 节目list
	private ArrayList<RankInfo> sequList; 	// 专辑list
	private ArrayList<RankInfo> ttsList; 	// tts
	private ArrayList<RankInfo> radioList; 	// radio
	private ArrayList<SuperRankInfo> list = new ArrayList<>();// 返回的节目list，拆分之前的list
	private List<RankInfo> subList;
	private List<String> delList;

    private View rootView;
    private Dialog delDialog;
    private Dialog dialog;
    private ExpandableListView expandListView;
    private TipView tipView;// 没有网络、没有数据提示

	private int delChildPosition = -1;
	private int delGroupPosition = -1;
	private String tag = "TOTAL_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
    public static boolean isData;// 是否有数据

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取全部喜欢信息");
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
            ((FavoriteActivity) context).setQkVisibleOrHide(false);
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		initDao();
        delDialog();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(FavoriteActivity.VIEW_UPDATE);
        context.registerReceiver(mBroadcastReceiver, mFilter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_favorite_total, container, false);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);
            expandListView = (ExpandableListView) rootView.findViewById(R.id.ex_listview);
            expandListView.setGroupIndicator(null);
			setListener();

			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				dialog = DialogUtils.Dialogph(context, "正在获取全部喜欢信息");
				send();
			} else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
                ((FavoriteActivity) context).setQkVisibleOrHide(false);
			}
		}
		return rootView;
	}

	// 初始化数据库
	private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
	}

	// 控件点击事件监听
	private void setListener() {
        expandListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				FavoriteActivity.updateViewPager(list.get(groupPosition).getKey());
				return true;
			}
		});

		// 长按删除喜欢
        expandListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View childView, int flatPos, long id) {
				if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
					long packedPos = ((ExpandableListView) parent).getExpandableListPosition(flatPos);
                    delGroupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                    delChildPosition = ExpandableListView.getPackedPositionChild(packedPos);
					if (delGroupPosition != -1 && delChildPosition != -1) {
                        delDialog.show();
					}
					return true;
				}
				return false;
			}
		});
	}

	// 长按单条删除数据对话框
	private void delDialog() {
		final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);// 取消
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);// 删除
		TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("确定?");

        delDialog = new Dialog(context, R.style.MyDialog);
        delDialog.setContentView(dialog1);
        delDialog.setCanceledOnTouchOutside(false);
        delDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancle:
                delDialog.dismiss();
                break;
            case R.id.tv_confirm:
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialogph(context, "正在删除");
                    if (delList == null) {
                        delList = new ArrayList<>();
                    }
                    String type = list.get(delGroupPosition).getList().get(delChildPosition).getMediaType();
                    String contentId = list.get(delGroupPosition).getList().get(delChildPosition).getContentId();
                    delList.add(type + "::" + contentId);
                    sendRequest();
                } else {
                    ToastUtils.show_always(context, "网络失败，请检查网络");
                }
                delDialog.dismiss();
                break;
        }
    }

	// 执行删除单条喜欢的方法
	protected void sendRequest() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			String s = delList.toString();
			jsonObject.put("DelInfos", s.substring(1, s.length() - 1).replaceAll(" ", ""));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.requestPost(GlobalConfig.delFavoriteListUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;

			@Override
			protected void requestSuccess(JSONObject result) {
				delList.clear();
				if(isCancelRequest) return ;
				try {
					ReturnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
                    context.sendBroadcast(new Intent(FavoriteActivity.VIEW_UPDATE));
                    send();
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

	// 请求网络获取数据
	private void send() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("PageSize", "12");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.requestPost(GlobalConfig.getFavoriteListUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) dialog.dismiss();
				if(isCancelRequest) return ;
				try {
					ReturnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
						subList = new Gson().fromJson(arg1.getString("FavoriteList"), new TypeToken<List<RankInfo>>() {}.getType());
					} catch (Exception e) {
						e.printStackTrace();
					}
					list.clear();
					if(playList != null) playList.clear();
					if(sequList != null) sequList.clear();
                    if(ttsList != null) ttsList.clear();
                    if(radioList != null) radioList.clear();

					if (subList != null && subList.size() > 0) {
						for (int i = 0; i < subList.size(); i++) {
							if (subList.get(i).getMediaType() != null && !subList.get(i).getMediaType().equals("")) {
								if (subList.get(i).getMediaType().equals("AUDIO")) {
									if (playList == null) {
										playList = new ArrayList<>();
										playList.add(subList.get(i));
									} else {
										if (playList.size() < 3) {
											playList.add(subList.get(i));
										}
									}
								} else if (subList.get(i).getMediaType().equals("SEQU")) {
									if (sequList == null) {
										sequList = new ArrayList<>();
										sequList.add(subList.get(i));
									} else {
										if (sequList.size() < 3) {
											sequList.add(subList.get(i));
										}
									}
								} else if (subList.get(i).getMediaType().equals("TTS")) {
									if (ttsList == null) {
										ttsList = new ArrayList<>();
										ttsList.add(subList.get(i));
									} else {
										if (ttsList.size() < 3) {
											ttsList.add(subList.get(i));
										}
									}
								} else if (subList.get(i).getMediaType().equals("RADIO")) {
									if (radioList == null) {
										radioList = new ArrayList<>();
										radioList.add(subList.get(i));
									} else {
										if (radioList.size() < 3) {
											radioList.add(subList.get(i));
										}
									}
								}
							}
						}
						if (sequList != null && sequList.size() != 0) {
							SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
							mSuperRankInfo1.setKey(sequList.get(0).getMediaType());
							mSuperRankInfo1.setList(sequList);
							list.add(mSuperRankInfo1);
						}
						if (playList != null && playList.size() != 0) {
							SuperRankInfo mSuperRankInfo = new SuperRankInfo();
							mSuperRankInfo.setKey(playList.get(0).getMediaType());
							mSuperRankInfo.setList(playList);
							list.add(mSuperRankInfo);
						}
						if (ttsList != null && ttsList.size() != 0) {
							SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
							mSuperRankInfo1.setKey(ttsList.get(0).getMediaType());
							mSuperRankInfo1.setList(ttsList);
							list.add(mSuperRankInfo1);
						}
						if (radioList != null && radioList.size() != 0) {
							SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
							mSuperRankInfo1.setKey(radioList.get(0).getMediaType());
							mSuperRankInfo1.setList(radioList);
							list.add(mSuperRankInfo1);
						}
						if (list.size() != 0) {
                            searchAdapter = new SearchContentAdapter(context, list);
                            expandListView.setAdapter(searchAdapter);
							for (int i = 0; i < list.size(); i++) {
                                expandListView.expandGroup(i);
							}
                            setItemListener();
                            tipView.setVisibility(View.GONE);
                            isData = true;
                            ((FavoriteActivity) context).setQkVisibleOrHide(true);
						} else {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有喜欢的节目\n快去收听喜欢的节目吧");
                            isData = false;
                            ((FavoriteActivity) context).setQkVisibleOrHide(false);
						}
					}
				} else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有喜欢的节目\n快去收听喜欢的节目吧");
                    isData = false;
                    ((FavoriteActivity) context).setQkVisibleOrHide(false);
				}
			}
			
			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) dialog.dismiss();
                isData = false;
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
                ((FavoriteActivity) context).setQkVisibleOrHide(false);
			}
		});
	}

	// ExpandableListView Item 点击事件监听
	protected void setItemListener() {
        expandListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				String mediaType = null;
				try {
                    mediaType = list.get(groupPosition).getList().get(childPosition).getMediaType();
				} catch (Exception e) {
					e.printStackTrace();
				}
                if(mediaType == null) {
                    return true;
                }
				if (mediaType.equals("RADIO") || mediaType.equals("AUDIO") || mediaType.equals("TTS")) {
					String playername = list.get(groupPosition).getList().get(childPosition).getContentName();
					String playerimage = list.get(groupPosition).getList().get(childPosition).getContentImg();
					String playerurl = list.get(groupPosition).getList().get(childPosition).getContentPlay();
					String playerurI = list.get(groupPosition).getList().get(childPosition).getContentURI();
					String playermediatype = list.get(groupPosition).getList().get(childPosition).getMediaType();
					String plaplayeralltime =list.get(groupPosition).getList().get(childPosition).getContentTimes();
					String playerintime = "0";
					String playercontentdesc = list.get(groupPosition).getList().get(childPosition).getContentDescn();
					String playernum = list.get(groupPosition).getList().get(childPosition).getPlayCount();
					String playerzantype = "0";
					String playerfrom = list.get(groupPosition).getList().get(childPosition).getContentPub();
					String playerfromid = "";
					String playerfromurl = "";
					String playeraddtime = Long.toString(System.currentTimeMillis());
					String bjuserid = CommonUtils.getUserId(context);
					String playcontentshareurl = list.get(groupPosition).getList().get(childPosition).getContentShareURL();
					String ContentFavorite = list.get(groupPosition).getList().get(childPosition).getContentFavorite();
					String ContentId = list.get(groupPosition).getList().get(childPosition).getContentId();
					String localurl = list.get(groupPosition).getList().get(childPosition).getLocalurl();

					String sequName=list.get(groupPosition).getList().get(childPosition).getSequName();
					String sequId=list.get(groupPosition).getList().get(childPosition).getSequId();
					String sequDesc=list.get(groupPosition).getList().get(childPosition).getSequDesc();
					String sequImg=list.get(groupPosition).getList().get(childPosition).getSequImg();
					String ContentPlayType= list.get(groupPosition).getList().get(childPosition).getContentPlayType();
					String IsPlaying=list.get(groupPosition).getList().get(childPosition).getIsPlaying();

					// 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
					PlayerHistory history = new PlayerHistory(
							playername,  playerimage, playerurl, playerurI,playermediatype,
							plaplayeralltime, playerintime, playercontentdesc, playernum,
							playerzantype,  playerfrom, playerfromid,playerfromurl, playeraddtime,bjuserid,playcontentshareurl,
							ContentFavorite,ContentId,localurl,sequName,sequId,sequDesc,sequImg,ContentPlayType,IsPlaying);
                    if(mediaType.equals("TTS")){
                        dbDao.deleteHistoryById(ContentId);
                    }else {
                        dbDao.deleteHistory(playerurl);
                    }
                    dbDao.addHistory(history);
					if (PlayerFragment.context != null) {
						MainActivity.change();
						HomeActivity.UpdateViewPager();
						Intent push=new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
						Bundle bundle1=new Bundle();
						bundle1.putString("text",list.get(groupPosition).getList().get(childPosition).getContentName());
						push.putExtras(bundle1);
						context.sendBroadcast(push);
						getActivity().finish();
					} else {
						Editor et = BSApplication.SharedPreferences.edit();
						et.putString(StringConstant.PLAYHISTORYENTER, "true");
						et.putString(StringConstant.PLAYHISTORYENTERNEWS, list.get(groupPosition).getList().get(childPosition).getContentName());
                        if(!et.commit()) {
                            Log.w("commit", "数据 commit 失败!");
                        }
						MainActivity.change();
						HomeActivity.UpdateViewPager();
						getActivity().finish();
					}
				} else if (mediaType.equals("SEQU")) {
					Intent intent = new Intent(context, AlbumActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("type", "search");
					bundle.putSerializable("list", list.get(groupPosition).getList().get(childPosition));
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
				}
				return true;
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if(resultCode==1){
				getActivity().finish();
			}
			break;
		}
	}

	// 广播接收器 用于刷新界面
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(FavoriteActivity.VIEW_UPDATE)) {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					send();
				} else {
					ToastUtils.show_always(context, "网络失败，请检查网络");
				}
			}
		}
	};

	// 获取当前页面选中的为选中的数目
	public int getdelitemsum() {
		int sum = 0;
		if (subList == null) {
			return sum;
		} else {
			sum = subList.size();
		}
		return sum;
	}

	// 删除
	public void delitem() {
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在删除");
			for (int i = 0; i < subList.size(); i++) {
				if (delList == null) {
					delList = new ArrayList<>();
				}
                String type = subList.get(i).getMediaType();
                String contentId = subList.get(i).getContentId();
                delList.add(type + "::" + contentId);
			}
			sendRequest();
		} else {
			ToastUtils.show_always(context, "网络连接失败，请检查网络!");
		}
	}

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
		context.unregisterReceiver(mBroadcastReceiver);
        expandListView = null;
		delDialog = null;
		rootView = null;
		context = null;
		playList = null;
		sequList = null;
		ttsList = null;
		radioList = null;
		list = null;
		subList = null;
		delList = null;
        searchAdapter = null;
		dialog = null;
		tag = null;
        isData = false;
		if(dbDao != null){
            dbDao.closedb();
            dbDao = null;
		}
	}
}
