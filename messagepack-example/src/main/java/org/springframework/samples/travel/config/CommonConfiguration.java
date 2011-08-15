package org.springframework.samples.travel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.messagepack.MessagePackHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.samples.travel.domain.*;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CommonConfiguration {

	private Class[] jaxbClasses = {Hotels.class, Bookings.class, Amenity.class, Booking.class, User.class, Hotel.class};

	@Bean
	public MarshallingHttpMessageConverter marshallingHttpMessageConverter() {
		MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter(this.marshaller());
		marshallingHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_XML));
		return marshallingHttpMessageConverter;
	}

	@Bean
	public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter() {
		MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		mappingJacksonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
		return mappingJacksonHttpMessageConverter;
	}

	@Bean
	public MessagePackHttpMessageConverter messagePackHttpMessageConverter() {
		return new MessagePackHttpMessageConverter();
	}

	@Bean
	public Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(this.jaxbClasses);
		return marshaller;
	}


	public List<HttpMessageConverter<?>> messageConverters(){
		return Arrays.<HttpMessageConverter<?>>asList(
				  mappingJacksonHttpMessageConverter(),
				  marshallingHttpMessageConverter(),
				  messagePackHttpMessageConverter()
			);
	}

}
