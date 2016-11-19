package com.woting.ui.home.player.main.fragment;

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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
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
import com.squareup.picasso.Picasso;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ShareUtils;
import com.woting.common.util.TimeUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.HorizontalListView;
import com.woting.common.widgetui.MyLinearLayout;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.download.dao.FileInfoDao;
import com.woting.ui.download.model.FileInfo;
import com.woting.ui.download.service.DownloadService;
import com.woting.ui.home.player.main.adapter.ImageAdapter;
import com.woting.ui.home.player.main.adapter.PlayerListAdapter;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.LanguageSearch;
import com.woting.ui.home.player.main.model.LanguageSearchInside;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.player.main.model.ShareModel;
import com.woting.ui.home.player.timeset.activity.TimerPowerOffActivity;
import com.woting.ui.home.player.timeset.service.timeroffservice;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.album.model.ContentInfo;
import com.woting.ui.home.program.comment.CommentActivity;
import com.woting.ui.home.search.adapter.SearchHotAdapter;
import com.woting.ui.mine.playhistory.activity.PlayHistoryActivity;
import com.woting.video.TtsPlayer;
import com.woting.video.VlcPlayer;
import com.woting.video.VoiceRecognizer;
import com.woting.video.WtAudioPlay;

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
	private static SimpleDateFormat format;
	private static SearchPlayerHistoryDao dbDao;
	private static LanguageSearchInside historyNews;
	private static String searchName;
	private FileInfoDao FID;
	private static SharedPreferences sp;
	private AudioManager audioMgr;
	private MessageReceiver Receiver;
	private static TextView tv_speak_status;
	private static TextView tv_name;
	private static TextView textSpeakContent;
	private View headView;
	private LinearLayout lin_right;
	private static ImageView img_play;
	private LinearLayout lin_left;
	private static ImageView img_news;
	private View rootView;
	private RelativeLayout lin_center;
	public static TextView time_start;
	public static TextView time_end;
	private LinearLayout lin_VoiceSearch;
	private LinearLayout lin_comment;
	private static XListView mListView;
	private static LinearLayout lin_tuijian;
	private LinearLayout lin_chose;
	private ImageView image_liu;
	private static MyLinearLayout rl_voice;
	private ImageView imageView_voice;
	private TextView tv_cancel;
	private LinearLayout lin_download;
	private UMImage image;
	private LinearLayout lin_share;
	private static SeekBar seekBar;
	private TextView textTime;
	// 数据
	private static int num;// -2 播放器没有播放，-1播放器里边的数据不在list中，其它是在list中
	private static ArrayList<LanguageSearchInside> allList = new ArrayList<LanguageSearchInside>();
	private int stepVolume;
	private int curVolume;
	private Bitmap bmpPress;
	private Bitmap bmp;
	private int screenWidth;
	// 其它
	private static PlayerListAdapter adapter;
	private static Dialog dialogs;
	private static Dialog wifiDialog;
	private Dialog ShareDialog;
	private Dialog dialog1;
	private static Handler mHandler;
	// 标识参数
	private static int sendType;// 第一次获取数据是有分页加载的
	private static int page = 1;// mainpage
	public static int TextPage=1; // 文本搜索page
	private static int VoicePage=1;       // 语音搜索page

	private int RefreshType;// 是不是第一次请求数据
	private boolean first = true;// 第一次进入界面
	private int voice_type = 2;// 判断此时是否按下语音按钮，1，按下2，松手
	public static WtAudioPlay audioPlay;
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
	private static String playType;// 当前播放的媒体类型
	private static String ContentFavorite;
	public static int timerService; // 当前节目播放剩余时间长度
	private static long currPosition=-1;//当前的播放时间
	private static Boolean PlayFlag=false; //是否首次播放的控制
	private RelativeLayout rv_details;
	private TextView tv_details_flag;
	private Boolean details_flag=false;
	private GridView flowTag;
	private ArrayList<String> testList;
	private static TextView tv_sequ;
	private static TextView tv_desc;
	private static ImageView img_download;
	private static TextView tv_download;
	private String voiceStr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		bmpPress = BitmapUtils.readBitMap(context,R.mipmap.wt_duijiang_button_pressed);
		bmp = BitmapUtils.readBitMap(context, R.mipmap.talknormal);
		RefreshType = 0;
		if (Receiver == null) {
			Receiver = new MessageReceiver();
			IntentFilter filter = new IntentFilter();
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
		setView(); // 设置界面
		setListener(); // 设置监听
		WifiDialog(); // wifi提示dialog
		ShareDialog(); // 分享dialog
		return rootView;
	}

	private void setView() {
		mListView = (XListView) rootView.findViewById(R.id.listView);
		mListView.setPullLoadEnable(false);
		mListView.setXListViewListener(this);

		lin_chose = (LinearLayout) rootView.findViewById(R.id.lin_chose);
		LinearLayout lin_ly_ckzj = (LinearLayout) rootView.findViewById(R.id.lin_ly_ckzj);
		LinearLayout lin_other = (LinearLayout) rootView.findViewById(R.id.lin_other);
		LinearLayout lin_ly_ckzb = (LinearLayout) rootView.findViewById(R.id.lin_ly_ckzb);
		LinearLayout lin_ly_history = (LinearLayout) rootView.findViewById(R.id.lin_ly_history);
		LinearLayout lin_ly_timeover = (LinearLayout) rootView.findViewById(R.id.lin_ly_timeover);
		TextView tv_ly_qx = (TextView) rootView.findViewById(R.id.tv_ly_qx);
		tv_ly_qx.setOnClickListener(this);
		lin_other.setOnClickListener(this);
		lin_ly_timeover.setOnClickListener(this);
		lin_ly_history.setOnClickListener(this);
		lin_ly_ckzb.setOnClickListener(this);
		lin_ly_ckzj.setOnClickListener(this);

		headView = LayoutInflater.from(context).inflate(R.layout.headview_fragment_play, null);
		lin_center = (RelativeLayout) headView.findViewById(R.id.lin_center);
		lin_tuijian = (LinearLayout) headView.findViewById(R.id.lin_tuijian);
		lin_comment = (LinearLayout) headView.findViewById(R.id.lin_comment);
		LinearLayout lin_more = (LinearLayout) headView.findViewById(R.id.lin_more);
		lin_more.setOnClickListener(this);
		lin_lukuangtts = (LinearLayout) headView.findViewById(R.id.lin_lukuangtts);
		lin_like = (LinearLayout) headView.findViewById(R.id.lin_like);
		img_like = (ImageView) headView.findViewById(R.id.img_like);
		tv_like = (TextView) headView.findViewById(R.id.tv_like);
		lin_right = (LinearLayout) headView.findViewById(R.id.lin_right);
		img_news = (ImageView) headView.findViewById(R.id.img_news);
		img_play = (ImageView) headView.findViewById(R.id.img_play);
		image_liu = (ImageView)headView.findViewById(R.id.image_liu);
		Bitmap bmp1 = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_bd);
		image_liu.setImageBitmap(bmp1);
		img_download= (ImageView) headView.findViewById(R.id.img_download);

		tv_details_flag=(TextView)headView.findViewById(R.id.tv_details_flag); // 展开或者隐藏按钮
        rv_details=(RelativeLayout)headView.findViewById(R.id.rv_details);    // 节目详情布局
		tv_like = (TextView) headView.findViewById(R.id.tv_like);
		tv_sequ=(TextView)headView.findViewById(R.id.tv_sequ);
		tv_desc=(TextView)headView.findViewById(R.id.tv_desc);

		tv_details_flag.setText("  显示  ");
		flowTag = (GridView)headView.findViewById(R.id.gv_tag);                    // 展示热门搜索词
	    testList=new ArrayList<>();
		testList.add("逻辑思维");
		testList.add("不是我不明白");
		testList.add("今天你吃饭了吗");
		testList.add("看世界");
		testList.add("影视资讯");
		flowTag.setAdapter(new SearchHotAdapter(context,testList));

		lin_left = (LinearLayout) headView.findViewById(R.id.lin_left);
		tv_name = (TextView) headView.findViewById(R.id.tv_name);
		seekBar = (SeekBar) headView.findViewById(R.id.seekBar);
		lin_share = (LinearLayout) headView.findViewById(R.id.lin_share);
		textTime = (TextView) headView.findViewById(R.id.text_time);
		tv_download=(TextView)headView.findViewById(R.id.tv_download);
		seekBar.setEnabled(false);
		// 配合seekBar使用的标签
		time_start = (TextView) headView.findViewById(R.id.time_start);
		time_end = (TextView) headView.findViewById(R.id.time_end);
		lin_VoiceSearch = (LinearLayout) headView.findViewById(R.id.lin_voicesearch); // 语音搜索
		rl_voice = (MyLinearLayout) rootView.findViewById(R.id.rl_voice);
		imageView_voice = (ImageView) rootView.findViewById(R.id.imageView_voice);
		imageView_voice.setImageBitmap(bmp);
		tv_cancel = (TextView) rootView.findViewById(R.id.tv_cancle);
		tv_speak_status = (TextView) rootView.findViewById(R.id.tv_speak_status);
		tv_speak_status.setText("请按住讲话");
		textSpeakContent = (TextView) rootView.findViewById(R.id.text_speak_content);
		mListView.addHeaderView(headView);
		lin_download = (LinearLayout) headView.findViewById(R.id.lin_download);// 下载
	}

	private void setListener() {

		lin_like.setOnClickListener(this);
		lin_left.setOnClickListener(this);
		lin_center.setOnClickListener(this);
		lin_lukuangtts.setOnClickListener(this);
		lin_comment.setOnClickListener(this);
		lin_right.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		lin_VoiceSearch.setOnClickListener(this);
		lin_download.setOnClickListener(this);
		lin_share.setOnClickListener(this);
		tv_details_flag.setOnClickListener(this);

		imageView_voice.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
							curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
							audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC,stepVolume, AudioManager.FLAG_PLAY_SOUND);
							voice_type = 1;
							mVoiceRecognizer=VoiceRecognizer.getInstance(context,BroadcastConstants.PLAYERVOICE);
							mVoiceRecognizer.startListen();
							tv_speak_status.setText("开始语音转换");
							imageView_voice.setImageBitmap(bmpPress);
							textSpeakContent.setVisibility(View.GONE);
						} else {
							ToastUtils.show_short(context, "网络失败，请检查网络");
						}
						break;
					case MotionEvent.ACTION_UP:
						audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC,curVolume, AudioManager.FLAG_PLAY_SOUND);
						voice_type = 2;
						mVoiceRecognizer.stopListen();
						imageView_voice.setImageBitmap(bmp);
						tv_speak_status.setText("请按住讲话");
						break;
				}
				return true;
			}
		});

		// seekBar事件
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
					if (playType != null &&playType != null && playType.equals("AUDIO")) {
						audioPlay.setTime((long) progress);
						mUIHandler.sendEmptyMessage(TIME_UI);
					}
				}
			}
		});
	}



	private static LanguageSearchInside getDaoList(Context context) {
		if (dbDao == null) {
			dbDao = new SearchPlayerHistoryDao(context);
		}
		List<PlayerHistory> historyDatabaseList = dbDao.queryHistory();
		if (historyDatabaseList != null && historyDatabaseList.size() > 0) {
			PlayerHistory historyNew =historyDatabaseList.get(0);
			historyNews = new LanguageSearchInside();
			historyNews.setType("1");
			historyNews.setContentURI(historyNew.getPlayerUrI());
			historyNews.setContentPersons(historyNew.getPlayerNum());
			historyNews.setContentKeyWord("");
			historyNews.setcTime("");
			historyNews.setContentSubjectWord("");
			historyNews.setContentTimes("");
			historyNews.setContentName(historyNew.getPlayerName());
			historyNews.setContentPubTime("");
			historyNews.setContentPub(historyNew.getPlayerFrom());
			historyNews.setContentPlay(historyNew.getPlayerUrl());
			historyNews.setMediaType(historyNew.getPlayerMediaType());
			historyNews.setContentId(historyNew.getContentID());
			historyNews.setContentDescn(historyNew.getPlayerContentDescn());
			historyNews.setContentImg(historyNew.getPlayerImage());
			try{
				if(historyNew.getPlayerAllTime().equals("")){
                historyNews.setPlayerAllTime("0");
            }else{
                historyNews.setPlayerAllTime(historyNew.getPlayerAllTime());
            }
			}catch (Exception e){
				e.printStackTrace();
			}

			try{
				if(historyNew.getPlayerInTime().equals("")){
					historyNews.setPlayerInTime("0");
				}else{
					historyNews.setPlayerInTime(historyNew.getPlayerInTime());
				}
			}catch (Exception e){

			}

			historyNews.setContentShareURL(historyNew.getPlayContentShareUrl());
			historyNews.setContentFavorite(historyNew.getContentFavorite());
			historyNews.setLocalurl(historyNew.getLocalurl());
			historyNews.setSequId(historyNew.getSequId());
			historyNews.setSequName(historyNew.getSequName());
			historyNews.setSequDesc(historyNew.getSequDesc());
			historyNews.setSequImg(historyNew.getSequImg());
		}
		return historyNews;
	}

	private void initDao() {// 初始化数据库命令执行对象
		dbDao = new SearchPlayerHistoryDao(context);
		FID = new FileInfoDao(context);
	}

	/**
	 * 把数据添加数据库
	 *
	 * @param languageSearchInside
	 */
	private static void addDb(LanguageSearchInside languageSearchInside) {
		String playerName = languageSearchInside.getContentName();
		String playerImage = languageSearchInside.getContentImg();
		String playerUrl = languageSearchInside.getContentPlay();
		String playerUrI = languageSearchInside.getContentURI();
		String playerMediaType = languageSearchInside.getMediaType();
		String playContentShareUrl = languageSearchInside.getContentShareURL();
		String playerAllTime = languageSearchInside.getPlayerAllTime();
		String playerInTime =languageSearchInside.getPlayerInTime();
		String playerContentDesc = languageSearchInside.getContentDescn();
		String playerNum = languageSearchInside.getPlayCount();
		String playerZanType = "false";
		String playerFrom = languageSearchInside.getContentPub();
		String playerFromId = "";
		String playerFromUrl = "";
		String playerAddTime = Long.toString(System.currentTimeMillis());
		String bjUserId = CommonUtils.getUserId(context);
		if(languageSearchInside.getContentFavorite()!=null){
			String contentFavorite=languageSearchInside.getContentFavorite();
			if(contentFavorite!=null){
				if(contentFavorite.equals("0")||contentFavorite.equals("1")){
					ContentFavorite=contentFavorite;
				}
			}
		}else {
			ContentFavorite = languageSearchInside.getContentFavorite();
		}
		String ContentID = languageSearchInside.getContentId();
		String localUrl = languageSearchInside.getLocalurl();
		String sequName = languageSearchInside.getSequName();
		String sequId = languageSearchInside.getSequId();
		String sequDesc = languageSearchInside.getSequDesc();
		String sequImg =languageSearchInside.getSequImg();

		PlayerHistory history = new PlayerHistory(playerName, playerImage,
				playerUrl, playerUrI, playerMediaType, playerAllTime,
				playerInTime, playerContentDesc, playerNum, playerZanType,
				playerFrom, playerFromId, playerFromUrl, playerAddTime,
				bjUserId, playContentShareUrl, ContentFavorite, ContentID, localUrl,sequName,sequId,sequDesc,sequImg);

		if (playerMediaType!= null && playerMediaType.trim().length() > 0&&playerMediaType.equals("TTS")) {
			dbDao.deleteHistoryById(ContentID);
		} else {
			dbDao.deleteHistory(playerUrl);
		}
		dbDao.addHistory(history);
	}

	private static void setItemListener() {
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				num = position - 2;
				getNetWork(num, context);
				stopCurrentTimer();
			}
		});
	}

	/**
	 * 按照界面排序号进行播放
	 *
	 * @param number
	 *            在play方法里初始化播放器对象 在musicPlay方法里执行相关操作 要考虑enterCenter方法
	 *
	 */
	protected static void play(int number) {
		if (allList != null && allList.get(number) != null&& allList.get(number).getMediaType() != null) {
			playType = allList.get(number).getMediaType();
			if (playType.equals("AUDIO") || playType.equals("RADIO")) {
				// 首先判断audioPlay是否为空
				// 如果为空，新建
				// 如果不为空 判断instance是否为当前播放 如果不是stop他后面再新建当前播放器的对象
				// 以下为实现播放器的方法
				if (audioPlay == null) {
					audioPlay = VlcPlayer.getInstance(context);
				} else {
					// 不为空
					if (audioPlay.mark().equals("TTS")) {
						audioPlay.stop();
					}
					audioPlay = VlcPlayer.getInstance(context);
				}
				if (allList.get(number).getContentPlay() != null) {
					img_play.setImageResource(R.mipmap.wt_play_play);
					if (allList.get(number).getContentName() != null) {
						tv_name.setText(allList.get(number).getContentName());
					} else {
						tv_name.setText("我听科技");
					}
					if (allList.get(number).getContentImg() != null) {
						String url;
						if (allList.get(number).getContentImg().startsWith("http")) {
							url = allList.get(number).getContentImg();
						} else {
							url = GlobalConfig.imageurl+ allList.get(number).getContentImg();
						}
						Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(img_news);
					} else {
						Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
						img_news.setImageBitmap(bmp);
					}
					for (int i = 0; i < allList.size(); i++) {
						allList.get(i).setType("1");
					}
					allList.get(number).setType("2");
					adapter.notifyDataSetChanged();
					if (allList.get(number).getLocalurl() != null) {
						musicPlay("file:///"+ allList.get(number).getLocalurl());
						ToastUtils.show_allways(context, "正在播放本地内容");
					} else {
						musicPlay(allList.get(number).getContentPlay());
					}
					GlobalConfig.playerobject = allList.get(number);
					resetHeadView();
					num = number;
				} else {
					ToastUtils.show_short(context, "暂不支持播放");
				}
			   } else if (playType.equals("TTS")) {
				if (allList.get(number).getContentURI() != null&& allList.get(number).getContentURI().trim().length() > 0) {
					if (audioPlay == null) {
						audioPlay = TtsPlayer.getInstance(context);
					} else {
						// 不为空
						if (audioPlay.mark().equals("VLC")) {
							audioPlay.stop();
						}
						audioPlay = TtsPlayer.getInstance(context);
					}
					img_play.setImageResource(R.mipmap.wt_play_play);
					if (allList.get(number).getContentName() != null) {
						tv_name.setText(allList.get(number).getContentName());
					} else {
						tv_name.setText("我听科技");
					}
					if (allList.get(number).getContentImg() != null) {
						String url;
						if (allList.get(number).getContentImg().startsWith("http")) {
							url = allList.get(number).getContentImg();
						} else {
							url = GlobalConfig.imageurl + allList.get(number).getContentImg();
						}
						Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(img_news);
					} else {
						Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
						img_news.setImageBitmap(bmp);
					}
					for (int i = 0; i < allList.size(); i++) {
						allList.get(i).setType("1");
					}
					allList.get(number).setType("2");
					adapter.notifyDataSetChanged();
					musicPlay(allList.get(number).getContentURI());
					GlobalConfig.playerobject = allList.get(number);
					resetHeadView();
					num = number;
				} else {
					getContentNews(allList.get(number).getContentId(), number);// 当contenturi为空时 获取内容
				}
			}
		}
	}

	private static void getNetWork(int number, Context context) {
		String wifiSet = sp.getString(StringConstant.WIFISET, "true"); // 是否开启网络流量提醒
		String wifiShow = sp.getString(StringConstant.WIFISHOW, "true");// 是否网络弹出框提醒
		if (wifiShow != null && !wifiShow.trim().equals("") && wifiShow.equals("true")) {
			if (wifiSet != null && !wifiSet.trim().equals("") && wifiSet.equals("true")) {
				// 开启网络播放数据连接提醒
				CommonHelper.checkNetworkStatus(context);// 网络设置获取
				GlobalConfig.playerobject=allList.get(number);
				//decideUpdatePlayHistory();
				addDb(allList.get(number));
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == 1) {
						play(number);
						num = number;
					} else {
						num = number;
						wifiDialog.show();
					}
				} else { // 07/28 没有网络情况下还有可能播放本地文件
					if (allList.get(number).getLocalurl() != null) {
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

 /*   //由此方法决定是否需要获取进度
	private static void decideUpdatePlayHistory() {
		String playUrlNow=GlobalConfig.playerobject.getContentPlay();
		if(playUrlLast==null){
		   playUrlLast=playUrlNow;
		}else{
			if(playUrlLast.equals(playUrlNow)){

			}else{
				if(audioPlay.mark().equals("AUDIO")){
					dbDao.updatePlayerIntime(playUrlLast,audioPlay.getTime());
				}else{

				}
			}
		}
	}*/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.lin_lukuangtts:
				if (audioPlay == null) {
					audioPlay = TtsPlayer.getInstance(context);
				} else {
					// 不为空
					if (audioPlay.mark().equals("VLC")) {
						audioPlay.pause();
					}
					audioPlay = TtsPlayer.getInstance(context);
				}
				ToastUtils.show_allways(context, "点击了路况TTS按钮");
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					dialogs = DialogUtils.Dialogph(context, "通讯中");
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
				ShareDialog.show();
				break;
			case R.id.lin_like:
				if (GlobalConfig.playerobject.getContentFavorite() != null
						&& !GlobalConfig.playerobject.getContentFavorite().equals("")) {
					sendFavorite();
				} else {
					ToastUtils.show_allways(context, "本节目信息获取有误，暂时不支持喜欢");
				}
				break;
			case R.id.lin_left:
				playLast();
				break;
			case R.id.tv_details_flag:
			    if(details_flag==false){
					details_flag=true;
					tv_details_flag.setText("  隐藏  ");
					rv_details.setVisibility(View.VISIBLE);
				}else{
					details_flag=false;
					tv_details_flag.setText("  显示  ");
					rv_details.setVisibility(View.GONE);
				}

				break;
			case R.id.lin_center:
				enterCenter();
				stopCurrentTimer();
				break;
			case R.id.lin_right:
				if (allList != null && allList.size() > 0) {
					if (num + 1 < allList.size()) {
						num = num + 1;
						getNetWork(num, context);
					} else {
						num = 0;
						getNetWork(num, context);
					}
					stopCurrentTimer();
				}
				break;
			case R.id.lin_more:
				if(lin_chose.getVisibility()==View.VISIBLE){
					linChoseClose();
				}else{
					linChoseOpen();
				}
			break;
			case R.id.tv_ly_qx:
				linChoseClose();
				break;
			case R.id.lin_other:
				linChoseClose();
				break;
			case R.id.lin_ly_timeover:
				linChoseClose();
				startActivity(new Intent(context, TimerPowerOffActivity.class));
				break;
			case R.id.lin_ly_history:
				linChoseClose();
				startActivity(new Intent(context, PlayHistoryActivity.class));
				break;
			case R.id.lin_ly_ckzb:
				linChoseClose();
				ToastUtils.show_allways(context,"查看主播");
				break;
			case R.id.lin_ly_ckzj:
				linChoseClose();
				if(GlobalConfig.playerobject.getSequId()!=null){
					Intent intent =new Intent(context, AlbumActivity.class);
					intent.putExtra("type","player");
					Bundle bundle=new Bundle();
					bundle.putSerializable("list",GlobalConfig.playerobject);
                    intent.putExtras(bundle);
					startActivity(intent);
				}else{
					ToastUtils.show_allways(context,"本节目没有所属专辑");
				}

			/*	ToastUtils.show_allways(context,"查看专辑");*/
				break;
			case R.id.lin_comment:
				if(!TextUtils.isEmpty(GlobalConfig.playerobject.getContentId())&&!TextUtils.isEmpty(GlobalConfig.playerobject.getMediaType())){
				Intent intent=new Intent(context, CommentActivity.class);
				intent.putExtra("contentId",GlobalConfig.playerobject.getContentId());
				intent.putExtra("MediaType",GlobalConfig.playerobject.getMediaType());
				startActivity(intent);
				}else{
					ToastUtils.show_allways(context,"当前播放的节目的信息有误，无法获取评论列表");
				}
				break;
			case R.id.lin_download:
				if (GlobalConfig.playerobject != null) {
					if (GlobalConfig.playerobject.getMediaType().equals("AUDIO")) {
						// 此处执行将当前播放任务加到数据库的操作
						LanguageSearchInside data = GlobalConfig.playerobject;
						if (data.getLocalurl() != null) {
							ToastUtils.show_allways(context, "此节目已经保存到本地，请到已下载界面查看");
							return;
						}
						// 对数据进行转换
						List<ContentInfo> dataList = new ArrayList<>();
						ContentInfo mcontent = new ContentInfo();
						mcontent.setAuthor(data.getContentPersons());
						mcontent.setContentPlay(data.getContentPlay());
						mcontent.setContentImg(data.getContentImg());
						mcontent.setContentName(data.getContentName());
						mcontent.setUserid(CommonUtils.getUserId(context));
						mcontent.setDownloadtype("0");
						if (data.getSeqInfo() == null
								|| data.getSeqInfo().getContentName() == null
								|| data.getSeqInfo().getContentName().equals("")) {
							mcontent.setSequname(data.getContentName());
						} else {
							mcontent.setSequname(data.getSeqInfo().getContentName());
						}
						if (data.getSeqInfo() == null
								|| data.getSeqInfo().getContentId() == null
								|| data.getSeqInfo().getContentId().equals("")) {
							mcontent.setSequid(data.getContentId());
						} else {
							mcontent.setSequid(data.getSeqInfo().getContentId());
						}
						if (data.getSeqInfo() == null
								|| data.getSeqInfo().getContentImg() == null
								|| data.getSeqInfo().getContentImg().equals("")) {
							mcontent.setSequimgurl(data.getContentImg());
						} else {
							mcontent.setSequimgurl(data.getSeqInfo().getContentImg());
						}
						if (data.getSeqInfo() == null
								|| data.getSeqInfo().getContentDesc() == null
								|| data.getSeqInfo().getContentDesc().equals("")) {
							mcontent.setSequdesc(data.getContentDescn());
						} else {
							mcontent.setSequdesc(data.getSeqInfo().getContentDesc());
						}
						dataList.add(mcontent);
						// 检查是否重复,如果不重复插入数据库，并且开始下载，重复了提示
						List<FileInfo> fileDataList = FID.queryFileInfoAll(CommonUtils.getUserId(context));
						if (fileDataList.size() != 0) {
							/**
							 * 此时有下载数据
							 */
							boolean isDownload = false;
							for (int j = 0; j < fileDataList.size(); j++) {
								if (fileDataList.get(j).getUrl().equals(mcontent.getContentPlay())) {
									isDownload = true;
									break;
								} else {
									isDownload = false;
								}
							}
							if (isDownload) {
								ToastUtils.show_allways(context,mcontent.getContentName() + "已经存在于下载列表");
							} else {
								FID.insertFileInfo(dataList);
								ToastUtils.show_allways(context,mcontent.getContentName() + "已经插入了下载列表");
								// 未下载列表
								List<FileInfo> fileUnDownLoadList = FID.queryFileInfo("false",CommonUtils.getUserId(context));
								for (int kk = 0; kk < fileUnDownLoadList.size(); kk++) {
									if (fileUnDownLoadList.get(kk).getDownloadtype() == 1) {
										DownloadService.workStop(fileUnDownLoadList.get(kk));
										FID.updataDownloadStatus(fileUnDownLoadList.get(kk).getUrl(), "2");
									}
								}

								for (int k = 0; k < fileUnDownLoadList.size(); k++) {
									if (fileUnDownLoadList.get(k).getUrl().equals(mcontent.getContentPlay())) {
										FileInfo file = fileUnDownLoadList.get(k);
										FID.updataDownloadStatus(mcontent.getContentPlay(), "1");
										DownloadService.workStart(file);
										Intent p_intent = new Intent("push_down_uncompleted");
										context.sendBroadcast(p_intent);
										break;
									}
								}
							}
						} else {
							/**
							 * 此时库里没数据
							 */
							FID.insertFileInfo(dataList);
							ToastUtils.show_allways(context,mcontent.getContentName() + "已经插入了下载列表");
							// 未下载列表
							List<FileInfo> fileUnDownloadList = FID.queryFileInfo("false", CommonUtils.getUserId(context));
							for (int k = 0; k < fileUnDownloadList.size(); k++) {
								if (fileUnDownloadList.get(k).getUrl().equals(mcontent.getContentPlay())) {
									FileInfo file = fileUnDownloadList.get(k);
									FID.updataDownloadStatus(mcontent.getContentPlay(), "1");
									DownloadService.workStart(file);
									Intent p_intent = new Intent("push_down_uncompleted");
									context.sendBroadcast(p_intent);
									break;
								}
							}
						}
					} else {
						ToastUtils.show_allways(context, "您现在播放的节目，目前不支持下载");
					}
				} else {
					ToastUtils.show_allways(context, "当前播放器播放对象为空");
				}
				break;
		}
	}

	private void linChoseClose() {
		Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.umeng_socialize_slide_out_from_bottom);
		lin_chose.setAnimation(mAnimation);
		lin_chose.setVisibility(View.GONE);
	}

	private void linChoseOpen() {
		Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.umeng_socialize_slide_in_from_bottom);
		lin_chose.setAnimation(mAnimation);
		lin_chose.setVisibility(View.VISIBLE);
	}

	public  static void playLast() {
		if (num - 1 >= 0) {
			num = num - 1;
			getNetWork(num, context);
			stopCurrentTimer();
		} else {
			ToastUtils.show_allways(context, "已经是第一条数据了");
		}

	}

	private void getLuKuangTTS() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);

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
					img_news.setImageResource(R.mipmap.wt_icon_lktts);
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
			playType = GlobalConfig.playerobject.getMediaType();
			if (playType.equals("AUDIO") || playType.equals("RADIO")) {
				// 首先判断audioPlay是否为空
				// 如果为空，新建
				// 如果不为空 判断instance是否为当前播放 如果不是stop他后面再新建当前播放器的对象
				// 以下为实现播放器的方法
				if (audioPlay == null) {
					audioPlay = VlcPlayer.getInstance(context);
				} else {
					// 不为空
					if (audioPlay.mark().equals("TTS")) {
						audioPlay.stop();
					}
					audioPlay = VlcPlayer.getInstance(context);
				}
				if (GlobalConfig.playerobject.getContentPlay() != null) {
					if (GlobalConfig.playerobject != null) {
						tv_name.setText(GlobalConfig.playerobject.getContentName());
					} else {
						tv_name.setText("我听科技");
					}
					if (GlobalConfig.playerobject.getContentImg() != null) {
						String url;
						if (GlobalConfig.playerobject.getContentImg().startsWith("http")) {
							url = GlobalConfig.playerobject.getContentImg();
						} else {
							url = GlobalConfig.imageurl+ GlobalConfig.playerobject.getContentImg();
						}
						Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(img_news);
					} else {
						Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
						img_news.setImageBitmap(bmp);
					}
					for (int i = 0; i < allList.size(); i++) {
						allList.get(i).setType("1");
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
			} else if (playType.equals("TTS")) {
				if (GlobalConfig.playerobject.getContentURI() != null && GlobalConfig.playerobject.getContentURI().trim().length() > 0) {
					if (audioPlay == null) {
						audioPlay = TtsPlayer.getInstance(context);
					} else {
						// 不为空
						if (audioPlay.mark().equals("VLC")) {
							audioPlay.stop();
						}
						audioPlay = TtsPlayer.getInstance(context);
					}

					if (GlobalConfig.playerobject.getContentName() != null) {
						tv_name.setText(GlobalConfig.playerobject.getContentName());
					} else {
						tv_name.setText("我听科技");
					}
					if (GlobalConfig.playerobject.getContentImg() != null) {
						String url;
						if (GlobalConfig.playerobject.getContentImg().startsWith("http")) {
							url = GlobalConfig.playerobject.getContentImg();
						} else {
							url = GlobalConfig.imageurl+ GlobalConfig.playerobject.getContentImg();
						}
						Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(img_news);
					} else {
						Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
						img_news.setImageBitmap(bmp);
					}
					for (int i = 0; i < allList.size(); i++) {
						allList.get(i).setType("1");
					}
					GlobalConfig.playerobject.setType("2");
					adapter.notifyDataSetChanged();
					musicPlay(GlobalConfig.playerobject.getContentURI());
					resetHeadView();
				}else{
					getContentNews(GlobalConfig.playerobject.getContentId(), 0);// 当contenturi为空时 获取内容
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
				if (sendType == 1) {
					mListView.setPullLoadEnable(false);
					RefreshType = 1;
					page = 1;
					firstSend();
				}
			}
		}, 1000);
	}

	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (sendType == 1) {
					RefreshType = 2;
					firstSend();
				}else if(sendType==2){
					RefreshType = 2;
					SendTextRequest(searchName,context);
				}else if(sendType==3){
					RefreshType =3;
					searchByVoice(voiceStr);
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
			    TextPage=1;
				SendTextRequest(news, context);
				Editor et = sp.edit();
				et.putString(StringConstant.PLAYHISTORYENTER, "false");
				et.commit();
			} else {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					dialogs = DialogUtils.Dialogph(context, "通讯中");
					firstSend();// 搜索第一次数据
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
	}


	/**
	 * 广播接收器
	 */
	class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastConstants.TIMER_UPDATE)) {
				String s = intent.getStringExtra("update");
				if (textTime != null) {
					textTime.setText(s);
				}
			} else if (action.equals(BroadcastConstants.TIMER_STOP)) {
				if (textTime != null) {
					textTime.setText("定时");
				}
			} else if (action.equals(BroadcastConstants.PLAYERVOICE)) {
				voiceStr = intent.getStringExtra("VoiceContent");

				tv_speak_status.setText("正在为您查找: "+voiceStr);
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					if(!voiceStr.trim().equals("")){
						tv_speak_status.setText("正在搜索: "+voiceStr);
						VoicePage=1;
						searchByVoice(voiceStr);
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
		if (num + 1 < allList.size()) {
			// 此时自动播放下一首
			num = num + 1;
			getNetWork(num, context);
		} else {
			// 全部播放完毕了
			num = 0;
			getNetWork(num, context);
		}
	}

	static Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case TIME_UI: // 更新进度及时间
				 if (GlobalConfig.playerobject != null&& GlobalConfig.playerobject.getMediaType() != null
							&& GlobalConfig.playerobject.getMediaType().trim().length() > 0
							&& GlobalConfig.playerobject.getMediaType().equals("AUDIO")) {
					 if(PlayFlag==true){
					 if(GlobalConfig.playerobject.getPlayerInTime()!=null&&
							 !GlobalConfig.playerobject.getPlayerInTime().equals("")){
						try {
							audioPlay.setTime(Long.valueOf(GlobalConfig.playerobject.getPlayerInTime()));
						}catch (Exception e){
							e.printStackTrace();
						}
						 currPosition = audioPlay.getTime();
						 PlayFlag=false;
					 }else{
						 currPosition = audioPlay.getTime();
						 PlayFlag=false;
					 }
					 }else{
						 currPosition = audioPlay.getTime();
					 }
						long duration = audioPlay.getTotalTime();
						updateTextViewWithTimeFormat(time_start,(int) (currPosition / 1000));
						updateTextViewWithTimeFormat(time_end,(int) (duration / 1000));
						seekBar.setMax((int) duration);
						timerService = (int) (duration - currPosition);
						if (audioPlay.isPlaying()) {
							seekBar.setProgress((int) currPosition);
						}
					   dbDao.updatePlayerInTime(GlobalConfig.playerobject.getContentPlay(),currPosition,duration);
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
					}
					mUIHandler.sendEmptyMessageDelayed(TIME_UI, 1000);
					break;
				case VOICE_UI:
					rl_voice.setVisibility(View.GONE);
					textSpeakContent.setVisibility(View.GONE);
					tv_speak_status.setText("请按住讲话");
					break;
				case PLAY:
					audioPlay.play(local);
					break;
				case PAUSE:
					audioPlay.pause();
					break;
				case CONTINUE:
					audioPlay.continuePlay();
					break;
				case STOP:
					audioPlay.stop();
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

	static String local;
	private static void musicPlay(String s) {
		if (local == null) {
			local = s;
			mUIHandler.sendEmptyMessage(PLAY);
			img_play.setImageResource(R.mipmap.wt_play_play);
			setPlayingType();
			PlayFlag=true;
		} else {
			// 不等于空
			if (local.equals(s)) {
				// 里面可以根据播放类型判断继续播放或者停止
				if(playType.equals("TTS")){
					if (audioPlay.isPlaying()) {
						// 播放状态，对应暂停方法，播放图
						audioPlay.stop();
						img_play.setImageResource(R.mipmap.wt_play_stop);
						setPauseType();
					} else {
						local = s;
						mUIHandler.sendEmptyMessage(PLAY);
						img_play.setImageResource(R.mipmap.wt_play_play);
						setPlayingType();
						PlayFlag=true;
					}
				}else{
				if (audioPlay.isPlaying()) {
					// 播放状态，对应暂停方法，播放图
					audioPlay.pause();
					if (playType.equals("AUDIO")) {
						mUIHandler.removeMessages(TIME_UI);
					}
					img_play.setImageResource(R.mipmap.wt_play_stop);
					setPauseType();
				} else {
					// 暂停状态，对应播放方法，暂停图
					audioPlay.continuePlay();
					img_play.setImageResource(R.mipmap.wt_play_play);
					setPlayingType();
				}
				}
			} else {
				local = s;
				mUIHandler.sendEmptyMessage(PLAY);
				img_play.setImageResource(R.mipmap.wt_play_play);
				setPlayingType();
				PlayFlag=true;
			}
		}

		if (playType != null && playType.trim().length() > 0 && playType.equals("AUDIO")) {
			seekBar.setEnabled(true);
			mUIHandler.sendEmptyMessage(TIME_UI);
		} else {
			seekBar.setEnabled(false);
			mUIHandler.sendEmptyMessage(TIME_UI);
		}
	}

	// 分享模块//
	private void ShareDialog() {
		final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sharedialog, null);
		HorizontalListView mGallery = (HorizontalListView) dialog.findViewById(R.id.share_gallery);
		TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancle);
		ShareDialog = new Dialog(context, R.style.MyDialog);
		// 从底部上升到一个位置
		ShareDialog.setContentView(dialog);
		Window window = ShareDialog.getWindow();
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		ViewGroup.LayoutParams params = dialog.getLayoutParams();
		params.width = screenWidth;
		dialog.setLayoutParams(params);
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.sharestyle);
		ShareDialog.setCanceledOnTouchOutside(true);
		ShareDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		dialog1 = DialogUtils.Dialogphnoshow(context, "通讯中", dialog1);
		Config.dialog = dialog1;
		final List<ShareModel> mList = ShareUtils.getShareModelList();
		ImageAdapter shareAdapter = new ImageAdapter(context, mList);
		mGallery.setAdapter(shareAdapter);
		mGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SHARE_MEDIA Platform = mList.get(position).getSharePlatform();
				CallShare(Platform);
				ShareDialog.dismiss();
			}
		});
		tv_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ShareDialog.isShowing()) {
					ShareDialog.dismiss();
				}
			}
		});
	}

	protected void CallShare(SHARE_MEDIA Platform) {
		String shareName;
		String shareDesc;
		String shareContentImg;
		String shareUrl;
		if (GlobalConfig.playerobject != null) {
			if (GlobalConfig.playerobject.getContentName() != null
					&& !GlobalConfig.playerobject.getContentName().equals("")) {
				shareName = GlobalConfig.playerobject.getContentName();
			} else {
				shareName = "我听我享听";
			}
			if (GlobalConfig.playerobject.getContentDescn() != null
					&& !GlobalConfig.playerobject.getContentDescn().equals("")) {
				shareDesc = GlobalConfig.playerobject.getContentDescn();
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
				shareUrl = GlobalConfig.playerobject.getContentShareURL();
			} else {
				shareUrl = "http://www.wotingfm.com/";
			}
			new ShareAction(context).setPlatform(Platform).withMedia(image)
					.withText(shareDesc).withTitle(shareName).withTargetUrl(shareUrl).share();
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
	private void searchByVoice(String str) {
		sendType = 3;
		// 发送数据
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("SearchStr", str);
			jsonObject.put("PageType", "0");
			jsonObject.put("Page",VoicePage);
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
						list.get(0).getContentDescn();
						if (list != null && list.size() != 0) {
							if(VoicePage==1){
								num = 0;
								allList.clear();
								allList.addAll(list);
								//decideUpdatePlayHistory();
								lin_tuijian.setVisibility(View.VISIBLE);
							}else{
								allList.addAll(list);
							}
							if(adapter==null){
								adapter = new PlayerListAdapter(context, allList);
								mListView.setAdapter(adapter);
							}else{
								adapter.notifyDataSetChanged();
							}
							GlobalConfig.playerobject=allList.get(0);
							stopCurrentTimer();
							getNetWork(0, context);
							VoicePage++;
							setItemListener();
							mListView.setPullRefreshEnable(false);
							mListView.setPullLoadEnable(true);
							mListView.stopRefresh();
							mListView.stopLoadMore();
							mListView.setRefreshTime(new Date().toLocaleString());
						}
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}

				} else if (ReturnType.equals("1011")) {
					mListView.stopLoadMore();
					mListView.setPullLoadEnable(false);
					ToastUtils.show_allways(context,"已经没有相关数据啦");
				} else {
					ToastUtils.show_allways(context,"已经没有相关数据啦");
					mListView.stopLoadMore();
					mListView.setPullLoadEnable(false);
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

	// wifi弹出框//
	private void WifiDialog() {
		final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_wifi_set, null);
		TextView tv_over = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_first = (TextView) dialog1.findViewById(R.id.tv_first);
		TextView tv_all = (TextView) dialog1.findViewById(R.id.tv_all);
		wifiDialog = new Dialog(context, R.style.MyDialog);
		wifiDialog.setContentView(dialog1);
		wifiDialog.setCanceledOnTouchOutside(true);
		wifiDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_over.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wifiDialog.dismiss();
			}
		});
		tv_first.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play(num);
				wifiDialog.dismiss();
			}
		});
		tv_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play(num);
				Editor et = sp.edit();
				et.putString(StringConstant.WIFISHOW, "false");
				et.commit();
				wifiDialog.dismiss();
			}
		});
	}

	/**
	 * 第一次进入该界面时候的数据
	 */
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
				if (ReturnType.equals("1001")) {
					page++;
					try {
						String List = result.getString("ResultList");
						JSONTokener jsonParser = new JSONTokener(List);
						JSONObject arg1 = (JSONObject) jsonParser.nextValue();
						MainList = arg1.getString("List");
						ArrayList<LanguageSearchInside> list = new Gson().fromJson(
								MainList, new TypeToken<List<LanguageSearchInside>>() {}.getType());
						if (RefreshType == 0) {
							// 得到数据库里边的第一条数据
							LanguageSearchInside fList =getDaoList(context);
							// 第一次进入该界面获取数据
							if (list != null && list.size() > 0) {
								// 此时有返回数据
								if (fList != null) {
									// 此时数据库里边的数据为空
									num = -1;
									setData(fList, list);
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
								if (fList != null) {
									list.add(fList);
									num = -1;
									setData(fList, list);
								} else {
									// 此时没有任何数据
									num = -2;
									mListView.setPullRefreshEnable(true);
									mListView.setPullLoadEnable(false);
									mListView.stopRefresh();
								}
							}
						} else if (RefreshType == 1) {
							// 下拉刷新--------暂未使用，注意：不要删除该段代码
							allList.clear();
							allList.addAll(list);
							if (GlobalConfig.playerobject != null && allList != null) {
								for (int i = 0; i < allList.size(); i++) {
									if (allList.get(i).getContentPlay().equals(GlobalConfig.playerobject.getContentPlay())) {
										allList.get(i).setType("0");
										num = i;
									}
								}
							}
							lin_tuijian.setVisibility(View.VISIBLE);
							adapter = new PlayerListAdapter(context, allList);
							mListView.setAdapter(adapter);
							setItemListener();
							mListView.setPullRefreshEnable(false);
							mListView.setPullLoadEnable(true);
							mListView.stopRefresh();
						} else {
							// 加载更多
							mListView.stopLoadMore();
							allList.addAll(list);
							adapter.notifyDataSetChanged();
							setItemListener();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						if (dialogs != null) {
							dialogs.dismiss();
						}
						allList.clear();
						lin_tuijian.setVisibility(View.GONE);
						adapter = new PlayerListAdapter(context, allList);
						mListView.setAdapter(adapter);
						mListView.setPullRefreshEnable(true);
						mListView.setPullLoadEnable(false);
						mListView.stopRefresh();
						mListView.stopLoadMore();
					}
				} else {
					if (dialogs != null) {
						dialogs.dismiss();
					}
					allList.clear();
					lin_tuijian.setVisibility(View.GONE);
					adapter = new PlayerListAdapter(context, allList);
					mListView.setAdapter(adapter);
					mListView.setPullRefreshEnable(true);
					mListView.setPullLoadEnable(false);
					mListView.stopRefresh();
					mListView.stopLoadMore();
				}
				resetHeadView();
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialogs != null) {
					dialogs.dismiss();
				}
				allList.clear();
				lin_tuijian.setVisibility(View.VISIBLE);
				adapter = new PlayerListAdapter(context, allList);
				mListView.setAdapter(adapter);
				mListView.setPullLoadEnable(false);
				mListView.stopRefresh();
				mListView.stopLoadMore();
				mListView.setRefreshTime(new Date().toLocaleString());
			}
		});
	}

	protected void setData(LanguageSearchInside fList, ArrayList<LanguageSearchInside> list) {
		// 如果数据库里边的数据不是空的，在headView设置该数据
		GlobalConfig.playerobject = fList;
		resetHeadView();
		if (fList.getContentName() != null) {
			tv_name.setText(fList.getContentName());
		} else {
			tv_name.setText("未知数据");
		}
        //如果进来就要看到 在这里设置界面
		if(!TextUtils.isEmpty(GlobalConfig.playerobject.getPlayerInTime())&&
				!TextUtils.isEmpty(GlobalConfig.playerobject.getPlayerAllTime())
				&&!GlobalConfig.playerobject.getPlayerInTime().equals("null")&&!GlobalConfig.playerobject.getPlayerAllTime().equals("null")){
			long current=Long.valueOf(GlobalConfig.playerobject.getPlayerInTime());
			long duration =Long.valueOf(GlobalConfig.playerobject.getPlayerAllTime());
			updateTextViewWithTimeFormat(time_start,(int) ( current / 1000));
			updateTextViewWithTimeFormat(time_end,(int) (duration / 1000));
			seekBar.setMax((int) duration);
			seekBar.setProgress((int)current);
		}else{
			time_start.setText("00:00:00");
			time_end.setText("00:00:00");
		}

		if (fList.getContentImg() != null) {
			String url;
			if (fList.getContentImg().startsWith("http")) {
				url = fList.getContentImg();
			} else {
				url = GlobalConfig.imageurl + fList.getContentImg();
			}
			Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(img_news);
		} else {
			Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
			img_news.setImageBitmap(bmp);
		}
		allList.clear();
		allList.addAll(list);
		if (GlobalConfig.playerobject != null && allList != null) {
			for (int i = 0; i < allList.size(); i++) {
				String s = allList.get(i).getContentPlay();
				if (s != null) {
					if (s.equals(GlobalConfig.playerobject.getContentPlay())) {
						allList.get(i).setType("0");
						num = i;
					}
				}
			}
		}
		lin_tuijian.setVisibility(View.VISIBLE);
		adapter = new PlayerListAdapter(context, allList);
		mListView.setAdapter(adapter);
		setItemListener();
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(true);
		mListView.stopRefresh();
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
			Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(img_news);
		} else {
			Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
			img_news.setImageBitmap(bmp);
		}
		allList.clear();
		allList.addAll(list);
		allList.get(0).setType("0");
		num = 0;
		lin_tuijian.setVisibility(View.VISIBLE);
		adapter = new PlayerListAdapter(context, allList);
		mListView.setAdapter(adapter);
		setItemListener();
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(true);
		mListView.stopRefresh();
	}

	protected static void resetHeadView() {
		if (GlobalConfig.playerobject != null) {
			//判断下载类型的方法
			if (GlobalConfig.playerobject.getMediaType().equals("AUDIO")) {

				img_download.setImageResource(R.mipmap.wt_play_xiazai);
				tv_download.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
			}else{
				img_download.setImageResource(R.mipmap.wt_play_xiazai_no);
				tv_download.setTextColor(context.getResources().getColor(R.color.gray));
			}

			if(GlobalConfig.playerobject.getSequName()!=null){
				tv_sequ.setText(GlobalConfig.playerobject.getSequName());
			}else{
				tv_sequ.setText("暂无专辑");
			}
			if(GlobalConfig.playerobject.getContentDescn()!=null){
				tv_desc.setText(GlobalConfig.playerobject.getContentDescn());
			}else{
				tv_desc.setText("暂无介绍");
			}
			if (GlobalConfig.playerobject.getContentFavorite() != null
					&& !GlobalConfig.playerobject.equals("")) {

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
			}
		} else {
               ToastUtils.show_allways(context,"播放器数据获取异常，请退出程序后尝试");
		}
	}

	// 文字请求
	public static void SendTextRequest(String contentName, final Context mContext) {

		final LanguageSearchInside fList =getDaoList(mContext);// 得到数据库里边的第一条数据
		// 发送数据
		sendType = 2;
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("SearchStr", contentName);
			jsonObject.put("PageType", "0");
			jsonObject.put("Page",TextPage);
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
					mListView.setPullLoadEnable(false);
				}
				if (ReturnType.equals("1001")) {
					try {
						LanguageSearch lists = new Gson().fromJson(MainList, new TypeToken<LanguageSearch>() {}.getType());
						List<LanguageSearchInside> list = lists.getList();
						if (list != null && list.size() != 0) {
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).getContentPlay() != null
										&& !list.get(i).getContentPlay().equals("null")
										&& !list.get(i).getContentPlay().equals("")
										&& list.get(i).getContentPlay().equals(fList.getContentPlay())) {
									list.remove(i);
								}
							}
					       if(TextPage==1){
							num = 0;
							allList.clear();
							   if(fList!=null&&!fList.equals("")) {
								   allList.add(fList);
							   }
							allList.addAll(list);
							GlobalConfig.playerobject=allList.get(num);
							//decideUpdatePlayHistory();
							lin_tuijian.setVisibility(View.VISIBLE);
						   }else{
							   allList.addAll(list);
						   }
							if(adapter==null){
							adapter = new PlayerListAdapter(context, allList);
							mListView.setAdapter(adapter);
							}else{
								adapter.notifyDataSetChanged();
							}
							stopCurrentTimer();
							getNetWork(0, context);
							TextPage++;
							setItemListener();
							mListView.setPullRefreshEnable(false);
							mListView.setPullLoadEnable(true);
							mListView.stopRefresh();
							mListView.stopLoadMore();
							mListView.setRefreshTime(new Date().toLocaleString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (ReturnType.equals("1011")) {
					ToastUtils.show_allways(context, "已经没有相关数据啦");
					mListView.stopLoadMore();
					mListView.setPullLoadEnable(false);
				} else {
					ToastUtils.show_allways(context, "已经没有相关数据啦");
					mListView.stopLoadMore();
					mListView.setPullLoadEnable(false);
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialogs != null) {
					dialogs.dismiss();
					mListView.stopLoadMore();
					mListView.setPullLoadEnable(false);
				}
			}
		});
	}

	private static void getContentNews(String id, final int number) {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
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
							if (audioPlay == null) {
								audioPlay = TtsPlayer.getInstance(context);
							} else {
								// 不为空
								if (audioPlay.mark().equals("VLC")) {
									audioPlay.stop();
								}
								audioPlay = TtsPlayer.getInstance(context);
							}
							img_play.setImageResource(R.mipmap.wt_play_play);
							if (allList.get(number).getContentName() != null) {
								tv_name.setText(allList.get(number).getContentName());
							} else {
								tv_name.setText("我听科技");
							}
							if (allList.get(number).getContentImg() != null) {
								String url;
								if (allList.get(number).getContentImg().startsWith("http")) {
									url = allList.get(number).getContentImg();
								} else {
									url = GlobalConfig.imageurl + allList.get(number).getContentImg();
								}
								Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(img_news);
							} else {
								Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
								img_news.setImageBitmap(bmp);
							}
							for (int i = 0; i < allList.size(); i++) {
								allList.get(i).setType("1");
							}
							allList.get(number).setType("2");
							adapter.notifyDataSetChanged();
							GlobalConfig.playerobject = allList.get(number);
							musicPlay(ContentURI);
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

	private static void sendFavorite() {
		dialogs = DialogUtils.Dialogph(context, "通讯中");
		JSONObject jsonObject =VolleyRequest.getJsonObject(context);
		try {
			// MediaType
			jsonObject.put("MediaType", GlobalConfig.playerobject.getMediaType());
			jsonObject.put("ContentId", GlobalConfig.playerobject.getContentId());
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
							for (int i = 0; i < allList.size(); i++) {
								if (allList.get(i).getContentURI().equals(GlobalConfig.playerobject.getContentURI())) {
									GlobalConfig.playerobject.setContentFavorite("1");
								}
							}
						} else {
							tv_like.setText("喜欢");
							img_like.setImageResource(R.mipmap.wt_dianzan_nomal);
							GlobalConfig.playerobject.setContentFavorite("0");
							for (int i = 0; i < allList.size(); i++) {
								if (allList.get(i).getContentURI().equals(GlobalConfig.playerobject.getContentURI())) {
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
					} else if (ReturnType.equals("200")) {
					ToastUtils.show_allways(context, "喜欢该节目，需要您登录");
					} else if (ReturnType.equals("T")) {
						ToastUtils.show_allways(context, "获取列表异常");
					} else {
						ToastUtils.show_allways(context, Message + "");
					}
				} else {
					ToastUtils.show_allways(context, "ReturnType==null");
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
		if(PlayerFragment.audioPlay != null && num >= 0){
			allList.get(num).setType("2");
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 设置当前为暂停状态
	 */
	private static void setPauseType(){
		if(PlayerFragment.audioPlay != null && num >= 0){
			allList.get(num).setType("0");
			adapter.notifyDataSetChanged();
		}
	}
}
