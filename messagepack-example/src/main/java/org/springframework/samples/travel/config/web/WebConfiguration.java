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
import org.springframework.samples.travel.config.services.ServicesConfiguration;
import org.springframework.samples.travel.domain.*;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

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

	private Class[] jaxbClasses = {Hotels.class, Bookings.class, Amenity.class, Booking.class, User.class, Hotel.class};

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    	// json support
		MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		mappingJacksonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
		converters.add(mappingJacksonHttpMessageConverter);

		// jaxb support
		MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter(this.marshaller());
		marshallingHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_XML));
		converters.add(marshallingHttpMessageConverter);

		// messagepack support
		MessagePackHttpMessageConverter messagePackHttpMessageConverter = new MessagePackHttpMessageConverter() ;
		converters.add(messagePackHttpMessageConverter );

	}

	@Bean
	public Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(this.jaxbClasses);
		return marshaller;
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}


}
