package com.woting.ui.home.player.main.fragment;//package com.wotingfm.activity.home.player.main.fragment;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.TimeZone;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.FragmentActivity;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.Animation.AnimationListener;
//import android.view.animation.AnimationUtils;
//import android.view.animation.LinearInterpolator;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.Response.ErrorListener;
//import com.android.volley.Response.Listener;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import com.google.gson.reflect.TypeToken;
//import com.iflytek.cloud.RecognizerListener;
//import com.iflytek.cloud.RecognizerResult;
//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechError;
//import com.iflytek.cloud.SpeechRecognizer;
//import com.iflytek.cloud.SpeechUtility;
//import com.shenstec.utils.image.ImageLoader;
//import com.woting.R;
//import com.woting.video.VLCPlayerService;
//import com.wotingfm.activity.home.player.main.adapter.PlayerListAdapter;
//import com.wotingfm.activity.home.player.main.adapter.PlayerListNoAdapter;
//import com.wotingfm.activity.home.player.main.dao.SearchPlayerHistoryDao;
//import com.wotingfm.activity.home.player.main.model.LanguageSearch;
//import com.wotingfm.activity.home.player.main.model.LanguageSearchInside;
//import com.wotingfm.activity.home.player.main.model.PlayerHistory;
//import com.wotingfm.activity.home.player.timeset.activity.TimerPowerOffActivity;
//import com.wotingfm.activity.interphone.chat.model.DBTalkHistorary;
//import com.wotingfm.config.GlobalConfig;
//import com.wotingfm.main.common.StringConstant;
//import com.wotingfm.main.commonactivity.BSApplication;
//import com.wotingfm.main.commonactivity.BaseFragment;
//import com.wotingfm.utils.CommonHelper;
//import com.wotingfm.utils.JsonParser;
//import com.wotingfm.utils.PhoneMessage;
//import com.wotingfm.utils.ToastUtil;
//import com.wotingfm.utils.Utils;
//import com.wotingfm.widgetui.xlistview.XListView;
//import com.wotingfm.widgetui.xlistview.XListView.IXListViewListener;
///**
// * 播放主页
// * @author
// *  辛龙 2016年2月4日
// */
//public class PlayersFragment extends BaseFragment implements OnClickListener,IXListViewListener {
//	private static FragmentActivity context;
//	private View headview;
//	private static ImageLoader imageLoader;
//	private LinearLayout lin_right;
//	private static ImageView img_play;
//	private LinearLayout lin_left;
//	private static int num ;//-2 播放器没有播放，-1播放器里边的数据不在list中，其它是在list中
//	private static  TextView tv_name;
//	private android.app.Dialog voicedialog;
//	private SpeechRecognizer mIat;
//	// 用HashMap存储听写结果
//	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
//	private static PlayerListAdapter adapter;
//	private static Animation operatingAnim;
//	private static ImageView img_news;
//	private static boolean firstentry = false;
//	private static ArrayList<LanguageSearchInside> alllist = new ArrayList<LanguageSearchInside>();
//	private View rootView;
//	private RelativeLayout lin_center;
//	private static SeekBar seekBar;
//	private static Handler handlers;
//	public static TextView time_start;
//	private static SimpleDateFormat format;
//	public static TextView time_end;
//	private static boolean playflag = false;// 标记播放状态
//	public static int JudgeTextRequest = -1;
//	private  Dialog dialog;
//	private LinearLayout lin_voicesearch;
//	private LinearLayout lin_time;
//	private static SearchPlayerHistoryDao dbdao;
//	private Handler mHandler;
//	private static XListView mlistView;
//	private static LinearLayout lin_tuijian;
//	private static int sendtype;//第一次获取数据是有分页加载的
//	private int page=1;
//	private int RefreshType;
//	private boolean first=true;
//	private static int playcenter=1;//1没有播放过数据，0已经播放过，在暂停按钮处使用
//	private static Dialog dialogs;
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
//		rootView = inflater.inflate(R.layout.fragment_play, container, false);
//		context = this.getActivity();
//		first=true;//第一次进入
//		mHandler = new Handler();
//		handlers = new Handler();
//		// 给播放的imageview图片设置旋转动画
//		operatingAnim = AnimationUtils.loadAnimation(context, R.anim.tip);
//		LinearInterpolator lin = new LinearInterpolator();
//		operatingAnim.setInterpolator(lin);
//		operatingAnim.setFillAfter(true);                   //停留在结束位置  
//		operatingAnim.setFillEnabled(true); 
//		operatingAnim.setAnimationListener(new AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {
//			}
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//				
//			}
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				
//			}
//		});
//		// 初始化语音配置对象
//		SpeechUtility.createUtility(context, SpeechConstant.APPID + "=56275014");
//		// 创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
//		mIat = SpeechRecognizer.createRecognizer(context, null);
//		imageLoader = new ImageLoader(context);
//		initDao();// 初始化数据库命令执行对象
//		setview();//设置界面
//		setlistener();//设置监听
//		RefreshType=0;
//		changeview();
//		Dialog();
//		WifiDialog();
//		return rootView;
//	}
//
//	private static  LanguageSearchInside getdaolist() {
//		List<PlayerHistory> historydatabaselist = dbdao.queryHistory();
//		LanguageSearchInside historynews = null;
//		if(historydatabaselist!=null&&historydatabaselist.size()>0){
//			PlayerHistory historynew= historydatabaselist.get(0);
//			historynews=new LanguageSearchInside();
//			historynews.setType("1");
//			historynews.setContentURI(historynew.getPlayerUrl());
//			historynews.setContentPersons("");
//			historynews.setContentCatalogs("");
//			historynews.setContentKeyWord("");
//			historynews.setcTime("");
//			historynews.setContentSubjectWord("");
//			historynews.setContentTimes("");
//			historynews.setContentName(historynew.getPlayerName());
//			historynews.setContentPubTime("");
//			historynews.setContentPub("");
//			historynews.setContentPlay(historynew.getPlayerUrl());
//			historynews.setMediaType(historynew.getPlayerMediaType());
//			historynews.setContentId("");
//			historynews.setContentDesc(historynew.getPlayerContentDesc());
//			historynews.setContentImg(historynew.getPlayerImage());
//		}
//		return historynews;
//	}
//
//	private void initDao() {// 初始化数据库命令执行对象
//		dbdao = new SearchPlayerHistoryDao(context);
//	}
//
//	/**
//	 * 把数据添加数据库
//	 * @param languageSearchInside 
//	 */
//	private static void adddb(LanguageSearchInside languageSearchInside) {
//		String playername = languageSearchInside.getContentName();
//		String playerimage =languageSearchInside.getContentImg() ;
//		String playerurl = languageSearchInside.getContentPlay();
//		String playermediatype = languageSearchInside.getMediaType();
//		String playeralltime = "";
//		String playerintime ="" ;
//		String playercontentdesc =languageSearchInside.getContentDesc() ;
//		String playernum ="999" ;
//		String playerzantype ="false" ;
//		String playerfrom = "";
//		String playerfromid = "";
//		String playeraddtime = Long.toString(System.currentTimeMillis());
//		String bjuserid =Utils.getUserId(context);
//		PlayerHistory history = new PlayerHistory(
//				playername, playerimage, playerurl, playermediatype,
//				playeralltime,playerintime, playercontentdesc, playernum,
//				playerzantype, playerfrom, playerfromid,playeraddtime,bjuserid);
//		dbdao.deleteHistory(playerurl);
//		dbdao.addHistory(history);	
//		Log.e("数据库数据条数=============", dbdao.queryHistory().size()+"");
//	}
//
//	private void setview() {
//		mlistView = (XListView) rootView.findViewById(R.id.listView);
//		mlistView .setPullLoadEnable(false);
//		mlistView.setXListViewListener(this);
//		headview = LayoutInflater.from(context).inflate(R.layout.headview_fragment_play, null);
//		lin_center = (RelativeLayout) headview.findViewById(R.id.lin_center);
//		lin_tuijian = (LinearLayout) headview.findViewById(R.id.lin_tuijian);
//		lin_right = (LinearLayout) headview.findViewById(R.id.lin_right);
//		img_news = (ImageView) headview.findViewById(R.id.img_news);
//		img_play = (ImageView) headview.findViewById(R.id.img_play);
//		lin_time = (LinearLayout) headview.findViewById(R.id.lin_time);
//		lin_left = (LinearLayout) headview.findViewById(R.id.lin_left);
//		tv_name = (TextView) headview.findViewById(R.id.tv_name);
//		seekBar = (SeekBar) headview.findViewById(R.id.seekBar);
//		// 配合seekbar使用的标签
//		time_start = (TextView) headview.findViewById(R.id.time_start);
//		time_end = (TextView) headview.findViewById(R.id.time_end);
//		// 语音搜索
//		lin_voicesearch = (LinearLayout) headview.findViewById(R.id.lin_voicesearch);
//		mlistView.addHeaderView(headview);
//	}
//
//	private void setlistener() {
//		lin_time.setOnClickListener(this);
//		lin_left.setOnClickListener(this);
//		lin_center.setOnClickListener(this);
//		lin_right.setOnClickListener(this);
//		lin_voicesearch.setOnClickListener(this);
//		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//			}
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//			}
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress,
//					boolean fromUser) {
//				if (fromUser) {
//					if(VLCPlayerService.getTime()==-1||VLCPlayerService.getLength()==-1){
//					}else{
//						VLCPlayerService.setPosition(progress);
//						// 通知方法 告知程序重新获取系统时间
//						String s = format.format(VLCPlayerService.getTime());
//						time_start.setText(s);
//						dbdao.updatefileinfo(alllist.get(num).getContentPlay(), "playerintime",String.valueOf(VLCPlayerService.getTime()));
//					}
//				}
//			}
//		});
//	}
//
//	private static void setitemlistener() {
//		mlistView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
//				int number=position - 2;
//				//				play(number);
//				getnetwork(number,context);
//			}
//		});
//	}
//
//	//按照界面排序号进行播放
//	protected static void play(int number) {
//		if(alllist!=null&&alllist.get(number)!=null){
//			adddb(alllist.get(number));
//			if(alllist.get(number).getMediaType().equals("AUDIO")){
//				if (alllist.get(number).getContentPlay() != null) {
//					img_play.setImageResource(R.drawable.wt_play_play);
//					if(alllist.get(number).getContentName()!=null){
//						tv_name.setText(alllist.get(number).getContentName());
//					}else{
//						tv_name.setText("wotingkeji");
//					}
//					if(alllist.get(number).getContentImg()!=null){
//						String url;
//						if(alllist.get(number).getContentImg().startsWith("http")){
//							url =  alllist.get(number).getContentImg();
//						}else{
//							url = GlobalConfig.imageurl + alllist.get(number).getContentImg();
//						}
//						imageLoader.DisplayImage(url.replace( "\\/", "/"), img_news, false, false,null);
//					}else{
//						img_news.setImageResource(R.drawable.wt_image_playertx_80);
//					}
//					for(int i=0;i<alllist.size();i++){
//						alllist.get(i).setType("1");
//					}
//					alllist.get(number).setType("2");
//					adapter.notifyDataSetChanged();
//					if (operatingAnim != null) {
//						operatingAnim.start();
//						img_news.startAnimation(operatingAnim);
//					}
//					VLCPlayerService.play(alllist.get(number).getContentPlay());
//					format = new SimpleDateFormat("HH:mm:ss");
//					format.setTimeZone(TimeZone.getTimeZone("GMT"));
//					Log.e("GMT======", VLCPlayerService.getLength()+"");
//					String s = format.format(VLCPlayerService.getLength());
//					time_end.setText(s);
//					dbdao.updatefileinfo(alllist.get(number).getContentPlay(), "playeralltime",String.valueOf(VLCPlayerService.getLength()));
//					seekBar.setProgress(0);
//					seekBar.setMax((int)VLCPlayerService.getLength());
//					seekBar.setEnabled(true);
//					playflag=true;
//					new changetime().start();
//					GlobalConfig.playerobject=alllist.get(number);
//					num=number;
//				} else {
//					ToastUtil.show_short(context, "暂不支持播放");
//				}
//			}else if(alllist.get(number).getMediaType().equals("RADIO")){
//				if (alllist.get(number).getContentPlay() != null) {
//					img_play.setImageResource(R.drawable.wt_play_play);
//					if(alllist.get(number).getContentName()!=null){
//						tv_name.setText(alllist.get(number).getContentName());
//					}else{
//						tv_name.setText("wotingkeji");
//					}
//					if(alllist.get(number).getContentImg()!=null){
//						String url;
//						if(alllist.get(number).getContentImg().startsWith("http")){
//							url =  alllist.get(number).getContentImg();
//						}else{
//							url = GlobalConfig.imageurl + alllist.get(number).getContentImg();
//						}
//						imageLoader.DisplayImage(url.replace( "\\/", "/"), img_news, false, false,null);
//					}else{
//						img_news.setImageResource(R.drawable.wt_image_playertx_80);
//					}
//					for(int i=0;i<alllist.size();i++){
//						alllist.get(i).setType("1");
//					}
//					alllist.get(number).setType("2");
//					adapter.notifyDataSetChanged();
//					if (operatingAnim != null) {
//						operatingAnim.start();
//						img_news.startAnimation(operatingAnim);
//					}
//					seekBar.setProgress(0);
//					seekBar.setEnabled(false);
//					time_start.setText("00:00:00");
//					time_end.setText("24:00:00");
//					VLCPlayerService.play(alllist.get(number).getContentPlay());
//					GlobalConfig.playerobject=alllist.get(number);
//					num=number;
//				} else {
//					ToastUtil.show_short(context, "暂不支持播放");
//				}
//			}
//
//		}}
//
//	private static void getnetwork(int number, Context context) {
//		SharedPreferences sp=context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
//		String wifiset = sp.getString(StringConstant.WIFISET, "true");//是否是第一次登录
//		if(wifiset!=null&&!wifiset.trim().equals("")&&wifiset.equals("true")){
//			//开启网络播放数据连接提醒
//			CommonHelper.checkNetworkStatus(context);//网络设置获取
//			if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE!=-1){
//				if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE==1){
//					ToastUtil.show_allways(context, "wifi已连接");
//					play( number) ;
//				}else{
//					ToastUtil.show_allways(context, "不是wifi");
//				}
//			}else{
//				ToastUtil.show_allways(context, "无网络连接");
//			}
//		}else{
//			//未开启网络播放数据连接提醒
//			play( number) ;
//		}
//	}
//
//	/**
//	 * 参数设置
//	 */
//	public void setParam() {
//		// 清空参数
//		mIat.setParameter(SpeechConstant.PARAMS, null);
//		// 搜索引擎 云搜索
//		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//		// 设置返回结果格式
//		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
//		// 设置语言
//		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//		// 设置语言区域
//		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
//		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
//		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
//		// 注：该参数暂时只对在线听写有效
//		mIat.setParameter(SpeechConstant.ASR_DWA, "1");
//		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
//		mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
//		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//		mIat.setParameter(SpeechConstant.VAD_EOS, "5000");
//	}
//
//	// 听写监听器
//	private RecognizerListener mRecoListener = new RecognizerListener() {
//		// 听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
//		// 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
//		// 关于解析Json的代码可参见MscDemo中JsonParser类；
//		// isLast等于true时会话结束。
//		public void onResult(RecognizerResult results, boolean isLast) {
//			printResult(results);
//			Log.e("Result:", results.getResultString());
//		}
//		// 会话发生错误回调接口
//		public void onError(SpeechError error) {
//			error.getPlainDescription(true); // 获取错误码描述
//		}
//		// 开始录音
//		public void onBeginOfSpeech() {
//			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//			Toast.makeText(context, "可以开始说话", 1).show();
//		}
//		// 音量值0~30
//		public void onVolumeChanged(int volume) {
//		}
//		// 结束录音
//		public void onEndOfSpeech() {
//			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//			Toast.makeText(context, "结束说话", 1).show();
//		}
//		// 扩展用接口
//		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
//			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
//			// 若使用本地能力，会话id为null
//			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
//			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
//			// Log.d(TAG, "session id =" + sid);
//			// }
//		}
//		@Override
//		public void onVolumeChanged(int arg0, byte[] arg1) {
//			// TODO Auto-generated method stub
//			// Toast.makeText(TestDemo.this, "当前正在说话，音量大小：" + arg0+"",
//			// 1).show();
//			// Log.d(TAG, "返回音频数据："+arg1.length);
//		}
//	};
//	private android.app.Dialog wifidialog;
//
//	private void printResult(RecognizerResult results) {
//		String text = JsonParser.parseIatResult(results.getResultString());
//		String sn = null;
//		// 读取json结果中的sn字段
//		try {
//			JSONObject resultJson = new JSONObject(results.getResultString());
//			sn = resultJson.optString("sn");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		mIatResults.put(sn, text);
//		StringBuffer resultBuffer = new StringBuffer();
//		for (String key : mIatResults.keySet()) {
//			resultBuffer.append(mIatResults.get(key));
//		}
//		String str = resultBuffer.toString();
//		/* tv_news.setText(str); */
//		if (str != null && !str.equals("")) {
//			str = str.replaceAll(
//					"[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]",
//					"");
//			ToastUtil.show_short(context, str);
//			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
//				searchByVoicesend(str);
//			} else {
//				ToastUtil.show_short(context, "网络失败，请检查网络");
//			}
//		} else {
//			ToastUtil.show_short(context, "您输入的对话信息为空");
//		}
//		/*
//		 * tv_news.setVisibility(View.VISIBLE); new Handler().postDelayed(new
//		 * Runnable() { public void run() { tv_news.setVisibility(View.GONE); }
//		 * }, 5000);
//		 */
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.lin_voicesearch:
//			voicedialog.show();
//			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
//				mIatResults.clear(); // 设置参数
//				setParam(); // 开始听写
//				mIat.startListening(mRecoListener);
//			} else {
//				ToastUtil.show_short(context, "网络失败，请检查网络");
//			}
//			break;
//		case R.id.lin_left:
//			if(num-1>=0){
//				Log.e("点击num===============", num+"");
//				int number=num-1;
//				Log.e("播放num===============", number+"");
//				getnetwork(number,context);
//			}else{
//				ToastUtil.show_allways(context, "已结是第一条数据了");
//			}
//			break;
//		case R.id.lin_center:
//			if(GlobalConfig.playerobject==null){
//			}else{
//				if(GlobalConfig.playerobject.getMediaType().equals("AUDIO")){
//					if(VLCPlayerService.getisPlaying()){
//						img_play.setImageResource(R.drawable.wt_play_stop);
//						VLCPlayerService.pause();
//						if (operatingAnim != null) {
//							operatingAnim.cancel();
//							img_news.clearAnimation();
//						}
//					}else {
//						if(playcenter==0){
//							VLCPlayerService.continueplay();
//							if (operatingAnim != null) {
//								operatingAnim.start();
//								img_news.startAnimation(operatingAnim);
//							}
//						}else{
//							if (GlobalConfig.playerobject.getContentPlay() != null) {
//								VLCPlayerService.play(GlobalConfig.playerobject.getContentPlay());
//								if (operatingAnim != null) {
//									operatingAnim.start();
//									img_news.startAnimation(operatingAnim);
//								}
//							}else{
//								ToastUtil.show_allways(context, "暂不支持播放");
//							}
//						}
//						img_play.setImageResource(R.drawable.wt_play_play);
//					}
//				}else{
//					if(VLCPlayerService.getisPlaying()){
//						VLCPlayerService.stop();
//						img_play.setImageResource(R.drawable.wt_play_stop);
//						if (operatingAnim != null) {
//							operatingAnim.cancel();
//							img_news.clearAnimation();
//						}
//					}else{
//						if (GlobalConfig.playerobject.getContentPlay() != null) {
//							seekBar.setProgress(0);
//							seekBar.setEnabled(false);
//							time_start.setText("00:00:00");
//							time_end.setText("24:00:00");
//							VLCPlayerService.play(GlobalConfig.playerobject.getContentPlay());
//						}else{
//							ToastUtil.show_allways(context, "暂不支持播放");
//						}
//						img_play.setImageResource(R.drawable.wt_play_play);
//					}
//				}
//			}
//			break;
//		case R.id.lin_right:
//			if(alllist!=null&&alllist.size()>0){
//				if(num+1<alllist.size()){
//					Log.e("点击num===============", num+"");
//					int number=num+1;
//					Log.e("播放num===============", number+"");
//					getnetwork(number,context);
//				}else{
//					ToastUtil.show_allways(context, "已结是最后一条数据了");
//				}
//			}
//			break;
//		case R.id.lin_time:
//			startActivity(new Intent(context, TimerPowerOffActivity.class));
//			break;
//		default:
//			break;
//		}
//	}
//
//	public void onRefresh() {
//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				if(sendtype==1){
//					mlistView .setPullLoadEnable(false);
//					RefreshType = 1;
//					page = 1;
//					firstsend();
//				}
//			}
//		}, 1000);
//	}
//
//	public void onLoadMore() {
//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				if(sendtype==1){
//					RefreshType = 2;
//					firstsend();
//				}
//			}
//		}, 1000);
//	}
//
//	// 语音搜索请求
//	private void searchByVoicesend(String str) {
//		sendtype=2;
//		// 发送数据
//		RequestQueue requestQueue = Volley.newRequestQueue(context);
//		Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
//			private String ReturnType;
//			private String MainList;
//			@Override
//			public void onResponse(JSONObject arg0) {
//				Log.e("语音搜索返回数据", arg0 + "");
//				try {
//					ReturnType = arg0.getString("ReturnType");
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				try {
//					MainList = arg0.getString("ResultList");
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				if (ReturnType.equals("1001")) {
//					//					LanguageSearch lists = new LanguageSearch();
//					try {
//						Gson gson = new Gson();
//						LanguageSearch	lists = gson.fromJson(MainList,	new TypeToken<LanguageSearch>() {}.getType());
//						List<LanguageSearchInside> list = lists.getList();
//						if (list != null && list.size() != 0) {
//							firstentry = false;
//							num = 0;
//							alllist.clear();
//							alllist.addAll(list);
//							lin_tuijian.setVisibility(View.VISIBLE);
//							adapter = new PlayerListAdapter(context, alllist);
//							mlistView.setAdapter(adapter);
//							setitemlistener();
//							//							play(0);
//							getnetwork(0,context);
//							mlistView .setPullRefreshEnable(false);
//							mlistView .setPullLoadEnable(false);
//							mlistView.stopRefresh();
//							mlistView.stopLoadMore();
//							mlistView.setRefreshTime(new Date().toLocaleString());
//						}
//					} catch (JsonSyntaxException e) {
//						e.printStackTrace();
//					}
//
//				} else if (ReturnType.equals("1011")) {
//					ToastUtil.show_short(context, "没有查询内容");
//				} else {
//					ToastUtil.show_short(context, "没有新的数据");
//				}
//			}
//		};
//		ErrorListener errorListener = new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				Log.e("语音搜索返回异常", arg0 + "");
//			}
//		};
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("MobileClass", PhoneMessage.model + "::"
//					+ PhoneMessage.productor);
//			jsonObject.put("SessionId", Utils.getSessionId(context));
//			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x"+ PhoneMessage.ScreenHeight);
//			jsonObject.put("IMEI", PhoneMessage.imei);
//			jsonObject.put("UserId", Utils.getUserId(context));
//			jsonObject.put("SearchStr", str);
//			jsonObject.put("PCDType", "1");
//			jsonObject.put("PageType", "1");
//			//			CatalogType
//			//			CatalogId//此处这俩参数都不需要
//			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, GlobalConfig.searchvoiceUrl,
//					jsonObject, listener, errorListener);
//			request.setRetryPolicy(new DefaultRetryPolicy(3 * 1000, 1, 1.0f));
//			requestQueue.add(request);
//			requestQueue.start();
//			Log.i("语音搜索路径及信息", GlobalConfig.searchvoiceUrl + jsonObject);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//
//	// 第一次进入该界面时候的数据
//	private void firstsend() {
//		sendtype=1;
//		RequestQueue requestQueue = Volley.newRequestQueue(context);
//		Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
//			private String ReturnType;
//			private String MainList;
//			@Override
//			public void onResponse(JSONObject arg0) {
//				if(dialog!=null){
//					dialog.dismiss();
//				}
//				Log.e("第一次数据返回数据", arg0 + "");
//				try {
//					ReturnType=	arg0.getString("ReturnType");
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				if(ReturnType.equals("1001")){
//					page++;
//					try {
//						String List = arg0.getString("ResultList");
//						JSONTokener jsonParser = new JSONTokener(List);
//						JSONObject arg1 = (JSONObject) jsonParser.nextValue();
//						MainList = arg1.getString("List");
//						//						ArrayList<LanguageSearchInside> list = new ArrayList<LanguageSearchInside>();
//						Gson gson=new Gson();
//						ArrayList<LanguageSearchInside> 	list=gson.fromJson(MainList, new TypeToken<List<LanguageSearchInside>>(){}.getType());
//						if(RefreshType==0){
//							//第一次进入该界面获取数据
//							LanguageSearchInside flist = getdaolist();//得到数据库里边的第一条数据
//							if(flist!=null){
//								//如果数据库里边的数据不是空的，在headview设置该数据
//								GlobalConfig.playerobject=flist;
//								num=-1;
//								if(flist.getContentName()!=null){
//									tv_name.setText(flist.getContentName());
//								}else{
//									tv_name.setText("wotingkeji");
//								}
//								if(flist.getContentImg()!=null){
//									String url;
//									if(flist.getContentImg().startsWith("http")){
//										url =  flist.getContentImg();
//									}else{
//										url = GlobalConfig.imageurl + flist.getContentImg();
//									}
//									imageLoader.DisplayImage(url.replace( "\\/", "/"), img_news, false, false,null);
//								}else{
//									img_news.setImageResource(R.drawable.wt_image_playertx_80);
//								}
//							}else if(list!=null&&list.size()>0){
//								//如果数据库里边的数据为空，需要判断获取的数据是否为空，不为空则设置第一条数据为headview
//								if(list.get(0)!=null&&list.get(0).getContentName()!=null){
//									GlobalConfig.playerobject=flist;
//									num=0;
//									tv_name.setText(list.get(0).getContentName());
//									if(list.get(0).getContentImg()!=null){
//										String url;
//										if(list.get(0).getContentImg().startsWith("http")){
//											url =  list.get(0).getContentImg();
//										}else{
//											url = GlobalConfig.imageurl + list.get(0).getContentImg();
//										}
//										imageLoader.DisplayImage(url.replace( "\\/", "/"), img_news, false, false,null);
//									}else{
//										img_news.setImageResource(R.drawable.wt_image_playertx_80);
//									}
//
//								}else{
//									num=-2;
//									tv_name.setText("wotingkeji");
//								}
//							}else{
//								num=-2;
//								tv_name.setText("wotingkeji");
//							}
//							alllist.clear();
//							alllist.addAll(list);
//							if(GlobalConfig.playerobject!=null&&alllist!=null){
//								for(int i=0;i<alllist.size();i++){
//									if(alllist.get(i).getContentPlay().equals(GlobalConfig.playerobject.getContentPlay())){
//										alllist.get(i).setType("2");
//										num=i;
//									}
//								}
//							}
//							lin_tuijian.setVisibility(View.VISIBLE);
//							adapter = new PlayerListAdapter(context, alllist);
//							mlistView.setAdapter(adapter);
//							setitemlistener();
//							mlistView .setPullLoadEnable(true);
//							mlistView.stopRefresh();
//							mlistView.setRefreshTime(new Date().toLocaleString());
//						}else if(RefreshType==1){
//							alllist.clear();
//							alllist.addAll(list);
//							if(GlobalConfig.playerobject!=null&&alllist!=null){
//								for(int i=0;i<alllist.size();i++){
//									if(alllist.get(i).getContentPlay().equals(GlobalConfig.playerobject.getContentPlay())){
//										alllist.get(i).setType("2");
//										num=i;
//									}
//								}
//							}
//							lin_tuijian.setVisibility(View.VISIBLE);
//							adapter = new PlayerListAdapter(context, alllist);
//							mlistView.setAdapter(adapter);
//							setitemlistener();
//							mlistView .setPullLoadEnable(true);
//							mlistView.stopRefresh();
//							mlistView.setRefreshTime(new Date().toLocaleString());
//						}else{
//							mlistView.stopLoadMore();
//							alllist.addAll(list);
//							adapter.notifyDataSetChanged();
//							setitemlistener();
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//						mlistView .setPullLoadEnable(false);
//						mlistView.stopRefresh();
//						mlistView.stopLoadMore();
//						mlistView.setRefreshTime(new Date().toLocaleString());
//						lin_tuijian.setVisibility(View.GONE);
//						PlayerListNoAdapter adapters = new PlayerListNoAdapter(context);
//						mlistView.setAdapter(adapters);
//					}
//				}else{
//					mlistView .setPullLoadEnable(false);
//					mlistView.stopRefresh();
//					mlistView.stopLoadMore();
//					mlistView.setRefreshTime(new Date().toLocaleString());
//					lin_tuijian.setVisibility(View.GONE);
//					PlayerListNoAdapter adapters = new PlayerListNoAdapter(context);
//					mlistView.setAdapter(adapters);
//				}
//			}
//		};
//		ErrorListener errorListener = new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				if(dialog!=null){
//					dialog.dismiss();
//				}
//				mlistView .setPullLoadEnable(false);
//				mlistView.stopRefresh();
//				mlistView.stopLoadMore();
//				mlistView.setRefreshTime(new Date().toLocaleString());
//				Log.e("语音搜索第一次数据返回异常", arg0 + "");
//			}
//		};
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("PCDType", "1");
//			jsonObject.put("SessionId",Utils.getSessionId(context));
//			jsonObject.put("MobileClass", PhoneMessage.model + "::"+ PhoneMessage.productor);
//			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x"+ PhoneMessage.ScreenHeight);
//			jsonObject.put("IMEI", PhoneMessage.imei);
//			PhoneMessage.getGps(context);
//			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
//			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
//			jsonObject.put("UserId", Utils.getUserId(context));
//			jsonObject.put("PageType", "0");
//			jsonObject.put("Page", String.valueOf(page));
//			jsonObject.put("PageSize", "10");
//			JsonObjectRequest request = new JsonObjectRequest(
//					Request.Method.POST, GlobalConfig.mainPageUrl,jsonObject,
//					listener, errorListener);
//			request.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));
//			requestQueue.add(request);
//			requestQueue.start();
//			Log.i("语音搜索获取第一次数据路径及信息", GlobalConfig.mainPageUrl + jsonObject);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//
//	// 文字请求
//	public static void SendTextRequest(String contname,final Context mContext) {
//		dialogs = Utils.Dialogph(context, "通讯中", dialogs);
//		final LanguageSearchInside flist = getdaolist();//得到数据库里边的第一条数据
//		if(flist!=null){
//			//如果数据库里边的数据不是空的，在headview设置该数据
//			if(alllist!=null&&alllist.size()>0){
//				ArrayList<LanguageSearchInside> alllists = alllist;
//				alllist.clear();
//				alllist.add(flist);
//				alllist.addAll(alllists);
//			}else{
//				alllist = new ArrayList<LanguageSearchInside>();
//				alllist.add(flist);
//			}
//			num=0;
//			if(flist.getContentName()!=null){
//				tv_name.setText(flist.getContentName());
//			}else{
//				tv_name.setText("wotingkeji");
//			}
//			adapter = new PlayerListAdapter(context, alllist);
//			mlistView.setAdapter(adapter);
//			setitemlistener();
//			//			play(0);
//			getnetwork(0,context);
//		}
//		// 发送数据
//		sendtype=2;
//		RequestQueue requestQueue = Volley.newRequestQueue(context);
//		Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
//			private String ReturnType;
//			private String MainList;
//			@Override
//			public void onResponse(JSONObject arg0) {
//				Log.e("文字搜索返回数据", arg0 + "");
//				if(dialogs!=null){
//					dialogs.dismiss();
//				}
//				try {
//					ReturnType = arg0.getString("ReturnType");
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				try {
//					MainList = arg0.getString("ResultList");
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				if (ReturnType.equals("1001")) {
//					//					LanguageSearch lists = new LanguageSearch();
//					try {
//						Gson gson = new Gson();
//						LanguageSearch	lists = gson.fromJson(MainList,	new TypeToken<LanguageSearch>() {}.getType());
//						List<LanguageSearchInside> list = lists.getList();
//						if (list != null && list.size() != 0) {
//							firstentry = false;
//							num = 0;
//							alllist.clear();
//							alllist.addAll(list);
//							lin_tuijian.setVisibility(View.VISIBLE);
//							adapter = new PlayerListAdapter(context, alllist);
//							mlistView.setAdapter(adapter);
//							setitemlistener();
//							mlistView .setPullRefreshEnable(false);
//							mlistView .setPullLoadEnable(false);
//							mlistView.stopRefresh();
//							mlistView.stopLoadMore();
//							mlistView.setRefreshTime(new Date().toLocaleString());
//						}
//					} catch (JsonSyntaxException e) {
//						e.printStackTrace();
//					}
//				} else if (ReturnType.equals("1011")) {
//					ToastUtil.show_short(context, "没有查询内容");
//				} else {
//					ToastUtil.show_short(context, "没有新的数据");
//				}
//			}
//		};
//		ErrorListener errorListener = new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				if(dialogs!=null){
//					dialogs.dismiss();
//				}
//				Log.e("文字搜索返回异常", arg0 + "");
//			}
//		};
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("MobileClass", PhoneMessage.model + "::"
//					+ PhoneMessage.productor);
//			jsonObject.put("SessionId", Utils.getSessionId(context));
//			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x"+ PhoneMessage.ScreenHeight);
//			jsonObject.put("IMEI", PhoneMessage.imei);
//			PhoneMessage.getGps(context);
//			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
//			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
//			jsonObject.put("UserId", Utils.getUserId(context));
//			jsonObject.put("SearchStr", contname);
//			jsonObject.put("PCDType", "1");
//			jsonObject.put("PageType", "0");
//			//			CatalogType
//			//			CatalogId//此处这俩参数都不需要
//			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, GlobalConfig.getSearchByText,
//					jsonObject, listener, errorListener);
//			request.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));
//			requestQueue.add(request);
//			requestQueue.start();
//			Log.i("文字搜索路径及信息", GlobalConfig.getSearchByText + jsonObject);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void Dialog() {
//		final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_yuyin, null);
//		ImageView dialog_delete = (ImageView) dialog1.findViewById(R.id.dialog_delete);
//		TextView tv_over = (TextView) dialog1.findViewById(R.id.tv_over);
//		voicedialog = new Dialog(context, R.style.MyDialog);
//		voicedialog.setContentView(dialog1);
//		voicedialog.setCanceledOnTouchOutside(true);
//		voicedialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
//		tv_over.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mIat.stopListening();
//				voicedialog.dismiss();
//			}
//		});
//		dialog_delete.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				voicedialog.dismiss();
//			}
//		});
//	}
//
//	//wifi弹出框
//	private void WifiDialog() {
//		final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_wifi_set, null);
//		TextView tv_over = (TextView) dialog1.findViewById(R.id.tv_cancle);
//		TextView tv_first = (TextView) dialog1.findViewById(R.id.tv_first);
//		TextView tv_all = (TextView) dialog1.findViewById(R.id.tv_all);
//		wifidialog = new Dialog(context, R.style.MyDialog);
//		wifidialog.setContentView(dialog1);
//		wifidialog.setCanceledOnTouchOutside(true);
//		wifidialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
//		tv_over.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				wifidialog.dismiss();
//			}
//		});
//		tv_first.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				wifidialog.dismiss();
//			}
//		});
//		tv_all.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				wifidialog.dismiss();
//			}
//		});
//	}
//
//	//更改进度条数据线程
//	public static class changetime extends Thread implements Runnable {
//		public void run() {
//			while (playflag) {
//				playcenter=0;
//				final String s = format.format(VLCPlayerService.getTime());
//				Message msg = new Message();
//				msg.obj = s;
//				handlers.sendMessage(msg);
//				try {
//					sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	// 接收线程数据来更改界面（界面只能在主线程中更改）
//	private void changeview() {
//		handlers = new Handler() {
//			public void handleMessage(Message msg) {
//				String time = (String) msg.obj;
//				time_start.setText(time);
//				if (VLCPlayerService.getPlayerState()==6) {
//					if(num+1<alllist.size()){
//						//此时自动播放下一首
//						dbdao.updatefileinfo(alllist.get(num).getContentPlay(), "playerintime",String.valueOf(VLCPlayerService.getTime()));
//						int number=num+1;
//						//						play(number);
//						getnetwork(number,context);
//					}else{
//						//全部播放完毕了
//						time_start.setText("00:00:00");
//						seekBar.setProgress(0);
//						img_play.setImageResource(R.drawable.wt_play_stop);
//						if (operatingAnim != null) {
//							operatingAnim.cancel();
//							img_news.clearAnimation();
//						}
//						for(int i=0;i<alllist.size();i++){
//							alllist.get(i).setType("1");
//						}
//						adapter.notifyDataSetChanged();
//					}
//				} else {
//					seekBar.setProgress((int) VLCPlayerService.getTime());
//					dbdao.updatefileinfo(alllist.get(num).getContentPlay(), "playerintime",String.valueOf(VLCPlayerService.getTime()));
//
//				}
//				super.handleMessage(msg);
//			}
//		};
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		if(first==true){
//			SharedPreferences sp = context.getSharedPreferences("wotingfm",Context.MODE_PRIVATE);
//			String enter = sp.getString(StringConstant.PLAYHISTORYENTER, "false");//
//			String news = sp.getString(StringConstant.PLAYHISTORYENTERNEWS, "");//
//			if(enter.equals("true")){
//				SendTextRequest(news, context);
//				Editor et = sp.edit();
//				et.putString(StringConstant.PLAYHISTORYENTER, "false");
//				et.commit();
//			}else{
//				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
//					dialog = Utils.Dialogph(context, "通讯中", dialog);
//					firstsend();//搜索第一次数据
//				} else {
//					ToastUtil.show_allways(context, "网络连接失败，请稍后重试");
//				}
//			}
//			first=false;
//		}
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		// 退出时释放连接
//		mIat.cancel();
//		mIat.destroy();
//	}
//}
