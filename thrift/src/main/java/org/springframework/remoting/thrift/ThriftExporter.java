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
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;

/**
 * Simple service exporter to automatically export Thrift services
 *
 * @author Josh Long
 */
public class ThriftExporter extends RemoteInvocationBasedExporter implements InitializingBean, SmartLifecycle {


	/**
	 * String to find interface of the class inside the Thrift service that we should bind this service to publically
	 */
	public static String IFACE_NAME = "$Iface";

	/**
	 * the name of the internal Processor class
	 */
	public static String PROCESSOR_NAME = "$Processor";

	private Class thriftClass;

	private TServer tServer ;

	private volatile boolean running = false;

	private TProcessor processor ;

	private int listenPort = 1995;

	private InetSocketAddress address;

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

	@SuppressWarnings("unchecked")
	private TProcessor buildProcessor() throws Exception {
		Class <TProcessor> processorClass = (Class<TProcessor>) getThriftServiceInnerClassOrNull(thriftClass, PROCESSOR_NAME, false);
		Assert.notNull(processorClass, "the processor class must not be null");

		Constructor constructor = ClassUtils.getConstructorIfAvailable(processorClass, getServiceInterface());
		Assert.notNull(constructor);

		Object newlyCreatedProcessorBean = constructor.newInstance(getService());
		Assert.notNull(newlyCreatedProcessorBean);
		Assert.isInstanceOf(TProcessor.class, newlyCreatedProcessorBean);

		return (TProcessor) newlyCreatedProcessorBean;
	}


	protected void establishThriftServiceClass(Class parentClass) {
		this.thriftClass = parentClass.getEnclosingClass();
		if (logger.isDebugEnabled()) {
			logger.debug("the parent class is " + this.thriftClass.getName());
		}
		Assert.notNull(this.thriftClass);
	}

	protected Class getThriftServiceInnerClassOrNull(Class thriftServiceClass, String mustContain, boolean isInterface) {
		Class[] declaredClasses = thriftServiceClass.getDeclaredClasses();


		for (Class declaredClass : declaredClasses) {
			if (declaredClass.isInterface()) {
				if (isInterface && declaredClass.getName().contains(mustContain)) {
					return declaredClass;
				}
			} else {
				if (!isInterface && declaredClass.getName().contains(mustContain)) {
					return declaredClass;
				}
			}
		}


		return null;
	}

	@Override
	public void setServiceInterface(Class serviceInterface) {
		if (serviceInterface.isInterface()) {
			String iFaceNameConvention = serviceInterface.getName();
			if (iFaceNameConvention.contains(IFACE_NAME)) {
				Class<?> clzz = serviceInterface.getEnclosingClass();
				Assert.notNull(clzz, "the enclosing class can not be null");
				establishThriftServiceClass(serviceInterface);
				super.setServiceInterface(serviceInterface);
			}
		} else if (!serviceInterface.isInterface()) {

			Class iface = this.getThriftServiceInnerClassOrNull(serviceInterface, IFACE_NAME, true);
			Assert.notNull(iface, "the service interface was not found, but is required");

			if (logger.isDebugEnabled()) {
				logger.debug("setting " + iface.getName() + " as the service interface.");
			}
			establishThriftServiceClass(iface);
			super.setServiceInterface(iface);
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Class serviceInterface = getServiceInterface();
		Object service = getService();
		Assert.notNull(service, "the service must not be null");
		Assert.notNull(serviceInterface, "the serviceInterface must not be null");

		Assert.isTrue(serviceInterface.isAssignableFrom(service.getClass()));

		this.processor = buildProcessor()  ;

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


			TServerSocket socket =  null ;
			if( this.address != null ){
				socket = new TServerSocket(this.address);
			} else {
				socket = new TServerSocket(this.listenPort) ;
			}
			TThreadPoolServer.Args args = new TThreadPoolServer.Args(socket);
			args.processor(processor);

			tServer  = new TThreadPoolServer(args) ;
			tServer.serve();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop() {
		if(null != this.tServer){
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
