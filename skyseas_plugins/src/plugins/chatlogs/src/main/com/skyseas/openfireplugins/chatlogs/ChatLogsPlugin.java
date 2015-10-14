package com.skyseas.openfireplugins.chatlogs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.JiveConstants;
import org.jivesoftware.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketExtension;
import org.xmpp.packet.Presence;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;
/**
 * function: 聊天记录插件
 * 
 */
public class ChatLogsPlugin implements PacketInterceptor, Plugin {
	private static final Logger log = LoggerFactory.getLogger(ChatLogsPlugin.class);
	private static JedisManager jedisManager ;
	private static ConcurrentHashMap<String, OfChatLogs> m = new ConcurrentHashMap<String, OfChatLogs>();
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
		PacketExtension extension = packet.getExtension("received","urn:xmpp:receipts");
		if(extension!=null){
			String id = extension.getElement().attributeValue("id");
			m.remove(id);
		}
		
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
					OfChatLogs chatLogs = this.get(message, incoming, session);
					if(message.getType() == Message.Type.chat&&packet.getExtension("request","urn:xmpp:receipts")!=null){
						m.put(message.getID(), chatLogs);
						System.out.println(message.getID()+"=============");
					}
					jedis = jedisManager.getJedis();
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
		logs.setIsGroup(message.getType()== Message.Type.groupchat?"1":"0");
		logs.setContent(message.toXML().replaceAll("\"", "\\\""));
		logs.setCreateTime(sdf.format(new Date()));
		return logs;
	}

	@Override
	public void destroyPlugin() {
		interceptorManager.removeInterceptor(this);
		jedisManager.destroy();
		log.info("chatlogs plugin stop success");
	}

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		
		try {
			Runnable r = new Runnable() {
				  
				@Override
				public void run() {
					long currentTime = new Date().getTime();
					System.out.println("=======");
					Iterator<String> iterator = m.keySet().iterator();
					System.out.println(m+"============");
					while(iterator.hasNext()){
						String key = iterator.next();
						OfChatLogs s = m.get(key);
						try {
							long d = sdf.parse(s.getCreateTime()).getTime();
							if(isOnline(s.getToUser())&&currentTime-d>=10000){
								store(s);
								m.remove(key);
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			};
			
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(r, 0, 10, TimeUnit.SECONDS);
			System.out.println("---------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		try {
			InputStream in =new BufferedInputStream(new FileInputStream(new File(pluginDirectory,"redis.properties")));
			Properties props = new Properties();
			props.load(in);
			jedisManager = JedisManager.getInstance();
			interceptorManager.addInterceptor(this);
			log.info("chatlogs plugin start success");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("error: {}", e);
		}
	}
	
	private boolean isOnline(String jid){
		 Collection<Presence> p =XMPPServer.getInstance().getPresenceManager().getPresences(jid);
	        if(p==null||p.isEmpty()){
	        	return false;
	        }else{
	        	return true;
	        }
	}
    
    private void store(OfChatLogs ofChatLogs){
    	 if (ofChatLogs == null) {
             return;
         }
         long messageID = SequenceManager.nextID(JiveConstants.OFFLINE);

         // Get the message in XML format.
         String msgXML = ofChatLogs.getContent();

         String sql = 
        	        "INSERT INTO ofOffline (username, messageID, creationDate, messageSize, stanza) " +
        	                "VALUES (?, ?, ?, ?, ?)";
         Connection con = null;
         PreparedStatement pstmt = null;
         try {
             con = DbConnectionManager.getConnection();
             pstmt = con.prepareStatement(sql);
             pstmt.setString(1, ofChatLogs.getToUser());
             pstmt.setLong(2, messageID);
             pstmt.setString(3, StringUtils.dateToMillis(sdf.parse(ofChatLogs.getCreateTime())));
             pstmt.setInt(4, msgXML.length());
             pstmt.setString(5, msgXML);
             pstmt.executeUpdate();
         }

         catch (Exception e) {
        	 e.printStackTrace();
         }
         finally {
             DbConnectionManager.closeConnection(pstmt, con);
         }

    }
}