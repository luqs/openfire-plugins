package com.skyseas.openfireplugins.userintegration;

import java.util.Date;
import org.jivesoftware.openfire.auth.AuthFactory;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserNotFoundException;
import com.skyseas.openfireplugins.userintegration.UserEventSubscriber.UserEventType;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import junit.framework.TestCase;

public class UserIntegrationEventListenerTest extends TestCase {
	
	@Mocked UserEventSubscriber 			subscriber;
	private UserIntegrationEventListener 	listener;
	private String 							expectPassword;
	private UserEventType 					expectEventType;
	private User 							expectUser;
	
	@Override
	protected void setUp() {
		listener =  new UserIntegrationEventListener(subscriber);
	}
	
	public void testConstructor_when_subscriber_is_null() {
		try {
			new UserIntegrationEventListener(null);
			fail("no throw exception");
		}catch(NullPointerException exp) {
			assertEquals("subscriber is null.", exp.getMessage());
		}
	}
	
	public void testGetSubscriber() {
		// Assert 
		assertEquals(subscriber, listener.getSubscriber());
	}
	
	public void testUserCreated() throws UserNotFoundException  {
		
		// Arrange
		expectEventType = UserEventType.CREATED;
		expectUser = new User("zhangsanCreated","创建", "created@xx.com", new Date(), new Date());
		expectPassword = "createdPassword";
		
		new NonStrictExpectations(AuthFactory.class) {
			{
				AuthFactory.getPassword(expectUser.getUsername());
				returns(expectPassword);
			}
		};
		
		// Act
		listener.userCreated(expectUser, null);
		
		// Assert
		assertEventDispatch();
		
	}
	
	public void testUserDeleting() throws UserNotFoundException  {
			
			// Arrange
			expectEventType = UserEventType.DELETING;
			expectUser = new User("zhangsanDeleting","删除", "deleting@xx.com", new Date(), new Date());
			expectPassword = "deletingPassword";
			
			new NonStrictExpectations(AuthFactory.class) {
				{
					AuthFactory.getPassword(expectUser.getUsername());
					returns(expectPassword);
				}
			};
			
			// Act
			listener.userDeleting(expectUser, null);
			
			// Assert
			assertEventDispatch();
			
		}


	public void testUserModified() throws UserNotFoundException  {
		
		// Arrange
		expectEventType = UserEventType.MODIFIED;
		expectUser = new User("zhangsanModified","修改", "modified@xx.com", new Date(), new Date());
		expectPassword = "modifiedPassword";
		
		new NonStrictExpectations(AuthFactory.class) {
			{
				AuthFactory.getPassword(expectUser.getUsername());
				returns(expectPassword);
			}
		};
		
		// Act
		listener.userModified(expectUser, null);
		
		// Assert
		assertEventDispatch();
		
	}

	

	private void assertEventDispatch() {
		// Assert
		new Verifications() {
			{
				subscriber.publish((UserInfo)any, (UserEventType)any);
				times = 1;
				forEachInvocation = new Object() {
					@SuppressWarnings("unused")
					public void validate(UserInfo userInfo, UserEventType eventType) {
						assertEquals(expectUser.getUsername(), userInfo.getUserName());
						assertEquals(expectUser.getName(), userInfo.getName());
						assertEquals(expectUser.getEmail(), userInfo.getEmail());
						assertEquals(expectPassword, userInfo.getPassword());
						assertEquals(expectEventType, eventType);
					}
				};
				
			}
		};
	}

}
