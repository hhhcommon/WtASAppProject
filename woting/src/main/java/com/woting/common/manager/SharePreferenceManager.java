package com.woting.common.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 快速保存数据
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class SharePreferenceManager{

	public synchronized static void saveBatchSharedPreference(Context context,String filename,Object name,Object value)	{
		SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(filename, 0);
		Editor editor = sharedPreferences.edit();
		if(name instanceof String)	{
			if(value instanceof Boolean){
				editor.putBoolean(String.valueOf(name), (Boolean)value);
			}else if(value instanceof Float){
				editor.putFloat(String.valueOf(name), (Float)value);
			}else if(value instanceof Integer){
				editor.putInt(String.valueOf(name), (Integer)value);
			}else if(value instanceof Long){
				editor.putLong(String.valueOf(name), (Long)value);
			}else{
				editor.putString(String.valueOf(name), String.valueOf(value));
			}
		}else if(name instanceof String[]){
			String str[] = (String[])name;
			if(value instanceof Boolean[]){
				Boolean bvalue[] = (Boolean[])value;
				for(int i=0;i<str.length;i++){
					editor.putBoolean(str[i], bvalue[i]);
				}
			}else if(value instanceof Float[]){
				Float fvalue[] = (Float[])value;
				for(int i=0;i<str.length;i++){
					editor.putFloat(str[i], fvalue[i]);
				}
			}else if(value instanceof Integer[]){
				Integer ivalue[] = (Integer[])value;
				for(int i=0;i<str.length;i++){
					editor.putInt(str[i], ivalue[i]);
				}
			}else if(value instanceof Long[]){
				Long lvalue[] = (Long[])value;
				for(int i=0;i<str.length;i++){
					editor.putLong(str[i], lvalue[i]);
				}
			}else{
				String svalue[] = (String[])value;
				for(int i=0;i<str.length;i++){
					editor.putString(str[i], svalue[i]);
				}			
			}
		}
		editor.commit();
	}

	public synchronized static Object getSharePreferenceValue(Context context,String filename,Object name,Object defValue){
		SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(filename, 0);
		Object value = null;
		if(name instanceof String){
			if(defValue instanceof Boolean){
				value = sharedPreferences.getBoolean(String.valueOf(name), (Boolean)defValue);
			}else if(defValue instanceof Float){
				value = sharedPreferences.getFloat(String.valueOf(name), (Float)defValue);
			}else if(defValue instanceof Integer){
				value = sharedPreferences.getInt(String.valueOf(name), (Integer)defValue);
			}else if(defValue instanceof Long){
				value = sharedPreferences.getLong(String.valueOf(name), (Long)defValue);
			}	else{
				value = sharedPreferences.getString(String.valueOf(name), (String)defValue);
			}
		}else	{
			String str[] = (String[])name;
			if(defValue instanceof Boolean[]){
				Boolean defbvalue[] = (Boolean[])defValue;
				Boolean bvalue[] = new Boolean[defbvalue.length];
				for(int i=0;i<str.length;i++){
					bvalue[i] = new Boolean(sharedPreferences.getBoolean(str[i], defbvalue[i]));;
				}
				value = bvalue;
			}
			else if(defValue instanceof Float[]){
				Float deffvalue[] = (Float[])defValue;
				Float fvalue[] = new Float[deffvalue.length];
				for(int i=0;i<str.length;i++)	{
					fvalue[i] = new Float(sharedPreferences.getFloat(str[i], deffvalue[i]));
				}
				value = fvalue;
			}	else if(defValue instanceof Integer[]){
				Integer defivalue[] = (Integer[])defValue;
				Integer ivalue[] = new Integer[defivalue.length];
				for(int i=0;i<str.length;i++)	{
					ivalue[i] = new Integer(sharedPreferences.getInt(str[i], defivalue[i]));
				}
				value = ivalue;
			}
			else if(defValue instanceof Long[]){
				Long deflvalue[] = (Long[])defValue;
				Long lvalue[] = new Long[deflvalue.length];
				for(int i=0;i<str.length;i++){
					lvalue[i] = new Long(sharedPreferences.getLong(str[i], deflvalue[i]));
				}
				value = lvalue;
			}
			else	{
				String defsvalue[] = (String[])defValue;
				String svalue[] = new String[defsvalue.length];
				for(int i=0;i<str.length;i++){
					svalue[i] = new String(sharedPreferences.getString(str[i], defsvalue[i]));
				}
				value = svalue;		
			}
		}
		return value;
	}
	
	/**
	 * 保存数据
	 * @param up 需要保存的数据
	 */
	public static void setupdate(String up){
		SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd-HH:mm:ss-SSS ");     
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间     
		String    addtime    =    formatter.format(curDate);
		try {
			String filePath= Environment.getExternalStorageDirectory() + "/woting/log/";
			File dir=new File(filePath);
			if (!dir.isDirectory()) dir.mkdirs();
			FileOutputStream out = null;
			try {
				byte[] b = up.getBytes();
				File outputFile = new File(dir,addtime);
				outputFile.createNewFile();
				out = new FileOutputStream(outputFile);
				out.write(b);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {if (out!=null) out.close();} catch(Exception e){} finally{out=null;};
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
