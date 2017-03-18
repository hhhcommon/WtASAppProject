package com.woting.ui.download.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.woting.R;

/**
 * 下载的节目列表
 * Created by Administrator on 2017/3/17.
 */
public class DownLoadAudioFragment extends Fragment {
    private FragmentActivity context;

    private View rootView;
    private ListView listView;// 已经下载的节目列表

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_download_audio, container, false);
            initView();
        }
        return rootView;
    }

    // 初始化视图
    private void initView() {
        listView = (ListView) rootView.findViewById(R.id.list_view);// 已经下载的节目列表
    }
}
