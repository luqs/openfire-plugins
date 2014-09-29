package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.ChatUser;
import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupEventListener;
import com.skyseas.openfireplugins.group.MessageFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

/**
 * 圈子事件广播监听器。
 * Created by zhangzhi on 2014/9/29.
 */
public class GroupEventBroadcastListener implements GroupEventListener {
    @Override
    public void userJoined(Group group, ChatUser user) {
        Message msg = MessageFactory.newInstanceForMemberJoined(user.getUserName(), user.getNickname());
        group.broadcast(msg);
    }

    @Override
    public void userExited(Group group, ChatUser user, String reason) {

    }

    @Override
    public void userKicked(Group group, ChatUser user, JID from, String reason) {

    }

    @Override
    public void userNicknameChanged(Group group, ChatUser user, String oldNickname) {

    }



    @Override
    public void groupCreated(Group group) {

    }

    @Override
    public void groupDestroyed(Group group, JID from, String reason) {

    }

    @Override
    public void groupInfoChanged(Group group) {

    }
}
