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
import org.springframework.http.converter.obm.util.IntegrationTestUtils;
import org.springframework.obm.Marshaller;
import org.springframework.obm.thrift.ThriftCrmService;
import org.springframework.obm.thrift.ThriftMarshaller;
import org.springframework.obm.thrift.crm.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Josh Long
 */
public class ThriftHttpMessageConverterTest extends BaseMarshallingHttpMessageConverterTest {

    private Log log = LogFactory.getLog(getClass());

    private Customer customer = new Customer("Josh", "Long", "josh@email.com", 242);

    static final public MediaType MEDIA_TYPE = new MediaType("application", "x-thrift");

    @Before
    public void before() throws Throwable {
        ThriftMarshaller marshaller = new ThriftMarshaller();
        marshaller.afterPropertiesSet();
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        setMediaType(MEDIA_TYPE);
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(marshaller);
        converter.setSupportedMediaTypes(Arrays.asList(MEDIA_TYPE));
        setHttpMessageConverter(converter);
    }

    @Test
    public void testHttpReading() throws Throwable {
        doTestHttpWriting(customer.getClass(), customer);
    }

    @Test
    public void testSimpleIntegration() throws Throwable {
        Map<RestTemplate, Server> tupleOfClientAndServer = IntegrationTestUtils.startServiceAndConnect(MyService.class);
        RestTemplate clientRestTemplate = tupleOfClientAndServer.keySet().iterator().next();
        Server server = tupleOfClientAndServer.values().iterator().next();

        Assert.assertNotNull(clientRestTemplate);

        Map<String, Object> mapOfVars = new HashMap<String, Object>();
        mapOfVars.put("customerId", 3);

        Customer customer = clientRestTemplate.getForEntity("http://localhost:8080/ws/customers/{customerId}", Customer.class, mapOfVars).getBody();
        Assert.assertNotNull(customer.getFirstName());
        Assert.assertNotNull(customer.getLastName());
        Assert.assertNotNull(customer.getEmail());

        if (log.isDebugEnabled()) {
            log.debug("response payload: " + ToStringBuilder.reflectionToString(customer));
        }

       IntegrationTestUtils.stopServerQuietly(server ) ;
    }

    @Configuration
    @EnableWebMvc
    static public class MyService extends IntegrationTestUtils.AbstractRestServiceConfiguration {
        @Bean
        public CrmRestController controller() {
            return new CrmRestController();
        }

        @Bean
        public ThriftCrmService crmService() {
            return new ThriftCrmService();
        }

        @Override
        public Marshaller getMarshaller() {
            return new ThriftMarshaller();
        }

        @Override
        public MediaType getMediaType() {
            return MEDIA_TYPE;
        }
    }


    @Controller
    @RequestMapping(value = "/ws/")
    public static class CrmRestController {

        @Inject private ThriftCrmService crmService;

        @RequestMapping(value = "/customers/{id}", method = RequestMethod.GET)
        @ResponseBody
        public Customer customer(@PathVariable("id") int id) {
            return crmService.getCustomerById(id);
        }
    }
}

