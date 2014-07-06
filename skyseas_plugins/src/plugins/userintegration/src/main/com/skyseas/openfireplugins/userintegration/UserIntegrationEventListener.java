package com.skyseas.openfireplugins.userintegration;

import java.util.Map;

import org.jivesoftware.openfire.auth.AuthFactory;
import org.jivesoftware.openfire.event.UserEventListener;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skyseas.openfireplugins.userintegration.UserEventSubscriber.UserEventType;

/**
 * openfire用户整合事件侦听器，{@link:UserIntegrationEventListener}侦听openfire用户事件，并将
 * 事件消息发布到指定订阅器。
 * @author apple
 *
 */
public class UserIntegrationEventListener implements UserEventListener {
	private static final Logger Log = LoggerFactory.getLogger(UserIntegrationEventListener.class);
	private UserEventSubscriber subscriber;

	public UserIntegrationEventListener(UserEventSubscriber subscriber) {
		if(subscriber == null) { throw new NullPointerException("subscriber is null."); }
		this.subscriber = subscriber;
	}

	// 用户消息创建之后
	@Override
	public void userCreated(User user, Map<String, Object> params) {
		publish(user, UserEventSubscriber.UserEventType.CREATED);
	}
	
	// 用户信息删除之前
	@Override
	public void userDeleting(User user, Map<String, Object> params) {
		publish(user, UserEventSubscriber.UserEventType.DELETING);
	}

	// 用户消息修改之后
	@Override
	public void userModified(User user, Map<String, Object> params) {
		publish(user, UserEventSubscriber.UserEventType.MODIFIED);
	}

	/**
	 * 获得当前订阅器实例。
	 * @return 订阅器实例。
	 */
	public UserEventSubscriber getSubscriber() {
		return subscriber;
	}
	
	/**
	 * 将用户事件发布到订阅器对象实例。
	 * @param user
	 * @param eventType
	 */
	private void publish(User user, UserEventType eventType) {
		UserInfo userInfo = getUserInfo(user);
		
		try {
			subscriber.publish(userInfo, eventType);
		}catch(Exception exp) {
			Log.error(String.format("将用户事件发布到订阅器失败，UserName:%s, EventType:%s", 
					user.getName(), eventType.toString()));
		}
	}
	
	/**
	 * 从openfire用户信息，获得订阅器需要的UserInfo实例。
	 * @param user
	 * @return
	 */
	private UserInfo getUserInfo(User user) {
		String password = null;
		
		try {
			
			// 从Auth模块获得用户密码信息
			password = AuthFactory.getPassword(user.getUsername());
		} catch (UnsupportedOperationException e) {
			Log.error(String.format("获得username:%s密码失败,因为当前AuthFactory不支持。", 
							user.getUsername()), e);
		} catch (UserNotFoundException e) {
			Log.error(String.format("获得username:%s密码失败,因为当前UserName不存在。", 
					user.getUsername()), e);
		}
	
		return new UserInfo(
				user.getUsername(),  
				password, 
				user.getEmail(), 
				user.getName());
	}

}
