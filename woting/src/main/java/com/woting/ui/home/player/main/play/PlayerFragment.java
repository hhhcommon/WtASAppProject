package com.woting.ui.home.player.main.play;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.baidu.cyberplayer.core.BVideoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.UMShareAPI;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.StringUtils;
import com.woting.common.util.TimeUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.MarqueeTextView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.home.player.main.adapter.PlayerListAdapter;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.LanguageSearch;
import com.woting.ui.home.player.main.model.LanguageSearchInside;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.player.timeset.service.timeroffservice;
import com.woting.ui.home.program.album.main.AlbumFragment;
import com.woting.ui.home.program.album.model.ContentInfo;
import com.woting.ui.home.program.comment.CommentActivity;
import com.woting.ui.home.search.main.SearchLikeFragment;
import com.woting.ui.interphone.message.messagecenter.activity.MessageMainActivity;
import com.woting.ui.mine.playhistory.main.PlayHistoryFragment;
import com.woting.ui.interphone.notify.activity.MessageFragment;
import com.woting.ui.main.MainActivity;
import com.woting.video.IntegrationPlayer;
import com.woting.video.VoiceRecognizer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 播放主界面
 */
public class PlayerFragment extends Fragment implements View.OnClickListener, XListView.IXListViewListener, AdapterView.OnItemClickListener {

    public static FragmentActivity context;
    public static int timerService;// 当前节目播放剩余时间长度
    public static boolean isCurrentPlay;

    private SharedPreferences sp = BSApplication.SharedPreferences;// 数据存储
    private IntegrationPlayer mPlayer;// 播放器
    private SearchPlayerHistoryDao mSearchHistoryDao;// 搜索历史数据库
    private String mRadioContentId;
    private AudioManager audioMgr;// 声音管理
    private VoiceRecognizer mVoiceRecognizer;// 讯飞
    private MessageReceiver mReceiver;// 广播接收
    private PlayerListAdapter adapter;
    public static SearchLikeFragment frag;// 搜索

    private Dialog dialog;// 加载数据对话框
    private Dialog wifiDialog;// WIFI 提醒对话框

    private MarqueeTextView mPlayAudioTitleName;// 正在播放的节目的标题
    private View mViewVoice;// 语音搜索 点击右上角"语音"显示
    private TextView mVoiceTextSpeakStatus;// 语音搜索状态
    private ImageView mVoiceImageSpeak;// 按下说话 抬起开始搜索

    private ImageView mPlayAudioImageCover;// 播放节目的封面
    private ImageView mPlayImageStatus;// 播放状态图片  播放 OR 暂停
    private ImageView imageMore;// 更多操作

    private SeekBar mSeekBar;// 播放进度
    private TextView mSeekBarStartTime;// 进度的开始时间
    private TextView mSeekBarEndTime;// 播放节目总长度

    private View rootView;
    private XListView mListView;// 播放列表

    private long totalTime;// 播放总长度
    private int page = 1;// mainPage
    private int refreshType = 0;// == -1 刷新  == 1 加载更多  == 0 第一次加载
    private int stepVolume;
    private int curVolume;// 当前音量
    private int index = 0;// 记录当前播放在列表中的位置
    private int sequListSize;// 播放专辑 获取在专辑列表已经获取的列表数量

    private Bitmap bmpPress;// 语音搜索按钮按下的状态图片
    private Bitmap bmp;// 语音搜索按钮未按下的状态图片

    private boolean isResetData;// 重新获取了数据  searchByText
    private boolean isPlaying;// 是否正在播放
    private boolean isInitData;// 第一次进入应用加载数据
    private boolean isNetPlay;// 播放网络地址
    private boolean isPlayLK;// 正在播放路况

    /**
     * 1.== "MAIN_PAGE"  ->  mainPageRequest;
     * 2.== "SEARCH_TEXT"  ->  searchByTextRequest;
     * 3.== "SEARCH_VOICE"  ->  searchByVoiceRequest;
     * 4.== "SEARCH_SEQU" -> 播放专辑
     * Default  == "MAIN_PAGE";
     */
    private String requestType = StringConstant.PLAY_REQUEST_TYPE_MAIN_PAGE;
    private String sendTextContent;// 文字搜索内容
    private String sendVoiceContent;// 语音搜索内容
    private String mediaType;// 当前播放节目类型
    private String contentId;// 专辑 ID  播放专辑列表时获取专辑列表数据需要的参数

    private List<LanguageSearchInside> playList = new ArrayList<>();// 播放列表
    private List<LanguageSearchInside> subList = new ArrayList<>();// 保存临时数据

    private Timer mTimer;
    private String IsPlaying; // 获取的当前的播放内容

    private Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IntegerConstant.REFRESH_PROGRAM:// 刷新节目单
                    if (!TextUtils.isEmpty(IsPlaying) && !TextUtils.isEmpty(mRadioContentId)) {
                        for (int i = 0; i < playList.size(); i++) {
                            if (playList.get(i).getContentId() != null && mRadioContentId != null &&
                                    playList.get(i).getContentId().equals(mRadioContentId)) {
                                if (playList.get(i).getIsPlaying() != null && !playList.get(i).getIsPlaying().equals(IsPlaying)) {
                                    playList.get(i).setIsPlaying(IsPlaying);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
                case IntegerConstant.PLAY_UPDATE_LIST:// 更新列表
                    updateList();
                    break;
                case IntegerConstant.PLAY_UPDATE_LIST_VIEW:// 更新列表界面
                    updateListView();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        setReceiver();// 注册广播接收器
        initData();// 初始化数据
        initTimerTask(); // 定时获取节目单的方法
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_play, container, false);

            View headView = LayoutInflater.from(context).inflate(R.layout.headview_fragment_play, null);
            initView(headView);// 设置界面
            initEvent(headView);// 设置控件点击事件

            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialogph(context, "通讯中...");
                mainPageRequest();
            } else {
                mListView.setAdapter(adapter = new PlayerListAdapter(context, playList));
                setPullAndLoad(true, false);
            }
        }
        return rootView;
    }

    // 初始化视图
    private void initView(View view) {
        // 开启服务绑定播放器 BVideoView
        BVideoView.setAK("1f32c8ae32894fd4b3030ec6e9bd14c2");
        BVideoView bVideoView = (BVideoView) rootView.findViewById(R.id.video_view);
        mPlayer.bindService(context, bVideoView);

        // -----------------  HeadView 相关控件初始化 START  ----------------
        ImageView mPlayAudioImageCoverMask = (ImageView) view.findViewById(R.id.image_liu);// 封面图片的六边形遮罩
        mPlayAudioImageCoverMask.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_bd));

        mPlayAudioTitleName = (MarqueeTextView) view.findViewById(R.id.tv_name);// 正在播放的节目的标题
        mPlayAudioImageCover = (ImageView) view.findViewById(R.id.img_news);// 播放节目的封面

        mPlayImageStatus = (ImageView) view.findViewById(R.id.img_play);// 播放状态图片  播放 OR 暂停
        imageMore = (ImageView) view.findViewById(R.id.image_more);// 更多操作

        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);// 播放进度
        mSeekBarStartTime = (TextView) view.findViewById(R.id.time_start);// 进度的开始时间
        mSeekBarEndTime = (TextView) view.findViewById(R.id.time_end);// 播放节目总长度
        // -----------------  HeadView 相关控件初始化 END  ----------------

        // -----------------  RootView 相关控件初始化 START  ----------------
        mListView = (XListView) rootView.findViewById(R.id.listView);

        mViewVoice = rootView.findViewById(R.id.id_voice_transparent);// 语音搜索 点击右上角"语音"显示
        mVoiceTextSpeakStatus = (TextView) rootView.findViewById(R.id.tv_speak_status);// 语音搜索状态
        mVoiceImageSpeak = (ImageView) rootView.findViewById(R.id.imageView_voice);
        // -----------------  RootView 相关控件初始化 END  ----------------

        mListView.addHeaderView(view);
        wifiDialog();// wifi 提示 dialog
    }

    // 初始化点击事件
    private void initEvent(View view) {
        // -----------------  HeadView 相关控件设置监听 START  ----------------
        view.findViewById(R.id.lin_lukuangtts).setOnClickListener(this);// 路况
        view.findViewById(R.id.lin_voicesearch).setOnClickListener(this);// 语音
        view.findViewById(R.id.lin_left).setOnClickListener(this);// 播放上一首
        view.findViewById(R.id.lin_center).setOnClickListener(this);// 播放
        view.findViewById(R.id.lin_right).setOnClickListener(this);// 播放下一首

        imageMore.setOnClickListener(this);// 更多操作
        setListener();
        // -----------------  HeadView 相关控件设置监听 END  ----------------

        // -----------------  RootView 相关控件设置监听 START  ----------------
        mListView.setXListViewListener(this);// 设置下拉刷新和加载更多监听
        mListView.setOnItemClickListener(this);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        rootView.findViewById(R.id.lin_news).setOnClickListener(this);// 消息中心

        rootView.findViewById(R.id.lin_find).setOnClickListener(this);// 搜索

        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);// 取消  点击关闭语音搜索
        rootView.findViewById(R.id.view_voice_other).setOnClickListener(this);// 点击隐藏语音搜索
        mVoiceImageSpeak.setOnTouchListener(new MyVoiceSpeakTouchLis());
        mVoiceImageSpeak.setImageBitmap(bmp);
        // -----------------  RootView 相关控件设置监听 END  ----------------
    }

    // 初始化数据
    private void initData() {
        mSearchHistoryDao = new SearchPlayerHistoryDao(context);

        mPlayer = IntegrationPlayer.getInstance();

        bmpPress = BitmapUtils.readBitMap(context, R.mipmap.wt_duijiang_button_pressed);
        bmp = BitmapUtils.readBitMap(context, R.mipmap.talknormal);

        UMShareAPI.get(context);// 初始化友盟
        setVoice();// 初始化音频控制器
    }

    // 注册广播接收器
    private void setReceiver() {
        if (mReceiver == null) {
            mReceiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PLAYERVOICE);// 语音搜索
            filter.addAction(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);// 文本搜索
            filter.addAction(BroadcastConstants.PUSH_MUSIC);

            filter.addAction(BroadcastConstants.UPDATE_PLAY_CURRENT_TIME);// 更新当前播放时间
            filter.addAction(BroadcastConstants.UPDATE_PLAY_TOTAL_TIME);// 更新当前播放总时间
            filter.addAction(BroadcastConstants.UPDATE_PLAY_LIST);// 更新播放列表
            filter.addAction(BroadcastConstants.UPDATE_PLAY_VIEW);// 更新播放界面

            filter.addAction(BroadcastConstants.PLAY_NO_NET);// 播放器没有网络
            filter.addAction(BroadcastConstants.PLAY_WIFI_TIP);// 需要提示
            filter.addAction(BroadcastConstants.LK_TTS_PLAY_OVER);// 路况播放完了

            // 下载完成更新 LocalUrl
            filter.addAction(BroadcastConstants.ACTION_FINISHED);
            filter.addAction(BroadcastConstants.ACTION_FINISHED_NO_DOWNLOADVIEW);

            // 播放专辑
            filter.addAction(BroadcastConstants.PLAY_SEQU_LIST);

            context.registerReceiver(mReceiver, filter);
        }
    }

    // SeekBar 监听
    private void setListener() {
        mSeekBar.setEnabled(false);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.setPlayCurrentTime((long) progress);
                }
            }
        });
    }

    // 显示搜索界面
    private void showSearch() {
        if (frag != null && PlayerActivity.playerFragment != null) {
            context.getSupportFragmentManager().beginTransaction()
                    .hide(PlayerActivity.playerFragment).show(frag).commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_find:// 搜索
                if (frag == null) {
                    frag = new SearchLikeFragment();
                    Bundle bun = new Bundle();
                    bun.putInt("FROM_TYPE", 0);// == 0 PlayerFragment
                    frag.setArguments(bun);
                    PlayerActivity.open(frag);
                } else {
                    showSearch();
                }
                MainActivity.hideOrShowTab(false);
                PlayerActivity.isVisible = false;
                break;
            case R.id.lin_news://
                startActivity(new Intent(context, MessageMainActivity.class));
                break;
            case R.id.lin_lukuangtts:// 获取路况
                TTSPlay();
                break;
            case R.id.tv_cancel:// 取消 点击隐藏语音对话框
                linChoseClose(mViewVoice);
                if (mCloseVoiceRunnable != null) {
                    mUIHandler.removeCallbacks(mCloseVoiceRunnable);
                    mVoiceTextSpeakStatus.setText("请按住讲话");
                }
                break;
            case R.id.lin_voicesearch:// 语音搜索框
                linChoseOpen(mViewVoice);
                break;
            case R.id.view_voice_other:
                linChoseClose(mViewVoice);
                if (mCloseVoiceRunnable != null) {
                    mUIHandler.removeCallbacks(mCloseVoiceRunnable);
                    mVoiceTextSpeakStatus.setText("请按住讲话");
                }
                break;
            case R.id.lin_left:// 上一首
                last();
                break;
            case R.id.lin_center:// 播放
                play();
                break;
            case R.id.lin_right:// 下一首
                next();
                break;
            case R.id.image_more:// 更多操作
                MainActivity.setViewSix();
                break;
        }
    }

    // 广播接收器
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BroadcastConstants.PLAY_TEXT_VOICE_SEARCH:// 文本搜索
                    sendTextContent = intent.getStringExtra(StringConstant.TEXT_CONTENT);
                    page = 1;
                    refreshType = 0;
                    requestType = StringConstant.PLAY_REQUEST_TYPE_SEARCH_TEXT;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            queryData();
                        }
                    }, 500);
                    break;
                case BroadcastConstants.PLAYERVOICE:// 语音搜索
                    sendVoiceContent = intent.getStringExtra("VoiceContent");
                    if (CommonHelper.checkNetwork(context)) {
                        if (!sendVoiceContent.trim().equals("")) {
                            mVoiceTextSpeakStatus.setText("正在搜索: " + sendVoiceContent);
                            mUIHandler.postDelayed(mCloseVoiceRunnable, 3000);

                            page = 1;
                            refreshType = 0;
                            requestType = StringConstant.PLAY_REQUEST_TYPE_SEARCH_VOICE;
                            mainPageRequest();
                        }
                    }
                    break;
                case BroadcastConstants.UPDATE_PLAY_TOTAL_TIME:// 更新时间总长度
                    mediaType = intent.getStringExtra(StringConstant.PLAY_MEDIA_TYPE);
                    totalTime = intent.getLongExtra(StringConstant.PLAY_TOTAL_TIME, -1);
                    if (totalTime == -1) {
                        int duration = 24 * 60 * 60;
                        mSeekBar.setEnabled(false);
                        mSeekBar.setClickable(false);
                        mSeekBar.setMax(duration);
                        updateTextViewWithTimeFormat(mSeekBarEndTime, duration);
                    } else {
                        mSeekBar.setEnabled(true);
                        mSeekBar.setClickable(true);
                        mSeekBar.setMax((int) totalTime);
                        updateTextViewWithTimeFormat(mSeekBarEndTime, (int) (totalTime / 1000));
                    }
                    addDb(GlobalConfig.playerObject);// 将播放对象加入数据库
                    break;
                case BroadcastConstants.UPDATE_PLAY_CURRENT_TIME:// 更新当前播放时间
                    // 缓存进度
                    if (mediaType != null && mediaType.equals(StringConstant.TYPE_AUDIO)) {
                        long secondProgress = intent.getLongExtra(StringConstant.PLAY_SECOND_PROGRESS, 0);
                        if (secondProgress == -1) {
                            mSeekBar.setSecondaryProgress((int) totalTime);
                        } else if (secondProgress == -100) {
                            mSeekBar.setSecondaryProgress(mSeekBar.getMax());
                        } else {
                            mSeekBar.setSecondaryProgress((int) secondProgress);
                        }
                    }

                    // 当前播放进度
                    long currentTime = intent.getLongExtra(StringConstant.PLAY_CURRENT_TIME, -1);
                    timerService = (int) (totalTime - currentTime);// 当前播放剩余时间
                    if (mediaType != null && mediaType.equals(StringConstant.TYPE_AUDIO)) {
                        mSeekBar.setProgress((int) currentTime);
                        updateTextViewWithTimeFormat(mSeekBarStartTime, (int) (currentTime / 1000));
                    } else {
                        int progress = TimeUtils.getTime(currentTime);
                        mSeekBar.setProgress(progress);
                        updateTextViewWithTimeFormat(mSeekBarStartTime, progress);
                    }

                    // playInTime
                    mSearchHistoryDao.updatePlayerInTime(GlobalConfig.playerObject.getContentPlay(), currentTime, totalTime);
                    break;
                case BroadcastConstants.UPDATE_PLAY_VIEW:// 更新界面
                    index = intent.getIntExtra(StringConstant.PLAY_POSITION, 0);// 列表中的位置

                    resetHeadView();

                    // 更新列表视图
                    mUIHandler.sendEmptyMessageDelayed(IntegerConstant.PLAY_UPDATE_LIST_VIEW, 0);
                    if (isInitData) {
                        mPlayImageStatus.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_play_play));
                        isPlaying = true;
                    }
                    isInitData = true;
                    break;
                case BroadcastConstants.PUSH_MUSIC:// 监听到电话状态发生更改
                    String phoneType = intent.getStringExtra("outMessage");
                    Log.e("电话状态", phoneType + "");
                    break;
                case BroadcastConstants.ACTION_FINISHED:// 更新下载列表
                case BroadcastConstants.ACTION_FINISHED_NO_DOWNLOADVIEW:
                    if (mPlayer != null) mPlayer.updateLocalList();
                    break;
                case BroadcastConstants.PLAY_NO_NET:// 播放器没有网络
                    ToastUtils.show_always(context, "没有网络!");
                    break;
                case BroadcastConstants.PLAY_WIFI_TIP:// 需要提示
                    wifiDialog.show();
                    break;
                case BroadcastConstants.LK_TTS_PLAY_OVER:// 路况播放完了
                    isPlayLK = false;
                    if (isPlaying) {// 正在播放
                        mPlayer.continuePlay();
                        mPlayImageStatus.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_play_play));
                    } else {// 暂停状态
                        mPlayer.pausePlay();
                        mPlayImageStatus.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_play_stop));
                    }

                    // 播放的节目封面图片
                    String url = GlobalConfig.playerObject.getContentImg();
                    if (url != null) {// 有封面图片
                        if (!url.startsWith("http")) {
                            url = GlobalConfig.imageurl + url;
                        }
                        url = AssembleImageUrlUtils.assembleImageUrl180(url);
                        Picasso.with(context).load(url.replace("\\/", "/")).into(mPlayAudioImageCover);
                    } else {// 没有封面图片设置默认图片
                        mPlayAudioImageCover.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx));
                    }

                    mUIHandler.sendEmptyMessageDelayed(IntegerConstant.PLAY_UPDATE_LIST_VIEW, 0);
                    break;
                case BroadcastConstants.PLAY_SEQU_LIST:// 播放专辑列表
                    contentId = intent.getStringExtra(StringConstant.ID_CONTENT);
                    sequListSize = intent.getIntExtra(StringConstant.SEQU_LIST_SIZE, 0);
                    requestType = StringConstant.PLAY_REQUEST_TYPE_SEARCH_SEQU;

                    sequListSize = 10;
                    page = 1;
                    refreshType = 0;
                    sequListRequest();
                    break;
            }
        }
    }

    // 语音搜索按钮的按下抬起操作监听
    class MyVoiceSpeakTouchLis implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!CommonHelper.checkNetwork(context)) return true;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:// 按下
                    pressDown();
                    break;
                case MotionEvent.ACTION_UP:// 抬起
                    putUp();
                    break;
                case MotionEvent.ACTION_CANCEL:// 抬起
                    putUp();
                    break;
            }
            return true;
        }
    }

    // 查询数据
    private void queryData() {
        playList.clear();
        LanguageSearchInside languageSearchInside = getDaoList(context);
        if (languageSearchInside != null) {
            playList.add(languageSearchInside);// 将查询得到的第一条数据加入播放列表中
            if (requestType.equals(StringConstant.PLAY_REQUEST_TYPE_SEARCH_TEXT)) {
                ArrayList<LanguageSearchInside> playerList = new ArrayList<>();
                playerList.add(languageSearchInside);
                mPlayer.updatePlayList(playerList);
                index = 0;
                mPlayer.startPlay(index);
                isResetData = true;
            }
        }
        mainPageRequest();
    }

    // listView 的 item 点击事件监听
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position - 2 >= 0) {
            position = position - 2;
            if (index == position) {// 判断和当前播放节目是否相同
                play();
            } else {// 和当前播放节目不相同则直接开始播放
                index = position;
                mPlayer.startPlay(index);
            }
            stopCurrentTimer();
        }
    }

    // 开始播放
    private void play() {
        if (isPlayLK) {
            mPlayer.stopLKTts();
            return ;
        }
        if (GlobalConfig.playerObject == null) return ;
        if (isNetPlay && !isPlaying) {
            mPlayer.startPlay(index);
        } else {
            if (mPlayer.playStatus()) {// 正在播放
                mPlayer.pausePlay();
                mPlayImageStatus.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_play_stop));
                isPlaying = false;
            } else {// 暂停状态
                mPlayer.continuePlay();
                mPlayImageStatus.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_play_play));
                isPlaying = true;
            }
            mUIHandler.sendEmptyMessageDelayed(IntegerConstant.PLAY_UPDATE_LIST_VIEW, 0);
        }
        Intent intent = new Intent(BroadcastConstants.UPDATE_PLAY_IMAGE);
        intent.putExtra(StringConstant.PLAY_IMAGE, isPlaying);
        context.sendBroadcast(intent);
        stopCurrentTimer();
    }

    // 下一首
    private void next() {
        index++;
        if (index >= playList.size()) index = 0;
        mPlayer.startPlay(index);
        stopCurrentTimer();
    }

    // 上一首
    private void last() {
        index--;
        if (index < 0) {
            ToastUtils.show_always(context, "已经是第一个节目了!");
            return ;
        }
        mPlayer.startPlay(index);
        stopCurrentTimer();
    }

    // TTS 的播放
    private void TTSPlay() {
        ToastUtils.show_always(context, "点击了路况TTS按钮");
        if (CommonHelper.checkNetwork(context)) {
            dialog = DialogUtils.Dialogph(context, "通讯中");
            getLuKuangTTS();// 获取路况数据播报
        }
    }

    @Override
    public void onRefresh() {
        refreshType = -1;
        if (requestType.equals(StringConstant.PLAY_REQUEST_TYPE_SEARCH_SEQU)) {
            sequListRequest();
        } else {
            mainPageRequest();
        }
    }

    @Override
    public void onLoadMore() {
        refreshType = 1;
        if (requestType.equals(StringConstant.PLAY_REQUEST_TYPE_SEARCH_SEQU)) {
            sequListRequest();
        } else {
            mainPageRequest();
        }
    }

    // 去除 ContentPlay == null 的数据
    private List<LanguageSearchInside> clearContentPlayNull(List<LanguageSearchInside> list) {
        int index = 0;
        while (index < list.size()) {
            if (list.get(index).getMediaType().equals("TTS")) {
                index++;
            } else {
                if (list.get(index).getContentPlay() == null || list.get(index).getContentPlay().trim().equals("")
                        || list.get(index).getContentPlay().trim().toUpperCase().equals("NULL")) {
                    list.remove(index);
                } else {
                    index++;
                }
            }
        }
        return list;
    }

    // 更新列表
    private void updateList() {
        if (isResetData) {// 文本搜索数据重置了
            isResetData = false;

            if (subList != null && subList.size() != 0) {
                if (mediaType != null && !mediaType.equals("TTS")) {
                    String contentPlay;
                    for (int i=0, size=subList.size(); i<size; i++) {
                        contentPlay = subList.get(i).getContentPlay();
                        if (contentPlay != null && contentPlay.equals(GlobalConfig.playerObject.getContentPlay())) {
                            playList.clear();
                            index = i;// 记录当前播放节目在列表中的位置
                            subList.get(i).setType("2");
                        } else {
                            subList.get(i).setType("1");
                        }
                    }
                }
                playList.addAll(subList);
            }
        } else {
            if (subList != null && subList.size() > 0) {
                if (playList.size() > 0) {
                    List<String> contentPlayList = new ArrayList<>();// 保存用于区别是否重复的内容
                    String contentPlay;// 用于区别是否重复 URL
                    String media;// 媒体类型  TTS 没有 contentPlay 需要特殊处理

                    for (int a = 0, s = playList.size(); a < s; a++) {
                        media = playList.get(a).getMediaType();
                        if (media != null && !media.equals("TTS")) {
                            contentPlay = playList.get(a).getContentPlay();
                            if (contentPlay != null && !contentPlay.trim().equals("") && !contentPlay.toUpperCase().equals("NULL")) {
                                contentPlayList.add(contentPlay);
                            }
                        }
                    }
                    for (int i = 0, size = subList.size(); i < size; i++) {
                        if (subList.get(i).getMediaType() != null && subList.get(i).getMediaType().equals("TTS")) continue;
                        if (!contentPlayList.contains(subList.get(i).getContentPlay())) {
                            if (refreshType == -1) {
                                playList.add(0, subList.get(i));
                                index++;
                            } else {
                                playList.add(subList.get(i));
                            }
                        }
                    }

                    contentPlayList.clear();
                } else {
                    playList.addAll(subList);
                }
            }
        }
        if (adapter == null) {
            mListView.setAdapter(adapter = new PlayerListAdapter(context, playList));
        } else {
            adapter.notifyDataSetChanged();
            Log.v("TAG", "adapter update view");
        }
        ArrayList<LanguageSearchInside> playerList = new ArrayList<>();
        playerList.addAll(playList);
        if(refreshType == 1) {
            mPlayer.updatePlayList(playerList);
        } else {
            mPlayer.updatePlayList(playerList, index);
        }

        // 每次语音搜索结果出来之后应该自动播放第一个节目
        if (requestType.equals(StringConstant.PLAY_REQUEST_TYPE_SEARCH_VOICE) && refreshType == 0) {// 语音结束后自动播放第一个节目
            index = 0;
            mPlayer.startPlay(index);
        } else if (requestType.equals(StringConstant.PLAY_REQUEST_TYPE_SEARCH_SEQU) && refreshType == 0) {// 播放专辑
            index = 0;
            mPlayer.startPlay(index);
        }

        if (dialog != null) dialog.dismiss();
        subList.clear();
    }

    // 更新列表界面
    private void updateListView() {
        for (int i = 0, size = playList.size(); i < size; i++) {
            if (i == index) {
                if (isPlaying) playList.get(i).setType("2");
                else playList.get(i).setType("0");
            } else {
                playList.get(i).setType("1");
            }
        }
        adapter.setList(playList);
    }

    // 按下按钮的操作
    private void pressDown() {
        if (mCloseVoiceRunnable != null) {
            mVoiceTextSpeakStatus.setText("请按住讲话");
            mUIHandler.removeCallbacks(mCloseVoiceRunnable);
        }
        curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);// 获取此时的音量大小
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, stepVolume, AudioManager.FLAG_PLAY_SOUND);// 设置想要的音量大小
        mVoiceRecognizer = VoiceRecognizer.getInstance(context, BroadcastConstants.PLAYERVOICE);// 讯飞开始
        mVoiceRecognizer.startListen();
        mVoiceTextSpeakStatus.setText("开始语音转换");
        mVoiceImageSpeak.setImageBitmap(bmpPress);
    }

    // 抬起手后的操作
    private void putUp() {
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND);// 还原原先音量大小
        mVoiceRecognizer.stopListen();// 讯飞停止
        mVoiceImageSpeak.setImageBitmap(bmp);
        mVoiceTextSpeakStatus.setText("请按住讲话");
    }

    // 更新时间展示数据
    private void updateTextViewWithTimeFormat(TextView view, long second) {
        int hh = (int) (second / 3600);
        int mm = (int) (second % 3600 / 60);
        int ss = (int) (second % 60);
        String strTemp = String.format(Locale.CHINA, "%02d:%02d:%02d", hh, mm, ss);
        view.setText(strTemp);
    }

    // 定时任务
    private void initTimerTask() {
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                if (GlobalConfig.playerObject != null && GlobalConfig.playerObject.getMediaType() != null && GlobalConfig.playerObject.getMediaType().equals("RADIO")) {
                    sendContentInfo(GlobalConfig.playerObject.getContentId());
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 0, 1000 * 60 * 5);
    }

    // wifi 弹出框
    private void wifiDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_wifi_set, null);
        wifiDialog = new Dialog(context, R.style.MyDialog);
        wifiDialog.setContentView(dialog1);
        wifiDialog.setCanceledOnTouchOutside(false);
        wifiDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        // 取消播放
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiDialog.dismiss();

                isNetPlay = true;
                mPlayImageStatus.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_play_stop));
                isPlaying = false;
                mUIHandler.sendEmptyMessage(IntegerConstant.PLAY_UPDATE_LIST_VIEW);
            }
        });
        // 允许本次播放
        dialog1.findViewById(R.id.tv_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiDialog.dismiss();
                mPlayer.startPlay(index, true);
                isNetPlay = false;
            }
        });
        // 不再提醒
        dialog1.findViewById(R.id.tv_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor et = sp.edit();
                et.putString(StringConstant.WIFISHOW, "false");
                if (!et.commit()) Log.i("TAG", "commit Fail");
                wifiDialog.dismiss();
                mPlayer.startPlay(index, true);
                isNetPlay = false;
            }
        });
    }

    // 设置刷新和加载
    private void setPullAndLoad(boolean isPull, boolean isLoad) {
        mListView.setPullRefreshEnable(isPull);
        mListView.setPullLoadEnable(isLoad);
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    // 智能关闭语音搜索框
    private Runnable mCloseVoiceRunnable = new Runnable() {
        @Override
        public void run() {
            linChoseClose(mViewVoice);// 2秒后隐藏界面
            mVoiceTextSpeakStatus.setText("请按住讲话");
        }
    };

    // 初始化音频控制器
    private void setVoice() {
        audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取最大音乐音量
        stepVolume = maxVolume / 100;
    }

    // 关闭 linChose 界面
    private void linChoseClose(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.umeng_socialize_slide_out_from_bottom);
            view.setAnimation(mAnimation);
            view.setVisibility(View.GONE);
        }
    }

    // 打开 linChose 界面
    private void linChoseOpen(View view) {
        if (view.getVisibility() == View.GONE) {
            Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.umeng_socialize_slide_in_from_bottom);
            view.setAnimation(mAnimation);
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVoiceRecognizer != null) {
            mVoiceRecognizer.ondestroy();
            mVoiceRecognizer = null;
        }
        if (mReceiver != null) { // 注销广播
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mPlayer != null) {
            mPlayer.unbindService(context);
            mPlayer = null;
        }
    }

    // 获取数据库数据
    private LanguageSearchInside getDaoList(Context context) {
        if (mSearchHistoryDao == null) mSearchHistoryDao = new SearchPlayerHistoryDao(context);
        List<PlayerHistory> historyDatabaseList = mSearchHistoryDao.queryHistory();
        if (historyDatabaseList != null && historyDatabaseList.size() > 0) {
            PlayerHistory historyNew = historyDatabaseList.get(0);
            LanguageSearchInside historyNews = new LanguageSearchInside();
            historyNews.setType("1");
            historyNews.setContentURI(historyNew.getPlayerUrI());
            // historyNews.setContentPersons(historyNew.getPlayerNum());
            historyNews.setContentKeyWord("");
            historyNews.setcTime(historyNew.getPlayerInTime());
            historyNews.setContentSubjectWord("");
            historyNews.setContentTimes(historyNew.getPlayerAllTime());
            historyNews.setContentName(historyNew.getPlayerName());
            historyNews.setContentPubTime("");
            historyNews.setContentPub(historyNew.getPlayerFrom());
            historyNews.setContentPlay(historyNew.getPlayerUrl());
            historyNews.setMediaType(historyNew.getPlayerMediaType());
            historyNews.setContentId(historyNew.getContentID());
            historyNews.setContentDescn(historyNew.getPlayerContentDescn());
            historyNews.setPlayCount(historyNew.getPlayerNum());
            historyNews.setContentImg(historyNew.getPlayerImage());
            historyNews.setIsPlaying(historyNew.getIsPlaying());
            try {
                if (historyNew.getPlayerAllTime() != null && historyNew.getPlayerAllTime().equals("")) {
                    historyNews.setPlayerAllTime("0");
                } else {
                    historyNews.setPlayerAllTime(historyNew.getPlayerAllTime());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (historyNew.getPlayerInTime() != null && historyNew.getPlayerInTime().equals("")) {
                    historyNews.setPlayerInTime("0");
                } else {
                    historyNews.setPlayerInTime(historyNew.getPlayerInTime());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            historyNews.setContentShareURL(historyNew.getPlayContentShareUrl());
            historyNews.setContentFavorite(historyNew.getContentFavorite());
            historyNews.setLocalurl(historyNew.getLocalurl());
            historyNews.setSequId(historyNew.getSequId());
            historyNews.setSequName(historyNew.getSequName());
            historyNews.setSequDesc(historyNew.getSequDesc());
            historyNews.setSequImg(historyNew.getSequImg());
            historyNews.setContentPlayType(historyNew.getContentPlayType());
            return historyNews;
        } else {
            return null;
        }
    }

    // 把数据添加数据库----播放历史数据库
    private void addDb(LanguageSearchInside languageSearchInside) {
        String playerName = languageSearchInside.getContentName();
        String playerImage = languageSearchInside.getContentImg();
        String playerUrl = languageSearchInside.getContentPlay();
        String playerUrI = languageSearchInside.getContentURI();
        String playerMediaType = languageSearchInside.getMediaType();
        String playContentShareUrl = languageSearchInside.getContentShareURL();
        String playerAllTime = languageSearchInside.getPlayerAllTime();
        String playerInTime = languageSearchInside.getPlayerInTime();
        String playerContentDesc = languageSearchInside.getContentDescn();
        String playerNum = languageSearchInside.getPlayCount();
        String playerZanType = "false";
        String playerFrom = languageSearchInside.getContentPub();
        String playerFromId = "";
        String playerFromUrl = "";
        String playerAddTime = Long.toString(System.currentTimeMillis());
        String bjUserId = CommonUtils.getUserId(context);
        String contentFavorite = languageSearchInside.getContentFavorite();
        String ContentID = languageSearchInside.getContentId();
        String localUrl = languageSearchInside.getLocalurl();
        String sequName = languageSearchInside.getSequName();
        String sequId = languageSearchInside.getSequId();
        String sequDesc = languageSearchInside.getSequDesc();
        String sequImg = languageSearchInside.getSequImg();
        String ContentPlayType = languageSearchInside.getContentPlayType();
        String IsPlaying = languageSearchInside.getIsPlaying();

        PlayerHistory history = new PlayerHistory(playerName, playerImage,
                playerUrl, playerUrI, playerMediaType, playerAllTime,
                playerInTime, playerContentDesc, playerNum, playerZanType,
                playerFrom, playerFromId, playerFromUrl, playerAddTime,
                bjUserId, playContentShareUrl, contentFavorite, ContentID, localUrl, sequName, sequId, sequDesc, sequImg, ContentPlayType, IsPlaying);

        if (mSearchHistoryDao == null)
            mSearchHistoryDao = new SearchPlayerHistoryDao(context);// 如果数据库没有初始化，则初始化 db
        if (playerMediaType != null && playerMediaType.trim().length() > 0 && playerMediaType.equals("TTS")) {
            mSearchHistoryDao.deleteHistoryById(ContentID);
        } else {
            mSearchHistoryDao.deleteHistory(playerUrl);
        }
        mSearchHistoryDao.addHistory(history);
    }

    // 开启定时服务中的当前播放完后关闭的关闭服务方法 点击暂停播放、下一首、上一首以及播放路况信息时都将自动关闭此服务
    private void stopCurrentTimer() {
        if (PlayerFragment.isCurrentPlay) {
            Intent intent = new Intent(context, timeroffservice.class);
            intent.setAction(BroadcastConstants.TIMER_STOP);
            context.startService(intent);
            PlayerFragment.isCurrentPlay = false;
        }
    }

    // 刷新节目单
    private void sendContentInfo(final String ContentId) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("BcIds", ContentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getIsPlayIngUrl, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        String ResultList = result.getString("ResultList"); // 获取列表
                        Map<String, String> map = StringUtils.parseData(ResultList);

                        try {
                            mRadioContentId = ContentId;
                            IsPlaying = map.get(ContentId);
                        } catch (Exception e) {
                            e.printStackTrace();
                            IsPlaying = "";
                        }

                        if (!TextUtils.isEmpty(IsPlaying)) {
                            mUIHandler.sendEmptyMessage(IntegerConstant.REFRESH_PROGRAM);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
            }
        });
    }

    // 获取路况信息内容
    private void getLuKuangTTS() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        VolleyRequest.requestPost(GlobalConfig.getLKTTS, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                try {
                    String Message = result.getString("ContentURI");
                    if (Message != null && Message.trim().length() > 0) {
                        mPlayAudioImageCover.setImageResource(R.mipmap.wt_icon_lktts);
                        mPlayImageStatus.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_play_play));
                        mPlayer.playLKTts(Message);
                        isPlayLK = true;
                        stopCurrentTimer();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isPlayLK = false;
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                isPlayLK = false;
            }
        });
    }

    // 根据专辑获取播放列表
    private void sequListRequest() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) { // 没有网络
            return ;
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("ContentId", contentId);
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("PageSize", "10");
            jsonObject.put("SortType", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.getSmSubMedias, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultInfo")).nextValue();
                        List<LanguageSearchInside> list = new Gson().fromJson(arg1.getString("SubList"), new TypeToken<List<LanguageSearchInside>>() {}.getType());
                        if (page == 1) playList.clear();
                        if (list != null && list.size() >= sequListSize) {
                            page++;
                            setPullAndLoad(false, true);
                        } else {
                            setPullAndLoad(false, false);
                        }
                        subList = clearContentPlayNull(list);// 去空
                        mUIHandler.sendEmptyMessage(IntegerConstant.PLAY_UPDATE_LIST);
                    } else {
                        setPullAndLoad(false, false);
                        mUIHandler.sendEmptyMessage(IntegerConstant.PLAY_UPDATE_LIST);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (refreshType == 0 && playList.size() <= 0) {
                        setPullAndLoad(true, false);
                    } else {
                        setPullAndLoad(false, false);
                    }
                    mUIHandler.sendEmptyMessage(IntegerConstant.PLAY_UPDATE_LIST);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                if (refreshType == 0 && playList.size() <= 0) {
                    setPullAndLoad(true, false);
                } else {
                    setPullAndLoad(false, false);
                }
                mUIHandler.sendEmptyMessage(IntegerConstant.PLAY_UPDATE_LIST);
            }
        });
    }

    // 网络请求操作
    private void mainPageRequest() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if (dialog != null) dialog.dismiss();
            setPullAndLoad(true, false);
            return;
        }
        final String requestUrl;
        switch (requestType) {
            case StringConstant.PLAY_REQUEST_TYPE_SEARCH_TEXT:
                requestUrl = GlobalConfig.getSearchByText;// 文字搜索
                break;
            case StringConstant.PLAY_REQUEST_TYPE_SEARCH_VOICE:
                requestUrl = GlobalConfig.searchvoiceUrl;// 语音搜索
                break;
            default:
                requestUrl = GlobalConfig.mainPageUrl;// 主网络请求
                break;
        }

        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            if (requestType != null && requestType.equals(StringConstant.PLAY_REQUEST_TYPE_SEARCH_TEXT)) {
                jsonObject.put("SearchStr", sendTextContent);
            } else if (requestType != null && requestType.equals(StringConstant.PLAY_REQUEST_TYPE_SEARCH_VOICE)) {
                jsonObject.put("SearchStr", sendVoiceContent);
            }
            jsonObject.put("PageType", "0");
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("PageSize", "10");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(requestUrl, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType.equals("1001")) {
                        List<LanguageSearchInside> list;
                        if (requestType.equals(StringConstant.PLAY_REQUEST_TYPE_MAIN_PAGE)) {
                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                            String listString = arg1.getString("List");
                            list = new Gson().fromJson(listString, new TypeToken<List<LanguageSearchInside>>() {}.getType());
                        } else {// "SEARCH_TEXT" OR "SEARCH_VOICE"
                            LanguageSearch lists = new Gson().fromJson(result.getString("ResultList"), new TypeToken<LanguageSearch>() {}.getType());
                            list = lists.getList();
                        }
                        if (requestType.equals(StringConstant.PLAY_REQUEST_TYPE_SEARCH_VOICE) && refreshType == 0) playList.clear();
                        if (list != null && list.size() >= 10) {
                            page++;
                            setPullAndLoad(true, true);
                        } else {
                            setPullAndLoad(true, false);
                        }
                        subList = clearContentPlayNull(list);// 去空
                        mUIHandler.sendEmptyMessageDelayed(IntegerConstant.PLAY_UPDATE_LIST, 200);
                    } else {
                        setPullAndLoad(false, false);
                        if (!requestType.equals(StringConstant.PLAY_REQUEST_TYPE_MAIN_PAGE) && refreshType == 0) {
                            ToastUtils.show_always(context, "没有查询到相关内容，您换个词试试吧~");
                        } else if (!requestType.equals(StringConstant.PLAY_REQUEST_TYPE_SEARCH_VOICE) && refreshType != 0) {
                            ToastUtils.show_always(context, "没有更多推荐了~~");
                        } else if (refreshType == 0) {
                            mUIHandler.sendEmptyMessageDelayed(IntegerConstant.PLAY_UPDATE_LIST, 200);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setPullAndLoad(true, false);
                    mUIHandler.sendEmptyMessageDelayed(IntegerConstant.PLAY_UPDATE_LIST, 200);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                setPullAndLoad(true, false);
                mUIHandler.sendEmptyMessageDelayed(IntegerConstant.PLAY_UPDATE_LIST, 200);
            }
        });
    }

    // 设置 headView 的界面
    protected void resetHeadView() {
        if (GlobalConfig.playerObject != null) {
            // 播放的节目标题
            String contentTitle = GlobalConfig.playerObject.getContentName();
            if (contentTitle != null) {
                mPlayAudioTitleName.setText(contentTitle);
            } else {
                mPlayAudioTitleName.setText("未知");
            }

            // 播放的节目封面图片
            String url = GlobalConfig.playerObject.getContentImg();
            if (url != null) {// 有封面图片
                if (!url.startsWith("http")) {
                    url = GlobalConfig.imageurl + url;
                }
                url = AssembleImageUrlUtils.assembleImageUrl180(url);
                Picasso.with(context).load(url.replace("\\/", "/")).into(mPlayAudioImageCover);
            } else {// 没有封面图片设置默认图片
                mPlayAudioImageCover.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx));
            }
        } else {
            ToastUtils.show_always(context, "播放器数据获取异常，请退出程序后尝试");
        }
    }
}