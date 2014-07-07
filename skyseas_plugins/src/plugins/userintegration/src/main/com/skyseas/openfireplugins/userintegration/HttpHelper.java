package com.skyseas.openfireplugins.userintegration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

public final class HttpHelper {
	private static final ObjectMapper 	objectMapper 	= new ObjectMapper(); /* 线程安全 */
	private HttpHelper() {}

	
	public static int request(String targetUrl, String method, Object content) throws IOException {
		URL url = new URL(targetUrl);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection(); 
		connection.setRequestMethod(method);
		
		if(content != null) {
			// 写入json内容主体
			byte[] data = objectMapper.writeValueAsBytes(content);
			connection.setDoOutput(true);
			connection.setRequestProperty("content-length", String.valueOf(data.length));
			connection.setRequestProperty("content-type", "application/json");
			connection.getOutputStream().write(data);
		}
		return connection.getResponseCode();
	}
	
	
}
