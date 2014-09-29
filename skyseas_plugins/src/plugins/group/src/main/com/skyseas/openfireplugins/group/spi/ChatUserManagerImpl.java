package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天用户管理器实现。
 * Created by zhangzhi on 2014/9/26.
 */
final class ChatUserManagerImpl implements ChatUserManager {
    private final static int MAX_USERS = 100;
    private final static Logger LOG = LoggerFactory.getLogger(ChatUserManagerImpl.class);
    private final ConcurrentHashMap<String, ChatUserImpl> users = new ConcurrentHashMap<String, ChatUserImpl>(32);
    private final GroupMemberPersistenceManager memberPersistenceMgr;
    private final int groupId;
    private final Group group;
    private final NumberOfUsersListener numberOfUsersListener;
    private final String xmppDomain;

    public ChatUserManagerImpl(Group group,
                               String xmppDomain,
                               NumberOfUsersListener numberOfUsersListener,
                               GroupMemberPersistenceManager memberPersistenceMgr) {
        assert group != null;
        assert xmppDomain != null;
        assert memberPersistenceMgr != null;

        this.groupId = Integer.parseInt(group.getId());
        this.group = group;
        this.xmppDomain = xmppDomain;
        this.numberOfUsersListener = numberOfUsersListener;
        this.memberPersistenceMgr = memberPersistenceMgr;
        initialize();
    }

    /**
     * 初始化时加载圈子成员列表到内存。
     */
    private void initialize() {
        List<GroupMemberInfo> members = loadMembers();
        assert members != null;

        for (GroupMemberInfo memberInfo : members) {
            addUserInternal(memberInfo.getUserName(), memberInfo.getNickName());
        }
    }

    /**
     * 获得所有聊天用户列表。
     *
     * @return
     */
    @Override
    public Collection<? extends ChatUser> getUsers() {
        return users.values();
    }


    /**
     * 获得用户数量。
     *
     * @return
     */
    @Override
    public int getNumberOfUsers() {
        return users.size();
    }

    /**
     * 获得指定聊天用户。
     *
     * @param userName
     * @return
     */
    @Override
    public ChatUserImpl getUser(String userName) {
        assert userName != null;
        return users.get(userName);
    }

    /**
     * 是否包含某用户。
     *
     * @param userName
     * @return
     */
    @Override
    public boolean hasUser(String userName) {
        assert userName != null;
        return users.containsKey(userName);
    }


    /**
     * 添加聊天用户。
     *
     * @param userName
     * @param nickname
     * @return
     * @throws FullMemberException
     */
    @Override
    public ChatUser addUser(String userName, String nickname) throws FullMemberException {
        assert userName != null && userName.length() > 0;
        assert nickname != null && nickname.length() > 0;

        ChatUser user = getUser(userName);
        if (user != null) {
            return user;
        }

        synchronized (this) {
            checkNumOfUsers();
            if (saveUser(userName, nickname)) {
                user = addUserInternal(userName, nickname);
                fireNumberOfUserChanged();
            }
        }

        if(user != null) {
            /* 触发用户加入圈子事件 */
            GroupEventDispatcher.fireUserJoined(group, user);
        }

        return user;
    }

    /**
     * 删除聊天用户。
     *
     * @param userName
     * @return
     */
    @Override
    public ChatUser removeUser(RemoveType type, String userName, JID from, String reason) {
        assert userName != null;

        ChatUser user = null;
        synchronized (this) {
            if (persistenceRemoveUser(userName)) {
                user = users.remove(userName);
                fireNumberOfUserChanged();
            }
        }

        if (user != null) {
            /*触发用户退出、踢出事件 */
            if (type == RemoveType.EXIT) {
                GroupEventDispatcher.fireUserExited(group, user, reason);
            } else {
                GroupEventDispatcher.fireUserKick(group, user, from, reason);
            }
        }
        return user;
    }

    /**
     * 修改用户圈子昵称。
     *
     * @param userName
     * @param nickname
     */
    @Override
    public void changeNickname(String userName, String nickname) {
        assert userName != null && userName.length() > 0;
        assert nickname != null && nickname.length() > 0;

        ChatUserImpl user = getUser(userName);
        if (user == null) {
            throw new IllegalArgumentException("userName");
        }

        String oldNickname = user.getNickname();
        synchronized (user) {
            if (persistenceChangeNickName(userName, nickname)) {
                user.setNickname(nickname);
            }
        }

        /* 触发昵称修改事件 */
        GroupEventDispatcher.fireUserNicknameChanged(group, user, oldNickname);
    }

    private void fireNumberOfUserChanged() {
        if (this.numberOfUsersListener != null) {
            this.numberOfUsersListener.numberOfUsersChanged(users.size());
        }
    }

    private void checkNumOfUsers() throws FullMemberException {
        if (users.size() >= MAX_USERS) {
            throw new FullMemberException(String.format("MAX_USERS:%d, now:%d.", MAX_USERS, users.size()));
        }
    }

    /**
     * 从持久化层删除用户。
     *
     * @param userName
     * @return
     */
    private boolean persistenceRemoveUser(String userName) {
        try {
            return memberPersistenceMgr.removeMember(groupId, userName);
        } catch (PersistenceException e) {
            LOG.error("加载成员列表失败", e);
            return false;
        }
    }

    /**
     * 从持久化层加载圈子成员列表。
     *
     * @return
     */
    private List<GroupMemberInfo> loadMembers() {
        try {
            return memberPersistenceMgr.getGroupMembers(groupId, null);
        } catch (PersistenceException e) {
            LOG.error("加载成员列表失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 保存用户到持久化层。
     *
     * @param userName
     * @param nickname
     * @return
     */
    private boolean saveUser(String userName, String nickname) {
        try {
            return memberPersistenceMgr.addMember(groupId, userName, nickname);
        } catch (PersistenceException e) {
            LOG.error("保存聊天用户失败。", e);
            return false;
        }
    }

    /**
     * 持久化修改用户昵称。
     *
     * @param userName
     * @param nickname
     * @return
     */
    private boolean persistenceChangeNickName(String userName, String nickname) {
        try {
            return memberPersistenceMgr.changeGroupProfile(groupId, userName, nickname);
        } catch (PersistenceException e) {
            LOG.error("修改昵称失败", e);
            return false;
        }
    }

    /* 将用户添加到管理器内部内存中 */
    private ChatUserImpl addUserInternal(String userName, String nickName) {
        ChatUserImpl user = createUser(userName, nickName);
        users.put(user.getUserName(), user);
        return user;
    }

    /* 创建聊天用户对象实例 */
    private ChatUserImpl createUser(String userName, String nickName) {
        userName = userName.toLowerCase();
        return new ChatUserImpl(userName,
                nickName,
                new JID(userName, xmppDomain, null, true));
    }

}
