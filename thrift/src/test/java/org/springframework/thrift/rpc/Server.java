/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
