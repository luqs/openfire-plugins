package com.skyseas.openfireplugins.userintegration;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.event.UserEventDispatcher;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * openfire�û����ϲ����
 * ����ʱ��������û��¼��������¼���Ϣͨ��{@link:UserIntegrationEventListener} ���з�����
 */
public class UserIntegrationPlugin implements Plugin {

    private static final Logger Log = LoggerFactory.getLogger(UserIntegrationPlugin.class);
    public final static String REGISTER_SUBSCRIBER_CLASS_KEY ="register.subscriber.class";

    /**
     * �û��¼�������
     */
	private UserIntegrationEventListener listener;


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
            UserEventSubscriber subscriber = createSubscriber(subscriberClassName);

            // �����û��¼�������
            UserIntegrationEventListener eventListener = createListener(subscriber);
            UserEventDispatcher.addListener(eventListener);
            
            this.listener = eventListener;
            Log.info(String.format("�û����ɲ����ʼ����� Subscriber:%s", subscriber.getClass()));
            
        }catch (Exception exp) {
            Log.error("�û����ϲ����ʼ��ʧ��", exp);
        }
    }

	/**
     * ��������¼�
     */
    @Override
    public void destroyPlugin() {
    	if(listener != null) {
	        // �������ʱ�Ƴ��û��¼�������
	    	UserEventDispatcher.removeListener(listener);
    	}
    }
    
    UserIntegrationEventListener getUserEventLisenter() { return listener; }
    
    private UserEventSubscriber createSubscriber(String subscriberClassName) 
    		throws ClassNotFoundException,  IllegalAccessException, InstantiationException {
    	
    	// ��������ļ�����ʽ���ö����������򹹽������ļ�ָ�����͵Ķ�����������ʹ��Ĭ�ϵ�Http������
    	if( subscriberClassName == null || subscriberClassName.length() == 0){
    		return new HttpUserEventSubscriber();
    	}
    	
    	// ��̬����ָ���Ķ�����
    	Class<?> subscriber = Class.forName(subscriberClassName);
        return (UserEventSubscriber)subscriber.newInstance();
    }

    private UserIntegrationEventListener createListener(UserEventSubscriber subscriber) {
		return new UserIntegrationEventListener(subscriber);
	}
}
