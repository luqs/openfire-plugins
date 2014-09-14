package com.skyseas.openfireplugins.group.spi;

import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupService;
import com.skyseas.openfireplugins.group.iq.AbstractIQHandler;
import com.skyseas.openfireplugins.group.iq.QueryHandler;
import com.skyseas.openfireplugins.group.iq.XHandler;
import junit.framework.TestCase;
import mockit.Mocked;
import org.xmpp.packet.IQ;

public class IQDispatcherTest extends TestCase {
    @Mocked GroupService groupService;
    @Mocked Group group;
    private IQDispatcher dispatcher;

    @Override
    public void setUp() {
       dispatcher = new IQDispatcher(groupService);
    }

    public void testInstallHandler() throws Exception {
        // Arrange

        // Act
        dispatcher.installHandler(TestIQHandler1.class);
        dispatcher.installHandler(TestIQHandler2.class);

        // Assert
        assertEquals(2, dispatcher.getHandlers().size());
    }

    public void testGetHandlers() throws Exception {

    }

    public void testDispatch() throws Exception {

    }

    @QueryHandler(namespace = "test", node = "info")
    private static class TestIQHandler1 extends AbstractIQHandler {
        @Override
        public void process(Group group, IQ packet) {

        }
    }

    @XHandler(namespace = "test", elementName = "info")
    private static class TestIQHandler2 extends AbstractIQHandler {
        @Override
        public void process(Group group, IQ packet) {

        }
    }
}