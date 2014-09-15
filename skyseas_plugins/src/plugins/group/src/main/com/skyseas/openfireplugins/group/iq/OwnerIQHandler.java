package com.skyseas.openfireplugins.group.iq;

import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.iq.GroupIQHandler;
import com.skyseas.openfireplugins.group.iq.IQContext;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

/**
 * Created by zhangzhi on 2014/9/15.
 */
public abstract class OwnerIQHandler extends GroupIQHandler {

    @Override
    protected Packet filter(IQContext context) {
        assert context != null;
        assert context.getPacket() != null;

        IQ packet = context.getPacket();
        Group group = context.getItem(IQContext.ITEM_GROUP);

        if (group != null && !checkPermission(group, packet)) {
            /* 只有圈子所有者才有权限操作 */
            packet = IQ.createResultIQ(packet);
            packet.setError(PacketError.Condition.not_authorized);
            return packet;
        }
        return null;
    }

    protected boolean checkPermission(Group group, IQ packet) {
        return group.getOwner().equals(packet.getFrom().asBareJID());
    }

}
