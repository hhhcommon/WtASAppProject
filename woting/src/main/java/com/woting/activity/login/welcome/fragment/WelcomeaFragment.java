package com.woting.activity.login.welcome.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.woting.R;
import com.woting.util.BitmapUtils;

/**
 *  第一张引导页
 * @author 辛龙
 * 2016年4月27日
 */
public class WelcomeaFragment extends Fragment  {
	private FragmentActivity context;
	private ImageView imageView1;
	private Bitmap bmp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentActivity context = this.getActivity();
		View rootView = inflater.inflate(R.layout.item_welcomea, container, false);
		ImageView imageView1 = (ImageView)rootView.findViewById(R.id.imageView1);
		bmp = BitmapUtils.readBitMap(context, R.mipmap.welcomea);
		imageView1.setImageBitmap(bmp);
		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(bmp != null && !bmp.isRecycled()) {  
			bmp.recycle();  
		} 
	}
}
