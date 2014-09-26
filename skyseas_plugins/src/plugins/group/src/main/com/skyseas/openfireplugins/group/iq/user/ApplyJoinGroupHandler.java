package com.skyseas.openfireplugins.group.iq.user;

import com.skyseas.openfireplugins.group.FullMemberException;
import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.iq.GroupIQHandler;
import com.skyseas.openfireplugins.group.iq.owner.ApplyProcessPacket;
import com.skyseas.openfireplugins.group.util.StringUtils;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

/**
 * 申请加入圈子处理程序。
 * Created by apple on 14-9-15.
 */
public class ApplyJoinGroupHandler extends GroupIQHandler {
    @Override
    protected void process(IQ packet, Group group) {
        assert packet != null;
        assert group != null;

        String userName = packet.getFrom().getNode();
        if(StringUtils.isNullOrEmpty(userName)){
            replyError(packet, PacketError.Condition.bad_request);
            return;
        }

        /* 已经是圈子成员 */
        if(group.getChatUserManager().hasUser(userName)) {
            replyOK(packet);
            //replyError(packet, PacketError.Condition.not_allowed);
            return;
        }

        ApplyProcessPacket applyPacket = new ApplyProcessPacket(packet.getChildElement());
        try {
            group.applyJoin(userName, userName, applyPacket.getReason());
            replyOK(packet);
        }catch (FullMemberException exp) {

            /* 已经达到最大成员限制 */
            handleException(exp, "圈子已满员，申请加入失败,GroupId:%s, UserName:%s",
                    group.getId(), userName);
            replyError(packet, PacketError.Condition.service_unavailable);
        }
    }
}
