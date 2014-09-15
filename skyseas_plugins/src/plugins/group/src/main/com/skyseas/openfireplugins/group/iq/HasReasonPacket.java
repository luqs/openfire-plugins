package com.skyseas.openfireplugins.group.iq;

import org.dom4j.Element;

/**
 * Created by zhangzhi on 2014/9/15.
 */
public abstract class HasReasonPacket extends ModelPacket implements HasReason {
    protected HasReasonPacket(Element extensionRoot, String modelElementName) {
        super(extensionRoot, modelElementName);
    }

    protected HasReasonPacket(String name, String namespace, String modelElementName) {
        super(name, namespace, modelElementName);
    }


    @Override
    public String getReason() {
        return getElementValue("reason", null);
    }
}
