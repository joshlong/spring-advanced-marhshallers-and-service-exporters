package org.springframework.obm.protocolbuffers;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.obm.support.AbstractMarshaller;
import org.springframework.oxm.XmlMappingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Protocol buffers is one of the most mature serialization libraries out there at the moment.
 *
 * @author Josh Long
 */
public class ProtocolBuffersMarshaller <T>  extends AbstractMarshaller<T> implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public boolean supports(Class<T> clazz) {
        return false;
    }

    @Override
    public T unmarshal(Class<T> clazz, InputStream source) throws IOException, XmlMappingException {
        return null;
    }

    @Override
    public void marshal(T obj, OutputStream os) throws IOException, XmlMappingException {
    }
}
