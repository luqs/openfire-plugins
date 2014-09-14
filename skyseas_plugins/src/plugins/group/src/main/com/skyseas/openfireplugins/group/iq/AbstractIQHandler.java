package com.skyseas.openfireplugins.group.iq;

import com.skyseas.openfireplugins.group.GroupService;
import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.RoutingTable;
import org.jivesoftware.openfire.XMPPServer;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

/**
 * Created by apple on 14-9-14.
 */
public abstract class AbstractIQHandler implements IQHandler {

    protected RoutingTable routingTable;
    protected PacketRouter packetRouter;
    protected GroupService groupService;

    @Override
    public void initialize(GroupService groupService) {
        this.groupService = groupService;
        XMPPServer server = groupService.getServer();
        packetRouter = server.getPacketRouter();
    }

    protected void replyError(IQ packet, PacketError.Condition condition) {
        IQ reply = IQ.createResultIQ(packet);
        reply.setError(condition);
        packetRouter.route(reply);
    }

    protected void replyOK(IQ packet) {
        packetRouter.route(IQ.createResultIQ(packet));
    }
}
