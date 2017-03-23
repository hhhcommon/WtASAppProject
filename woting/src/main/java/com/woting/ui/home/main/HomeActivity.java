package com.woting.ui.home.main;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.woting.R;
import com.woting.common.util.SequenceUUID;
import com.woting.common.util.ToastUtils;
import com.woting.ui.main.MainActivity;

/**
 * 内容主页
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class HomeActivity extends FragmentActivity {
    private static HomeActivity context;

    public static boolean isHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wt_home);
        context = this;
        View textMain = findViewById(R.id.tv_main);
        if (MainActivity.v) textMain.setVisibility(View.VISIBLE);
        open(new HomeFragment());
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

    // 打开新的 Fragment
    public static void open(Fragment frg) {
        context.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_content, frg)
                .addToBackStack(SequenceUUID.getUUID())
                .commitAllowingStateLoss();
        if (context.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            MainActivity.hideOrShowTab(false);
            isHide = true;
        }
    }

    // 关闭已经打开的 Fragment
    public static void close() {
        context.getSupportFragmentManager().popBackStackImmediate();// 立即删除回退栈中的数据
        if (context.getSupportFragmentManager().getBackStackEntryCount() == 1) {
            MainActivity.hideOrShowTab(true);
            isHide = false;
        }
    }

    private long tempTime;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            long time = System.currentTimeMillis();
            if (time - tempTime <= 2000) {
                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                tempTime = time;
                ToastUtils.show_always(context, "再按一次退出");
            }
        } else {
            close();
        }
    }
}
