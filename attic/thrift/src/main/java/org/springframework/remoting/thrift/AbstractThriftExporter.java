package org.springframework.remoting.thrift;

import org.apache.thrift.TProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.thrift.util.ThriftUtil;
import org.springframework.util.Assert;

/**
 * Exports a single bean, but does not "serve" the interface over a network connection. Requires
 * for the {@link #serviceInterface} property a reference to the "IFace" class inside the
 * Thrift generated class, e.g., {@code Foo.IFace.class}. The {@link #service} itself is any POJO,
 * but that POJO must implement the {@link #serviceInterface}.
 *
 * @author Josh Long
 */
abstract public class AbstractThriftExporter extends RemoteExporter implements InitializingBean {

	/**
	 * the top-level class that was generated as part of thrift compilation for a given service interface
	 */
	protected Class thriftClass;

	/**
	 * handles dispatching incoming requests to the 'service'
	 */
	protected TProcessor processor;

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

	public void setProcessor(TProcessor processor) {
		this.processor = processor;
	}

}
