package com.woting.ui.interphone.notice.messagecenter.activity;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

import com.woting.R;
import com.woting.common.util.SequenceUUID;
import com.woting.ui.interphone.notice.messagecenter.fragment.MessageFragment;

/**
 * 消息中心列表主页
 * 作者：xinlong on 2016/5/5 21:18
 * 邮箱：645700751@qq.com
 */
public class MessageMainActivity extends FragmentActivity {
	private static MessageMainActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wt_message);
		context = this;
		// 适配顶栏样式
		setType();
		MessageMainActivity.open(new MessageFragment());
	}

	// 适配顶栏样式
	private void setType() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);        // 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    // 透明导航栏
	}

	/**
	 * 打开一个新的fragment
	 */
	public static void open(Fragment frg) {
		context.getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_content, frg)
				.addToBackStack(SequenceUUID.getUUID())
				.commit();
	}

	/**
	 * 关闭当前fragment
	 */
	public static void close() {
		context.getSupportFragmentManager().popBackStack();
	}

	public static void hideShow(Fragment from, Fragment to) {
		context.getSupportFragmentManager().beginTransaction().
				hide(from).show(to).commit();
	}

    @Override
    public void onBackPressed() {
        if (context.getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            close();
        }
    }

    // 设置 android app 的字体大小不受系统字体大小改变的影响
	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		Configuration config = new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config, res.getDisplayMetrics());
		return res;
	}
}