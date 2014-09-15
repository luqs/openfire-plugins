package com.skyseas.openfireplugins.group.iq;

import com.skyseas.openfireplugins.group.Group;
import com.skyseas.openfireplugins.group.GroupService;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by apple on 14-9-14.
 */
final class IQDispatcher {
    private Logger LOG = LoggerFactory.getLogger(IQDispatcher.class);
    private final HashMap<StringPair, IQHandler> queryHandlers  = new HashMap<StringPair, IQHandler>();
    private final HashMap<StringPair, IQHandler> xHandlers      = new HashMap<StringPair, IQHandler>();
    private final GroupService groupService;

    public IQDispatcher(GroupService groupService) {
        assert groupService != null;
        this.groupService = groupService;
    }

    public void installHandler(Class<?> klass) {
        if(klass == null) { throw new NullPointerException("klass"); }

        if(!IQHandler.class.isAssignableFrom(klass)) {
            throw new IllegalArgumentException("class is not IQHandler.");
        }

        if(!tryAddQueryHandler(klass) && !tryAddXHandler(klass)) {
            throw new IllegalArgumentException("IQHandler lack of definition");
        }
    }

    Collection<IQHandler> getHandlers() {
        int size = queryHandlers.size() + xHandlers.size();
        ArrayList<IQHandler> handlers = new ArrayList<IQHandler>(size);
        handlers.addAll(queryHandlers.values());
        handlers.addAll(xHandlers.values());
        return handlers;
    }

    public void dispatch(IQ packet) {
        assert packet != null;

        IQHandler handler = getHandler(packet);
        if(handler != null) {
            process(handler, packet);
        } else {
            /* 回复客户端IQ不被接受 */
            replyNoAcceptable(packet);
        }
    }

    public void dispatch(IQ packet, Group group) {
        assert packet != null;
        assert group != null;

        IQHandler handler = getHandler(packet);
        if(handler != null) {
            process((GroupIQHandler)handler, packet, group);
        } else {
            /* 回复客户端IQ不被接受 */
            replyNoAcceptable(packet);
        }
    }



    private IQHandler getHandler(IQ packet) {
        Element extElement = packet.getChildElement();
        if(extElement != null) {
            String extName = extElement.getName();
            if ("query".equals(extName)) {
                return getQueryHandler(packet, extElement);
            } else if ("x".equals(extName)) {
                return getXHandler(packet, extElement);
            }
        }
        return null;
    }

    private boolean tryAddXHandler(Class<?> klass) {
        XHandler desc = klass.getAnnotation(XHandler.class);
        if(desc != null) {
            StringPair key = new StringPair(desc.namespace(), desc.elementName());
            xHandlers.put(key, createAndInitHandler(klass));
            return true;
        }
        return false;
    }

    private boolean tryAddQueryHandler(Class<?> klass) {
        QueryHandler desc = klass.getAnnotation(QueryHandler.class);
        if(desc != null) {
            StringPair key = new StringPair(desc.namespace(), desc.node());
            queryHandlers.put(key, createAndInitHandler(klass));
            return true;
        }
        return false;
    }

    protected void process(IQHandler handler, IQ packet) {
        handler.process(new IQContext(packet));
    }

    protected void process(IQHandler handler, IQ packet, Group group) {
        IQContext context = new IQContext(packet);
        context.setItem(IQContext.ITEM_GROUP, group);
        handler.process(context);
    }

    protected IQHandler createAndInitHandler(Class<?> klass) {
        IQHandler handler;
        try {
            handler = (IQHandler)klass.newInstance();
        } catch (Exception e) {
            LOG.error("创建Handler实例失败 class:"+ klass, e);
            throw new IllegalArgumentException("IQHandler class invalid.",e);
        }

        if(handler != null) {
            try {
                handler.initialize(groupService);
            }catch (Exception e) {
                LOG.error("初始化Handler失败，class:" + handler.getClass(), e);
            }
        }
        return handler;
    }

    private IQHandler getXHandler(IQ packet, Element extElement) {
        String firstElementName = null;
        if(extElement.elements().size() > 0) {
            firstElementName = ((Element)extElement.elements().get(0)).getName();
        }
        StringPair key = new StringPair(extElement.getNamespaceURI(), firstElementName);
        return xHandlers.get(key);
    }

    private IQHandler getQueryHandler(IQ packet, Element extElement) {
        StringPair key = new StringPair(extElement.getNamespaceURI(),
                extElement.attributeValue("node"));
        return queryHandlers.get(key);
    }



    private void replyNoAcceptable(IQ packet) {
        IQ replyError = IQ.createResultIQ(packet);
        replyError.setError(PacketError.Condition.not_acceptable);
        groupService.getServer().getPacketRouter().route(replyError);
    }

    /**
     * 字符串对
     */
    private final static class StringPair {
        private final String str1;
        private final String str2;
        public StringPair(String str1, String str2) {
            this.str1 = str1 == null ? "" : str1;
            this.str2 = str2 == null ? "" : str2;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) { return false;}
            if(obj instanceof StringPair) {
                StringPair other = (StringPair)obj;

                return
                        str1.equals(other.str1) &&
                        str2.equals(other.str2);
            }
            return false;
        }

        @Override
        public int hashCode(){
            int result = 1;
            result = result * 31 + str1.hashCode();
            result = result * 31 + str2.hashCode();
            return result;
        }
    }
}
