package org.springframework.jms.support.converter.obm;

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
 * @author Josh Long
 * @see org.springframework.jms.support.converter.MarshallingMessageConverter
 */
public class MarshallingMessageConverter implements MessageConverter, InitializingBean {

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Class<?> payloadClass;

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
            return marshalToBytesMessage(object, session, this.marshaller);
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
        Assert.notNull(this.marshaller, "Property 'marshaller' is required");
        Assert.notNull(this.unmarshaller, "Property 'unmarshaller' is required");
    }


    protected Object unmarshalFromBytesMessage(Class clzz, BytesMessage message, org.springframework.obm.Unmarshaller unmarshaller) throws JMSException, IOException, XmlMappingException {
        byte[] bytes = new byte[(int) message.getBodyLength()];
        message.readBytes(bytes);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        return unmarshaller.unmarshal(clzz, bis);
    }

    protected BytesMessage marshalToBytesMessage(Object object, Session session, org.springframework.obm.Marshaller marshaller) throws JMSException, IOException, XmlMappingException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(object, bos);
        BytesMessage message = session.createBytesMessage();
        message.writeBytes(bos.toByteArray());
        return message;
    }

}
