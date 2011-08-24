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
