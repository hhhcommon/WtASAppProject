package com.woting.common.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.util.Log;
/**
 * http数据请求交互 单例方式
 * @author 辛龙
 */
public class MyHttp {
	private static String TAG="MyHttp";
	private static final String CHARSET = HTTP.UTF_8;
	public static HttpClient httpClient;
	public static  String cookieStr;
	public static HttpClient getHttp(){
		if(null==httpClient){
			 HttpParams params =new BasicHttpParams();
	            // 设置一些基本参数
	            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	            HttpProtocolParams.setContentCharset(params,
	                    CHARSET);
	            HttpProtocolParams.setUseExpectContinue(params, true);
	            HttpProtocolParams
	                    .setUserAgent(
	                            params,
	                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
	                                    +"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
	            // 超时设置
	            /* 从连接池中取连接的超时时间 */
	            ConnManagerParams.setTimeout(params, 30000);
	            /* 连接超时 */
	            HttpConnectionParams.setConnectionTimeout(params, 100000);
	            /* 请求超时 */
	            HttpConnectionParams.setSoTimeout(params, 80000);
	            
	            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
	            SchemeRegistry schReg =new SchemeRegistry();
	            schReg.register(new Scheme("http", PlainSocketFactory
	                    .getSocketFactory(), 80));
	            schReg.register(new Scheme("https", SSLSocketFactory
	                    .getSocketFactory(), 443));
	         // 使用线程安全的连接管理来创建HttpClient
	            ClientConnectionManager conMgr =new ThreadSafeClientConnManager(
	                    params, schReg);
	            httpClient= new DefaultHttpClient(conMgr,params);
		}
		return httpClient;
	}
	/**
	 * 通过一个地址获取内容
	 * @param paramString 
	 * @throws UnsupportedEncodingException
	 * @throws IllegalStateException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String httpGet(String paramString) throws UnsupportedEncodingException, IllegalStateException, ClientProtocolException, IOException{
		HttpClient httpClient = getHttp();
	    HttpUriRequest httpUriRequest = new HttpGet(paramString);
	      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((httpClient).execute(httpUriRequest).getEntity().getContent(), "UTF-8"));
	      StringBuilder stringBuilder = new StringBuilder();
	      String str = bufferedReader.readLine();
	      CookieStore mCookieStore = ((AbstractHttpClient) httpClient).getCookieStore();
          List<org.apache.http.cookie.Cookie> cookies =  mCookieStore.getCookies();
          for (int i = 0; i < cookies.size(); i++) {
               //这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
        	  cookieStr=cookies.get(i).getName()+"="+cookies.get(i).getValue();
          }
	      if (str == null||str.equals("")){
	    	  stringBuilder.append(str);
		      str = bufferedReader.readLine();
		      return null;
	      }else{
		       return str;
	      }
	     
	}
	/**
	 * 传递参数的数据请求
	 * @param paramString
	 * @param paramList
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static String httpPost(String paramString, List <NameValuePair> paramList) throws ClientProtocolException, IOException{
	    HttpClient defaultHttpClient = getHttp();
	    HttpUriRequest httpPost = new HttpPost(paramString);
	    if(null!=paramList)
	    	((HttpEntityEnclosingRequestBase) httpPost).setEntity(new UrlEncodedFormEntity((List<? extends org.apache.http.NameValuePair>) paramList, "UTF-8"));
	     HttpEntity httpEntity = defaultHttpClient.execute(httpPost).getEntity();
	      if (httpEntity == null)
	        return null;
	      String resultStr = EntityUtils.toString(httpEntity);
	      CookieStore mCookieStore = ((AbstractHttpClient) defaultHttpClient).getCookieStore();
          List<org.apache.http.cookie.Cookie> cookies =  mCookieStore.getCookies();
          for (int i = 0; i < cookies.size(); i++) {
               //这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
        	  cookieStr=cookies.get(i).getName()+"="+cookies.get(i).getValue();
          }
	      return resultStr;
	  }
	/**
	 * 上传文件
	 * @param file 文件
	 * @param url 地址
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String postFile(File file,String url) throws ClientProtocolException, IOException {  
		FileBody bin = null;
        HttpClient httpclient = getHttp();  
        HttpPost httppost = new HttpPost(url);  
        if(file != null) {  
            bin = new FileBody(file);  
        }  
        MultipartEntity reqEntity = new MultipartEntity();  
        reqEntity.addPart("upfile", bin);  
        httppost.setEntity(reqEntity);  
        Log.i(TAG,"执行: " + httppost.getRequestLine());  
        HttpResponse response = httpclient.execute(httppost);  
        Log.i(TAG,"statusCode is " + response.getStatusLine().getStatusCode());  
        HttpEntity resEntity = response.getEntity();  
        Log.i(TAG,""+response.getStatusLine());  
        if (resEntity != null) {  
        	 String resultStr = EntityUtils.toString(resEntity);
        	 resEntity.consumeContent();  
        	 return resultStr;
        }  
        return null;  
    }  
	/**
	 * 
	 * @param urlpath
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	 public static String getByUrl( String urlpath,String encoding) throws Exception {
		 URL url = new URL(urlpath);
		 //实例化一个HTTP连接对象conn
		 HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		 //定义请求方式为GET，其中GET的格式需要注意
		 conn.setRequestMethod("GET");
		 //定义请求时间，在ANDROID中最好是不好超过10秒。否则将被系统回收。
		 conn.setConnectTimeout(6*1000);
		 if(conn.getResponseCode()== 200){
			 InputStream inStream = conn.getInputStream();
			 byte[] data = readStream(inStream);
			 Log.e("ndk", new String(data));
			return new String(data,encoding);
			
		 }	
		 return null;
	 }	
	 
	public static  byte[] readStream(InputStream inStream) throws Exception{
		 //readStream获得了传递进来的输入流
		 //要返回输入流，就需要定义一个输出流。
		 //定义一个字节数组型的输出流，ByteArrayOutputStream
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			//建立一个缓冲区buffer
			byte[] buffer = new byte[1024];
			int len= -1;
			//将输入流不断的读，并放到缓冲区中去。直到读完
			while((len=inStream.read(buffer))!=-1){
				//将缓冲区的数据不断的写到内存中去。边读边写
				outStream.write(buffer, 0, len);
			}
			outStream.close();
			inStream.close();
			//将输出流以字节数组的方式返回
			return outStream.toByteArray();
		} 
	/**
	 * 
	 * @param uploadUrl
	 * @param srcPath
	 * @return
	 */
	public static String uploadFile(String uploadUrl,String srcPath) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		try {
			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
			// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"myFile\"; filename=\""
					+  URLEncoder.encode(srcPath.substring(srcPath.lastIndexOf("/") + 1))
					+ "\""
					+ end);
			dos.writeBytes(end);
			FileInputStream fis = new FileInputStream(srcPath);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			// 读取文件
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();
			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();
			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			dos.close();
			is.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
