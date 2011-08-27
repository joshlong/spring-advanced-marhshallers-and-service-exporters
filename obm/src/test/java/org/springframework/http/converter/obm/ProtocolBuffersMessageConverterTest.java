package org.springframework.http.converter.obm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.obm.support.BaseMarshallingHttpMessageConverterTest;
import org.springframework.obm.protocolbuffers.ProtocolBuffersMarshaller;
import org.springframework.obm.protocolbuffers.crm.Crm;

/**
 * Simple test of the Google Protocols marshaller with {@link org.springframework.http.converter.HttpMessageConverter}
 *
 * @author Josh Long
 */
public class ProtocolBuffersMessageConverterTest extends BaseMarshallingHttpMessageConverterTest {

    private Crm.Customer customer;

    private Log log = LogFactory.getLog(getClass());

    static MediaType MEDIA_TYPE = new MediaType("application", "x-protobuffs");

    @Before
    public void before() throws Throwable {
        customer = Crm.Customer.newBuilder()
                           .setEmail("email@email.com")
                           .setFirstName("josh")
                           .setLastName("long").build();
        ProtocolBuffersMarshaller marshaller = new ProtocolBuffersMarshaller();

        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        setMediaType(MEDIA_TYPE);
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(marshaller);
        setHttpMessageConverter(converter);
    }

    @Test
    public void testHttpReading() throws Throwable {
        doTestHttpWriting(customer.getClass(), this.customer);
    }
}

