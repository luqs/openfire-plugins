package com.skyseas.openfireplugins.userintegration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 基于HTTP的用户事件订阅器，将用户事件信息通过HTTP接口进行发布。
 * @author apple
 *
 */
public class HttpUserEventSubscriber implements UserEventSubscriber {
	private EventConfigItem[] 			eventConfigItems;
	private static final Logger 		Log 			= LoggerFactory.getLogger(HttpUserEventSubscriber.class);

	public HttpUserEventSubscriber() {
		initConfig();
	}
	
	@Override
	public void publish(UserInfo user, UserEventType eventType) {
		assert user != null;
		assert eventType != null;
		
		// 获得当前事件类型的订阅配置项
		EventConfigItem eventConfigItem = getEventConfigItem(eventType);
		try {
			eventConfigItem.processEvent(user);
		} catch(Exception exp) {
			Log.error(String.format("处理用户事件失败：UserName:%s, EventType:%s", 
					user.getUsername(), eventType));
		}
	}
	
	public static String getEventTypeConfigKey(UserEventType eventType, String property) {
		final String CONFIG_KEY_PREFIX = "userintegration.httpscriber.";
		return CONFIG_KEY_PREFIX + eventType.toString() + "." + property;
	}

	private EventConfigItem getEventConfigItem(UserEventType eventType) {
		return eventConfigItems[eventType.ordinal()];
	}

	/**
	 * 初始化配置信息
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
	 * 事件配置项
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
		
		/**
		 * 处理事件
		 * @param user
		 * @throws IOException 
		 */
		public void processEvent(UserInfo user) throws IOException {
			if(enabled) {
				String targetUrl 	= getTargetUrl(user);
				Object content		= sendConetntBody ? wrapContentBody(user) : null;
				int statusCode 		= HttpHelper.request(targetUrl, method, content);
			}
		}
		
		private Object wrapContentBody(UserInfo user) {
			HashMap<String,Object> map = new HashMap<String, Object>(1);
			map.put("User", user);
			return map;
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
