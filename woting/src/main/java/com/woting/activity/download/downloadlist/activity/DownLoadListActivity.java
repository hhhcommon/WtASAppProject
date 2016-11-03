package com.woting.activity.download.downloadlist.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.activity.download.dao.FileInfoDao;
import com.woting.activity.download.downloadlist.adapter.DownLoadListAdapter;
import com.woting.activity.download.downloadlist.adapter.DownLoadListAdapter.downloadlist;
import com.woting.activity.download.model.FileInfo;
import com.woting.activity.home.main.HomeActivity;
import com.woting.activity.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.activity.home.player.main.fragment.PlayerFragment;
import com.woting.activity.home.player.main.model.PlayerHistory;
import com.woting.activity.main.MainActivity;
import com.woting.common.constant.StringConstant;
import com.woting.manager.MyActivityManager;
import com.woting.util.CommonUtils;
import com.woting.util.ToastUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
/**
 * 下载列表
 * @author 辛龙
 *2016年8月8日
 */
public class DownLoadListActivity extends BaseActivity implements OnClickListener {
	private DownLoadListActivity context;
	private LinearLayout head_left;
	private ListView mListView;
	private TextView head_name_tv;
	//	private LinearLayout lin_clear;
	private TextView tv_sum;
	private TextView tv_TotalCache;
	private LinearLayout lin_Ding_Lan;
	private List<FileInfo> fileInfoList = new ArrayList<>();
	private DownLoadListAdapter adapter;
	private String sequName;
	private int positionNow = -1;	// 标记当前选中的位置
	private String sequId;
	private int sum = 0;
	private Dialog confirmDialog;
	private Dialog confirmDialog1;
	private DecimalFormat df;
	private SearchPlayerHistoryDao dbDao;
	private FileInfoDao FID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloadlist);
		context = this;
		InitDao();
		handleIntent();
		setview();
		confirmDialog();// 确定是否删除记录弹窗
		setListener();
		df = new DecimalFormat("0.00");
	}

	@Override
	protected void onResume() {
		super.onResume();
		setListValue();// 给list赋初值
	}

	private void confirmDialog() {
		final View dialog1 = LayoutInflater.from(this).inflate(R.layout.dialog_exit_confirm, null);
		TextView tv_cancel = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
		TextView tv_title = (TextView) dialog1.findViewById(R.id.tv_title);
		tv_title.setText("文件不存在，是否删除这条记录?");
		confirmDialog = new Dialog(this, R.style.MyDialog);
		confirmDialog.setContentView(dialog1);
		confirmDialog.setCanceledOnTouchOutside(true);
		confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}
		});

		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 这里添加删除数据库事件
				try {
					FID.deletefileinfo(fileInfoList.get(positionNow).getLocalurl(), CommonUtils.getUserId(context));
					if (confirmDialog != null) {
						confirmDialog.dismiss();
					}
					setListValue();
					Intent p_intent = new Intent("push_down_completed");
					context.sendBroadcast(p_intent);
					ToastUtils.show_allways(context, "此目录内已经没有内容");
				} catch (Exception e) {
					ToastUtils.show_allways(context, "文件删除失败，请稍后重试");
					if (confirmDialog != null) {
						confirmDialog.dismiss();
					}
				}
			}
		});
	}

	private void InitDao() {
		FID = new FileInfoDao(DownLoadListActivity.this);
		dbDao = new SearchPlayerHistoryDao(DownLoadListActivity.this);
	}

	private void setListValue() {
		sum=0;
		fileInfoList = FID.queryFileinfo(sequId, CommonUtils.getUserId(context),0);
		if (fileInfoList.size() != 0) {
			lin_Ding_Lan.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.VISIBLE);
			adapter = new DownLoadListAdapter(context, fileInfoList);
			mListView.setAdapter(adapter);
			setItemListener();
			setInterface();
			tv_sum.setText("共" + fileInfoList.size() + "个节目");
			for(int i=0;i<fileInfoList.size();i++){
				sum += fileInfoList.get(i).getEnd();
			}
			if(sum != 0){
				tv_TotalCache.setText("共"+df.format(sum / 1000.0 / 1000.0) + "MB");
			}
		}else{
			lin_Ding_Lan.setVisibility(View.GONE);
			adapter = new DownLoadListAdapter(context, fileInfoList);
			mListView.setAdapter(adapter);
			Intent p_intent = new Intent("push_down_completed");
			context.sendBroadcast(p_intent);
			ToastUtils.show_allways(context, "此目录内已经没有内容");
		}
	}

	private void setInterface() {
		adapter.setonListener(new downloadlist() {
			@Override
			public void checkposition(int position) {
				deleteConfirmDialog(position);
				confirmDialog1.show();
			}
		});
	}

	/*
	 * 删除对话框
	 */
	private void deleteConfirmDialog(final int position) {
		final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
		TextView tv_cancel = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
		TextView tv_title = (TextView) dialog1.findViewById(R.id.tv_title);
		tv_title.setText("是否删除这条记录");
		confirmDialog1 = new Dialog(context, R.style.MyDialog);
		confirmDialog1.setContentView(dialog1);
		confirmDialog1.setCanceledOnTouchOutside(false);
		confirmDialog1.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmDialog1.dismiss();
			}
		});

		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmDialog1.dismiss();
				FID.deletefileinfo(fileInfoList.get(position).getLocalurl(), CommonUtils.getUserId(context));
				setListValue();
				Intent p_intent = new Intent("push_down_completed");
				context.sendBroadcast(p_intent);
			}
		});
	}

	private void setItemListener() {
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(fileInfoList != null && fileInfoList.size() != 0){
					positionNow =position;
					FileInfo mFileInfo = fileInfoList.get(position);
					if(mFileInfo.getLocalurl() != null && !mFileInfo.getLocalurl().equals("")){
						File file = new File(mFileInfo.getLocalurl());
						if (file.exists()) {
							String playername = mFileInfo.getFileName().substring(0, mFileInfo.getFileName().length() - 4);
							String playerimage = mFileInfo.getImageurl();
							String playerurl = mFileInfo.getUrl();
							String playerurI = mFileInfo.getLocalurl();
							String playlocalrurl = mFileInfo.getLocalurl();
							String playermediatype = "AUDIO";
							String playercontentshareurl = mFileInfo.getContentShareURL();
							String plaplayeralltime = "0";
							String playerintime = "0";
							String playercontentdesc = mFileInfo.getPlaycontent();
							String playernum ="0";
							String playerzantype = "0";
							String playerfrom = "";
							String playerfromid = "";
							String playerfromurl = "";
							String playeraddtime = Long.toString(System.currentTimeMillis());
							String bjuserid = CommonUtils.getUserId(context);
							String ContentFavorite = mFileInfo.getContentFavorite();
							String ContentId = mFileInfo.getContentId();
							String sequName=mFileInfo.getSequname();
							String sequId=mFileInfo.getSequid();
							String sequImg=mFileInfo.getSequimgurl();
							String sequDesc=mFileInfo.getSequdesc();

							//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
							PlayerHistory history = new PlayerHistory(
									playername,  playerimage, playerurl, playerurI,playermediatype,
									plaplayeralltime, playerintime, playercontentdesc, playernum,
									playerzantype,  playerfrom, playerfromid, playerfromurl,playeraddtime,bjuserid,playercontentshareurl,ContentFavorite,
									ContentId,playlocalrurl,sequName,sequId,sequDesc,sequImg);
							dbDao.deleteHistory(playerurl);
							dbDao.addHistory(history);
							if(PlayerFragment.context != null){
								MainActivity.change();
								HomeActivity.UpdateViewPager();
								PlayerFragment.SendTextRequest(mFileInfo.getFileName().substring(0, mFileInfo.getFileName().length() - 4), context);
							}else{
								SharedPreferences sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
								SharedPreferences.Editor et = sp.edit();
								et.putString(StringConstant.PLAYHISTORYENTER, "true");
								et.putString(StringConstant.PLAYHISTORYENTERNEWS,mFileInfo.getFileName().substring(0, mFileInfo.getFileName().length() - 4));
								et.commit();
								MainActivity.change();
								HomeActivity.UpdateViewPager();
							}
							setResult(1);
							finish();
							dbDao.closedb();
						} else {	// 此处要调对话框，点击同意删除对应的文件信息
							/* ToastUtil.show_always(context, "文件已经被删除，是否删除本条记录"); */
							positionNow = position;
							confirmDialog.show();
						}
					}
				}
			}
		});
	}

	private void setListener() {
		head_left.setOnClickListener(this);
	}

	private void handleIntent() {
		Intent intent = context.getIntent();
		Bundle bundle = intent.getExtras();
		sequName = bundle.getString("sequname");
		sequId = bundle.getString("sequid");
	}

	private void setview() {
		// 返回按钮
		head_left = (LinearLayout) findViewById(R.id.head_left_btn);
		mListView = (ListView) findViewById(R.id.lv_downloadlist);
		head_name_tv = (TextView) findViewById(R.id.head_name_tv);
		head_name_tv.setText(sequName);
		tv_sum = (TextView) findViewById(R.id.tv_sum);
		tv_TotalCache = (TextView) findViewById(R.id.tv_totalcache);
		lin_Ding_Lan= (LinearLayout) findViewById(R.id.lin_dinglan);
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
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		head_left=null;
		mListView=null;
		head_name_tv=null;
		tv_sum=null;
		tv_TotalCache=null;
		lin_Ding_Lan=null;
		fileInfoList.clear();
		fileInfoList=null;
		adapter=null;
		confirmDialog=null;
		confirmDialog1=null;
		df=null;
		dbDao=null;
		FID=null;
		context = this;
		setContentView(R.layout.activity_null);
	}
}
