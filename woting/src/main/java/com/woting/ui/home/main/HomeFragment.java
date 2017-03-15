package com.woting.ui.home.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.baseadapter.MyFragmentPagerAdapter;
import com.woting.ui.home.program.diantai.main.OnLineFragment;
import com.woting.ui.home.program.fenlei.fragment.FenLeiFragment;
import com.woting.ui.home.program.tuijian.fragment.RecommendFragment;
import com.woting.ui.home.search.main.SearchLikeFragment;
import com.woting.ui.interphone.notify.activity.NotifyNewsActivity;

import java.util.ArrayList;

/**
 * HomeFragment
 * Created by Administrator on 2017/3/4.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private Context context;

    private View rootView;
    private static TextView view1;// 推荐
    private static TextView view2;// 电台
    private static TextView view3;// 分类
    private static ViewPager mPager;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_news:// 消息
                Intent intentNews = new Intent(context, NotifyNewsActivity.class);
                startActivity(intentNews);
                break;
            case R.id.lin_find:// 搜索
                SearchLikeFragment fragment = new SearchLikeFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("FROM_TYPE", 1);// == 1 HomeFragment
                fragment.setArguments(bundle);
                HomeActivity.open(fragment);
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_wt_home, container, false);
            initView();
        }
        return rootView;
    }

    // 初始化视图
    private void initView() {
        view1 = (TextView) rootView.findViewById(R.id.tv_guid1);// 推荐
        view1.setOnClickListener(new txListener(0));

        view2 = (TextView) rootView.findViewById(R.id.tv_guid2);// 电台
        view2.setOnClickListener(new txListener(1));

        view3 = (TextView) rootView.findViewById(R.id.tv_guid3);// 分类
        view3.setOnClickListener(new txListener(2));

        rootView.findViewById(R.id.lin_news).setOnClickListener(this);// 消息
        rootView.findViewById(R.id.lin_find).setOnClickListener(this);// 搜索

        initViewPager();
    }

    private class txListener implements View.OnClickListener {
        private int index = 0;

        public txListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
            updateView(index);
        }
    }

    // 初始化 ViewPager
    private void initViewPager() {
        mPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mPager.setOffscreenPageLimit(1);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        RecommendFragment rf = new RecommendFragment();// 推荐
        OnLineFragment of = new OnLineFragment();// 电台
        FenLeiFragment ff = new FenLeiFragment();// 分类

        fragmentList.add(rf);
        fragmentList.add(of);
        fragmentList.add(ff);
        mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
        mPager.setCurrentItem(0);// 设置当前显示标签页为第一页 mPager
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageSelected(int arg0) {
            updateView(arg0);
        }
    }

    // 更新顶部标题视图
    private void updateView(int index) {
        if (index == 0) {// 推荐
            view1.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            view1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));

            view2.setTextColor(context.getResources().getColor(R.color.white));
            view2.setBackgroundColor(0);
//            view2.setBackgroundDrawable(context.getResources().getDrawable(0));

            view3.setTextColor(context.getResources().getColor(R.color.white));
//            view3.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
            view3.setBackgroundColor(0);
        } else if (index == 1) {// 电台
            view1.setTextColor(context.getResources().getColor(R.color.white));
//            view1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
            view1.setBackgroundColor(0);

            view2.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            view2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));

            view3.setTextColor(context.getResources().getColor(R.color.white));
//            view3.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
            view3.setBackgroundColor(0);
        } else if (index == 2) {// 分类
            view1.setTextColor(context.getResources().getColor(R.color.white));
//            view1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
            view1.setBackgroundColor(0);

            view2.setTextColor(context.getResources().getColor(R.color.white));
//            view2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
            view2.setBackgroundColor(0);

            view3.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            view3.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
}
