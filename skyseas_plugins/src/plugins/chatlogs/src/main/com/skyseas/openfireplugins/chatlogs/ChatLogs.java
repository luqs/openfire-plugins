package com.skyseas.openfireplugins.chatlogs;

import java.sql.Timestamp;

import org.jivesoftware.util.JiveConstants;

/**
 * function: 聊天记录对象实体
 */
public class ChatLogs {
	private long messageId;
	private String sessionJID;
	private String sender;
	private String receiver;
	private Timestamp createDate;
	private String content;
	private String detail;
	private int length;
	private int state; // 1 表示删除

	public interface LogState {
		int show = 0;
		int remove = 1;
	}

	/**
	 * function: 自增id序列管理器，类型变量
	 */
	public class ChatLogsConstants extends JiveConstants {
		// 日志表id自增对应类型
		public static final int CHAT_LOGS = 52;
		// 用户在线统计id自增对应类型
		public static final int USER_ONLINE_STATE = 53;
	}

	public ChatLogs() {
	}

	public ChatLogs(String sessionJID, Timestamp createDate, String content,
			String detail, int length) {
		super();
		this.sessionJID = sessionJID;
		this.createDate = createDate;
		this.content = content;
		this.detail = detail;
		this.length = length;
	}

	public ChatLogs(long messageId, String sessionJID, Timestamp createDate,
			String content, String detail, int length, int state) {
		super();
		this.messageId = messageId;
		this.sessionJID = sessionJID;
		this.createDate = createDate;
		this.content = content;
		this.detail = detail;
		this.length = length;
		this.state = state;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public String getSessionJID() {
		return sessionJID;
	}

	public void setSessionJID(String sessionJID) {
		this.sessionJID = sessionJID;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "messageId:"+getMessageId()+"\t"
		+"sessionJID:"+getSessionJID()+"\t"
		+"sender:"+getSender()+"\t"
		+"receiver:"+getReceiver()+"\t"
		+"createDate:"+getCreateDate()+"\t"
		+"content:"+getContent()+"\t"
		+"detail:"+getDetail()+"\t"
		+"length:"+getLength()+"\t"
		+"state:"+getState();
		 
	}
}