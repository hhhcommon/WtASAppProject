package com.woting.util;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.woting.R;
import com.woting.activity.home.player.main.model.sharemodel;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取分享的图标列表list
 * @author 辛龙
 *2016年8月5日
 */
public class ShareUtils {
	
	public static List<sharemodel> getShareModelList() {
		List<sharemodel> list=new ArrayList<sharemodel>();
		String[] textlist={"微信朋友圈","微信好友","QQ好友","QQ空间","新浪微博"};
		SHARE_MEDIA[] sharelist={SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA};
		int[] imglist={R.mipmap.img_loginwx,R.mipmap.img_loginwx,R.mipmap.img_loginqq,R.mipmap.img_loginqq,R.mipmap.img_loginwb};
		for(int i=0;i<textlist.length;i++){
			sharemodel sm=new sharemodel();
		 sm.setShareImageUrl(imglist[i]);
		 sm.setSharePlatform(sharelist[i]);
		 sm.setShareText(textlist[i]);
         list.add(sm);		 			
		}
		return list;  
	}  
}
