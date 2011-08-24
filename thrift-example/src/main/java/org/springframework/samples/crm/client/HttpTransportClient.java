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

package org.springframework.samples.crm.client;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.thrift.ThriftProxyFactoryBean;
import org.springframework.thrift.crm.Crm;
import org.springframework.thrift.crm.Customer;

/**
 * <P> Simple example of a client using the Thrift HTTP transport
 *
 * @author Josh Long
 */
public class HttpTransportClient {

	private static Log log = LogFactory.getLog(HttpTransportClient.class);

	@Configuration
	public static class ThriftProxyClientConfiguration {
		@Bean
		public ThriftProxyFactoryBean client() {
			// demonstrates how to use the protocol over HTTP
			THttpClient.Factory httpClientFactory = new THttpClient.Factory("http://localhost:8080/http/crm");
			TTransport tTransport = httpClientFactory.getTransport(null);

			ThriftProxyFactoryBean proxy = new ThriftProxyFactoryBean();
			proxy.setTransport(tTransport);
			proxy.setServiceInterface(Crm.class);
			return proxy;
		}
	}

	public static void main(String[] args) throws Throwable {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ThriftProxyClientConfiguration.class);
		Crm.Iface clientInterface = ac.getBean(Crm.Iface.class);
		Customer customer = clientInterface.createCustomer("Josh", "Long", "emial@email.com");
		if (log.isDebugEnabled()) {
			log.debug("received the customer, " + customer.toString());
		}
	}
}
