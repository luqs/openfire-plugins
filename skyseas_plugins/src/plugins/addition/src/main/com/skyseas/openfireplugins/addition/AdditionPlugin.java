package com.skyseas.openfireplugins.addition;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.container.PluginServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.File;

/**
 * Created by zhangzhi on 2014/11/21.
 */
public class AdditionPlugin implements Plugin {
    private static Logger LOGGER = LoggerFactory.getLogger(AdditionPlugin.class);
    public final static String PLUGIN_NAME          = "addition";
    final static String RESOURCE_SERVLET_REL_PATH   = "/resource";
    final static String RESOURCE_SERVLET_PATH       = "/" + PLUGIN_NAME + RESOURCE_SERVLET_REL_PATH;
    private ResourceServlet servlet;
    private String servletUrl;

    @Override
    public void initializePlugin(PluginManager pluginManager, File file) {
        ResourceServlet ser = createServlet();
        try {
            /* 注册push servlet实例到当前插件路径下 */
            this.servletUrl             = PluginServlet.registerServlet(pluginManager, this, ser, RESOURCE_SERVLET_REL_PATH);
            this.servlet                = ser;
            /* 将push servlet排除在认证检测之外，这样http服务器才能直接访问 */
            AuthCheckFilter.addExclude(RESOURCE_SERVLET_PATH);
        } catch (ServletException e) {
            LOGGER.error("register servlet fail", e);
        }
    }

    @Override
    public void destroyPlugin() {
        if (servlet != null) {
            try {
                /* 插件销毁时，卸载注册的servlet */
                PluginServlet.unregisterServlet(this, RESOURCE_SERVLET_PATH);
                servlet = null;
            } catch (ServletException e) {
                LOGGER.error("unregister servlet fail", e);
            } finally {
                AuthCheckFilter.removeExclude(RESOURCE_SERVLET_PATH);
            }
        }
    }


    ResourceServlet createServlet() {
        return new ResourceServlet(null);
    }

    public String getServletUrl() {
        return servletUrl;
    }
}
