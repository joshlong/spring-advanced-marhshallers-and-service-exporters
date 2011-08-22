package org.springframework.thrift.util;

import org.apache.thrift.TProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;

/**
 * Class to store methods common to multiple implementations
 *
 * @author Josh Long
 */
abstract public class ThriftUtil {
	public static   int DEFAULT_PORT = 1995;

	public static Class buildServiceInterface(Class serviceInterface) {
		if (serviceInterface.isInterface()) {
			String iFaceNameConvention = serviceInterface.getName();
			if (iFaceNameConvention.contains(IFACE_NAME)) {
				Class<?> clzz = serviceInterface.getEnclosingClass();
				Assert.notNull(clzz, "the enclosing class can not be null");
				return (serviceInterface);
			}
		} else if (!serviceInterface.isInterface()) {
			Class iface = ThriftUtil.getThriftServiceInnerClassOrNull(serviceInterface, IFACE_NAME, true);
			Assert.notNull(iface, "the service interface was not found, but is required");
			return (iface);
		}
		return null;
	}

	public static Class getThriftServiceInnerClassOrNull(Class thriftServiceClass, String mustContain, boolean isInterface) {
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

	/**
	 * String to find interface of the class inside the Thrift service that we should bind this service to publically
	 */
	public static String IFACE_NAME = "$Iface";

	/**
	 * the name of the internal Processor class
	 */
	public static String PROCESSOR_NAME = "$Processor";


	@SuppressWarnings("unchecked")
	public static TProcessor buildProcessor(Class thriftClass, Class svcInterface, Object service) throws Exception {
		Class<TProcessor> processorClass = (Class<TProcessor>) getThriftServiceInnerClassOrNull(thriftClass, PROCESSOR_NAME, false);
		Assert.notNull(processorClass, "the processor class must not be null");
		Constructor constructor = ClassUtils.getConstructorIfAvailable(processorClass, svcInterface);
		Assert.notNull(constructor);
		Object newlyCreatedProcessorBean = constructor.newInstance(service);
		Assert.notNull(newlyCreatedProcessorBean);
		Assert.isInstanceOf(TProcessor.class, newlyCreatedProcessorBean);
		return (TProcessor) newlyCreatedProcessorBean;
	}

}
