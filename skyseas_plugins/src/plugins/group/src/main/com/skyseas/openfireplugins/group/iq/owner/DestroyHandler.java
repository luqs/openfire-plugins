package com.skyseas.openfireplugins.group.iq.owner;

import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupEventDispatcher;
import com.skyseas.openfireplugins.group.util.HasReasonPacket;
import com.skyseas.openfireplugins.group.iq.IQHandler;
import com.skyseas.openfireplugins.group.iq.OwnerIQHandler;
import com.skyseas.openfireplugins.group.iq.XHandler;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

/**
 * Created by apple on 14-9-9.
 */
@XHandler(namespace = IQHandler.OWNER_NAMESPACE, elementName = "destroy")
class DestroyHandler extends OwnerIQHandler {
    @Override
    public void process(IQ packet, Group group) {
        assert packet != null;
        assert group !=null;

        if(groupManager.remove(group)){
            replyOK(packet);

            /**
             * 触发圈子销毁事件。
             */
            DestroyPacket destroyPacket = new DestroyPacket(packet.getChildElement());
            GroupEventDispatcher.fireGroupDestroyed(
                    group,
                    packet.getFrom(),
                    destroyPacket.getReason());
        }else {
            replyError(packet, PacketError.Condition.internal_server_error);
        }
    }

    private static class DestroyPacket extends HasReasonPacket {
        public DestroyPacket(Element element) {
            super(element, "destroy");
        }
    }
}
