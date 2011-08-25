package org.springframework.avro.rpc;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.avro.crm.Crm;
import org.springframework.avro.crm.Customer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;

/**
 * Simple example demonstrating a client-server with Avro.
 * <p/>
 * Scratchpad.
 *
 * @author Josh Long
 */
public class Main {



	static class MyCrm implements Crm {

		static private Random randomIdGenerator = new Random();

		@Override
		public Customer createCustomer(CharSequence fn, CharSequence ln, CharSequence email) throws AvroRemoteException {
			Customer c = new Customer();
			c.email = email;
			c.lastName = ln;
			c.firstName = fn;
			c.id = randomIdGenerator.nextInt();
			return c;
		}
	}

	static <T> Server startServer(Class<T> tc, int port) throws IOException {
		Crm impl = new MyCrm();
		SpecificResponder sr = new SpecificResponder(Crm.class, impl);
		InetSocketAddress inetSocketAddress = new InetSocketAddress(port);

		SaslSocketServer ssl = new SaslSocketServer(sr, inetSocketAddress);
		ssl.start();
		return ssl;
	}

	static <T> T startClient(Class<T> clz, int port) throws IOException {
		SaslSocketTransceiver client = new SaslSocketTransceiver(new InetSocketAddress(port));
		return SpecificRequestor.getClient(clz, client);

	}

	static public void main(String args[]) throws Throwable {

		int port = 1999;
		Server server = startServer(Crm.class, port);
		Crm client = startClient(Crm.class, port);
		Customer customer = client.createCustomer("josh", "Long", "email@email.com");
		System.out.println(ToStringBuilder.reflectionToString(customer));

	}

}
