package com.woting.ui.home.main;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.woting.R;
import com.woting.common.service.SocketService;
import com.woting.common.util.ToastUtils;
import com.woting.ui.baseadapter.MyFragmentPagerAdapter;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.program.main.ProgramFragment;
import com.woting.ui.home.search.activity.SearchLikeActivity;
import com.woting.ui.interphone.notify.activity.NotifyNewsActivity;
import com.woting.ui.main.MainActivity;

import java.util.ArrayList;

/**
 * 内容主页
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class HomeActivity extends FragmentActivity {
    private static TextView view1;
    private static TextView view2;
    private static HomeActivity context;
    private static ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wt_home);
        context = this;
        InitTextView();
        InitViewPager();
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

    private void InitTextView() {
        view1 = (TextView) findViewById(R.id.tv_guid1);
        view2 = (TextView) findViewById(R.id.tv_guid2);
        LinearLayout lin_news = (LinearLayout) findViewById(R.id.lin_news);
        lin_news.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotifyNewsActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout lin_find = (LinearLayout) findViewById(R.id.lin_find);
        lin_find.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到搜索界面  原来的代码 要加在这里
                Intent intent = new Intent(context, SearchLikeActivity.class);
                startActivity(intent);
            }
        });
        view1.setOnClickListener(new txListener(0));
        view2.setOnClickListener(new txListener(1));
    }

    public class txListener implements OnClickListener {
        private int index = 0;

        public txListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
            if (index == 0) {
                view1.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                view2.setTextColor(context.getResources().getColor(R.color.white));
                view1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
                view2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
            } else if (index == 1) {
                view1.setTextColor(context.getResources().getColor(R.color.white));
                view2.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                view1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
                view2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
            }
        }
    }

    /*
     * 初始化ViewPager
     */
    public void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setOffscreenPageLimit(1);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        PlayerFragment playFragment = new PlayerFragment();
        ProgramFragment newsFragment = new ProgramFragment();
        fragmentList.add(playFragment);
        fragmentList.add(newsFragment);
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());    // 页面变化时的监听器
        mPager.setCurrentItem(0);    // 设置当前显示标签页为第一页mPager
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                view1.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                view2.setTextColor(context.getResources().getColor(R.color.white));
                view1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
                view2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
            } else if (arg0 == 1) {
                view1.setTextColor(context.getResources().getColor(R.color.white));
                view2.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                view1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
                view2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
            }
        }
    }

    public static void UpdateViewPager() {
        mPager.setCurrentItem(0);// 设置当前显示标签页为第一页mPager
        view1.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
        view2.setTextColor(context.getResources().getColor(R.color.white));
        view1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_home_white));
        view2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_wt_circle_orange));
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

    /*
     * 手机实体返回按键的处理  与onbackpress同理
     */
    long waitTime = 2000;
    long touchTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                ToastUtils.show_always(HomeActivity.this, "再按一次退出");
                touchTime = currentTime;
            } else {
                SocketService.workStop(false);
                MainActivity.stop();
                MobclickAgent.onKillProcess(this);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
