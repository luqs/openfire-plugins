package com.skyseas.openfireplugins.group.iq.group;

import com.skyseas.openfireplugins.group.GroupInfo;
import com.skyseas.openfireplugins.group.util.DataFormExtension;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;

/**
 * 圈子信息扩展协议包
 */
final class GroupInfoPacket{

    public static GroupInfo getGroupInfo(IQ packet) {
        DataFormExtension form = DataFormExtension.getForm(packet);
        return form != null ? getGroupInfo(form) : null;
    }

    static GroupInfo getGroupInfo(DataFormExtension form) {
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setName(form.getFirstValue("name"));
        groupInfo.setSubject(form.getFirstValue("subject"));
        groupInfo.setLogo(form.getFirstValue("logo"));
        groupInfo.setDescription(form.getFirstValue("description"));
        groupInfo.setCategory(form.getFirstValueAsInt("category", 1));

        String opennessValue = form.getFirstValue("openness");
        if (opennessValue != null) {
            groupInfo.setOpennessType(GroupInfo.OpennessType.valueOf(opennessValue));
        } else {
            groupInfo.setOpennessType(GroupInfo.OpennessType.PUBLIC);
        }
        return groupInfo;
    }
}