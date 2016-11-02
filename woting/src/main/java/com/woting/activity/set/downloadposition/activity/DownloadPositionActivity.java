package com.woting.activity.set.downloadposition.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.activity.download.service.DownloadService;

/**
 * 下载位置
 * @author 辛龙
 * 2016年8月8日
 */
public class DownloadPositionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadposition);
        setView();
    }

    // 初始化视图
    private void setView() {
        // 设置下载位置的路径 当前只能看
        if (!DownloadService.DOWNLOAD_PATH.equals("") && DownloadService.DOWNLOAD_PATH != null) {
            TextView textDownloadPosition = (TextView) findViewById(R.id.tv_downloadposition);
            textDownloadPosition.setText(DownloadService.DOWNLOAD_PATH);
        }

        findViewById(R.id.head_left_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
