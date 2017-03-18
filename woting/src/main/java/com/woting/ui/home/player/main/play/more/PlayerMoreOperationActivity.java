package com.woting.ui.home.player.main.play.more;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.woting.R;
import com.woting.common.util.SequenceUUID;
import com.woting.common.util.ToastUtils;
import com.woting.ui.main.MainActivity;

/**
 * 更多操作
 */
public class PlayerMoreOperationActivity extends FragmentActivity {
    private static PlayerMoreOperationActivity context;
    public static boolean isVisible = true;// 是否可见

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_more_operation);

        context = this;
        open(new PlayerMoreOperationFragment());
    }

    // 打开新的 Fragment
    public static void open(Fragment frg) {
        context.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_content, frg)
                .addToBackStack(SequenceUUID.getUUID())
                .commit();
        if (context.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            MainActivity.hideOrShowTab(false);
            isVisible = false;
        }
    }

    // 关闭已经打开的 Fragment
    public static void close() {
        context.getSupportFragmentManager().popBackStackImmediate();
        if (context.getSupportFragmentManager().getBackStackEntryCount() == 1) {
            MainActivity.hideOrShowTab(true);
            isVisible = true;
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
                ToastUtils.show_always(this, "再按一次退出");
            }
        } else {
            close();
        }
    }
}