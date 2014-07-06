package com.skyseas.openfireplugins.userintegration;

import java.util.Map;

import org.jivesoftware.openfire.event.UserEventListener;
import org.jivesoftware.openfire.user.User;

public class UserIntegrationEventListener implements UserEventListener {

	private UserEventSubscriber subscriber;

	public UserIntegrationEventListener(UserEventSubscriber subscriber) {
		this.subscriber = subscriber;
	}

	@Override
	public void userCreated(User user, Map<String, Object> params) {
	}

	@Override
	public void userDeleting(User user, Map<String, Object> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userModified(User user, Map<String, Object> params) {
		// TODO Auto-generated method stub

	}

	public UserEventSubscriber getSubscriber() {
		return subscriber;
	}

}
