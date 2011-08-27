package org.springframework.util.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
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
    public static interface
            ServerExecutionCallback {
        void doWithServer(RestTemplate restTemplate, Server server) throws Throwable;
    }


    static Log log = LogFactory.getLog(IntegrationTestUtils.class);

    static private Map<AbstractRestServiceConfiguration, BeanFactory> beanFactoryMap = new ConcurrentHashMap<AbstractRestServiceConfiguration, BeanFactory>();

    public static boolean stopServerQuietly(Server server) throws Throwable {
        Assert.assertNotNull(server);

        if (!server.isStopped()) {
            server.stop();
        }
        while (!server.isStopped()) {
            Thread.sleep(500);
        }
        return true;
    }

    /**
     * passes classes of {@link AbstractRestServiceConfiguration} to {@link EndpointTestUtils#serve(JettyContextConfigurationCallback)}
     * and waits for the {@link Server} to start. To make sure that the we can obtain a pointer to the {@link RestTemplate} that
     * was configured in {@link org.springframework.context.annotation.Configuration configuration class}, we implant the Spring
     * context with a bean tht simply exports the {@link ApplicationContext} to a well known variable, {@link #beanFactoryMap},
     * where this code then looks it up and returns it to the client.
     *
     * @param clazz the configuration class
     * @throws Throwable should an exception be thrown
     */
    public static void startServiceAndConnect(Class<? extends AbstractRestServiceConfiguration> clazz, ServerExecutionCallback c) throws Throwable {
        DispatcherServletJettyConfigurationCallback configurationCallback = new DispatcherServletJettyConfigurationCallback(clazz);
        Server server = EndpointTestUtils.serve(configurationCallback);
        server.start();
        org.springframework.util.Assert.isTrue(beanFactoryMap.size() == 1, "there should only be one entry in this map.");
        BeanFactory beanFactory = beanFactoryMap.values().iterator().next();
        beanFactoryMap.clear();
        Assert.assertNotNull(beanFactory);
        RestTemplate restTemplate = beanFactory.getBean(RestTemplate.class);
        Assert.assertNotNull(restTemplate);
        try {
            c.doWithServer(restTemplate, server);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Throwable th) {
            if (log.isErrorEnabled()) {
                log.error("something went wrong in execution the callback ", th);
            }
        } finally {
            stopServerQuietly(server);
        }
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

