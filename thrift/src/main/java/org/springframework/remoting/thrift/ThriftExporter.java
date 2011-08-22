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


package org.springframework.remoting.thrift;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.thrift.util.ThriftUtil;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;

/**
 * Exports Thrift based RPC services. Requires for the {@link #serviceInterface} property
 * a reference to the IFace class inside the Thrift generated class, e.g., {@code Foo.IFace.class}.
 *
 * The {@link #service} itself is any POJO, but that POJO must implement the {@link #serviceInterface}.
 *
 * @author Josh Long
 */
public class ThriftExporter extends RemoteInvocationBasedExporter implements InitializingBean, SmartLifecycle {

	private Class thriftClass;

	private TServer tServer;

	private volatile boolean running = false;

	private TProcessor processor;

	private int listenPort = ThriftUtil.DEFAULT_PORT;

	private InetSocketAddress address;

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}


	@Override
	public void setServiceInterface(Class serviceInterface) {
		super.setServiceInterface(ThriftUtil.buildServiceInterface(serviceInterface));
		this.thriftClass = getServiceInterface().getEnclosingClass();
		Assert.notNull(this.thriftClass, "the 'thriftClass' can't be null");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Class serviceInterface = getServiceInterface();
		Object service = getService();

		Assert.notNull(service, "the service must not be null");
		Assert.notNull(serviceInterface, "the serviceInterface must not be null");
		Assert.isTrue(serviceInterface.isAssignableFrom(service.getClass()));

		this.processor = ThriftUtil.buildProcessor(thriftClass, getServiceInterface(), getService());

	}


	@Override
	public boolean isAutoStartup() {
		return true;
	}


	@Override
	public void stop(Runnable callback) {
		stop();
		if (callback != null) {
			callback.run();
		}
	}

	@Override
	public void start() {

		this.running = true;
		try {

			if (logger.isDebugEnabled()) {
				logger.debug("starting " + ThriftExporter.class.getName() + ". This exporter's only been tested on Thrift 0.7. Your mileage may vary with other versions");
			}

			TServerSocket socket = null;
			if (this.address != null) {
				socket = new TServerSocket(this.address);
			} else {
				socket = new TServerSocket(this.listenPort);
			}
			TThreadPoolServer.Args args = new TThreadPoolServer.Args(socket);
			args.processor(processor);

			if (logger.isDebugEnabled()) {
				logger.debug("starting to listen on " + socket.getServerSocket().toString());
			}

			tServer = new TThreadPoolServer(args);
			tServer.serve();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop() {
		if (null != this.tServer) {
			tServer.stop();
		}
		this.running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public int getPhase() {
		return 0;
	}
}
