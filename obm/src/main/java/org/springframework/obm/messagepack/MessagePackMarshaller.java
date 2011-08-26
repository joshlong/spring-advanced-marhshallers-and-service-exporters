/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.obm.messagepack;

import org.msgpack.MessagePack;
import org.msgpack.Template;
import org.msgpack.template.builder.BeansTemplateBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.obm.support.AbstractMarshaller;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * Implementation of the marshaler and unmarshaller contracts that delegates to MessagePack for serialization
 *
 * @author Josh Long
 */
public class MessagePackMarshaller<T> extends AbstractMarshaller<T> implements InitializingBean {

    private BeansTemplateBuilder beansTemplateBuilder = new BeansTemplateBuilder();

    private boolean serializeJavaBeanProperties = true;

    private Set<Class<?>> messagePackClasses = new CopyOnWriteArraySet<Class<?>>();

    public void setSerializeJavaBeanProperties(boolean serializeJavaBeanProperties) {
        this.serializeJavaBeanProperties = serializeJavaBeanProperties;
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

    @Override
    public void marshal(T obj, OutputStream os) throws Exception {
        Assert.isTrue(messagePackSupports(obj.getClass()), "the class must be registered");
        MessagePack.pack(os, obj);
    }

    @Override
    public boolean supports(Class<T> clazz) {
        return messagePackSupports(clazz);
    }

    @Override
    public T unmarshal(Class<T> clazz, InputStream source) throws Exception {
        Assert.isTrue(messagePackSupports(clazz), "the class must be registered");
        return MessagePack.unpack(source, clazz);
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
}
