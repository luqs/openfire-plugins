package com.skyseas.openfireplugins.userintegration;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ����HTTP���û��¼������������û��¼���Ϣͨ��HTTP�ӿڽ��з�����
 * @author apple
 *
 */
public class HttpUserEventSubscriber implements UserEventSubscriber {
	private EventConfigItem[] 			eventConfigItems;
	private static final Logger 		Log 			= LoggerFactory.getLogger(HttpUserEventSubscriber.class);
	private static final ObjectMapper 	objectMapper 	= new ObjectMapper(); /* �̰߳�ȫ */

	public HttpUserEventSubscriber() {
		initConfig();
	}
	
	@Override
	public void publish(UserInfo user, UserEventType eventType) {

		assert user != null;
		assert eventType != null;
		
		// ��õ�ǰ�¼����͵Ķ���������
		EventConfigItem eventConfigItem = eventConfigItems[eventType.ordinal()];
		if(eventConfigItem.enabled) {
			publish(user, eventConfigItem);
		} else {
			Log.info(String.format("��ǰ�¼�����δ���ã�����publish��eventType:%s��", eventType));
		}
	}
	
	private void publish(UserInfo user, EventConfigItem eventConfigItem) {
		String targetUrl = eventConfigItem.getTargetUrl(user);
		if(targetUrl.length() > 0) {
			try {
				int httpStatusCode = httpRequest(
						eventConfigItem.method, 
						targetUrl, 
						eventConfigItem.sendConetntBody ? wrapContentBody(user) : null);
				
				if(httpStatusCode != HttpURLConnection.HTTP_OK) {
					Log.warn(String.format("��������δ�ɹ�, userName:%s, eventType:%s, httpStatusCode:%d", 
							user.getUsername(), eventConfigItem.eventType, httpStatusCode));
					
					System.out.print("no");
				}else {
					System.out.print("ok");
				}
				
			} catch (Exception e) {
				Log.error(String.format("��������ʧ��, userName:%s, eventType:%s", 
						user.getUsername(), eventConfigItem.eventType), e);
				System.out.print("no:" + e);
			}	
		} else {
			Log.warn(String.format(
					"û��Ϊ��ǰ�¼������ʵ���targeturl, ����publish��eventType:%s", 
					eventConfigItem.eventType));
		}
	}
	
	private Object wrapContentBody(UserInfo user) {
		HashMap<String,Object> map = new HashMap<String, Object>(1);
		map.put("user", user);
		return map;
	}
	
	private int httpRequest(String method, String targetUrl, Object contentBody) throws Exception{
		URL url = new URL(targetUrl);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection(); 
		
		try {
			connection.setRequestMethod(method);
			if(contentBody != null) {
				// д��json��������
				byte[] data = objectMapper.writeValueAsBytes(contentBody);
				connection.setDoOutput(true);
				connection.setRequestProperty("content-length", String.valueOf(data.length));
				connection.setRequestProperty("content-type", "application/json");
				connection.getOutputStream().write(data);
			}
			connection.connect();
			return connection.getResponseCode();
			
		}catch(Exception exp) {
			throw exp;
		}finally {
			if(connection != null) { connection.disconnect(); }
		}
	}

	public static String getEventTypeConfigKey(UserEventType eventType, String property) {
		final String CONFIG_KEY_PREFIX = "userintegration.httpscriber.";
		return CONFIG_KEY_PREFIX + eventType.toString() + "." + property;
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	private void initConfig() {
		UserEventType[] values 	= UserEventType.values();
		EventConfigItem[] items = new EventConfigItem[values.length];
		
		for(int i = 0; i < values.length; i++) {
			UserEventType eventType = values[i];
			EventConfigItem item = new EventConfigItem(
						eventType,
						getConfigValue(eventType, "enabled", "false").equals("true"),
						getConfigValue(eventType, "method", null),
						getConfigValue(eventType, "targeturl", null),
						getConfigValue(eventType, "sendcontentbody", "false").equals("true")			
					);
			items[i] = item;
		}
		
		this.eventConfigItems = items;
	}
	
	private String getConfigValue(UserEventType eventType, String property, String defaultValue) {
		return JiveGlobals.getProperty(getEventTypeConfigKey(eventType, property), defaultValue);
	}
	
	/**
	 * �¼�������
	 * @author apple
	 *
	 */
    static class EventConfigItem {
		private final static String 	USER_NAME_PLACEHOLDER = "$username";
		private final UserEventType 	eventType;
		private final boolean 			enabled;
		private final String 			method;
		private final String 			targetUrl;
		private final boolean 			sendConetntBody;
		private final boolean 			urlShouldFormat;
		
		public EventConfigItem(
				UserEventType 	eventType,
				boolean 		enabled,
				String 			method,
				String 			targetUrl,
				boolean 		sendConetntBody
				){
			this.eventType 			= eventType;
			this.enabled 			= enabled;
			this.method 			= method;
			this.targetUrl 			= targetUrl;
			this.sendConetntBody 	= sendConetntBody;
			this.urlShouldFormat 	= targetUrl != null &&  targetUrl.contains(USER_NAME_PLACEHOLDER);
		}
		
		public String getTargetUrl(UserInfo user) {
			if(!urlShouldFormat) { return targetUrl; }
			assert user != null;
			assert targetUrl != null;
			
			try {
				return targetUrl.replace(
						(CharSequence)USER_NAME_PLACEHOLDER, 
						(CharSequence)URLEncoder.encode(user.getUsername(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				return null;
			}
			
		}
	}

}
