package com.woting.activity.home.program.album.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.download.dao.FileInfoDao;
import com.woting.activity.download.model.FileInfo;
import com.woting.activity.download.service.DownloadService;
import com.woting.activity.home.main.HomeActivity;
import com.woting.activity.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.activity.home.player.main.fragment.PlayerFragment;
import com.woting.activity.home.player.main.model.PlayerHistory;
import com.woting.activity.home.program.album.activity.AlbumActivity;
import com.woting.activity.home.program.album.adapter.AlbumAdapter;
import com.woting.activity.home.program.album.adapter.AlbumMainAdapter;
import com.woting.activity.home.program.album.model.ContentInfo;
import com.woting.activity.main.MainActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 专辑列表页
 * 
 * @author woting11
 */
public class ProgramFragment extends Fragment implements OnClickListener {
	private View rootView;
	private Context context;
	private FileInfoDao FID;
	private SearchPlayerHistoryDao dbdao;
	private Dialog dialog;
	private ListView lv_album, lv_download; 		// 节目列表 下载列表
	private ImageView img_download, img_quanxuan; 	// 下载 全选
	private TextView tv_quxiao, tv_download, tv_sum, textTotal;
	private LinearLayout lin_quanxuan, lin_status2;
	private ImageView imageSort;					// 排序
	private ImageView imageSortDown;
	private AlbumMainAdapter mainadapter;
	private AlbumAdapter adapter;
	private List<ContentInfo> SubListAll = new ArrayList<ContentInfo>();
	private List<ContentInfo> urllist = new ArrayList<ContentInfo>();
	private List<ContentInfo> SubList; 				// 请求返回的网络数据值
	private List<FileInfo> flist;
	private boolean flag = false; 					// 标记全选的按钮
	private int sum = 0; 							// 计数项
	private String userid;
	private boolean isCancelRequest;
	private String tag = "PROGRAM_VOLLEY_REQUEST_CANCEL_TAG";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		userid = CommonUtils.getUserId(context);
		initDao();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_album_program, container, false);
			findView(rootView);
		}
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
			send();
		} else {
			ToastUtils.show_short(context, "网络失败，请检查网络");
		}
		return rootView;
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void findView(View view) {
		lv_album = (ListView) view.findViewById(R.id.lv_album); 			// 专辑显示界面
		img_download = (ImageView) view.findViewById(R.id.img_download);
		img_download.setOnClickListener(this);
		tv_quxiao = (TextView) view.findViewById(R.id.tv_quxiao); 			// 取消动画
		tv_quxiao.setOnClickListener(this);
		img_quanxuan = (ImageView) view.findViewById(R.id.img_quanxuan); 	// img_quanxuan
		lin_quanxuan = (LinearLayout) view.findViewById(R.id.lin_quanxuan); // lin_quanxuan
		lin_quanxuan.setOnClickListener(this);
		lv_download = (ListView) view.findViewById(R.id.lv_download); 		// lv_download
		tv_download = (TextView) view.findViewById(R.id.tv_download); 		// 开始下载
		tv_download.setOnClickListener(this);
		tv_sum = (TextView) view.findViewById(R.id.tv_sum); 				// 计数项
		lin_status2 = (LinearLayout) view.findViewById(R.id.lin_status2); 	// 第二种状态
		textTotal = (TextView) view.findViewById(R.id.text_total); 			// 下载列表的总计
		
		imageSort = (ImageView) view.findViewById(R.id.img_sort);			// 排序
		imageSort.setOnClickListener(this);
		
		imageSortDown = (ImageView) view.findViewById(R.id.img_sort_down);
		imageSortDown.setOnClickListener(this);
	}

	/**
	 * ListView 的 Item 的监听事件
	 */
	private void setListener() {
		lv_album.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (SubList != null && SubList.get(position) != null && SubList.get(position).getMediaType() != null) {
					String MediaType = SubList.get(position).getMediaType();
					if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
						String playername = SubList.get(position).getContentName();
						String playerimage = SubList.get(position).getContentImg();
						String playerurl = SubList.get(position).getContentPlay();
						String playerurI = SubList.get(position).getContentURI();
						String playermediatype = SubList.get(position).getMediaType();
						String playcontentshareurl = SubList.get(position).getContentShareURL();
						String contentid = SubList.get(position).getContentId();
						String plaplayeralltime = "0";
						String playerintime = "0";
						String playercontentdesc = SubList.get(position).getContentDesc();
						String playernum = SubList.get(position).getPlayCount();
						String playerzantype = "0";
						String playerfrom = "";
						String playerfromid = "";
						String playerfromurl = "";
						String playeraddtime = Long.toString(System.currentTimeMillis());
						String bjuserid = CommonUtils.getUserId(context);
						String ContentFavorite = SubList.get(position).getContentFavorite();
						String localurl=SubList.get(position).getLocalurl();
						//name id desc img
						String sequName=SubList.get(position).getSequname();
						String sequId=SubList.get(position).getSequid();
						String sequDesc=SubList.get(position).getSequdesc();
						String sequImg=SubList.get(position).getSequimgurl();

						// 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
						PlayerHistory history = new PlayerHistory(playername, playerimage, playerurl, playerurI,
								playermediatype, plaplayeralltime, playerintime, playercontentdesc, playernum,
								playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid,
								playcontentshareurl, ContentFavorite, contentid,localurl,sequName,sequId,sequDesc,sequImg);
						dbdao.deleteHistory(playerurl);
						dbdao.addHistory(history);
						if(PlayerFragment.context!=null){
							MainActivity.change();
							HomeActivity.UpdateViewPager();
							PlayerFragment.SendTextRequest(SubList.get(position).getContentName(), context);
						}else{
							SharedPreferences sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
							Editor et = sp.edit();
							et.putString(StringConstant.PLAYHISTORYENTER, "true");
							et.putString(StringConstant.PLAYHISTORYENTERNEWS,SubList.get(position).getContentName());
							et.commit();
							MainActivity.change();
							HomeActivity.UpdateViewPager();
						}
						getActivity().setResult(1);
						getActivity().finish();
					} else {
						ToastUtils.show_short(context, "暂不支持的Type类型");
					}
				}
			}
		});
	}

	/**
	 * 向服务器发送请求
	 */
	public void send() {
		VolleyRequest.RequestPost(GlobalConfig.getContentById, tag, setParam(), new VolleyCallback() {
			private String ReturnType;
			private String ResultList;
			private String StringSubList;
			private JSONObject arg1;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				try {
					ReturnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if (ReturnType != null) { // 根据返回值来对程序进行解析
					if (ReturnType.equals("1001")) {
						try {
							ResultList = result.getString("ResultInfo"); // 获取列表
							JSONTokener jsonParser = new JSONTokener(ResultList);
							arg1 = (JSONObject) jsonParser.nextValue();

							// 此处后期需要用typetoken将字符串StringSubList 转化成为一个list集合
							StringSubList = arg1.getString("SubList");
							Gson gson = new Gson();
							SubList = gson.fromJson(StringSubList, new TypeToken<List<ContentInfo>>() {}.getType());
							if (SubList != null && SubList.size() > 0) {
								SubListAll.clear();
								SubListAll.addAll(SubList);
								mainadapter = new AlbumMainAdapter(context, SubList);
								lv_album.setAdapter(mainadapter);
								setListener();
								getdate();
								adapter = new AlbumAdapter(context, SubListAll);
								lv_download.setAdapter(adapter);
								setinterface();
								textTotal.setText("共" + SubListAll.size() + "集");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					if (ReturnType.equals("0000")) {
						ToastUtils.show_allways(context, "无法获取相关的参数");
					} else if (ReturnType.equals("1002")) {
						ToastUtils.show_allways(context, "无此分类信息");
					} else if (ReturnType.equals("1003")) {
						ToastUtils.show_allways(context, "无法获得列表");
					} else if (ReturnType.equals("1011")) {
						ToastUtils.show_allways(context, "列表为空（列表为空[size==0]");
					} else if (ReturnType.equals("T")) {
						ToastUtils.show_allways(context, "获取列表异常");
					}
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
			jsonObject.put("MediaType", "SEQU");
			jsonObject.put("ContentId", AlbumActivity.id);
			jsonObject.put("Page", "1");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * 实现接口的方法
	 */
	private void setinterface() {
		lv_download.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (SubListAll != null && SubListAll.get(position) != null) {
					if (SubListAll.get(position).getCheckType() == 3) {
						ToastUtils.show_allways(context, "已经下载过");
					} else {
						if (SubListAll.get(position).getCheckType() == 1) {
							SubListAll.get(position).setCheckType(2);
						} else {
							SubListAll.get(position).setCheckType(1);
						}
						int downLoadSum = 0;
						sum = 0;
						for (int i = 0; i < SubListAll.size(); i++) {
							if (SubListAll.get(i).getCheckType() == 2) {
								sum++;
							}
							if(SubListAll.get(i).getCheckType() == 3) {
								downLoadSum++;
							}
							setsum();
							adapter.notifyDataSetChanged();
						}

						//更新全选图标
						if(sum == (SubListAll.size() - downLoadSum)){
							flag = true;
							img_quanxuan.setImageResource(R.mipmap.image_all_check);
						}else{
							flag = false;
							img_quanxuan.setImageResource(R.mipmap.image_not_all_check);
						}
					}
				}
			}
		});
	}

	/**
	 * 获取数据
	 */
	protected void getdate() {
		flist = FID.queryFileinfoAll(userid);
		Log.e("flist", flist.size() + "");
		ArrayList<FileInfo> seqlist = new ArrayList<FileInfo>();
		if (flist != null && flist.size() > 0) {
			for (int i = 0; i < flist.size(); i++) {
				if (flist.get(i).getSequimgurl() != null
						&& flist.get(i).getSequimgurl().equals(AlbumActivity.ContentImg)) {

					seqlist.add(flist.get(i));
				}
			}
		}
		Log.e("seqlist", seqlist.size() + "");
		if (seqlist != null && seqlist.size() > 0) {
			for (int i = 0; i < seqlist.size(); i++) {
				String linshi = seqlist.get(i).getUrl();
				if (linshi != null && !linshi.trim().equals("")) {
					for (int j = 0; j < SubListAll.size(); j++) {
						if (SubListAll.get(j).getContentPlay() != null
								&& SubListAll.get(j).getContentPlay().equals(linshi)) {
							SubListAll.get(j).setCheckType(3);
						}
					}
				}
			}
		}
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_download: 	// 显示下载列表
			if(SubList.size() == 0){
				return ;
			}
			SubListAll.clear();
			SubListAll.addAll(SubList);
			getdate();
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			} else {
				adapter = new AlbumAdapter(context, SubListAll);
				lv_download.setAdapter(adapter);
			}
			lv_download.setSelection(0);
			lin_status2.setVisibility(View.VISIBLE);
			break;
		case R.id.tv_quxiao: 		// 取消
			lin_status2.setVisibility(View.GONE);

			for(int i=0; i<SubListAll.size(); i++){
				if(SubListAll.get(i).getCheckType() != 3){
					img_quanxuan.setImageResource(R.mipmap.image_not_all_check);
					SubListAll.get(i).setCheckType(1);
				}
			}
			sum = 0;
			setsum();
			flag = false;
			break;
		case R.id.lin_quanxuan: 	// 全选
			if (flag == false) { 	// 默认为未选中状态
				sum = 0;
				for (int i = 0; i < SubListAll.size(); i++) {
					if (SubListAll.get(i).getCheckType() != 3) {
						SubListAll.get(i).setCheckType(2);
						sum++;
					}
				}
				flag = true;
				img_quanxuan.setImageResource(R.mipmap.image_all_check);
				setsum();
			} else {
				for (int i = 0; i < SubListAll.size(); i++) {
					if (SubListAll.get(i).getCheckType() != 3) {
						SubListAll.get(i).setCheckType(1);
					}
				}
				flag = false;
				img_quanxuan.setImageResource(R.mipmap.image_not_all_check);
				sum = 0;
				setsum();
			}
			adapter.notifyDataSetChanged();
			break;
		case R.id.tv_download: 		// 下载
			urllist.clear();
			for (int i = 0; i < SubListAll.size(); i++) {
				if(SubListAll.get(i).getCheckType()==2){
					ContentInfo mContent = SubListAll.get(i);
					mContent.setSequdesc(AlbumActivity.ContentDesc);
					mContent.setSequname(AlbumActivity.ContentName);
					mContent.setSequimgurl(AlbumActivity.ContentImg);	
					mContent.setSequid(AlbumActivity.id);
					//判断userid是否为空
					/*	userid=Utils.getUserId(context);*/
					mContent.setUserid(userid);
					mContent.setDownloadtype("0");
					FID.updatedownloadstatus(mContent.getContentPlay(), "0");//将所有数据设置
					urllist.add(mContent);
				}
			}
			if (urllist.size() > 0) {
				FID.insertfileinfo(urllist);
				List<FileInfo> linshilist = FID.queryFileinfo("false",userid);//查询表中未完成的任务
				//未下载列表
				for(int kk=0;kk<linshilist.size();kk++){
					if(linshilist.get(kk).getDownloadtype()==1){
						DownloadService.workStop(linshilist.get(kk));
						FID.updatedownloadstatus(linshilist.get(kk).getUrl(), "2");
						Log.e("测试下载问题"," 暂停下载的单体"+(linshilist.get(kk).getFileName()));
					}
				}
				linshilist.get(0).setDownloadtype(1);
				FID.updatedownloadstatus(linshilist.get(0).getUrl(), "1");
				Log.e("数据库内数据", linshilist.toString());	
				DownloadService.workStart(linshilist.get(0));
				//发送更新界面数据广播
				Intent p_intent=new Intent("push_down_uncompleted");
				context.sendBroadcast(p_intent);
				lin_status2.setVisibility(View.GONE);
			} else {
				ToastUtils.show_allways(context, "请重新选择数据");
				return;
			}
			break;
		case R.id.img_sort:
			if(SubList.size() != 0 && mainadapter != null){
				Collections.reverse(SubList);			// 倒序
				mainadapter.notifyDataSetChanged();
				imageSortDown.setVisibility(View.VISIBLE);
				imageSort.setVisibility(View.GONE);
			}
			break;
		case R.id.img_sort_down:
			if(SubList.size() != 0 && mainadapter != null){
				Collections.reverse(SubList);			// 倒序
				mainadapter.notifyDataSetChanged();
				imageSortDown.setVisibility(View.GONE);
				imageSort.setVisibility(View.VISIBLE);
			}
			break;
		}
	}
	
	protected void setsum() {
		tv_sum.setText(sum + "");
	}

	/**
	 * 初始化数据库命令执行对象
	 */
	private void initDao() {
		dbdao = new SearchPlayerHistoryDao(context);
		FID = new FileInfoDao(context);
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
		rootView = null;
		context = null;
		FID = null;
		dbdao = null;
		dialog = null;
		lv_album = null;
		lv_download = null;
		img_download = null;
		img_quanxuan = null;
		tv_quxiao = null;
		tv_download = null;
		tv_sum = null;
		textTotal = null;
		lin_quanxuan = null;
		lin_status2 = null;
		imageSort = null;
		imageSortDown = null;
		mainadapter = null;
		adapter = null;
		SubListAll = null;
		urllist = null;
		SubList = null;
		flist = null;
		userid = null;
	}
}
