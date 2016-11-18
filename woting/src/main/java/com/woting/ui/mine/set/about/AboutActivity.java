package com.woting.ui.mine.set.about;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.util.PhoneMessage;
import com.woting.ui.baseactivity.AppBaseActivity;

/**
 * 关于
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class AboutActivity extends AppBaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setView();
    }

    // 初始化视图
    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);      // 返回
        TextView textVersion = (TextView) findViewById(R.id.tv_verson); // 版本号
        textVersion.setText(PhoneMessage.appVersonName);
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
