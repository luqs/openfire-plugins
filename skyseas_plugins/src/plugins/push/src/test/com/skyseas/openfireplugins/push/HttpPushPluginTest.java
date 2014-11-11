package com.skyseas.openfireplugins.push;

import junit.framework.TestCase;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.container.PluginServlet;

import javax.servlet.GenericServlet;
import java.io.File;

public class HttpPushPluginTest extends TestCase {
    private HttpPushPlugin plugin;
    @Mocked PluginManager pluginManager;
    @Mocked PushServlet pushServlet;

    @Override
    protected void setUp() {
        plugin = new HttpPushPlugin();
        new NonStrictExpectations(PluginServlet.class) {
            {
                pluginManager.getPluginDirectory(plugin);
                result = new File("/push");
            }
        };
    }

    public void testInitializePlugin() throws Exception {

        // Arrange


        // Act
        plugin.initializePlugin(pluginManager, null);

        // Assert
        new Verifications(){
            {
                PluginServlet.registerServlet(
                        pluginManager,
                        plugin,
                        withAny((PushServlet)null),
                        HttpPushPlugin.PATH);
                times = 1;
            }
        };
    }

    public void testDestroyPlugin() throws Exception {
        // Arrange
        plugin.initializePlugin(pluginManager, null);

        // Act
        plugin.destroyPlugin();

        // Assert
        new Verifications(){
            {
                PluginServlet.unregisterServlet(plugin, HttpPushPlugin.PATH);
                times = 1;
            }
        };
    }
}