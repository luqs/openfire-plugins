package com.skyseas.openfireplugins.userintegration;

import junit.framework.TestCase;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.util.JiveGlobals;

public class UserIntegrationPluginTest extends TestCase {

	private UserIntegrationPlugin plugin;

	@Override
	public void setUp() throws Exception {
		plugin = new UserIntegrationPlugin();
	}


	public void testInitializePlugin_when_subscriber_is_not_set() {
		
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
		RegisterInterceptor interceptor = plugin.getInterceptor();
		assertTrue(InterceptorManager.getInstance().getInterceptors().contains(interceptor));
		assertEquals(interceptor.getSubscriber().getClass(), HttpSubscriber.class);
	}
	
	public void testInitializePlugin_when_subscriber_is_set() {
		
		// Arrange
		new NonStrictExpectations(JiveGlobals.class) {
			{
				JiveGlobals.getProperty(UserIntegrationPlugin.REGISTER_SUBSCRIBER_CLASS_KEY);
				result = "com.skyseas.openfireplugins.userintegration.UserIntegrationPluginTest$MockSubscriber";
			}
		};
		
		// Act
		plugin.initializePlugin(null, null);
		
		// Assert
		RegisterInterceptor interceptor = plugin.getInterceptor();
		assertTrue(InterceptorManager.getInstance().getInterceptors().contains(interceptor));
		assertEquals(interceptor.getSubscriber().getClass(), MockSubscriber.class);
	}
	
	public void testDestroyPlugin() {
		// Arrange
		plugin.initializePlugin(null, null);
		
		// Act
		plugin.destroyPlugin();
		
		// Assert
		assertFalse(InterceptorManager
				.getInstance()
				.getInterceptors()
				.contains(plugin.getInterceptor()));
	}
	
	public static class MockSubscriber implements RegisterSubscriber {

		@Override
		public void publish(RegisterUser user) { }
	}
	
}
