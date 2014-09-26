package com.skyseas.openfireplugins.group;

import java.util.Collection;

/**
 * Created by apple on 14-9-13.
 */
public interface ChatUserManager {
    /**
     * 获得用户列表。
     * @return
     */
    Collection<? extends ChatUser> getUsers();

    /**
     * 获得特定用户。
     * @param userName
     * @return
     */
    ChatUser getUser(String userName);

    /**
     * 返回当前用户是否是多用户聊天房间用户。
     * @param userName
     * @return
     */
    boolean hasUser(String userName);

    /**
     * 添加用户。
     * @param userName
     * @param nickname
     */
    ChatUser addUser(String userName, String nickname) throws FullMemberException;

    /**
     * 删除用户。
     * @param userName
     * @return
     */
    ChatUser removeUser(String userName);

    /**
     * 修改用户昵称。
     * @param userName
     * @param nickname
     */
    void changeNickname(String userName, String nickname);

}
