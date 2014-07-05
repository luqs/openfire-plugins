package com.skyseas.openfireplugins.userintegration;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * openfire�û����ϲ����
 * ����ʱ�������XMPPЭ���е� <b>jabber:iq:register</b>��Ϣ�����û��ɹ�ע��XMPP�˺�֮��
 * ���û�ע����˺���Ϣͨ�� {@link:RegisterSubscriber} ���з�����
 */
public class UserIntegrationPlugin implements Plugin {

    private static final Logger Log = LoggerFactory.getLogger(UserIntegrationPlugin.class);
    public final static String REGISTER_SUBSCRIBER_CLASS_KEY ="register.subscriber.class";

    /**
     * ע����Ϣ������
     */
    private RegisterInterceptor interceptor;


    /**
     * �����ʼ���¼�
     * @param pluginManager ���������ʵ��
     * @param file
     */
    @Override
    public void initializePlugin(PluginManager pluginManager, File file) {
        try {
            // �������ļ��л�ȡ����������
            String subscriberClassName = JiveGlobals.getProperty(REGISTER_SUBSCRIBER_CLASS_KEY);
            RegisterSubscriber subscriber = createSubscriber(subscriberClassName);

            // ����ע����Ϣ������
            interceptor = createInterceptor(subscriber);
            InterceptorManager.getInstance().addInterceptor(interceptor);
            
            Log.info(String.format("�û����ɲ����ʼ����� Subscriber:%s", 
            		subscriber.getClass()));
            
        }catch (Exception exp) {
            Log.error("�û����ϲ����ʼ��ʧ��", exp);
        }
    }
    
    
    /**
     * ��������¼�
     */
    @Override
    public void destroyPlugin() {
        // �������ʱ�Ƴ���Ϣ������
        InterceptorManager.getInstance().removeInterceptor(interceptor);
    }
    
    private RegisterSubscriber createSubscriber(String subscriberClassName) 
    		throws ClassNotFoundException, 
    		IllegalAccessException, InstantiationException {
    	
    	if( subscriberClassName == null || 
			subscriberClassName.length() == 0){
    		
    		// ʹ��Ĭ�ϵ�restful�ӿڵĶ�����
    		return new HttpSubscriber();
    	}
    	
    	// ��̬����ָ���Ķ�����
    	Class<?> subscriber = Class.forName(subscriberClassName);
        return (RegisterSubscriber)subscriber.newInstance();
    }

    
    private RegisterInterceptor createInterceptor(RegisterSubscriber subscriber) {
        return new RegisterInterceptor(subscriber);
    }
    
    RegisterInterceptor getInterceptor() {
    	return interceptor;
    }
}
