package org.springframework.remoting.messagepack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Client {

	private static Log log = LogFactory.getLog(Client.class);

	static public void main(String[] a) throws Throwable {

		try {
			MessagePackRpcProxyFactoryBean client = new MessagePackRpcProxyFactoryBean();
			client.setServiceInterface(Server.EchoService.class);
			client.setHost(Server.HOST);
			client.setPort(Server.PORT);
			client.setBeanClassLoader(ClassLoader.getSystemClassLoader());
			client.afterPropertiesSet();

			Object object = client.getObject();

			Server.EchoService myService = (Server.EchoService) object;

			if (log.isInfoEnabled()) {
				log.info(myService.hello("Josh"));
			}

		} catch (Throwable throwable) {
			if (log.isErrorEnabled()) {
				log.error("there's an error", throwable);
			}
		}
	}
}
