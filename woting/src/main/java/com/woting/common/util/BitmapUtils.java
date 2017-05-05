package com.woting.common.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.woting.common.manager.FileManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 图片工具
 *
 * @author 辛龙
 *         2016年8月5日
 */
public class BitmapUtils {

    /**
     * 以最省内存的方式读取本地资源的图片
     * Android中图片有四种属性，分别是：
     * ALPHA_8：每个像素占用1byte内存
     * ARGB_4444：每个像素占用2byte内存
     * ARGB_8888：每个像素占用4byte内存 （默认）
     * RGB_565：每个像素占用2byte内存
     * Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，
     * 显示质量最高。但同样的，占用的内存也最大。
     * 所以在对图片效果不是特别高的情况下使用RGB_565（565没有透明度属性* @param context
     *
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 压缩图片A
     *
     * @param image 压缩前图片
     * @return 压缩后图片
     */
    public static Bitmap compressImage_A(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            Log.e("Crash", "压缩前" + baos.toByteArray().length / 1024);
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
            Log.e("Crash", "压缩后" + baos.toByteArray().length / 1024);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 压缩图片B （建议使用）
     *
     * @param image 压缩前图片
     * @return 压缩后图片
     */
    public static Bitmap compressImage_B(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 80) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            Log.e("Crash", "压缩前" + baos.toByteArray().length / 1024);
            options -= 10;//每次都减少10
            if (options < 50) {
                break;
            } else {
                baos.reset();//重置baos即清空baos
            }
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            Log.e("Crash", "压缩后" + baos.toByteArray().length / 1024);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 解码文件，把文件变成图片
     *
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
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            Log.e("BitmapUtils", e.toString() + "");
        }
        return null;
    }

    /**
     * 计算图片大小
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        //原始图片大小
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 保存图片
     *
     * @param context
     * @param bitmap
     * @return
     * @throws Exception
     */
    public static String saveBitmap(Context context, Bitmap bitmap) throws Exception {
        //		File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String savepath = FileManager.getImageSaveFilePath(context);
        FileManager.createDirectory(savepath);
        String fileName = "wt-" + System.currentTimeMillis() + ".jpg";
        //		File file = new File(outDir, fileName);
        File file = new File(savepath, fileName);
        if (file != null && file.exists()) {
            file.delete();
        }
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
        out.flush();
        out.close();
        return savepath + fileName;
    }

    /**
     * 获取图片路径
     * @param context
     * @param uri
     * @return 图片路径
     */
    public static String getFilePath(Context context, Uri uri) {
        int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
        String path;
        if (sdkVersion >= 19) {
            path = BitmapUtils.getPath_above19(context, uri);
        } else {
            path = BitmapUtils.getFilePath_below19(context, uri);
        }
        return path;
    }

    // API19 以下获取图片路径的方法
    private static String getFilePath_below19(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        System.out.println("***************" + column_index);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        System.out.println("path:" + path);
        return path;
    }

    /**
     * API level 19以上才有
     * 创建项目时，我们设置了最低版本 API Level，比如我的是 10，
     * 因此，AS 检查我调用的 API 后，发现版本号不能向低版本兼容，
     * 比如我用的“DocumentsContract.isDocumentUri(context, uri)”是 Level 19 以上才有的，
     * 自然超过了10，所以提示错误。
     * 添加    @TargetApi(Build.VERSION_CODES.KITKAT)即可。
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getPath_above19(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
