package com.skyseas.openfireplugins.addition;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Override;
import java.util.StringTokenizer;

/**
 * 推送XMPP消息的servlet。
 * Created by zhangzhi on 2014/11/11.
 */
public class ResourceServlet extends HttpServlet {
    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceServlet.class);
    private final ResourceStorage resourceStorage;

    ResourceServlet(ResourceStorage resourceStorage) {
       this.resourceStorage = resourceStorage;
    }

    /**
     * 初始化上传组件
     * @param config
     */
    @Override
    public void init(ServletConfig config) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
    }


    /**
     * POST 认为上传文件
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    /**
     * GET 认为下载文件
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    /**
     * 完成当前请求，并设置响应状态信息。
     *
     * @param response
     * @param status
     * @param message
     * @throws java.io.IOException
     */
    protected void finish(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        if (message != null) {
            try {
                response.getWriter().write(message);
                response.getWriter().flush();
            } finally {
                response.getWriter().close();
            }
        }
    }

}
