package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.*;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

/**
 * Created by apple on 14-9-14.
 */
public class GroupImpl extends AbstractMultiUserChat implements Group {
    protected GroupImpl(JID jid, GroupInfo groupInfo, ChatUserManager userManager) {
        super(String.valueOf(groupInfo.getId()), jid, userManager);
    }

    @Override
    public JID getOwner() {
        return null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public GroupInfo getGroupInfo() {
        return null;
    }

    @Override
    public boolean updateGroupInfo(GroupInfo groupInfo) {
        return false;
    }


    @Override
    public void send(Packet packet) {

    }

    @Override
    public void send(JID jid, Message msg) {

    }
}
