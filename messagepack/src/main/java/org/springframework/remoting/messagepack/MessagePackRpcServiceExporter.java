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

package org.springframework.remoting.messagepack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.msgpack.MessagePack;
import org.msgpack.Template;
import org.msgpack.rpc.Server;
import org.msgpack.rpc.config.ClientConfig;
import org.msgpack.rpc.dispatcher.MethodDispatcher;
import org.msgpack.rpc.loop.EventLoop;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.messagepack.MessagePackRegistrar;
import org.springframework.messagepack.util.MessagePackUtils;
import org.springframework.messagepack.util.ReflectionUtils;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link RemoteInvocationBasedExporter} based on the {@link org.msgpack.MessagePack} RPC framework.
 * <p/>
 * TODO set a list of classes that you want to the exporter to automatically register. Perhaps useful when you don't want the exporter to crawl your class tree.
 *
 * @author Josh Long
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 * @see org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter
 */
public class MessagePackRpcServiceExporter extends RemoteInvocationBasedExporter implements InitializingBean, SmartLifecycle {

	private volatile boolean running = true;

	private Log log = LogFactory.getLog(getClass());
	private Server server;
	private ClientConfig clientConfig;
	private String listenHost = "127.0.0.1";
	private int listenPort = 1995;
	private InetSocketAddress address;
	private EventLoop eventLoop;
	private boolean exportServiceParameters = true;
	private boolean serializeJavaBeanProperties = true;

	private Set<Class> classes =new HashSet<Class>() ;

	private MessagePackRegistrar registrar = new MessagePackRegistrar();

	public void setExportServiceParameters(boolean exportServiceParameters) {
		this.exportServiceParameters = exportServiceParameters;
	}

	public void setSerializeJavaBeanProperties(boolean serializeJavaBeanProperties) {
		this.serializeJavaBeanProperties = serializeJavaBeanProperties;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

	public void setHost(String lh) {
		if (StringUtils.hasText(lh)) {
			this.listenHost = lh;
		}
	}

	public void setPort(int lp) {
		if (lp > 0) {
			this.listenPort = lp;
		}
	}

	public void setEventLoop(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
	}


	public void setServer(Server server) {
		this.server = server;
	}

	public void setClientConfig(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	protected Server buildServer() {
		Server svr;
		if (clientConfig != null) {
			if (log.isDebugEnabled()) {
				log.debug("using user provided clientConfig.");
			}
			svr = new Server(clientConfig, eventLoop);
		} else {
			svr = new Server(eventLoop);
		}
		return svr;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Class<?> serviceInterface = getServiceInterface();

		Object service = getService();

		registrar.setSerializeJavaBeanProperties(this.serializeJavaBeanProperties);
		if(exportServiceParameters) {
			registrar.discoverClasses( service.getClass());
		}
		registrar.registerClasses(this.classes);
		registrar.afterPropertiesSet();


		Assert.notNull(service, "the service target can NOT be null!");

		if (eventLoop == null) {
			EventLoopFactoryBean eventLoopFactoryBean = new EventLoopFactoryBean();
			eventLoop = eventLoopFactoryBean.getObject();
			if (log.isDebugEnabled()) {
				log.debug("using EventLoop#defaultEventLoop(). " +
						          "Consider using an existing EventLoop," +
						          " or using a " + EventLoopFactoryBean.class.getName());
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("using existing " + EventLoop.class.getName());
			}
		}

		if (server == null) {
			server = buildServer();
		}

		Assert.notNull(this.server, "the server must not be null");

		MethodDispatcher dispatcher = null;
		if (serviceInterface != null) {
			if (log.isDebugEnabled()) {
				log.debug("service interface (" + serviceInterface.getName() + ") was provided. Using to limit the methods exposed.");
			}
			dispatcher = new MethodDispatcher(service, serviceInterface);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("no service interface was provided. Exposing target object directly.");
			}
			dispatcher = new MethodDispatcher(service);
		}

		server.serve(dispatcher);
		if (this.address != null) {
			server.listen(this.address);
		} else {
			server.listen(this.listenHost, this.listenPort);
		}


	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
		running = false ;
	}

	@Override
	public void start() {
		try {

			eventLoop.join();
			running = true ;
		} catch (InterruptedException e) {
			if (log.isErrorEnabled()) {
				log.debug(e);
			}

		}
	}

	@Override
	public void stop() {
		try {
			this.eventLoop.shutdown();
		} finally {
			this.server.close();
		}
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
