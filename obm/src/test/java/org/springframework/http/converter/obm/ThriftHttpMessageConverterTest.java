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
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.obm.support.DebugClientHttpRequestInterceptor;
import org.springframework.http.converter.obm.support.DebuggingResponseExtractor;
import org.springframework.obm.thrift.ThriftCrmService;
import org.springframework.obm.thrift.ThriftMarshaller;
import org.springframework.obm.thrift.crm.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.util.DispatcherServletJettyConfigurationCallback;
import org.springframework.util.EndpointTestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    // this is more of a mock test
    @Test
    public void testHttpReading() throws Throwable {
        doTestHttpWriting(customer.getClass(), customer);
    }

    @Test
    public void testIntegrationWithRest() throws Throwable {
        DispatcherServletJettyConfigurationCallback callback = new DispatcherServletJettyConfigurationCallback(RestConfiguration.class);
        Server server = EndpointTestUtils.serve(callback);
        server.start();


        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ClientConfiguration.class);
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

        String url = buildServiceUrl("ws/customers/{customerId}");

        Map<String, Object> mapOfVars = new HashMap<String, Object>();
        mapOfVars.put("customerId", 3);

        Customer customer = restTemplate.getForEntity(url, Customer.class, mapOfVars).getBody();
        log.info("response payload: " + ToStringBuilder.reflectionToString(customer));
        restTemplate.execute(url, HttpMethod.GET, null, new DebuggingResponseExtractor(), mapOfVars);
    }

    private String buildServiceUrl(String prefix) {
        return "http://localhost:8080/" + (prefix.startsWith("/") ? "" : "/") + prefix;
    }

    @Configuration
    public static class CommonConfiguration {

        @Bean
        public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter() {
            MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
            mappingJacksonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
            return mappingJacksonHttpMessageConverter;
        }

        @Bean
        public ThriftMarshaller thriftMarshaller() {
            return new ThriftMarshaller();
        }

        @Bean
        public MarshallingHttpMessageConverter messagePackHttpMessageConverter() {
            MarshallingHttpMessageConverter mc = new MarshallingHttpMessageConverter(thriftMarshaller());
            mc.setSupportedMediaTypes(Arrays.asList(ThriftHttpMessageConverterTest.MEDIA_TYPE));
            return mc;
        }

        public List<HttpMessageConverter<?>> messageConverters() {
            return Arrays.<HttpMessageConverter<?>>asList(mappingJacksonHttpMessageConverter(), messagePackHttpMessageConverter());
        }
    }

    @Configuration
    @Import(CommonConfiguration.class)
    public static class ClientConfiguration {

        @Inject private CommonConfiguration commonConfiguration;

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(commonConfiguration.messagePackHttpMessageConverter()));
            restTemplate.setInterceptors(new ClientHttpRequestInterceptor[]{new DebugClientHttpRequestInterceptor(ThriftHttpMessageConverterTest.MEDIA_TYPE)});
            return restTemplate;
        }
    }

    @Configuration
    @Import(CommonConfiguration.class)
    @EnableWebMvc
    public static class RestConfiguration extends WebMvcConfigurerAdapter {

        @Inject private CommonConfiguration commonConfiguration;

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            for (HttpMessageConverter<?> mc : commonConfiguration.messageConverters()) {
                converters.add(mc);
            }
        }

        @Bean
        public ThriftCrmService crmService() {
            return new ThriftCrmService();
        }

        @Bean
        public CrmRestController crmController() {
            return new CrmRestController();
        }

    }

    @Controller
    @RequestMapping(value = "/ws/")
    public static class CrmRestController {

        @Inject
        private ThriftCrmService crmService;

        @RequestMapping(value = "/customers/{id}", method = RequestMethod.GET)
        @ResponseBody
        public Customer customer(@PathVariable("id") int id) {
            return crmService.getCustomerById(id);
        }
    }
}
