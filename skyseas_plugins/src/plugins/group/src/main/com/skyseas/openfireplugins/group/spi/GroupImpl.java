package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.*;
import org.jivesoftware.openfire.PacketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.*;

/**
 * Created by apple on 14-9-14.
 */
final class GroupImpl extends AbstractMultiUserChat implements Group, NumberOfUsersListener {
    private final static Logger LOG = LoggerFactory.getLogger(GroupImpl.class);
    private final JID owner;
    private final GroupPersistenceManager persistenceMgr;
    private final GroupMemberPersistenceManager memberPersistenceMgr;
    private final String xmppDomain;
    private final GroupIQDispatcher dispatcher;
    private volatile GroupInfo groupInfo;
    private volatile ChatUserManager chatUserManager;
    private ApplyStrategy applyStrategy;
    private PacketRouter packetRouter;

    GroupImpl(JID jid,
              GroupInfo groupInfo,
              String xmppDomain,
              GroupIQDispatcher dispatcher,
              PacketRouter packetRouter,
              GroupPersistenceManager persistenceMgr,
              GroupMemberPersistenceManager memberPersistenceMgr) {
        super(String.valueOf(groupInfo.getId()), jid);
        assert jid != null;
        assert groupInfo != null;
        assert xmppDomain != null;
        assert dispatcher != null;
        assert persistenceMgr != null;
        assert memberPersistenceMgr != null;

        this.groupInfo = groupInfo;
        this.xmppDomain = xmppDomain;
        this.owner = new JID(groupInfo.getOwner(), xmppDomain, null, true);
        this.dispatcher = dispatcher;
        this.packetRouter = packetRouter;
        this.persistenceMgr = persistenceMgr;
        this.memberPersistenceMgr = memberPersistenceMgr;
        setApplyStrategy(groupInfo.getOpennessType());
    }

    @Override
    public JID getOwner() {
        return owner;
    }

    @Override
    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    @Override
    public boolean updateGroupInfo(GroupInfo groupInfo) {
        assert groupInfo != null;

        if (!persistenceUpdate(groupInfo)) {
            return false;
        }

        /* 合并更新内存中的Group副本，注意：这和持久化GroupInfo不是原子的。 */
        combineUpdate(groupInfo);

        /* 触发圈子修改 */
        GroupEventDispatcher.fireGroupInfoChanged(this);
        return true;
    }

    @Override
    public void applyJoin(JID proposer, String nickname, String reason) throws FullMemberException {
        assert proposer != null;
        assert nickname != null && nickname.length() > 0;
        applyStrategy.applyToJoin(this, proposer, nickname, reason);
    }

    @Override
    protected void handle(IQ packet) {
        dispatcher.dispatch(packet, this);
    }

    @Override
    protected void routePacket(Packet packet, ChatUser user) {
        user.send(packetRouter, packet);
    }

    @Override
    protected void routePacket(Packet packet) {
        packetRouter.route(packet);
    }

    @Override
    public void send(JID recipients, Message msg) {
        msg.setFrom(this.jid);
        msg.setTo(recipients);
        routePacket(msg);
    }

    @Override
    public ChatUserManager getChatUserManager() {
        if (chatUserManager != null) {
            return chatUserManager;
        }

        synchronized (this) {
            if (chatUserManager == null) {
                chatUserManager = new ChatUserManagerImpl(this, xmppDomain, this, memberPersistenceMgr);
            }
        }
        return chatUserManager;
    }

    public boolean destroy() {
        return true;
    }

    private boolean persistenceUpdate(GroupInfo groupInfo) {
        try {
            return persistenceMgr.updateGroup(groupInfo);
        } catch (PersistenceException e) {
            handleException(e, "更新圈子到数据库失败");
        }
        return false;
    }

    private void combineUpdate(GroupInfo updater) {
        synchronized (this) {
            if (updater.getOpennessType() != null) {
                setApplyStrategy(updater.getOpennessType());
            }
            groupInfo = GroupInfo.combine(updater, groupInfo);
        }
    }

    private void setApplyStrategy(GroupInfo.OpennessType opennessType) {
        applyStrategy = ApplyStrategy.getStrategyFor(opennessType);
    }

    private void handleException(Throwable exp, String format, String... args) {
        LOG.error(String.format(format, args) + ", GroupId:" + id, exp);
    }


    @Override
    public void numberOfUsersChanged(int newNumberOfUsers) {

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
