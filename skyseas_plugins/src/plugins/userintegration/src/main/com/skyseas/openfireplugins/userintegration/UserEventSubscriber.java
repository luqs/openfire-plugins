package com.skyseas.openfireplugins.userintegration;


/**
 * �û��¼������������ڽ���openfire���û��¼���
 */
public interface UserEventSubscriber {
	
	/**
	 * �û��¼����͡�
	 * @author apple
	 *
	 */
	public enum UserEventType {
		
		/**
		 * ���û�����֮��
		 */
		CREATED,
		
		/**
		 * ���û�����ɾ��֮ǰ��
		 */
		DELETING,
		
		/**
		 * ���û���Ϣ�޸�֮���磺�û����뱻�޸ġ�
		 */
		MODIFIED
	}

    /**
     * ����ע���û���Ϣ���������ϡ�
     * @param user �û���Ϣ��
     * @param eventType �¼����͡�
     */
    void publish(UserInfo user, UserEventType eventType);
}
