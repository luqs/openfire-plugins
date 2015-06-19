package com.skyseas.openfireplugins.group.spi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;

import com.skyseas.openfireplugins.group.ChatUser;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.openfire.OfflineMessageStore;
import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.util.JiveConstants;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * Created by zhangzhi on 2014/9/26.
 */
class ChatUserImpl implements ChatUser {
	private static final Logger Log = LoggerFactory.getLogger(ChatUserImpl.class);
    private final String userName;
    private volatile String nickname;
    private final JID jid;

    public ChatUserImpl(String userName, String nickName, JID jid) {
        assert userName != null;
        assert nickName != null;
        assert jid != null;

        this.userName = userName;
        this.nickname = nickName;
        this.jid = jid;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    @Override
    public void send(PacketRouter router, Packet packet) {
        assert router != null;
        assert packet != null;

        packet.setTo(jid);
        
        Collection p =XMPPServer.getInstance().getPresenceManager().getPresences(getUserName());
        if(p==null||p.isEmpty()){
        	store((Message)packet);
        }else{
        	router.route(packet);
        }
    }
    
    private void store(Message message){
    	 if (message == null) {
             return;
         }
         JID recipient = message.getTo();
         String username = recipient.getNode();
         // If the username is null (such as when an anonymous user), don't store.
         if (username == null || !UserManager.getInstance().isRegisteredUser(recipient)) {
             return;
         }
         else
         if (!XMPPServer.getInstance().getServerInfo().getXMPPDomain().equals(recipient.getDomain())) {
             // Do not store messages sent to users of remote servers
             return;
         }

         long messageID = SequenceManager.nextID(JiveConstants.OFFLINE);

         // Get the message in XML format.
         String msgXML = message.getElement().asXML();

         String sql = 
        	        "INSERT INTO ofOffline (username, messageID, creationDate, messageSize, stanza) " +
        	                "VALUES (?, ?, ?, ?, ?)";
         Connection con = null;
         PreparedStatement pstmt = null;
         try {
             con = DbConnectionManager.getConnection();
             pstmt = con.prepareStatement(sql);
             pstmt.setString(1, username);
             pstmt.setLong(2, messageID);
             pstmt.setString(3, StringUtils.dateToMillis(new java.util.Date()));
             pstmt.setInt(4, msgXML.length());
             pstmt.setString(5, msgXML);
             pstmt.executeUpdate();
         }

         catch (Exception e) {
        	 e.printStackTrace();
             Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
         }
         finally {
             DbConnectionManager.closeConnection(pstmt, con);
         }

    }

    @Override
    public String getUserName() {
        return userName;
    }


    @Override
    public JID getJid(){
        return this.jid;
    }
}
