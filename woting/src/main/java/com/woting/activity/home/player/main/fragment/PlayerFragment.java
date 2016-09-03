package com.woting.activity.home.player.main.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
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

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.woting.R;
import com.woting.activity.download.dao.FileInfoDao;
import com.woting.activity.download.model.FileInfo;
import com.woting.activity.download.service.DownloadService;
import com.woting.activity.home.player.main.adapter.ImageAdapter;
import com.woting.activity.home.player.main.adapter.PlayerListAdapter;
import com.woting.activity.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.activity.home.player.main.model.LanguageSearch;
import com.woting.activity.home.player.main.model.LanguageSearchInside;
import com.woting.activity.home.player.main.model.PlayerHistory;
import com.woting.activity.home.player.main.model.sharemodel;
import com.woting.activity.home.player.timeset.activity.TimerPowerOffActivity;
import com.woting.activity.home.player.timeset.service.timeroffservice;
import com.woting.activity.home.program.album.model.ContentInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.helper.CommonHelper;
import com.woting.helper.ImageLoader;
import com.woting.util.BitmapUtils;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ShareUtils;
import com.woting.util.TimeUtils;
import com.woting.util.ToastUtils;
import com.woting.video.TtsPlayer;
import com.woting.video.VlcPlayer;
import com.woting.video.VoiceRecognizer;
import com.woting.video.WtAudioPlay;
import com.woting.widgetui.HorizontalListView;
import com.woting.widgetui.MyLinearLayout;
import com.woting.widgetui.xlistview.XListView;
import com.woting.widgetui.xlistview.XListView.IXListViewListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 播放主页
 * 2016年2月4日
 * @author 辛龙
 */
public class PlayerFragment extends Fragment implements OnClickListener, IXListViewListener {
	public static FragmentActivity context;
	// 功能性
	private static ImageLoader imageLoader;
	private static SimpleDateFormat format;
	private static SearchPlayerHistoryDao dbdao;
	private FileInfoDao FID;
	private static SharedPreferences sp;
	private AudioManager audioMgr;
	private MessageReceiver Receiver;
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
	private static ArrayList<LanguageSearchInside> alllist = new ArrayList<LanguageSearchInside>();
	//	private FileInfo file;
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
	public static WtAudioPlay audioplay;
	private HandlerThread mHandlerThread;
	private LinearLayout lin_like;
	private static ImageView img_like;
	private LinearLayout lin_lukuangtts;
	private VoiceRecognizer mVoiceRecognizer;
	private static TextView tv_like;
	private final static int TIME_UI = 10;
	private final static int VOICE_UI = 11;
	private final static int PLAY = 1;
	private final static int PAUSE = 2;
	private final static int STOP = 3;
	private final static int CONTINUE = 4;
	public static boolean isCurrentPlay;
	private static String playmtype;// 当前播放的媒体类型

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		imageLoader = new ImageLoader(context);
		bmppresss = BitmapUtils.readBitMap(context,R.mipmap.wt_duijiang_button_pressed);
		bmp = BitmapUtils.readBitMap(context, R.mipmap.talknormal);
		RefreshType = 0;
		if (Receiver == null) {
			Receiver = new MessageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("pushmusic");
			filter.addAction(BroadcastConstants.TIMER_UPDATE);
			filter.addAction(BroadcastConstants.TIMER_STOP);
			filter.addAction(BroadcastConstants.PLAYERVOICE);
			context.registerReceiver(Receiver, filter);
		}
		format = new SimpleDateFormat("HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		// 开启播放器服务
		context.startService(new Intent(context, TtsPlayer.class));
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		// 初始化语音配置对象
		SpeechUtility.createUtility(context, SpeechConstant.APPID + "=56275014");
		// 创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
		//mVoiceRecognizer=VoiceRecognizer.getInstance(context,BroadcastConstants.PLAYERVOICE);
		initDao();// 初始化数据库命令执行对象
		UMShareAPI.get(context);// 初始化友盟
		audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		// 获取最大音乐音量
		int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		stepVolume = maxVolume / 100;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_play, container, false);
		mHandler = new Handler();
		setview(); // 设置界面
		setlistener(); // 设置监听
		WifiDialog(); // wifi提示dialog
		ShareDialog(); // 分享dialog
		return rootView;
	}

	private void setview() {
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
		Bitmap bmp1 = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
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
					searchPress();
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
						audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC,stepVolume, AudioManager.FLAG_PLAY_SOUND);
						voice_type = 1;	
					    mVoiceRecognizer=VoiceRecognizer.getInstance(context,BroadcastConstants.PLAYERVOICE);
						mVoiceRecognizer.startListen(); 
						tv_speak_status.setText("开始语音转换");
						imageView_voice.setImageBitmap(bmppresss);
						textSpeakContent.setVisibility(View.GONE);
					} else {
						ToastUtils.show_short(context, "网络失败，请检查网络");
					}
					Log.i("---Main---", "--- > > > " + "按下");
					break;
				case MotionEvent.ACTION_UP:
					audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC,curVolume, AudioManager.FLAG_PLAY_SOUND);
					voice_type = 2;
					mVoiceRecognizer.stopListen();
					imageView_voice.setImageBitmap(bmp);
					tv_speak_status.setText("请按住讲话");
					Log.i("---Main---", "--- > > > " + "抬起");
					break;
				}
				return true;
			}
		});
		// seekbar事件
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				/**
				 * 定时服务开启当前节目播放完关闭时拖动进度条时更新定时时间
				 */
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (PlayerFragment.isCurrentPlay) {
							Intent intent = new Intent(context, timeroffservice.class);
							intent.setAction(BroadcastConstants.TIMER_START);
							int time = PlayerFragment.timerService;
							intent.putExtra("time", time);
							context.startService(intent);
						}
					}
				}, 1000);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					if (playmtype != null && playmtype != null && playmtype.equals("AUDIO")) {
						audioplay.setTime((long) progress);
						mUIHandler.sendEmptyMessage(TIME_UI);
						/*updateTextViewWithTimeFormat(time_start, progress);*/
					}
				}
			}
		});
	}

	public static void searchPress() {
		
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
			/* historynews.setContentCatalogs(""); */
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

	/**
	 * 把数据添加数据库
	 * 
	 * @param languageSearchInside
	 */
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
		String bjuserid = CommonUtils.getUserId(context);
		String ContentFavorite = languageSearchInside.getContentFavorite();
		String ContentID = languageSearchInside.getContentId();
		String localurl = languageSearchInside.getLocalurl();
		PlayerHistory history = new PlayerHistory(playername, playerimage,
				playerurl, playerurI, playermediatype, playeralltime,
				playerintime, playercontentdesc, playernum, playerzantype,
				playerfrom, playerfromid, playerfromurl, playeraddtime,
				bjuserid, playcontentshareurl, ContentFavorite, ContentID, localurl);

		if (playermediatype != null && playermediatype.trim().length() > 0&& playermediatype.equals("TTS")) {
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
				num = position - 2;
				/*GlobalConfig.playerobject=alllist.get(num);*/
				getnetwork(num, context);
				stopCurrentTimer();
			}
		});
	}

	/**
	 * 按照界面排序号进行播放
	 * 
	 * @param number
	 *            在play方法里初始化播放器对象 在musicplay方法里执行相关操作 要考虑entercenter方法
	 * 
	 */
	protected static void play(int number) {
		if (alllist != null && alllist.get(number) != null&& alllist.get(number).getMediaType() != null) {
			String s=alllist.get(number).getContentFavorite();
			String s1=alllist.get(number).getContentName();
			String s2=alllist.get(number).getContentURI();
			adddb(alllist.get(number));
			playmtype = alllist.get(number).getMediaType();
			if (playmtype.equals("AUDIO") || playmtype.equals("RADIO")) {
				// 首先判断audioplay是否为空
				// 如果为空，新建
				// 如果不为空 判断instance是否为当前播放 如果不是stop他后面再新建当前播放器的对象
				// 以下为实现播放器的方法
				if (audioplay == null) {
					audioplay = VlcPlayer.getInstance(context);
				} else {
					// 不为空
					if (audioplay.mark().equals("TTS")) {
						audioplay.stop();
					}
					audioplay = VlcPlayer.getInstance(context);
				}
				if (alllist.get(number).getContentPlay() != null) {
					img_play.setImageResource(R.mipmap.wt_play_play);
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
							url = GlobalConfig.imageurl+ alllist.get(number).getContentImg();
						}
						imageLoader.DisplayImage(url.replace("\\/", "/"),img_news, false, false, null, null);
					} else {
						img_news.setImageResource(R.mipmap.wt_image_playertx);
					}
					for (int i = 0; i < alllist.size(); i++) {
						alllist.get(i).setType("1");
					}
					alllist.get(number).setType("2");
					adapter.notifyDataSetChanged();
					if (alllist.get(number).getLocalurl() != null) {
						musicPlay("file:///"+ alllist.get(number).getLocalurl());
						ToastUtils.show_allways(context, "正在播放本地内容");
					} else {
						musicPlay(alllist.get(number).getContentPlay());
					}

					GlobalConfig.playerobject = alllist.get(number);
					resetHeadView();
					num = number;
				} else {
					ToastUtils.show_short(context, "暂不支持播放");
				}
			} else if (playmtype.equals("TTS")) {
				if (alllist.get(number).getContentURI() != null
						&& alllist.get(number).getContentURI().trim().length() > 0) {
					if (audioplay == null) {
						audioplay = TtsPlayer.getInstance(context);
					} else {
						// 不为空
						if (audioplay.mark().equals("VLC")) {
							audioplay.stop();
						}
						audioplay = TtsPlayer.getInstance(context);
					}
					img_play.setImageResource(R.mipmap.wt_play_play);
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
						img_news.setImageResource(R.mipmap.wt_image_playertx);
					}
					for (int i = 0; i < alllist.size(); i++) {
						alllist.get(i).setType("1");
					}
					alllist.get(number).setType("2");
					adapter.notifyDataSetChanged();
					musicPlay(alllist.get(number).getContentURI());
					GlobalConfig.playerobject = alllist.get(number);
					resetHeadView();
					num = number;
				} else {
					getContentNews(alllist.get(number).getContentId(), number);
				}
			}
		}
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
				} else { // 07/28 没有网络情况下还有可能播放本地文件
					if (alllist.get(number).getLocalurl() != null) {
						play(number);
						num = number;
					} else {
						ToastUtils.show_allways(context, "无网络连接");
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
			if (audioplay == null) {
				audioplay = TtsPlayer.getInstance(context);
			} else {
				// 不为空
				if (audioplay.mark().equals("VLC")) {
					audioplay.pause();
				}
				audioplay = TtsPlayer.getInstance(context);
			}
			ToastUtils.show_allways(context, "点击了路况TTS按钮");
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				dialogs = DialogUtils.Dialogph(context, "通讯中", dialogs);
				getLuKuangTTS();// 获取路况数据播报
			} else {
				ToastUtils.show_allways(context, "网络连接失败，请稍后重试");
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
			
			String s=GlobalConfig.playerobject.getContentFavorite();
			if (GlobalConfig.playerobject.getContentFavorite() != null
			&& !GlobalConfig.playerobject.getContentFavorite().equals("")) {
				sendfavorite();
			} else {
				ToastUtils.show_long(context, "本节目信息获取有误，暂时不支持喜欢");
			}
			break;
		case R.id.lin_left:
		  playLast();
			break;
		case R.id.lin_center:
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
					/* ToastUtil.show_allways(context, "已经将该节目添加到下载列表"); */
					// 此处执行将当前播放任务加到数据库的操作
					LanguageSearchInside datals = GlobalConfig.playerobject;
					if (datals.getLocalurl() != null) {
						ToastUtils.show_allways(context, "此节目已经保存到本地，请到已下载界面查看");
						return;
					}
					// 对数据进行转换
					List<ContentInfo> datalist = new ArrayList<ContentInfo>();
					ContentInfo mcontent = new ContentInfo();
					mcontent.setAuthor(datals.getContentPersons());
					mcontent.setContentPlay(datals.getContentPlay());
					mcontent.setContentImg(datals.getContentImg());
					mcontent.setContentName(datals.getContentName());
					mcontent.setUserid(CommonUtils.getUserId(context));
					mcontent.setDownloadtype("0");
					// sequname
					if (datals.getSeqInfo() == null
							|| datals.getSeqInfo().getContentName() == null
							|| datals.getSeqInfo().getContentName().equals("")) {
						mcontent.setSequname(datals.getContentName());
					} else {
						mcontent.setSequname(datals.getSeqInfo().getContentName());
					}
					// sequid
					if (datals.getSeqInfo() == null
							|| datals.getSeqInfo().getContentId() == null
							|| datals.getSeqInfo().getContentId().equals("")) {
						mcontent.setSequid(datals.getContentId());
					} else {
						mcontent.setSequid(datals.getSeqInfo().getContentId());
					}
					// sequimg
					if (datals.getSeqInfo() == null
							|| datals.getSeqInfo().getContentImg() == null
							|| datals.getSeqInfo().getContentImg().equals("")) {
						mcontent.setSequimgurl(datals.getContentImg());
					} else {
						mcontent.setSequimgurl(datals.getSeqInfo().getContentImg());
					}
					// sequdesc
					if (datals.getSeqInfo() == null
							|| datals.getSeqInfo().getContentDesc() == null
							|| datals.getSeqInfo().getContentDesc().equals("")) {
						mcontent.setSequdesc(datals.getContentDesc());
					} else {
						mcontent.setSequdesc(datals.getSeqInfo().getContentDesc());
					}
					datalist.add(mcontent);
					// 检查是否重复,如果不重复插入数据库，并且开始下载，重复了提示
					List<FileInfo> filedatalist = FID.queryFileinfoAll(CommonUtils.getUserId(context));
					if (filedatalist.size() != 0) {
						/**
						 * 此时有下载数据
						 */
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
							ToastUtils.show_allways(context,mcontent.getContentName() + "已经存在于下载列表");
						} else {
							FID.insertfileinfo(datalist);
							ToastUtils.show_allways(context,mcontent.getContentName() + "已经插入了下载列表");
							// 未下载列表
							List<FileInfo> fileundownloadlist = FID.queryFileinfo("false",CommonUtils.getUserId(context));
							FileInfo file = null;
							for (int kk = 0; kk < fileundownloadlist.size(); kk++) {
								if (fileundownloadlist.get(kk).getDownloadtype() == 1) {
									DownloadService.workStop(fileundownloadlist.get(kk));
									FID.updatedownloadstatus(fileundownloadlist.get(kk).getUrl(), "2");
									Log.e("测试下载问题",	" 暂停下载的单体"+ (fileundownloadlist.get(kk).getFileName()));
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
						/**
						 * 此时库里没数据
						 */
						FID.insertfileinfo(datalist);
						ToastUtils.show_allways(context,mcontent.getContentName() + "已经插入了下载列表");
						// 未下载列表
						List<FileInfo> fileundownloadlist = FID.queryFileinfo("false", CommonUtils.getUserId(context));
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
					ToastUtils.show_allways(context, "您现在播放的是电台节目，不支持下载");
				}
			} else {
				ToastUtils.show_allways(context, "当前播放器播放对象为空");
			}
			break;
		}
	}

	public  static void playLast() {
		if (num - 1 >= 0) {
			Log.e("点击num===============", num + "");
			num = num - 1;
			getnetwork(num, context);
			stopCurrentTimer();
		} else {
			ToastUtils.show_allways(context, "已经是第一条数据了");
		}
		
	}

	private void getLuKuangTTS() {
		JSONObject jsonObject = new JSONObject();
		try {
			// 公共请求属性
			jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(context);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			// 模块属性
			jsonObject.put("UserId", CommonUtils.getUserId(context));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.getLKTTS, jsonObject, new VolleyCallback() {
			private String Message;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialogs != null) {
					dialogs.dismiss();
				}
				try {
					Message = result.getString("ContentURI");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (Message != null && Message.trim().length() > 0) {
					img_news.setImageResource(R.mipmap.wt_image_lktts);
	/*				musicPlay("nullnullnullnullnullnullnull");*/
				    musicPlay(Message); 
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialogs != null) {
					dialogs.dismiss();
				}
			}
		});
	}

	/**
	 * 开启定时服务中的当前播放完后关闭的关闭服务方法 点击暂停播放、下一首、上一首以及播放路况信息时都将自动关闭此服务
	 */
	private static void stopCurrentTimer() {
		if (PlayerFragment.isCurrentPlay) {
			Intent intent = new Intent(context, timeroffservice.class);
			intent.setAction(BroadcastConstants.TIMER_STOP);
			context.startService(intent);
			PlayerFragment.isCurrentPlay = false;
		}
	}

	public static void enterCenter() {
		if (GlobalConfig.playerobject != null && GlobalConfig.playerobject.getMediaType() != null) {
			playmtype = GlobalConfig.playerobject.getMediaType();
			if (playmtype.equals("AUDIO") || playmtype.equals("RADIO")) {
				// 首先判断audioplay是否为空
				// 如果为空，新建
				// 如果不为空 判断instance是否为当前播放 如果不是stop他后面再新建当前播放器的对象
				// 以下为实现播放器的方法
				if (audioplay == null) {
					audioplay = VlcPlayer.getInstance(context);
				} else {
					// 不为空
					if (audioplay.mark().equals("TTS")) {
						audioplay.stop();
					}
					audioplay = VlcPlayer.getInstance(context);
				}
				if (GlobalConfig.playerobject.getContentPlay() != null) {
					if (GlobalConfig.playerobject != null) {
						tv_name.setText(GlobalConfig.playerobject.getContentName());
					} else {
						tv_name.setText("wotingkeji");
					}
					if (GlobalConfig.playerobject.getContentImg() != null) {
						String url;
						if (GlobalConfig.playerobject.getContentImg().startsWith("http")) {
							url = GlobalConfig.playerobject.getContentImg();
						} else {
							url = GlobalConfig.imageurl+ GlobalConfig.playerobject.getContentImg();
						}
						imageLoader.DisplayImage(url.replace("\\/", "/"),img_news, false, false, null, null);
					} else {
						img_news.setImageResource(R.mipmap.wt_image_playertx);
					}
					for (int i = 0; i < alllist.size(); i++) {
						alllist.get(i).setType("1");
					}
					GlobalConfig.playerobject.setType("2");
					adapter.notifyDataSetChanged();
					if (GlobalConfig.playerobject.getLocalurl() != null) {
						musicPlay("file:///"+ GlobalConfig.playerobject.getLocalurl());
						ToastUtils.show_allways(context, "正在播放本地内容");
					} else {
						musicPlay(GlobalConfig.playerobject.getContentPlay());
					}
					resetHeadView();
				} else {
					ToastUtils.show_short(context, "暂不支持播放");
				}
			} else if (playmtype.equals("TTS")) {
				if (GlobalConfig.playerobject.getContentURI() != null
						&& GlobalConfig.playerobject.getContentURI().trim().length() > 0) {
					if (audioplay == null) {
						audioplay = TtsPlayer.getInstance(context);
					} else {
						// 不为空
						if (audioplay.mark().equals("VLC")) {
							audioplay.stop();
						}
						audioplay = TtsPlayer.getInstance(context);
					}

					if (GlobalConfig.playerobject.getContentName() != null) {
						tv_name.setText(GlobalConfig.playerobject.getContentName());
					} else {
						tv_name.setText("wotingkeji");
					}
					if (GlobalConfig.playerobject.getContentImg() != null) {
						String url;
						if (GlobalConfig.playerobject.getContentImg().startsWith("http")) {
							url = GlobalConfig.playerobject.getContentImg();
						} else {
							url = GlobalConfig.imageurl+ GlobalConfig.playerobject.getContentImg();
						}
						imageLoader.DisplayImage(url.replace("\\/", "/"),img_news, false, false, null, null);
					} else {
						img_news.setImageResource(R.mipmap.wt_image_playertx);
					}
					for (int i = 0; i < alllist.size(); i++) {
						alllist.get(i).setType("1");
					}
					GlobalConfig.playerobject.setType("2");
					adapter.notifyDataSetChanged();
					musicPlay(GlobalConfig.playerobject.getContentURI());
					resetHeadView();
				}
			}
		} else {
			ToastUtils.show_allways(context, "当前播放对象为空");
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
			String enter = sp.getString(StringConstant.PLAYHISTORYENTER, "false");
			String news = sp.getString(StringConstant.PLAYHISTORYENTERNEWS, "");
			if (enter.equals("true")) {
				SendTextRequest(news, context);
				Editor et = sp.edit();
				et.putString(StringConstant.PLAYHISTORYENTER, "false");
				et.commit();
			} else {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					dialogs = DialogUtils.Dialogph(context, "通讯中", dialogs);
					firstsend();// 搜索第一次数据
				} else {
					ToastUtils.show_allways(context, "网络连接失败，请稍后重试");
				}
			}
			first = false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(mVoiceRecognizer!=null){
			mVoiceRecognizer.ondestroy();
			mVoiceRecognizer=null;
		}
		if (Receiver != null) { // 注销广播
			context.unregisterReceiver(Receiver);
			Receiver = null;
		}
		mHandlerThread.quit(); // 关闭线程
	}

	/**
	 * 广播接收器
	 */
	class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("pushmusic")) {
				String message = intent.getStringExtra("outmessage");
				if (GlobalConfig.playerobject != null) {
					if (message != null && message.equals("1")) {
						/*
						 * // 暂停音乐播放 if
						 * (GlobalConfig.playerobject.getMediaType()
						 * .equals("AUDIO") ||
						 * GlobalConfig.playerobject.getMediaType
						 * ().equals("RADIO")) { if (mPlayerStatus ==
						 * PLAYER_STATUS.PLAYER_PLAYING) {
						 * img_play.setImageResource(R.drawable.wt_play_stop);
						 * } } else { if (TTSPlayerStatus ==
						 * PLAYER_STATUS.TTS_PLAYING) {
						 * img_play.setImageResource(R.drawable.wt_play_stop);
						 * } }
						 */
						/* playNext(); */
					} else {

					}
				}
			} else if (action.equals(BroadcastConstants.TIMER_UPDATE)) {
				String s = intent.getStringExtra("update");
				if (textTime != null) {
					textTime.setText(s);
				}
			} else if (action.equals(BroadcastConstants.TIMER_STOP)) {
				if (textTime != null) {
					textTime.setText("定时");
				}
			} else if (action.equals(BroadcastConstants.PLAYERVOICE)) {
				String str = intent.getStringExtra("VoiceContent");
				tv_speak_status.setText("正在为您查找: "+str);
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					if(!str.trim().equals("")){
					tv_speak_status.setText("正在搜索: "+str);	
					/*	textSpeakContent.setVisibility(View.VISIBLE);
						textSpeakContent.setText("正在搜索:"+str);*/
						searchByVoicesend(str);							
						Handler handler =new Handler();
						handler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								rl_voice.setVisibility(View.GONE);
							}
						}, 2000);
					} 					
				} else {
					ToastUtils.show_short(context, "网络失败，请检查网络");
				}
			}

		}
	}

	public static void playNext() {
		if (num + 1 < alllist.size()) {
			// 此时自动播放下一首
			num = num + 1;
			getnetwork(num, context);
		} else {
			// 全部播放完毕了
			num = 0;
			getnetwork(num, context);
		}
	}

	public static int timerService; // 当前节目播放剩余时间长度
	static Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// case NEXT_PLAY://播放下一首
			//
			// break;
			case TIME_UI: // 更新进度及时间
				if (GlobalConfig.playerobject != null&& GlobalConfig.playerobject.getMediaType() != null
				&& GlobalConfig.playerobject.getMediaType().trim().length() > 0
				&& GlobalConfig.playerobject.getMediaType().equals("AUDIO")) {
					long currPosition = audioplay.getTime();
					long duration = audioplay.getTotalTime();
					updateTextViewWithTimeFormat(time_start,(int) (currPosition / 1000));
					updateTextViewWithTimeFormat(time_end,(int) (duration / 1000));
					seekBar.setMax((int) duration);
					timerService = (int) (duration - currPosition);
					if (audioplay.isPlaying()) {
						seekBar.setProgress((int) currPosition);
					}
					/*	if (duration > 0 && currPosition >= (duration - 1000)) {
						playNext();
						Log.e("播放器播放", "开始播放下一首");
					}	*/
				} else if (GlobalConfig.playerobject != null
						&& GlobalConfig.playerobject.getMediaType() != null
						&& GlobalConfig.playerobject.getMediaType().trim().length() > 0
						&& GlobalConfig.playerobject.getMediaType().equals("RADIO")) {
					int _currPosition = TimeUtils.getTime(System.currentTimeMillis());
					int _duration = 24 * 60 * 60;
					updateTextViewWithTimeFormat(time_start, _currPosition);
					updateTextViewWithTimeFormat(time_end, _duration);
					seekBar.setMax(_duration);
					seekBar.setProgress(_currPosition);

				} else if (GlobalConfig.playerobject != null
						&& GlobalConfig.playerobject.getMediaType() != null
						&& GlobalConfig.playerobject.getMediaType().trim().length() > 0
						&& GlobalConfig.playerobject.getMediaType().equals("TTS")) {

					int _currPosition = TimeUtils.getTime(System.currentTimeMillis());
					int _duration = 24 * 60 * 60;
					updateTextViewWithTimeFormat(time_start, _currPosition);
					updateTextViewWithTimeFormat(time_end, _duration);
					seekBar.setMax(_duration);
					seekBar.setProgress(_currPosition);
					/*	int _currPosition = TTS_SpeakProgress;
					int _duration = (int) (TTSNews.length() / 4.15);
					updateTextViewWithTimeFormat(time_start, _currPosition);
					updateTextViewWithTimeFormat(time_end, _duration);
					seekBar.setMax(_duration);
					seekBar.setProgress(_currPosition);
					TTS_SpeakProgress++;*/
				}
				mUIHandler.sendEmptyMessageDelayed(TIME_UI, 1000);
				break;
			case VOICE_UI:
				rl_voice.setVisibility(View.GONE);
				textSpeakContent.setVisibility(View.GONE);
				tv_speak_status.setText("请按住讲话");
				break;
			case PLAY:
				audioplay.play(local);
				break;
			case PAUSE:
				audioplay.pause();
				break;
			case CONTINUE:
				audioplay.continuePlay();
				break;
			case STOP:
				audioplay.stop();
				break;
			}
		}
	};

	private static void updateTextViewWithTimeFormat(TextView view, long second) {
		int hh = (int) (second / 3600);
		int mm = (int) (second % 3600 / 60);
		int ss = (int) (second % 60);
		String strTemp = null;
		strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
		view.setText(strTemp);
	}

	/**
	 * 播放 方法处理类 mUIHandler
	 */
	/*
	 * static String local;//存放当前播放的状态 private static void musicPlay(String s) {
	 * if(local==null){ local=s; audioplay.play(s);//首次播放
	 * img_play.setImageResource(R.drawable.wt_play_play); }else{ //不等于空
	 * if(local.equals(s)){ //里面可以根据播放类型判断继续播放或者停止 if(audioplay.isPlaying()){
	 * //播放状态，对应暂停方法，播放图 audioplay.pause();
	 * img_play.setImageResource(R.drawable.wt_play_stop); }else{
	 * //暂停状态，对应播放方法，暂停图 audioplay.continuePlay();
	 * img_play.setImageResource(R.drawable.wt_play_play); } }else{
	 * audioplay.play(s);//首次播放 local=s;
	 * img_play.setImageResource(R.drawable.wt_play_play); } } if (playmtype!=
	 * null && playmtype.trim().length() > 0 && playmtype.equals("AUDIO")) {
	 * seekBar.setEnabled(true); mUIHandler.sendEmptyMessage(TIME_UI); } else {
	 * seekBar.setEnabled(false); mUIHandler.sendEmptyMessage(TIME_UI); } }
	 */static String local;

	 private static void musicPlay(String s) {
		 if (local == null) {
			 local = s;
			 mUIHandler.sendEmptyMessage(PLAY);
			 img_play.setImageResource(R.mipmap.wt_play_play);
			 setPlayingType();
		 } else {
			 // 不等于空
			 if (local.equals(s)) {
				 // 里面可以根据播放类型判断继续播放或者停止
				 if (audioplay.isPlaying()) {
					 // 播放状态，对应暂停方法，播放图
					 audioplay.pause();
					 // mUIHandler.sendEmptyMessage(PAUSE);
					 if (playmtype.equals("AUDIO")) {
						 mUIHandler.removeMessages(TIME_UI);
					 }
					 img_play.setImageResource(R.mipmap.wt_play_stop);
					 setPauseType();
				 } else {
					 // 暂停状态，对应播放方法，暂停图
					 audioplay.continuePlay();
					 // mUIHandler.sendEmptyMessage(CONTINUE);
					 img_play.setImageResource(R.mipmap.wt_play_play);
					 setPlayingType();
				 }
			 } else {
				 local = s;
				 mUIHandler.sendEmptyMessage(PLAY);
				 img_play.setImageResource(R.mipmap.wt_play_play);
				 setPlayingType();
			 }
		 }

		 if (playmtype != null && playmtype.trim().length() > 0 && playmtype.equals("AUDIO")) {
			 seekBar.setEnabled(true);
			 mUIHandler.sendEmptyMessage(TIME_UI);
		 } else {
			 seekBar.setEnabled(false);
			 mUIHandler.sendEmptyMessage(TIME_UI);
		 }
	 }

	 // //////////
	 // 分享模块//
	 // //////////
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
		 /* Sharedialog.show(); */
		 dialog1 = DialogUtils.Dialogphnoshow(context, "通讯中", dialog1);
		 Config.dialog = dialog1;
		 final List<sharemodel> mylist = ShareUtils.getShareModelList();
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
			 new ShareAction(context).setPlatform(Platform).withMedia(image)
			 .withText(shareDesc).withTitle(sharename).withTargetUrl(shareurl).share();
		 } else {
			 ToastUtils.show_short(context, "没有数据");
		 }
	 }

	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
	 }
	 
	 // 语音搜索请求
	 private void searchByVoicesend(String str) {
		 sendtype = 2;
		 // 发送数据
		 JSONObject jsonObject = new JSONObject();
		 try {
			 jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			 jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			 jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			 jsonObject.put("IMEI", PhoneMessage.imei);
			 jsonObject.put("UserId", CommonUtils.getUserId(context));
			 jsonObject.put("SearchStr", str);
			 jsonObject.put("PCDType", GlobalConfig.PCDType);
			 jsonObject.put("PageType", "0");
		 } catch (JSONException e) {
			 e.printStackTrace();
		 }

		 VolleyRequest.RequestTextVoicePost(GlobalConfig.searchvoiceUrl, jsonObject, new VolleyCallback() {
			 private String ReturnType;
			 private String MainList;

			 @Override
			 protected void requestSuccess(JSONObject result) {
				 try {
					 ReturnType = result.getString("ReturnType");
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 try {
					 MainList = result.getString("ResultList");
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 if (ReturnType.equals("1001")) {
					 try {
						 LanguageSearch lists = new Gson().fromJson(MainList, new TypeToken<LanguageSearch>() {}.getType());
						 List<LanguageSearchInside> list = lists.getList();
						 list.get(0).getContentDesc();
						 if (list != null && list.size() != 0) {
							 num = 0;
							 alllist.clear();
							 alllist.addAll(list);
							 GlobalConfig.playerobject=alllist.get(num);
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
					 ToastUtils.show_short(context, "没有查询内容");
				 } else {
					 ToastUtils.show_short(context, "没有新的数据");
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

			 @Override
			 protected void requestError(VolleyError error) {
				 new Handler().postDelayed(new Runnable() {
					 @Override
					 public void run() {
						 if (voice_type == 2) {
							 mUIHandler.sendEmptyMessage(VOICE_UI);
						 }
					 }
				 }, 5000);				
			 }
		 });
	 }

	 // /////////////
	 // wifi弹出框//
	 // ////////////
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

	 /**
	  * 第一次进入该界面时候的数据
	  */
	 private void firstsend() {
		 sendtype = 1;
		 JSONObject jsonObject = new JSONObject();
		 try {
			 jsonObject.put("PCDType", GlobalConfig.PCDType);
			 jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			 jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			 jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			 jsonObject.put("IMEI", PhoneMessage.imei);
			 PhoneMessage.getGps(context);
			 jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			 jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			 jsonObject.put("UserId", CommonUtils.getUserId(context));
			 jsonObject.put("PageType", "0");
			 jsonObject.put("Page", String.valueOf(page));
			 jsonObject.put("PageSize", "10");
		 } catch (JSONException e) {
			 e.printStackTrace();
		 }

		 VolleyRequest.RequestPost(GlobalConfig.mainPageUrl, jsonObject, new VolleyCallback() {
			 private String ReturnType;
			 private String MainList;

			 @Override
			 protected void requestSuccess(JSONObject result) {
				 if (dialogs != null) {
					 dialogs.dismiss();
				 }
				 Log.e("第一次返回值",""+result.toString());
				 try {
					 ReturnType = result.getString("ReturnType");
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 if (ReturnType.equals("1001")) {
					 page++;
					 try {
						 String List = result.getString("ResultList");
						 JSONTokener jsonParser = new JSONTokener(List);
						 JSONObject arg1 = (JSONObject) jsonParser.nextValue();
						 MainList = arg1.getString("List");
						 ArrayList<LanguageSearchInside> list = new Gson().fromJson(
								 MainList, new TypeToken<List<LanguageSearchInside>>() {}.getType());
						 String s=list.get(0).getContentDesc();			 
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
									 if (alllist.get(i).getContentPlay().equals(GlobalConfig.playerobject.getContentPlay())) {
										 alllist.get(i).setType("0");
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

			 @Override
			 protected void requestError(VolleyError error) {
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
			 }
		 });
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
			 img_news.setImageResource(R.mipmap.wt_image_playertx);
		 }
		 alllist.clear();
		 alllist.addAll(list);
		 if (GlobalConfig.playerobject != null && alllist != null) {
			 for (int i = 0; i < alllist.size(); i++) {
				 // alllist.get(i).getContentPlay() == null
				 // if (alllist.get(i).getContentPlay() != null &&
				 // alllist.get(i).getContentPlay().equals(GlobalConfig.playerobject.getContentPlay()))
				 // {
				 String s = alllist.get(i).getContentPlay();
				 if (s != null) {
					 if (s.equals(GlobalConfig.playerobject.getContentPlay())) {
						 alllist.get(i).setType("0");
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
			 img_news.setImageResource(R.mipmap.wt_image_playertx);
		 }
		 alllist.clear();
		 alllist.addAll(list);
		 alllist.get(0).setType("0");
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
				 ToastUtils.show_allways(context, "电台节目目前不支持分享");
				 return;
				 // 设置灰色界面
			 } else {
				 // 支持分享
				 // 设置回界面
			 }
			 if (GlobalConfig.playerobject.getContentFavorite() != null
					 && !GlobalConfig.playerobject.equals("")) {
				 
				 Log.w("ContentFavorite", "--- > > > ContentFavorite = " + GlobalConfig.playerobject.getContentFavorite());
				 
				 if(GlobalConfig.playerobject.getContentFavorite().equals("0")){
					 tv_like.setText("喜欢");
					 img_like.setImageResource(R.mipmap.wt_dianzan_nomal);
				 }else{
					 tv_like.setText("已喜欢");
					 img_like.setImageResource(R.mipmap.wt_dianzan_select);
				 }
			 } else {
				 tv_like.setText("喜欢");
				 img_like.setImageResource(R.mipmap.wt_dianzan_nomal);
				 // ToastUtil.show_allways(context, "本节目不支持喜欢");
			 }
		 } else {
			 // ToastUtil.show_allways(context,"当前播放对象异常");
		 }
	 }

	 // 文字请求
	 public static void SendTextRequest(String contname, final Context mContext) {
		 /* dialogs = Utils.Dialogph(mContext, "通讯中", dialogs); */
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
		 JSONObject jsonObject = new JSONObject();
		 try {
			 jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			 jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			 jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			 jsonObject.put("IMEI", PhoneMessage.imei);
			 PhoneMessage.getGps(context);
			 jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			 jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			 jsonObject.put("UserId", CommonUtils.getUserId(context));
			 jsonObject.put("SearchStr", contname);
			 jsonObject.put("PCDType",GlobalConfig.PCDType);
			 jsonObject.put("PageType", "0");
		 } catch (JSONException e) {
			 e.printStackTrace();
		 }

		 VolleyRequest.RequestTextVoicePost(GlobalConfig.getSearchByText, jsonObject, new VolleyCallback() {
			 private String ReturnType;
			 private String MainList;

			 @Override
			 protected void requestSuccess(JSONObject result) {
				 if (dialogs != null) {
					 dialogs.dismiss();
				 }
				 try {
					 ReturnType = result.getString("ReturnType");
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 try {
					 MainList = result.getString("ResultList");
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 if (ReturnType.equals("1001")) {
					 try {
						 LanguageSearch lists = new Gson().fromJson(MainList, new TypeToken<LanguageSearch>() {}.getType());
						 List<LanguageSearchInside> list = lists.getList();
						 if (list != null && list.size() != 0) {
							 for (int i = 0; i < list.size(); i++) {
								 // String s = list.get(i).getContentPlay();
								 if (list.get(i).getContentPlay() != null
										 && !list.get(i).getContentPlay().equals("null")
										 && !list.get(i).getContentPlay().equals("")
										 && list.get(i).getContentPlay().equals(flist.getContentPlay())) {
									 list.remove(i);
								 }
							 }
							 num = 0;
							 alllist.clear();
							 alllist.add(flist);
							 alllist.addAll(list);
							 GlobalConfig.playerobject=alllist.get(num);
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
					 } catch (Exception e) {
						 e.printStackTrace();
					 }
				 } else if (ReturnType.equals("1011")) {
					 ToastUtils.show_short(context, "没有查询内容");
				 } else {
					 ToastUtils.show_short(context, "没有新的数据");
				 }
			 }

			 @Override
			 protected void requestError(VolleyError error) {
				 if (dialogs != null) {
					 dialogs.dismiss();
				 }
			 }
		 });
	 }

	 private static void getContentNews(String id, final int number) {
		 JSONObject jsonObject = new JSONObject();
		 try {
			 jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			 jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			 jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			 jsonObject.put("IMEI", PhoneMessage.imei);
			 PhoneMessage.getGps(context);
			 jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			 jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			 jsonObject.put("UserId", CommonUtils.getUserId(context));
			 jsonObject.put("MediaType", "TTS");
			 jsonObject.put("ContentId", id);
		 } catch (JSONException e) {
			 e.printStackTrace();
		 }

		 VolleyRequest.RequestTextVoicePost(GlobalConfig.getContentById, jsonObject, new VolleyCallback() {
			 private String ReturnType;
			 private String MainList;

			 @Override
			 protected void requestSuccess(JSONObject result) {
				 try {
					 ReturnType = result.getString("ReturnType");
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 try {
					 MainList = result.getString("ResultInfo");
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 if (ReturnType.equals("1001")) {
					 try {
						 LanguageSearchInside lists = new Gson().fromJson(MainList, 
								 new TypeToken<LanguageSearchInside>() {}.getType());
						 String ContentURI = lists.getContentURI();
						 Log.e("ContentURI", ContentURI + "");
						 if (ContentURI != null && ContentURI.trim().length() > 0) {
							 img_play.setImageResource(R.mipmap.wt_play_play);
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
								 img_news.setImageResource(R.mipmap.wt_image_playertx);
							 }
							 for (int i = 0; i < alllist.size(); i++) {
								 alllist.get(i).setType("1");
							 }
							 alllist.get(number).setType("2");
							 adapter.notifyDataSetChanged();
							 musicPlay(ContentURI);
//							 GlobalConfig.playerobject = alllist.get(number);
							 GlobalConfig.playerobject = alllist.get(number);
							 resetHeadView();// 页面的对象改变，根据对象重新设置属性
							 num = number;
						 }
					 } catch (JsonSyntaxException e) {
						 e.printStackTrace();
					 }
				 }				
			 }

			 @Override
			 protected void requestError(VolleyError error) {

			 }
		 });
	 }

	 private static void sendfavorite() {
		 dialogs = DialogUtils.Dialogph(context, "通讯中", dialogs);
		 JSONObject jsonObject = new JSONObject();
		 try {
			 // 公共请求属性
			 jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			 jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			 jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			 jsonObject.put("IMEI", PhoneMessage.imei);
			 PhoneMessage.getGps(context);
			 jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			 jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			 // 模块属性
			 jsonObject.put("UserId", CommonUtils.getUserId(context));
			 // MediaType
			 jsonObject.put("MediaType", GlobalConfig.playerobject.getMediaType());
			 jsonObject.put("ContentId", GlobalConfig.playerobject.getContentId());
			 jsonObject.put("PCDType",GlobalConfig.PCDType);
			 if (GlobalConfig.playerobject.getContentFavorite().equals("0")) {
				 jsonObject.put("Flag", 1);
			 } else {
				 jsonObject.put("Flag", 0);
			 }
		 } catch (JSONException e) {
			 e.printStackTrace();
		 }

		 VolleyRequest.RequestPost(GlobalConfig.clickFavoriteUrl, jsonObject, new VolleyCallback() {
			 private String ReturnType;
			 private String Message;

			 @Override
			 protected void requestSuccess(JSONObject result) {
				 if (dialogs != null) {
					 dialogs.dismiss();
				 }
				 try {
					 ReturnType = result.getString("ReturnType");
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 try {
					 Message = result.getString("Message");
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }

				 // 根据返回值来对程序进行解析
				 if (ReturnType != null) {
					 if (ReturnType.equals("1001")) {
						 if (GlobalConfig.playerobject.getContentFavorite().equals("0")) {
							 tv_like.setText("已喜欢");
							 img_like.setImageResource(R.mipmap.wt_dianzan_select);
							 GlobalConfig.playerobject.setContentFavorite("1");
							 for (int i = 0; i < alllist.size(); i++) {
								 if (alllist.get(i).getContentURI().equals(GlobalConfig.playerobject.getContentURI())) {
									 GlobalConfig.playerobject.setContentFavorite("1");
								 }
							 }
						 } else {
							 tv_like.setText("喜欢");
							 img_like.setImageResource(R.mipmap.wt_dianzan_nomal);
							 GlobalConfig.playerobject.setContentFavorite("0");
							 for (int i = 0; i < alllist.size(); i++) {
								 if (alllist.get(i).getContentURI().equals(GlobalConfig.playerobject.getContentURI())) {
									 GlobalConfig.playerobject.setContentFavorite("0");
								 }
							 }
						 }
					 } else if (ReturnType.equals("0000")) {
						 ToastUtils.show_allways(context, "无法获取相关的参数");
					 } else if (ReturnType.equals("1002")) {
						 ToastUtils.show_allways(context, "无法获得内容类别");
					 } else if (ReturnType.equals("1003")) {
						 ToastUtils.show_allways(context, "无法获得内容Id");
					 } else if (ReturnType.equals("1004")) {
						 ToastUtils.show_allways(context, "所指定的节目不存在");
					 } else if (ReturnType.equals("1005")) {
						 ToastUtils.show_allways(context, "已经喜欢了此内容");
					 } else if (ReturnType.equals("1006")) {
						 ToastUtils.show_allways(context, "还未喜欢此内容");
					 } else if (ReturnType.equals("T")) {
						 ToastUtils.show_allways(context, "获取列表异常");
					 } else {
						 ToastUtils.show_allways(context, Message + "");
					 }
				 } else {
					 ToastUtils.show_allways(context, "Returntype==null");
				 }
			 }

			 @Override
			 protected void requestError(VolleyError error) {
				 if (dialogs != null) {
					 dialogs.dismiss();
				 }
			 }
		 });
	 }

	 /**
	  * 设置当前为播放状态
	  */
	 private static void setPlayingType(){
		 if(PlayerFragment.audioplay != null && num >= 0){
			 alllist.get(num).setType("2");
			 adapter.notifyDataSetChanged();
		 }
	 }

	 /**
	  * 设置当前为暂停状态
	  */
	 private static void setPauseType(){
		 if(PlayerFragment.audioplay != null && num >= 0){
			 alllist.get(num).setType("0");
			 adapter.notifyDataSetChanged();
		 }
	 }
}
