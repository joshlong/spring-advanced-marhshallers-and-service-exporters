/*
 * Copyright 2011 the original author or authors.
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
package org.springframework.remoting.avro;


import org.apache.avro.ipc.Responder;
import org.apache.avro.ipc.Server;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;

/**
 * {@link RemoteExporter} for Avro's {@link org.apache.avro.Protocol protocols}.
 *
 * @author Josh Long
 */
abstract public class AbstractAvroServerExporter extends AbstractAvroExporter implements InitializingBean, SmartLifecycle {

	private volatile boolean running = false;
	private final Object monitor = new Object();
	private volatile boolean setup = false;
	private Server server;
	protected InetSocketAddress inetSocketAddress;
	protected int port = 2003;

	public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
		this.inetSocketAddress = inetSocketAddress;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public final void afterPropertiesSet() throws Exception {
		synchronized (this.monitor) {
			if (setup) {
				return;
			}
			if (inetSocketAddress == null) {
				inetSocketAddress = new InetSocketAddress(this.port);
			}
			Assert.isTrue(port > 0, "the port must be greater than 0");
			onInit();
			server = buildServer(getResponder());
			Assert.notNull(this.server, "the server was not properly constructed!");
			setup = true;
		}
	}

	protected void onInit() throws Exception {
	}

	/**
	 * Each subclass can provider their own {@link Server} implementation
	 *
	 * @param responder the responder to serve
	 * @return a server
	 * @throws Exception should anything go wrong
	 */
	abstract protected Server buildServer(Responder responder) throws Exception;

	@Override
	public boolean isAutoStartup() {
		return true;
	}


	@Override
	public void stop() {
		// shut it down
		this.running = false;
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
		Assert.notNull(this.server, "the service is null");
		server.start();
		running = true;
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
