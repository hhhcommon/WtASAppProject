package com.woting.ui.download.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.woting.R;
import com.woting.common.util.ToastUtils;
import com.woting.ui.baseadapter.MyFragmentPagerAdapter;
import com.woting.ui.download.fragment.DownLoadCompleted;
import com.woting.ui.download.fragment.DownLoadUnCompleted;
import com.woting.ui.home.search.activity.SearchLikeActivity;
import com.woting.ui.interphone.notify.activity.NotifyNewsActivity;

import java.util.ArrayList;

/**
 * 下载主页
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class DownloadActivity extends FragmentActivity implements OnClickListener {
    private DownloadActivity context;

    private TextView textCompleted;
    private TextView textUncompleted;
    private ViewPager viewDownload;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_news:            // 跳转到新消息界面
                startActivity(new Intent(context, NotifyNewsActivity.class));
                break;
            case R.id.lin_find:            // 跳转到搜索界面
                startActivity(new Intent(context, SearchLikeActivity.class));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_download);
        context = this;
        setView();
        initViewPager();
        setType();
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
        TextView tv_main = (TextView) findViewById(R.id.tv_main);
        if (v) {

            tv_main.setVisibility(View.VISIBLE);
        } else {
            tv_main.setVisibility(View.GONE);
        }
    }

    // 设置界面
    private void setView() {
        findViewById(R.id.lin_news).setOnClickListener(this);
        findViewById(R.id.lin_find).setOnClickListener(this);

        textCompleted = (TextView) findViewById(R.id.tv_completed);
        textUncompleted = (TextView) findViewById(R.id.tv_uncompleted);
        viewDownload = (ViewPager) findViewById(R.id.viewpager);
    }

    private void initViewPager() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new DownLoadCompleted());
        fragmentList.add(new DownLoadUnCompleted());
        viewDownload.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
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

    long waitTime = 2000L;
    long touchTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                ToastUtils.show_allways(context, "再按一次退出");
                touchTime = currentTime;
            } else {
                MobclickAgent.onKillProcess(context);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 设置android app 的字体大小不受系统字体大小改变的影响
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
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
    protected void onDestroy() {
        super.onDestroy();
        textCompleted = null;
        textUncompleted = null;
        viewDownload = null;
        context = null;
        setContentView(R.layout.activity_null);
    }
}
