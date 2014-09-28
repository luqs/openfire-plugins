package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.ChatUser;
import com.skyseas.openfireplugins.group.FullMemberException;
import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupInfo;
import com.skyseas.openfireplugins.group.iq.owner.ApplyProcessPacket;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;

import java.util.Random;

/**
 * 圈子申请策略。
 * Created by zhangzhi on 2014/9/28.
 */
abstract class ApplyStrategy {

    /**
     * 用户申请加入圈子。
     * @param group
     * @param proposer
     * @param nickname
     * @param reason
     * @throws FullMemberException
     */
    public abstract void applyToJoin(Group group, JID proposer, String nickname, String reason)
            throws FullMemberException;


    /**
     * 获得申请处理策略，通过圈子开放程序。
     * @param opennessType
     * @return
     */
    public static ApplyStrategy getStrategyFor(GroupInfo.OpennessType opennessType) {
        return opennessType == GroupInfo.OpennessType.AFFIRM_REQUIRED
                ? FORWARDING_TO_OWNER
                : IMMEDIATE_PROCESS;
    }

    /**
     * 直接处理申请通过。
     */
    final static ApplyStrategy IMMEDIATE_PROCESS = new ApplyStrategy() {

        @Override
        public void applyToJoin(Group group, JID proposer, String nickname, String reason)
                throws FullMemberException {

            /**
             * 1.直接添加用户到圈子。
             * 2.通知申请者。
             */
            group.getChatUserManager().addUser(proposer.getNode(), nickname);
            Message message = ApplyProcessPacket.newInstanceForApplyResult(true, group.getJid().toString(), null);
            group.send(proposer, message);
        }
    };


    /**
     * 将申请转发给圈子所有者。
     */
    final static ApplyStrategy FORWARDING_TO_OWNER = new ApplyStrategy() {
        @Override
        public void applyToJoin(Group group, JID proposer, String nickname, String reason) {
            /**
             * 1.创建申请事务。
             * 2.将申请信息转发到圈子所有者。
             */
            String transId = createApplyTrans(proposer.getNode(), group.getId());
            Message message = ApplyProcessPacket.newInstanceForwardingToOwner(
                    transId,
                    proposer.toBareJID(),
                    reason);
            group.send(group.getOwner(), message);
        }

        private String createApplyTrans(String userName, String groupId) {
            return String.valueOf(Math.abs(new Random().nextInt()));
        }
    };
}
