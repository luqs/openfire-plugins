package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

/**
 * Created by apple on 14-9-14.
 */
public class GroupImpl extends AbstractMultiUserChat implements Group {
    private final static Logger LOG = LoggerFactory.getLogger(GroupImpl.class);
    private final JID owner;
    private volatile GroupInfo groupInfo;

    public GroupImpl(JID jid, GroupInfo groupInfo, ChatUserManager userManager) {
        super(String.valueOf(groupInfo.getId()), jid, userManager);
        this.groupInfo = groupInfo;
        this.owner = new JID(groupInfo.getOwner());
    }

    @Override
    public JID getOwner() {
        return owner;
    }

    public boolean destroy() {
        return true;
    }

    @Override
    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    @Override
    public boolean updateGroupInfo(GroupInfo groupInfo) {
        return false;
    }

    @Override
    public void applyJoin(String userName, String nickname, String reason) throws FullMemberException {

    }

    @Override
    public void send(Packet packet) {

    }

    @Override
    public void send(JID jid, Message msg) {

    }


    /**
     * 创建一个圈子。
     */
    static GroupImpl create(GroupInfo groupInfo, GroupPersistenceManager persistenceMgr, GroupService groupService) {
        assert groupInfo != null;
        assert groupService != null;
        return null;
    }

    /**
     * 加载一个圈子。
     */
    static GroupImpl load(int groupId, GroupPersistenceManager persistenceMgr, GroupService groupService) {
        assert groupId > 0;
        assert groupService != null;
        return null;
    }
}
