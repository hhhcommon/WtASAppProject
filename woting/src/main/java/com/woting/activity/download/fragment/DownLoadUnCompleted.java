package com.woting.activity.download.fragment;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.woting.R;
import com.woting.activity.download.adapter.DownloadAdapter;
import com.woting.activity.download.dao.FileInfoDao;
import com.woting.activity.download.model.FileInfo;
import com.woting.activity.download.service.DownloadService;
import com.woting.activity.download.service.DownloadTask;
import com.woting.common.constant.BroadcastConstants;
import com.woting.util.CommonUtils;
import com.woting.util.ToastUtils;

/**
 * 要注意删除事件和下载完毕事件后对数据库表的操作
 */
public class DownLoadUnCompleted extends Fragment {
	private ListView listView;
	private DownloadAdapter adapter;
	private FragmentActivity context;
	private TextView tv_start;// 开始下载按钮
	private List<FileInfo> filoinfolist;// 表中未完成的任务
	private View rootView;
	private boolean dwtype = false;// 判断
	private ImageView img_start;
	private FileInfoDao FID;// 数据库操作对象
	private LinearLayout lin_start;
	private LinearLayout lin_clear;
//	private SharedPreferences sp;
	private LinearLayout lin_status_yes;
	private LinearLayout lin_status_no;
	private String userid;
	private MessageReceivers Receiver;
	private int num=-1;
	/**
	 * playerfragment里添加下载功能 将数据插入数据库 sequid为空 
	 * 修改downloadcompletefragment内获取list的方法
	 */


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
//		sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		// 注册广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastConstants.ACTION_UPDATE);
		filter.addAction(BroadcastConstants.ACTION_FINISHED);
		context.registerReceiver(mReceiver, filter);
		if(Receiver == null) {
			Receiver = new MessageReceivers();
			IntentFilter filters = new IntentFilter();
			filters.addAction("push_down_uncompleted");
			context.registerReceiver(Receiver, filters);
		}
	}

	class MessageReceivers extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals("push_down_uncompleted")){
				setDownLoadSource();
			}
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_download_uncompleted,container, false);
		setview();
		initdao();			// 初始化数据库对象
		setlistener();		// 给控件设置监听
		setDownLoadSource();// 设置界面数据
		return rootView;
	}

	private void setview() {
		listView = (ListView) rootView.findViewById(R.id.listView);
		tv_start = (TextView) rootView.findViewById(R.id.tv_start);
		img_start = (ImageView) rootView.findViewById(R.id.img_start);
		lin_start = (LinearLayout) rootView.findViewById(R.id.lin_start);
		lin_clear = (LinearLayout) rootView.findViewById(R.id.lin_clear);
		lin_status_yes = (LinearLayout) rootView.findViewById(R.id.lin_status_yes);	// 有未下载时布局
		lin_status_no = (LinearLayout) rootView.findViewById(R.id.lin_status_no);	// 无未下载时布局
	}

	// 初始化数据库对象
	private void initdao() {
		FID = new FileInfoDao(context);
	}

	/**
	 * 设置界面数据
	 */
	private void setDownLoadSource() {
		userid = CommonUtils.getUserId(context);
		filoinfolist = FID.queryFileinfo("false",userid);// 查询表中未完成的任务
		if (filoinfolist.size() == 0) {
			lin_status_yes.setVisibility(View.GONE);
			lin_status_no.setVisibility(View.VISIBLE);
		} else {
			lin_status_no.setVisibility(View.GONE);
			lin_status_yes.setVisibility(View.VISIBLE);

			if (DownloadTask.mContext != null) {
				if (DownloadTask.isPause == true) {
					img_start.setImageResource(R.mipmap.wt_download_play);
					dwtype = false;
					tv_start.setText("全部开始");
				} else {
					dwtype = true;
					img_start.setImageResource(R.mipmap.wt_download_pause);
					tv_start.setText("全部暂停");
				}
			} else {
				//这里改了 原值为false
				dwtype = true;
				img_start.setImageResource(R.mipmap.wt_download_play);
				tv_start.setText("全部开始");
			}
			Log.e("广播消息", "执行刷新");
			adapter = new DownloadAdapter(context, filoinfolist);
			listView.setAdapter(adapter);
			setonitemlistener();
		}
	}

	private void setlistener() {
		lin_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dwtype == true) {
					/*
					 * 1:全部暂停事件  2：目前为1状态的设置为2 3：所有为0状态的不处理   4： 设置图片和文字
					 * 5：设置downloadstatus标签为-1，下载完当前任务后，不再下载另一条
					 */
					for (int i = 0; i < filoinfolist.size(); i++) {
						if (filoinfolist.get(i).getDownloadtype() == 1) {
							filoinfolist.get(i).setDownloadtype(2);
							FID.updatedownloadstatus(filoinfolist.get(i).getUrl(), "2");
							DownloadService.workStop(filoinfolist.get(i));
						}
					}
					adapter.notifyDataSetChanged();
					img_start.setImageResource(R.mipmap.wt_download_play);
					tv_start.setText("全部开始");
					dwtype = false;
				} else {
					/*
					 * 1:全部开始事件    2：目前为1状态的设置为2 ，2状态的不处理    3：所有为0状态的不处理
					 * 将position为0的数据标记为下载状态=14： 设置图片和文字
					 * 5：设置downloadstatus标签为1，下载完当前任务后，开始下载另一条
					 */
					if (filoinfolist != null && filoinfolist.size() > 0) {
						for (int i = 0; i < filoinfolist.size(); i++) {
							if (filoinfolist.get(i).getDownloadtype() == 1) {
								filoinfolist.get(i).setDownloadtype(2);
								FID.updatedownloadstatus(filoinfolist.get(i).getUrl(), "2");
								DownloadService.workStop(filoinfolist.get(i));
							}
						}
						if (adapter == null) {
							adapter = new DownloadAdapter(context, filoinfolist);
							listView.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						img_start.setImageResource(R.mipmap.wt_download_pause);
						tv_start.setText("全部暂停");
						// 如果点击了全部开始 就需要开始下一个下载对象
						getFileInfo(filoinfolist.get(getnum()));
						Handler h=new Handler();
						h.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								if(DownloadTask.downloadstatus==-1){
									ToastUtils.show_allways(context, filoinfolist.get(num).getFileName()+"的下载出现问题");
									getFileInfo(filoinfolist.get(getnum()));
								}
								
							}
						}, 10000);
						dwtype = true;
					}
				}
			}

			
		});
	
		
		// 清空数据库中所有未下载完成的数据
		lin_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FID.deletefilebyuserid(userid);
				setDownLoadSource();
				return;
			}
		});
	}
	private int getnum() {
		if(num<filoinfolist.size()){
			num++;
		}else{
			num=0;
		}
		return num;
	}

	private void setonitemlistener() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if (filoinfolist.get(position).getDownloadtype() == 0) {
					/*
					 * 点击该项目时，此时是未下载状态需要把下载中状态的数据变为暂停状态暂停状态的数据不需要改变
					 * 最后把该数据状态变为开始下载中状态
					 */
					for (int i = 0; i < filoinfolist.size(); i++) {
						if (filoinfolist.get(i).getDownloadtype() == 1) {
							filoinfolist.get(i).setDownloadtype(2);
							FID.updatedownloadstatus(filoinfolist.get(i).getUrl(), "2");
							DownloadService.workStop(filoinfolist.get(i));
						}
					}
					getFileInfo(filoinfolist.get(position));
				} else if (filoinfolist.get(position).getDownloadtype() == 1) {
					/*
					 * 点击该项目时，此时该项目的状态是下载中 只需要把项目自己变为暂停状态即可
					 */
					filoinfolist.get(position).setDownloadtype(2);
					FID.updatedownloadstatus(filoinfolist.get(position).getUrl(), "2");
					DownloadService.workStop(filoinfolist.get(position));
					adapter.notifyDataSetChanged();
				} else {
					/*
					 * 点击该项目时，该项目为暂停状态 把其它的播放状态变为暂停状态 最后把自己状态变为下载中状态
					 */
					for (int i = 0; i < filoinfolist.size(); i++) {
						if (filoinfolist.get(i).getDownloadtype() == 1) {
							filoinfolist.get(i).setDownloadtype(2);
							FID.updatedownloadstatus(filoinfolist.get(i).getUrl(), "2");
							DownloadService.workStop(filoinfolist.get(i));
						}
					}
					getFileInfo(filoinfolist.get(position));
				}
			}
		});
	}

	/**
	 * 给fileinfo初值
	 */
	private void getFileInfo(FileInfo fileInfo) {
		fileInfo.setDownloadtype(1);
		FID.updatedownloadstatus(fileInfo.getUrl(), "1");
		DownloadService.workStart(fileInfo);
	}

	/**
	 * 更新UI的广播接收器
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context contexts, Intent intent) {
			if (BroadcastConstants.ACTION_UPDATE.equals(intent.getAction())) {
				int start = intent.getIntExtra("start", 0);
				int end = intent.getIntExtra("end", 0);
				String url = intent.getStringExtra("url");
				if (adapter != null) {
					adapter.updateProgress(url, start, end);
				}
			} else if (BroadcastConstants.ACTION_FINISHED.equals(intent.getAction())) {
				// 下载结束
				FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
				ToastUtils.show_short(contexts, fileInfo.getFileName()+ "已经下载完毕");
				FID.updatefileinfo(fileInfo.getFileName());
				//发送更新界面数据广播
				Intent p_intent=new Intent("push_down_completed");
				context. sendBroadcast(p_intent);
				// List<FileInfo> fileinfolist = FID.queryFileinfo("true",userid);
				if(dwtype){
					filoinfolist = FID.queryFileinfo("false",userid);// 查询表中未完成的任务
					if (filoinfolist != null && filoinfolist.size() > 0) {
						filoinfolist.get(0).setDownloadtype(1);
						FID.updatedownloadstatus(filoinfolist.get(0).getUrl(), "1");
						DownloadService.workStart(filoinfolist.get(0));
						adapter = new DownloadAdapter(context, filoinfolist);
						listView.setAdapter(adapter);
						setonitemlistener();
						setDownLoadSource();
					} else {
						img_start.setImageResource(R.mipmap.wt_download_play);
						tv_start.setText("全部开始");
						adapter = new DownloadAdapter(context, filoinfolist);
						listView.setAdapter(adapter);
						setonitemlistener();
						setDownLoadSource();
					}
				}else{
					filoinfolist = FID.queryFileinfo("false",userid);// 查询表中未完成的任务
					img_start.setImageResource(R.mipmap.wt_download_play);
					tv_start.setText("全部开始");
					adapter = new DownloadAdapter(context, filoinfolist);
					listView.setAdapter(adapter);
					setonitemlistener();
					setDownLoadSource();
				}
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent intent1 = new Intent(context, DownloadService.class);
		context.stopService(intent1);
		if(Receiver != null){
			context.unregisterReceiver(Receiver);
			Receiver = null;
		}
		context = null;

		//当程序结束时将所有符合条件的数据全部设置为待下载状态
		//for (int i = 0; i < filoinfolist.size(); i++) {
		//	FID.updatedownloadstatus(filoinfolist.get(i).getUrl(), "0");
		//}
	}
}
