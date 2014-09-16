package com.skyseas.openfireplugins.group.iq.owner;

import com.skyseas.openfireplugins.group.util.HasReasonPacket;
import com.skyseas.openfireplugins.group.util.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;


/**
 * Created by apple on 14-9-8.
 */
public class ApplyProcessPacket extends HasReasonPacket {

    public ApplyProcessPacket(String name, String namespace) {
        super(name, namespace, "apply");
    }

    public ApplyProcessPacket(Element element) {
        super(element, "apply");
    }

    public String getReason() {
        return getElementValue("reason");
    }

    private void setReason(String reason) {
        modeElement.addElement("reason").setText(reason);
    }


    public JID getFrom() {
        return new JID(getAttributeValue("from"));
    }

    private void setFrom(String from) {
        modeElement.addAttribute("from", from);
    }

    public String getId() {
        return getAttributeValue("id");
    }

    private void setId(String id) {
        modeElement.addAttribute("id", id);
    }

    public boolean isAgree() {
        return modeElement.element("agree") != null;
    }

    public boolean isDecline() {
        return modeElement.element("decline") != null;
    }


    private String getElementValue(String name) {
        Element ele = this.modeElement.element(name);
        if(ele == null) { return "";}
        return ele.getStringValue();
    }

    private String getAttributeValue(String name) {
        Attribute attr = this.modeElement.attribute(name);
        if(attr == null) { return ""; }
        return attr.getValue();
    }

    /**
     * 创建一个转发给圈子所有者的申请扩展包。
     * @param id
     * @param from
     * @param reason
     * @return
     */
    public static ApplyProcessPacket newInstanceForwardingToOwner(String id, String from, String reason) {
        ApplyProcessPacket packet = new ApplyProcessPacket("x", "http://skysea.com/protocol/group#owner");
        packet.setId(id);
        packet.setFrom(from);
        packet.setReason(reason);
        return packet;
    }


    /**
     * 创建一个反应申请结果的扩展包。
     * @param result
     * @param handler
     * @param reason
     * @return
     */
    public static Message newInstanceForApplyResult(boolean result, String handler, String reason) {
        ApplyProcessPacket packet = new ApplyProcessPacket("x", "http://skysea.com/protocol/group#user");
        Message message = new Message();

        Element resultEle = result
                ? packet.modeElement.addElement("agree")
                : packet.modeElement.addElement("decline");

        if(!StringUtils.isNullOrEmpty(handler)) {
            resultEle.addAttribute("from", handler);
        }

        if(!StringUtils.isNullOrEmpty(reason)) {
            packet.setReason(reason);
        }

        packet.appendTo(message.getElement());
        return message;
    }

}
