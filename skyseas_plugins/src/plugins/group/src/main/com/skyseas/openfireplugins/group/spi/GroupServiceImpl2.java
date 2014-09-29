package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.GroupManager;
import com.skyseas.openfireplugins.group.GroupService;
import org.jivesoftware.openfire.XMPPServer;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

/**
 * Created by apple on 14-9-14.
 */
public class GroupServiceImpl2 implements GroupService , Component {
    @Override
    public String getServiceName() {
        return "group";
    }

    @Override
    public String getServiceDomain() {
        return null;
    }

    @Override
    public XMPPServer getServer() {
        return null;
    }

    @Override
    public GroupManager getGroupManager() {
        return null;
    }

    @Override
    public JID getGroupJid(String groupId) {
        return null;
    }

    @Override
    public String getName() {
        return "myserivce";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void processPacket(Packet packet) {
        System.out.println(packet);
    }

    @Override
    public void initialize(JID jid, ComponentManager componentManager) throws ComponentException {
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
