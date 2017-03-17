package com.woting.ui.home.player.main.play.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woting.R;
import com.woting.ui.download.activity.DownloadFragment;
import com.woting.ui.mine.favorite.main.FavoriteFragment;
import com.woting.ui.mine.playhistory.main.PlayHistoryFragment;
import com.woting.ui.mine.subscriber.activity.SubscriberListFragment;

/**
 * PlayerMoreOperation
 * Created by Administrator on 2017/3/16.
 */
public class PlayerMoreOperationFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity context;

    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_player_more_operation, container, false);

            initView();
            initEvent();
        }
        return rootView;
    }

    // 初始化视图
    private void initView() {

    }

    // 初始化点击事件
    private void initEvent() {
        rootView.findViewById(R.id.text_history).setOnClickListener(this);// 播放历史
        rootView.findViewById(R.id.text_subscribe).setOnClickListener(this);// 我的订阅
        rootView.findViewById(R.id.text_local).setOnClickListener(this);// 我的下载
        rootView.findViewById(R.id.text_liked).setOnClickListener(this);// 我喜欢的
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_history:// 播放历史
                PlayHistoryFragment historyFrag = new PlayHistoryFragment();
                Bundle bundleHis = new Bundle();
                bundleHis.putInt("fromType", 6);
                historyFrag.setArguments(bundleHis);
                PlayerMoreOperationActivity.open(historyFrag);
                break;
            case R.id.text_subscribe:// 我的订阅
                SubscriberListFragment subscriberListFragment = new SubscriberListFragment();
                Bundle bundleSub = new Bundle();
                bundleSub.putInt("fromType", 6);
                subscriberListFragment.setArguments(bundleSub);
                PlayerMoreOperationActivity.open(subscriberListFragment);
                break;
            case R.id.text_local:// 我的下载
                PlayerMoreOperationActivity.open(new DownloadFragment());
                break;
            case R.id.text_liked:// 我喜欢的
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("fromType", 6);
                favoriteFragment.setArguments(bundle);
                PlayerMoreOperationActivity.open(favoriteFragment);
                break;
        }
    }
}
