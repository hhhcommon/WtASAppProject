package com.woting.ui.home.player.programme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.woting.R;
import com.woting.ui.home.player.programme.adapter.ProgrammeAdapter;
import com.woting.ui.home.player.programme.model.program;

import java.util.ArrayList;

/**
 * 节目单列表
 */
public class ProgrammeFragment extends Fragment{
	private View rootView;
	private ListView mListView;
	private ArrayList<program> list;
	private FragmentActivity context;


	/**
	 * 创建 Fragment 实例
	 */
	public static Fragment instance(ArrayList<program> pro) {
		Fragment fragment = new ProgrammeFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("list", pro);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=getActivity();
		Bundle bundle = getArguments();                 //取值 用以判断加载的数据
		list = (ArrayList<program>) bundle.getSerializable("list");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_programme, container, false);
			mListView = (ListView) rootView.findViewById(R.id.listView);
		}
		if(list!=null&&list.size()>0){
			ProgrammeAdapter adapter = new ProgrammeAdapter(context, list);
			mListView.setAdapter(adapter);
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
