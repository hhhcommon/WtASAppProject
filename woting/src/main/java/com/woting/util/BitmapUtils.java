package com.woting.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.woting.manager.FileManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 图片工具
 * @author 辛龙
 *2016年8月5日
 */
public class BitmapUtils {

	/** 
	 * 以最省内存的方式读取本地资源的图片 
	 *	Android中图片有四种属性，分别是：
	 *ALPHA_8：每个像素占用1byte内存
	 *ARGB_4444：每个像素占用2byte内存
	 *ARGB_8888：每个像素占用4byte内存 （默认）
	 *RGB_565：每个像素占用2byte内存
	 *Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，
	 *显示质量最高。但同样的，占用的内存也最大。
	 *所以在对图片效果不是特别高的情况下使用RGB_565（565没有透明度属性* @param context 
	 * @param resId 
	 * @return 
	 */  
	public static Bitmap readBitMap(Context context, int resId){  
		BitmapFactory.Options opt = new BitmapFactory.Options();  
		opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		opt.inPurgeable = true;  
		opt.inInputShareable = true;  
		//获取资源图片  
		InputStream is = context.getResources().openRawResource(resId);  
		return BitmapFactory.decodeStream(is,null,opt);
	}

	/**
	 * 压缩图片A 
	 * @param image 压缩前图片
	 * @return  压缩后图片
	 */
	public static Bitmap compressImage_A(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			Log.e("Crash", "压缩前"+baos.toByteArray().length / 1024);
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
			Log.e("Crash", "压缩后"+baos.toByteArray().length / 1024);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 压缩图片B （建议使用）
	 * @param image 压缩前图片
	 * @return  压缩后图片
	 */
	public static Bitmap compressImage_B(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024>80) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩	
			Log.e("Crash", "压缩前"+baos.toByteArray().length / 1024);
			options -= 10;//每次都减少10
			if(options<50){
				break;
			}else{
				baos.reset();//重置baos即清空baos
			}
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			Log.e("Crash", "压缩后"+baos.toByteArray().length / 1024);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 解码文件，把文件变成图片
	 * @param f 保存图片信息的文件
	 * @return 图片
	 */
	public static Bitmap decodeFile(File f) {
		try {
			//			 decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 480;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE|| height_tmp / 2 < REQUIRED_SIZE) break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			Log.e("BitmapUtils", e.toString()+"");
		}
		return null;
	}

	/**
	 * 计算图片大小
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static  int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
		// Raw height and width of image
		//原始图片大小
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);            
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	
	/**
	 * 保存图片
	 * @param context
	 * @param bitmap
	 * @return
	 * @throws Exception
	 */
	public static String  saveBitmap(Context context,Bitmap bitmap) throws Exception{
		//		File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		String savepath = FileManager.getImageSaveFilePath(context);
		FileManager.createDirectory(savepath);
		String fileName="wt-"+System.currentTimeMillis()+".jpg";
		//		File file = new File(outDir, fileName);
		File file = new File(savepath,fileName);
		if (file != null && file.exists()){
			file.delete();
		}
		FileOutputStream out = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
		out.flush();
		out.close();
		return savepath+fileName;
	}

}
