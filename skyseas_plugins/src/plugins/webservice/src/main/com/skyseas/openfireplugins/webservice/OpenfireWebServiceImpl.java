package com.skyseas.openfireplugins.webservice;

import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

public class OpenfireWebServiceImpl implements OpenfireWebService {
	
	@Override
	public BaseResult getAllGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResult updateGroupStatus(String domain, String toId, String content) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 发送服务器消息
	 */
	@Override
	public BaseResult sendServerMessage(String domain, String toId, String content) {
		XMPPServer
				.getInstance()
				.getSessionManager()
				.sendServerMessage(new JID(toId + "@" + domain), "服务器提示",
						content);
		return new BaseResult();
	}

	/**
	 * 发送一条消息
	 */
	@Override
	public BaseResult sendNoticeMessage(String domain, String fromId, String toId,
			String content) {
		XMPPServer server = XMPPServer.getInstance();
		MessageRouter messageRouter = server.getMessageRouter();
		Message message = new Message();
		message.setFrom(new JID(fromId + "@" + domain));
		message.setTo(new JID(toId + "@" + domain));
		message.setBody(content);
		message.setType(Message.Type.headline);
		messageRouter.route(message);
		return new BaseResult();
	}

}