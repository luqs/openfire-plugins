package com.skyseas.openfireplugins.group;

/**
 * Created by apple on 14-9-14.
 */

import org.xmpp.packet.JID;

/**
 * 圈子基础接口。
 * Created by apple on 14-9-13.
 */
public interface Group extends MultiUserChat {

    /**
     * 获得圈子所有者。
     * @return
     */
    JID getOwner();

    /**
     * 销毁圈子。
     */
    void destroy();

    /**
     * 获得圈子描述信息。
     * @return
     */
    GroupInfo getGroupInfo();

    /**
     * 更新圈子描述
     */
    boolean updateGroupInfo(GroupInfo groupInfo);

}
