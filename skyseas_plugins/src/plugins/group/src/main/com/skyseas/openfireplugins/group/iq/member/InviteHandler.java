package com.skyseas.openfireplugins.group.iq.member;

import com.skyseas.openfireplugins.group.ChatUser;
import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.iq.*;
import com.skyseas.openfireplugins.group.util.ModelPacket;
import com.skyseas.openfireplugins.group.util.StringUtils;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

/**
 * 邀请处理程序。
 * Created by zhangzhi on 2014/10/9.
 */
@XHandler(namespace = IQHandler.MEMBER_NAMESPACE, elementName = "invite")
public class InviteHandler extends MemberIQHandler {

    @Override
    protected void process(IQ packet, Group group) {
        assert packet != null;
        assert group != null;

        InvitePacket invPacket = new InvitePacket(packet.getChildElement());
        if (StringUtils.isNullOrEmpty(invPacket.getUserName())) {
            replyError(packet, PacketError.Condition.bad_request);
            return;
        }

        ChatUser user = null;
        try {
            user = group.getChatUserManager().addUser(
                    invPacket.getUserName(),
                    invPacket.getNickname());
        } catch (Exception exp) {;}

        if(user != null) {
            replyOK(packet);
        }else {
            replyError(packet, PacketError.Condition.internal_server_error);
        }
    }


    private static class InvitePacket extends ModelPacket {
        public InvitePacket(Element element) {
            super(element, "invite");
        }

        protected Element getMemberElement() {
            return modeElement.element("member");
        }

        public String getUserName() {
            Element ele = getMemberElement();
            if (ele == null) {
                return null;
            }
            return ele.attributeValue("username");
        }

        public String getNickname() {
            Element ele = getMemberElement();
            if (ele == null) {
                return null;
            }
            return ele.attributeValue("nickname") == null
                    ? getUserName()
                    : ele.attributeValue("nickname");
        }
    }
}

