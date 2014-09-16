package com.skyseas.openfireplugins.group.iq.group;

import com.skyseas.openfireplugins.group.ChatUser;
import com.skyseas.openfireplugins.group.util.DataItemProcessDelegate;
import com.skyseas.openfireplugins.group.util.DataListPacket;
import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.iq.GroupIQHandler;
import com.skyseas.openfireplugins.group.iq.IQHandler;
import org.xmpp.forms.DataForm;
import org.xmpp.packet.IQ;

import java.util.Collection;
import java.util.HashMap;

/**
 * 圈子成员查询处理程序。
 * Created by apple on 14-9-14.
 */
class MembersQueryHandler extends GroupIQHandler {

    @Override
    protected void process(IQ packet, Group group) {
        assert packet != null;
        assert group != null;

        /* 获得圈子用户列表数据包 */
        DataListPacket<ChatUser> dataListPacket = getUserListPacket(group);

        IQ reply = IQ.createResultIQ(packet);
        reply.setChildElement(dataListPacket.getElement());
        routePacket(reply);
    }

    private DataListPacket<ChatUser> getUserListPacket(Group group) {
        Collection<ChatUser> users = (Collection<ChatUser>)group.getChatUserManager().getUsers();
        DataListPacket<ChatUser> packet = new DataListPacket<ChatUser>(
                        IQHandler.GROUP_NAMESPACE,
                        users,
                        new ChatUserProcessDelegate());
        packet.getElement().addAttribute("node", "members");
        return packet;
    }

    static class ChatUserProcessDelegate implements DataItemProcessDelegate<ChatUser> {
        private final HashMap<String, Object> dataMap;
        public ChatUserProcessDelegate() {
            dataMap = new HashMap<String, Object>(2);
        }

        @Override
        public Object getPrimaryProperty(ChatUser dataItem) {
            return dataItem.getUserName();
        }

        @Override
        public void beforeProcess(DataForm form) {
            form.addReportedField("username",   null, null);
            form.addReportedField("nickname",   null, null);
        }

        @Override
        public void process(DataForm form, ChatUser dataItem) {
            dataMap.put("nickname",     dataItem.getNickname());
            dataMap.put("username",     dataItem.getUserName());
            form.addItemFields(dataMap);
        }
    }
}
