package com.skyseas.openfireplugins.group.iq.group;

import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupInfo;
import com.skyseas.openfireplugins.group.iq.AbstractIQHandler;
import com.skyseas.openfireplugins.group.iq.IQHandler;
import com.skyseas.openfireplugins.group.iq.XHandler;
import org.dom4j.Element;
import org.xmpp.forms.DataForm;
import org.xmpp.forms.FormField;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

import java.util.Date;

/**
 * 圈子创建处理程序。
 * Created by apple on 14-9-14.
 */
@XHandler(namespace = IQHandler.GROUP_NAMESPACE, elementName = "x")
class CreateHandler extends AbstractIQHandler {
    @Override
    public void process(IQ packet) {
        assert packet != null;

        GroupInfo groupInfo = UpdateHandler.GroupInfoPacket.getGroupInfo(packet.getChildElement());
        groupInfo.setOwner(packet.getFrom().getNode());
        groupInfo.setCreateTime(new Date());

        Group group = createGroup(groupInfo);
        if(group != null) {
            routePacket(createResultIQ(packet, group.getJid()));
        }else {
            replyError(packet, PacketError.Condition.internal_server_error);
        }
    }

    private Group createGroup(GroupInfo groupInfo) {
        try {
            return groupManager.create(groupInfo);
        } catch (Exception exp) {
            handleException(exp, "创建圈子失败");
            return null;
        }
    }

    private IQ createResultIQ(IQ packet, JID jid) {
        packet = IQ.createResultIQ(packet);
        Element element = packet.setChildElement("x", IQHandler.GROUP_NAMESPACE);

        /* 创建数据表单返回新创建的圈子jid */
        DataForm form = new DataForm(DataForm.Type.result);
        FormField jidField = form.addField();
        jidField.setVariable("jid");
        jidField.addValue(jid);

        element.add(form.getElement());
        return packet;
    }
}
