package org.springframework.http.converter.obm;

import org.apache.commons.lang.builder.ToStringBuilder;
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
import org.springframework.obm.protocolbuffers.ProtocolBuffersMarshaller;
import org.springframework.obm.protocolbuffers.crm.Crm;
import org.springframework.obm.thrift.ThriftCrmService;
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
 * Simple test of the Google Protocols marshaller with {@link org.springframework.http.converter.HttpMessageConverter}
 *
 * @author Josh Long
 */
public class ProtocolBuffersMessageConverterTest extends BaseMarshallingHttpMessageConverterTest {

    private Crm.Customer customer;

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

   /*
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
    */

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

    // cleint side
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

    // integration test
    @Configuration
    public static class CommonConfiguration {

        @Bean
        public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter() {
            MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
            mappingJacksonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
            return mappingJacksonHttpMessageConverter;
        }

        @Bean
        public ProtocolBuffersMarshaller thriftMarshaller() {
            return new ProtocolBuffersMarshaller();
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
    } @Configuration
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

}

