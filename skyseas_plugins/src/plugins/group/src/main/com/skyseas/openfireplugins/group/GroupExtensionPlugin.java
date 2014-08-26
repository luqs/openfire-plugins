package com.skyseas.openfireplugins.group;

import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;

public class GroupExtensionPlugin implements Plugin {

	@Override
	public void destroyPlugin() {
		
	}

	@Override
	public void initializePlugin(PluginManager arg0, File arg1) {
		//
		
		GroupIQHandler handler = new GroupIQHandler();
		XMPPServer.getInstance().getIQRouter().addHandler(handler);
	}

}
