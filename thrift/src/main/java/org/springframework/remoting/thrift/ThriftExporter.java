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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.util.Assert;

/**
 * Simple service exporter to automatically export Thrift services
 * todo HessianServiceExporter (e.g., a ThriftExporter over Servlets) and  * todo build a ThriftRpcHttpServiceExporter which does the same sort of thing (backed by Servlets) as HessianExporter vs
 *
 * @author Josh Long
 */
public class ThriftExporter extends RemoteInvocationBasedExporter implements InitializingBean {


	/*	Crm.Iface iface = new Crm.Iface() {
		   @Override
		   public Customer createCustomer(String fn, String ln, String email) throws TException {
			   Customer customer = new Customer( "Josh", "Long" ,"email@email.com", 234) ;
			   return customer;
		   }

		   @Override
		   public Customer getCustomerById(int customerId) throws TException {
			   Customer customer = new Customer( "Josh", "Long" ,"email@email.com", 232) ;
			   return customer;
		   }
	   } ;

	   Crm.Processor <Crm.Iface> processor = new Crm.Processor<Crm.Iface>(iface);

	   TServerSocket socket = new TServerSocket( 9090);

	   TThreadPoolServer.Args args = new TThreadPoolServer.Args( socket ) ;
	   args.processor(processor)  ;
	   TServer tServer  = new TThreadPoolServer(args) ;

	   tServer.serve();*/


	private final String IFACE_NAME = "$Iface";
	private final String ASYNC_IFACE_NAME = "$AsyncIface"          ;
	protected Class getInnerClassOrNull(Class thriftServiceClass, String mustContain, boolean isInterface) {
		Class[] declaredClasses = thriftServiceClass.getDeclaredClasses();
		if (logger.isDebugEnabled()) {
			logger.debug("there are " + declaredClasses.length + " declared classes");
		}

		for (Class declaredClass : declaredClasses) {
			if (declaredClass.isInterface()) {
				if (logger.isDebugEnabled()) {
					logger.debug("the class " + declaredClass.getName() + " is an interface");
				}
				if (isInterface && declaredClass.getName().contains(mustContain)) {
					return declaredClass;
				}


			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("the class " + declaredClass.getName() + " is a regular class");
				}
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
				super.setServiceInterface(serviceInterface);
			}
		} else if (!serviceInterface.isInterface()) {

			Class iface = this.getInnerClassOrNull(serviceInterface, IFACE_NAME, true);
			Assert.notNull(iface, "the service interface was not found, but is required");

			if (logger.isDebugEnabled()) {
				logger.debug("setting " + iface.getName() + " as the service interface.");
			}
			super.setServiceInterface(iface);
		}
		try {
			resetServiceInterface();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setService(Object service) {
		super.setService(service);
		try {
			resetServiceInterface();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	protected void resetServiceInterface() throws Exception {
		if (getService() != null && getServiceInterface() != null) {
			Object service = getService();
			Class<?> interfaceClass = getServiceInterface();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {


	}
}
