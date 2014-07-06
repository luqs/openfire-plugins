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
 * openfire�û������¼���������{@link:UserIntegrationEventListener}����openfire�û��¼�������
 * �¼���Ϣ������ָ����������
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

	// �û���Ϣ����֮��
	@Override
	public void userCreated(User user, Map<String, Object> params) {
		publish(user, UserEventSubscriber.UserEventType.CREATED);
	}
	
	// �û���Ϣɾ��֮ǰ
	@Override
	public void userDeleting(User user, Map<String, Object> params) {
		publish(user, UserEventSubscriber.UserEventType.DELETING);
	}

	// �û���Ϣ�޸�֮��
	@Override
	public void userModified(User user, Map<String, Object> params) {
		publish(user, UserEventSubscriber.UserEventType.MODIFIED);
	}

	/**
	 * ��õ�ǰ������ʵ����
	 * @return ������ʵ����
	 */
	public UserEventSubscriber getSubscriber() {
		return subscriber;
	}
	
	/**
	 * ���û��¼�����������������ʵ����
	 * @param user
	 * @param eventType
	 */
	private void publish(User user, UserEventType eventType) {
		UserInfo userInfo = getUserInfo(user);
		
		try {
			subscriber.publish(userInfo, eventType);
		}catch(Exception exp) {
			Log.error(String.format("���û��¼�������������ʧ�ܣ�UserName:%s, EventType:%s", 
					user.getName(), eventType.toString()));
		}
	}
	
	/**
	 * ��openfire�û���Ϣ����ö�������Ҫ��UserInfoʵ����
	 * @param user
	 * @return
	 */
	private UserInfo getUserInfo(User user) {
		String password = null;
		
		try {
			
			// ��Authģ�����û�������Ϣ
			password = AuthFactory.getPassword(user.getUsername());
		} catch (UnsupportedOperationException e) {
			Log.error(String.format("���username:%s����ʧ��,��Ϊ��ǰAuthFactory��֧�֡�", 
							user.getUsername()), e);
		} catch (UserNotFoundException e) {
			Log.error(String.format("���username:%s����ʧ��,��Ϊ��ǰUserName�����ڡ�", 
					user.getUsername()), e);
		}
	
		return new UserInfo(
				user.getUsername(),  
				password, 
				user.getEmail(), 
				user.getName());
	}

}
