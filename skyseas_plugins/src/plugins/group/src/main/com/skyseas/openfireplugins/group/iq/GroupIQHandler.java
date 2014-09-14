package com.skyseas.openfireplugins.group.iq;

import com.skyseas.openfireplugins.group.Group;
import org.xmpp.packet.IQ;

/**
 * Created by apple on 14-9-14.
 */
public abstract class GroupIQHandler extends AbstractIQHandler {
    @Override
    public void process(IQ packet) {
        throw new RuntimeException();
    }

    protected abstract void process(IQ packet , Group group);
}
