package com.skyseas.openfireplugins.userintegration;

/**
 * �û��¼�����������openfire�û��ɹ�ע��֮��Ὣע����˻���Ϣ�������������ϡ�
 */
public interface UserEventSubscriber {

    /**
     * ����ע���û���Ϣ���������ϡ�
     * @param user ע��ɹ����˻���Ϣ��
     */
    void publish(UserInfo user);
}
