package org.springframework.obm.snappy;

import org.junit.Before;
import org.junit.Test;
import org.springframework.obm.BaseMarshallerTest;
import org.springframework.obm.thrift.ThriftMarshaller;
import org.springframework.obm.thrift.crm.Customer;

/**
 *
 * Snappy is a compression Marshaller that wraps other Marshallers.
 *
 * @author Josh Long
 */
public class TestSnappyMarshaller      extends BaseMarshallerTest<Customer> {

    private Customer customer = new Customer("Josh", "Long", "josh@email.com", 242);

    @Before
    public void before() throws Throwable {

        ThriftMarshaller<Customer> marshaller = new ThriftMarshaller<Customer>();
        marshaller.afterPropertiesSet();

        SnappyMarshaller snappyMarshaller = new SnappyMarshaller( marshaller);

        setMarshaller(snappyMarshaller);
        setUnmarshaller(snappyMarshaller);
    }

    @Test
    public void testMarshaller() throws Throwable {
        doTestMarshalling(Customer.class, customer);
    }
}

