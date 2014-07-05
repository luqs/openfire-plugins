package com.skyseas.openfireplugins.userintegration;

import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Packet;

public class AbstractPacketInterceptor implements PacketInterceptor {

	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
		// TODO Auto-generated method stub
	}
	
	

}
