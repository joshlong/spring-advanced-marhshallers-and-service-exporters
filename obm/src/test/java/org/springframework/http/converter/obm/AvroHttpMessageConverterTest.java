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
package org.springframework.http.converter.obm;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.obm.support.BaseMarshallingHttpMessageConverterTest;
import org.springframework.obm.Marshaller;
import org.springframework.obm.avro.AvroMarshaller;
import org.springframework.obm.avro.crm.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.util.http.IntegrationTestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.Map;

/**
 * test the {@link org.springframework.http.converter.HttpMessageConverter avro http converter}
 *
 * @author Josh Long
 */
public class AvroHttpMessageConverterTest extends BaseMarshallingHttpMessageConverterTest {
    static MediaType MEDIA_TYPE = new MediaType("avro", "binary");

    private Log log = LogFactory.getLog(getClass());

    private Customer customer = new Customer();

    @Before
    public void before() throws Throwable {
        customer.id = 225;
        customer.firstName = "Josh";
        customer.lastName = "Long";
        customer.email = "josh@emai.com";

        AvroMarshaller marshaller = new AvroMarshaller();

        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        setMediaType(MEDIA_TYPE);
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(marshaller);
        setHttpMessageConverter(converter);
    }

    @Test
    public void testAvroMarshaller() throws Throwable {
        doTestHttpWriting(customer.getClass(), customer);
    }

    static private String fn = "george", ln = "harrison", email = "geore@email.com";

    @Test
    public void testSimpleIntegration() throws Throwable {
        IntegrationTestUtils.startServiceAndConnect(MyService.class, new IntegrationTestUtils.ServerExecutionCallback() {
            @Override
            public void doWithServer(RestTemplate restTemplate, Server server) throws Throwable {
                Assert.assertNotNull(restTemplate);

                int id = 344;
                Map<String, Object> mapOfVars = new HashMap<String, Object>();
                mapOfVars.put("cid", id);

                Customer customer = restTemplate.getForEntity("http://localhost:8080/ws/customers/{cid}", Customer.class, mapOfVars).getBody();
                Assert.assertTrue(customer.id == id);
                Assert.assertTrue(customer.firstName.toString().equals(fn));
                Assert.assertTrue(customer.lastName.toString().equals(ln));
                Assert.assertTrue(customer.email.toString().equals(email));

                if (log.isDebugEnabled()) {
                    log.debug("response payload: " + ToStringBuilder.reflectionToString(customer));
                }

            }
        });

    }

    @Configuration
    @EnableWebMvc
    static public class MyService extends IntegrationTestUtils.AbstractRestServiceConfiguration {
        @Bean
        public CustomerController controller() {
            return new CustomerController();
        }

        @Override
        public Marshaller getMarshaller() {
            return new AvroMarshaller();
        }

        @Override
        public MediaType getMediaType() {
            return MEDIA_TYPE;
        }
    }


    @Controller
    @RequestMapping(value = "/ws/")
    public static class CustomerController {
        @RequestMapping(value = "/customers/{id}", method = RequestMethod.GET)
        @ResponseBody
        public Customer customer(@PathVariable("id") int id) {

            Customer avroCustomer = new Customer();
            avroCustomer.id = id;
            avroCustomer.firstName = fn;
            avroCustomer.lastName = ln;
            avroCustomer.email = email;
            return avroCustomer;
        }
    }

}