package com.skyseas.openfireplugins.userintegration;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * openfire用户整合插件。
 * 运行时插件拦截XMPP协议中的 <b>jabber:iq:register</b>消息，当用户成功注册XMPP账号之后
 * 将用户注册的账号信息通过 {@link:RegisterSubscriber} 进行发布。
 */
public class UserIntegrationPlugin implements Plugin {

    private static final Logger Log = LoggerFactory.getLogger(UserIntegrationPlugin.class);
    public final static String REGISTER_SUBSCRIBER_CLASS_KEY ="register.subscriber.class";

    /**
     * 注册消息拦截器
     */
    private RegisterInterceptor interceptor;


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
            RegisterSubscriber subscriber = createSubscriber(subscriberClassName);

            // 创建注册消息拦截器
            interceptor = createInterceptor(subscriber);
            InterceptorManager.getInstance().addInterceptor(interceptor);
            
            Log.info(String.format("用户集成插件初始化完毕 Subscriber:%s", 
            		subscriber.getClass()));
            
        }catch (Exception exp) {
            Log.error("用户整合插件初始化失败", exp);
        }
    }
    
    
    /**
     * 插件销毁事件
     */
    @Override
    public void destroyPlugin() {
        // 插件销毁时移除消息拦截器
        InterceptorManager.getInstance().removeInterceptor(interceptor);
    }
    
    private RegisterSubscriber createSubscriber(String subscriberClassName) 
    		throws ClassNotFoundException, 
    		IllegalAccessException, InstantiationException {
    	
    	if( subscriberClassName == null || 
			subscriberClassName.length() == 0){
    		
    		// 使用默认的restful接口的订阅器
    		return new HttpSubscriber();
    	}
    	
    	// 动态加载指定的订阅器
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
