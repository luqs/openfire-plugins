package com.skyseas.openfireplugins.group.iq.owner;

import com.skyseas.openfireplugins.group.ChatUser;
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
 * 踢出成员处理程序。
 * Created by zhangzhi on 2014/9/9.
 */
@XHandler(namespace = IQHandler.OWNER_NAMESPACE, elementName = "kick")
public class KickHandler extends OwnerIQHandler {
    @Override
    public void process(IQ packet, Group group) {
        assert packet != null;
        assert group != null;

        KickPacket kickPacket = new KickPacket(packet.getChildElement());
        String kickUserName = kickPacket.getUserName();

        /* 所有者不能将自己踢出 */
        if (group.getOwner().getNode().equals(kickUserName)) {
            replyError(packet, PacketError.Condition.not_allowed);
            return;
        }

        ChatUser user = group.getChatUserManager().removeUser(kickUserName);
        if (user != null) {
            replyOK(packet);

            // TODO: 移动到内部？
            /* 触发用户被踢出事件 */
            GroupEventDispatcher.fireUserKick(
                    group,
                    user,
                    packet.getFrom(),
                    kickPacket.getReason());
        } else {
            replyError(packet, PacketError.Condition.internal_server_error);
        }
    }

    private static class KickPacket extends HasReasonPacket {
        public KickPacket(Element element) {
            super(element, "kick");
        }

        public String getUserName() {
            return modeElement.attributeValue("username", null);
        }
    }
}
