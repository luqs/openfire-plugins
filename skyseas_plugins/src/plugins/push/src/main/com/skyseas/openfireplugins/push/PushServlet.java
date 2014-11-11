package com.skyseas.openfireplugins.push;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 推送XMPP消息的servlet。
 * Created by zhangzhi on 2014/11/11.
 */
public class PushServlet extends HttpServlet {
    private final static Logger LOGGER = LoggerFactory.getLogger(PushServlet.class);
    public static final String PACKET_CONTENT_PARAMETER_NAME = "packet_content";
    private final PacketSender sender;

    public PushServlet() {
        this(new DefaultPacketSender());
    }

    PushServlet(PacketSender sender) {
        this.sender = sender;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /**
         * 接收来自客户端的post请求，
         * 从请求中取得XMPP数据包，并将数据包通过PacketSender发送出去。
         */
        String rawPacket = getRawPacket(request);
        if(rawPacket.length() > 0) {
            Packet packet = createPacket(rawPacket);
            if(packet != null && sendPacket(packet)) {
                finish(response, HttpServletResponse.SC_OK, "ok");
            }else {
                finish(response, HttpServletResponse.SC_BAD_REQUEST,
                        "invalid xmpp data.");
            }
        }else {
            finish(response, HttpServletResponse.SC_BAD_REQUEST,
                    "invalid xmpp data.");
        }
    }

    /**
     * 获得原始的XMPP包内容。
     * @param request
     * @return
     */
    private String getRawPacket(HttpServletRequest request) {
        String packetContent = request.getParameter(PACKET_CONTENT_PARAMETER_NAME);
        return packetContent != null ? packetContent.trim() : "";
    }

    /**
     * 用原始的packet xml文本创建包对象实例。
     * @param rawPacket
     * @return
     */
    private Packet createPacket(String rawPacket) {
        Element element;
        try {
            /* 将内容主题分析为XML元素 */
            element = DocumentHelper.parseText(rawPacket).getRootElement();
        } catch (DocumentException e) {
            LOGGER.error("parse xml fail", e);
            return null;
        }

        if ("message".equals(element.getName())) {
            return new Message(element);
        } else if ("iq".equals(element.getName())) {
            return new IQ(element);
        } else if ("presence".equals(element.getName())) {
            return new Presence(element);
        } else {
            return null;
        }
    }

    /**
     * 完成当前请求，并设置响应状态信息。
     * @param response
     * @param status
     * @param message
     * @throws IOException
     */
    private void finish(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        if(message != null) {
            try {
                response.getWriter().write(message);
                response.getWriter().flush();
            }finally {
                response.getWriter().close();
            }
        }
    }

    /**
     * 发送数据包。
     * @param packet
     * @return
     */
    private boolean sendPacket(Packet packet) {
        try {
            sender.send(packet);
            return true;
        }catch (Exception exp) {
            LOGGER.error("send packet fail.", packet);
        }
        return false;
    }
}
