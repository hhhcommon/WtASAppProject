package com.woting.activity.download.downloadlist.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.woting.R;
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
public class DownLoadListActivity extends Activity implements OnClickListener {
	private DownLoadListActivity context;
	private LinearLayout head_left;
	private ListView mlistview;
	private TextView head_name_tv;
	//	private LinearLayout lin_clear;
	private TextView tv_sum;
	private TextView tv_totalcache;
	private LinearLayout lin_dinglan;
	private List<FileInfo> fileinfolist = new ArrayList<FileInfo>();
	private DownLoadListAdapter adapter;
	private String sequname;
	private int positionnow = -1;	// 标记当前选中的位置
	private String sequid;
	private int sum = 0;
	private Dialog confirmdialog;
	private Dialog confirmdialog1;
	private DecimalFormat df;
	private SearchPlayerHistoryDao dbdao;
	private FileInfoDao FID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloadlist);
		context = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		InitDao();
		handleIntent();
		setview();
		confirmdialog();// 确定是否删除记录弹窗
		setListener();
		df = new DecimalFormat("0.00");
	}

	@Override
	protected void onResume() {
		super.onResume();
		setListValue();// 给list赋初值
	}

	private void confirmdialog() {
		final View dialog1 = LayoutInflater.from(this).inflate(R.layout.dialog_exit_confirm, null);
		TextView tv_cancle = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
		TextView tv_title = (TextView) dialog1.findViewById(R.id.tv_title);
		tv_title.setText("文件不存在，是否删除这条记录?");
		confirmdialog = new Dialog(this, R.style.MyDialog);
		confirmdialog.setContentView(dialog1);
		confirmdialog.setCanceledOnTouchOutside(true);
		confirmdialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmdialog.dismiss();
			}
		});

		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 这里添加删除数据库事件
				try {
					FID.deletefileinfo(fileinfolist.get(positionnow).getLocalurl(), CommonUtils.getUserId(context));
					if (confirmdialog != null) {
						confirmdialog.dismiss();
					}
					setListValue();
					Intent p_intent = new Intent("push_down_completed");
					context.sendBroadcast(p_intent);
					ToastUtils.show_allways(context, "此目录内已经没有内容");
				} catch (Exception e) {
					ToastUtils.show_allways(context, "文件删除失败，请稍后重试");
					if (confirmdialog != null) {
						confirmdialog.dismiss();
					}
				}
			}
		});
	}

	private void InitDao() {
		FID = new FileInfoDao(DownLoadListActivity.this);
		dbdao = new SearchPlayerHistoryDao(DownLoadListActivity.this);
	}

	private void setListValue() {
		sum=0;
		fileinfolist = FID.queryFileinfo(sequid, CommonUtils.getUserId(context),0);
		if (fileinfolist.size() != 0) {
			lin_dinglan.setVisibility(View.VISIBLE);
			mlistview.setVisibility(View.VISIBLE);
			adapter = new DownLoadListAdapter(context, fileinfolist);
			mlistview.setAdapter(adapter);
			setItemListener();
			setInterface();
			tv_sum.setText("共" + fileinfolist.size() + "个节目");
/*			tv_totalcache.setText("20.0MB");*/
			for(int i=0;i<fileinfolist.size();i++){
				sum += fileinfolist.get(i).getEnd();			
			}
			if(sum != 0){
				tv_totalcache.setText("共"+df.format(sum / 1000.0 / 1000.0) + "MB");
			}
		}else{
			lin_dinglan.setVisibility(View.GONE);
			adapter = new DownLoadListAdapter(context, fileinfolist);
			mlistview.setAdapter(adapter);
			/*lin_dinglan.setVisibility(View.GONE);
			mlistview.setVisibility(View.GONE);*/
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
				confirmdialog1.show();
			}
		});
	}

	/*
	 * 删除对话框
	 */
	private void deleteConfirmDialog(final int position) {
		final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
		TextView tv_cancle = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
		TextView tv_title = (TextView) dialog1.findViewById(R.id.tv_title);
		tv_title.setText("是否删除这条记录");
		confirmdialog1 = new Dialog(context, R.style.MyDialog);
		confirmdialog1.setContentView(dialog1);
		confirmdialog1.setCanceledOnTouchOutside(false);
		confirmdialog1.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmdialog1.dismiss();
			}
		});

		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmdialog1.dismiss();
				FID.deletefileinfo(fileinfolist.get(position).getLocalurl(), CommonUtils.getUserId(context));
				setListValue();
				Intent p_intent = new Intent("push_down_completed");
				context.sendBroadcast(p_intent);
			}
		});
	}

	private void setItemListener() {
		mlistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//ToastUtil.show_always(context, "我的localurl是"+fileinfolist.get(position).getLocalurl());
				if(fileinfolist != null && fileinfolist.size() != 0){
					positionnow =position;
					FileInfo mFileInfo = fileinfolist.get(position);
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
							dbdao.deleteHistory(playerurl);
							dbdao.addHistory(history);
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
							dbdao.closedb();
						} else {	// 此处要调对话框，点击同意删除对应的文件信息
							/* ToastUtil.show_always(context, "文件已经被删除，是否删除本条记录"); */
							positionnow = position;
							confirmdialog.show();
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
		sequname = bundle.getString("sequname");
		sequid = bundle.getString("sequid");
	}

	private void setview() {
		// 返回按钮
		head_left = (LinearLayout) findViewById(R.id.head_left_btn);
		mlistview = (ListView) findViewById(R.id.lv_downloadlist);
		head_name_tv = (TextView) findViewById(R.id.head_name_tv);
		head_name_tv.setText(sequname);
		tv_sum = (TextView) findViewById(R.id.tv_sum);
		tv_totalcache = (TextView) findViewById(R.id.tv_totalcache);
		lin_dinglan = (LinearLayout) findViewById(R.id.lin_dinglan);
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
		mlistview=null;
		head_name_tv=null;
		tv_sum=null;
		tv_totalcache=null;
		lin_dinglan=null;
		fileinfolist.clear();
		fileinfolist=null;
		adapter=null;
		confirmdialog=null;
		confirmdialog1=null;
		df=null;
		dbdao=null;
		FID=null;
		context = this;
		setContentView(R.layout.activity_null);
	}
}
