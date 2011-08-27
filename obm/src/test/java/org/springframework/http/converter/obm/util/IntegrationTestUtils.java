package org.springframework.http.converter.obm.util;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.junit.Assert;
import org.mortbay.jetty.Server;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.obm.MarshallingHttpMessageConverter;
import org.springframework.http.converter.obm.support.DebugClientHttpRequestInterceptor;
import org.springframework.obm.Marshaller;
import org.springframework.util.DispatcherServletJettyConfigurationCallback;
import org.springframework.util.EndpointTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It turns out that setting up a working server to integration test an object
 * is pretty tedious for a unit test and most of the code is invariant. so the goal
 * here is to extract the variants and make that easy to setup going forward.
 *
 * @author Josh Long
 */
public class IntegrationTestUtils {

    static private Map<AbstractRestServiceConfiguration, BeanFactory> beanFactoryMap = new ConcurrentHashMap<AbstractRestServiceConfiguration, BeanFactory>();


    public static boolean stopServerQuietly(Server server) throws Throwable {
        Assert.assertNotNull(server);

        if (!server.isStopped()) {
            server.stop();
        }
        while (!server.isStopped()) {
            Thread.sleep(500);
        }

        Thread.sleep(1000 *3);
        return true;
    }

    public static Map<RestTemplate, Server> startServiceAndConnect(Class<? extends AbstractRestServiceConfiguration> clazz) throws Throwable {
        DispatcherServletJettyConfigurationCallback configurationCallback = new DispatcherServletJettyConfigurationCallback(clazz);
        Server server = EndpointTestUtils.serve(configurationCallback);
        server.start();
        BeanFactory beanFactory = beanFactoryMap.values().iterator().next();
        Assert.assertNotNull(beanFactory);
        RestTemplate restTemplate = beanFactory.getBean(RestTemplate.class);
        Assert.assertNotNull(restTemplate);

        Map<RestTemplate, Server> tuple = new HashMap<RestTemplate, Server>();
        tuple.put(restTemplate, server);
        return tuple;
    }


    /**
     * Abstract template class. Clients may extend this class and then fill out the
     * definitions for the salient parts.
     *
     * @author Josh Long
     */
    public abstract static class AbstractRestServiceConfiguration extends WebMvcConfigurerAdapter {

        abstract public Marshaller getMarshaller();

        abstract public MediaType getMediaType();

        @Bean // hackety hackety
        public ApplicationContextAware applicationContextExporter() {
            return new ApplicationContextAware() {
                @Override
                public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
                    beanFactoryMap.put(AbstractRestServiceConfiguration.this, applicationContext);
                }
            };
        }

        @Bean
        public Marshaller marshaller() {
            return getMarshaller();
        }

        @Bean
        public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter() {
            MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
            mappingJacksonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
            return mappingJacksonHttpMessageConverter;
        }

        @Bean
        public HttpMessageConverter messageConverter() {
            MarshallingHttpMessageConverter mc = new MarshallingHttpMessageConverter(this.marshaller());
            mc.setSupportedMediaTypes(Arrays.asList(getMediaType()));
            return mc;
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(messageConverter());
            converters.add(mappingJacksonHttpMessageConverter());
        }

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(messageConverter()));
            restTemplate.setInterceptors(new ClientHttpRequestInterceptor[]{new DebugClientHttpRequestInterceptor(getMediaType())});
            return restTemplate;
        }

    }


}

