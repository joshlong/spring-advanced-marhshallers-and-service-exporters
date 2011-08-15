package org.springframework.remoting.messagepack;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class Server {

	private static Log log = LogFactory.getLog(Server.class);

	// both the client and the server need to agree on which host to connect
	public static String HOST = "127.0.0.1";
	public static int PORT = 1995;

	/**
	 * Service interface. Required only by the client. The server doesn't even need to implement it, though it helps.
	 */
	public static interface EchoService {
		String hello(String in);
	}

	public static class MyEchoService implements EchoService {
		@Override
		public String hello(String in) {
			return "Hello, " + in;
		}
	}

	public static void main(String[] args) throws Throwable {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.afterPropertiesSet();

		try {
			MessagePackRpcServiceExporter exporter = new MessagePackRpcServiceExporter();
			exporter.setHost(HOST);
			exporter.setPort(PORT);
			exporter.setService(new MyEchoService());
			exporter.setBeanClassLoader(ClassLoader.getSystemClassLoader());
			exporter.afterPropertiesSet();
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			throw new RuntimeException(e) ;
		}
	}

}

