package com.woting.ui.download.activity;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.util.PhoneMessage;
import com.woting.ui.baseadapter.MyFragmentPagerAdapter;
import com.woting.ui.download.fragment.DownLoadAudioFragment;
import com.woting.ui.download.fragment.DownLoadSequFragment;
import com.woting.ui.download.fragment.DownLoadUnCompletedFragment;

import java.util.ArrayList;

/**
 * 下载主页
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class DownloadFragment extends Fragment implements OnClickListener {
    private FragmentActivity context;

    private View rootView;
    private TextView textSequ;// 下载的专辑
    private TextView textAudio;// 下载的节目
    private TextView textDown;// 正在下载中的节目
    private ViewPager viewDownload;

    private ImageView image;

    private int bmpW;
    private int offset;

    public static boolean isVisible = false;

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_download, container, false);
            rootView.setOnClickListener(this);

            initImage();
            setView();
            initViewPager();
            setType();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
    }

    // 适配顶栏样式
    private void setType() {
        String a = android.os.Build.VERSION.RELEASE;
        Log.e("系统版本号", a + "");
        Log.e("系统版本号截取", a.substring(0, a.indexOf(".")) + "");
        boolean v = false;
        if (Integer.parseInt(a.substring(0, a.indexOf("."))) >= 5) {
            v = true;
        }
        TextView tv_main = (TextView) rootView.findViewById(R.id.tv_main);
        if (v) {
            tv_main.setVisibility(View.VISIBLE);
        } else {
            tv_main.setVisibility(View.GONE);
        }
    }

    // 设置界面
    private void setView() {
        textSequ = (TextView) rootView.findViewById(R.id.text_sequ);// 下载的专辑
        textAudio = (TextView) rootView.findViewById(R.id.text_audio);// 下载的节目
        textDown = (TextView) rootView.findViewById(R.id.text_down);// 正在下载的节目
        viewDownload = (ViewPager) rootView.findViewById(R.id.viewpager);
    }

    private void initViewPager() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new DownLoadSequFragment());// 下载的专辑
        fragmentList.add(new DownLoadAudioFragment());// 下载的单体节目列表
        fragmentList.add(new DownLoadUnCompletedFragment());// 正在下载的节目

        viewDownload.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        viewDownload.setOnPageChangeListener(new MyOnPageChangeListener());
        viewDownload.setCurrentItem(0);
        viewDownload.setOffscreenPageLimit(1);

        textSequ.setOnClickListener(new DownloadClickListener(0));
        textAudio.setOnClickListener(new DownloadClickListener(1));
        textDown.setOnClickListener(new DownloadClickListener(2));
    }

    // 更新界面
    private void updateView(int index) {
        if (index == 0) {// 下载的专辑
            textSequ.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            textAudio.setTextColor(context.getResources().getColor(R.color.wt_login_third));
            textDown.setTextColor(context.getResources().getColor(R.color.wt_login_third));
        } else if (index == 1) {// 下载的节目
            textSequ.setTextColor(context.getResources().getColor(R.color.wt_login_third));
            textAudio.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            textDown.setTextColor(context.getResources().getColor(R.color.wt_login_third));
        } else if (index == 2) {// 正在下载的节目
            textDown.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            textAudio.setTextColor(context.getResources().getColor(R.color.wt_login_third));
            textSequ.setTextColor(context.getResources().getColor(R.color.wt_login_third));
        }
    }

    class DownloadClickListener implements OnClickListener {
        private int index = 0;

        public DownloadClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewDownload.setCurrentItem(index);        // 界面切换字体的改变
            updateView(index);
        }
    }

    // 动态设置 cursor 的宽
    public void initImage() {
        image = (ImageView) rootView.findViewById(R.id.cursor);
        ViewGroup.LayoutParams lp = image.getLayoutParams();
        lp.width = (PhoneMessage.ScreenWidth / 3);
        image.setLayoutParams(lp);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.left_personal_bg).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 3 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    class MyOnPageChangeListener implements OnPageChangeListener {
        private int one = offset * 2 + bmpW;    // 两个相邻页面的偏移量
        private int currIndex;

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);// 平移动画
            currIndex = arg0;
            animation.setFillAfter(true);   // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);     // 动画持续时间0.2秒
            image.startAnimation(animation);// 是用ImageView来显示动画的
            updateView(arg0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isVisible = false;
        textSequ = null;
        textDown = null;
        viewDownload = null;
        context = null;
    }
}
