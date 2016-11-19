package com.woting.ui.mine.upload;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.util.PhoneMessage;
import com.woting.ui.baseactivity.AppBaseFragmentActivity;
import com.woting.ui.mine.upload.fragment.UploadSequFragment;
import com.woting.ui.mine.upload.fragment.UploadSoundFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的上传
 * Created by Administrator on 2016/11/18.
 */
public class UploadActivity extends AppBaseFragmentActivity implements View.OnClickListener {
    private List<Fragment> fragmentList;

    private ViewPager viewPager;
    private ImageView image;
    private TextView textSequ;// 专辑
    private TextView textSound;// 声音

    private int bmpW;// 横线图片宽度
    private int offset;// 图片移动的偏移量
    private int currIndex;// 当前界面编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initView();
    }

    // 初始化视图
    private void initView() {
        initImage();

        textSequ = (TextView) findViewById(R.id.text_sequ);// 专辑
        textSequ.setOnClickListener(this);

        textSound = (TextView) findViewById(R.id.text_sound);// 声音
        textSound.setOnClickListener(this);

        initViewPager();
    }

    // 初始化 ViewPager
    private void initViewPager() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new UploadSequFragment());
        fragmentList.add(new UploadSoundFragment());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());    // 页面变化时的监听器
        viewPager.setCurrentItem(0);                                        // 设置当前显示标签页为第一页
    }

    // 设置 cursor 的宽
    public void initImage() {
        image = (ImageView) findViewById(R.id.cursor);
        ViewGroup.LayoutParams lp = image.getLayoutParams();
        lp.width = (PhoneMessage.ScreenWidth / 2);
        image.setLayoutParams(lp);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.left_personal_bg).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 2 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_sequ:// 专辑
                viewPager.setCurrentItem(0);
                break;
            case R.id.text_sound:// 声音
                viewPager.setCurrentItem(1);
                break;
        }
    }

    // ViewPager 设置适配器
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    // ViewPager 监听事件设置
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private int one = offset * 2 + bmpW;    // 两个相邻页面的偏移量

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);// 平移动画
            currIndex = arg0;
            animation.setFillAfter(true);        // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);          // 动画持续时间 0.2 秒
            image.startAnimation(animation);     // 是用 ImageView 来显示动画的
            if (arg0 == 0) { // 全部
                textSequ.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                textSound.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (arg0 == 1) { // 声音
                textSound.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                textSequ.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            }
        }
    }
}
