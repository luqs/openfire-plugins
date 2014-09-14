package com.skyseas.openfireplugins.group.iq.group;

import com.skyseas.openfireplugins.group.GroupInfo;
import com.skyseas.openfireplugins.group.NoPermissionException;
import com.skyseas.openfireplugins.group.iq.IQHandlerTest;
import mockit.Delegate;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.xmpp.packet.IQ;

public class UpdateHandlerTest extends IQHandlerTest<UpdateHandler> {

    private IQ packet;

    public UpdateHandlerTest() {
        super(new UpdateHandler());
    }

    public void setUp() {
        super.setUp();
        packet = IQ("<iq from='user@skysea.com' to='100@group.skysea.com' id='v1' type='set'>\n" +
                        "\t<x xmlns='http://skysea.com/protocol/group'>\n" +
                        "\t\t<x xmlns='jabber:x:data' type='submit'>\n" +
                        "\t\t    <field var='name'  type='text-single'>\n" +
                        "\t\t      <value>圈子名称</value>\n" +
                        "\t\t    </field>\n" +
                        "\t\t    <field var='category' type='list-single'>\n" +
                        "\t\t      <value>56</value>\n" +
                        "\t\t    </field>\n" +
                        "\t\t    <field var='subject' type='text-single'>\n" +
                        "\t\t      <value>圈子主题</value>\n" +
                        "\t\t    </field>\n" +
                        "\t\t    <field var='description' type='text-multi'>\n" +
                        "\t\t      <value>圈子描述</value>\n" +
                        "\t\t    </field>\n" +
                        "\t\t    <field var='logo' type='text-single'>\n" +
                        "\t\t      <value>logo</value>\n" +
                        "\t\t    </field>\n" +
                        "\t\t    <field var='openness' type='list-single'>\n" +
                        "\t\t      <value>AFFIRM_REQUIRED</value>\n" +
                        "\t\t    </field>\n" +
                        "\t  \t</x>\n" +
                        "  \t</x>\n" +
                        "</iq>");
    }

    public void testProcess() throws Exception {
        //Arrange

        // Act
        handler.process(packet, group);

        // Assert
        new Verifications(){
            {
                group.updateGroupInfo(with(new Delegate<GroupInfo>() {
                    public void validate(GroupInfo groupInfo) {
                        //assertEquals(0, groupInfo.getId());
                        assertEquals("圈子名称", groupInfo.getName());
                        assertEquals("圈子描述", groupInfo.getDescription());
                        assertEquals(GroupInfo.OpennessType.AFFIRM_REQUIRED, groupInfo.getOpennessType());
                        assertEquals("圈子主题", groupInfo.getSubject());
                        assertEquals(56, groupInfo.getCategory());
                        assertEquals("logo", groupInfo.getLogo());
                    }
                }), packet.getFrom());

                packetRouter.route(with(new Delegate<IQ>() {
                    public void validate(IQ packet) {
                        String expectXml = "<iq type=\"result\" id=\"v1\" from=\"100@group.skysea.com\" to=\"user@skysea.com\"/>";
                        assertEquals(expectXml, packet.toString().trim());
                    }
                }));
            }
        };
    }

    public void testProcess_When_Operator_No_Permission() throws Exception{
        // Arrange
        new NonStrictExpectations(){
            {
                group.updateGroupInfo(withAny((GroupInfo)null), packet.getFrom());
                result = new NoPermissionException();
            }
        };

        // Act
        handler.process(packet, group);

        // Assert
        new Verifications(){
            {
                packetRouter.route(with(new Delegate<IQ>() {
                    public void validate(IQ packet) {
                        String expectXml =
                                "<iq type=\"error\" id=\"v1\" from=\"100@group.skysea.com\" to=\"user@skysea.com\">\n" +
                                "  <error code=\"401\" type=\"auth\">\n" +
                                "    <not-authorized xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>\n" +
                                "  </error>\n" +
                                "</iq>";
                        assertEquals(expectXml, packet.toString().trim());
                    }
                }));
                times = 1;
            }
        };

    }
}