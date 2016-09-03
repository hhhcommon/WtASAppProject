package com.woting.helper;//package com.wotingfm.helper;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Collections;
//import java.util.Map;
//import java.util.WeakHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//
//import com.shenstec.utils.file.AbstractFileCache;
//import com.shenstec.utils.file.FileCache;
//import com.shenstec.utils.image.ImageUtils;
//import com.shenstec.utils.image.MemoryCache;
///**
// * 图片异步加载处理
// * @author 辛龙
// *2016年8月5日
// */
//public class ImageLoader_gray {
//	private ImageLoadListener loadListener;
//	private MemoryCache memoryCache = new MemoryCache();
//	private AbstractFileCache fileCache;
//	private Map<ImageView, String> imageViews = Collections
//			.synchronizedMap(new WeakHashMap<ImageView, String>());
//	private Map<ProgressBar, String> probars = Collections
//			.synchronizedMap(new WeakHashMap<ProgressBar, String>());
//	// 线程池
//	private ExecutorService executorService;
//	/**
//	 * 
//	 * @param context
//	 */
//	public ImageLoader_gray(Context context) {
//		fileCache = new FileCache(context);
//		executorService = Executors.newFixedThreadPool(5);
//	}
//
//	/**
//	 * 图片加载
//	 * @param url 图片地址
//	 * @param imageView 显示图片的控件
//	 * @param isLoadOnlyFromCache 是否只从缓存加载 false 缓存如果没有则网络下载
//	 * @param isDecodeBitmap 是否压缩
//	 * @param loadListener 下载完成事件 如果不需要，请设置为null
//	 * @param progressBar 下载进度条 如果不需要进度条，请设置为null
//	 */
//	public void DisplayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache,boolean isDecodeBitmap,ImageLoadListener loadListener,ProgressBar progressBar) {
//		imageViews.put(imageView, url);
//		probars.put(progressBar, url);
//		// 先从内存缓存中查找
//		this.loadListener=loadListener;
//		Bitmap bitmap = memoryCache.get(url);
//		if (bitmap != null){
//			Bitmap bitmaps = grey (bitmap);
//			imageView.setImageBitmap(bitmaps);
//			if(progressBar!=null)
//				progressBar.setVisibility(View.GONE);
//			if(this.loadListener!=null)
//				this.loadListener.complete();
//		}else if (!isLoadOnlyFromCache){
//			// 若没有的话则开启新线程加载图片
//			queuePhoto(url, imageView,isDecodeBitmap,progressBar);
//		}
//	}
//	
////	public static final Bitmap grey(Bitmap bitmap) {
////		 int width = bitmap.getWidth();
////		 int height = bitmap.getHeight();
////		 Bitmap faceIconGreyBitmap = Bitmap
////		   .createBitmap(width, height, Bitmap.Config.ARGB_8888);
////		 Canvas canvas = new Canvas(faceIconGreyBitmap);
////		 Paint paint = new Paint();
////		 ColorMatrix colorMatrix = new ColorMatrix();
////		 colorMatrix.setSaturation(0);
////		 ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
////		   colorMatrix);
////		 paint.setColorFilter(colorMatrixFilter);
////		 canvas.drawBitmap(bitmap, 0, 0, paint);
////		 return faceIconGreyBitmap;
////		}
//	
////	public static Bitmap grey(Bitmap bmpOriginal)
////    {        
////        int width, height;
////        height = bmpOriginal.getHeight();
////        width = bmpOriginal.getWidth();    
////
////        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
////        Canvas c = new Canvas(bmpGrayscale);
////        Paint paint = new Paint();
////        ColorMatrix cm = new ColorMatrix();
////        cm.setSaturation(0);
////        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
////        paint.setColorFilter(f);
////        c.drawBitmap(bmpOriginal, 0, 0, paint);
////        return bmpGrayscale;
////    }
//	
//	 /**
//     * 将彩色图转换为灰度图
//     * @param img 位图
//     * @return  返回转换好的位图
//     */ 
//    public Bitmap grey(Bitmap img) { 
//        int width = img.getWidth();         //获取位图的宽  
//        int height = img.getHeight();       //获取位图的高  
//         
//        int []pixels = new int[width * height]; //通过位图的大小创建像素点数组  
//         
//        img.getPixels(pixels, 0, width, 0, 0, width, height); 
//        int alpha = 0xFF << 24;  
//        for(int i = 0; i < height; i++)  { 
//            for(int j = 0; j < width; j++) { 
//                int grey = pixels[width * i + j]; 
//                 
//                int red = ((grey  & 0x00FF0000 ) >> 16); 
//                int green = ((grey & 0x0000FF00) >> 8); 
//                int blue = (grey & 0x000000FF); 
//                 
//                grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11); 
//                grey = alpha | (grey << 16) | (grey << 8) | grey; 
//                pixels[width * i + j] = grey; 
//            } 
//        } 
//        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565); 
//        result.setPixels(pixels, 0, width, 0, 0, width, height); 
//        return result; 
//    } 
//
//	private void queuePhoto(String url, ImageView imageView,boolean isDecodeBitmap,ProgressBar progressBar) {
//		PhotoToLoad p = new PhotoToLoad(url, imageView,isDecodeBitmap,progressBar);
//		executorService.submit(new PhotosLoader(p));
//	}
//	/**
//	 * 根据网络地址获取bitmap 等比例缩放后的
//	 * @param url
//	 * @return
//	 */
//	private Bitmap getBitmap(String url,boolean isDecodeBitmap,ProgressBar progressBar) {
//		// 先从文件缓存中查找是否有
//		File f = fileCache.getFile(url);
//		Bitmap b = null;
//		if (f != null && f.exists()){
//			if(isDecodeBitmap){
//				b = ImageUtils.decodeFile(f);
//			}else{
//				b =ImageUtils.readBitMap(f.getAbsolutePath());
//			}
//		}
//		if (b != null){
//			return b;
//		}
//		// 最后从指定的url中下载图片
//		try {
//			Bitmap bitmap = null;
//			URL imageUrl = new URL(url);
//			HttpURLConnection conn = (HttpURLConnection) imageUrl
//					.openConnection();
//			conn.setConnectTimeout(30000);
//			conn.setReadTimeout(30000);
//			conn.setInstanceFollowRedirects(true);
//			if(progressBar!=null)
//				progressBar.setMax(conn.getContentLength());
//			InputStream is = conn.getInputStream();
//			OutputStream os = new FileOutputStream(f);
//			CopyStream(is, os,progressBar);
//			os.close();
//			if(isDecodeBitmap){
//				bitmap = ImageUtils.decodeFile(f);
//			}else{
//				bitmap =ImageUtils.readBitMap(f.getAbsolutePath());
//			}
//			Bitmap bitmaps = grey (bitmap);
////			bitmap = ImageUtils.decodeFile(f);
//			return bitmaps;
//		} catch (Exception ex) {
//			Log.e("", "getBitmap catch Exception...\nmessage = " + ex.getMessage());
//			return null;
//		}
//	}
//
//	// Task for the queue
//	private class PhotoToLoad {
//		public String url;
//		public ImageView imageView;
//		public boolean isDecodeBitmap;
//		public ProgressBar progressBar;
//
//		public PhotoToLoad(String u, ImageView i,boolean isDecodeBitmap,ProgressBar progressBar) {
//			url = u;
//			imageView = i;
//			this.isDecodeBitmap=isDecodeBitmap;
//			this.progressBar=progressBar;
//		}
//	}
//
//	class PhotosLoader implements Runnable {
//		PhotoToLoad photoToLoad;
//		PhotosLoader(PhotoToLoad photoToLoad) {
//			this.photoToLoad = photoToLoad;
//		}
//
//		@Override
//		public void run() {
//			if (imageViewReused(photoToLoad))
//				return;
//			Bitmap bmp = getBitmap(photoToLoad.url,photoToLoad.isDecodeBitmap,photoToLoad.progressBar);
//			memoryCache.put(photoToLoad.url, bmp);
//			if (imageViewReused(photoToLoad))
//				return;
//			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
//			// 更新的操作放在UI线程中
//			Activity a = (Activity) photoToLoad.imageView.getContext();
//			a.runOnUiThread(bd);
//		}
//	}
//
//	/**
//	 * 防止图片错位
//	 * 
//	 * @param photoToLoad
//	 * @return
//	 */
//	boolean imageViewReused(PhotoToLoad photoToLoad) {
//		String tag = imageViews.get(photoToLoad.imageView);
//		if (tag == null || !tag.equals(photoToLoad.url))
//			return true;
//		return false;
//	}
//
//	// 用于在UI线程中更新界面
//	class BitmapDisplayer implements Runnable {
//		Bitmap bitmap;
//		PhotoToLoad photoToLoad;
//
//		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
//			bitmap = b;
//			photoToLoad = p;
//		}
//
//		public void run() {
//			if (imageViewReused(photoToLoad))
//				return;
//			if (bitmap != null){
//				photoToLoad.imageView.setImageBitmap(bitmap);
//				if(photoToLoad.progressBar!=null){
//					photoToLoad.progressBar.setVisibility(View.GONE);
//					Log.e("Client", "下载结束");
//				}else{
//				}
//				if(loadListener!=null)
//					loadListener.complete();
//			}
//		}
//	}
//
//	public void clearCache() {
//		memoryCache.clear();
//		fileCache.clear();
//	}
//
//	public static void CopyStream(InputStream is, OutputStream os,ProgressBar progressBar) {
//		final int buffer_size = 1024;
//		try {
//			int doneLen=0;
//			int len = 0;
//			byte[] bytes = new byte[buffer_size];
////			for (;;) {
////				doneLen = doneLen + len;
////				int count = is.read(bytes, 0, buffer_size);
////				if (count == -1)
////					break;
////				os.write(bytes, 0, count);
////				Log.e("Client", "下载进度"+doneLen);
////				if(progressBar!=null){
////					Log.e("Client", "下载进度条显示"+doneLen);
////					progressBar.setProgress(doneLen);
////				}
////			}
//			while ((len = is.read(bytes)) != -1) {
//				doneLen = doneLen + len;
//				os.write(bytes, 0, len);
//				if(progressBar!=null){
//					Log.e("Client", "下载进度条显示"+doneLen);
//					progressBar.setProgress(doneLen);
//				}
//			}
//		} catch (Exception ex) {
//			Log.e("", "CopyStream catch Exception...");
//		}
//	}
//	
//	public interface ImageLoadListener{
//		public void complete();
//	}
//}
