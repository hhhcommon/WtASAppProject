package com.woting.ui.home.program.album.anchor;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.baseactivity.AppBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 主播详情界面
 */
public class AnchorDetailsActivity extends AppBaseActivity implements View.OnClickListener {
    private XListView listAnchor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_details);

        initView();
        initData();
    }

    // 初始化视图
    private void initView() {
        View headView = LayoutInflater.from(context).inflate(R.layout.headview_activity_anchor_details, null);

        TextView textAnchorName = (TextView) findViewById(R.id.text_anchor_name);// 标题  即主播 Name
        textAnchorName.setText("罗振宇");

        listAnchor = (XListView) findViewById(R.id.list_anchor);// 主播的节目列表
        listAnchor.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listAnchor.setHeaderDividersEnabled(false);
        listAnchor.setPullRefreshEnable(false);
        listAnchor.setPullLoadEnable(false);
        listAnchor.addHeaderView(headView);

        initEvent();
    }

    // 初始化数据
    private void initData() {
        List<String> list = getAnchorList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
        listAnchor.setAdapter(adapter);
    }

    // 初始化点击事件
    private void initEvent() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);// 返回
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
        }
    }

    private List<String> getAnchorList() {
        List<String> list = new ArrayList<>();

        for(int i=0; i<10; i++) {
            list.add("主播的节目_" + i);
        }

        return list;
    }
}
