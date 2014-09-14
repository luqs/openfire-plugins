package com.skyseas.openfireplugins.group.iq.group;

import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupInfo;
import com.skyseas.openfireplugins.group.NoPermissionException;
import com.skyseas.openfireplugins.group.iq.DataFormModelBase;
import com.skyseas.openfireplugins.group.iq.GroupIQHandler;
import com.skyseas.openfireplugins.group.iq.IQHandler;
import com.skyseas.openfireplugins.group.iq.XHandler;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

/**
 * 圈子更新处理程序。
 * Created by apple on 14-9-14.
 */
@XHandler(namespace = IQHandler.GROUP_NAMESPACE, elementName = "x")
class UpdateHandler extends GroupIQHandler {
    @Override
    public void process(IQ packet, Group group) {
        assert packet != null;
        assert group != null;

        GroupInfo updateInfo = GroupInfoPacket.getGroupInfo(packet.getChildElement());
        try{
            group.updateGroupInfo(updateInfo, packet.getFrom());
            replyOK(packet);
        }catch (NoPermissionException exp) {
            /*无操作权限*/
            replyError(packet, PacketError.Condition.not_authorized);
        }catch (Exception exp) {
            handleException(exp, "更新圈子信息失败");
            replyError(packet, PacketError.Condition.internal_server_error);
        }
    }

    /**
     * 圈子信息扩展协议包
     */
    static class GroupInfoPacket extends DataFormModelBase {
        private GroupInfoPacket(Element element) {
            super(element);
        }

        public static GroupInfo getGroupInfo(Element element) {
            GroupInfoPacket pak = new GroupInfoPacket(element);
            return pak.getGroupInfo();
        }

        public GroupInfo getGroupInfo() {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setName(getFieldValue("name"));
            groupInfo.setSubject(getFieldValue("subject"));
            groupInfo.setLogo(getFieldValue("logo"));
            groupInfo.setDescription(getFieldValue("description"));
            groupInfo.setCategory(getIntegerFieldValue("category", 1));

            String opennessValue =getFieldValue("openness");
            if(opennessValue != null) {
                groupInfo.setOpennessType(GroupInfo.OpennessType.valueOf(opennessValue));
            }else {
                groupInfo.setOpennessType(GroupInfo.OpennessType.PUBLIC);
            }
            return groupInfo;
        }
    }
}
