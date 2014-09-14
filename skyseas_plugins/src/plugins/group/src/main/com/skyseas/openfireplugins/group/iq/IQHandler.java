package com.skyseas.openfireplugins.group.iq;

import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupService;
import org.xmpp.packet.IQ;

/**
 * Created by apple on 14-9-14.
 */
public interface IQHandler {

    void process(Group group, IQ packet);

    void initialize(GroupService groupService);
}
