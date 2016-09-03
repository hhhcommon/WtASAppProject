package com.woting.activity.interphone.creatgroup.creat.util;

public class ImageUploadReturnUtil {
	
	public static String getResPonse(String res) 
	{
        if(res!=null){
        	res=res.substring(8,res.length()-2);
        }
		return res;
	}
}
