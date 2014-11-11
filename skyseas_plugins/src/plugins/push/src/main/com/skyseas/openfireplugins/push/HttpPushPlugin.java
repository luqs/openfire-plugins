package com.skyseas.openfireplugins.push;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.container.PluginServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.File;

/**
 * openfire消息的HTTP推送插件。
 * Created by zhangzhi on 2014/11/11.
 */
public class HttpPushPlugin implements Plugin {
    public final static String PLUGIN_NAME = "push";
    final static String PATH = "/packet";
    private static Logger LOGGER = LoggerFactory.getLogger(HttpPushPlugin.class);
    private PushServlet servlet;
    private String servletUrl;

    @Override
    public void initializePlugin(PluginManager pluginManager, File file) {
        PushServlet ser = createServlet();
        try {
            /**
             * 插件初始化时，注册servlet实例到当前插件路径下。
             */
            this.servletUrl = PluginServlet.registerServlet(pluginManager, this, ser, PATH);
            this.servlet    = ser;
        } catch (ServletException e) {
            LOGGER.error("register servlet fail", e);
        }
    }

    @Override
    public void destroyPlugin() {
        if (servlet != null) {
            try {
                /**
                 * 插件销毁时，卸载注册的servlet。
                 */
                PluginServlet.unregisterServlet(this, PATH);
                servlet = null;
            } catch (ServletException e) {
                LOGGER.error("unregister servlet fail", e);
            }
        }
    }

    /**
     * 获得推送servlet的url地址。
     * @return
     */
    public String getServletUrl() {
        return servletUrl;
    }

    PushServlet createServlet() {
        return new PushServlet();
    }


}
