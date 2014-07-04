package com.skyseas.openfireplugins.userintegration;

import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Packet;

/**
 * Created by apple on 14-7-4.
 */
public class RegisterInterceptor implements PacketInterceptor {
    public RegisterInterceptor(RegisterSubscriber subscriber) {

    }

    @Override
    public void interceptPacket(Packet packet, Session session, boolean b, boolean b2) throws PacketRejectedException {

    }
}
