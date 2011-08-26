package org.springframework.samples.travel.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.messagepack.MessagePackHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.samples.travel.config.CommonConfiguration;
import org.springframework.samples.travel.config.services.ServicesConfiguration;
import org.springframework.samples.travel.domain.*;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Sets up all artifacts related to the web
 */
@Configuration
@EnableWebMvc
@Import(ServicesConfiguration.class)
@ComponentScan({"org.springframework.samples.travel.rest" })
public class WebConfiguration extends WebMvcConfigurerAdapter {

	@Inject private CommonConfiguration commonConfiguration ;

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

		for(HttpMessageConverter<?> mc : commonConfiguration.messageConverters())
			converters.add(mc);

	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
