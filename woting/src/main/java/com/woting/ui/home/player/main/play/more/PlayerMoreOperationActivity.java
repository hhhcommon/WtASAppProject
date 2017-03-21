package com.woting.ui.home.player.main.play.more;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.woting.R;
import com.woting.common.util.SequenceUUID;
import com.woting.ui.main.MainActivity;

/**
 * 更多操作
 */
public class PlayerMoreOperationActivity extends FragmentActivity {
    private static PlayerMoreOperationActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_more_operation);
        context = this;
        View textMain = findViewById(R.id.tv_main);
        if (MainActivity.v) textMain.setVisibility(View.VISIBLE);
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
        }
    }

    // 关闭已经打开的 Fragment
    public static void close() {
        context.getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            MainActivity.change();
        } else {
            close();
        }
    }
}
