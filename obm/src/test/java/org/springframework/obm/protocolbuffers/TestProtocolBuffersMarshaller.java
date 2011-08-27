package org.springframework.obm.protocolbuffers;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.obm.BaseMarshallerTest;
import org.springframework.obm.protocolbuffers.crm.Crm;

/**
 * @author Josh Long
 */
public class TestProtocolBuffersMarshaller extends BaseMarshallerTest<Crm.Customer> {

    private Crm.Customer customer;

    @Before
    public void before() throws Throwable {

        customer = Crm.Customer.newBuilder()
                           .setEmail("email@email.com")
                           .setFirstName("josh")
                           .setLastName("long").build();

        Assert.assertNotNull(customer);

        ProtocolBuffersMarshaller protocolBuffersMarshaller = new ProtocolBuffersMarshaller();

        setMarshaller(protocolBuffersMarshaller);
        setUnmarshaller(protocolBuffersMarshaller);
    }

    @Test
    public void testMarshalling() throws Throwable {
        doTestMarshalling(Crm.Customer.class, this.customer);
    }
}
