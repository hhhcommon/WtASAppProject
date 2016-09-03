package com.woting.activity.download.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.download.adapter.DownLoadSequAdapter;
import com.woting.activity.download.adapter.DownLoadSequAdapter.downloadsequCheck;
import com.woting.activity.download.dao.FileInfoDao;
import com.woting.activity.download.downloadlist.activity.DownLoadListActivity;
import com.woting.activity.download.model.FileInfo;
import com.woting.util.CommonUtils;
import com.woting.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class DownLoadCompleted extends Fragment implements OnClickListener {
	private  FragmentActivity context;
	private View rootView;
	private View headview;
	private RelativeLayout rv_download;
	private LinearLayout lin_dinglan;
	private LinearLayout lin_quanxuan;
	private LinearLayout lin_clear;
	private LinearLayout lin_download_single;
	private LinearLayout lin_nodenglu;
	private List<FileInfo> fileSequList;	// 专辑list
	private List<FileInfo> fileinfolist;	// 查询当前userid下已经下载完成的list
	private List<FileInfo> fileDellList;	// 删除list
	private FileInfoDao FID;
	private DownLoadSequAdapter adapter;
	private String userid;
	private ListView mlistView;
	private boolean flag = false;			// 删除按钮的处理框
	private boolean quanxuanflag = false;	// 全选flag
	private ImageView img_quanxuan;
	private Dialog confirmdialog;
	private MessageReceiver Receiver;
//	private int judgetype=-1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		if(Receiver == null) {
			Receiver = new MessageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("push_down_completed");
			context.registerReceiver(Receiver, filter);
		}
		rootView = inflater.inflate(R.layout.fragment_download_completed, container, false);
		initdao();
		setView();
		setLisener();
		setDownLoadSource();
		return rootView;
	}
	
	class MessageReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals("push_down_completed")){
				setDownLoadSource();
			}
		}
	}

	private void setView() {
		rv_download=(RelativeLayout)rootView.findViewById(R.id.wt_download_rv);
		mlistView = (ListView) rootView.findViewById(R.id.listView);
		//mlistView.addHeaderView(headview);
		lin_dinglan = (LinearLayout) rootView.findViewById(R.id.lin_dinglan);
		lin_quanxuan = (LinearLayout) rootView.findViewById(R.id.lin_quanxuan);
		lin_clear = (LinearLayout) rootView.findViewById(R.id.lin_clear);
		img_quanxuan = (ImageView) rootView.findViewById(R.id.img_quanxuan);
		lin_nodenglu=(LinearLayout)rootView.findViewById(R.id.lin_status_no);
	}

	private void setLisener() {
		lin_dinglan.setOnClickListener(this);
		lin_quanxuan.setOnClickListener(this);
		lin_clear.setOnClickListener(this);
	}

	/**
	 * 查询数据库当中已完成的数据，此数据传输到adapter中进行适配
	 */
	public void setDownLoadSource() {
		userid = CommonUtils.getUserId(context);
		flag = false;
		lin_quanxuan.setVisibility(View.INVISIBLE);
		img_quanxuan.setImageResource(R.mipmap.wt_group_nochecked);
		quanxuanflag = false;
		fileinfolist = FID.queryFileinfo("true", userid);
		if (fileinfolist.size() > 0) {
			lin_nodenglu.setVisibility(View.GONE);
			fileSequList = FID.GroupFileinfoAll(userid);
			Log.e("fileSequList", fileSequList.size() + "");
			if (fileSequList.size() > 0) {
				for(int i=0;i<fileSequList.size();i++){
					if(fileSequList.get(i).getSequid().equals("woting")){
						//此处应出现添加headview进首项	
						headview = LayoutInflater.from(context).inflate(R.layout.adapter_download_complete, null);
//						judgetype = 1;
						lin_download_single = (LinearLayout)headview.findViewById(R.id.lin_download_single);
						lin_download_single.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(context, DownLoadListActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("sequname", "单体节目");
								bundle.putString("sequid", "woting");
								intent.putExtras(bundle);
								context.startActivity(intent);	
							}
						});
						
						mlistView.addHeaderView(headview);
					}else{
						if(i == fileSequList.size()-1){
							if(headview != null){
								mlistView.removeHeaderView(headview);
							}
						}
					}
				}
				adapter = new DownLoadSequAdapter(context, fileSequList);
				lin_dinglan.setVisibility(View.VISIBLE);
				mlistView.setVisibility(View.VISIBLE);
				rv_download.setVisibility(View.VISIBLE);
				mlistView.setAdapter(adapter);				
				setItemListener();
				setInterface();// 获取接口位置，设置相应事件
			}
		} else {
			lin_dinglan.setVisibility(View.GONE);
			mlistView.setVisibility(View.GONE);
			rv_download.setVisibility(View.GONE);
			lin_nodenglu.setVisibility(View.VISIBLE);
			ToastUtils.show_allways(context, "还没有下载完成的任务");
		}
	}

	/**
	 * 设置接口回调方法
	 */
	private  void setInterface() {
		adapter.setOnListener(new downloadsequCheck() {
			@Override
			public void checkposition(int position) {
				if (fileSequList.get(position).getChecktype() == 0) {
					fileSequList.get(position).setChecktype(1);
				} else {
					fileSequList.get(position).setChecktype(0);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private  void setItemListener() {
		mlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, DownLoadListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("sequname", fileSequList.get(position).getSequname());
				bundle.putString("sequid", fileSequList.get(position).getSequid());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
	}

	private void initdao() {
		FID = new FileInfoDao(context);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_clear:	// 删除
			/* ToastUtil.show_allways(context,"全部清理get"); */
			if (flag == false) {
				lin_quanxuan.setVisibility(View.VISIBLE);
				for (int i = 0; i < fileSequList.size(); i++) {
					fileSequList.get(i).setViewtype(1);
				}
				flag = true;
			} else {
				// 隐藏删除框
				// 检查当前的list当中是否有checktype=1的；
				// 隐藏删除框时设置所有项目的默认选定状态为0
				// img_quanxuan设置为未选中状态
				if (fileDellList != null) {
					fileDellList.clear();
				}
				for (int i = 0; i < fileSequList.size(); i++) {
					if (fileSequList.get(i).getChecktype() == 1) {
						if (fileDellList == null) {
							fileDellList = new ArrayList<FileInfo>();
							fileDellList.add(fileSequList.get(i));
						} else {
							fileDellList.add(fileSequList.get(i));
						}
					}
				}
				if(fileDellList != null && fileDellList.size() > 0){
					//初始化对话框	接入对话框事件
					deleteConfirmDialog();
					confirmdialog.show();
				}else{
					lin_quanxuan.setVisibility(View.INVISIBLE);
					for (int i = 0; i < fileSequList.size(); i++) {
						fileSequList.get(i).setViewtype(0);
						fileSequList.get(i).setChecktype(0);// 隐藏删除框时设置所有项目的默认选定状态为0
					}
					flag = false;
					img_quanxuan.setImageResource(R.mipmap.wt_group_nochecked);
					quanxuanflag = false;
				}
			}
			adapter.notifyDataSetChanged();
			break;
		case R.id.lin_quanxuan:
			if (quanxuanflag == false) {
				// 变更为全部选中状态
				img_quanxuan.setImageResource(R.mipmap.wt_group_checked);
				for (int i = 0; i < fileSequList.size(); i++) {
					fileSequList.get(i).setChecktype(1);
				}
				quanxuanflag = true;
			} else {
				// 变更为非全部选中状态
				img_quanxuan.setImageResource(R.mipmap.wt_group_nochecked);
				for (int i = 0; i < fileSequList.size(); i++) {
					fileSequList.get(i).setChecktype(0);
				}
				quanxuanflag = false;
			}
			adapter.notifyDataSetChanged();
			break;
		}
	}
	
    /**
     * 删除对话框
     */
	private void deleteConfirmDialog() {
		final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
		TextView tv_cancle = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
		TextView tv_title = (TextView) dialog1.findViewById(R.id.tv_title);
		tv_title.setText("是否删除这"+fileDellList.size()+"条记录");
		confirmdialog = new Dialog(context, R.style.MyDialog);
		confirmdialog.setContentView(dialog1);
		confirmdialog.setCanceledOnTouchOutside(false);
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
				confirmdialog.dismiss();
				/*ToastUtil.show_allways(context, "此处对接确认事件即删除数据库操作 完成后需要刷新adapter");*/
				for(int i=0;i<fileDellList.size();i++){
					FID.deletesequ(fileDellList.get(i).getSequname(), userid);
				}
				setDownLoadSource();//重新适配界面操作
			    quanxuanflag = false;// 全选flag
			    flag =false;
			    lin_quanxuan.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(Receiver != null){
			context.unregisterReceiver(Receiver);
			Receiver = null;
		}
		context = null;
	}
}
