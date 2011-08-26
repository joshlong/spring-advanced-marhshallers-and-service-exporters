package org.springframework.obm.thrift;


import org.junit.Before;
import org.junit.Test;
import org.springframework.obm.BaseMarshallerTest;
import org.springframework.obm.thrift.crm.Customer;

/**
 * @author Josh Long
 */
public class TestThriftMarshaller extends BaseMarshallerTest<Customer> {

    private Customer customer = new Customer("Josh", "Long", "josh@email.com", 242);

    @Before
    public void before() throws Throwable {

        ThriftMarshaller<Customer> marshaller = new ThriftMarshaller<Customer>();
        marshaller.afterPropertiesSet();

        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
    }

    @Test
    public void testMarshaller() throws Throwable {
        doTestMarshalling(Customer.class, customer);
    }
}

