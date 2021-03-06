package com.feeyo.net.http.util;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class HlsRpcUtil {
	
	// Code define
	public static final int START_CODE 			= 1;
	public static final int CLOSE_CODE 			= 2;
	public static final int CLOSE_ALL_CODE 		= 3;
	
	public static final int UPD_ALIAS_CODE 		= 11;
	public static final int UPD_ADS_CODE 		= 12;

	private static int timeout = 5;
	private static RequestConfig config = RequestConfig.custom()
			.setConnectTimeout(timeout * 1000)
			.setConnectionRequestTimeout(timeout * 1000)
			.setSocketTimeout(timeout * 1000).build();
	
	private static HlsRpcUtil _instance;
	
	private HlsRpcUtil() {}

	public static HlsRpcUtil INSTANCE() {
		if ( _instance == null ) {
			synchronized ( HlsRpcUtil.class ) {
				if ( _instance == null ) {
					_instance =  new HlsRpcUtil();
				}
			}
		}
		return _instance;
	}
	
	private boolean post(String uri, JSONObject jsonObject) {
		
		try {
			
			String data = jsonObject.toJSONString();
			
			HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
			HttpPost post = new HttpPost( uri );
			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(data, "UTF-8"));

			HttpResponse response = client.execute(post);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
				if ( responseString.equals("OK"))
					return true;
			}

		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return false;
	}
	
	
	public boolean startLiveStream(String uri, long streamId, int streamType, List<String> aliasNames, 
			float sampleRate, int sampleSizeInBits, int channels, int fps) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", START_CODE);
		jsonObject.put("streamId", streamId);
		jsonObject.put("streamType", streamType);
		jsonObject.put("aliasNames", aliasNames);
		jsonObject.put("sampleRate", sampleRate);
		jsonObject.put("sampleSizeInBits", sampleSizeInBits);
		jsonObject.put("channels", channels);
		jsonObject.put("fps", fps);
		
		return post(uri, jsonObject);
	}
	
	public boolean closeLiveStream(String uri, long streamId) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", CLOSE_CODE);
		jsonObject.put("streamId", streamId);
		
		return post(uri, jsonObject);
	}
	
	public boolean closeAllLiveStream(String uri) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", CLOSE_ALL_CODE);
		
		return post(uri, jsonObject);
	}
	
	public boolean updateAliasNames(String uri, long streamId,  List<String> aliasNames) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", UPD_ALIAS_CODE);
		jsonObject.put("streamId", streamId);
		jsonObject.put("aliasNames", aliasNames);
		
		return post(uri, jsonObject);
	}
	
	public boolean updateAds(String uri, boolean isHasAds) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", UPD_ADS_CODE);
		jsonObject.put("isHasAds", isHasAds);
		
		return post(uri, jsonObject);
	}
	


}
