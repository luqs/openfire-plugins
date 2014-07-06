package com.skyseas.openfireplugins.userintegration;


/**
 * 用户事件订阅器，用于接收openfire的用户事件。
 */
public interface UserEventSubscriber {
	
	/**
	 * 用户事件类型。
	 * @author apple
	 *
	 */
	public enum UserEventType {
		
		/**
		 * 当用户创建之后。
		 */
		CREATED,
		
		/**
		 * 当用户即将删除之前。
		 */
		DELETING,
		
		/**
		 * 当用户消息修改之后，如：用户密码被修改。
		 */
		MODIFIED
	}

    /**
     * 发布注册用户信息到订阅器上。
     * @param user 用户信息。
     * @param eventType 事件类型。
     */
    void publish(UserInfo user, UserEventType eventType);
}
