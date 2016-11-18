package com.woting.ui.baseactivity;

import android.content.Context;
import android.os.Bundle;

import com.woting.common.manager.MyActivityManager;

public abstract class AppBaseActivity extends BaseActivity {
    protected Context context;

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
