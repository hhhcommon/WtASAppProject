package com.woting.ui.interphone.group.creatgroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.woting.R;
import com.woting.ui.base.baseactivity.AppBaseActivity;

/**
 * 创建群主页
 *
 * @author 辛龙
 *         2016年5月16日
 */
public class CreateGroupActivity extends AppBaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroupmain);
        setView();
    }

    private void setView() {
        findViewById(R.id.lin_groupmain_first).setOnClickListener(this);
        findViewById(R.id.lin_groupmain_second).setOnClickListener(this);
        findViewById(R.id.lin_groupmain_third).setOnClickListener(this);
        findViewById(R.id.head_left_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.lin_groupmain_first:
                Bundle bundle = new Bundle();
                bundle.putString("Type", "Open");
                Intent intent = new Intent(CreateGroupActivity.this, CreateGroupContentActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                break;
            case R.id.lin_groupmain_second:
                Bundle bundle1 = new Bundle();
                bundle1.putString("Type", "PassWord");
                Intent intent1 = new Intent(CreateGroupActivity.this, CreateGroupContentActivity.class);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case R.id.lin_groupmain_third:
                Bundle bundle2 = new Bundle();
                bundle2.putString("Type", "Validate");
                Intent intent2 = new Intent(CreateGroupActivity.this, CreateGroupContentActivity.class);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.activity_null);
    }
}
