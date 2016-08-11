package com.angm.jikeb.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import android.util.Log;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
/**
 * 网络请求工具
 * 
 * @author 彭计伟
 *
 */
public class OkHttpUtils {
  
	public String result = "";
	
	static OkHttpUtils oUtils;
	
	private static HttpCallBackListener mycall;   
   
	public static OkHttpUtils getInstance(HttpCallBackListener callback) {
		mycall = callback;
		if (oUtils == null) {
			oUtils = new OkHttpUtils();
		}
		return oUtils;
	}
	/**
	 * okhttp get请求
	 * @param type 编码类型
	 * @param url_get 请求地址
	 * @return
	 */
	public OkHttpUtils GET_DATA(String url_get) {
		
		result = "";
		
		OkHttpClient okHttpClient = new OkHttpClient();
		
		Request request = new Request.Builder().url(url_get).build();
		
		okHttpClient.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Response arg0) throws IOException {

				byte[] b = arg0.body().bytes();
				result = new String(b, "utf-8");
				mycall.response(result);
			}
			
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				
			}
			
		});

		return null;
	}
    /**
     * okhttp post
     * 
     * @param url_post post请求地址
     * @param type 编码类型
     * @param params post 传入参数
     * @return 返回string 
     */
	@SuppressWarnings("rawtypes")
	public void POST_DATA(String url_post, Map<String, Object> params) {
		
		result = "";
		
		OkHttpClient okHttpClient = new OkHttpClient();
		
		FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
		
		if(params != null){
			Set entrySet = params.entrySet();
			Iterator it = entrySet.iterator();
			while(it.hasNext()){
				Entry me = (Entry) it.next();
				String k = (String) me.getKey();  
				String v = (String) me.getValue(); 
				formEncodingBuilder.add(k, v);
			}
		}
       	Request request = new Request.Builder().url(url_post).post(formEncodingBuilder.build()).build();	
       
       	okHttpClient.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Response arg0) throws IOException {

				byte[] b = arg0.body().bytes();
				result = new String(b, "utf-8");
				mycall.response(result);
			}
			
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Log.i("OkHttpUtils", arg0.body().toString());
			}
		});
		
	}
	/**
	 * 同步
	 * @param url_get get请求地址
	 * @param type 编码类型
	 * @return 返回string 
	 */
	public void GET_DATA_TONG(String url_get, final String type){
		
		result = "";
		
		OkHttpClient okHttpClient = new OkHttpClient();
		
		Request request = new Request.Builder().url(url_get).build();
		
		
		try {
			Response execute = okHttpClient.newCall(request).execute();
			if(execute.isSuccessful()){
				result = execute.body().string();
				mycall.response(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
    /**
     * 同步
     * okhttp post
     * @param url_post post请求地址
     * @param type 编码类型
     * @param params post 传入参数
     * @return 返回string 
     */

	@SuppressWarnings("rawtypes")
	public void POST_DATA_TONG(String url_post, final String type, Map<String, Object> params) {
		
		result = "";
		
		OkHttpClient okHttpClient = new OkHttpClient();
		
		FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
		
		if(params != null){
			Set entrySet = params.entrySet();
			Iterator it = entrySet.iterator();
			while(it.hasNext()){
				Entry me = (Entry) it.next();
				String k = (String) me.getKey();  
				String v = (String) me.getValue(); 
				formEncodingBuilder.add(k, v);
			}
		}
       	Request request = new Request.Builder().url(url_post).post(formEncodingBuilder.build()).build();	
       
       
		try {
			Response execute = okHttpClient.newCall(request).execute();
			if(execute.isSuccessful()){
				result = execute.body().string();
				mycall.response(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 网络请求回调方法
	 * @author 彭计伟
	 *
	 */
	public interface HttpCallBackListener {
		public void response(String result);
	}
	
}
