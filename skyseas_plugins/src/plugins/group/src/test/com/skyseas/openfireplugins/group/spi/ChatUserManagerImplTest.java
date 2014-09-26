package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.ChatUser;
import com.skyseas.openfireplugins.group.GroupMemberInfo;
import com.skyseas.openfireplugins.group.GroupMemberPersistenceManager;
import junit.framework.TestCase;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import java.util.ArrayList;
import java.util.List;

public class ChatUserManagerImplTest extends TestCase {
    @Mocked
    GroupMemberPersistenceManager persistenceManager;
    private int groupId = 100;
    private GroupMemberInfo memberInfo;
    private ChatUserManagerImpl manager;

    @Override
    protected void setUp() throws Exception {
        memberInfo = new GroupMemberInfo();
        memberInfo.setUserName("user");
        memberInfo.setNickName("碧眼狐狸");

        final List<GroupMemberInfo> members = new ArrayList<GroupMemberInfo>();
        members.add(memberInfo);
        new NonStrictExpectations() {
            {
                persistenceManager.getGroupMembers(groupId, null);
                result = members;
                times = 1;
            }
        };

        manager = new ChatUserManagerImpl(groupId, persistenceManager);
    }

    public void testInitialize() throws Exception {
        assertUserInManager(memberInfo.getUserName(), memberInfo.getNickName());
        assertEquals(1, manager.getUsers().size());
    }

    public void testAddUser() throws Exception {
        // Arrange
        final String userName = "user2";
        final String nickname = "大海故乡";
        new NonStrictExpectations() {
            {
                persistenceManager.addMember(groupId, userName, nickname);
                result = true;
                times = 1;
            }
        };

        // Act
        ChatUser user = manager.addUser(userName, nickname);

        // Assert
        assertEquals(user.getUserName(), userName);
        assertEquals(user.getNickname(), nickname);
        assertEquals(user, manager.getUser(userName));
        assertUserInManager(userName, nickname);
        assertEquals(2, manager.getUsers().size());
    }

    public void testRemoveUser() throws Exception{
        // Arrange
        ChatUser user = manager.getUser(memberInfo.getUserName());
        new NonStrictExpectations(){
            {
                persistenceManager.removeMember(groupId, memberInfo.getUserName());
                result = true;
                times = 1;
            }
        };

        // Act
        ChatUser result = manager.removeUser(memberInfo.getUserName());

        // Assert
        assertEquals(user, result);
        assertEquals(0, manager.getUsers().size());
        assertFalse(manager.hasUser(memberInfo.getUserName()));
        assertNull(manager.getUser(memberInfo.getUserName()));
    }

    public void testChangeNickname() throws Exception {
        // Arrange
        final String newNickname = "大刀关胜";
        new NonStrictExpectations(){
            {
                persistenceManager.changeGroupProfile(groupId, memberInfo.getUserName(), newNickname);
                result = true;
                times = 1;
            }
        };

        // Act
        manager.changeNickname(memberInfo.getUserName(), newNickname);

        // Assert
        ChatUser user = manager.getUser(memberInfo.getUserName());
        assertEquals(newNickname, user.getNickname());
    }

    private void assertUserInManager(String userName, String nickName) {
        assertTrue(manager.hasUser(userName));
        ChatUser user = manager.getUser(userName);
        assertEquals(userName, user.getUserName());
        assertEquals(nickName, user.getNickname());

        boolean found = false;
        for (ChatUser u:manager.getUsers()) {
            if(u.getUserName().equals(userName)) {
                assertEquals(nickName, u.getNickname());
                found = true;
            }
        }
        assertTrue(found);
    }
}