package org.springframework.remoting.messagepack;

import org.msgpack.MessagePackObject;
import org.msgpack.object.ArrayType;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.BeansTemplateBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.messagepack.MessagePackUtils;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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

		Cat garfield = client.fetch();

		MessagePackUtils.expandResultIntoExpectedObjectGraph(Cat.class, garfield);

		System.out.println(garfield);

		//Collection<Cat> cats = listOf(Cat.class,  garfield  , "friends");
		//for ( Cat c : cats )
		///System.out.println( c.toString())  ;

		/*System.out.println(garfield.toString());
		for (Object c : garfield.getFriends()) {
			ArrayType arrayType = (ArrayType) c;
			Cat catObj = arrayType.convert(Cat.class);
			System.out.println( "cat: " + catObj.toString());

			*//*List<MessagePackObject> mpo=arrayType.asList() ;
			for(MessagePackObject mpobj: mpo){
				Cat cat = mpobj.convert(Cat.class);
				System.out.println( "class: "+ cat.getName());
			}*//*
			Class<?> clzz =c.getClass() ;
			System.out.println( "class: "+ clzz.getName());
		}
*/

	/*	Future<MessagePackObject> helloResponse = client.hello("Josh");
		System.out.println("Calling EchoService#hello(String) " + helloResponse.get().asString());

*/
	}
}
