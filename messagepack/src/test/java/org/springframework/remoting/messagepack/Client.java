package org.springframework.remoting.messagepack;

import org.msgpack.MessagePackObject;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.messagepack.MessagePackUtils;

import java.util.concurrent.Future;

public class Client {

	public static interface ClientService extends EchoService {
		Future<MessagePackObject> hello(String name);
	}

	@Configuration
	static class ClientConfiguration {
		@Bean
		public MessagePackRpcProxyFactoryBean<ClientService> clientService() {
			MessagePackRpcProxyFactoryBean<ClientService> svc = new MessagePackRpcProxyFactoryBean<ClientService>();
			svc.setServiceInterface(ClientService.class);
			svc.setPort(Server.PORT);
			svc.setHost(Server.HOST);
			return svc;
		}
	}


	public static void main(String args[]) throws Throwable {



		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(ClientConfiguration.class);
		ClientService client = annotationConfigApplicationContext.getBean(ClientService.class);

		String noise  = client.alarm("Danger");
		System.out.println("Calling "+AlarmService.class.getName()+ "#alarm(String) "  + noise )  ;

		String echoResponse = client.echo("You're stupid.");
		System.out.println("Calling " +EchoService.class.getName()+ "#echo(String) " + echoResponse);

		Cat garfield = client.fetch();
		System.out.println(garfield);


		Future<MessagePackObject> helloResponse = client.hello("Josh");
		System.out.println("Calling EchoService#hello(String) " + helloResponse.get().asString());
	}
}
