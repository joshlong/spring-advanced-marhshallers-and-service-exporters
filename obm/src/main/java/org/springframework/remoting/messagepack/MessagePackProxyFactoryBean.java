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

package org.springframework.remoting.messagepack;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.msgpack.rpc.Client;
import org.msgpack.rpc.config.ClientConfig;
import org.msgpack.rpc.loop.EventLoop;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.obm.messagepack.support.MessagePackRegistrar;
import org.springframework.obm.messagepack.util.MessagePackUtils;
import org.springframework.remoting.support.RemoteAccessor;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Used to create client side proxies that can communicate with the remote services.
 * <p/>
 * The interface used on the client does <EM>not</EM> need to match the interface exposed on the server.
 *
 * @author Josh Long
 * @see org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean
 * @see org.springframework.remoting.rmi.RmiProxyFactoryBean
 */
public class MessagePackProxyFactoryBean<T> extends RemoteAccessor implements FactoryBean<T>, BeanClassLoaderAware, InitializingBean {

    private Log log = LogFactory.getLog(getClass());
    private boolean remapResults = true;

    private boolean exportServiceParameters = true;
    private Set<Class> classes = new HashSet<Class>();
    private boolean serializeJavaBeanProperties = true;

    private MessagePackRegistrar registrar = new MessagePackRegistrar();

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

    /**
     * When {@link org.msgpack.MessagePack} sends back the object graph, it comes in as the first level object,
     * then any relationships come in as collections of {@link org.msgpack.MessagePackObject}, not the original type.
     * <p/>
     * Well, using some heuristics (specifically, w.r.t. to generics), we can automatically remap these results onto your domain
     * POJOs for you, transparently, before you get the results.
     */
    private MethodInterceptor objectGraphCleaningMethodInterceptor = new MethodInterceptor() {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            if (log.isDebugEnabled()) {
                log.debug("the client called: " + invocation.getMethod().toGenericString());
            }
            return MessagePackUtils.remapResult(invocation.proceed());
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {

        registrar.setSerializeJavaBeanProperties(this.serializeJavaBeanProperties);
        if (exportServiceParameters) {
            registrar.discoverClasses(getServiceInterface());
        }
        registrar.registerClasses(this.classes);
        registrar.afterPropertiesSet();

        if (eventLoop == null) {
            this.eventLoop = new EventLoopFactoryBean().getObject();
        }
        if (clientConfig != null) {
            client = new Client(this.host, this.port, this.clientConfig, this.eventLoop);
        } else {
            client = new Client(this.host, this.port, this.eventLoop);
        }

        Assert.notNull(this.client, "the client can't be null");
        Assert.notNull(this.host, "the host can't be null");

        Object p = client.proxy(getServiceInterface());

        if (remapResults) {
            ProxyFactory factory = new ProxyFactory(p);
            factory.addInterface(getServiceInterface());
            factory.addAdvice(objectGraphCleaningMethodInterceptor);
            p = (T) factory.getProxy(getBeanClassLoader());
        }
        this.proxy = (T) p;

        Assert.notNull(this.proxy, "the proxy can't be null");

    }

    /**
     * Should the results be re-built based on heuristics designed to capture the intent of the code
     *
     * @param remapResults
     */
    public void setRemapResults(boolean remapResults) {
        this.remapResults = remapResults;
    }


    public void setExportServiceParameters(boolean exportServiceParameters) {
        this.exportServiceParameters = exportServiceParameters;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public void setSerializeJavaBeanProperties(boolean serializeJavaBeanProperties) {
        this.serializeJavaBeanProperties = serializeJavaBeanProperties;
    }

}
