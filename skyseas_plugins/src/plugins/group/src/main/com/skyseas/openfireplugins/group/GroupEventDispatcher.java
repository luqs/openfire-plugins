package com.skyseas.openfireplugins.group;

import org.xmpp.packet.JID;

/**
 * Created by zhangzhi on 2014/9/15.
 */
public class GroupEventDispatcher {

    public static void fireUserExited(Group group, ChatUser user, String reason) {

    }

    public static void fireUserNicknameChanged(Group group, ChatUser user, String oldNickname) {

    }

    public static void fireGroupDestroyed(Group group, JID from, String reason) {

    }

    public static void fireUserKick(Group group, ChatUser user, JID from, String reason) {

    }

    public static void fireUserJoined(Group group, ChatUser user) {

    }

    public static void fireGroupCreated(Group group) {

    }

    public static void fireGroupInfoChanged(Group group) {
    }
}
