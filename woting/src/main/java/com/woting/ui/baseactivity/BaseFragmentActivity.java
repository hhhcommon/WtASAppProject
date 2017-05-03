package com.woting.ui.baseactivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

/**
 * 作者：xinlong on 2016/10/25 21:18
 * 邮箱：645700751@qq.com
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
    protected Context context;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
/*        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);        // 透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    // 透明导航栏*/
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
