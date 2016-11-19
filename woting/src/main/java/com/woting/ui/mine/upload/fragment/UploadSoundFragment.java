package com.woting.ui.mine.upload.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.woting.R;
import com.woting.ui.home.program.fmlist.model.RankInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传的声音列表
 * Created by Administrator on 2016/11/19.
 */
public class UploadSoundFragment extends Fragment {
    private List<RankInfo> subList;
    private List<String> delList;
    private ArrayList<RankInfo> newList = new ArrayList<>();

    private View rootView;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_upload, container, false);
            listView = (ListView) rootView.findViewById(R.id.list_view);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super .onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
}
