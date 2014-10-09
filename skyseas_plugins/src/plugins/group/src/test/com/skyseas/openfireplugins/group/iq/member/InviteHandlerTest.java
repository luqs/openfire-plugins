package com.skyseas.openfireplugins.group.iq.member;

import com.skyseas.openfireplugins.group.iq.IQHandlerTest;
import com.skyseas.openfireplugins.group.iq.group.MockChatUser;
import junit.framework.TestCase;
import mockit.Delegate;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;

public class InviteHandlerTest extends IQHandlerTest<InviteHandler> {

    public InviteHandlerTest() {
        super(new InviteHandler());
    }

    public void testProcess() throws Exception {
        // Arrange
        final MockChatUser user = new MockChatUser("user100", "独孤求败");
        new NonStrictExpectations(){
            {
                userManager.addUser(user.getUserName(), user.getNickname());
                result = user;
                times = 1;
            }
        };

        IQ packet = IQ("<iq from='user@skysea.com' to='100@group.skysea.com' id='v8' type='set'>\n" +
                "  <x xmlns='http://skysea.com/protocol/group#member'>\n" +
                "  \t<invite>\n" +
                "  \t\t<member username='user100' nickname='独孤求败' />\n" +
                "  \t</invite>\n" +
                "  </x>\n" +
                "</iq>");

        // Act
        handler.process(packet, group);

        // Assert
        new Verifications(){
            {
                handler.routePacket(with(new Delegate<Packet>() {
                    public void validate(Packet p) {
                        assertEquals(
                                "<iq type=\"result\" " +
                                        "id=\"v8\" " +
                                        "from=\"100@group.skysea.com\" " +
                                        "to=\"user@skysea.com\"/>",
                                p.toXML().trim());
                    }
                }));
                times = 1;
            }
        };

    }

    public void testProcess_When_Add_User_Fail() throws Exception {
        // Arrange
        new NonStrictExpectations(){
            {
                userManager.addUser("user100", "独孤求败");
                result = null;
                times = 1;
            }
        };

        IQ packet = IQ("<iq from='user@skysea.com' to='100@group.skysea.com' id='v8' type='set'>\n" +
                "  <x xmlns='http://skysea.com/protocol/group#member'>\n" +
                "  \t<invite>\n" +
                "  \t\t<member username='user100' nickname='独孤求败' />\n" +
                "  \t</invite>\n" +
                "  </x>\n" +
                "</iq>");

        // Act
        handler.process(packet, group);

        // Assert
        new Verifications(){
            {
                handler.routePacket(with(new Delegate<Packet>() {
                    public void validate(Packet p) {
                        assertEquals(
                                "<iq type=\"error\" id=\"v8\" from=\"100@group.skysea.com\" to=\"user@skysea.com\">" +
                                        "<error code=\"500\" type=\"wait\">" +
                                        "<internal-server-error xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>" +
                                        "</error>" +
                                        "</iq>",
                                p.toXML().trim());
                    }
                }));
                times = 1;
            }
        };
    }
}