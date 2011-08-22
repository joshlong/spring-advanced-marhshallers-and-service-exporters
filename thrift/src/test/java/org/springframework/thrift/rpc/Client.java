package org.springframework.thrift.rpc;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.springframework.thrift.crm.Crm;
import org.springframework.thrift.crm.Customer;

public class Client {

	static void setup() throws Throwable {
		TSocket socket = new TSocket("localhost", 9090);
		TBinaryProtocol protocol = new TBinaryProtocol(socket);

		Crm.Client client = new Crm.Client(protocol);

		socket.open();
		Customer customer = client.createCustomer("josh", "Long", "josh.long@email.com");

		System.out.println(ToStringBuilder.reflectionToString(customer) );

	}

	static public void main(String args[]) throws Throwable {
		setup();
	}
}
