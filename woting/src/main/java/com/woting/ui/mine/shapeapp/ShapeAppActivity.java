package com.woting.ui.mine.shapeapp;

import android.os.Bundle;
import android.view.View;

import com.woting.R;
import com.woting.ui.baseactivity.BaseActivity;

/**
 * 分享应用
 */
public class ShapeAppActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_app);

        initView();
    }

    // 初始化控件
    private void initView() {
        findViewById(R.id.image_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.image_back) {// 返回
            finish();
        }
    }
}
