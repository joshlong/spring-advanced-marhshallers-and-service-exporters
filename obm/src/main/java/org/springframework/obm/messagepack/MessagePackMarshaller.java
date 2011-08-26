package org.springframework.obm.messagepack;

import org.msgpack.MessagePack;
import org.msgpack.Template;
import org.msgpack.template.builder.BeansTemplateBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.obm.support.AbstractMarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * @author Josh Long
 */
public class MessagePackMarshaller extends AbstractMarshaller  implements InitializingBean{
    private BeansTemplateBuilder beansTemplateBuilder = new BeansTemplateBuilder();

    private boolean serializeJavaBeanProperties = true;

    private Set<Class<?>> messagePackClasses = new CopyOnWriteArraySet<Class<?>>();

    public void setSerializeJavaBeanProperties(boolean serializeJavaBeanProperties) {
        this.serializeJavaBeanProperties = serializeJavaBeanProperties;
    }

    @Override
    public void marshal(Object graph, OutputStream result) throws IOException, XmlMappingException {
        Assert.isTrue(messagePackSupports(graph.getClass()), "the class must be registered");
        MessagePack.pack( result, graph);
    }

    @Override
    public boolean supports(Class clazz) {
        return messagePackSupports(clazz);
    }

    @Override
    public Object unmarshal(Class  clazz, InputStream in ) throws IOException, XmlMappingException {
        Assert.isTrue(messagePackSupports(clazz), "the class must be registered");
        return MessagePack.unpack(in, clazz);
    }

    protected boolean messagePackSupports(Class<?> clazz) {

        if (messagePackClasses.contains(clazz)) {
            return true;
        }

        // otherwise, register it
        messagePackClasses.add(clazz);

        if (serializeJavaBeanProperties) {
            Template template = beansTemplateBuilder.buildTemplate(clazz);
            MessagePack.register(clazz, template);
        } else {
            MessagePack.register(clazz);
        }

        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
      if (!serializeJavaBeanProperties) {
            if (log.isDebugEnabled()) {
                log.debug("the 'serializeJavaBeanProperties' property has been set to false, " +
                                  "which means that all POJOs must expose public variables to properly be serialized.");
            }
        }
    }
}
