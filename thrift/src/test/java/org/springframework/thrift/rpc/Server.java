package org.springframework.thrift.rpc;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.*;
import org.springframework.thrift.crm.Crm;
import org.springframework.thrift.crm.Customer;

/**
 * Simple example demonstrating the rpc part of this arrangement
 *
 * @author Josh Long
 */
public class Server {

	private int port = 7911 ;
	private boolean protocolStrictRead=true,
					protocolStrictWrite=true;

	static void setup () throws Throwable {

		Crm.Iface iface = new Crm.Iface() {
			@Override
			public Customer createCustomer(String fn, String ln, String email) throws TException {
				Customer customer = new Customer( "Josh", "Long" ,"email@email.com", 234) ;
			    return customer;
			}

			@Override
			public Customer getCustomerById(int customerId) throws TException {
				Customer customer = new Customer( "Josh", "Long" ,"email@email.com", 232) ;
				return customer;
			}
		} ;

		Crm.Processor <Crm.Iface> processor = new Crm.Processor<Crm.Iface>(iface);

		TServerSocket socket = new TServerSocket( 9090);

		TThreadPoolServer.Args args = new TThreadPoolServer.Args( socket ) ;
		args.processor(processor)  ;
		TServer tServer  = new TThreadPoolServer(args) ;

		tServer.serve();

	}

	TServerTransport serverSocket;
	Object service ;
	TProtocol protocol ;
	TServerTransport transport ;

	public void setServerSocket(TServerTransport serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void setService(Object service) {
		this.service = service;
	}

	public void setProtocol(TProtocol protocol) {
		this.protocol = protocol;
	}

	static public void main(String []args) throws Throwable {
       setup()  ;
	}
}
