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
	public BeanNameUrlHandlerMapping beanNameUrlHandlerMapping (){
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
