/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
public class RestConfiguration extends WebMvcConfigurerAdapter {

	@Inject
	private CommonConfiguration commonConfiguration;

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter<?> mc : commonConfiguration.messageConverters()) {
			converters.add(mc);
		}
	}

	@Bean
	public CrmService crmService() {
		return new CrmService();
	}

	@Bean
	public CrmRestController controller() {
		return new CrmRestController();
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
