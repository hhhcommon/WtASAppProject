package com.woting.ui.download.fragment;

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
import android.widget.TextView;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.CommonUtils;
import com.woting.common.widgetui.TipView;
import com.woting.ui.download.adapter.DownLoadSequAdapter;
import com.woting.ui.download.dao.FileInfoDao;
import com.woting.ui.download.downloadlist.activity.DownLoadListActivity;
import com.woting.ui.download.model.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载完成界面
 */
public class DownLoadCompleted extends Fragment implements OnClickListener {
    private FragmentActivity context;
    private MessageReceiver receiver;
    private FileInfoDao FID;
    private DownLoadSequAdapter adapter;

    private Dialog confirmDialog;
    private View rootView;
    private View headView;
    private LinearLayout linearAllCheck;
    private ListView mListView;
    private ImageView imageAllCheck;
    private TipView tipView;// 没有数据提示

    private List<FileInfo> fileSequList;// 专辑list
    private List<FileInfo> fileDellList;// 删除list

    private String userId;
    private boolean flag;// 删除按钮的处理框
    private boolean allCheckFlag;// 全选flag
    private List<FileInfo> f;

    private void initDao() {
        FID = new FileInfoDao(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        if (receiver == null) {
            receiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH_DOWN_COMPLETED);
            filter.addAction(BroadcastConstants.PUSH_ALLURL_CHANGE);
            context.registerReceiver(receiver, filter);
        }
        rootView = inflater.inflate(R.layout.fragment_download_completed, container, false);

        initDao();
        setView();
        setDownLoadSource();
        return rootView;
    }

    private void setView() {
        rootView.findViewById(R.id.lin_clear).setOnClickListener(this);
        rootView.findViewById(R.id.lin_dinglan).setOnClickListener(this);
        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        mListView = (ListView) rootView.findViewById(R.id.listView);

        linearAllCheck = (LinearLayout) rootView.findViewById(R.id.lin_quanxuan);
        linearAllCheck.setOnClickListener(this);

        imageAllCheck = (ImageView) rootView.findViewById(R.id.img_quanxuan);
    }

    // 查询数据库当中已完成的数据，此数据传输到 adapter 中进行适配
    public void setDownLoadSource() {
        userId = CommonUtils.getUserId(context);
        flag = false;
        linearAllCheck.setVisibility(View.INVISIBLE);
        imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
        allCheckFlag = false;
        f = FID.queryFileInfo("true", userId);
        if (f.size() > 0) {
            tipView.setVisibility(View.GONE);
            fileSequList = FID.GroupFileInfoAll(userId);
            Log.e("f", fileSequList.size() + "");
            if (fileSequList.size() > 0) {
                for (int i = 0; i < fileSequList.size(); i++) {
                    if (fileSequList.get(i).getSequid().equals("woting")) {
                        headView = LayoutInflater.from(context).inflate(R.layout.adapter_download_complete, null);
                        headView.findViewById(R.id.lin_download_single).setOnClickListener(this);
                        mListView.addHeaderView(headView);
                    } else if (i == fileSequList.size() - 1) {
                        if (headView != null) {
                            mListView.removeHeaderView(headView);
                        }
                    }
                }
                mListView.setAdapter(adapter = new DownLoadSequAdapter(context, fileSequList));
                setItemListener();
                setInterface();
            }
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有下载的内容\n快去把想听的内容下载下来吧");
        }
    }

    // 设置接口回调方法
    private void setInterface() {
        adapter.setOnListener(new DownLoadSequAdapter.downloadSequCheck() {
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

    private void setItemListener() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_clear:    // 删除
                if (!flag) {
                    linearAllCheck.setVisibility(View.VISIBLE);
                    for (int i = 0; i < fileSequList.size(); i++) {
                        fileSequList.get(i).setViewtype(1);
                    }
                } else {
                    // 隐藏删除框  检查当前的 list 当中是否有 checkType == 1 的
                    // 隐藏删除框时设置所有项目的默认选定状态为 0  设置为未选中状态
                    if (fileDellList != null) {
                        fileDellList.clear();
                    }
                    for (int i = 0; i < fileSequList.size(); i++) {
                        if (fileSequList.get(i).getChecktype() == 1) {
                            if (fileDellList == null) {
                                fileDellList = new ArrayList<>();
                            }
                            fileDellList.add(fileSequList.get(i));
                        }
                    }
                    if (fileDellList != null && fileDellList.size() > 0) {
                        deleteConfirmDialog();
                    } else {
                        linearAllCheck.setVisibility(View.INVISIBLE);
                        for (int i = 0; i < fileSequList.size(); i++) {
                            fileSequList.get(i).setViewtype(0);
                            fileSequList.get(i).setChecktype(0);// 隐藏删除框时设置所有项目的默认选定状态为 0
                        }
                        imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
                        allCheckFlag = false;
                    }
                }
                flag = !flag;
                adapter.notifyDataSetChanged();
                break;
            case R.id.lin_quanxuan:
                if (allCheckFlag) {
                    imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);// 变更为非全部选中状态
                    for (int i = 0; i < fileSequList.size(); i++) {
                        fileSequList.get(i).setChecktype(0);
                    }
                } else {
                    imageAllCheck.setImageResource(R.mipmap.wt_group_checked);// 变更为全部选中状态
                    for (int i = 0; i < fileSequList.size(); i++) {
                        fileSequList.get(i).setChecktype(1);
                    }
                }
                allCheckFlag = !allCheckFlag;
                adapter.notifyDataSetChanged();
                break;
            case R.id.lin_download_single:
                Intent intent = new Intent(context, DownLoadListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sequname", "单体节目");
                bundle.putString("sequid", "woting");
                intent.putExtras(bundle);
                context.startActivity(intent);
                break;
            case R.id.tv_confirm:
                List<String> sequIdList=new ArrayList<>();
                for (int i = 0; i < fileDellList.size(); i++) {
                    FID.deleteSequ(fileDellList.get(i).getSequname(), userId);
                    sequIdList.add(fileDellList.get(i).getSequid());
                }
                deleteLocal(sequIdList);
                setDownLoadSource();// 重新适配界面操作
                allCheckFlag = false;// 全选flag
                flag = false;
                linearAllCheck.setVisibility(View.INVISIBLE);
            case R.id.tv_cancle:
                confirmDialog.dismiss();
                break;
        }
    }

    private void deleteLocal(List<String> sequIdList) {
        try{
            for(int i=0;i<sequIdList.size();i++){
                String Name=sequIdList.get(i);
                for(int j=0;j<f.size();j++){
                 if(f.get(j).getSequid()!=null&&f.get(j).getSequid().equals(sequIdList.get(i))){
                     String adada=f.get(j).getSequid();
                     if(f.get(j).getLocalurl()!=null){
                         File file=new File(f.get(j).getLocalurl());
                         if(file.exists()){
                            file.delete();
                         }
                     }
                 }
                }
            }
        }catch (Exception e){
            e.printStackTrace();

        }

    }


    // 删除对话框
    private void deleteConfirmDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("是否删除这" + fileDellList.size() + "条记录");

        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        confirmDialog.show();
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastConstants.PUSH_DOWN_COMPLETED)) {
                setDownLoadSource();
            }else if(intent.getAction().equals(BroadcastConstants.PUSH_ALLURL_CHANGE)){
                setDownLoadSource();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        context = null;
    }
}
