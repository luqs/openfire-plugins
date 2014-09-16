package com.skyseas.openfireplugins.group.iq.group;

import com.skyseas.openfireplugins.group.GroupInfo;
import com.skyseas.openfireplugins.group.util.DataFormModelBase;
import org.dom4j.Element;

/**
 * 圈子信息扩展协议包
 */
class GroupInfoPacket extends DataFormModelBase {
    GroupInfoPacket(Element element) {
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
