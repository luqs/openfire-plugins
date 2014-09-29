package com.skyseas.openfireplugins.group;

import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.xmpp.packet.JID;

/**
 * Created by apple on 14-9-14.
 */
public interface GroupService {
    /**
     * 获得圈子服务名称。如：group。
     * @return
     */
    String getServiceName();

    /**
     * 获得服务的域名。 如：group.skysea.com
     * @return
     */
    String getServiceDomain();

    /**
     * 获得XMPP服务器实例。
     * @return
     */
    XMPPServer getServer();

    /**
     * 获得圈子管理器。
     * @return
     */
    GroupManager getGroupManager();

    /**
     * 获得圈子jid。
     * @param groupId
     * @return
     */
    JID getGroupJid(String groupId);

}
