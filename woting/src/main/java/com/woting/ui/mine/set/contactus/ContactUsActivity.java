package com.woting.ui.mine.set.contactus;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.woting.R;
import com.woting.ui.baseactivity.BaseActivity;

/**
 * 联系我们界面
 * @author 辛龙
 * 2016年8月8日
 */
public class ContactUsActivity extends BaseActivity implements OnClickListener {

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
