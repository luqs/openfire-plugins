package com.skyseas.openfireplugins.userintegration;

/**
 * �û�ע�ᶩ��������openfire�û��ɹ�ע��֮��Ὣע����˻���Ϣ�������������ϡ�
 */
public interface RegisterSubscriber {

    /**
     * ����ע���û���Ϣ���������ϡ�
     * @param user ע��ɹ����˻���Ϣ��
     */
    void publish(RegisterUser user);
}
