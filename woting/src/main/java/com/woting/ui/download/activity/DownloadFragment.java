package com.woting.ui.download.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.baseadapter.MyFragmentPagerAdapter;
import com.woting.ui.download.fragment.DownLoadCompleted;
import com.woting.ui.download.fragment.DownLoadUnCompleted;

import java.util.ArrayList;

/**
 * 下载主页
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class DownloadFragment extends Fragment implements OnClickListener {
    private FragmentActivity context;

    private View rootView;
    private TextView textCompleted;
    private TextView textUncompleted;
    private ViewPager viewDownload;

    public static boolean isVisible = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_news:            // 跳转到新消息界面
//                startActivity(new Intent(context, NotifyNewsActivity.class));
                break;
            case R.id.lin_find:            // 跳转到搜索界面
//                startActivity(new Intent(context, SearchLikeFragment.class));
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_download, container, false);
            rootView.setOnClickListener(this);

            setView();
            initViewPager();
            setType();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
    }

    // 适配顶栏样式
    private void setType() {
        String a = android.os.Build.VERSION.RELEASE;
        Log.e("系统版本号", a + "");
        Log.e("系统版本号截取", a.substring(0, a.indexOf(".")) + "");
        boolean v = false;
        if (Integer.parseInt(a.substring(0, a.indexOf("."))) >= 5) {
            v = true;
        }
        TextView tv_main = (TextView) rootView.findViewById(R.id.tv_main);
        if (v) {
            tv_main.setVisibility(View.VISIBLE);
        } else {
            tv_main.setVisibility(View.GONE);
        }
    }

    // 设置界面
    private void setView() {
        rootView.findViewById(R.id.lin_news).setOnClickListener(this);
        rootView.findViewById(R.id.lin_find).setOnClickListener(this);

        textCompleted = (TextView) rootView.findViewById(R.id.tv_completed);
        textUncompleted = (TextView) rootView.findViewById(R.id.tv_uncompleted);
        viewDownload = (ViewPager) rootView.findViewById(R.id.viewpager);
    }

    private void initViewPager() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new DownLoadCompleted());
        fragmentList.add(new DownLoadUnCompleted());
        viewDownload.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        viewDownload.setOnPageChangeListener(new MyOnPageChangeListener());
        viewDownload.setCurrentItem(0);
        viewDownload.setOffscreenPageLimit(1);
        textCompleted.setOnClickListener(new DownloadClickListener(0));
        textUncompleted.setOnClickListener(new DownloadClickListener(1));
    }

    // 更新界面
    private void updateView(int index) {
        if (index == 0) {
            textCompleted.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            textCompleted.setBackgroundResource(R.drawable.color_wt_circle_home_white);
            textUncompleted.setTextColor(context.getResources().getColor(R.color.white));
            textUncompleted.setBackgroundResource(R.drawable.color_wt_circle_orange);
        } else if (index == 1) {
            textUncompleted.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            textUncompleted.setBackgroundResource(R.drawable.color_wt_circle_home_white);
            textCompleted.setTextColor(context.getResources().getColor(R.color.white));
            textCompleted.setBackgroundResource(R.drawable.color_wt_circle_orange);
        }
    }

    class DownloadClickListener implements OnClickListener {
        private int index = 0;

        public DownloadClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewDownload.setCurrentItem(index);        // 界面切换字体的改变
            updateView(index);
        }
    }

    class MyOnPageChangeListener implements OnPageChangeListener {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        isVisible = false;
        textCompleted = null;
        textUncompleted = null;
        viewDownload = null;
        context = null;
    }
}
