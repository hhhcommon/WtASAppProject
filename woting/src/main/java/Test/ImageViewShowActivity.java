//package Test;
//
//import java.util.ArrayList;
//
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.woting.R;
//import com.woting.common.util.PhoneMessage;
//import com.woting.ui.baseactivity.BaseActivity;
//
//public class ImageViewShowActivity extends BaseActivity implements View.OnClickListener {
//	private TextView tv_num;
//	private ViewPager mViewPager;
//	private String list;
//	private int num;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_image_show);
//
//		num=this.getIntent().getIntExtra("num", 0);
//		list = this.getIntent().getStringExtra("list");
//
//		tv_num=(TextView)findViewById(R.id.tv_num);
//		findViewById(R.id.tv_num).setOnClickListener(this);
//		mViewPager = (ViewPager)findViewById(R.id.viewpager);
//		mViewPager.setOffscreenPageLimit(1);
//		setData();
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.head_left_btn:
//			finish();
//		break;
//		}
//	}
//
//	private void setData() {
//		final ArrayList<View> mViews;
//		if(list!=null&&!list.trim().equals("")){
//			if(list.contains("YMKJ")){
//				String url[] = list.split("YMKJ");
//				  mViews = new ArrayList<View>();
//				for(int i=0;i<url.length;i++){
//					View layout = LayoutInflater.from(this).inflate(R.layout.layout_image, null);
//					ImageView imageView = (ImageView) layout.findViewById(R.id.imageView);
//					LayoutParams pra=imageView.getLayoutParams();
//					pra.width= PhoneMessage.ScreenWidth;
//					imageView.setLayoutParams(pra);
//					imageLoader.DisplayImage(url[i].replace( "\\/", "/"), imageView, false, false, null,null);
//					mViews.add(layout);
//				}
//				tv_num.setText("1/"+mViews.size());
//
//			}else{
//				mViews = new ArrayList<View>();
//				View layout = LayoutInflater.from(this).inflate(R.layout.layout_image, null);
//				ImageView imageView = (ImageView) layout.findViewById(R.id.imageView);
//				imageLoader.DisplayImage(list.replace( "\\/", "/"), imageView, false, false, null,null);
//				LayoutParams pra=imageView.getLayoutParams();
//				pra.width=PhoneMessage.ScreenWidth;
//				imageView.setLayoutParams(pra);
//				mViews.add(layout);
//				tv_num.setText("1/"+mViews.size());
//			}
//			tv_num.setText(String.valueOf(num+1)+"/"+mViews.size());
//			mViewPager.setAdapter(new MyPagerAdapter());
//			mViewPager.setCurrentItem(num);
//			mViewPager.setOnTouchListener(new View.OnTouchListener() {
//
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					v.getParent().requestDisallowInterceptTouchEvent(true);
//					return false;
//				}
//
//			});
//
//			mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
//				@Override
//				public void onPageSelected(int arg0) {
//				}
//
//				@Override
//				public void onPageScrolled(int arg0, float arg1, int arg2) {
//					tv_num.setText((arg0+1)+"/"+mViews.size());
//					mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
//				}
//
//				@Override
//				public void onPageScrollStateChanged(int arg0) {
//				}
//			});
//		}else{
//			mViewPager.setVisibility(View.GONE);
//		}
//
//
//	}
//
//	private class MyPagerAdapter extends PagerAdapter{
//		@Override
//		public void destroyItem(View v, int position, Object obj) {
//			((ViewPager)v).removeView(mViews.get(position));
//		}
//
//		@Override
//		public void finishUpdate(View arg0) {
//		}
//
//		@Override
//		public int getCount() {
//			return mViews.size();
//		}
//
//		@Override
//		public Object instantiateItem(View v, int position) {
//			((ViewPager)v).addView(mViews.get(position));
//			return mViews.get(position);
//		}
//
//		@Override
//		public boolean isViewFromObject(View arg0, Object arg1) {
//			return arg0 == arg1;
//		}
//
//		@Override
//		public void restoreState(Parcelable arg0, ClassLoader arg1) {
//		}
//
//		@Override
//		public Parcelable saveState() {
//			return null;
//		}
//
//		@Override
//		public void startUpdate(View arg0) {
//		}
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode,KeyEvent event) {
//		// 是否触发按键为back键
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			finish();
//		}
//		return super.onKeyDown(keyCode,event);
//	}
//
//}
