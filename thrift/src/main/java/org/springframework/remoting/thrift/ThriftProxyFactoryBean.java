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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteAccessor;
import org.springframework.thrift.util.ThriftUtil;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Builds proxies that can connect to a Thrift RPC service.
 *
 * <P>Thrift clients will by default
 * use the Thrift binary protocol unless the {@link #protocolFactory} is overridden.
 *
 * @author Josh Long
 * @see org.springframework.remoting.caucho.HessianProxyFactoryBean
 */
public class ThriftProxyFactoryBean extends RemoteAccessor implements InitializingBean, MethodInterceptor, FactoryBean<Object> {

	// default protocol will be binary
	private TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

	// transport is optional
	private TTransport transport;

	// protocol is optional
	private TProtocol protocol;

	// the client as created using the Thrift APIs
	private Object client;

	// the proxy that we create which is in turn returned to the client of this class
	private Object serviceProxy;

	private String host = "127.0.0.1";

	private int port = ThriftUtil.DEFAULT_PORT;

	public void setProtocolFactory(TProtocolFactory inProtocolFactory) {
		this.protocolFactory = inProtocolFactory;
	}

	public void setTransport(TTransport transport) {
		this.transport = transport;
	}

	public void setProtocol(TProtocol protocol) {
		this.protocol = protocol;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public void setServiceInterface(Class serviceInterface) {
		super.setServiceInterface(ThriftUtil.buildServiceInterface(serviceInterface));
	}

	public Object getObject() {
		return this.serviceProxy;
	}

	public Class<?> getObjectType() {
		return getServiceInterface();
	}

	public boolean isSingleton() {
		return true;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// now, unless i miss my mark, the interface of this proxy and the proxy weve created locally are the same,
		// so it should be a simple matter to forward the MethodInvocation on to the local client
		Method method = invocation.getMethod();

		if (logger.isDebugEnabled()) {
			logger.debug("invoking " + invocation.toString() + " on the client proxy");
		}

		return method.invoke(this.client, invocation.getArguments());
	}

	@Override
	public void afterPropertiesSet() {
		if (getServiceInterface() == null) {
			throw new IllegalArgumentException("Property 'serviceInterface' is required");
		}
		if (this.transport == null) {
			Assert.notNull(this.host, "Property 'host' is required");
			Assert.isTrue(this.port > 0, "Property 'port' is required and must be greater than 0");
			this.transport = new TSocket(this.host, this.port);
		}

		if (this.protocol == null) {
			this.protocol =  this.protocolFactory.getProtocol(this.transport);
		}

		try {
			Class thriftClass = getServiceInterface().getEnclosingClass();
			Assert.notNull(thriftClass, "the enclosing class must not be null");

			Class clientClass = ThriftUtil.getThriftServiceInnerClassOrNull(thriftClass, "$Client", false);
			Assert.notNull(clientClass, "the client class must not be null ");

			Constructor constructor = ClassUtils.getConstructorIfAvailable(clientClass, TProtocol.class);

			this.client = constructor.newInstance(this.protocol);

			Assert.notNull(this.client, "the Thrift RPC client was not correctly created. Aborting.");
			this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
			this.transport.open();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}
