package com.skyseas.openfireplugins.group.iq.member;

import com.skyseas.openfireplugins.group.util.DataListPacket;
import com.skyseas.openfireplugins.group.GroupInfo;
import com.skyseas.openfireplugins.group.iq.AbstractIQHandler;
import com.skyseas.openfireplugins.group.iq.IQHandler;
import com.skyseas.openfireplugins.group.iq.QueryHandler;
import com.skyseas.openfireplugins.group.iq.ServiceIQHandler;
import com.skyseas.openfireplugins.group.iq.group.GroupSummaryProcessDelegate;
import com.skyseas.openfireplugins.group.util.StringUtils;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

import java.util.Collections;
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
        List<GroupInfo> groups = getJoinedGroups(userName);

        if (groups != null) {
            IQ reply = createResultIQ(packet, groups);
            routePacket(reply);
        } else {
            replyError(packet, PacketError.Condition.internal_server_error);
        }
    }

    private List<GroupInfo> getJoinedGroups(String userName) {
        try {
            List<GroupInfo> groups = groupManager.getMemberGroups(userName);
            if (groups != null) {
                return groups;
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            handleException(e, "获取用户圈子列表失败。UserName:%s", userName);
            return null;
        }
    }

    private IQ createResultIQ(IQ packet, List<GroupInfo> groups) {
        DataListPacket<GroupInfo> dataListPacket = new DataListPacket<GroupInfo>(
                IQHandler.MEMBER_NAMESPACE,
                groups,
                new GroupSummaryProcessDelegate(groupService.getServiceDomain()));

        packet = IQ.createResultIQ(packet);
        packet.setChildElement(dataListPacket.getElement().addAttribute("node", "groups"));
        return packet;
    }


}
