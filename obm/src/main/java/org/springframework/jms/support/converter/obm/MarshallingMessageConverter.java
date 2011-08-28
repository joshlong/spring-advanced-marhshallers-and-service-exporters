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
package org.springframework.jms.support.converter.obm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.obm.Marshaller;
import org.springframework.obm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.util.Assert;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Because the {@link Marshaller} interface requires a class to which to transform the bytes,
 * you must specify a {@link #payloadClass payload class} when using this {@link MessageConverter}.
 * This is inconvenient, however, in most typical configurations a {@link org.springframework.jms.core.JmsTemplate}
 * will be tied to the workload of one {@link javax.jms.Destination}, and typically a {@link javax.jms.Destination}
 * will only transport one type of payload.
 *
 * @author Josh Long
 * @see org.springframework.jms.support.converter.MarshallingMessageConverter
 */
public class MarshallingMessageConverter implements MessageConverter, InitializingBean {

    private Log log = LogFactory.getLog(getClass());

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Class<?> payloadClass;

    public MarshallingMessageConverter() {
    }

    public MarshallingMessageConverter(Class<?> aClass, Marshaller marshaller1) {
        this();
        try {
            setMarshaller(marshaller1);
            setPayloadClass(aClass);
            if (marshaller1 instanceof Unmarshaller) {
                setUnmarshaller((Unmarshaller) marshaller1);
            }
            afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MarshallingMessageConverter(Class<?> cl, Marshaller marshaller, Unmarshaller unmarshaller) {
        this();
        try {
            setMarshaller(marshaller);
            setUnmarshaller(unmarshaller);
            setPayloadClass(cl);
            afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setPayloadClass(Class<?> payloadClass) {
        this.payloadClass = payloadClass;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    @Override
    public javax.jms.Message toMessage(Object object, javax.jms.Session session) throws JMSException, MessageConversionException {
        try {
            javax.jms.Message msg = marshalToBytesMessage(object, session, this.marshaller);
            if (log.isDebugEnabled()) {
                log.debug("converted " + object + " to a message.");
            }
            return msg;
        } catch (Exception ex) {
            throw new MessageConversionException("Could not marshal [" + object + "]", ex);
        }
    }

    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        try {
            Assert.isInstanceOf(BytesMessage.class, message);
            if (message instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) message;
                return unmarshalFromBytesMessage(this.payloadClass, bytesMessage, this.unmarshaller);
            }

        } catch (IOException ex) {
            throw new MessageConversionException("Could not access message content: " + message, ex);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.payloadClass, "the payload class can't be null");
        Assert.notNull(this.marshaller, "Property 'thriftMarshaller' is required");
        Assert.notNull(this.unmarshaller, "Property 'unmarshaller' is required");
    }

    protected Object unmarshalFromBytesMessage(Class clzz, BytesMessage message, org.springframework.obm.Unmarshaller unmarshaller) throws JMSException, IOException, XmlMappingException {
        try {
            byte[] bytes = new byte[(int) message.getBodyLength()];
            message.readBytes(bytes);
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Object result = unmarshaller.unmarshal(clzz, bis);
            Assert.notNull(result, "the result from the queue is null");
            if (log.isDebugEnabled()) {
                log.debug("received: " + result);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected BytesMessage marshalToBytesMessage(Object object, Session session, org.springframework.obm.Marshaller marshaller) throws JMSException, IOException, XmlMappingException {
        Assert.notNull(object);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BytesMessage message;
        try {

            marshaller.marshal(object, bos);

            message = session.createBytesMessage();
            message.writeBytes(bos.toByteArray());

            if (log.isDebugEnabled()) {
                log.debug("sent:" + object);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return message;
    }

}
