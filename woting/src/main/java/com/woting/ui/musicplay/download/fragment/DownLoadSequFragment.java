package com.woting.ui.musicplay.download.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.FileSizeUtil;
import com.woting.common.widgetui.TipView;
import com.woting.ui.musicplay.download.main.DownloadFragment;
import com.woting.ui.musicplay.download.adapter.DownLoadSequAdapter;
import com.woting.ui.musicplay.download.dao.FileInfoDao;
import com.woting.ui.musicplay.download.downloadlist.activity.DownLoadListActivity;
import com.woting.ui.musicplay.download.model.FileInfo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载完成界面
 */
public class DownLoadSequFragment extends Fragment implements OnClickListener {
    private FragmentActivity context;
    private MessageReceiver receiver;
    private FileInfoDao FID;
    private DownLoadSequAdapter adapter;

    private Dialog confirmDialog;
    private View rootView;
    private View headView;
    private ListView mListView;
    private TipView tipView;// 没有数据提示

    private List<FileInfo> fileSequList;// 专辑 list
    private int index = -1;


    private TextView tv_size;

    private void initDao() {
        FID = new FileInfoDao(context);
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();
        if (receiver == null) {
            receiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH_DOWN_COMPLETED);
            filter.addAction(BroadcastConstants.PUSH_ALLURL_CHANGE);
            filter.addAction(BroadcastConstants.DOWNLOAD_CLEAR_EMPTY_SEQU);// 清空下载的全部专辑
            context.registerReceiver(receiver, filter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_download_completed, container, false);
            rootView.setOnClickListener(this);

            setView();
            setDownLoadSource();
        }
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (fileSequList != null && fileSequList.size() > 0) {
                DownloadFragment.setVisibleSequ(true);
            } else {
                DownloadFragment.setVisibleSequ(false);
            }
        }
    }

    private void setView() {
        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tv_size = (TextView) rootView.findViewById(R.id.tv_size);
        mListView = (ListView) rootView.findViewById(R.id.listView);
    }

    // 查询数据库当中已完成的数据，此数据传输到 adapter 中进行适配
    public void setDownLoadSource() {
        if (fileSequList != null) fileSequList.clear();
        String userId = CommonUtils.getUserId(context);
         List<FileInfo>   f = FID.queryFileInfo("true", userId);

        if (f.size() > 0) {
            DownloadFragment.setVisibleSequ(true);
            tipView.setVisibility(View.GONE);

            fileSequList = FID.GroupFileInfoAll(userId);

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

                int size=0;
                for(int i=0;i<fileSequList.size();i++){
                    try {
                        size=size+fileSequList.get(i).getSum();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                DecimalFormat df = new DecimalFormat("0.00");
                long size1= FileSizeUtil.getAvailableInternalMemorySize();//获取手机内部剩余存储空间
                tv_size.setText("已占用空间"+df.format(size / 1000.0 / 1000.0)+"M,可用空间"+FileSizeUtil.formatFileSize(size1, false)+"M");
                mListView.setAdapter(adapter = new DownLoadSequAdapter(context, fileSequList));
                setItemListener();
                setInterface();
            }
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有下载的内容\n快去把想听的内容下载下来吧");
            DownloadFragment.setVisibleSequ(false);
        }
    }

    // 设置接口回调方法
    private void setInterface() {
        adapter.setOnListener(new DownLoadSequAdapter.downloadSequCheck() {
            @Override
            public void delPosition(int position) {
                index = position;
                deleteConfirmDialog();
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
            case R.id.lin_download_single:
                Intent intent = new Intent(context, DownLoadListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sequname", "单体节目");
                bundle.putString("sequid", "woting");
                intent.putExtras(bundle);
                context.startActivity(intent);
                break;
            case R.id.tv_confirm:// 确定删除
                if (index != -1) {
                    String userId = CommonUtils.getUserId(context);
                    List<FileInfo>   f = FID.queryFileInfo("true", userId);
                    File file = new File(f.get(index).getLocalurl());
                    if (file.exists()) {
                        if (file.delete()) {
                            FID.deleteSequ(fileSequList.get(index).getSequname(), CommonUtils.getUserId(context));
                            index = -1;
                        }
                    }
                } else {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i <fileSequList .size(); i++) {
                        FID.deleteSequ(fileSequList.get(i).getSequname(), CommonUtils.getUserId(context));
                        list.add(fileSequList.get(i).getSequid());
                    }
                    String userId = CommonUtils.getUserId(context);
                    List<FileInfo>   f = FID.queryFileInfo("true", userId);
                    deleteLocal(list,f);
                }
                context.sendBroadcast(new Intent(BroadcastConstants.PUSH_DOWN_COMPLETED));
                confirmDialog.dismiss();
            case R.id.tv_cancle:// 取消删除
                confirmDialog.dismiss();
                break;
        }
    }

    // 删除
    private void deleteLocal(List<String> sequIdList,List<FileInfo>   f) {
        try {
            for (int i = 0; i < sequIdList.size(); i++) {
                for (int j = 0; j < f.size(); j++) {
                    if (f.get(j).getSequid() != null && f.get(j).getSequid().equals(sequIdList.get(i))) {
                        if (f.get(j).getLocalurl() != null) {
                            File file = new File(f.get(j).getLocalurl());
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除对话框
    private void deleteConfirmDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("是否删除记录?");

        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        confirmDialog.show();
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BroadcastConstants.DOWNLOAD_CLEAR_EMPTY_SEQU:
                    if (isVisible()) {
                        index = -1;
                        deleteConfirmDialog();
                    }
                    break;
                case BroadcastConstants.PUSH_DOWN_COMPLETED:
                case BroadcastConstants.PUSH_ALLURL_CHANGE:// 下载的内容需要更新
                    setDownLoadSource();
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ((ViewGroup)rootView.getParent()).removeView(rootView);
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
