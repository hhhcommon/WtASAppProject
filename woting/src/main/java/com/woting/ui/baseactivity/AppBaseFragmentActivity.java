package com.woting.ui.baseactivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import com.woting.common.manager.MyActivityManager;

/**
 * 作者：xinlong on 2016/10/25 21:18
 * 邮箱：645700751@qq.com
 */
public abstract class AppBaseFragmentActivity extends BaseFragmentActivity {
    protected Context context;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.popOneActivity(this);
    }
}
