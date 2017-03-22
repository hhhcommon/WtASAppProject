package com.woting.ui.mine.favorite.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.ui.baseadapter.MyFragmentPagerAdapter;
import com.woting.ui.home.player.main.play.more.PlayerMoreOperationActivity;
import com.woting.ui.mine.favorite.fragment.RadioFragment;
import com.woting.ui.mine.favorite.fragment.SequFragment;
import com.woting.ui.mine.favorite.fragment.SoundFragment;
import com.woting.ui.mine.favorite.fragment.TTSFragment;
import com.woting.ui.mine.favorite.fragment.TotalFragment;
import com.woting.ui.mine.main.MineActivity;

import java.util.ArrayList;

/**
 * 我喜欢的
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class FavoriteFragment extends Fragment implements OnClickListener {
    private static FragmentActivity context;
    private MyBroadcast mBroadcast;
    private TotalFragment totalFragment;
    private SequFragment sequfragment;
    private SoundFragment soundfragment;
    private RadioFragment radiofragment;
    private TTSFragment ttsfragment;

    private View rootView;
    private static TextView tv_total;
    private static TextView tv_sequ;
    private static TextView tv_sound;
    private static TextView tv_radio;
    private static TextView tv_tts;
    private static ViewPager mPager;
    private static TextView tv_qingkong;
    private static TextView tv_bianji;    // 加一个 bol 值，如果 key 值为 0 是为编辑状态，为 1 时显示完成
    private Dialog confirmDialog;
    private Dialog DelDialog;
    private ImageView image;
    private ImageView imageAllCheck;

    public static final String VIEW_UPDATE = "VIEW_UPDATE";
    public static final String SET_ALL_IMAGE = "SET_ALL_IMAGE";// 全选
    public static final String SET_NOT_ALL_IMAGE = "SET_NOT_ALL_IMAGE";// 非全选
    public static final String SET_NOT_LOAD_REFRESH = "SET_NOT_LOAD_REFRESH";// 禁止刷新加载
    public static final String SET_LOAD_REFRESH = "SET_LOAD_REFRESH";// 允许刷新加载
    public static int type;

    private static int lastIndex = -1;
    private static int currentIndex = 0;// 标记当前viewpager显示的页面
    private int bmpW;
    private int offset;
    private int dialogFlag = 0;// 编辑全选状态的变量0为未选中，1为选中
    private int textFlag = 0;// 标记右上角text的状态0为编辑，1为取消
    public static boolean isEdit = false;// 是否为编辑状态

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        // 注册广播
        mBroadcast = new MyBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FavoriteFragment.SET_ALL_IMAGE);
        intentFilter.addAction(FavoriteFragment.SET_NOT_ALL_IMAGE);
        context.registerReceiver(mBroadcast, intentFilter);

        Bundle bundle = getArguments();
        type = bundle.getInt(StringConstant.FROM_TYPE);// 根据 TAB 标签传参
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_favorite, container, false);

            initImage();
            delDialog();
            confirmDialog();
            setView();
        }
        return rootView;
    }

    // 初始化视图
    private void setView() {
        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);      // 返回按钮
        
        tv_total = (TextView) rootView.findViewById(R.id.tv_total);              // 全部
        tv_total.setOnClickListener(new txListener(0));
        
        tv_sequ = (TextView) rootView.findViewById(R.id.tv_sequ);                // 专辑
        tv_sequ.setOnClickListener(new txListener(1));
        
        tv_sound = (TextView) rootView.findViewById(R.id.tv_sound);              // 声音
        tv_sound.setOnClickListener(new txListener(2));
        
        tv_radio = (TextView) rootView.findViewById(R.id.tv_radio);              // 电台
        tv_radio.setOnClickListener(new txListener(3));
        
        tv_tts = (TextView) rootView.findViewById(R.id.tv_tts);                  // TTS
        tv_tts.setOnClickListener(new txListener(4));
        
        mPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mPager.setOffscreenPageLimit(1);
        
        tv_qingkong = (TextView) rootView.findViewById(R.id.tv_qingkong);
        tv_qingkong.setOnClickListener(this);
        
        tv_bianji = (TextView) rootView.findViewById(R.id.tv_bianji);
        tv_bianji.setOnClickListener(this);

        initViewPager();
    }

    // 初始化 ViewPager
    private void initViewPager() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        totalFragment = new TotalFragment();
        sequfragment = new SequFragment();
        soundfragment = new SoundFragment();
        radiofragment = new RadioFragment();
        ttsfragment = new TTSFragment();
        fragmentList.add(totalFragment);
        fragmentList.add(sequfragment);
        fragmentList.add(soundfragment);
        fragmentList.add(radiofragment);
        fragmentList.add(ttsfragment);
        mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());    // 页面变化时的监听器
        mPager.setCurrentItem(0);                                        // 设置当前显示标签页为第
    }

    public static void updateViewPager(String mediaType) {
        int index = 0;
        if (mediaType != null && !mediaType.equals("")) {
            if (mediaType.equals("SEQU")) {
                index = 1;
            } else if (mediaType.equals("AUDIO")) {
                index = 2;
            } else if (mediaType.equals("RADIO")) {
                index = 3;
            } else if (mediaType.equals("TTS")) {
                index = 4;
            }
            mPager.setCurrentItem(index);
            currentIndex = index;
            viewChange(index);
        }
    }

    // 动态设置cursor的宽
    public void initImage() {
        image = (ImageView) rootView.findViewById(R.id.cursor);
        LayoutParams lp = image.getLayoutParams();
        lp.width = (PhoneMessage.ScreenWidth / 5);
        image.setLayoutParams(lp);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.left_personal_bg).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 5 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                if (type == 5) {// Mine
                    MineActivity.close();
                } else if (type == 6) {
                    PlayerMoreOperationActivity.close();
                }
                break;
            case R.id.tv_qingkong:// 清空
                handleData(0);
                break;
            case R.id.tv_bianji:// 编辑
                if (textFlag == 0) {
                    handleData(1);
                } else {
                    isEdit = false;
                    textFlag = 0;
                    tv_bianji.setText("编辑");
                    handleData(2);
                    if (DelDialog.isShowing()) {
                        DelDialog.dismiss();
                        imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
                        dialogFlag = 0;
                    }
                }
                break;
        }
    }

    // 四种参数 1为打开该界面的隐藏栏，0为收起隐藏栏，2为全选，3为取消全选
    private void handleData(int type) {
        if (currentIndex == 0) {
            // 全部 先调total的查询全部方法 返回是否有值的弹窗
            int sum = totalFragment.getdelitemsum();
            if (type == 0) {
                if (sum != 0) {
                    confirmDialog.show();
                } else {
                    ToastUtils.show_always(context, "您还没有喜欢的数据");
                }
            }
        } else if (currentIndex == 1) {
            if (type == 1) {// 打开view
                boolean flag = sequfragment.changeviewtype(1);
                if (flag) {
                    isEdit = true;
                    sequfragment.setViewVisibility();
                    context.sendBroadcast(new Intent(FavoriteFragment.SET_NOT_LOAD_REFRESH));

                    textFlag = 1;
                    tv_bianji.setText("取消");
                    if (DelDialog != null) {
                        DelDialog.show();
                    }
                } else {
                    ToastUtils.show_always(context, "当前页无数据");
                }
            } else if (type == 2) {// 隐藏view
                sequfragment.changeviewtype(0);
                sequfragment.setViewHint();
                context.sendBroadcast(new Intent(FavoriteFragment.SET_LOAD_REFRESH));

            } else if (type == 3) {// 全选
                sequfragment.changechecktype(1);
            } else if (type == 4) {// 解除全选
                sequfragment.changechecktype(0);
            } else if (type == 5) {// 删除
                if (sequfragment.getdelitemsum() == 0) {
                    ToastUtils.show_always(context, "请选择您要删除的数据");
                    return;
                }
                if (DelDialog.isShowing()) {
                    DelDialog.dismiss();
                    imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
                    dialogFlag = 0;
                }
                textFlag = 0;
                tv_bianji.setText("编辑");
                sequfragment.delitem();
                sequfragment.setViewHint();
                context.sendBroadcast(new Intent(FavoriteFragment.SET_LOAD_REFRESH));
            }
        } else if (currentIndex == 2) {
            // 声音
            if (type == 1) {// 打开view
                boolean flag = soundfragment.changeviewtype(1);
                if (flag) {
                    isEdit = true;
                    soundfragment.setViewVisibility();
                    context.sendBroadcast(new Intent(FavoriteFragment.SET_NOT_LOAD_REFRESH));
                    textFlag = 1;
                    tv_bianji.setText("取消");
                    if (DelDialog != null) {
                        DelDialog.show();
                    }
                } else {
                    ToastUtils.show_always(context, "当前页无数据");
                }
            } else if (type == 2) {// 隐藏view
                soundfragment.changeviewtype(0);
                soundfragment.setViewHint();
                context.sendBroadcast(new Intent(FavoriteFragment.SET_LOAD_REFRESH));
            } else if (type == 3) {// 全选
                soundfragment.changechecktype(1);
            } else if (type == 4) {// 解除全选
                soundfragment.changechecktype(0);
            } else if (type == 5) {// 删除
                if (soundfragment.getdelitemsum() == 0) {
                    ToastUtils.show_always(context, "请选择您要删除的数据");
                    return;
                }
                if (DelDialog.isShowing()) {
                    DelDialog.dismiss();
                    imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
                    dialogFlag = 0;
                }
                textFlag = 0;
                tv_bianji.setText("编辑");
                soundfragment.delitem();
                soundfragment.setViewHint();
                context.sendBroadcast(new Intent(FavoriteFragment.SET_LOAD_REFRESH));
            }
        } else if (currentIndex == 3) {
            // 电台
            if (type == 1) {// 打开view
                boolean flag = radiofragment.changeviewtype(1);
                if (flag) {
                    isEdit = true;
                    radiofragment.setViewVisibility();
                    context.sendBroadcast(new Intent(FavoriteFragment.SET_NOT_LOAD_REFRESH));
                    textFlag = 1;
                    tv_bianji.setText("取消");
                    if (DelDialog != null) {
                        DelDialog.show();
                    }
                } else {
                    ToastUtils.show_always(context, "当前页无数据");
                }
            } else if (type == 2) {// 隐藏view
                radiofragment.changeviewtype(0);
                radiofragment.setViewHint();
                context.sendBroadcast(new Intent(FavoriteFragment.SET_LOAD_REFRESH));
            } else if (type == 3) {// 全选
                radiofragment.changechecktype(1);
            } else if (type == 4) {// 解除全选
                radiofragment.changechecktype(0);
            } else if (type == 5) {// 删除
                if (radiofragment.getdelitemsum() == 0) {
                    ToastUtils.show_always(context, "请选择您要删除的数据");
                    return;
                }
                if (DelDialog.isShowing()) {
                    DelDialog.dismiss();
                    imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
                    dialogFlag = 0;
                }
                textFlag = 0;
                tv_bianji.setText("编辑");
                radiofragment.delitem();
                radiofragment.setViewHint();
                context.sendBroadcast(new Intent(FavoriteFragment.SET_LOAD_REFRESH));
            }
        } else if (currentIndex == 4) {
            // TTS
            if (type == 1) {// 打开view
                boolean flag = ttsfragment.changeviewtype(1);
                if (flag) {
                    isEdit = true;
                    ttsfragment.setViewVisibility();
                    context.sendBroadcast(new Intent(FavoriteFragment.SET_NOT_LOAD_REFRESH));
                    textFlag = 1;
                    tv_bianji.setText("取消");
                    if (DelDialog != null) {
                        DelDialog.show();
                    }
                } else {
                    ToastUtils.show_always(context, "当前页无数据");
                }
            } else if (type == 2) {// 隐藏view
                ttsfragment.changeviewtype(0);
                ttsfragment.setViewHint();
                context.sendBroadcast(new Intent(FavoriteFragment.SET_LOAD_REFRESH));
            } else if (type == 3) {// 全选
                ttsfragment.changechecktype(1);
            } else if (type == 4) {// 解除全选
                ttsfragment.changechecktype(0);
            } else if (type == 5) {// 删除
                if (ttsfragment.getdelitemsum() == 0) {
                    ToastUtils.show_always(context, "请选择您要删除的数据");
                    return;
                }
                if (DelDialog.isShowing()) {
                    DelDialog.dismiss();
                    imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
                    dialogFlag = 0;
                }
                textFlag = 0;
                tv_bianji.setText("编辑");
                ttsfragment.delitem();
                ttsfragment.setViewHint();
                context.sendBroadcast(new Intent(FavoriteFragment.SET_LOAD_REFRESH));
            }
        }
    }

    // 界面更新
    public static void viewChange(int index) {
        if (index == 0) {
            tv_total.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            tv_sequ.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sound.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_radio.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_tts.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_bianji.setVisibility(View.GONE);
            if(TotalFragment.isData) {
                tv_qingkong.setVisibility(View.VISIBLE);
            } else {
                tv_qingkong.setVisibility(View.GONE);
            }
        } else if (index == 1) {
            tv_total.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sequ.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            tv_sound.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_radio.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_tts.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_qingkong.setVisibility(View.GONE);
            if(SequFragment.isData) {
                tv_bianji.setVisibility(View.VISIBLE);
            } else{
                tv_bianji.setVisibility(View.GONE);
            }
        } else if (index == 2) {
            tv_total.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sequ.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sound.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            tv_radio.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_tts.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_qingkong.setVisibility(View.GONE);
            if(SoundFragment.isData) {
                tv_bianji.setVisibility(View.VISIBLE);
            } else{
                tv_bianji.setVisibility(View.GONE);
            }
        } else if (index == 3) {
            tv_total.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sequ.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sound.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_radio.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            tv_tts.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_qingkong.setVisibility(View.GONE);
            if(RadioFragment.isData) {
                tv_bianji.setVisibility(View.VISIBLE);
            } else {
                tv_bianji.setVisibility(View.GONE);
            }
        } else if (index == 4) {
            tv_total.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sequ.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sound.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_radio.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_tts.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            tv_qingkong.setVisibility(View.GONE);
            if(TTSFragment.isData) {
                tv_bianji.setVisibility(View.VISIBLE);
            } else {
                tv_bianji.setVisibility(View.GONE);
            }
        }
    }

    // 设置清空可见或隐藏
    public static void setQkVisibleOrHide(boolean visible) {
        if(visible) {
            tv_qingkong.setVisibility(View.VISIBLE);
        } else {
            tv_qingkong.setVisibility(View.GONE);
        }
    }

    // delDialog 初始化
    private void delDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_fravorite, null);
        LinearLayout lin_favorite_quanxuan = (LinearLayout) dialog.findViewById(R.id.lin_favorite_quanxuan);
        LinearLayout lin_favorite_shanchu = (LinearLayout) dialog.findViewById(R.id.lin_favorite_shanchu);
        imageAllCheck = (ImageView) dialog.findViewById(R.id.img_fravorite_quanxuan);
        DelDialog = new Dialog(context, R.style.MyDialog_duijiang);
        DelDialog.setContentView(dialog);
        Window window = DelDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int scrEnw = dm.widthPixels;
        LayoutParams params = dialog.getLayoutParams();
        params.width = scrEnw;
        dialog.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        DelDialog.setCanceledOnTouchOutside(false);

        lin_favorite_quanxuan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogFlag == 0) {
                    imageAllCheck.setImageResource(R.mipmap.wt_group_checked);
                    dialogFlag = 1;
                    handleData(3);
                } else {
                    imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
                    dialogFlag = 0;
                    handleData(4);
                }
            }
        });

        lin_favorite_shanchu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleData(5);
            }
        });
    }

    // confirmDialog 初始化
    private void confirmDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        TextView tv_cancle = (TextView) dialog1.findViewById(R.id.tv_cancle);
        TextView tv_title = (TextView) dialog1.findViewById(R.id.tv_title);
        TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
        tv_title.setText("是否删除所有的喜欢数据?");
        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        tv_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });

        tv_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                totalFragment.delitem();
                confirmDialog.dismiss();
            }
        });
    }

    // 广播接收  用于更新全选
    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SET_ALL_IMAGE)) {
                imageAllCheck.setImageResource(R.mipmap.wt_group_checked);
                dialogFlag = 1;
            } else if (intent.getAction().equals(SET_NOT_ALL_IMAGE)) {
                imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
                dialogFlag = 0;
            }
        }
    }

    // 页面变化时的监听器
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
            animation.setFillAfter(true);    // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);        // 动画持续时间0.2秒
            image.startAnimation(animation);// 是用ImageView来显示动画的
            currentIndex = currIndex;
            if (lastIndex == -1) {
                lastIndex = currentIndex;
            } else {
                if (lastIndex != currentIndex) {
                    handleData(2);
                    handleData(4);
                    if (DelDialog.isShowing()) {
                        DelDialog.dismiss();
                        imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
                        dialogFlag = 0;
                    }
                    textFlag = 0;
                    tv_bianji.setText("编辑");
                    isEdit = false;
                    lastIndex = currentIndex;
                }
            }
            viewChange(currIndex);
        }
    }

    // TextView 事件监听 
    class txListener implements OnClickListener {
        private int index = 0;

        public txListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
            currentIndex = index;
            if (lastIndex == -1) {
                lastIndex = currentIndex;
            } else {
                if (lastIndex != currentIndex) {
                    handleData(2);
                    handleData(4);
                    if (DelDialog.isShowing()) {
                        DelDialog.dismiss();
                        imageAllCheck.setImageResource(R.mipmap.wt_group_nochecked);
                        dialogFlag = 0;
                    }
                    textFlag = 0;
                    tv_bianji.setText("编辑");
                    lastIndex = currentIndex;
                }
            }
            viewChange(index);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(mBroadcast);
        if(DelDialog != null) {
            DelDialog.dismiss();
            DelDialog = null;
        }
        image = null;
        tv_total = null;
        tv_sequ = null;
        tv_sound = null;
        tv_radio = null;
        tv_tts = null;
        tv_qingkong = null;
        tv_bianji = null;
        imageAllCheck = null;
        mPager = null;
        confirmDialog = null;
        totalFragment = null;
        sequfragment = null;
        soundfragment = null;
        radiofragment = null;
        context = null;
    }
}
