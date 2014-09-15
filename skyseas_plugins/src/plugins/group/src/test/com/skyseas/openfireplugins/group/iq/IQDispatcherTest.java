package com.skyseas.openfireplugins.group.iq;

import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupService;
import junit.framework.TestCase;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.dom4j.DocumentHelper;
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
        GroupIQHandler[] handlers = new GroupIQHandler[2];
        dispatcher.getHandlers().toArray(handlers);
        assertEquals(2, handlers.length);

        TestIQHandler1 handler1 = (TestIQHandler1)handlers[0];
        assertEquals(groupService, handler1.getGroupService());

        TestIQHandler2 handler2 = (TestIQHandler2)handlers[1];
        assertEquals(groupService, handler2.getGroupService());
    }

    public void testInstallHandler_When_Class_Invalid() throws Exception {
        // Arrange
        Class<?> klass = Object.class;

        // Act & Assert
        try {
            dispatcher.installHandler(klass);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("class is not IQHandler.", e.getMessage());
            return;

        }

        fail();
    }

    public void testDispatch_When_Packet_Contains_query_Extension() throws Exception {
        // Arrange
        dispatcher.installHandler(TestIQHandler1.class);
        final TestIQHandler1 handler = (TestIQHandler1)dispatcher.getHandlers().toArray()[0];
        final IQ iq = new IQ(DocumentHelper.parseText(
                "<iq from='user@skysea.com' to='100@group.skysea.com' id='v2' type='get'>\n" +
                        "  <query xmlns='test' node='info'/>\n" +
                        "</iq>").getRootElement());
        new NonStrictExpectations(handler){
            {
                handler.process(iq, group);
                times = 1;
            }
        };

        // Act
        dispatcher.dispatch(iq, group);

    }
    public void testDispatch_When_Packet_Contains_x_Extension() throws Exception {
        // Arrange
        dispatcher.installHandler(TestIQHandler2.class);
        final TestIQHandler2 handler = (TestIQHandler2)dispatcher.getHandlers().toArray()[0];
        final IQ iq = new IQ(DocumentHelper.parseText(
                "<iq from='user@skysea.com' to='100@group.skysea.com' id='v2' type='get'>\n" +
                        "  <x xmlns='add'><member /></x>\n" +
                        "</iq>").getRootElement());
        new NonStrictExpectations(handler){
            {
                handler.process(iq, group);
                times = 1;
            }
        };

        // Act
        dispatcher.dispatch(iq, group);

    }

    @QueryHandler(namespace = "test", node = "info")
    public static class TestIQHandler1 extends GroupIQHandler {
        @Override
        public void process(IQ packet, Group group) {

        }

        public GroupService getGroupService() {
            return this.groupService;
        }
    }

    @XHandler(namespace = "add", elementName = "member")
    public static class TestIQHandler2 extends GroupIQHandler {
        @Override
        public void process(IQ packet, Group group) {

        }

        public GroupService getGroupService() {
            return this.groupService;
        }
    }
}