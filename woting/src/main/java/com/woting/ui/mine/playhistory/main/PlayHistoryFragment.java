package com.woting.ui.mine.playhistory.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.widgetui.TipView;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.player.main.play.PlayerActivity;
import com.woting.ui.mine.playhistory.adapter.PlayHistoryAdapter;

import java.util.List;


public class PlayHistoryFragment extends Fragment implements OnClickListener {
    private SearchPlayerHistoryDao dbDao;	// 播放历史数据库
    private List<PlayerHistory> subList;
    private PlayHistoryAdapter adapter;

    private Dialog confirmDialog;
    private Dialog delDialog;// 长按删除数据确认对话框
    private TextView clearEmpty, openEdit;

    private ListView listView;// 数据列表
    private TipView tipView;// 没有数据的提示
    private FragmentActivity context;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_playhistory, container, false);
            rootView.setOnClickListener(this);
            context = getActivity();
            dbDao = new SearchPlayerHistoryDao(context);    // 初始化数据库
            initDialog();
            initViews();
            initData();
        }
        return rootView;
    }

    // 初始化视图
    private void initViews() {
        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);  // 左上返回键

        listView = (ListView) rootView.findViewById(R.id.lv_main);
        tipView = (TipView) rootView.findViewById(R.id.tip_view);

        clearEmpty = (TextView) rootView.findViewById(R.id.clear_empty); 	// 清空
        clearEmpty.setOnClickListener(this);

        openEdit = (TextView) rootView.findViewById(R.id.open_edit); 		// 编辑
        openEdit.setOnClickListener(this);
    }

    // 初始化数据
    private void initData() {
        subList = dbDao.queryHistory();
        if (subList != null && subList.size() > 0) {
            listView.setAdapter(adapter = new PlayHistoryAdapter(context, subList));
        } else {
            clearEmpty.setVisibility(View.GONE);
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有播放过的节目哟\n快去收听节目吧");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:	// 左上角返回键
                PlayerActivity.close();
                break;
            case R.id.clear_empty:		// 清空数据
                if(confirmDialog!=null&&!confirmDialog.isShowing()){
                confirmDialog.show();
                }
                break;
            case R.id.tv_cancle:// 取消删除
                if(confirmDialog!=null&&confirmDialog.isShowing()){
                confirmDialog.dismiss();
                }
                break;
            case R.id.tv_confirm:// 确定删除
                dbDao.deleteHistoryAll();
                subList.clear();
                adapter.notifyDataSetChanged();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有播放过的节目哟\n快去收听节目吧");
                clearEmpty.setVisibility(View.GONE);
                confirmDialog.dismiss();
                break;
        }
    }

    // 清空所有数据 对话框
    private void initDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("是否清空全部历史记录");

        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(true);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context = null;
        clearEmpty = null;
        openEdit = null;
    }
}
