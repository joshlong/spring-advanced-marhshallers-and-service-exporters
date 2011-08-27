package org.springframework.http.converter.obm.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.util.internal.ConcurrentHashMap;
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

    public static RestTemplate exposeRestfulService(Class<? extends AbstractRestServiceConfiguration> clazz) throws Throwable {
        DispatcherServletJettyConfigurationCallback configurationCallback = new DispatcherServletJettyConfigurationCallback(clazz);
        Server server = EndpointTestUtils.serve(configurationCallback);
        server.start();
        BeanFactory beanFactory = beanFactoryMap.values().iterator().next();
        return beanFactory.getBean(RestTemplate.class);
    }

    // hackety hackety
    // to keep our end of the bargain we need a correctly configured RestTemplate.
    // so we let Spring configure it and then simply 'export' it so i can get access it
    public static class BeanFactoryExporter implements ApplicationContextAware {
        private AbstractRestServiceConfiguration abstractRestServiceConfiguration;

        public BeanFactoryExporter(AbstractRestServiceConfiguration abstractRestServiceConfiguration) {
            this.abstractRestServiceConfiguration = abstractRestServiceConfiguration;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            beanFactoryMap.put(this.abstractRestServiceConfiguration, applicationContext);
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

        private Log log = LogFactory.getLog(getClass());

        @Bean
        public BeanFactoryExporter rtExporter() {
            return new BeanFactoryExporter(this);
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

