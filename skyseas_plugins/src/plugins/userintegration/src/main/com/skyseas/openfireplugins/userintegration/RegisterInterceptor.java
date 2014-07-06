package com.skyseas.openfireplugins.userintegration;

import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Packet;

/**
 * Created by apple on 14-7-4.
 */
public class RegisterInterceptor implements PacketInterceptor {
    private UserEventSubscriber subscriber;

	public RegisterInterceptor(UserEventSubscriber subscriber) {
		if(subscriber == null) { throw new NullPointerException("subscriber"); }
		this.subscriber = subscriber;
    }

    @Override
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
    	System.out.printf("incoming:%b\r\n", incoming);
    	System.out.printf("processed:%b\r\n", processed);
    	System.out.printf("xml:%s\r\n", packet.toXML());
    	System.out.println();
    	System.out.println("--------------------------------");
    	System.out.println();
    	System.out.println();
    }

	public UserEventSubscriber getSubscriber() {
		return subscriber;
	}
}
