package org.springframework.http.converter.messagepack;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.TaskExecutor;
import org.springframework.remoting.messagepack.MessagePackRpcProxyFactoryBean;
import org.springframework.remoting.messagepack.MessagePackRpcServiceExporter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class MessagePackRpcServiceExporterTest {

	private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

	private static Log log = LogFactory.getLog(MessagePackRpcServiceExporterTest.class);

	private int port = 2242;

	private String host = "127.0.0.1";

	static class MyService {
		String hello(String in) {
			return "Hello " + in;
		}

	}

	static class LaunchServer implements Runnable {
		private int port;
		private String host;

		public LaunchServer(String h, int p) {
			this.host = h;
			this.port = p;
		}

		@Override
		public void run() {
			try {
				MessagePackRpcServiceExporter exporter = new MessagePackRpcServiceExporter();
				exporter.setHost(this.host);
				exporter.setPort(this.port);
				exporter.setService(new MyService());
				exporter.setBeanClassLoader(ClassLoader.getSystemClassLoader());
				exporter.afterPropertiesSet();
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error(e);
				}
			}
		}
	}

	@Before
	public void setup() throws Throwable {
		this.executor.afterPropertiesSet();
		this.executor.execute(new LaunchServer(this.host, this.port));
	}

	@Test
	public void testConnectingAsClient() throws Throwable {
		MessagePackRpcProxyFactoryBean client = new MessagePackRpcProxyFactoryBean();
		client.setServiceInterface(MyService.class);
		client.setPort(this.port);
		client.setHost(this.host);
		client.setBeanClassLoader(ClassLoader.getSystemClassLoader());

		Object object = client.getObject();

		MyService myService = (MyService) object;
		System.out.println(myService.hello("Josh"));
	}
}
