package com.skyseas.openfireplugins.userintegration;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.event.UserEventDispatcher;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * openfire用户整合插件。
 * 运行时插件侦听用户事件，并将事件信息通过{@link:RegisterSubscriber} 进行发布。
 */
public class UserIntegrationPlugin implements Plugin {

    private static final Logger Log = LoggerFactory.getLogger(UserIntegrationPlugin.class);
    public final static String REGISTER_SUBSCRIBER_CLASS_KEY ="register.subscriber.class";

    /**
     * 用户事件侦听器
     */
	private UserIntegrationEventLister listener;


    /**
     * 插件初始化事件
     * @param pluginManager 插件管理器实例
     * @param file
     */
    @Override
    public void initializePlugin(PluginManager pluginManager, File file) {
        try {
            // 从配置文件中获取订阅器类型
            String subscriberClassName = JiveGlobals.getProperty(REGISTER_SUBSCRIBER_CLASS_KEY);
            UserEventSubscriber subscriber = createSubscriber(subscriberClassName);

            // 创建用户事件侦听器
            listener = createListener(subscriber);
            UserEventDispatcher.addListener(listener);
            
            Log.info(String.format("用户集成插件初始化完毕 Subscriber:%s", subscriber.getClass()));
            
        }catch (Exception exp) {
            Log.error("用户整合插件初始化失败", exp);
        }
    }

	/**
     * 插件销毁事件
     */
    @Override
    public void destroyPlugin() {
        // 插件销毁时移除用户事件侦听器
    	UserEventDispatcher.removeListener(listener);
    }
    
    private UserEventSubscriber createSubscriber(String subscriberClassName) 
    		throws ClassNotFoundException,  IllegalAccessException, InstantiationException {
    	
    	// 如果配置文件有显式设置订阅器类型则构建配置文件指定类型的订阅器，否则使用默认的Http订阅器
    	if( subscriberClassName == null || subscriberClassName.length() == 0){
    		return new HttpSubscriber();
    	}
    	
    	// 动态加载指定的订阅器
    	Class<?> subscriber = Class.forName(subscriberClassName);
        return (UserEventSubscriber)subscriber.newInstance();
    }

    private UserIntegrationEventLister createListener(UserEventSubscriber subscriber) {
		return new UserIntegrationEventLister(subscriber);
	}
    
    UserIntegrationEventLister getUserEventLisenter() {
    	return listener;
    }

}
