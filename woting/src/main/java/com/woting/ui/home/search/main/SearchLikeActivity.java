package com.woting.ui.home.search.main;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.SequenceUUID;
import com.woting.ui.main.MainActivity;

/**
 * 界面搜索界面
 * @author 辛龙
 * 2016年4月16日
 */
public class SearchLikeActivity extends FragmentActivity {
    private static SearchLikeActivity context;
    public static String fromType = "-1";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchlike);
        context = this;
        setType();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastConstants.FROM_ACTIVITY);
        registerReceiver(mReceiver, mFilter);

        open(new SearchLikeFragment());
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
        View tv_main = findViewById(R.id.tv_main);
        if (v) {
            tv_main.setVisibility(View.VISIBLE);
        } else {
            tv_main.setVisibility(View.GONE);
        }
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
    }

    // 关闭已经打开的 Fragment
    public static void close() {
        context.getSupportFragmentManager().popBackStackImmediate();// 立即删除回退栈中的数据
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (fromType.equals("HOME")) {
                MainActivity.setViewOne();
            } else if (fromType.equals("PLAY")) {
                MainActivity.change();
            }
        } else {
            close();
        }
    }

    // 广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.FROM_ACTIVITY)) {
                fromType = intent.getStringExtra("fromType");
                Log.v("TAG", "fromType -- > > " + fromType);
            }
        }
    };
}
