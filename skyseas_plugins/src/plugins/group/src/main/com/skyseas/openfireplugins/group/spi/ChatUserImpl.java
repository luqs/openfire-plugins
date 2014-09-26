package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.ChatUser;

/**
 * Created by zhangzhi on 2014/9/26.
 */
public class ChatUserImpl implements ChatUser {
    private final String userName;
    private volatile String nickname;

    public ChatUserImpl(String userName, String nickName) {
        this.userName = userName;
        this.nickname = nickName;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
