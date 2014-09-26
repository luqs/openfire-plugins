package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.*;
import com.skyseas.openfireplugins.group.util.Paging;
import junit.framework.TestCase;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.xmpp.packet.JID;

import java.util.ArrayList;
import java.util.List;

public class GroupManagerImplTest extends TestCase {
    @Mocked
    GroupPersistenceManager persistenceManager;
    @Mocked
    GroupService groupService;
    GroupManagerImpl groupManager;

    GroupInfo groupInfo;
    GroupImpl group;

    @Override
    public void setUp() {
        groupManager = new GroupManagerImpl(groupService, persistenceManager);
        groupInfo = new GroupInfo();
        groupInfo.setId(22);
        groupInfo.setOwner("zz");
        group = new GroupImpl(new JID("100@group.skysea.com"), groupInfo, null);
    }

    public void testSearch() throws Exception {
        // Arrange
        final Paging<GroupInfo> groups = new Paging<GroupInfo>();
        final GroupQueryObject queryObject = new GroupQueryObject();
        final int offset = 1;
        final int limit = 10;

        new NonStrictExpectations() {
            {
                persistenceManager.queryGroups(queryObject, offset, limit);
                result = groups;
                times = 1;
            }
        };

        // Act
        Object result = groupManager.search(queryObject, offset, limit);

        // Assert
        assertEquals(groups, result);
    }

    public void testCreate() throws Exception {
        // Arrange
        new NonStrictExpectations(GroupImpl.class) {
            {
                GroupImpl.create(groupInfo, persistenceManager, groupService);
                result = group;
                times = 1;
            }
        };

        // Act
        GroupImpl retGroup = groupManager.create(groupInfo);

        // Assert
        assertEquals(group, retGroup);
        assertEquals(retGroup, groupManager.getActivatedGroup(retGroup.getId()));
    }

    public void testGetMemberGroups() throws Exception {
        // Arrange
        final String username = "zz";
        final List<GroupInfo> groups = new ArrayList<GroupInfo>();
        new NonStrictExpectations() {
            {
                persistenceManager.getMemberJoinedGroups(username);
                result = groups;
                times = 1;
            }
        };

        // Act
        Object result = groupManager.getMemberJoinedGroups(username);

        // Assert
        assertEquals(groups, result);
    }

    public void testRemoveGroup() throws Exception {

        // Arrange
        new NonStrictExpectations(GroupImpl.class) {
            {
                GroupImpl.load(groupInfo.getId(), persistenceManager, groupService);
                result = group;
                times = 1;
            }
        };

        new NonStrictExpectations() {
            {
                group.destroy();
                result = true;
                times = 1;
            }
        };

        // Act
        Group result1 = groupManager.getGroup(group.getId());
        boolean result2 = groupManager.remove(result1);
        Group result3 = groupManager.getActivatedGroup(group.getId());

        // Assert
        assertEquals(group, result1);
        assertTrue(result2);
        assertNull(result3);
    }

    public void testRemoveGroup_When_Group_Is_Not_Active() {
        assertNull(groupManager.getActivatedGroup(group.getId()));
        assertFalse(groupManager.remove(group));
    }

    public void testGetGroup() throws Exception {
        // Arrange
        new NonStrictExpectations(GroupImpl.class) {
            {
                GroupImpl.load(groupInfo.getId(), persistenceManager, groupService);
                result = group;
                times = 1;
            }
        };

        // Act
        Object result1 = groupManager.getGroup(group.getId());
        Object result2 = groupManager.getGroup(group.getId());
        Object result3 = groupManager.getActivatedGroup(group.getId());

        // Assert
        assertEquals(group, result1);
        assertEquals(group, result2);
        assertEquals(group, result3);
    }


}