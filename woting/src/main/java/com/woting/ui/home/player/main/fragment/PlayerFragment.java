package com.woting.ui.home.player.main.fragment;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ShareUtils;
import com.woting.common.util.StringUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.HorizontalListView;
import com.woting.common.widgetui.MarqueeTextView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.download.activity.DownloadActivity;
import com.woting.ui.download.dao.FileInfoDao;
import com.woting.ui.download.fragment.DownLoadUnCompleted;
import com.woting.ui.download.model.FileInfo;
import com.woting.ui.download.service.DownloadService;
import com.woting.ui.home.player.main.adapter.ImageAdapter;
import com.woting.ui.home.player.main.adapter.PlayerListAdapter;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.LanguageSearchInside;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.player.main.model.ShareModel;
import com.woting.ui.home.player.programme.ProgrammeActivity;
import com.woting.ui.home.player.timeset.activity.TimerPowerOffActivity;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.album.model.ContentInfo;
import com.woting.ui.home.program.comment.CommentActivity;
import com.woting.ui.mine.playhistory.activity.PlayHistoryActivity;
import com.woting.video.IntegrationPlayer;
import com.woting.video.VoiceRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 播放主界面
 */
public class PlayerFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static int TIME_UI = 10;// 更新时间
    private final static int VOICE_UI = 11;// 更新语音搜索
    private final static int RefreshProgram = 12;// 刷新节目单

    private static SharedPreferences sp = BSApplication.SharedPreferences;// 数据存储
    public static FragmentActivity context;
    public static IntegrationPlayer mPlayer;// 播放器
    private static SearchPlayerHistoryDao mSearchHistoryDao;// 搜索历史数据库
    private static String mRadioContentId;
    private FileInfoDao mFileDao;// 文件相关数据库
    private AudioManager audioMgr;// 声音管理
    private VoiceRecognizer mVoiceRecognizer;// 讯飞
    private MessageReceiver mReceiver;// 广播接收

    private static PlayerListAdapter adapter;

    private static Dialog dialog;// 加载数据对话框
    private static Dialog wifiDialog;// WIFI 提醒对话框
    private Dialog shareDialog;// 分享对话框
    private View rootView;

    public static MarqueeTextView mPlayAudioTitleName;// 正在播放的节目的标题
    private static View mViewVoice;// 语音搜索 点击右上角"语音"显示
    public static TextView mVoiceTextSpeakStatus;// 语音搜索状态
    private ImageView mVoiceImageSpeak;// 按下说话 抬起开始搜索

    private static ImageView mPlayAudioImageCover;// 播放节目的封面
    private static ImageView mPlayImageStatus;// 播放状态图片  播放 OR 暂停

    private static SeekBar mSeekBar;// 播放进度
    public static TextView mSeekBarStartTime;// 进度的开始时间
    public static TextView mSeekBarEndTime;// 播放节目总长度

    public static TextView mPlayAudioTextLike;// 喜欢播放节目
    public static TextView mPlayAudioTextProgram;// 节目单
    public static TextView mPlayAudioTextDownLoad;// 下载
    public static TextView mPlayAudioTextShare;// 分享
    public static TextView mPlayAudioTextComment;// 评论
    public static TextView mPlayAudioTextMore;// 更多
    private View mViewMoreChose;// 点击"更多"显示

    private View mProgramDetailsView;// 节目详情
    private TextView mProgramVisible;// "隐藏" OR "显示"
    private static TextView mProgramTextAnchor;// 主播
    public static TextView mProgramTextSequ;// 专辑
    public static TextView mProgramSources;// 来源
    public static TextView mProgramTextDescn;// 节目介绍

    private static XListView mListView;// 播放列表

    public static int timerService;// 当前节目播放剩余时间长度
    public static int TextPage = 0;// 文本搜索 page
    private static int sendType;// 第一次获取数据是有分页加载的
    private static int page = 1;// mainPage
    private static int voicePage = 1;// 语音搜索 page
    private static int num;// == -2 播放器没有播放  == -1 播放器里边的数据不在 list 中  == 其它 是在 list 中

    private int stepVolume;
    private int curVolume;// 当前音量
    private int refreshType;// 是不是第一次请求数据
//    private int voiceType = 2;// 是否按下语音按钮 == 1 按下  == 2 松手

    private Bitmap bmpPress;// 语音搜索按钮按下的状态图片
    private Bitmap bmp;// 语音搜索按钮未按下的状态图片

    public static boolean isCurrentPlay;
    private boolean detailsFlag = false;// 是否展示节目详情
    private boolean first = true;// 第一次进入界面

    private String voiceStr;// 语音搜索内容

    private static ArrayList<LanguageSearchInside> allList = new ArrayList<>();
    private static Timer mTimer;
    private static String IsPlaying; //获取的当前的播放内容

    /////////////////////////////////////////////////////////////
    // 以下是生命周期方法
    /////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setReceiver();// 注册广播接收器
        initData();// 初始化数据
        initDao();// 初始化数据库命令执行对象
        initTimerTask(); //定时获取节目单的方法
        fileInfoList = getDownList();// 获取已经下载的列表
        Log.i("TAG", "onCreate: fileInfoList.size  -- > " + fileInfoList.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_play, container, false);
        View headView = LayoutInflater.from(context).inflate(R.layout.headview_fragment_play, null);
        initViews(headView);// 设置界面
        initEvent(headView);// 设置控件点击事件
        return rootView;
    }

    // 初始化视图
    private void initViews(View view) {
        // -----------------  HeadView 相关控件初始化 START  ----------------
        ImageView mPlayAudioImageCoverMask = (ImageView) view.findViewById(R.id.image_liu);// 封面图片的六边形遮罩
        mPlayAudioImageCoverMask.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_bd));

        mPlayAudioTitleName = (MarqueeTextView) view.findViewById(R.id.tv_name);// 正在播放的节目的标题
        mPlayAudioImageCover = (ImageView) view.findViewById(R.id.img_news);// 播放节目的封面

        mPlayImageStatus = (ImageView) view.findViewById(R.id.img_play);// 播放状态图片  播放 OR 暂停

        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);// 播放进度
        mSeekBarStartTime = (TextView) view.findViewById(R.id.time_start);// 进度的开始时间
        mSeekBarEndTime = (TextView) view.findViewById(R.id.time_end);// 播放节目总长度

        mPlayAudioTextLike = (TextView) view.findViewById(R.id.tv_like);// 喜欢播放节目
        mPlayAudioTextProgram = (TextView) view.findViewById(R.id.tv_programme);// 节目单
        mPlayAudioTextDownLoad = (TextView) view.findViewById(R.id.tv_download);// 下载
        mPlayAudioTextShare = (TextView) view.findViewById(R.id.tv_share);// 分享
        mPlayAudioTextComment = (TextView) view.findViewById(R.id.tv_comment);// 评论
        mPlayAudioTextMore = (TextView) view.findViewById(R.id.tv_more);// 更多

        mProgramDetailsView = view.findViewById(R.id.rv_details);// 节目详情布局
        mProgramVisible = (TextView) view.findViewById(R.id.tv_details_flag);// "隐藏" OR "显示"
        mProgramTextAnchor = (TextView) view.findViewById(R.id.tv_zhu_bo);// 主播
        mProgramTextSequ = (TextView) view.findViewById(R.id.tv_sequ);// 专辑
        mProgramSources = (TextView) view.findViewById(R.id.tv_origin);// 来源
        mProgramTextDescn = (TextView) view.findViewById(R.id.tv_desc);// 节目介绍

        // ------- 暂无标签 ------
//        GridView flowTag = (GridView) view.findViewById(R.id.gv_tag);
//        List<String> testList = new ArrayList<>();
//        testList.add("逻辑思维");
//        testList.add("不是我不明白");
//        testList.add("今天你吃饭了吗");
//        testList.add("看世界");
//        testList.add("影视资讯");
//        flowTag.setAdapter(new SearchHotAdapter(context, testList));// 展示热门搜索词
        // ------- 暂无标签 ------
        // -----------------  HeadView 相关控件初始化 END  ----------------

        // -----------------  RootView 相关控件初始化 START  ----------------
        mListView = (XListView) rootView.findViewById(R.id.listView);
        mViewMoreChose = rootView.findViewById(R.id.lin_chose);// 点击"更多"显示

        mViewVoice = rootView.findViewById(R.id.id_voice_transparent);// 语音搜索 点击右上角"语音"显示
        mVoiceTextSpeakStatus = (TextView) rootView.findViewById(R.id.tv_speak_status);// 语音搜索状态
        mVoiceImageSpeak = (ImageView) rootView.findViewById(R.id.imageView_voice);
        // -----------------  RootView 相关控件初始化 END  ----------------

        mListView.addHeaderView(view);
        wifiDialog();// wifi 提示 dialog
        shareDialog();// 分享 dialog
    }

    // 初始化点击事件
    private void initEvent(View view) {
        // -----------------  HeadView 相关控件设置监听 START  ----------------
        view.findViewById(R.id.lin_lukuangtts).setOnClickListener(this);// 路况
        view.findViewById(R.id.lin_voicesearch).setOnClickListener(this);// 语音
        view.findViewById(R.id.lin_left).setOnClickListener(this);// 播放上一首
        view.findViewById(R.id.lin_center).setOnClickListener(this);// 播放
        view.findViewById(R.id.lin_right).setOnClickListener(this);// 播放下一首

        mPlayAudioTextLike.setOnClickListener(this);// 喜欢播放节目
        mPlayAudioTextProgram.setOnClickListener(this);// 节目单
        mPlayAudioTextDownLoad.setOnClickListener(this);// 下载
        mPlayAudioTextShare.setOnClickListener(this);// 分享
        mPlayAudioTextComment.setOnClickListener(this);// 评论
        mPlayAudioTextMore.setOnClickListener(this);// 更多
        mProgramVisible.setOnClickListener(this);// 点击显示节目详情
        setListener();
        // -----------------  HeadView 相关控件设置监听 END  ----------------

        // -----------------  RootView 相关控件设置监听 START  ----------------
//        mListView.setXListViewListener(this);// 设置下拉刷新和加载更多监听
        mListView.setOnItemClickListener(this);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);

        rootView.findViewById(R.id.lin_other).setOnClickListener(this);// 灰色透明遮罩 点击隐藏更多
        rootView.findViewById(R.id.lin_ly_ckzj).setOnClickListener(this);// 查看专辑
        rootView.findViewById(R.id.lin_ly_ckzb).setOnClickListener(this);// 查看主播
        rootView.findViewById(R.id.lin_ly_history).setOnClickListener(this);// 查看播放历史
        rootView.findViewById(R.id.lin_ly_timeover).setOnClickListener(this);// 定时关闭
        rootView.findViewById(R.id.tv_ly_qx).setOnClickListener(this);// 取消 点击隐藏更多

        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);// 取消  点击关闭语音搜索
        rootView.findViewById(R.id.view__voice_other).setOnClickListener(this);// 点击隐藏语音搜索
        mVoiceImageSpeak.setOnTouchListener(new MyVoiceSpeakTouchLis());
        mVoiceImageSpeak.setImageBitmap(bmp);
        // -----------------  RootView 相关控件设置监听 END  ----------------
    }

    // 初始化数据
    private void initData() {
        mPlayer = IntegrationPlayer.getInstance();

        refreshType = 0;// 是不是第一次请求数据
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
            filter.addAction(BroadcastConstants.PLAYERVOICE);
            filter.addAction(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
            filter.addAction(BroadcastConstants.PUSH_MUSIC);
            filter.addAction(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);

            // 下载完成更新 LocalUrl
            filter.addAction(BroadcastConstants.ACTION_FINISHED);
            filter.addAction(BroadcastConstants.ACTION_FINISHED_NO_DOWNLOADVIEW);
            context.registerReceiver(mReceiver, filter);
        }
    }

    // 初始化数据库命令执行对象
    private void initDao() {
        mSearchHistoryDao = new SearchPlayerHistoryDao(context);
        mFileDao = new FileInfoDao(context);
    }

    // 初始化音频控制器
    private void setVoice() {
        audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取最大音乐音量
        stepVolume = maxVolume / 100;
    }

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
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 用来判读是否是第一次进入
        if (first) {
            first = false;

            // 从播放历史界面或者我喜欢的界面跳转到该界面
            String enter = sp.getString(StringConstant.PLAYHISTORYENTER, "false");
            String news = sp.getString(StringConstant.PLAYHISTORYENTERNEWS, "");
            if (enter.equals("true")) {
                TextPage = 0;
                sendTextRequest(news);
                SharedPreferences.Editor et = sp.edit();
                et.putString(StringConstant.PLAYHISTORYENTER, "false");
                if (et.commit()) Log.v("TAG", "数据 commit 失败!");
            } else {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialogph(context, "通讯中");
                    firstSend();
                } else {
                    first = true;
                    mListView.setAdapter(adapter = new PlayerListAdapter(context, allList));
                    setPullAndLoad(true, false);
                }
            }
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
    }

    public static void playNoNet() {

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
    private static void updateTextViewWithTimeFormat(TextView view, long second) {
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

    // 设置 headView 的界面
    protected static void resetHeadView() {
        if (GlobalConfig.playerObject != null) {
            String type = GlobalConfig.playerObject.getMediaType();

            // 播放的节目标题
            String contentTitle = GlobalConfig.playerObject.getContentName();
            if (contentTitle != null) {
                mPlayAudioTitleName.setText(contentTitle);
            } else {
                mPlayAudioTitleName.setText("未知");
            }

            // 主播信息
            if (GlobalConfig.playerObject.getContentPersons() != null && GlobalConfig.playerObject.getContentPersons().size() > 0) {
                try {
                    if (TextUtils.isEmpty(GlobalConfig.playerObject.getContentPersons().get(0).getPerName())) {
                        mProgramTextAnchor.setText(GlobalConfig.playerObject.getContentPersons().get(0).getPerName());
                    } else {
                        mProgramTextAnchor.setText("未知");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mProgramTextAnchor.setText("未知");
                }
            } else {
                // 节目详情 主播  暂没有主播
                mProgramTextAnchor.setText("未知");
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

            // 喜欢状态
            String contentFavorite = GlobalConfig.playerObject.getContentFavorite();
            if (type != null && type.equals("TTS")) {// TTS 不支持喜欢
                mPlayAudioTextLike.setClickable(false);
                mPlayAudioTextLike.setText("喜欢");
                mPlayAudioTextLike.setTextColor(context.getResources().getColor(R.color.gray));
                mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                        null, context.getResources().getDrawable(R.mipmap.wt_dianzan_nomal_gray), null, null);
            } else {
                mPlayAudioTextLike.setClickable(true);
                mPlayAudioTextLike.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                if (contentFavorite == null || contentFavorite.equals("0")) {
                    mPlayAudioTextLike.setText("喜欢");
                    mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                            null, context.getResources().getDrawable(R.mipmap.wt_dianzan_nomal), null, null);
                } else {
                    mPlayAudioTextLike.setText("已喜欢");
                    mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                            null, context.getResources().getDrawable(R.mipmap.wt_dianzan_select), null, null);
                }
            }

            // 节目单 RADIO
            if (type != null && type.equals("RADIO")) {
                mPlayAudioTextProgram.setVisibility(View.VISIBLE);
                mPlayAudioTextProgram.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                mPlayAudioTextProgram.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.mipmap.img_play_more_jiemudan), null, null);
            } else {// 电台 有节目单
                mPlayAudioTextProgram.setVisibility(View.GONE);
            }

            // 下载状态
            if (type != null && type.equals("AUDIO")) {// 可以下载
                if (fileInfoList != null && fileInfoList.size() > 0) {
                    for (int i = 0, size = fileInfoList.size(); i < size; i++) {
                        if (GlobalConfig.playerObject.getContentPlay() != null && GlobalConfig.playerObject.getContentPlay().equals(fileInfoList.get(i).getUrl())) {
                            GlobalConfig.playerObject.setLocalurl(fileInfoList.get(i).getLocalurl());
                        }
                    }
                }
                mPlayAudioTextDownLoad.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(GlobalConfig.playerObject.getLocalurl())) {// 已下载
                    mPlayAudioTextDownLoad.setClickable(false);
                    mPlayAudioTextDownLoad.setCompoundDrawablesWithIntrinsicBounds(
                            null, context.getResources().getDrawable(R.mipmap.wt_play_xiazai_no), null, null);
                    mPlayAudioTextDownLoad.setTextColor(context.getResources().getColor(R.color.gray));
                    mPlayAudioTextDownLoad.setText("已下载");
                } else {// 没有下载
                    mPlayAudioTextDownLoad.setClickable(true);
                    mPlayAudioTextDownLoad.setCompoundDrawablesWithIntrinsicBounds(
                            null, context.getResources().getDrawable(R.mipmap.wt_play_xiazai), null, null);
                    mPlayAudioTextDownLoad.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                    mPlayAudioTextDownLoad.setText("下载");
                }
            } else {// 不可以下载
                if (type != null && type.equals("TTS")) {
                    mPlayAudioTextDownLoad.setVisibility(View.VISIBLE);
                    mPlayAudioTextDownLoad.setClickable(false);
                    mPlayAudioTextDownLoad.setCompoundDrawablesWithIntrinsicBounds(
                            null, context.getResources().getDrawable(R.mipmap.wt_play_xiazai_no), null, null);
                    mPlayAudioTextDownLoad.setTextColor(context.getResources().getColor(R.color.gray));
                    mPlayAudioTextDownLoad.setText("下载");
                } else {
                    mPlayAudioTextDownLoad.setVisibility(View.GONE);
                }
            }

            // 评论  TTS 不支持评论
            if (type != null && type.equals("TTS")) {
                mPlayAudioTextComment.setClickable(false);
                mPlayAudioTextComment.setTextColor(context.getResources().getColor(R.color.gray));
                mPlayAudioTextComment.setCompoundDrawablesWithIntrinsicBounds(
                        null, context.getResources().getDrawable(R.mipmap.wt_comment_image_gray), null, null);
            } else if (type != null && !type.equals("TTS")) {
                mPlayAudioTextComment.setClickable(true);
                mPlayAudioTextComment.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                mPlayAudioTextComment.setCompoundDrawablesWithIntrinsicBounds(
                        null, context.getResources().getDrawable(R.mipmap.wt_comment_image), null, null);
            }

            // 节目详情 专辑
            String sequName = GlobalConfig.playerObject.getSequName();
            if (sequName != null && !sequName.trim().equals("") && !sequName.equals("null")) {
                mProgramTextSequ.setText(sequName);
            } else {
                mProgramTextSequ.setText("暂无专辑");
            }

            // 节目详情 来源
            String contentPub = GlobalConfig.playerObject.getContentPub();
            if (contentPub != null && !contentPub.trim().equals("") && !contentPub.equals("null")) {
                mProgramSources.setText(contentPub);
            } else {
                mProgramSources.setText("暂无来源");
            }

            // 节目详情 介绍
            String contentDescn = GlobalConfig.playerObject.getContentDescn();
            if (contentDescn != null && !contentDescn.trim().equals("") && !contentDescn.equals("null")) {
                mProgramTextDescn.setText(contentDescn);
            } else {
                mProgramTextDescn.setText("暂无介绍");
            }
        } else {
            ToastUtils.show_always(context, "播放器数据获取异常，请退出程序后尝试");
        }
    }


    // 关闭 linChose 界面
    private static void linChoseClose(View view) {
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

    // 分享模块
    private void shareDialog() {
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sharedialog, null);
        HorizontalListView mGallery = (HorizontalListView) dialogView.findViewById(R.id.share_gallery);
        shareDialog = new Dialog(context, R.style.MyDialog);
        // 从底部上升到一个位置
        shareDialog.setContentView(dialogView);
        Window window = shareDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        ViewGroup.LayoutParams params = dialogView.getLayoutParams();
        params.width = screenWidth;
        dialogView.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        shareDialog.setCanceledOnTouchOutside(true);
        shareDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        PlayerFragment.dialog = DialogUtils.Dialogphnoshow(context, "通讯中", PlayerFragment.dialog);
        Config.dialog = PlayerFragment.dialog;
        final List<ShareModel> mList = ShareUtils.getShareModelList();
        ImageAdapter shareAdapter = new ImageAdapter(context, mList);
        mGallery.setAdapter(shareAdapter);
        mGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SHARE_MEDIA Platform = mList.get(position).getSharePlatform();
                callShare(Platform);
                shareDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareDialog.isShowing()) shareDialog.dismiss();
            }
        });
    }

    // wifi 弹出框
    private void wifiDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_wifi_set, null);
        wifiDialog = new Dialog(context, R.style.MyDialog);
        wifiDialog.setContentView(dialog1);
        wifiDialog.setCanceledOnTouchOutside(true);
        wifiDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        // 取消播放
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiDialog.dismiss();
            }
        });
        // 允许本次播放
        dialog1.findViewById(R.id.tv_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiDialog.dismiss();
                startPlay(position);
            }
        });
        // 不再提醒
        dialog1.findViewById(R.id.tv_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor et = sp.edit();
                et.putString(StringConstant.WIFISHOW, "false");
                if (et.commit()) Log.i("TAG", "commit Fail");
                wifiDialog.dismiss();
                startPlay(position);
            }
        });
    }

    // 获取数据库数据
    private static LanguageSearchInside getDaoList(Context context) {
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

    // 分享数据详情
    protected void callShare(SHARE_MEDIA Platform) {
        if (GlobalConfig.playerObject != null) {
            String shareName = GlobalConfig.playerObject.getContentName();
            if (shareName == null || shareName.equals("")) {
                shareName = "我听我享听";
            }
            String shareDesc = GlobalConfig.playerObject.getContentDescn();
            if (shareDesc == null || shareDesc.equals("")) {
                shareDesc = "暂无本节目介绍";
            }
            String shareContentImg = GlobalConfig.playerObject.getContentImg();
            if (shareContentImg == null || shareContentImg.equals("")) {
                shareContentImg = "http://182.92.175.134/img/logo-web.png";
            }
            UMImage image = new UMImage(context, shareContentImg);
            String shareUrl = GlobalConfig.playerObject.getContentShareURL();
            if (shareUrl == null || shareUrl.equals("")) {
                shareUrl = "http://www.wotingfm.com/";
            }
            new ShareAction(context).setPlatform(Platform).withMedia(image).withText(shareDesc).withTitle(shareName).withTargetUrl(shareUrl).share();
        }
    }

    // 内容的下载
    private void download() {
        LanguageSearchInside data = GlobalConfig.playerObject;
        if (data == null || !data.getMediaType().equals("AUDIO")) return;
        if (data.getLocalurl() != null) {
            ToastUtils.show_always(context, "此节目已经保存到本地，请到已下载界面查看");
            return;
        }
        // 对数据进行转换
        List<ContentInfo> dataList = new ArrayList<>();
        ContentInfo m = new ContentInfo();
        // m.setAuthor(data.getContentPersons());
        m.setContentPlay(data.getContentPlay());
        m.setContentImg(data.getContentImg());
        m.setContentName(data.getContentName());
        m.setContentPub(data.getContentPub());
        m.setContentTimes(data.getContentTimes());
        m.setUserid(CommonUtils.getUserId(context));
        m.setDownloadtype("0");
        if (data.getSeqInfo() == null || data.getSeqInfo().getContentName() == null || data.getSeqInfo().getContentName().equals("")) {
            m.setSequname(data.getContentName());
        } else {
            m.setSequname(data.getSeqInfo().getContentName());
        }
        if (data.getSeqInfo() == null || data.getSeqInfo().getContentId() == null || data.getSeqInfo().getContentId().equals("")) {
            m.setSequid(data.getContentId());
        } else {
            m.setSequid(data.getSeqInfo().getContentId());
        }
        if (data.getSeqInfo() == null || data.getSeqInfo().getContentImg() == null || data.getSeqInfo().getContentImg().equals("")) {
            m.setSequimgurl(data.getContentImg());
        } else {
            m.setSequimgurl(data.getSeqInfo().getContentImg());
        }
        if (data.getSeqInfo() == null || data.getSeqInfo().getContentDesc() == null || data.getSeqInfo().getContentDesc().equals("")) {
            m.setSequdesc(data.getContentDescn());
        } else {
            m.setSequdesc(data.getSeqInfo().getContentDesc());
        }
        dataList.add(m);
        // 检查是否重复,如果不重复插入数据库，并且开始下载，重复了提示
        List<FileInfo> fileDataList = mFileDao.queryFileInfoAll(CommonUtils.getUserId(context));
        if (fileDataList.size() != 0) {// 此时有下载数据
            boolean isDownload = false;
            for (int j = 0; j < fileDataList.size(); j++) {
                if (fileDataList.get(j).getUrl().equals(m.getContentPlay())) {
                    isDownload = true;
                    break;
                }
            }
            if (isDownload) {
                ToastUtils.show_always(context, m.getContentName() + "已经存在于下载列表");
            } else {
                mFileDao.insertFileInfo(dataList);
                ToastUtils.show_always(context, m.getContentName() + "已经插入了下载列表");
                List<FileInfo> fileUnDownLoadList = mFileDao.queryFileInfo("false", CommonUtils.getUserId(context));// 未下载列表
                for (int kk = 0; kk < fileUnDownLoadList.size(); kk++) {
                    if (fileUnDownLoadList.get(kk).getDownloadtype() == 1) {
                        DownloadService.workStop(fileUnDownLoadList.get(kk));
                        mFileDao.updataDownloadStatus(fileUnDownLoadList.get(kk).getUrl(), "2");
                    }
                }
                for (int k = 0; k < fileUnDownLoadList.size(); k++) {
                    if (fileUnDownLoadList.get(k).getUrl().equals(m.getContentPlay())) {
                        FileInfo file = fileUnDownLoadList.get(k);
                        mFileDao.updataDownloadStatus(m.getContentPlay(), "1");
                        DownloadService.workStart(file);
                        Intent p_intent = new Intent(BroadcastConstants.PUSH_DOWN_UNCOMPLETED);
                        context.sendBroadcast(p_intent);
                        break;
                    }
                }
            }
        } else {// 此时库里没数据
            mFileDao.insertFileInfo(dataList);
            ToastUtils.show_always(context, m.getContentName() + "已经插入了下载列表");
            List<FileInfo> fileUnDownloadList = mFileDao.queryFileInfo("false", CommonUtils.getUserId(context));// 未下载列表
            for (int k = 0; k < fileUnDownloadList.size(); k++) {
                if (fileUnDownloadList.get(k).getUrl().equals(m.getContentPlay())) {
                    FileInfo file = fileUnDownloadList.get(k);
                    mFileDao.updataDownloadStatus(m.getContentPlay(), "1");
                    DownloadService.workStart(file);
                    if (DownloadActivity.isVisible) {
                        DownLoadUnCompleted.dwType = true;
                    }
                    Intent p_intent = new Intent(BroadcastConstants.PUSH_DOWN_UNCOMPLETED);
                    context.sendBroadcast(p_intent);
                    break;
                }
            }
        }
    }

    private static List<FileInfo> fileInfoList;// 保存已经下载得数据

    // 获取已经下载过的列表
    private List<FileInfo> getDownList() {
        return mFileDao.queryFileInfo("true", CommonUtils.getUserId(context));
    }

    /////////////////////////////////////////////////////////////
    // 以下是系统方法
    /////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_lukuangtts:// 获取路况
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
            case R.id.view__voice_other:
                linChoseClose(mViewVoice);
                if (mCloseVoiceRunnable != null) {
                    mUIHandler.removeCallbacks(mCloseVoiceRunnable);
                    mVoiceTextSpeakStatus.setText("请按住讲话");
                }
                break;
            case R.id.tv_share:// 分享
                shareDialog.show();
                break;
            case R.id.tv_like:// 喜欢
                if (!CommonHelper.checkNetwork(context)) return;
                if (GlobalConfig.playerObject == null) return;
                if (GlobalConfig.playerObject.getContentFavorite() != null && !GlobalConfig.playerObject.getContentFavorite().equals("")) {
                    sendFavorite();
                } else {
                    ToastUtils.show_always(context, "本节目暂时不支持喜欢!");
                }
                break;
            case R.id.tv_details_flag:// 节目详情
                if (!detailsFlag) {
                    mProgramVisible.setText("  隐藏  ");
                    mProgramDetailsView.setVisibility(View.VISIBLE);
                } else {
                    mProgramVisible.setText("  显示  ");
                    mProgramDetailsView.setVisibility(View.GONE);
                }
                detailsFlag = !detailsFlag;
                break;
            case R.id.lin_left:// 上一首
                last();
                break;
            case R.id.lin_center:// 播放
                startPlay(position);
                break;
            case R.id.lin_right:// 下一首
                next();
                break;
            case R.id.tv_more:// 更多
                if (mViewMoreChose.getVisibility() == View.VISIBLE) {
                    linChoseClose(mViewMoreChose);
                } else {
                    linChoseOpen(mViewMoreChose);
                }
                break;
            case R.id.tv_ly_qx:// 取消 点击隐藏更多
                linChoseClose(mViewMoreChose);
                break;
            case R.id.lin_other:// 点击隐藏更多
                linChoseClose(mViewMoreChose);
                break;
            case R.id.lin_ly_timeover:// 定时关闭
                linChoseClose(mViewMoreChose);
                startActivity(new Intent(context, TimerPowerOffActivity.class));
                break;
            case R.id.lin_ly_history:// 播放历史
                linChoseClose(mViewMoreChose);
                startActivity(new Intent(context, PlayHistoryActivity.class));
                break;
            case R.id.tv_programme:// 节目单
                Intent p = new Intent(context, ProgrammeActivity.class);
                Bundle b = new Bundle();
                b.putString("BcId", GlobalConfig.playerObject.getContentId());
                p.putExtras(b);
                startActivity(p);
                break;
            case R.id.lin_ly_ckzb:// 查看主播
                if (!CommonHelper.checkNetwork(context)) return;
                linChoseClose(mViewMoreChose);
                ToastUtils.show_always(context, "查看主播");
                break;
            case R.id.lin_ly_ckzj:// 查看专辑
                if (!CommonHelper.checkNetwork(context)) return;
                if (GlobalConfig.playerObject == null) return;
                linChoseClose(mViewMoreChose);
                if (GlobalConfig.playerObject.getSequId() != null) {
                    Intent intent = new Intent(context, AlbumActivity.class);
                    intent.putExtra("type", "player");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", GlobalConfig.playerObject);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    ToastUtils.show_always(context, "本节目没有所属专辑");
                }
                break;
            case R.id.tv_comment:// 评论
                if (GlobalConfig.playerObject == null) return;
                if (!TextUtils.isEmpty(GlobalConfig.playerObject.getContentId()) && !TextUtils.isEmpty(GlobalConfig.playerObject.getMediaType())) {
                    if (CommonUtils.getUserIdNoImei(context) != null && !CommonUtils.getUserIdNoImei(context).equals("")) {
                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra("contentId", GlobalConfig.playerObject.getContentId());
                        intent.putExtra("MediaType", GlobalConfig.playerObject.getMediaType());
                        startActivity(intent);
                    } else {
                        ToastUtils.show_always(context, "请先登录~~");
                    }
                } else {
                    ToastUtils.show_always(context, "当前播放的节目的信息有误，无法获取评论列表");
                }
                break;
            case R.id.tv_download:// 下载
                download();
                break;
        }
    }

    static Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_UI: // 更新进度及时间
                    mUIHandler.sendEmptyMessageDelayed(TIME_UI, 1000);
                    break;
                case VOICE_UI:
                    linChoseClose(mViewVoice);
                    mVoiceTextSpeakStatus.setText("请按住讲话");
                    break;
                case RefreshProgram:
                    if (!TextUtils.isEmpty(IsPlaying) && !TextUtils.isEmpty(mRadioContentId)) {
                        for (int i = 0; i < allList.size(); i++) {
                            if (allList.get(i).getContentId() != null && mRadioContentId != null &&
                                    allList.get(i).getContentId().equals(mRadioContentId)) {
                                if (allList.get(i).getIsPlaying() != null && !allList.get(i).getIsPlaying().equals(IsPlaying)) {
                                    allList.get(i).setIsPlaying(IsPlaying);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
            }
        }
    };

    // listView 的 item 点击事件监听
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startPlay(position);
    }

    // 广播接收器
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BroadcastConstants.PLAY_TEXT_VOICE_SEARCH:
                    PlayerFragment.TextPage = 0;
                    isResetData = true;
                    sendTextContent = intent.getStringExtra("text");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendTextRequest(sendTextContent);
                        }
                    }, 500);
                    break;
                case BroadcastConstants.PLAYERVOICE:
                    voiceStr = intent.getStringExtra("VoiceContent");
                    if (CommonHelper.checkNetwork(context)) {
                        if (!voiceStr.trim().equals("")) {
                            mVoiceTextSpeakStatus.setText("正在搜索: " + voiceStr);
                            voicePage = 1;
                            searchByVoice(voiceStr);
                            mUIHandler.postDelayed(mCloseVoiceRunnable, 3000);
                        }
                    }
                    break;
                case BroadcastConstants.PUSH_MUSIC:
                    // 监听到电话状态发生更改
                    String phoneType = intent.getStringExtra("outMessage");
                    Log.e("电话状态", phoneType + "");
                    break;
                case BroadcastConstants.ACTION_FINISHED:
                case BroadcastConstants.ACTION_FINISHED_NO_DOWNLOADVIEW:
                    if (mFileDao != null) {
                        fileInfoList = mFileDao.queryFileInfo("true", CommonUtils.getUserId(context));
                        if (GlobalConfig.playerObject == null || GlobalConfig.playerObject.getContentPlay() == null) return;
                        for (int i = 0; i < fileInfoList.size(); i++) {
                            if (fileInfoList.get(i).getUrl().equals(GlobalConfig.playerObject.getContentPlay())) {
                                if (fileInfoList.get(i).getLocalurl() != null) {
                                    GlobalConfig.playerObject.setLocalurl(fileInfoList.get(i).getLocalurl());
                                }
                                mPlayAudioTextDownLoad.setClickable(false);
                                mPlayAudioTextDownLoad.setCompoundDrawablesWithIntrinsicBounds(
                                        null, context.getResources().getDrawable(R.mipmap.wt_play_xiazai_no), null, null);
                                mPlayAudioTextDownLoad.setTextColor(context.getResources().getColor(R.color.gray));
                                mPlayAudioTextDownLoad.setText("已下载");
                                break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    // 智能关闭语音搜索框
    private Runnable mCloseVoiceRunnable = new Runnable() {
        @Override
        public void run() {
            linChoseClose(mViewVoice);// 2秒后隐藏界面
            mVoiceTextSpeakStatus.setText("请按住讲话");
        }
    };

    private static List<String> contentUrlList = new ArrayList<>();// 保存 ContentURI 用于去重  用完即 clear

    /////////////////////////////////////////////////////////////
    // 以下是网络请求操作
    /////////////////////////////////////////////////////////////
    // 第一次进入该界面时候的数据
    private void firstSend() {
        sendType = 1;
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PageType", "0");
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("PageSize", "10");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.mainPageUrl, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType.equals("1001")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    setPullAndLoad(true, false);
                    ToastUtils.show_always(context, "数据出错了，请您稍后再试!");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                if (allList.size() <= 0 || adapter == null) {
                    mListView.setAdapter(adapter = new PlayerListAdapter(context, allList));
                }
                setPullAndLoad(true, false);
            }
        });
    }

    // 喜欢---不喜欢操作
    private static void sendFavorite() {
        dialog = DialogUtils.Dialogph(context, "通讯中");
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", GlobalConfig.playerObject.getMediaType());
            jsonObject.put("ContentId", GlobalConfig.playerObject.getContentId());
            if (GlobalConfig.playerObject.getContentFavorite().equals("0")) {
                jsonObject.put("Flag", 1);
            } else {
                jsonObject.put("Flag", 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.clickFavoriteUrl, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                try {
                    ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && (ReturnType.equals("1001") || ReturnType.equals("1005"))) {
                        if (GlobalConfig.playerObject.getContentFavorite().equals("0")) {
                            mPlayAudioTextLike.setText("已喜欢");
                            mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                                    null, context.getResources().getDrawable(R.mipmap.wt_dianzan_select), null, null);
                            GlobalConfig.playerObject.setContentFavorite("1");
                            if (num > 0) allList.get(num).setContentFavorite("1");
                        } else {
                            mPlayAudioTextLike.setText("喜欢");
                            mPlayAudioTextLike.setCompoundDrawablesWithIntrinsicBounds(
                                    null, context.getResources().getDrawable(R.mipmap.wt_dianzan_nomal), null, null);
                            GlobalConfig.playerObject.setContentFavorite("0");
                            if (num > 0) allList.get(num).setContentFavorite("0");
                        }
                    } else {
                        ToastUtils.show_always(context, "数据出错了，请您稍后再试!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    private String sendTextContent = "";// 关键字

    private boolean isResetData;// 重新获取了数据  searchByText

    // 获取与文字相关的内容数据
    private void sendTextRequest(String contentName) {
        final LanguageSearchInside fList = getDaoList(context);// 得到数据库里边的第一条数据
        if (TextPage == 0) {
            num = 0;
            allList.clear();
            if (fList != null) allList.add(fList);
            GlobalConfig.playerObject = allList.get(num);
            if (adapter == null) {
                mListView.setAdapter(adapter = new PlayerListAdapter(context, allList));
            } else {
                adapter.notifyDataSetChanged();
            }
            TextPage++;
        }
        sendType = 2;
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("SearchStr", contentName);
            jsonObject.put("PageType", "0");
            jsonObject.put("Page", TextPage);
            jsonObject.put("PageSize", "10");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestTextVoicePost(GlobalConfig.getSearchByText, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {

                } else {
                    ToastUtils.show_always(context, "已经没有相关数据啦");
                    setPullAndLoad(true, false);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                setPullAndLoad(true, false);
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 语音搜索请求
    private void searchByVoice(String str) {
        sendType = 3;
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("SearchStr", str);
            jsonObject.put("PageType", "0");
            jsonObject.put("Page", voicePage);
            jsonObject.put("PageSize", "10");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestTextVoicePost(GlobalConfig.searchvoiceUrl, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType.equals("1001")) {

                    } else {
                        ToastUtils.show_always(context, "已经没有相关数据啦");
                        setPullAndLoad(true, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.show_always(context, "已经没有相关数据啦");
                    setPullAndLoad(true, false);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                setPullAndLoad(true, false);
            }
        });
    }

    // 设置刷新和加载
    private static void setPullAndLoad(boolean isPull, boolean isLoad) {
        mListView.setPullRefreshEnable(isPull);
        mListView.setPullLoadEnable(isLoad);
        mListView.stopRefresh();
        mListView.stopLoadMore();
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

    private void sendContentInfo(final String ContentId) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("BcIds", ContentId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getIsPlayIngUrl, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null) { // 根据返回值来对程序进行解析
                        if (ReturnType.equals("1001")) {
                            try {
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
                                    mUIHandler.sendEmptyMessage(RefreshProgram);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtils.show_short(context, "当前无节目单数据");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
            }
        });
    }


    private int position = 0;// 记录当前播放在列表中的位置
    private List<LanguageSearchInside> playList = new ArrayList<>();// 播放列表

    // 注册广播
    private void regitsBroadcast() {

    }

    // 绑定服务
    private void bindService() {

    }

    // 解绑服务
    private void unbindService() {

    }

    // 查询数据
    private void queryData() {
        sendRequest();
    }

    // 发送网络请求
    private void sendRequest() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            return;
        }

        ToastUtils.show_always(context, "");
    }

    // 发送喜欢 OR 取消喜欢的状态到服务器
    private void sendFavoriteRequest() {

    }

    // 内容下载
    private void downloadContent() {

    }

    // 处理消息
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    // 开始播放
    private void startPlay(int position) {

    }

    // 下一首
    private void next() {
        position++;
        if (position >= playList.size()) position = 0;
        startPlay(position);
    }

    // 上一首
    private void last() {
        position--;
        if (position < 0) position = playList.size() - 1;
        startPlay(position);
    }

    // 更新界面
    private void updateView() {

    }

    // 更新播放列表
    private void updateList() {

    }

    // 更新时间
    private void updateTime() {

    }

}