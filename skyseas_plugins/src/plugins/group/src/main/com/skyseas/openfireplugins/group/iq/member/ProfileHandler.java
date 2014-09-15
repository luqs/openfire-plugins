package com.skyseas.openfireplugins.group.iq.member;

import com.skyseas.openfireplugins.group.ChatUser;
import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupEventDispatcher;
import com.skyseas.openfireplugins.group.iq.GroupIQHandler;
import com.skyseas.openfireplugins.group.iq.ModelPacket;
import com.skyseas.openfireplugins.group.util.StringUtils;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

/**
 * 用户个人信息处理程序。
 * Created by zhangzhi on 2014/9/15.
 */
public class ProfileHandler extends GroupIQHandler {
    @Override
    protected void process(IQ packet, Group group) {
        assert packet != null;
        assert group != null;

        String userName     = packet.getFrom().getNode();
        String nickname  = new ProfilePacket(packet.getChildElement()).getNickname();

        if(StringUtils.isNullOrEmpty(userName)||
           StringUtils.isNullOrEmpty(nickname)) {
            replyError(packet, PacketError.Condition.bad_request);
            return;
        }

        ChatUser user = group.getChatUserManager().getUser(userName);
        if(user == null) {
            replyError(packet, PacketError.Condition.not_authorized);
            return;
        }

        String oldNickname = user.getNickname();
        if(changeNickname(group, userName, nickname)){
            replyOK(packet);

            /* 触发用户修改昵称事件。 */
            GroupEventDispatcher.fireUserNicknameChanged(
                    group,
                    user,
                    oldNickname,
                    nickname);
        }else {
            replyError(packet, PacketError.Condition.internal_server_error);
        }

    }

    private boolean changeNickname(Group group, String userName, String nickname) {
        try {
            group.getChatUserManager().changeNickname(userName, nickname);
            return true;
        }catch (Exception exp) {
            handleException(exp, "修改用户昵称失败，GroupId:%s, UserName:%s, Nickname:%s",
                    group.getId(),
                    userName,
                    nickname);
            return false;
        }
    }

    private static class ProfilePacket extends ModelPacket {
        public ProfilePacket(Element element) {
            super(element, "profile");
        }
        public String getNickname() {
            return getElementValue("nickname", null);
        }
    }
}
