package com.woting.activity.home.program.tuijian.slideshowview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.activity.home.program.tuijian.slideshowview.model.ImgReceive;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SlideShowView extends FrameLayout {
	// 轮播图图片数量
	private final static int IMAGE_COUNT = 4;
	// 自动轮播的时间间隔
//	private final static int TIME_INTERVAL = 5;
	// 自动轮播启用开关
	private final static boolean isAutoPlay = true;
	// 自定义轮播图的资源
//	private static String[] imageUrls;
	// 放轮播图片的ImageView 的list
	private List<ImageView> imageViewsList;
	// 放圆点的View的list
	private List<View> dotViewsList;
	private ViewPager viewPager;
	// 当前轮播页
	private int currentItem = 0;
	// 定时任务
	private ScheduledExecutorService scheduledExecutorService;
	private Context context;
	// Handler
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			viewPager.setCurrentItem(currentItem);
		}
	};

	protected List<ImgReceive> Imagelist = new ArrayList<ImgReceive>();

	public SlideShowView(Context context) {
		this(context, null);
	}

	public SlideShowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initData();
		if (isAutoPlay) {
			startPlay();
		}
	}

	/**
	 * 开始轮播图切换
	 */
	private void startPlay() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
	}

	/**
	 * 停止轮播图切换
	 */
	private void stopPlay() {
		scheduledExecutorService.shutdown();
	}

	/**
	 * 初始化相关Data
	 */
	private void initData() {
		imageViewsList = new ArrayList<ImageView>();
		dotViewsList = new ArrayList<View>();

		// 一步任务获取图片
		/* new GetListTask().execute(""); */
		send();
	}

	// 获取服务器地址
	private void send() {
		RequestQueue requestQueue =Volley.newRequestQueue(context);
		Listener<JSONObject> listener = new Listener<JSONObject>() {
			private String ResultList;
			private String StringSubList;
			private String ReturnType;
			private Dialog dialog;

			@Override
			// 此处返回一个节目信息的list
			public void onResponse(JSONObject arg0) {
				if (dialog != null) {
					dialog.dismiss();
				}
				// pulltorefresh处理page=1 pulltoloadmore不处理page 再返回值处处理page++
				// 默认加载第一页

				Log.e("返回数据", arg0.toString());
				try {
					ReturnType = arg0.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					// 获取列表
					ResultList = arg0.getString("ResultList");
					JSONTokener jsonParser = new JSONTokener(ResultList);
					JSONObject arg1 = (JSONObject) jsonParser.nextValue();
					StringSubList = arg1.getString("SubList");
//					Gson gson = new Gson();
					// 此处服务器返回的图片不能用 根本打不开 要用自己的
					/*
					 * Imagelist = gson.fromJson(StringSubList, new
					 * TypeToken<List<ImgReceive>>() { }.getType());
					 */
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (ReturnType != null) {
					if (ReturnType.equals("1001")) {
						ToastUtils.show_short(context, "成功获取到返回值");
						if (Imagelist.size() == 0) {
							ImgReceive imgurl1 = new ImgReceive();
							ImgReceive imgurl2 = new ImgReceive();
							ImgReceive imgurl3 = new ImgReceive();
							ImgReceive imgurl4 = new ImgReceive();
							imgurl1.setImgUrl("http://data1.act3.qq.com/2010-10-11/15/c5e934ade2eee9f9f93748a3d964dbe8.jpg");
							imgurl2.setImgUrl("http://data1.act3.qq.com/2010-10-11/15/c5e934ade2eee9f9f93748a3d964dbe8.jpg");
							imgurl3.setImgUrl("http://data1.act3.qq.com/2010-10-11/15/c5e934ade2eee9f9f93748a3d964dbe8.jpg");
							imgurl4.setImgUrl("http://data1.act3.qq.com/2010-10-11/15/c5e934ade2eee9f9f93748a3d964dbe8.jpg");
							Imagelist.add(imgurl1);
							Imagelist.add(imgurl2);
							Imagelist.add(imgurl3);
							Imagelist.add(imgurl4);
						}
						initUI(context);
					} else {
						if (ReturnType.equals("1002")) {
							ToastUtils.show_short(context, "无分类Id为[001]的分类");
						} else if (ReturnType.equals("1003")) {
							ToastUtils.show_short(context, "获取列表失败");
						} else if (ReturnType.equals("1011")) {
							ToastUtils.show_short(context, "该分类下列表为空");
						} else if (ReturnType.equals("0000")) {
							ToastUtils.show_short(context, "无法获取需要的参数");
						} else if (ReturnType.equals("T")) {
							ToastUtils.show_short(context, "异常");
						}
					}
				} else {
					ToastUtils.show_short(context, "ReturnType不能为空");
				}
			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
			}
		};
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("ContentType", "0");
			jsonObject.put("CatalogType", "001");// 001为一个结果 002为另一个
			jsonObject.put("CatalogId", "0001");
			jsonObject.put("Size", 4);
			jsonObject.put("PCDType", "1");
			JsonObjectRequest request = new JsonObjectRequest(
					Request.Method.POST, GlobalConfig.getadvertUrl, jsonObject,
					listener, errorListener);
			requestQueue.add(request);
			requestQueue.start();
			Log.e("路径", GlobalConfig.getListByCatalog);
			Log.e("提交数据", jsonObject + "");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 初始化Views等UI
	 */
	private void initUI(final Context context) {
		/*
		 * if (imageUrls == null || imageUrls.length == 0) return;
		 */
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);
		LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
		dotLayout.removeAllViews();
		// 热点个数与图片特殊相等
		for (int i = 0; i < Imagelist.size(); i++) {
			ImageView view = new ImageView(context);
			// 此处给img数组设置属性，可以存放一个list，或者是一个对象，只要设置一个对应的图片URL即可
			view.setTag(Imagelist.get(i).getImgUrl());
			// if(i==0)//给一个默认图
			// view.setBackgroundResource(R.drawable.focus_image_default4);
			view.setScaleType(ScaleType.FIT_XY);
			imageViewsList.add(view);
			ImageView dotView = new ImageView(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.leftMargin = 4;
			params.rightMargin = 4;
			dotLayout.addView(dotView, params);
			dotViewsList.add(dotView);
		}
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	/**
	 * 填充ViewPager的页面适配器
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(imageViewsList.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			ImageView imageView = imageViewsList.get(position);
			final int j = position;
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ToastUtils.show_short(context, "点击的第" + j + "图");
				}
			});
//			String s = (String) imageView.getTag();
			Picasso.with(context).load(imageView.getTag() + "").into(imageView);
			((ViewPager) container).addView(imageViewsList.get(position));
			return imageViewsList.get(position);
		}

		@Override
		public int getCount() {
			return imageViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}
	}

	/**
	 * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		boolean isAutoPlay = false;

		@Override
		public void onPageScrollStateChanged(int arg0) {
			switch (arg0) {
			case 1:// 手势滑动，空闲中
				isAutoPlay = false;
				break;
			case 2:// 界面切换中
				isAutoPlay = true;
				break;
			case 0:// 滑动结束，即切换完毕或者加载完毕
					// 当前为最后一张，此时从右向左滑，则切换到第一张
				if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
					viewPager.setCurrentItem(0);
				}
				// 当前为第一张，此时从左向右滑，则切换到最后一张
				else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
					viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
				}
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int pos) {
			currentItem = pos;
			for (int i = 0; i < dotViewsList.size(); i++) {
				if (i == pos) {
					((View) dotViewsList.get(pos)).setBackgroundResource(R.mipmap.indicators_now);
				} else {
					((View) dotViewsList.get(i)).setBackgroundResource(R.mipmap.indicators_default);
				}
			}
		}
	}

	/**
	 * 执行轮播图切换任务
	 *
	 */
	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViewsList.size();
				handler.obtainMessage().sendToTarget();
			}
		}
	}

	/**
	 * 销毁ImageView资源，回收内存
	 */
	private void destoryBitmaps() {
		for (int i = 0; i < IMAGE_COUNT; i++) {
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			if (drawable != null) {
				// 解除drawable对view的引用
				drawable.setCallback(null);
			}
		}
	}
}
