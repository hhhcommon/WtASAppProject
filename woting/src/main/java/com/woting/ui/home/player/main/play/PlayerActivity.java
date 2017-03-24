package com.woting.ui.home.player.main.play;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.woting.R;
import com.woting.common.util.SequenceUUID;
import com.woting.common.util.ToastUtils;
import com.woting.ui.main.MainActivity;

/**
 * Play
 * Created by Administrator on 2017/3/6.
 */
public class PlayerActivity extends FragmentActivity {
    private static PlayerActivity context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        context = this;
        PlayerActivity.open(new PlayerFragment());

        View textMain = findViewById(R.id.tv_main);
        if (MainActivity.v) textMain.setVisibility(View.VISIBLE);
    }

    // 打开新的 Fragment
    public static void open(Fragment frg) {
        context.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_content, frg)
                .addToBackStack(SequenceUUID.getUUID())
                .commitAllowingStateLoss();
        if (context.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            MainActivity.hideOrShowTab(false);
        }

        Log.v("TAG", "Player open count -- > " + context.getSupportFragmentManager().getBackStackEntryCount());
    }

    // 关闭已经打开的 Fragment
    public static void close() {
        context.getSupportFragmentManager().popBackStackImmediate();
        if (context.getSupportFragmentManager().getBackStackEntryCount() == 1) {
            MainActivity.hideOrShowTab(true);
        }
    }

    private long tempTime;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            MainActivity.hideOrShowTab(true);
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
