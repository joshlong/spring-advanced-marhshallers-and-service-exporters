package org.springframework.remoting.messagepack;

import org.msgpack.MessagePackObject;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.BeansTemplateBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.style.ToStringCreator;

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

		String echoResponse = client.echo("You're stupid.");
		System.out.println("Calling EchoService#echo(String) " + echoResponse);


		BeansTemplateBuilder builder = new BeansTemplateBuilder();
		TemplateRegistry.register(Cat.class, builder.buildTemplate(Cat.class));


		Cat felix = client.fetch();
		System.out.println(new ToStringCreator(felix).append("name", felix.getName()).append("age", felix.getAge()).toString());


		Future<MessagePackObject> helloResponse = client.hello("Josh");
		System.out.println("Calling EchoService#hello(String) " + helloResponse.get().asString());


	}
}
