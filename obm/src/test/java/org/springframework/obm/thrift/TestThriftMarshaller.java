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

