package com.woting.ui.music.live.liveparade;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.base.baseactivity.AppBaseActivity;
import com.woting.ui.music.live.model.live;

/**
 * 节目预告
 * 作者：xinlong on 2017/4/26 16:18
 * 邮箱：645700751@qq.com
 */
public class LiveParadeActivity extends AppBaseActivity implements OnClickListener {

    private TextView tv_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_parade);
        live news = getData();// 获取数据
        setView(news);        // 初始化视图+设置数据
    }

    // 获取数据
    private live getData() {
        live news = (live) this.getIntent().getSerializableExtra("data");
        return news;
    }

    // 初始化视图+设置数据
    private void setView(live news) {
        findViewById(R.id.head_left_btn).setOnClickListener(this);                // 返回
        TextView tv_programName = (TextView) findViewById(R.id.tv_programName);   // 节目名称
        TextView tv_programDesc = (TextView) findViewById(R.id.tv_programDesc);   // 节目描述
        TextView tv_playTime = (TextView) findViewById(R.id.tv_playTime);         // 节目播出时间
        TextView tv_orderNum = (TextView) findViewById(R.id.tv_orderNum);         // 已预约人数
        TextView tv_anchorName = (TextView) findViewById(R.id.tv_anchorName);     // 主播名
        tv_order = (TextView) findViewById(R.id.tv_order);               // 预约
        tv_order.setOnClickListener(this);
        ImageView image_portrait = (ImageView) findViewById(R.id.image_portrait); // 主播头像
        ImageView img_cover = (ImageView) findViewById(R.id.img_cover);           // 遮罩
        ImageView image_sex = (ImageView) findViewById(R.id.image_sex);           // 主播性别

        if (news != null) {
            // 有数据的时候在这适配
            String programName = news.getTitle();
            tv_programName.setText(programName);

            String programDesc = news.getTitle();// 错误
            tv_programDesc.setText(programDesc);

            String orderNum = news.getAudience_count();// 错误
            tv_orderNum.setText(orderNum);

            String anchorName = news.getOwner().getName();//
            tv_anchorName.setText(anchorName);


        } else {
            // 没数据的时候在这适配
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:    // 返回
                finish();
                break;
            case R.id.tv_order:    // 返回
                break;

        }
    }
}
