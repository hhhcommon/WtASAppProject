package com.woting.ui.music.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.ui.baseadapter.MyFragmentPagerAdapter;
import com.woting.ui.music.live.main.LiveFragment;
import com.woting.ui.music.radio.main.OnLineFragment;
import com.woting.ui.music.classify.fragment.FenLeiFragment;
import com.woting.ui.music.recommended.RecommendFragment;
import com.woting.ui.interphone.notice.messagecenter.activity.MessageMainActivity;
import com.woting.ui.main.MainActivity;

import java.util.ArrayList;

/**
 * HomeFragment
 * Created by Administrator on 2017/3/4.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity context;

    private View rootView;
    private static TextView view1;// 推荐
    private static TextView view2;// 电台
    private static TextView view3;// 分类
    private static TextView view4;// 直播

    private static ViewPager mPager;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_news:// 消息
                Intent intentNews = new Intent(context, MessageMainActivity.class);
                startActivity(intentNews);
                break;
            case R.id.lin_find:// 搜索
                MainActivity.setViewSeven();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra(StringConstant.FROM_TYPE, IntegerConstant.TAG_HOME);
                        intent.setAction(BroadcastConstants.FROM_ACTIVITY);
                        context.getApplicationContext().sendBroadcast(intent);
                    }
                }, 500);
                break;
        }
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
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

        view4 = (TextView) rootView.findViewById(R.id.tv_guid4);// 直播
        view4.setOnClickListener(new txListener(3));

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
        OnLineFragment of = new OnLineFragment();      // 电台
        FenLeiFragment ff = new FenLeiFragment();      // 分类
        LiveFragment lv = new LiveFragment();          // 直播
        fragmentList.add(rf);
        fragmentList.add(of);
        fragmentList.add(ff);
        fragmentList.add(lv);
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
            view3.setTextColor(context.getResources().getColor(R.color.white));
            view3.setBackgroundColor(0);
            view4.setTextColor(context.getResources().getColor(R.color.white));
            view4.setBackgroundColor(0);
        } else if (index == 1) {// 电台
            view1.setTextColor(context.getResources().getColor(R.color.white));
            view1.setBackgroundColor(0);
            view2.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            view2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
            view3.setTextColor(context.getResources().getColor(R.color.white));
            view3.setBackgroundColor(0);
            view4.setTextColor(context.getResources().getColor(R.color.white));
            view4.setBackgroundColor(0);
        } else if (index == 2) {// 分类
            view1.setTextColor(context.getResources().getColor(R.color.white));
            view1.setBackgroundColor(0);
            view2.setTextColor(context.getResources().getColor(R.color.white));
            view2.setBackgroundColor(0);
            view3.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            view3.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
            view4.setTextColor(context.getResources().getColor(R.color.white));
            view4.setBackgroundColor(0);
        }else if (index == 3) {// 分类
            view1.setTextColor(context.getResources().getColor(R.color.white));
            view1.setBackgroundColor(0);
            view2.setTextColor(context.getResources().getColor(R.color.white));
            view2.setBackgroundColor(0);
            view3.setTextColor(context.getResources().getColor(R.color.white));
            view3.setBackgroundColor(0);
            view4.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            view4.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
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
