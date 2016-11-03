package Test;/*package com.wotingfm.activity.home.player.main.fragment;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.baidu.cyberplayer.core.BVideoView.OnTotalCacheUpdateListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.woting.R;
import com.woting.video.VlcPlayer;
import com.woting.video.WtAudioPlay;
import com.woting.video.bdplayer;
import com.wotingfm.activity.download.dao.FileInfoDao;
import com.wotingfm.activity.download.model.FileInfo;
import com.wotingfm.activity.download.service.DownloadService;
import com.wotingfm.activity.home.player.main.adapter.ImageAdapter;
import com.wotingfm.activity.home.player.main.adapter.PlayerListAdapter;
import com.wotingfm.activity.home.player.main.dao.SearchPlayerHistoryDao;
import com.wotingfm.activity.home.player.main.model.LanguageSearch;
import com.wotingfm.activity.home.player.main.model.LanguageSearchInside;
import com.wotingfm.activity.home.player.main.model.PlayerHistory;
import com.wotingfm.activity.home.player.main.model.shareModel;
import com.wotingfm.activity.home.player.timeset.activity.TimerPowerOffActivity;
import com.wotingfm.activity.home.player.timeset.service.timeroffservice;
import com.wotingfm.activity.home.program.album.model.ContentInfo;
import com.wotingfm.common.StringConstant;
import com.wotingfm.commonactivity.BaseFragment;
import com.wotingfm.config.GlobalConfig;
import com.wotingfm.utils.CommonHelper;
import com.wotingfm.utils.ImageLoader;
import com.wotingfm.utils.JsonParser;
import com.wotingfm.utils.PhoneMessage;
import com.wotingfm.utils.ReadBitmapUtils;
import com.wotingfm.utils.TimeUtil;
import com.wotingfm.utils.ToastUtil;
import com.wotingfm.utils.Utils;
import com.wotingfm.widgetui.HorizontalListView;
import com.wotingfm.widgetui.MyLinearLayout;
import com.wotingfm.widgetui.xlistview.XListView;
import com.wotingfm.widgetui.xlistview.XListView.IXListViewListener;

*//**
 * 播放主页
 * 
 * 2016年2月4日
 * 
 * @author 辛龙
 *//*
public class PlayerFragment extends BaseFragment
implements OnClickListener, IXListViewListener, OnPreparedListener, OnErrorListener, OnInfoListener,
OnCompletionListener, OnPlayingBufferCacheListener, OnTotalCacheUpdateListener {

	public static FragmentActivity context;
	private String AK = "a27bb387e4e64170984d44a72d06d387"; // 请录入您的AK !!!
	// 功能性
	private static ImageLoader imageLoader;
	private SpeechRecognizer mIat;
	private static SimpleDateFormat format;
	private static SearchPlayerHistoryDao dbdao;
	private FileInfoDao FID;
	private static SharedPreferences sp;
	private AudioManager audioMgr;
	private MessageReceiver Receiver;
	private static BVideoView mVV;
	private static SpeechSynthesizer mTts;
	// 界面
	private static TextView tv_speak_status;
	private static TextView tv_name;
	private static TextView textSpeakContent;
	private View headview;
	private LinearLayout lin_right;
	private static ImageView img_play;
	private LinearLayout lin_left;
	private static ImageView img_news;
	private View rootView;
	private RelativeLayout lin_center;
	public static TextView time_start;
	public static TextView time_end;
	private LinearLayout lin_voicesearch;
	private LinearLayout lin_time;
	private static XListView mlistView;
	private static LinearLayout lin_tuijian;
	private ImageView image_liu;
	private static MyLinearLayout rl_voice;
	private ImageView imageView_voice;
	private TextView tv_cancle;
	private LinearLayout lin_download;
	private UMImage image;
	private LinearLayout lin_share;
	private static SeekBar seekBar;
	private TextView textTime;
	// 数据
	private static int num;// -2 播放器没有播放，-1播放器里边的数据不在list中，其它是在list中
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();// 用HashMap存储听写结果
	private static ArrayList<LanguageSearchInside> alllist = new ArrayList<LanguageSearchInside>();
	private List<FileInfo> filedatalist;
	private List<FileInfo> fileundownloadlist;
	private FileInfo file;
	protected String sequid;
	protected String sequimage;
	protected String sequname;
	protected String sequdesc;
	private int stepVolume;
	private int curVolume;
	private Bitmap bmppresss;
	private Bitmap bmp;
	private int screenw;
	// 其它
	private static PlayerListAdapter adapter;
	private static Dialog dialogs;
	private static Dialog wifidialog;
	private Dialog Sharedialog;
	private Dialog dialog1;
	private static Handler mHandler;
	// 标识参数
	public static int JudgeTextRequest = -1;
	private static int sendtype;// 第一次获取数据是有分页加载的
	private int page = 1;
	private int RefreshType;// 是不是第一次请求数据
	private boolean first = true;// 第一次进入界面
	private int voice_type = 2;// 判断此时是否按下语音按钮，1，按下2，松手
	private final static Object SYNC_Playing = new Object();
	private static int TTS_SpeakProgress = 0;// ttS播放进度
	private WtAudioPlay audioplay;
	
	// 播放状态
	public enum PLAYER_STATUS {
		TTS_PLAYING, 		// TTS播放状态
		TTS_STOP, 			// TTS停止状态
		TTS_POUSE, 			// TTS暂停状态
		TTS_NOTHING, 		// TTS啥也没做
		PLAYER_NOTHING, 	// 播放器啥也没做
		PLAYER_STOP, 		// 播放器停止状态
		PLAYER_POUSE, 		// 播放器暂停状态
		PLAYER_PREPARED, 	// 播放器准备好
		PLAYER_PLAYING		// 播放器播放状态
	}

	public static PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_NOTHING;// T5此时的播放状态
	public static PLAYER_STATUS TTSPlayerStatus = PLAYER_STATUS.TTS_NOTHING;// TTS此时的播放状态
	private static String TTSNews;
	private boolean mIsHwDecode = false;// 编码格式类型判断
	private HandlerThread mHandlerThread;
	private LinearLayout lin_like;
	private static ImageView img_like;
	private LinearLayout lin_lukuangtts;
	private FileInfo filetemp;
	private static SpeechSynthesizer lukuangTts;
	private static TextView tv_like;
	private static mVVHandler mVVHandler;

	private final static int T5_START = 1;
	private final static int T5_STOP = 2;
	private final static int T5_POUSE = 3;
	private final static int T5_RESUME = 4;
	// private final static int TTS_START = 5;
	// private final static int TTS_STOP = 6;
	// private final static int TTS_POUSE = 7;
	// private final static int TTS_RESUME = 8;
	// private final static int NEXT_PLAY = 9;
	private final static int TIME_UI = 10;
	private final static int VOICE_UI = 11;
	
	public static boolean isCurrentPlay;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		imageLoader = new ImageLoader(context);
		bmppresss = ReadBitmapUtils.readBitMap(context, R.drawable.wt_duijiang_button_pressed);
		bmp = ReadBitmapUtils.readBitMap(context, R.drawable.talknormal);
		BVideoView.setAK(AK);// 设置ak
		RefreshType = 0;
		if (Receiver == null) {
			Receiver = new MessageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("pushmusic");
			filter.addAction(timeroffservice.TIMER_UPDATE);
			filter.addAction(timeroffservice.TIMER_STOP);
			context.registerReceiver(Receiver, filter);
		}
		format = new SimpleDateFormat("HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		// 初始化语音配置对象
		SpeechUtility.createUtility(context, SpeechConstant.APPID + "=56275014");
		// 创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
		mIat = SpeechRecognizer.createRecognizer(context, null);
		mTts = SpeechSynthesizer.createSynthesizer(context, null);
		lukuangTts = SpeechSynthesizer.createSynthesizer(context, null);
		setParamTTS();
		initDao();// 初始化数据库命令执行对象
		UMShareAPI.get(context);// 初始化友盟
		audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		// 获取最大音乐音量
		int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// // 初始化音量大概为最大音量的1/2
		// curVolume = maxVolume / 2;
		// 每次调整的音量大概为最大音量的1/100
		stepVolume = maxVolume / 100;
		// audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC,
		// curVolume,AudioManager.FLAG_PLAY_SOUND);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_play, container, false);
		mHandler = new Handler();
		setview(); 		// 设置界面
		setlistener(); 	// 设置监听
		WifiDialog(); 	// wifi提示dialog
		ShareDialog(); 	// 分享dialog

		*//**
		 * 开启后台事件处理线程
		 *//*
		mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mVVHandler = new mVVHandler(mHandlerThread.getLooper());
		return rootView;
	}

	private void setview() {
		mVV = (BVideoView) rootView.findViewById(R.id.video_view);
		mlistView = (XListView) rootView.findViewById(R.id.listView);
		mlistView.setPullLoadEnable(false);
		mlistView.setXListViewListener(this);
		headview = LayoutInflater.from(context).inflate(R.layout.headview_fragment_play, null);
		lin_center = (RelativeLayout) headview.findViewById(R.id.lin_center);
		lin_tuijian = (LinearLayout) headview.findViewById(R.id.lin_tuijian);
		lin_lukuangtts = (LinearLayout) headview.findViewById(R.id.lin_lukuangtts);
		lin_like = (LinearLayout) headview.findViewById(R.id.lin_like);
		img_like = (ImageView) headview.findViewById(R.id.img_like);
		tv_like = (TextView) headview.findViewById(R.id.tv_like);
		lin_right = (LinearLayout) headview.findViewById(R.id.lin_right);
		img_news = (ImageView) headview.findViewById(R.id.img_news);
		img_play = (ImageView) headview.findViewById(R.id.img_play);
		image_liu = (ImageView) headview.findViewById(R.id.image_liu);
		Bitmap bmp1 = ReadBitmapUtils.readBitMap(context, R.drawable.wt_6_b_y_b);
		image_liu.setImageBitmap(bmp1);
		lin_time = (LinearLayout) headview.findViewById(R.id.lin_time);
		lin_left = (LinearLayout) headview.findViewById(R.id.lin_left);
		tv_name = (TextView) headview.findViewById(R.id.tv_name);
		seekBar = (SeekBar) headview.findViewById(R.id.seekBar);
		lin_share = (LinearLayout) headview.findViewById(R.id.lin_share);
		textTime = (TextView) headview.findViewById(R.id.text_time);
		seekBar.setEnabled(false);
		// 配合seekbar使用的标签
		time_start = (TextView) headview.findViewById(R.id.time_start);
		time_end = (TextView) headview.findViewById(R.id.time_end);
		lin_voicesearch = (LinearLayout) headview.findViewById(R.id.lin_voicesearch); // 语音搜索
		rl_voice = (MyLinearLayout) rootView.findViewById(R.id.rl_voice);
		imageView_voice = (ImageView) rootView.findViewById(R.id.imageView_voice);
		imageView_voice.setImageBitmap(bmp);
		tv_cancle = (TextView) rootView.findViewById(R.id.tv_cancle);
		tv_speak_status = (TextView) rootView.findViewById(R.id.tv_speak_status);
		tv_speak_status.setText("请按住讲话");
		textSpeakContent = (TextView) rootView.findViewById(R.id.text_speak_content);

		mlistView.addHeaderView(headview);
		lin_download = (LinearLayout) headview.findViewById(R.id.lin_download);// 下载
	}

	private void setlistener() {
		// 往下是百度播放器的监听
		mVV.setOnPreparedListener(this);
		mVV.setOnCompletionListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);
		mVV.setOnTotalCacheUpdateListener(this);
		mVV.setDecodeMode(mIsHwDecode ? BVideoView.DECODE_HW : BVideoView.DECODE_SW);
		// 往上是百度播放器的监听
		lin_time.setOnClickListener(this);
		lin_like.setOnClickListener(this);
		lin_left.setOnClickListener(this);
		lin_center.setOnClickListener(this);
		lin_lukuangtts.setOnClickListener(this);
		lin_right.setOnClickListener(this);
		tv_cancle.setOnClickListener(this);
		lin_voicesearch.setOnClickListener(this);
		lin_download.setOnClickListener(this);
		lin_share.setOnClickListener(this);

		imageView_voice.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
						audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, stepVolume, AudioManager.FLAG_PLAY_SOUND);
						voice_type = 1;
						mIatResults.clear(); // 设置参数
						setParam(); // 开始听写
						mIat.startListening(mRecoListener);
						tv_speak_status.setText("开始语音转换");
						imageView_voice.setImageBitmap(bmppresss);
						textSpeakContent.setVisibility(View.GONE);
					} else {
						ToastUtil.show_short(context, "网络失败，请检查网络");
					}
					Log.i("---Main---", "--- > > > " + "按下");
					break;
				case MotionEvent.ACTION_UP:
					audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND);
					voice_type = 2;
					mIat.stopListening();
					imageView_voice.setImageBitmap(bmp);
					tv_speak_status.setText("请按住讲话");
					Log.i("---Main---", "--- > > > " + "抬起");
					break;
				}
				return true;
			}
		});
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int iseekPos = seekBar.getProgress();
				mVV.seekTo(iseekPos);
				mUIHandler.sendEmptyMessage(TIME_UI);
				
				*//**
				 * 定时服务开启当前节目播放完关闭时拖动进度条时更新定时时间
				 *//*
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						if(PlayerFragment.isCurrentPlay){
							Intent intent = new Intent(context, timeroffservice.class);
							intent.setAction(timeroffservice.TIMER_START);
							int time = PlayerFragment.timerService;
							intent.putExtra("time", time);
							context.startService(intent);
						}
					}
				}, 1000);
				
				Log.i("progress", "--- > > > + TIME_UI _ 1");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mUIHandler.sendEmptyMessage(TIME_UI);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					if (mPlayerStatus != PLAYER_STATUS.PLAYER_NOTHING && GlobalConfig.playerobject != null
							&& GlobalConfig.playerobject.getMediaType() != null
							&& GlobalConfig.playerobject.getMediaType().equals("AUDIO")) {
						updateTextViewWithTimeFormat(time_start, progress);
					}
				}
			}
		});
	}
	
	private static LanguageSearchInside getdaolist(Context context) {
		if (dbdao == null) {
			dbdao = new SearchPlayerHistoryDao(context);
		}
		List<PlayerHistory> historydatabaselist = dbdao.queryHistory();
		LanguageSearchInside historynews = null;
		if (historydatabaselist != null && historydatabaselist.size() > 0) {
			PlayerHistory historynew = historydatabaselist.get(0);
			historynews = new LanguageSearchInside();
			historynews.setType("1");
			historynews.setContentURI(historynew.getPlayerUrI());
			historynews.setContentPersons("");
			 historynews.setContentCatalogs(""); 
			historynews.setContentKeyWord("");
			historynews.setcTime("");
			historynews.setContentSubjectWord("");
			historynews.setContentTimes("");
			historynews.setContentName(historynew.getPlayerName());
			historynews.setContentPubTime("");
			historynews.setContentPub("");
			historynews.setContentPlay(historynew.getPlayerUrl());
			historynews.setMediaType(historynew.getPlayerMediaType());
			historynews.setContentId(historynew.getContentID());
			historynews.setContentDesc(historynew.getPlayerContentDesc());
			historynews.setContentImg(historynew.getPlayerImage());
			historynews.setPlayerAllTime(historynew.getPlayerAllTime());
			historynews.setPlayerInTime(historynew.getPlayerInTime());
			historynews.setContentShareURL(historynew.getPlayContentShareUrl());
			historynews.setContentFavorite(historynew.getContentFavorite());
			historynews.setLocalurl(historynew.getLocalurl());
		}
		return historynews;
	}

	private void initDao() {// 初始化数据库命令执行对象
		dbdao = new SearchPlayerHistoryDao(context);
		FID = new FileInfoDao(context);
	}

	*//**
	 * 把数据添加数据库
	 * 
	 * @param languageSearchInside
	 *//*
	private static void adddb(LanguageSearchInside languageSearchInside) {
		String playername = languageSearchInside.getContentName();
		String playerimage = languageSearchInside.getContentImg();
		String playerurl = languageSearchInside.getContentPlay();
		String playerurI = languageSearchInside.getContentURI();
		String playermediatype = languageSearchInside.getMediaType();
		String playcontentshareurl = languageSearchInside.getContentShareURL();
		String playeralltime = "";
		String playerintime = "";
		String playercontentdesc = languageSearchInside.getContentDesc();
		String playernum = "999";
		String playerzantype = "false";
		String playerfrom = "";
		String playerfromid = "";
		String playerfromurl = "";
		String playeraddtime = Long.toString(System.currentTimeMillis());
		String bjuserid = Utils.getUserId(context);
		String ContentFavorite = languageSearchInside.getContentFavorite();
		String ContentID = languageSearchInside.getContentId();
		String localurl = languageSearchInside.getLocalurl();
		PlayerHistory history = new PlayerHistory(playername, playerimage, playerurl, playerurI, playermediatype,
				playeralltime, playerintime, playercontentdesc, playernum, playerzantype, playerfrom, playerfromid,
				playerfromurl, playeraddtime, bjuserid, playcontentshareurl, ContentFavorite, ContentID, localurl);

		if (playermediatype != null && playermediatype.trim().length() > 0 && playermediatype.equals("TTS")) {
			dbdao.deleteHistoryById(ContentID);
		} else {
			dbdao.deleteHistory(playerurl);
		}
		dbdao.addHistory(history);
	}

	private static void setitemlistener() {
		mlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int number = position - 2;
				getnetwork(number, context);
				stopCurrentTimer();
			}
		});
	}

	*//**
	 * 按照界面排序号进行播放
	 * 
	 * @param number
	 *//*
	protected static void play(int number) {
		if (lukuangTts.isSpeaking()) {
			lukuangTts.stopSpeaking();
		}
		if (alllist != null && alllist.get(number) != null) {
			adddb(alllist.get(number));
			if (alllist.get(number).getMediaType().equals("AUDIO")) {
				if (TTSPlayerStatus == PLAYER_STATUS.TTS_PLAYING) {
					// mVVHandler.sendEmptyMessage(TTS_POUSE);
					ttsStop();
				}
				if (alllist.get(number).getContentPlay() != null) {
					img_play.setImageResource(R.drawable.wt_play_play);
					if (alllist.get(number).getContentName() != null) {
						tv_name.setText(alllist.get(number).getContentName());
					} else {
						tv_name.setText("wotingkeji");
					}
					if (alllist.get(number).getContentImg() != null) {
						String url;
						if (alllist.get(number).getContentImg().startsWith("http")) {
							url = alllist.get(number).getContentImg();
						} else {
							url = GlobalConfig.imageurl + alllist.get(number).getContentImg();
						}
						imageLoader.DisplayImage(url.replace("\\/", "/"), img_news, false, false, null, null);
					} else {
						img_news.setImageResource(R.drawable.wt_image_playertx_80);
					}
					for (int i = 0; i < alllist.size(); i++) {
						alllist.get(i).setType("1");
					}
					alllist.get(number).setType("2");
					adapter.notifyDataSetChanged();
					if(alllist.get(number).getLocalurl()!=null){
						musicPlay(alllist.get(number).getLocalurl(), "AUDIO");
						ToastUtil.show_allways(context, "正在播放本地内容");
					}else{
						musicPlay(alllist.get(number).getContentPlay(), "AUDIO");
					}
					// dbdao.updatefileinfo(alllist.get(number).getContentPlay(),
					// "playeralltime", String.valueOf(mVV.getDuration()));
					GlobalConfig.playerobject = alllist.get(number);
					resetHeadView();
					num = number;
				} else {
					ToastUtil.show_short(context, "暂不支持播放");
				}
			} else if (alllist.get(number).getMediaType().equals("RADIO")) {
				if (alllist.get(number).getContentPlay() != null) {
					if (TTSPlayerStatus == PLAYER_STATUS.TTS_PLAYING) {
						// mVVHandler.sendEmptyMessage(TTS_POUSE);
						ttsStop();
					}
					img_play.setImageResource(R.drawable.wt_play_play);
					if (alllist.get(number).getContentName() != null) {
						tv_name.setText(alllist.get(number).getContentName());
					} else {
						tv_name.setText("wotingkeji");
					}
					if (alllist.get(number).getContentImg() != null) {
						String url;
						if (alllist.get(number).getContentImg().startsWith("http")) {
							url = alllist.get(number).getContentImg();
						} else {
							url = GlobalConfig.imageurl + alllist.get(number).getContentImg();
						}
						imageLoader.DisplayImage(url.replace("\\/", "/"), img_news, false, false, null, null);
					} else {
						img_news.setImageResource(R.drawable.wt_image_playertx_80);
					}
					for (int i = 0; i < alllist.size(); i++) {
						alllist.get(i).setType("1");
					}
					alllist.get(number).setType("2");
					adapter.notifyDataSetChanged();
					if(alllist.get(number).getLocalurl()!=null){
						musicPlay(alllist.get(number).getLocalurl(), "RADIO");
						ToastUtil.show_allways(context, "正在播放本地内容");
					}else{
						musicPlay(alllist.get(number).getContentPlay(), "RADIO");
					}
					GlobalConfig.playerobject = alllist.get(number);
					resetHeadView();
					num = number;
				} else {
					ToastUtil.show_short(context, "暂不支持播放");
				}
			} else if (alllist.get(number).getMediaType().equals("TTS")) {
				if (alllist.get(number).getContentURI() != null
						&& alllist.get(number).getContentURI().trim().length() > 0) {
					img_play.setImageResource(R.drawable.wt_play_play);
					if (alllist.get(number).getContentName() != null) {
						tv_name.setText(alllist.get(number).getContentName());
					} else {
						tv_name.setText("wotingkeji");
					}
					if (alllist.get(number).getContentImg() != null) {
						String url;
						if (alllist.get(number).getContentImg().startsWith("http")) {
							url = alllist.get(number).getContentImg();
						} else {
							url = GlobalConfig.imageurl + alllist.get(number).getContentImg();
						}
						imageLoader.DisplayImage(url.replace("\\/", "/"), img_news, false, false, null, null);
					} else {
						img_news.setImageResource(R.drawable.wt_image_playertx_80);
					}
					for (int i = 0; i < alllist.size(); i++) {
						alllist.get(i).setType("1");
					}
					alllist.get(number).setType("2");
					adapter.notifyDataSetChanged();
					
					Log.i("播放状态", "---- > > >" + mPlayerStatus);
					
					if (mPlayerStatus != PLAYER_STATUS.PLAYER_NOTHING) {
						Log.i("播放状态", "11111111111111111111" );
						mVVHandler.sendEmptyMessage(T5_STOP);
					}
					TTSPlay(alllist.get(number).getContentURI());
					GlobalConfig.playerobject = alllist.get(number);
					resetHeadView();
					num = number;
				} else {
					getContentNews(alllist.get(number).getContentId(), number);
				}
			}
		}
	}

	private static void TTSPlay(String tts) {
		TTSNews = tts;
		seekBar.setEnabled(false);
		ttsStart();
	}

	private static void getnetwork(int number, Context context) {
		String wifiset = sp.getString(StringConstant.WIFISET, "true"); // 是否开启网络流量提醒
		String wifishow = sp.getString(StringConstant.WIFISHOW, "true");// 是否网络弹出框提醒
		if (wifishow != null && !wifishow.trim().equals("") && wifishow.equals("true")) {
			if (wifiset != null && !wifiset.trim().equals("") && wifiset.equals("true")) {
				// 开启网络播放数据连接提醒
				CommonHelper.checkNetworkStatus(context);// 网络设置获取
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == 1) {
						play(number);
						num = number;
					} else {
						num = number;
						wifidialog.show();
					}
				} else {	// 07/28   没有网络情况下还有可能播放本地文件
					if(alllist.get(number).getLocalurl()!=null){
						play(number);
						num = number;
					}else{
						ToastUtil.show_allways(context, "无网络连接");
					}
				}
			} else {
				// 未开启网络播放数据连接提醒
				num = number;
				play(number);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_lukuangtts:
			if (lukuangTts.isSpeaking()) {
				lukuangTts.stopSpeaking();
				enterCenter();
			} else {
				if (GlobalConfig.playerobject == null) {
				} else {
					if (GlobalConfig.playerobject.getMediaType().equals("AUDIO")) {
						if (mPlayerStatus == PLAYER_STATUS.PLAYER_PLAYING) {
							img_play.setImageResource(R.drawable.wt_play_stop);
							mVVHandler.sendEmptyMessage(T5_POUSE);
							stopCurrentTimer();
						}
					} else if (GlobalConfig.playerobject.getMediaType().equals("RADIO")) {
						if (mPlayerStatus == PLAYER_STATUS.PLAYER_PLAYING) {
							img_play.setImageResource(R.drawable.wt_play_stop);
							mVVHandler.sendEmptyMessage(T5_POUSE);
						}
					} else if (GlobalConfig.playerobject.getMediaType().equals("TTS")) {
						if (TTSPlayerStatus == PLAYER_STATUS.TTS_PLAYING) {
							ttsPouse();
							img_play.setImageResource(R.drawable.wt_play_stop);
							stopCurrentTimer();
						}
					}
				}
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					dialogs = Utils.Dialogph(context, "通讯中", dialogs);
					getLuKuangTTS();// 获取路况数据播报
				} else {
					ToastUtil.show_allways(context, "网络连接失败，请稍后重试");
				}
			}
			break;
		case R.id.tv_cancle:
			rl_voice.setVisibility(View.GONE);
			textSpeakContent.setVisibility(View.GONE);
			break;
		case R.id.lin_voicesearch:
			textSpeakContent.setVisibility(View.GONE);
			rl_voice.setVisibility(View.VISIBLE);
			break;
		case R.id.lin_share:
			Sharedialog.show();
			break;
		case R.id.lin_like:
			// if (GlobalConfig.playerobject.getMediaType().equals("AUDIO")) {
			// }
			if (GlobalConfig.playerobject.getContentFavorite() != null
			&& !GlobalConfig.playerobject.getContentFavorite().equals("")) {
				sendfavorite();
			} else {
				ToastUtil.show_long(context, "本节目信息获取有误，暂时不支持喜欢");
			}
			break;
		case R.id.lin_left:
			if (num - 1 >= 0) {
				Log.e("点击num===============", num + "");
				num = num - 1;
				getnetwork(num, context);
				stopCurrentTimer();
			} else {
				ToastUtil.show_allways(context, "已经是第一条数据了");
			}
			break;
		case R.id.lin_center:
			if (lukuangTts.isSpeaking()) {
				lukuangTts.stopSpeaking();
			}
			enterCenter();
			stopCurrentTimer();
			break;
		case R.id.lin_right:
			if (alllist != null && alllist.size() > 0) {
				if (num + 1 < alllist.size()) {
					Log.e("点击num===============", num + "");
					num = num + 1;
					getnetwork(num, context);
				} else {
					num = 0;
					getnetwork(num, context);
				}
				stopCurrentTimer();
			}
			break;
		case R.id.lin_time:
			startActivity(new Intent(context, TimerPowerOffActivity.class));
			break;
		case R.id.lin_download:
			if (GlobalConfig.playerobject != null) {
				if (GlobalConfig.playerobject.getMediaType().equals("AUDIO")) {
					 ToastUtil.show_allways(context, "已经将该节目添加到下载列表"); 
					// 此处执行将当前播放任务加到数据库的操作
					LanguageSearchInside datals = GlobalConfig.playerobject;
				
					if(datals.getLocalurl()!=null){
						ToastUtil.show_allways(context, "此节目已经保存到本地，请到已下载界面查看");
						return;
					}
					// 对数据进行转换
					List<ContentInfo> datalist = new ArrayList<ContentInfo>();
					ContentInfo mcontent = new ContentInfo();
					mcontent.setAuthor(datals.getContentPersons());
					mcontent.setContentPlay(datals.getContentPlay());
					mcontent.setContentImg(datals.getContentImg());
					mcontent.setContentName(datals.getContentName());
					mcontent.setUserid(Utils.getUserId(context));
					mcontent.setDownloadtype("0");
					// sequname
					if (datals.getSeqInfo() == null || datals.getSeqInfo().getContentName() == null
							|| datals.getSeqInfo().getContentName().equals("")) {
						mcontent.setSequname(datals.getContentName());
					} else {
						mcontent.setSequname(datals.getSeqInfo().getContentName());
					}
					// sequid
					if (datals.getSeqInfo() == null || datals.getSeqInfo().getContentId() == null
							|| datals.getSeqInfo().getContentId().equals("")) {
						mcontent.setSequid(datals.getContentId());
					} else {
						mcontent.setSequid(datals.getSeqInfo().getContentId());
					}
					// sequimg
					if (datals.getSeqInfo() == null || datals.getSeqInfo().getContentImg() == null
							|| datals.getSeqInfo().getContentImg().equals("")) {
						mcontent.setSequimgurl(datals.getContentImg());
					} else {
						mcontent.setSequimgurl(datals.getSeqInfo().getContentImg());
					}
					// sequdesc
					if (datals.getSeqInfo() == null || datals.getSeqInfo().getContentDesc() == null
							|| datals.getSeqInfo().getContentDesc().equals("")) {
						mcontent.setSequdesc(datals.getContentDesc());
					} else {
						mcontent.setSequdesc(datals.getSeqInfo().getContentDesc());
					}
					datalist.add(mcontent);
					// 检查是否重复,如果不重复插入数据库，并且开始下载，重复了提示
					List<FileInfo> filedatalist = FID.queryFileinfoAll(Utils.getUserId(context));
					if (filedatalist.size() != 0) {
						*//**
						 * 此时有下载数据
						 *//*
						boolean isdownload = false;
						for (int j = 0; j < filedatalist.size(); j++) {
							if (filedatalist.get(j).getUrl().equals(mcontent.getContentPlay())) {
								isdownload = true;
								break;
							} else {
								isdownload = false;
							}
						}
						if (isdownload) {
							ToastUtil.show_allways(context, mcontent.getContentName() + "已经存在于下载列表");
						} else {
							FID.insertfileinfo(datalist);
							ToastUtil.show_allways(context, mcontent.getContentName() + "已经插入了下载列表");
							// 未下载列表
							List<FileInfo> fileundownloadlist = FID.queryFileinfo("false", Utils.getUserId(context));
							FileInfo file = null;
							for (int kk = 0; kk < fileundownloadlist.size(); kk++) {
								if (fileundownloadlist.get(kk).getDownloadtype() == 1) {
									DownloadService.workStop(fileundownloadlist.get(kk));
									FID.updatedownloadstatus(fileundownloadlist.get(kk).getUrl(), "2");
									Log.e("测试下载问题", " 暂停下载的单体" + (fileundownloadlist.get(kk).getFileName()));
								}
							}

							for (int k = 0; k < fileundownloadlist.size(); k++) {
								if (fileundownloadlist.get(k).getUrl().equals(mcontent.getContentPlay())) {
									file = fileundownloadlist.get(k);
									FID.updatedownloadstatus(mcontent.getContentPlay(), "1");
									DownloadService.workStart(file);
									Intent p_intent = new Intent("push_down_uncompleted");
									context.sendBroadcast(p_intent);
									Log.e("广播消息", "开始下载");
									break;
								}
							}
						}
					} else {
						*//**
						 * 此时库里没数据
						 *//*
						FID.insertfileinfo(datalist);
						ToastUtil.show_allways(context, mcontent.getContentName() + "已经插入了下载列表");
						// 未下载列表
						List<FileInfo> fileundownloadlist = FID.queryFileinfo("false", Utils.getUserId(context));
						FileInfo file = null;
						for (int k = 0; k < fileundownloadlist.size(); k++) {
							if (fileundownloadlist.get(k).getUrl().equals(mcontent.getContentPlay())) {
								file = fileundownloadlist.get(k);
								FID.updatedownloadstatus(mcontent.getContentPlay(), "1");
								DownloadService.workStart(file);
								Intent p_intent = new Intent("push_down_uncompleted");
								context.sendBroadcast(p_intent);
								break;
							}
						}
					}
				} else {
					ToastUtil.show_allways(context, "您现在播放的是电台节目，不支持下载");
				}
			} else {
				ToastUtil.show_allways(context, "当前播放器播放对象为空");
			}
			break;
		}
	}
	
	*//**
	 * 开启定时服务中的当前播放完后关闭的关闭服务方法  
	 * 点击暂停播放、下一首、上一首以及播放路况信息时都将自动关闭此服务
	 *//*
	private static void stopCurrentTimer(){
		if(PlayerFragment.isCurrentPlay){
			Intent intent = new Intent(context, timeroffservice.class);
			intent.setAction(timeroffservice.TIMER_STOP);
			context.startService(intent);
			PlayerFragment.isCurrentPlay = false;
		}
	}
	
	private static void enterCenter() {
		if (GlobalConfig.playerobject == null) {
		} else {
			if (GlobalConfig.playerobject.getMediaType().equals("AUDIO")) {
				if (GlobalConfig.playerobject.getContentImg() != null) {
					String url;
					if (GlobalConfig.playerobject.getContentImg().startsWith("http")) {
						url = GlobalConfig.playerobject.getContentImg();
					} else {
						url = GlobalConfig.imageurl + GlobalConfig.playerobject.getContentImg();
					}
					imageLoader.DisplayImage(url.replace("\\/", "/"), img_news, false, false, null, null);
				} else {
					img_news.setImageResource(R.drawable.wt_image_playertx_80);
				}
				
				if (mPlayerStatus == PLAYER_STATUS.PLAYER_PLAYING) {
					img_play.setImageResource(R.drawable.wt_play_stop);
					mVVHandler.sendEmptyMessage(T5_POUSE);
					
				} else {
					if (mPlayerStatus != PLAYER_STATUS.PLAYER_NOTHING) {
						mVVHandler.sendEmptyMessage(T5_RESUME);
					} else {
						if (GlobalConfig.playerobject.getContentPlay() != null) {
							musicPlay(GlobalConfig.playerobject.getContentPlay(), "AUDIO");
						} else {
							ToastUtil.show_allways(context, "暂不支持播放");
						}
					}
					img_play.setImageResource(R.drawable.wt_play_play);
				}
			} else if (GlobalConfig.playerobject.getMediaType().equals("RADIO")) {
				if (GlobalConfig.playerobject.getContentImg() != null) {
					String url;
					if (GlobalConfig.playerobject.getContentImg().startsWith("http")) {
						url = GlobalConfig.playerobject.getContentImg();
					} else {
						url = GlobalConfig.imageurl + GlobalConfig.playerobject.getContentImg();
					}
					imageLoader.DisplayImage(url.replace("\\/", "/"), img_news, false, false, null, null);
				} else {
					img_news.setImageResource(R.drawable.wt_image_playertx_80);
				}
				if (mPlayerStatus == PLAYER_STATUS.PLAYER_PLAYING) {
					img_play.setImageResource(R.drawable.wt_play_stop);
					mVVHandler.sendEmptyMessage(T5_POUSE);
				} else if (mPlayerStatus == PLAYER_STATUS.PLAYER_STOP) {
					mVVHandler.sendEmptyMessage(T5_RESUME);
					img_play.setImageResource(R.drawable.wt_play_play);
				} else if (mPlayerStatus != PLAYER_STATUS.PLAYER_NOTHING) {
//					mVVHandler.sendEmptyMessage(T5_RESUME);
					alllist.get(num).setType("2");
					adapter.notifyDataSetChanged();
					if (GlobalConfig.playerobject.getContentPlay() != null) {
						musicPlay(GlobalConfig.playerobject.getContentPlay(), "RADIO");
					} else {
						ToastUtil.show_allways(context, "暂不支持播放");
					}
					img_play.setImageResource(R.drawable.wt_play_play);
				}else {
					if (GlobalConfig.playerobject.getContentPlay() != null) {
						musicPlay(GlobalConfig.playerobject.getContentPlay(), "RADIO");
					} else {
						ToastUtil.show_allways(context, "暂不支持播放");
					}
					img_play.setImageResource(R.drawable.wt_play_play);
				}
			} else if (GlobalConfig.playerobject.getMediaType().equals("TTS")) {
				if (GlobalConfig.playerobject.getContentImg() != null) {
					String url;
					if (GlobalConfig.playerobject.getContentImg().startsWith("http")) {
						url = GlobalConfig.playerobject.getContentImg();
					} else {
						url = GlobalConfig.imageurl + GlobalConfig.playerobject.getContentImg();
					}
					imageLoader.DisplayImage(url.replace("\\/", "/"), img_news, false, false, null, null);
				} else {
					img_news.setImageResource(R.drawable.wt_image_playertx_80);
				}
				if (TTSPlayerStatus == PLAYER_STATUS.TTS_PLAYING) {
					// mVVHandler.sendEmptyMessage(TTS_POUSE);
					ttsPouse();
					img_play.setImageResource(R.drawable.wt_play_stop);
				} else if (TTSPlayerStatus == PLAYER_STATUS.TTS_POUSE) {
					// mVVHandler.sendEmptyMessage(TTS_RESUME);
					ttsResume();
					img_play.setImageResource(R.drawable.wt_play_play);
				} else {
					if (GlobalConfig.playerobject.getContentURI() != null) {
						TTSPlay(GlobalConfig.playerobject.getContentURI());
						img_play.setImageResource(R.drawable.wt_play_play);
					} else {
						ToastUtil.show_allways(context, "暂不支持播放");
					}
				}
			}
		}
	}

	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (sendtype == 1) {
					mlistView.setPullLoadEnable(false);
					RefreshType = 1;
					page = 1;
					firstsend();
				}
			}
		}, 1000);
	}

	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (sendtype == 1) {
					RefreshType = 2;
					firstsend();
				}
			}
		}, 1000);
	}

	@Override
	public void onResume() {
		super.onResume();
		sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		if (first == true) {
			// 从播放历史界面跳转到该界面
			String enter = sp.getString(StringConstant.PLAYHISTORYENTER, "false");//
			String news = sp.getString(StringConstant.PLAYHISTORYENTERNEWS, "");//
			if (enter.equals("true")) {
				SendTextRequest(news, context);
				Editor et = sp.edit();
				et.putString(StringConstant.PLAYHISTORYENTER, "false");
				et.commit();
			} else {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					dialogs = Utils.Dialogph(context, "通讯中", dialogs);
					firstsend();// 搜索第一次数据
				} else {
					ToastUtil.show_allways(context, "网络连接失败，请稍后重试");
				}
			}
			first = false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mIat.cancel();
		mIat.destroy();

		if (Receiver != null) { // 注销广播
			context.unregisterReceiver(Receiver);
			Receiver = null;
		}
		mHandlerThread.quit(); // 关闭线程
	}

	*//**
	 * 广播接收器
	 * 
	 * @author woting11
	 *//*
	class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("pushmusic")) {
				String message = intent.getStringExtra("outmessage");
				if (GlobalConfig.playerobject != null) {
					if (message != null && message.equals("1")) {
						// 暂停音乐播放
						if (GlobalConfig.playerobject.getMediaType().equals("AUDIO")
								|| GlobalConfig.playerobject.getMediaType().equals("RADIO")) {
							if (mPlayerStatus == PLAYER_STATUS.PLAYER_PLAYING) {
								img_play.setImageResource(R.drawable.wt_play_stop);
								mVVHandler.sendEmptyMessage(T5_POUSE);
							}
						} else {
							if (TTSPlayerStatus == PLAYER_STATUS.TTS_PLAYING) {
								img_play.setImageResource(R.drawable.wt_play_stop);
								// mVVHandler.sendEmptyMessage(TTS_POUSE);
								ttsPouse();
							}
						}
					} else {

					}
				}
			}else if(action.equals(timeroffservice.TIMER_UPDATE)){
				String s = intent.getStringExtra("update");
				if(textTime != null){
					textTime.setText(s);
				}
			}else if(action.equals(timeroffservice.TIMER_STOP)){
				if(textTime != null){
					textTime.setText("定时");
				}
			}
		}
	}

	*//**
	 * 播放器模块
	 *//*
	class mVVHandler extends Handler {
		public mVVHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			*//**
			 * 以下是mVV播放状态
			 *//*
			case T5_START:// mVV执行播放
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_NOTHING) {
					synchronized (SYNC_Playing) {
						try {
							SYNC_Playing.wait();
							Log.e("线程等待", "wait player status to idle");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				mVV.setVideoPath(mVideoSource);
				// 显示或者隐藏缓冲提示
				mVV.showCacheInfo(false);
				mVV.start();
				mPlayerStatus = PLAYER_STATUS.PLAYER_PLAYING;// 此时的播放状态==播放中
				Log.e("mplayer播放", "mplayer开始播放" + "播放路径==" + mVideoSource + "  ");
				break;
			case T5_POUSE:// mVV执行暂停
				mVV.pause();
				mPlayerStatus = PLAYER_STATUS.PLAYER_POUSE;// 此时的播放状态==暂停播放中
				Log.e("mplayer播放", "mplayer暂停播放");
				mUIHandler.removeMessages(TIME_UI);
				
				// TODO Auto-generated method stub
				context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						alllist.get(num).setType("0");
						adapter.notifyDataSetChanged();
					}
				});
				
				break;
			case T5_STOP:// mVV执行停止
				mVV.stopPlayback();
				mPlayerStatus = PLAYER_STATUS.PLAYER_STOP;// 此时的播放状态==停止播放
				Log.i("播放状态", "---- > > >mplayer停止播放");
				Log.e("mplayer播放", "mplayer停止播放");
				break;
			case T5_RESUME:// mVV执行继续播放
				mVV.resume();
				mPlayerStatus = PLAYER_STATUS.PLAYER_PLAYING;// 此时的播放状态==播放中
				Log.e("mplayer播放", "mplayer继续播放");
				mUIHandler.sendEmptyMessage(TIME_UI);
				
				// TODO Auto-generated method stub
				context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						alllist.get(num).setType("2");
						adapter.notifyDataSetChanged();
					}
				});
				
				break;
				*//**
				 * 以下是TTS播放状态
				 *//*
				// case TTS_START://TTS执行播放
				//
				// break;
				// case TTS_POUSE://TTS执行暂停
				//
				// break;
				// case TTS_STOP://TTS执行停止
				//
				// break;
				// case TTS_RESUME://TTS执行继续播放
				//
				// break;
			}
		}
	};

	private static String mVideoSource;

	*//**
	 * 缓冲监听
	 *//*
	@Override
	public boolean onInfo(int what, int extra) {
		switch (what) {
		case BVideoView.MEDIA_INFO_BUFFERING_START:		// 开始缓冲
			break;
		case BVideoView.MEDIA_INFO_BUFFERING_END:		// 结束缓冲
			break;
		}
		return true;
	}

	public static void ttsStop() {
		Log.e("TTS播放", "TTS停止播放");
		mTts.stopSpeaking();
		TTSPlayerStatus = PLAYER_STATUS.TTS_STOP;// 此时的播放状态==TTS停止播放
	}

	public static void ttsPouse() {
		Log.e("TTS播放", "TTS暂停播放");
		mTts.pauseSpeaking();
		mHandler.removeMessages(TIME_UI);
		TTSPlayerStatus = PLAYER_STATUS.TTS_POUSE;// 此时的播放状态==TTS暂停播放
		
		// TODO Auto-generated method stub
		alllist.get(num).setType("0");
		adapter.notifyDataSetChanged();
	}

	public static void ttsResume() {
		Log.e("TTS播放", "TTS继续播放");
		mTts.resumeSpeaking();
		mUIHandler.sendEmptyMessage(TIME_UI);
		TTSPlayerStatus = PLAYER_STATUS.TTS_PLAYING;// 此时的播放状态==TTS播放中
		
		// TODO Auto-generated method stub
		alllist.get(num).setType("2");
		adapter.notifyDataSetChanged();
	}

	public static void ttsStart() {
		// if (mPlayerStatus != PLAYER_STATUS.PLAYER_NOTHING) {
		// synchronized (SYNC_Playing) {
		// try {
		// SYNC_Playing.wait();
		// Log.e("线程等待", "wait player status to idle");
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// }

		SynthesizerListener mtslistener = new SynthesizerListener() {

			@Override
			public void onSpeakResumed() {
				Log.e("mts==============", "onSpeakResumed");
			}

			@Override
			public void onSpeakProgress(int arg0, int arg1, int arg2) {
				Log.e("mts==播放进度==============", arg0 + "");
				Log.e("mts==播放进度文件中开始位置==============", arg1 + "");
				Log.e("mts==播放进度文件中结束未知==============", arg2 + "");
				TTSPlayerStatus = PLAYER_STATUS.TTS_PLAYING;// 此时的播放状态==TTS播放中
			}

			@Override
			public void onSpeakPaused() {
				Log.e("mts==============", "onSpeakPaused");
			}

			@Override
			public void onSpeakBegin() {
				Log.e("mts==============", "onSpeakBegin");
			}

			@Override
			public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
				Log.e("mts==============", "onEvent");
				// Log.e("mts==onEvent===arg0==============", arg0+"");
				// Log.e("mts==onEvent===arg1==============", arg1+"");
				// Log.e("mts==onEvent===arg2==============", arg2+"");
			}

			@Override
			public void onCompleted(SpeechError arg0) {
				synchronized (SYNC_Playing) {
					SYNC_Playing.notify();
				}
				mPlayerStatus = PLAYER_STATUS.PLAYER_NOTHING;
				TTSPlayerStatus = PLAYER_STATUS.TTS_STOP;// 此时的播放状态==TTS停止播放
				// mUIHandler.sendEmptyMessage(NEXT_PLAY);
				Log.e("TTS播放", "TTS播放完成");
				playNext();
			}

			@Override
			public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
				Log.e("mts==缓冲进度==============", arg0 + "");
				Log.e("mts==缓冲进度文件中开始位置==============", arg1 + "");
				Log.e("mts==缓冲进度文件中结束位置==============", arg2 + "");
			}
		};

		mTts.startSpeaking(TTSNews, mtslistener);
		TTS_SpeakProgress = 0;
		mUIHandler.sendEmptyMessage(TIME_UI);
	}

	public static void playNext() {
		if (num + 1 < alllist.size()) {
			// 此时自动播放下一首
			int number = num + 1;
			getnetwork(number, context);
		} else {
			// 全部播放完毕了
			int number = 0;
			getnetwork(number, context);
		}
	}
	
	public static int timerService;		// 当前节目播放剩余时间长度

	static Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// case NEXT_PLAY://播放下一首
			//
			// break;
			case TIME_UI: // 更新进度及时间
				if (GlobalConfig.playerobject != null && GlobalConfig.playerobject.getMediaType() != null
				&& GlobalConfig.playerobject.getMediaType().trim().length() > 0
				&& GlobalConfig.playerobject.getMediaType().equals("AUDIO")) {
					int currPosition = mVV.getCurrentPosition();
					int duration = mVV.getDuration();
					updateTextViewWithTimeFormat(time_start, currPosition);
					updateTextViewWithTimeFormat(time_end, duration);
					seekBar.setMax(duration);
					timerService = duration - currPosition;
					
					if (mVV.isPlaying()) {
						seekBar.setProgress(currPosition);
					}
					// Log.e("currPosition", currPosition+"");
					// Log.e("duration", duration+"");
					if (duration > 0 && currPosition == (duration - 1)) {
						playNext();
						Log.e("播放器播放", "开始播放下一首");
					}
				} else if (GlobalConfig.playerobject != null && GlobalConfig.playerobject.getMediaType() != null
						&& GlobalConfig.playerobject.getMediaType().trim().length() > 0
						&& GlobalConfig.playerobject.getMediaType().equals("RADIO")) {
					int _currPosition = TimeUtil.getTime(System.currentTimeMillis());
					int _duration = 24 * 60 * 60;
					updateTextViewWithTimeFormat(time_start, _currPosition);
					updateTextViewWithTimeFormat(time_end, _duration);
					seekBar.setMax(_duration);
					seekBar.setProgress(_currPosition);
				} else if (GlobalConfig.playerobject != null && GlobalConfig.playerobject.getMediaType() != null
						&& GlobalConfig.playerobject.getMediaType().trim().length() > 0
						&& GlobalConfig.playerobject.getMediaType().equals("TTS")) {

					int _currPosition = TTS_SpeakProgress;
					int _duration = (int) (TTSNews.length() / 4.15);
					updateTextViewWithTimeFormat(time_start, _currPosition);
					updateTextViewWithTimeFormat(time_end, _duration);
					seekBar.setMax(_duration);
					seekBar.setProgress(_currPosition);
					TTS_SpeakProgress++;
				}
				mUIHandler.sendEmptyMessageDelayed(TIME_UI, 1000);
				break;
			case VOICE_UI:
				rl_voice.setVisibility(View.GONE);
				textSpeakContent.setVisibility(View.GONE);
				tv_speak_status.setText("请按住讲话");
				break;
			}
		}
	};

	*//**
	 * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
	 *//*
	@Override
	public void onPlayingBufferCache(int percent) {
		Log.e("缓冲百分比===", percent + "");
	}

	*//**
	 * 播放出错
	 *//*
	@Override
	public boolean onError(int what, int extra) {
		// 此时跟播放完成的状态一样
		Log.e("mplayer播放", "mplayer播放出错");
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_NOTHING;
		// mUIHandler.sendEmptyMessage(TIME_UI);
		return true;
	}

	*//**
	 * 播放完成
	 *//*
	@Override
	public void onCompletion() {
		Log.e("mplayer播放", "mplayer播放完成");
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		// 空闲状态
		mPlayerStatus = PLAYER_STATUS.PLAYER_NOTHING;
		// mUIHandler.sendEmptyMessage(TIME_UI);
		// 播放下一首任务
		// mVVHandler.sendEmptyMessage(NEXT_PLAY);
	}

	*//**
	 * 准备播放就绪
	 *//*
	@Override
	public void onPrepared() {
		Log.e("准备播放完毕", "onPrepared");
		// 设置mPlayerStatus此时状态
		// mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
		// 发送消息UI事件更新CURR的位置
		// mVVHandler.sendEmptyMessage(TIME_UI);
	}

	@Override
	public void onTotalCacheUpdate(long pos) {
		Log.e("对总缓存更新", "Totally cached " + pos);
	}
	
	private static void updateTextViewWithTimeFormat(TextView view, int second) {
		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;
		String strTemp = null;
		// if (0 != hh) {
		strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
		// } else {
		// strTemp = String.format("%02d:%02d", mm, ss);
		// }
		view.setText(strTemp);
		// Log.e("展示数据", strTemp+"");
		// dbdao.updatefileinfo(GlobalConfig.playerobject.getContentPlay(),"playerintime",
		// String.valueOf(mVV.getCurrentPosition()));
	}

	*//**
	 * 开始播放
	 *//*
	private static void musicPlay(String url, String mediaType) {
		Uri uriPath = Uri.parse(url);
		if (null != uriPath) {
			String scheme = uriPath.getScheme();
			if (null != scheme) {
				mVideoSource = uriPath.toString();
			} else {
				mVideoSource = uriPath.getPath();
			}
		}
		WtAudioPlay audioplay=VlcPlayer.getInstance(context);
		Log.e("vlc播放的地址为==========>", url+"");
		audioplay.play(url);
		context.startActivity(new Intent(context,bdplayer.class));
		
		if (mPlayerStatus != PLAYER_STATUS.PLAYER_NOTHING) {
			mVVHandler.sendEmptyMessage(T5_STOP);
		}
		// 开始播放
		mVVHandler.sendEmptyMessage(T5_START);
		if (mediaType != null && mediaType.trim().length() > 0 && mediaType.equals("AUDIO")) {
			seekBar.setEnabled(true);
			mUIHandler.sendEmptyMessage(TIME_UI);
		} else {
			seekBar.setEnabled(false);
			mUIHandler.sendEmptyMessage(TIME_UI);
		}
	}

	////////////////
	// tts播放模块//
	///////////////
	private void setParamTTS() {
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置在线合成发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, "vixf");
		// 设置合成语速
		// mTts.setParameter(SpeechConstant.SPEED, "40");
		// 设置合成音调
		// mTts.setParameter(SpeechConstant.PITCH,"50");
		// 设置合成音量
		mTts.setParameter(SpeechConstant.VOLUME, "50");
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
		// mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
		// Environment.getExternalStorageDirectory()+"/WTFM/Record/tts.pcm");
		// mTts.startSpeaking("<body>/n<p>/n文本测试，<a href='#'>明月出天山</a>，"
		// + "苍茫云海间，长风几万里，吹度玉门关。汉下白登道，胡窥青海湾，古来征战地，"
		// + "不见有人还。戍客望边驿，思归多苦颜，高楼当此夜，叹息未应闲。/n</p>/n</body>", null );

	}

	////////////
	// 分享模块//
	////////////
	private void ShareDialog() {
		final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sharedialog, null);
		HorizontalListView mgallery = (HorizontalListView) dialog.findViewById(R.id.share_gallery);
		TextView tv_cancle = (TextView) dialog.findViewById(R.id.tv_cancle);
		Sharedialog = new Dialog(context, R.style.MyDialog);
		// 从底部上升到一个位置
		Sharedialog.setContentView(dialog);
		Window window = Sharedialog.getWindow();
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenw = dm.widthPixels;
		ViewGroup.LayoutParams params = dialog.getLayoutParams();
		params.width = (int) screenw;
		dialog.setLayoutParams(params);
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.sharestyle);
		Sharedialog.setCanceledOnTouchOutside(true);
		Sharedialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		 Sharedialog.show(); 
		dialog1 = Utils.Dialogphnoshow(context, "通讯中", dialog1);
		Config.dialog = dialog1;
		final List<ShareMode> mylist = Utils.getShareModelList();
		ImageAdapter shareadapter = new ImageAdapter(context, mylist);
		mgallery.setAdapter(shareadapter);
		mgallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SHARE_MEDIA Platform = mylist.get(position).getSharePlatform();
				CallShare(Platform);
				Sharedialog.dismiss();
			}
		});
		tv_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Sharedialog.isShowing()) {
					Sharedialog.dismiss();
				}
			}
		});
	}

	protected void CallShare(SHARE_MEDIA Platform) {
		String sharename;
		String shareDesc;
		String shareContentImg;
		String shareurl;
		if (GlobalConfig.playerobject != null) {
			if (GlobalConfig.playerobject.getContentName() != null
					&& !GlobalConfig.playerobject.getContentName().equals("")) {
				sharename = GlobalConfig.playerobject.getContentName();
			} else {
				sharename = "我听我享听";
			}
			if (GlobalConfig.playerobject.getContentDesc() != null
					&& !GlobalConfig.playerobject.getContentDesc().equals("")) {
				shareDesc = GlobalConfig.playerobject.getContentDesc();
			} else {
				shareDesc = "暂无本节目介绍";
			}
			if (GlobalConfig.playerobject.getContentImg() != null
					&& !GlobalConfig.playerobject.getContentImg().equals("")) {
				shareContentImg = GlobalConfig.playerobject.getContentImg();
				image = new UMImage(context, shareContentImg);
			} else {
				shareContentImg = "http://182.92.175.134/img/logo-web.png";
				image = new UMImage(context, shareContentImg);
			}
			if (GlobalConfig.playerobject.getContentShareURL() != null
					&& !GlobalConfig.playerobject.getContentShareURL().equals("")) {
				shareurl = GlobalConfig.playerobject.getContentShareURL();
			} else {
				shareurl = "http://www.wotingfm.com/";
			}
			new ShareAction(context).setPlatform(Platform).withMedia(image).withText(shareDesc).withTitle(sharename)
			.withTargetUrl(shareurl).share();
		} else {
			ToastUtil.show_short(context, "没有数据");
		}
	}

	// private UMShareListener umShareListener = new UMShareListener() {
	// @Override
	// public void onResult(SHARE_MEDIA platform) {
	// Log.d("plat", "platform" + platform);
	// Toast.makeText(context, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
	// if(Sharedialog.isShowing()){
	// Sharedialog.dismiss();
	// }
	// }
	// @Override
	// public void onError(SHARE_MEDIA platform, Throwable t) {
	// Toast.makeText(context, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
	// if(Sharedialog.isShowing()){
	// Sharedialog.dismiss();
	// }
	// }
	// @Override
	// public void onCancel(SHARE_MEDIA platform) {
	// ToastUtil.show_allways(context,"用户退出认证");
	// if(Sharedialog.isShowing()){
	// Sharedialog.dismiss();
	// }
	// }
	// };

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
	}

	////////////////////
	// 语音转文字-讯飞//
	///////////////////
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// 搜索引擎 云搜索
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
		// 设置语言
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置语言区域
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA, "1");
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "5000");
	}

	// 听写监听器
	private RecognizerListener mRecoListener = new RecognizerListener() {
		// 听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
		// 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
		// 关于解析Json的代码可参见MscDemo中JsonParser类；
		// isLast等于true时会话结束。
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
			Log.e("Result:", results.getResultString());
		}

		// 会话发生错误回调接口
		public void onError(SpeechError error) {
			error.getPlainDescription(true); // 获取错误码描述
		}

		// 开始录音
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			// Toast.makeText(context, "可以开始说话", 1).show();
		}

		// 音量值0~30
		public void onVolumeChanged(int volume) {
		}

		// 结束录音
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			// Toast.makeText(context, "结束说话", 1).show();
		}

		// 扩展用接口
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			// Toast.makeText(TestDemo.this, "当前正在说话，音量大小：" + arg0+"",
			// 1).show();
			// Log.d(TAG, "返回音频数据："+arg1.length);
		}
	};

	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());
		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mIatResults.put(sn, text);
		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		String str = resultBuffer.toString();
		 tv_news.setText(str); 
		if (str != null && !str.equals("")) {
			str = str.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
			tv_speak_status.setText("正在搜索: ");
			textSpeakContent.setVisibility(View.VISIBLE);
			textSpeakContent.setText(str);
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				if (sn.equals("1")) {
					searchByVoicesend(str);
				}
			} else {
				ToastUtil.show_short(context, "网络失败，请检查网络");
			}
		} else {
			ToastUtil.show_short(context, "您输入的对话信息为空");
		}
		
		 * tv_news.setVisibility(View.VISIBLE); new Handler().postDelayed(new
		 * Runnable() { public void run() { tv_news.setVisibility(View.GONE); }
		 * }, 5000);
		 
	}

	///////////////
	// wifi弹出框//
	//////////////
	private void WifiDialog() {
		final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_wifi_set, null);
		TextView tv_over = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_first = (TextView) dialog1.findViewById(R.id.tv_first);
		TextView tv_all = (TextView) dialog1.findViewById(R.id.tv_all);
		wifidialog = new Dialog(context, R.style.MyDialog);
		wifidialog.setContentView(dialog1);
		wifidialog.setCanceledOnTouchOutside(true);
		wifidialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_over.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wifidialog.dismiss();
			}
		});
		tv_first.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play(num);
				wifidialog.dismiss();
			}
		});
		tv_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play(num);
				Editor et = sp.edit();
				et.putString(StringConstant.WIFISHOW, "false");
				et.commit();
				wifidialog.dismiss();
			}
		});
	}

	// 语音搜索请求
	private void searchByVoicesend(String str) {
		sendtype = 2;
		// 发送数据
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			private String ReturnType;
			private String MainList;

			@Override
			public void onResponse(JSONObject arg0) {
				Log.e("语音搜索返回数据", arg0 + "");
				try {
					ReturnType = arg0.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					MainList = arg0.getString("ResultList");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType.equals("1001")) {
					// LanguageSearch lists = new LanguageSearch();
					try {
						Gson gson = new Gson();
						LanguageSearch lists = gson.fromJson(MainList, new TypeToken<LanguageSearch>() {}.getType());
						List<LanguageSearchInside> list = lists.getList();
						if (list != null && list.size() != 0) {
							num = 0;
							alllist.clear();
							alllist.addAll(list);
							lin_tuijian.setVisibility(View.VISIBLE);
							adapter = new PlayerListAdapter(context, alllist);
							mlistView.setAdapter(adapter);
							setitemlistener();
							getnetwork(0, context);
							mlistView.setPullRefreshEnable(false);
							mlistView.setPullLoadEnable(false);
							mlistView.stopRefresh();
							mlistView.stopLoadMore();
							mlistView.setRefreshTime(new Date().toLocaleString());
						}
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
						Log.e("语音搜索异常信息", e.toString());
					}

				} else if (ReturnType.equals("1011")) {
					ToastUtil.show_short(context, "没有查询内容");
				} else {
					ToastUtil.show_short(context, "没有新的数据");
				}
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (voice_type == 2) {
							mUIHandler.sendEmptyMessage(VOICE_UI);
						}
					}
				}, 5000);
			}
		};

		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				Log.e("语音搜索返回异常", arg0 + "");
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (voice_type == 2) {
							mUIHandler.sendEmptyMessage(VOICE_UI);
						}
					}
				}, 5000);
			}
		};

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("SessionId", Utils.getSessionId(context));
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			jsonObject.put("UserId", Utils.getUserId(context));
			jsonObject.put("SearchStr", str);
			jsonObject.put("PCDType", "1");
			jsonObject.put("PageType", "0");
			// CatalogType
			// CatalogId//此处这俩参数都不需要
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, GlobalConfig.searchvoiceUrl,
					jsonObject, listener, errorListener) {

				@Override
				protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
					try {
						String jsonString = new String(response.data, "UTF-8");
						if (jsonString != null && jsonString.startsWith("\ufeff")) {
							jsonString = jsonString.substring(1);
						}
						JSONObject jsonObject = new JSONObject(jsonString);
						return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
					} catch (UnsupportedEncodingException e) {
						return Response.error(new ParseError(e));
					} catch (Exception je) {
						return Response.error(new ParseError(je));
					}
				}
			};
			request.setRetryPolicy(new DefaultRetryPolicy(3 * 1000, 1, 1.0f));
			requestQueue.add(request);
			requestQueue.start();
			Log.i("语音搜索路径及信息", GlobalConfig.searchvoiceUrl + jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	*//**
	 * 第一次进入该界面时候的数据
	 *//*
	private void firstsend() {
		sendtype = 1;
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			private String ReturnType;
			private String MainList;

			@Override
			public void onResponse(JSONObject arg0) {
				if (dialogs != null) {
					dialogs.dismiss();
				}
				Log.e("第一次数据返回数据", arg0 + "");
				try {
					ReturnType = arg0.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType.equals("1001")) {
					page++;
					try {
						String List = arg0.getString("ResultList");
						JSONTokener jsonParser = new JSONTokener(List);
						JSONObject arg1 = (JSONObject) jsonParser.nextValue();
						MainList = arg1.getString("List");
						Gson gson = new Gson();
						ArrayList<LanguageSearchInside> list = gson.fromJson(MainList,
								new TypeToken<List<LanguageSearchInside>>() {
						}.getType());
						if (RefreshType == 0) {
							// 得到数据库里边的第一条数据
							LanguageSearchInside flist = getdaolist(context);
							// 第一次进入该界面获取数据
							if (list != null && list.size() > 0) {
								// 此时有返回数据
								if (flist != null) {
									// 此时数据库里边的数据为空
									// list.add(flist);
									num = -1;
									setData(flist, list);
								} else {
									// 此时数据库里边的数据为空
									if (list.get(0) != null) {
										num = 0;
										setDataForNoList(list);
									} else {
										num = -2;
									}
								}
							} else {
								if (flist != null) {
									list.add(flist);
									num = -1;
									setData(flist, list);
								} else {
									// 此时没有任何数据
									num = -2;
									mlistView.setPullRefreshEnable(true);
									mlistView.setPullLoadEnable(false);
									mlistView.stopRefresh();
								}
							}
						} else if (RefreshType == 1) {
							// 下拉刷新--------暂未使用，注意：不要删除该段代码
							alllist.clear();
							alllist.addAll(list);
							if (GlobalConfig.playerobject != null && alllist != null) {
								for (int i = 0; i < alllist.size(); i++) {
									if (alllist.get(i).getContentPlay()
											.equals(GlobalConfig.playerobject.getContentPlay())) {
										alllist.get(i).setType("2");
										num = i;
									}
								}
							}
							lin_tuijian.setVisibility(View.VISIBLE);
							adapter = new PlayerListAdapter(context, alllist);
							mlistView.setAdapter(adapter);
							setitemlistener();
							mlistView.setPullRefreshEnable(false);
							mlistView.setPullLoadEnable(true);
							mlistView.stopRefresh();
						} else {
							// 加载更多
							mlistView.stopLoadMore();
							alllist.addAll(list);
							adapter.notifyDataSetChanged();
							setitemlistener();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						if (dialogs != null) {
							dialogs.dismiss();
						}
						alllist.clear();
						lin_tuijian.setVisibility(View.GONE);
						adapter = new PlayerListAdapter(context, alllist);
						mlistView.setAdapter(adapter);
						mlistView.setPullRefreshEnable(true);
						mlistView.setPullLoadEnable(false);
						mlistView.stopRefresh();
						mlistView.stopLoadMore();
					}
				} else {
					if (dialogs != null) {
						dialogs.dismiss();
					}
					alllist.clear();
					lin_tuijian.setVisibility(View.GONE);
					adapter = new PlayerListAdapter(context, alllist);
					mlistView.setAdapter(adapter);
					mlistView.setPullRefreshEnable(true);
					mlistView.setPullLoadEnable(false);
					mlistView.stopRefresh();
					mlistView.stopLoadMore();
				}
				resetHeadView();
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				if (dialogs != null) {
					dialogs.dismiss();
				}
				alllist.clear();
				lin_tuijian.setVisibility(View.VISIBLE);
				adapter = new PlayerListAdapter(context, alllist);
				mlistView.setAdapter(adapter);
				mlistView.setPullLoadEnable(false);
				mlistView.stopRefresh();
				mlistView.stopLoadMore();
				mlistView.setRefreshTime(new Date().toLocaleString());
				Log.e("第一次数据返回异常", arg0 + "");
			}
		};
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("PCDType", "1");
			jsonObject.put("SessionId", Utils.getSessionId(context));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(context);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			jsonObject.put("UserId", Utils.getUserId(context));
			jsonObject.put("PageType", "0");
			jsonObject.put("Page", String.valueOf(page));
			jsonObject.put("PageSize", "10");
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, GlobalConfig.mainPageUrl, jsonObject,
					listener, errorListener);
			request.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 2, 1.0f));
			requestQueue.add(request);
			requestQueue.start();
			Log.e("获取第一次数据路径及信息", GlobalConfig.mainPageUrl + jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void setData(LanguageSearchInside flist, ArrayList<LanguageSearchInside> list) {
		// 如果数据库里边的数据不是空的，在headview设置该数据
		GlobalConfig.playerobject = flist;
		resetHeadView();
		if (flist.getContentName() != null) {
			tv_name.setText(flist.getContentName());
		} else {
			tv_name.setText("未知数据");
		}
		// if (flist.getPlayerAllTime() == null||
		// flist.getPlayerAllTime().trim().equals("")) {
		time_start.setText("00:00:00");
		time_end.setText("00:00:00");
		// } else {
		// time_start.setText("00:00:00");
		// String ss = format.format(Long.parseLong(flist.getPlayerAllTime()));
		// time_end.setText(ss);
		// }
		if (flist.getContentImg() != null) {
			String url;
			if (flist.getContentImg().startsWith("http")) {
				url = flist.getContentImg();
			} else {
				url = GlobalConfig.imageurl + flist.getContentImg();
			}
			imageLoader.DisplayImage(url.replace("\\/", "/"), img_news, false, false, null, null);
		} else {
			img_news.setImageResource(R.drawable.wt_image_playertx_80);
		}
		alllist.clear();
		alllist.addAll(list);
		if (GlobalConfig.playerobject != null && alllist != null) {
			for (int i = 0; i < alllist.size(); i++) {
				// alllist.get(i).getContentPlay() == null
				//if (alllist.get(i).getContentPlay() != null && alllist.get(i).getContentPlay().equals(GlobalConfig.playerobject.getContentPlay())) {
				String s=alllist.get(i).getContentPlay();
				if(s!=null){
					if (s.equals(GlobalConfig.playerobject.getContentPlay())) {
						alllist.get(i).setType("2");
						num = i;
					}
				}
			}
		}
		lin_tuijian.setVisibility(View.VISIBLE);
		adapter = new PlayerListAdapter(context, alllist);
		mlistView.setAdapter(adapter);
		setitemlistener();
		mlistView.setPullRefreshEnable(false);
		mlistView.setPullLoadEnable(true);
		mlistView.stopRefresh();
	}

	protected void setDataForNoList(ArrayList<LanguageSearchInside> list) {
		GlobalConfig.playerobject = list.get(0);
		resetHeadView();
		if (list.get(0).getContentName() != null && list.get(0).getContentName().trim().length() > 0) {
			tv_name.setText(list.get(0).getContentName());
		} else {
			tv_name.setText("未知数据");
		}
		time_start.setText("00:00:00");
		time_end.setText("00:00:00");
		if (list.get(0).getContentImg() != null) {
			String url;
			if (list.get(0).getContentImg().startsWith("http")) {
				url = list.get(0).getContentImg();
			} else {
				url = GlobalConfig.imageurl + list.get(0).getContentImg();
			}
			imageLoader.DisplayImage(url.replace("\\/", "/"), img_news, false, false, null, null);
		} else {
			img_news.setImageResource(R.drawable.wt_image_playertx_80);
		}
		alllist.clear();
		alllist.addAll(list);
		alllist.get(0).setType("2");
		num = 0;
		lin_tuijian.setVisibility(View.VISIBLE);
		adapter = new PlayerListAdapter(context, alllist);
		mlistView.setAdapter(adapter);
		setitemlistener();
		mlistView.setPullRefreshEnable(false);
		mlistView.setPullLoadEnable(true);
		mlistView.stopRefresh();
	}

	protected static void resetHeadView() {
		if (GlobalConfig.playerobject != null) {

			if (GlobalConfig.playerobject.getMediaType().equals("Radio")) {
				// 不支持分享
				ToastUtil.show_allways(context, "电台节目目前不支持分享");
				return;
				// 设置灰色界面
			} else {
				// 支持分享
				// 设置回界面
			}
			if (GlobalConfig.playerobject.getContentFavorite() != null && !GlobalConfig.playerobject.equals("")) {
				if (GlobalConfig.playerobject.getContentFavorite().equals("1")) {
					tv_like.setText("已喜欢");
					img_like.setImageResource(R.drawable.wt_dianzan_select);
				} else {
					tv_like.setText("喜欢");
					img_like.setImageResource(R.drawable.wt_dianzan_nomal);
				}

			} else {
				tv_like.setText("喜欢");
				img_like.setImageResource(R.drawable.wt_dianzan_nomal);
				// ToastUtil.show_allways(context, "本节目不支持喜欢");
			}
		} else {
			// ToastUtil.show_allways(context,"当前播放对象异常");
		}
	}

	// 文字请求
	public static void SendTextRequest(String contname, final Context mContext) {
		if (lukuangTts != null) {
			if (lukuangTts.isSpeaking()) {
				lukuangTts.stopSpeaking();
			}
		}
		 dialogs = Utils.Dialogph(mContext, "通讯中", dialogs); 
		final LanguageSearchInside flist = getdaolist(mContext);// 得到数据库里边的第一条数据
		if (flist != null) {
			// 如果数据库里边的数据不是空的，在headview设置该数据
			if (alllist != null && alllist.size() > 0) {
				alllist.clear();
				alllist.add(flist);
			} else {
				alllist = new ArrayList<LanguageSearchInside>();
				alllist.add(flist);
			}
			num = 0;
			if (flist.getContentName() != null) {
				tv_name.setText(flist.getContentName());
			} else {
				tv_name.setText("wotingkeji");
			}
			adapter = new PlayerListAdapter(context, alllist);
			mlistView.setAdapter(adapter);
			setitemlistener();
			// play(0);
			stopCurrentTimer();
			getnetwork(0, context);
		}
		// 发送数据
		sendtype = 2;
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			private String ReturnType;
			private String MainList;

			@Override
			public void onResponse(JSONObject arg0) {
				Log.e("文字搜索返回数据", arg0 + "");
				if (dialogs != null) {
					dialogs.dismiss();
				}
				try {
					ReturnType = arg0.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					MainList = arg0.getString("ResultList");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType.equals("1001")) {
					// LanguageSearch lists = new LanguageSearch();
					try {
						Gson gson = new Gson();
						LanguageSearch lists = gson.fromJson(MainList, new TypeToken<LanguageSearch>() {}.getType());
						List<LanguageSearchInside> list = lists.getList();
						if (list != null && list.size() != 0) {
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).getContentPlay() != null
										&& list.get(i).getContentPlay().equals(flist.getContentPlay())) {
									list.remove(i);
								}
							}
							num = 0;
							alllist.clear();
							alllist.add(flist);
							alllist.addAll(list);
							lin_tuijian.setVisibility(View.VISIBLE);
							adapter = new PlayerListAdapter(context, alllist);
							mlistView.setAdapter(adapter);
							setitemlistener();
							mlistView.setPullRefreshEnable(false);
							mlistView.setPullLoadEnable(false);
							mlistView.stopRefresh();
							mlistView.stopLoadMore();
							mlistView.setRefreshTime(new Date().toLocaleString());
						}
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				} else if (ReturnType.equals("1011")) {
					ToastUtil.show_short(context, "没有查询内容");
				} else {
					ToastUtil.show_short(context, "没有新的数据");
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				if (dialogs != null) {
					dialogs.dismiss();
				}
				Log.e("文字搜索返回异常", arg0 + "");
			}
		};
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("SessionId", Utils.getSessionId(context));
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(context);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			jsonObject.put("UserId", Utils.getUserId(context));
			jsonObject.put("SearchStr", contname);
			jsonObject.put("PCDType", "1");
			jsonObject.put("PageType", "0");
			// CatalogType
			// CatalogId//此处这俩参数都不需要
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, GlobalConfig.getSearchByText,
					jsonObject, listener, errorListener) {
				@Override
				protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
					try {
						String jsonString = new String(response.data, "UTF-8");
						if (jsonString != null && jsonString.startsWith("\ufeff")) {
							jsonString = jsonString.substring(1);
						}
						JSONObject jsonObject = new JSONObject(jsonString);
						return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
					} catch (UnsupportedEncodingException e) {
						return Response.error(new ParseError(e));
					} catch (Exception je) {
						return Response.error(new ParseError(je));
					}
				}
			};
			request.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));
			requestQueue.add(request);
			requestQueue.start();
			Log.e("文字搜索路径及信息", GlobalConfig.getSearchByText + jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static void getContentNews(String id, final int number) {
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			private String ReturnType;
			private String MainList;

			@Override
			public void onResponse(JSONObject arg0) {
				Log.e("获取getContenturl返回数据", arg0 + "");
				try {
					ReturnType = arg0.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					MainList = arg0.getString("ResultInfo");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType.equals("1001")) {
					try {
						Gson gson = new Gson();
						LanguageSearchInside lists = gson.fromJson(MainList, new TypeToken<LanguageSearchInside>() {
						}.getType());
						String ContentURI = lists.getContentURI();
						Log.e("ContentURI", ContentURI + "");
						if (ContentURI != null && ContentURI.trim().length() > 0) {
							img_play.setImageResource(R.drawable.wt_play_play);
							if (alllist.get(number).getContentName() != null) {
								tv_name.setText(alllist.get(number).getContentName());
							} else {
								tv_name.setText("wotingkeji");
							}
							if (alllist.get(number).getContentImg() != null) {
								String url;
								if (alllist.get(number).getContentImg().startsWith("http")) {
									url = alllist.get(number).getContentImg();
								} else {
									url = GlobalConfig.imageurl + alllist.get(number).getContentImg();
								}
								imageLoader.DisplayImage(url.replace("\\/", "/"), img_news, false, false, null, null);
							} else {
								img_news.setImageResource(R.drawable.wt_image_playertx_80);
							}
							for (int i = 0; i < alllist.size(); i++) {
								alllist.get(i).setType("1");
							}
							
							alllist.get(number).setType("2");
							adapter.notifyDataSetChanged();
							if (mPlayerStatus != PLAYER_STATUS.PLAYER_NOTHING) {
								mVVHandler.sendEmptyMessage(T5_STOP);
							}
							TTSPlay(ContentURI);
							GlobalConfig.playerobject = alllist.get(number);
							resetHeadView();// 页面的对象改变，根据对象重新设置属性
							num = number;
						}
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				Log.e("获取getContenturl返回异常", arg0 + "");
			}
		};
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("SessionId", Utils.getSessionId(context));
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(context);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			jsonObject.put("UserId", Utils.getUserId(context));
			jsonObject.put("MediaType", "TTS");
			jsonObject.put("ContentId", id);
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, GlobalConfig.getContentById,
					jsonObject, listener, errorListener) {
				@Override
				protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
					try {
						String jsonString = new String(response.data, "UTF-8");
						if (jsonString != null && jsonString.startsWith("\ufeff")) {
							jsonString = jsonString.substring(1);
						}
						JSONObject jsonObject = new JSONObject(jsonString);
						return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
					} catch (UnsupportedEncodingException e) {
						return Response.error(new ParseError(e));
					} catch (Exception je) {
						return Response.error(new ParseError(je));
					}
				}
			};
			request.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));
			requestQueue.add(request);
			requestQueue.start();
			Log.e("获取getContenturl", GlobalConfig.getContentById + jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void sendfavorite() {
		dialogs = Utils.Dialogph(context, "通讯中", dialogs);
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			private String ReturnType;
			private String Message;

			@Override
			public void onResponse(JSONObject arg0) {
				if (dialogs != null) {
					dialogs.dismiss();
				}
				Log.e("返回数据", arg0 + "");
				try {
					ReturnType = arg0.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					Message = arg0.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				// 根据返回值来对程序进行解析
				if (ReturnType != null) {
					if (ReturnType.equals("1001")) {
						if (GlobalConfig.playerobject.getContentFavorite().equals("0")) {
							tv_like.setText("已喜欢");
							img_like.setImageResource(R.drawable.wt_dianzan_select);
							GlobalConfig.playerobject.setContentFavorite("1");
							for (int i = 0; i < alllist.size(); i++) {
								if (alllist.get(i).getContentURI().equals(GlobalConfig.playerobject.getContentURI())) {
									GlobalConfig.playerobject.setContentFavorite("1");
								}
							}
						} else {
							tv_like.setText("喜欢");
							img_like.setImageResource(R.drawable.wt_dianzan_nomal);
							GlobalConfig.playerobject.setContentFavorite("0");
							for (int i = 0; i < alllist.size(); i++) {
								if (alllist.get(i).getContentURI().equals(GlobalConfig.playerobject.getContentURI())) {
									GlobalConfig.playerobject.setContentFavorite("0");
								}
							}
						}

					} else if (ReturnType.equals("0000")) {
						ToastUtil.show_allways(context, "无法获取相关的参数");
					} else if (ReturnType.equals("1002")) {
						ToastUtil.show_allways(context, "无法获得内容类别");
					} else if (ReturnType.equals("1003")) {
						ToastUtil.show_allways(context, "无法获得内容Id");
					} else if (ReturnType.equals("1004")) {
						ToastUtil.show_allways(context, "所指定的节目不存在");
					} else if (ReturnType.equals("1005")) {
						ToastUtil.show_allways(context, "已经喜欢了此内容");
					} else if (ReturnType.equals("1006")) {
						ToastUtil.show_allways(context, "还未喜欢此内容");
					} else if (ReturnType.equals("T")) {
						ToastUtil.show_allways(context, "获取列表异常");
					} else {
						ToastUtil.show_allways(context, Message + "");
					}
				} else {
					ToastUtil.show_allways(context, "Returntype==null");
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				if (dialogs != null) {
					dialogs.dismiss();
				}
			}
		};
		JSONObject jsonObject = new JSONObject();
		try {
			// 公共请求属性
			jsonObject.put("SessionId", Utils.getSessionId(context));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(context);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			// 模块属性
			jsonObject.put("UserId", Utils.getUserId(context));
			// MediaType
			jsonObject.put("MediaType", GlobalConfig.playerobject.getMediaType());
			jsonObject.put("ContentId", GlobalConfig.playerobject.getContentId());
			jsonObject.put("PCDType", "1");
			if (GlobalConfig.playerobject.getContentFavorite().equals("0")) {
				jsonObject.put("Flag", 1);
			} else {
				jsonObject.put("Flag", 0);
			}
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, GlobalConfig.clickFavoriteUrl,
					jsonObject, listener, errorListener);
			requestQueue.add(request);
			requestQueue.start();
			Log.e("喜欢提交路径+提交数据", GlobalConfig.clickFavoriteUrl + jsonObject + "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getLuKuangTTS() {
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			private String Message;

			@Override
			public void onResponse(JSONObject arg0) {
				if (dialogs != null) {
					dialogs.dismiss();
				}
				Log.e("获取路况tts数据", arg0 + "");
				try {
					Message = arg0.getString("ContentURI");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (Message != null && Message.trim().length() > 0) {
					img_news.setImageResource(R.drawable.wt_image_lktts);
					lukuangttsStart(Message);
				}

			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				if (dialogs != null) {
					dialogs.dismiss();
				}
			}
		};
		JSONObject jsonObject = new JSONObject();
		try {
			// 公共请求属性
			jsonObject.put("SessionId", Utils.getSessionId(context));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(context);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			// 模块属性
			jsonObject.put("UserId", Utils.getUserId(context));
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, GlobalConfig.getLKTTS, jsonObject,
					listener, errorListener);
			requestQueue.add(request);
			requestQueue.start();
			Log.e("获取路况tts数据以及路径", GlobalConfig.getLKTTS + jsonObject + "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void lukuangttsStart(String news) {
		SynthesizerListener mtslistener = new SynthesizerListener() {
			@Override
			public void onSpeakResumed() {
				Log.e("mts==============", "onSpeakResumed");
			}

			@Override
			public void onSpeakProgress(int arg0, int arg1, int arg2) {
			}

			@Override
			public void onSpeakPaused() {
			}

			@Override
			public void onSpeakBegin() {
			}

			@Override
			public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			}

			@Override
			public void onCompleted(SpeechError arg0) {
				synchronized (SYNC_Playing) {
					SYNC_Playing.notify();
				}
				enterCenter();
			}

			@Override
			public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
			}
		};
		lukuangTts.startSpeaking(news, mtslistener);
	}

}
*/