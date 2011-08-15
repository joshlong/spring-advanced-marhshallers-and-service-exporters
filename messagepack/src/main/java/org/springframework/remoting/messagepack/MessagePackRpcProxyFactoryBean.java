package org.springframework.remoting.messagepack;

import org.msgpack.rpc.Client;
import org.msgpack.rpc.config.ClientConfig;
import org.msgpack.rpc.loop.EventLoop;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteAccessor;
import org.springframework.util.Assert;

/**
 * @author Josh Long
 * @see org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean
 * @see org.springframework.remoting.rmi.RmiProxyFactoryBean
 */
public class MessagePackRpcProxyFactoryBean extends RemoteAccessor implements FactoryBean<Object>, BeanClassLoaderAware, InitializingBean {

	private ClientConfig clientConfig;
	private Client client;
	private EventLoop eventLoop;
	private Object proxy;
	private String host;
	private ClassLoader classLoader;
	private int port = 1995;

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public Object getObject() throws Exception {
		return this.proxy;
	}

	@Override
	public Class<?> getObjectType() {
		return this.proxy.getClass();
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		if (eventLoop == null) {
			this.eventLoop = EventLoop.defaultEventLoop();
		}
		if (clientConfig != null) {
			client = new Client(this.host, this.port, this.clientConfig, this.eventLoop);
		} else {
			client = new Client(this.host, this.port, this.eventLoop);
		}

		Assert.notNull(this.client,  "the client can't be null");
		Assert.notNull(this.host, "the HOST can't be null");

	    proxy = client.proxy(getServiceInterface());
		Assert.notNull(this.proxy,  "the proxy can't be null");
	}
}
