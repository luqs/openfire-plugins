package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.*;
import com.skyseas.openfireplugins.group.util.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangzhi on 2014/9/26.
 */
class GroupManagerImpl implements GroupManager {
    private final static Logger LOG = LoggerFactory.getLogger(GroupManagerImpl.class);
    private final ConcurrentHashMap<String, GroupImpl> groups = new ConcurrentHashMap<String, GroupImpl>(256);
    private final GroupPersistenceManager persistenceMgr;
    private final GroupService groupService;

    public GroupManagerImpl(GroupService groupService, GroupPersistenceManager persistenceMgr) {
        assert groupService != null;
        assert persistenceMgr != null;

        this.groupService = groupService;
        this.persistenceMgr = persistenceMgr;
    }

    /**
     * 搜索圈子列表。
     */
    @Override
    public Paging<GroupInfo> search(GroupQueryObject query, int offset, int limit) {
        assert query != null;
        try {
            return persistenceMgr.queryGroups(query, offset, limit);
        } catch (PersistenceException exp) {
            LOG.error("查询圈子列表失败", exp);
            return new Paging<GroupInfo>();
        }
    }

    /**
     * 创建圈子。
     */
    @Override
    public GroupImpl create(GroupInfo groupInfo) {
        assert groupInfo != null;

        /* 创建持久化的圈子，并将其激活在内存中 */
        GroupImpl group = GroupImpl.create(
                groupInfo,
                persistenceMgr,
                groupService);

        if (group != null) { activeGroup(group); }
        return group;
    }

    /**
     * 获得圈子对象。
     */
    @Override
    public Group getGroup(String groupId) {
        assert groupId != null;

        /* 首先尝试从内存中获取圈子*/
        GroupImpl group = groups.get(groupId);
        if (group != null) { return group; }

        /* 从内存获取失败，尝试从持久化层加载，并激活在内存中 */
        group = GroupImpl.load(Integer.valueOf(groupId),
                persistenceMgr,
                groupService);

        if (group != null) {
            return activeGroup(groupId, group);
        }
        return null;
    }

    /**
     * 删除圈子。
     */
    @Override
    public boolean remove(Group group) {
        assert group != null;

        /* 从内存中移除圈子对象，并销毁该圈子 */
        GroupImpl removedGroup = groups.remove(group.getId());
        if(removedGroup == group) {
            return removedGroup.destroy();
        }
        return false;
    }

    @Override
    public List<GroupInfo> getMemberJoinedGroups(String userName) {
        try {
            // TODO: 缓存
            return persistenceMgr.getMemberJoinedGroups(userName);
        }catch (PersistenceException exp) {
            LOG.error("获取成员圈子列表失败。", exp);
            return Collections.emptyList();
        }
    }

    /**
     * 获得已经在内存中激活的Group对象实例。
     * @param groupId
     * @return
     */
    public Group getActivatedGroup(String groupId){
        assert groupId != null;
        return groups.get(groupId);
    }

    /**
     * 直接将圈子激活在内存中
     * @param group
     */
    private void activeGroup(GroupImpl group) {
        groups.put(group.getId(), group);
    }

    /**
     * 激活圈子，并考虑会与内存中相同groupId冲突的情况。
     * @param groupId
     * @return
     */
    private Group activeGroup(String groupId, GroupImpl newGroup) {

        /* 再次确认圈子不在内存中 */
        Group group = groups.get(groupId);
        if(group != null ){ return group;}

        /**
         * 在此我们并没有使用锁定的方式保证内存中Group的唯一性，
         * 而是使用乐观的并发控制方式，假设针对当前Group的初始化
         * 并没有其他的并发线程。
         *
         * 创建好圈子的Group对象后通过putIfAbsent写入groups，并根据返回值
         * 判断是否写入成功。 如果返回null则代表groups中并不存在该Group，
         * 否则说明遇到并发，这时候返回已存在的Group即可。
         *
         *
         * 同时也要注意，如果创建Group对象的代价较高，并且并发较高时可能并不适合。
         */
        Group orgGroup = groups.putIfAbsent(groupId, newGroup);

        return orgGroup == null /* 返回null代表原先并不存在该Group对象 */
                ? newGroup
                : orgGroup;
    }
}
