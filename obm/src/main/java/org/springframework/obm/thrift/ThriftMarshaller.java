package org.springframework.obm.thrift;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.obm.support.AbstractMarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * implementation of {@link org.springframework.obm.Marshaller} and {@link org.springframework.obm.Unmarshaller}
 * that supports Apache Thrift (http://thrift.apache.org/).
 *
 * @author Josh Long
 */
public class ThriftMarshaller extends AbstractMarshaller implements InitializingBean {
    private TSerializer serializer;

    public void setDeserializer(TDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    public void setSerializer(TSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public boolean supports(Class clazz) {
        return true ;
    }

    private TDeserializer deserializer;

    @Override
    public Object unmarshal(Class clazz, InputStream source) throws IOException, XmlMappingException {
        Assert.isTrue(TBase.class.isAssignableFrom(clazz), "the request payload must be a subclas of TBase");
        Class<? extends TBase> tBaseClass = (Class<? extends TBase>) clazz;
        try {
            TBase obj = tBaseClass.newInstance();

            byte[] bytes = FileCopyUtils.copyToByteArray(source);

            this.deserializer.deserialize(obj, bytes);

            return obj;
        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error("something occurred when trying to TDeserializer#deserialize() the incoming request", e);
            }
            throw new RuntimeException(e);
        }
    }



    @Override
    public void marshal(Object o, OutputStream result) throws IOException, XmlMappingException {
        Assert.isInstanceOf(TBase.class, o);
        try {
            TBase tBaseOut = (TBase) o;
            byte[] bytesForObj = this.serializer.serialize(tBaseOut);
            FileCopyUtils.copy(bytesForObj, result);
        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error("something occurred when trying to TSerializer#serialize() the response", e);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (serializer == null) {
            this.serializer = new TSerializer();
        }

        if (deserializer == null) {
            this.deserializer = new TDeserializer();
        }
    }
}

