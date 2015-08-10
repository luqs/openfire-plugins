package com.skyseas.openfireplugins.chatlogs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;
/**
 * function: 聊天记录插件
 * 
 */
public class ChatLogsPlugin implements PacketInterceptor, Plugin {
	private static final Logger log = LoggerFactory.getLogger(ChatLogsPlugin.class);
	private static JedisManager jedisManager ;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	

	public ChatLogsPlugin() {
		interceptorManager = InterceptorManager.getInstance();
	}

	// Hook for intercpetorn
	private InterceptorManager interceptorManager;

	/**
	 * function: 拦截消息核心方法，Packet就是拦截消息对象
	 * 
	 */
	@Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
		this.doAction(packet, incoming, processed, session);
	}

	/**
	 * function: 执行保存/分析聊天记录动作
	 * 
	 * @param packet
	 *            数据包
	 * @param incoming
	 *            true表示发送方
	 * @param session
	 *            当前用户session
	 */
	private void doAction(Packet packet, boolean incoming, boolean processed,
			Session session) {
		Packet copyPacket = packet.createCopy();
		if (packet instanceof Message) {
			Message message = (Message) copyPacket;
			// 一对一聊天，单人模式
			if ((message.getType() == Message.Type.chat||message.getType() == Message.Type.groupchat)
					&&message.getBody()!=null) {
				//debug("单人聊天信息：" + message.toXML());
				// 程序执行中；是否为结束或返回状态（是否是当前session用户发送消息）
				if (processed || !incoming) {
					return;
				}
				//直接插入数据库
				//logsManager.add(this.get(packet, incoming, session));
				//将聊天记录发到消息队列
				Jedis jedis = null;
				try {
					jedis = jedisManager.getJedis();
					OfChatLogs chatLogs = this.get(message, incoming, session);
					jedis.lpush("chatlogs", new Gson().toJson(chatLogs));
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					jedisManager.closeJedis(jedis);
				}
			} 
		}
	}

	/**
	 * function: 创建一个聊天记录实体对象，并设置相关数据
	 * 
	 * @param packet
	 *            数据包
	 * @param incoming
	 *            如果为ture就表明是发送者
	 * @param session
	 *            当前用户session
	 * @return 聊天实体
	 */
	private OfChatLogs get(Message message, boolean incoming, Session session) {
		OfChatLogs logs = new OfChatLogs();
		JID jid = session.getAddress();
		if (incoming) { // 发送者
			logs.setFromUser(jid.getNode());
			JID recipient = message.getTo();
			logs.setToUser(recipient.getNode());
		}
		logs.setContent(message.toXML().replaceAll("\"", "\\\""));
		logs.setCreateTime(sdf.format(new Date()));
		return logs;
	}

	@Override
	public void destroyPlugin() {
		jedisManager.destroy();
		interceptorManager.removeInterceptor(this);
		log.info("插件销毁成功");
	}

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		interceptorManager.addInterceptor(this);
		
		try {
			InputStream in =new BufferedInputStream(new FileInputStream(new File(pluginDirectory,"redis.properties")));
			Properties props = new Properties();
			props.load(in);
			jedisManager = JedisManager.getInstance(props);
			log.info("插件启动成功");
		} catch (IOException e) {
			log.error("error: {}", e);
			e.printStackTrace();
		}
	}
}