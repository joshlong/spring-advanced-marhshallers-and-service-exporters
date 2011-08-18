package org.springframework.remoting.messagepack;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class Server {

	private static Log log = LogFactory.getLog(Server.class);

	// both the client and the server need to agree on which host to connect
	public static String HOST = "127.0.0.1";
	public static int PORT = 1995;


	@Configuration
	static class MyServerConfiguration {
		@Bean
		public DefaultEchoService service() {
			return new DefaultEchoService()  ;
		}

		@Bean
		public MessagePackRpcServiceExporter helloService() {
			MessagePackRpcServiceExporter exporter = new MessagePackRpcServiceExporter();
			exporter.setHost(HOST);
			exporter.setPort(PORT);
			exporter.setService(service());
			return exporter;
		}
	}

	public static void main(String[] args) throws Throwable {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyServerConfiguration.class);

	}

}

