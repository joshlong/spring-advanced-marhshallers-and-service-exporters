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
import org.springframework.remoting.thrift.ThriftServiceExporter;
import org.springframework.samples.crm.services.CrmService;
import org.springframework.thrift.crm.Crm;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

@Configuration
public class ExporterConfiguration {
	@Bean
	public Crm.Iface crmServiceImpl() {
		return new CrmService();
	}

	@Bean
	public BeanNameUrlHandlerMapping beanNameUrlHandlerMapping() {
		return new BeanNameUrlHandlerMapping();
	}

	@Bean(name = "/crm")
	public ThriftServiceExporter crm() {
		ThriftServiceExporter exporter = new ThriftServiceExporter();
		exporter.setService(crmServiceImpl());
		exporter.setServiceInterface(Crm.Iface.class);
		return exporter;
	}
}
