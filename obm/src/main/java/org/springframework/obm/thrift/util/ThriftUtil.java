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

package org.springframework.obm.thrift.util;

import org.apache.thrift.TProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;

/**
 * utility methods common to multiple classes in the Thrift support
 *
 * @author Josh Long
 */
abstract public class ThriftUtil {
    /**
     * string to find the {@link TProcessor} implementation inside the Thrift class
     */
    public static String PROCESSOR_NAME = "$Processor";

    /**
     * String to find interface of the class inside the Thrift service that we should bind this service to publicly
     */
    public static String IFACE_NAME = "$Iface";

    /**
     * The client and the server will both attempt to bind to this port, first.
     * <p/>
     * If you override the port in either the client or the server, be sure to change the other!
     */
    public static int DEFAULT_PORT = 1995;

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

    public static Class getThriftServiceInnerClassOrNull(Class thriftServiceClass, String match, boolean isInterface) {
        Class[] declaredClasses = thriftServiceClass.getDeclaredClasses();

        for (Class declaredClass : declaredClasses) {
            if (declaredClass.isInterface()) {
                if (isInterface && declaredClass.getName().contains(match)) {
                    return declaredClass;
                }
            } else {
                if (!isInterface && declaredClass.getName().contains(match)) {
                    return declaredClass;
                }
            }
        }
        return null;
    }

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
