package com.skyseas.openfireplugins.group;

import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.packet.IQ;

public class GroupIQHandler extends IQHandler {
	private IQHandlerInfo info;
	public GroupIQHandler() {
		super("GroupExtensionIQHandler");
	}

	
	/**
	 * ����IQ���������Ϣ��
	 */
	@Override
	public IQHandlerInfo getInfo() {
		if(info == null) {
			info = new IQHandlerInfo("x", "http://skyseas.com/protocol/group#user");
		}
		return info;
	}

	@Override
	public IQ handleIQ(IQ message) throws UnauthorizedException {
		// TODO Auto-generated method stub
		return null;
	}

}
