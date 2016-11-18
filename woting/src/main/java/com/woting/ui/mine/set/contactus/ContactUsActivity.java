package com.woting.ui.mine.set.contactus;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.woting.R;
import com.woting.ui.baseactivity.AppBaseActivity;

/**
 * 联系我们界面
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class ContactUsActivity extends AppBaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);
        setView();
    }

    // 初始化视图
    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:    // 返回
                finish();
                break;
        }
    }
}
