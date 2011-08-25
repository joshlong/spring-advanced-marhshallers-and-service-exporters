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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteAccessor;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that builds serviceProxy-side proxies that
 * in turn talk
 *
 * @author Josh Long
 */
public class AvroProxyFactoryBean<T> extends RemoteAccessor implements InitializingBean, MethodInterceptor, FactoryBean<T> {

	private int port = 2003;
	private Transceiver transceiver;
	private Object serviceProxy;

	private InetSocketAddress address;
	private Object client;

	public void setPort(int port) {
		this.port = port;
	}

	public void setTransceiver(Transceiver transceiver) {
		this.transceiver = transceiver;
	}

	@Override
	public T getObject() throws Exception {
		return (T) serviceProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return getServiceInterface();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (null == address) {
			address = new InetSocketAddress(port);
		}
		if (transceiver == null) {
			Assert.isTrue(port > 0, "the port must be greater than 0");
			transceiver = new NettyTransceiver(address);
		}
		this.client = SpecificRequestor.getClient(getServiceInterface(), transceiver);
		Assert.notNull(client, "we weren't able to build a serviceProxy");
		this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// now, unless i miss my mark, the interface of this proxy and the proxy weve created locally are the same,
		// so it should be a simple matter to forward the MethodInvocation on to the local serviceProxy
		Method method = invocation.getMethod();

		if (logger.isDebugEnabled()) {
			logger.debug("invoking " + invocation.toString() + " on the serviceProxy proxy");
		}

		return method.invoke(this.client, invocation.getArguments());
	}
}
