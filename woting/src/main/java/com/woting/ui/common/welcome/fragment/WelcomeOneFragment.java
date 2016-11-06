package com.woting.ui.common.welcome.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.woting.R;
import com.woting.common.util.BitmapUtils;

/**
 * 第一张引导页
 * @author 辛龙
 * 2016年4月27日
 */
public class WelcomeOneFragment extends Fragment  {
	private Bitmap bmp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.item_welcomea, container, false);
		ImageView imageView1 = (ImageView)rootView.findViewById(R.id.imageView1);
		bmp = BitmapUtils.readBitMap(getActivity(), R.mipmap.welcomea);
		imageView1.setImageBitmap(bmp);
		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(bmp != null && !bmp.isRecycled()) {  
			bmp.recycle();
            bmp = null;
		} 
	}
}
