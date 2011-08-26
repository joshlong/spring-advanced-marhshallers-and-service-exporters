package org.springframework.obm.avro;

import org.junit.Before;
import org.junit.Test;
import org.springframework.obm.BaseMarshallerTest;
import org.springframework.obm.avro.crm.Customer;

/**
 * @author Josh Long
 */
public class TestAvroMarshaller extends BaseMarshallerTest<Customer> {
    private Customer customer = new Customer();

    @Before
    public void before() throws Throwable {
        // the public fields-based approach is ... regrettable
        // the 1.6 version of Avro will remedy this: AVRO-784 or AVRO-839
        customer.id = 225;
        customer.firstName = "Josh";
        customer.lastName = "Long";
        customer.email = "josh@emai.com";

        AvroMarshaller<Customer> am = new AvroMarshaller<Customer>();
        setMarshaller(am);
        setUnmarshaller(am);
    }

    @Test
    public void testMarshalling() throws Throwable {
        doTestMarshalling(Customer.class, this.customer);
    }
}