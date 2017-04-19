package com.woting.ui.mine.set.downloadposition;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.home.download.service.DownloadClient;

/**
 * 下载位置
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class DownloadPositionActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadposition);
        setView();
    }

    // 初始化视图
    private void setView() {
        // 设置下载位置的路径 当前只能看
        if (!DownloadClient.DOWNLOAD_PATH.equals("") && DownloadClient.DOWNLOAD_PATH != null) {
            TextView textDownloadPosition = (TextView) findViewById(R.id.tv_downloadposition);
            textDownloadPosition.setText(DownloadClient.DOWNLOAD_PATH);
        }

        findViewById(R.id.head_left_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
