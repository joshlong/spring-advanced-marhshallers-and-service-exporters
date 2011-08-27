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
import org.springframework.obm.messagepack.Cat;
import org.springframework.obm.messagepack.MessagePackMarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.util.http.RestIntegrationTestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Josh Long
 */
public class MessagePackHttpMessageConverterTest extends BaseMarshallingHttpMessageConverterTest {

    static MediaType MEDIA_TYPE = new MediaType("application", "x-msgpack");
    private Log log = LogFactory.getLog(getClass());
    private Cat cat = new Cat();

    @Before
    public void before() throws Throwable {
        cat.setId(4);
        cat.setName("Felix");

        MessagePackMarshaller marshaller = new MessagePackMarshaller();
        marshaller.afterPropertiesSet();

        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        setMediaType(MEDIA_TYPE);
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(marshaller);
        setHttpMessageConverter(converter);
    }

    @Test
    public void testHttpReading() throws Throwable {
        doTestHttpWriting(cat.getClass(), cat);
    }


    @Test
    public void testSimpleIntegration() throws Throwable {
        RestIntegrationTestUtils.startServiceAndConnect(MyService.class, new RestIntegrationTestUtils.ServerExecutionCallback() {
            @Override
            public void doWithServer(RestTemplate clientRestTemplate, Server server) throws Throwable {

                Assert.assertNotNull(clientRestTemplate);

                int id = 344;
                Map<String, Object> mapOfVars = new HashMap<String, Object>();
                mapOfVars.put("cat", id);

                Cat customer = clientRestTemplate.getForEntity("http://localhost:8080/ws/cats/{cat}", Cat.class, mapOfVars).getBody();
                Assert.assertTrue(customer.getId() == id);
                Assert.assertNotNull(customer.getName());

                if (log.isDebugEnabled()) {
                    log.debug("response payload: " + ToStringBuilder.reflectionToString(customer));
                }

            }
        });

    }

    @Configuration
    @EnableWebMvc
    static public class MyService extends RestIntegrationTestUtils.AbstractRestServiceConfiguration {
        @Bean
        public CatController controller() {
            return new CatController();
        }

        @Override
        public Marshaller getMarshaller() {
            return new MessagePackMarshaller();
        }

        @Override
        public MediaType getMediaType() {
            return MEDIA_TYPE;
        }
    }


    @Controller
    @RequestMapping(value = "/ws/")
    public static class CatController {
        @RequestMapping(value = "/cats/{id}", method = RequestMethod.GET)
        @ResponseBody
        public Cat customer(@PathVariable("id") int id) {
            return new Cat(Math.random() > .5 ? "Felix" : "Garfield", id);
        }
    }
}

