package org.springframework.remoting.messagepack;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.support.RemoteAccessor;
import org.springframework.remoting.support.RemoteInvocationBasedAccessor;

/***
 *
 * @see org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean
 * @see org.springframework.remoting.rmi.RmiProxyFactoryBean
 * @author Josh Long
 */
public class MessagePackRpcProxyFactoryBean
		extends RemoteAccessor
		implements FactoryBean<Object>, BeanClassLoaderAware {

	private String host;
	private ClassLoader classLoader ;
	private int port;

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
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}



}
