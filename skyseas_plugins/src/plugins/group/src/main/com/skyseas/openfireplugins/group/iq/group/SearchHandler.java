package com.skyseas.openfireplugins.group.iq.group;

import com.skyseas.openfireplugins.group.*;
import com.skyseas.openfireplugins.group.iq.*;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import java.util.List;

/**
 * 圈子搜索处理程序。
 * Created by apple on 14-9-14.
 */
@QueryHandler(namespace = SearchHandler.SEARCH_NAMESPACE)
class SearchHandler extends ServiceIQHandler {
    public final static String SEARCH_NAMESPACE = "jabber:iq:search";

    @Override
    public void process(IQ packet) {
        QueryInfo queryInfo = new QueryInfo(packet.getChildElement());
        Paging<GroupInfo> paging;
        try {
            /* 分页查询圈子数据 */
            paging = groupManager.search(
                    queryInfo.getQueryObject(),
                    queryInfo.getIndex(),
                    queryInfo.getMax());
        } catch (Exception e) {
            handleException(e, "搜索圈子失败");
            replyError(packet, PacketError.Condition.internal_server_error);
            return;
        }

        /* 构建分页数据包 */
        PagingPacket pagingPacket = new PagingPacket(
                SEARCH_NAMESPACE,
                paging,
                new GroupSummaryProcessDelegate(groupService.getServiceDomain()));

        IQ reply = IQ.createResultIQ(packet);
        reply.setChildElement(pagingPacket.getElement());
        routePacket(reply);
    }


    /**
     * 分页的扩展数据包。
     */
    static class PagingPacket<T> extends DataListPacket<T> {

        private final Paging<T> pagingData;

        public PagingPacket(String namespace,
                            Paging<T> pagingData,
                            DataItemProcessDelegate<T> dataItemProcessDelegate) {

            super(namespace, pagingData.getItems(), dataItemProcessDelegate);
            this.pagingData = pagingData;
            this.initialize();
        }

        private void initialize() {

            RSMPacketExtension pagingInfo = new RSMPacketExtension();
            pagingInfo.setCount(pagingData.getCount());

            if(pagingData.getCount() >0){
                List<T> dataItems = pagingData.getItems();

                if(dataItems.size() > 0) {
                    pagingInfo.setFirstValue(delegate.getPrimaryProperty(
                            dataItems.get(0)), pagingData.getOffset());

                    pagingInfo.setLastValue(delegate.getPrimaryProperty(
                            dataItems.get(dataItems.size() - 1)));
                }
            }

            this.element.add(pagingInfo.getElement());
        }
    }

    /**
     * 圈子查询信息
     */
    public static class QueryInfo extends DataFormModelBase {
        private RSMPacketExtension rsm;

        public QueryInfo (Element element) {
            super(element);
        }

        public GroupQueryObject getQueryObject() {
            GroupQueryObject query = new GroupQueryObject();
            query.setName(getFieldValue("name"));
            query.setGroupId(getIntegerFieldValue("id", 0));
            query.setCategory(getIntegerFieldValue("category", 0));
            return query;
        }

        public int getIndex() {
            return getRSM().getIndex(0);
        }

        public int getMax() {
            return getRSM().getMax(0);
        }

        public RSMPacketExtension getRSM() {
            if(rsm == null) {
                Element ele = element.element(RSMPacketExtension.Q_NAME);
                rsm = ele != null ? new RSMPacketExtension(ele) : new RSMPacketExtension();
            }
            return rsm;
        }

    }
}
