package com.skyseas.openfireplugins.group;

import com.skyseas.openfireplugins.group.util.Paging;

import java.util.List;

/**
 * Created by apple on 14-9-14.
 */
public interface GroupManager {

    /**
     * 搜索圈子信息列表。
     * @return
     */
    Paging<GroupInfo> search(GroupQueryObject query, int offset, int limit);

    /**
     * 创建圈子。
     * @param groupInfo
     * @return 圈子标示符。
     */
    Group create(GroupInfo groupInfo);


    /**
     * 获得圈子Id。
     * @param id
     * @return
     */
    Group getGroup(String id);

    boolean remove(Group group);

    List<GroupInfo> getMemberGroups(String userName);
}
