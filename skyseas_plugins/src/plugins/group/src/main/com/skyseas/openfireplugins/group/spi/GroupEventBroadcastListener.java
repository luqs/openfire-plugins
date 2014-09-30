package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.*;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

/**
 * 圈子事件广播监听器。
 * Created by zhangzhi on 2014/9/29.
 */
public final class GroupEventBroadcastListener implements GroupEventListener {
    public final static GroupEventBroadcastListener INSTANCE = new GroupEventBroadcastListener();
    private GroupEventBroadcastListener(){}

    @Override
    public void userJoined(Group group, ChatUser user) {
        Message msg = MessageFactory.newInstanceForMemberJoined(user.getUserName(), user.getNickname());
        group.broadcast(msg);
    }

    @Override
    public void userExited(Group group, ChatUser user, String reason) {
        Message msg = MessageFactory.newInstanceForMemberExit(user.getUserName(), user.getNickname(), reason);
        group.broadcast(msg);
    }

    @Override
    public void userKicked(Group group, ChatUser user, JID from, String reason) {
        /* 广播到圈子成员时，忽略reason，只有发给被踢出者时才包含reason */
        Message msg = MessageFactory.newInstanceForMemberKick(user.getUserName(), user.getNickname(), from, null);
        group.broadcast(msg);

        msg = MessageFactory.newInstanceForMemberKick(user.getUserName(), user.getNickname(), from, reason);
        msg.setType(null); /* 确保可以离线存储 */
        group.send(((ChatUserImpl)user).getJid(), msg);
    }

    @Override
    public void userNicknameChanged(Group group, ChatUser user, String oldNickname) {
        Message msg = MessageFactory.newInstanceForMemberUpdateProfile(user.getUserName(),oldNickname, user.getNickname());
        group.broadcast(msg);
    }

    @Override
    public void groupDestroyed(Group group, JID from, String reason) {
        Message msg = MessageFactory.newInstanceForGroupDestroyed(from, reason);
        group.broadcast(msg);
    }

    @Override
    public void groupCreated(Group group) {

    }


    @Override
    public void groupInfoChanged(Group group) {

    }
}
