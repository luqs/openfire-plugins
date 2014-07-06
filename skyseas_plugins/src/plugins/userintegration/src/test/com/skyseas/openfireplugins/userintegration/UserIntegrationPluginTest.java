package com.skyseas.openfireplugins.userintegration;

import junit.framework.TestCase;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.jivesoftware.openfire.event.UserEventDispatcher;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.util.JiveGlobals;

public class UserIntegrationPluginTest extends TestCase {

 private UserIntegrationPlugin plugin;

	@Override
	public void setUp() throws Exception {
		plugin = new UserIntegrationPlugin();
	}


	public void testInitializePlugin_when_subscriber_is_not_set(@Mocked UserEventDispatcher dispatcher) {
		
		// Arrange
		new NonStrictExpectations(JiveGlobals.class) {
			{
				JiveGlobals.getProperty(UserIntegrationPlugin.REGISTER_SUBSCRIBER_CLASS_KEY);
				result = null;
			}
		};
		
		// Act
		plugin.initializePlugin(null, null);
		
		// Assert
		final UserIntegrationEventLister lisenter = plugin.getUserEventLisenter();
		assertEquals(lisenter.getSubscriber().getClass(), HttpSubscriber.class);
		new Verifications(){
			{
				UserEventDispatcher.addListener(lisenter); times = 1;
			}
		};
	}
	
	public void testInitializePlugin_when_subscriber_is_set(@Mocked UserEventDispatcher dispatcher) {
		
		// Arrange
		new NonStrictExpectations(JiveGlobals.class) {
			{
				JiveGlobals.getProperty(UserIntegrationPlugin.REGISTER_SUBSCRIBER_CLASS_KEY);
				result = MockSubscriber.class.getName();
			}
		};
		
		// Act
		plugin.initializePlugin(null, null);
		
		// Assert
		final UserIntegrationEventLister lisenter = plugin.getUserEventLisenter();
		assertEquals(lisenter.getSubscriber().getClass(), MockSubscriber.class);
		new Verifications(){
			{
				UserEventDispatcher.addListener(lisenter); times = 1;
			}
		};
	}
	
	public void testDestroyPlugin(@Mocked UserEventDispatcher dispatcher) {
		// Arrange
		plugin.initializePlugin(null, null);
		final UserIntegrationEventLister lisenter = plugin.getUserEventLisenter();
		
		// Act
		plugin.destroyPlugin();
		
		// Assert
		new Verifications(){
			{
				UserEventDispatcher.removeListener(lisenter); times = 1;
			}
		};
	}
	
	public static class MockSubscriber implements UserEventSubscriber {

		@Override
		public void publish(UserInfo user) { }
	}
	
}
