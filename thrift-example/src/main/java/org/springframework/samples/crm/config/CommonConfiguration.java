package org.springframework.samples.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.thrift.ThriftHttpMessageConverter;
import org.springframework.samples.crm.rest.CrmRestController;
import org.springframework.samples.crm.services.CrmService;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CommonConfiguration {

	@Bean
	public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter() {
		MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		mappingJacksonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
		return mappingJacksonHttpMessageConverter;
	}

	@Bean
	public ThriftHttpMessageConverter messagePackHttpMessageConverter() {
		return new ThriftHttpMessageConverter();
	}


	public List<HttpMessageConverter<?>> messageConverters(){
		return Arrays.<HttpMessageConverter<?>>asList(
				  mappingJacksonHttpMessageConverter(),
				  messagePackHttpMessageConverter());
	}

}
