package org.springframework.remoting.messagepack;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class Server {

	private static Log log = LogFactory.getLog(Server.class);

	// both the client and the server need to agree on which host to connect
	public static String HOST = "127.0.0.1";
	public static int PORT = 1995;

	/**
	 * Service interface. Required only by the client. The server doesn't even need to implement it, though it helps.
	 */
	public static class MyEchoService implements EchoService {
		private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		public MyEchoService() {
			executor.afterPropertiesSet();
		}

		/**
		 * nb: this is not part of the interface, but MessagePack will attempt to call it first, if it's available.
		 * <p/>
		 * The {@link org.msgpack.rpc.Request} parameter's used to give the callee a chance to handle the request asynchronously or to leverage the error handling mechanism.
		 *
		 * @param request object required to facilitate communication
		 * @param in      the string to send back
		 */
		public void hello(final org.msgpack.rpc.Request request, final String in) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(10 * 1000);
						if (log.isDebugEnabled()) {
							log.debug("sleeping for 10s");
						}
						request.sendResult("Hello, " + in);
					} catch (Throwable thro) {
						if (log.isErrorEnabled()) {
							log.error(thro);
						}
					}
				}
			});
		}

		@Override
		public String echo(String in) {
			return "No, " + in;
		}
	}

	@Configuration
	static class MyServerConfiguration {
		@Bean
		public MyEchoService service() {
			return new MyEchoService();
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
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyServerConfiguration.class);
	}

}

