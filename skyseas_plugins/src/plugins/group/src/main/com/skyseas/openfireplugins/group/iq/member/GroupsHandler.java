package com.skyseas.openfireplugins.group.iq.member;

import com.skyseas.openfireplugins.group.util.DataListPacket;
import com.skyseas.openfireplugins.group.GroupInfo;
import com.skyseas.openfireplugins.group.iq.AbstractIQHandler;
import com.skyseas.openfireplugins.group.iq.IQHandler;
import com.skyseas.openfireplugins.group.iq.QueryHandler;
import com.skyseas.openfireplugins.group.iq.ServiceIQHandler;
import com.skyseas.openfireplugins.group.iq.group.GroupSummaryProcessDelegate;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

import java.util.List;

/**
 * 用户圈子列表处理程序。
 * Created by zhangzhi on 2014/9/15.
 */
@QueryHandler(namespace = IQHandler.MEMBER_NAMESPACE, node = "groups")
class GroupsHandler extends ServiceIQHandler {
    @Override
    public void process(IQ packet) {
        assert packet != null;

        String userName = packet.getFrom().getNode();
        List<GroupInfo> groupInfoList;

        try {
            groupInfoList = groupManager.getMemberGroups(userName);
        } catch (Exception e) {
            handleException(e, "获取用户圈子列表失败。UserName:%s", userName);
            replyError(packet, PacketError.Condition.internal_server_error);
            return;
        }

        DataListPacket<GroupInfo> dataListPacket = new DataListPacket<GroupInfo>(
                IQHandler.MEMBER_NAMESPACE,
                groupInfoList,
                new GroupSummaryProcessDelegate(groupService.getServiceDomain()));

        IQ reply = IQ.createResultIQ(packet);
        reply.setChildElement(dataListPacket.getElement().addAttribute("node", "groups"));
        routePacket(reply);
    }
}
