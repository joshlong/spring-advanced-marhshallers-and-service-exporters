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
import org.springframework.util.IntegrationTestUtils;
import org.springframework.obm.Marshaller;
import org.springframework.obm.protocolbuffers.ProtocolBuffersMarshaller;
import org.springframework.obm.protocolbuffers.crm.Crm;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Simple test of the Google Protocols marshaller with {@link org.springframework.http.converter.HttpMessageConverter}
 *
 * @author Josh Long
 */
public class ProtocolBuffersMessageConverterTest extends BaseMarshallingHttpMessageConverterTest {

    private Crm.Customer customer;

    private Log log = LogFactory.getLog(getClass());

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

    @Test
    public void testSimpleIntegration() throws Throwable {

        Map<RestTemplate,Server> tupleOfClientAndServer = IntegrationTestUtils.startServiceAndConnect(MyService.class);
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

      IntegrationTestUtils.stopServerQuietly(server) ;
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

        @Inject
        private ThriftCrmService crmService;

        @RequestMapping(value = "/customers/{id}", method = RequestMethod.GET)
        @ResponseBody
        public Customer customer(@PathVariable("id") int id) {
            return crmService.getCustomerById(id);
        }
    }


}

