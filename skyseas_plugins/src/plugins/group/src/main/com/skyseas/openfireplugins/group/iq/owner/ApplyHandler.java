package com.skyseas.openfireplugins.group.iq.owner;

import com.skyseas.openfireplugins.group.FullMemberException;
import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.iq.IQHandler;
import com.skyseas.openfireplugins.group.iq.OwnerIQHandler;
import com.skyseas.openfireplugins.group.iq.XHandler;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;

/**
 * 申请处理程序。
 * Created by apple on 14-9-15.
 */
@XHandler(namespace = IQHandler.OWNER_NAMESPACE, elementName = "apply")
public class ApplyHandler extends OwnerIQHandler {

    @Override
    public void process(IQ packet, Group group) {
        assert group != null;
        assert packet != null;

        ApplyProcessPacket applyPacket = new ApplyProcessPacket(packet.getChildElement());
        ApplyProcessObject processor = new ApplyProcessObject(group, packet.getFrom(), applyPacket);

        if (applyPacket.isDecline()) {
            processor.decline();
        } else if (!processor.agree()) {

            /* 同意申请但添加用户到圈子失败 */
            replyError(packet, PacketError.Condition.service_unavailable);
            return;
        }

        replyOK(packet);
    }

    private static class ApplyProcessObject {
        private final Group group;
        private final JID operator;
        private final ApplyProcessPacket appPacket;

        public ApplyProcessObject(Group group, JID operator, ApplyProcessPacket appPacket) {
            this.group = group;
            this.operator = operator;
            this.appPacket = appPacket;
        }

        public boolean agree() {
            try {
                group.getChatUserManager().addUser(
                        appPacket.getFrom().getNode(),
                        appPacket.getFrom().getNode());
            } catch (FullMemberException e) {
                return false;
            }

            notifyProposer(true);
            // TODO: 触发用户加入事件？
            return true;
        }

        public void decline() {
            notifyProposer(false);
        }

        private void notifyProposer(boolean result) {
            /* 被拒绝时向申请者发送消息 */
            Message msg = ApplyProcessPacket.newInstanceForApplyResult(
                    result,
                    operator.toBareJID(),
                    appPacket.getReason());
            group.send(appPacket.getFrom(), msg);
        }
    }
}
