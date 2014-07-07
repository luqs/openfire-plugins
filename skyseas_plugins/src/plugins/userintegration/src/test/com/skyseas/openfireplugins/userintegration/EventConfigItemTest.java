package com.skyseas.openfireplugins.userintegration;

import junit.framework.TestCase;
import com.skyseas.openfireplugins.userintegration.HttpUserEventSubscriber.EventConfigItem;
import com.skyseas.openfireplugins.userintegration.UserEventSubscriber.UserEventType;

public class EventConfigItemTest extends TestCase {

	public void testGetTargetUrl_when_target_url_should_not_be_format() {
		// Arrange
		String expectTargetUrl = "http://www.xxx.com";
		EventConfigItem item = new EventConfigItem
				(UserEventType.CREATED, true,"post", expectTargetUrl, true);
		UserInfo userInfo = new UserInfo("zz", "1234", "zz@qq.com", "zz");
		
		// Act
		String actualTargetUrl = item.getTargetUrl(userInfo);
		
		// Assert
		assertEquals(expectTargetUrl, actualTargetUrl);
	}
	
	public void testGetTargetUrl_when_target_url_should_be_format() {
		// Arrange
		EventConfigItem item = new EventConfigItem
				(UserEventType.CREATED, true,"post", "http://www.xxx.com?u=$username&u=$username", true);
		UserInfo userInfo = new UserInfo("уежг", "1234", "zz@qq.com", "zz");
		
		// Act
		String targetUrl = item.getTargetUrl(userInfo);
		
		// Assert
		assertEquals("http://www.xxx.com?u=%E5%BC%A0%E6%99%BA&u=%E5%BC%A0%E6%99%BA", targetUrl);
	}
}
