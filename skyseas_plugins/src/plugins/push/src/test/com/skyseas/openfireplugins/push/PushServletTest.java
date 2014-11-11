package com.skyseas.openfireplugins.push;

import com.skyseas.openfireplugins.push.PacketSender;
import com.skyseas.openfireplugins.push.PushServlet;
import junit.framework.TestCase;
import mockit.Delegate;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.xmpp.packet.Packet;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class PushServletTest extends TestCase {
    @Mocked
    HttpServletRequest request;
    @Mocked
    HttpServletResponse response;
    @Mocked
    PacketSender sender;
    @Mocked
    PrintWriter writer;
    private PushServlet servlet;

    @Override
    protected void setUp() throws Exception {
        servlet = new PushServlet(sender);
        new NonStrictExpectations() {
            {
                response.getWriter();
                result = writer;
            }
        };
    }

    public void testDoPost_When_Content_Is_Empty() throws Exception {
        // Arrange


        // Act
        servlet.doPost(request, response);

        // Assert
        new Verifications() {
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                times = 1;

                writer.write("invalid xmpp data.");
                times = 1;

                writer.close();
                times = 1;
            }
        };
    }


    public void testDoPost() throws Exception {
        // Arrange
        new NonStrictExpectations() {
            {
                request.getParameter(PushServlet.PACKET_CONTENT_PARAMETER_NAME);
                result = "<message from=\"skysea.com/event\" to=\"user@skysea.com\" id=\"v2\">\n" +
                        "  <x xmlns=\"http://skysea.com/protocol/event\">\n" +
                        "    <x xmlns=\"jabber:x:data\" type=\"result\">\n" +
                        "        <field var=\"EVENT_NAME\"> <value>activity_deleted</value> </field>\n" +
                        "        <field var=\"activity_id\"> <value>100</value> </field>\n" +
                        "    </x>\n" +
                        "  </x>\n" +
                        "</message>";
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        new Verifications() {
            {
                sender.send(with(new Delegate<Packet>() {
                    public void validate(Packet packet) {
                        assertEquals(
                                "<message from=\"skysea.com/event\" to=\"user@skysea.com\" id=\"v2\">\n" +
                                        "  <x xmlns=\"http://skysea.com/protocol/event\">\n" +
                                        "    <x xmlns=\"jabber:x:data\" type=\"result\">\n" +
                                        "        <field var=\"EVENT_NAME\"> <value>activity_deleted</value> </field>\n" +
                                        "        <field var=\"activity_id\"> <value>100</value> </field>\n" +
                                        "    </x>\n" +
                                        "  </x>\n" +
                                        "</message>",
                                packet.toXML().toString());
                    }
                }));
                times = 1;

                response.setStatus(HttpServletResponse.SC_OK);
                times = 1;
            }
        };
    }

    public void testDoPost_When_Content_Is_Invalid_Packet() throws Exception {
        // Arrange
        new NonStrictExpectations() {
            {
                request.getParameter(PushServlet.PACKET_CONTENT_PARAMETER_NAME);
                result = "<x from='skysea.com/event' to='user@skysea.com' id='v2'>\n" +
                        "  <x xmlns='http://skysea.com/protocol/event'>\n" +
                        "    <x xmlns='jabber:x:data' type='result'>\n" +
                        "        <field var='EVENT_NAME'> <value>activity_deleted</value> </field>\n" +
                        "        <field var='activity_id'> <value>100</value> </field>\n" +
                        "    </x>\n" +
                        "  </x>\n" +
                        "</x>";
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        new Verifications() {
            {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                times = 1;

                writer.write("invalid xmpp data.");
                times = 1;

                writer.close();
                times = 1;
            }
        };
    }

    public void testDoPost_When_Send_Fail() throws Exception {
        // Arrange
        new NonStrictExpectations() {
            {
                request.getParameter(PushServlet.PACKET_CONTENT_PARAMETER_NAME);
                result =  "<iq from='skysea.com/event' type='set' id='v2'>\n" +
                        "  <x xmlns='http://skysea.com/protocol/event'>\n" +
                        "    <x xmlns='jabber:x:data' type='result'>\n" +
                        "        <field var='EVENT_NAME'> <value>activity_deleted</value> </field>\n" +
                        "        <field var='activity_id'> <value>100</value> </field>\n" +
                        "    </x>\n" +
                        "  </x>\n" +
                        "</iq>";

                sender.send(withAny((Packet) null));
                result = new IllegalArgumentException();
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        new Verifications() {
            {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                times = 1;

                writer.write("invalid xmpp data.");
                times = 1;

                writer.close();
                times = 1;
            }
        };
    }


    public static class TestServletInputStream extends ServletInputStream {
        private final byte[] data;
        private int position;

        public TestServletInputStream(String content) {
            this.data = content.getBytes(Charset.forName("utf-8"));
            this.position = 0;
        }

        @Override
        public int read() throws IOException {
            if (position < data.length) {
                return data[position++] & 0xFF;
            } else {
                return -1;
            }
        }

        public int length() {
            return data.length;

        }
    }
}