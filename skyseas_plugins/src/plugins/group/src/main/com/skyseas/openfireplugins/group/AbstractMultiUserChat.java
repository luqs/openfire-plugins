package com.skyseas.openfireplugins.group;

import org.xmpp.packet.JID;

/**
 * Created by apple on 14-9-14.
 */
public abstract class AbstractMultiUserChat implements MultiUserChat {
    protected final String id;
    protected final JID jid;
    protected final ChatUserManager userManager;

    protected AbstractMultiUserChat(String id, JID jid, ChatUserManager userManager) {
        this.id = id;
        this.jid = jid;
        this.userManager = userManager;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public JID getJid() {
        return this.jid;
    }

    @Override
    public ChatUserManager getChatUserManager() {
        return this.userManager;
    }
}
