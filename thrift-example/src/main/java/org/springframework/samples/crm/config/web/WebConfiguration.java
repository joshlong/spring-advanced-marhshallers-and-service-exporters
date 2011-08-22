package org.springframework.samples.crm.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.samples.crm.config.CommonConfiguration;
import org.springframework.samples.crm.rest.CrmRestController;
import org.springframework.samples.crm.services.CrmService;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.inject.Inject;
import java.util.List;

/**
 * Sets up all artifacts related to the web
 */
@Configuration
@Import(CommonConfiguration.class)
@EnableWebMvc
public class WebConfiguration extends WebMvcConfigurerAdapter {

	@Inject private CommonConfiguration commonConfiguration ;

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

		for(HttpMessageConverter<?> mc : commonConfiguration.messageConverters())
			converters.add(mc);
	}
	@Bean public CrmService crmService (){
		return new CrmService();
	}
	@Bean public CrmRestController controller (){
		return new CrmRestController();
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
