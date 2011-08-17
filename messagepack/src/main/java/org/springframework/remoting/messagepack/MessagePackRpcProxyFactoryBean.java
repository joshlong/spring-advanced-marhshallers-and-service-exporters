package org.springframework.remoting.messagepack;

import org.msgpack.rpc.Client;
import org.msgpack.rpc.config.ClientConfig;
import org.msgpack.rpc.loop.EventLoop;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteAccessor;
import org.springframework.util.Assert;
import org.springframework.util.messagepack.MessagePackUtils;

/**
 * Used to create client side proxies that can communicate with the remote services.
 *
 * The interface used on the client does <EM>not</EM> need to match the interface exposed on the server.
 *
 *
 *
 *
 * @author Josh Long
 * @see org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean
 * @see org.springframework.remoting.rmi.RmiProxyFactoryBean
 */
public class MessagePackRpcProxyFactoryBean <T> extends RemoteAccessor implements FactoryBean<T>, BeanClassLoaderAware, InitializingBean {

	public void setExportServiceParameters(boolean exportServiceParameters) {
		this.exportServiceParameters = exportServiceParameters;
	}

	public void setSerializeJavaBeanProperties(boolean serializeJavaBeanProperties) {
		this.serializeJavaBeanProperties = serializeJavaBeanProperties;
	}

	private boolean exportServiceParameters = true;
	private boolean serializeJavaBeanProperties = true;
	private ClientConfig clientConfig;
	private Client client;
	private EventLoop eventLoop;
	private T proxy;
	private String host = "127.0.0.1";
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
	public T getObject() throws Exception {
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
		if (exportServiceParameters) {
			MessagePackUtils.registerClassesOnInterface( getServiceInterface(), this.serializeJavaBeanProperties);
		}

		if (eventLoop == null) {
			this.eventLoop = new EventLoopFactoryBean().getObject();
		}
		if (clientConfig != null) {
			client = new Client(this.host, this.port, this.clientConfig, this.eventLoop);
		} else {
			client = new Client(this.host, this.port, this.eventLoop);
		}

		Assert.notNull(this.client,  "the client can't be null");
		Assert.notNull(this.host, "the host can't be null");

	    proxy = (T) client.proxy(getServiceInterface());

		Assert.notNull(this.proxy,  "the proxy can't be null");

	}
}
