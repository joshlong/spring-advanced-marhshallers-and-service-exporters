package org.springframework.remoting.jbr;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jboss.remoting.transporter.TransporterClient;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteAccessor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Supports proxying clients for JBoss Remoting.
 * <p/>
 * There are two different types of serialization
 * <p/>
 * the default, JBoss, uses a special serialization library from JBoss that
 * <ul>
 * <LI> Eschews the need to implement {@link java.io.Serializable}</LI>
 * <LI> Eschews the need to </LI>
 * </ul>
 * <p/>
 * http://www.theserverlabs.com/blog/2009/02/19/jboss-remoting-jboss-serialization-kills-javarmi-and-spring-remoting/
 *
 * @author Josh Long
 */
public class JbossRemotingProxyFactoryBean<T> extends RemoteAccessor implements InitializingBean, BeanNameAware, MethodInterceptor, FactoryBean<T> {

    private JbossSerialization serializationType = JbossSerialization.JBOSS;

    private String transport = "socket";

    private String host = "127.0.0.1";

    private int port = 5400;

    private Object client;

    private T serviceProxy;

    private String serverName;

    private boolean clustered;

    private String beanName;

    private String url;

    public void setSerializationType(JbossSerialization serializationType) {
        this.serializationType = serializationType;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.hasText(this.serverName)) {
            this.serverName = this.beanName;
        }

        if (!StringUtils.hasText(this.url)) {
            this.url = buildUrl();
        }

        if (logger.isDebugEnabled()) {
            logger.debug(getServiceInterface() + " client URL is " + this.url);
        }

        Class<T> clientInterface = getServiceInterface();

        // build the jbr client
        this.client = (T) TransporterClient.createTransporterClient(this.url, clientInterface);
        Assert.isInstanceOf(getServiceInterface(), this.client);

        // build the object that'll manage calls from the client to that jbr proxy
        serviceProxy = (T) new ProxyFactory(clientInterface, this).getProxy(getBeanClassLoader());

    }


    protected String buildUrl() {
        Assert.hasText(this.transport);
        Assert.hasText(this.host);
        Assert.notNull(this.serializationType, "you must specify a serialization type (" +
                                                       JbossSerialization.JBOSS.name() + "," +
                                                       JbossSerialization.JAVA.name() + ")");
        Assert.isTrue(this.port > 0, "you must specify a non-zero port");
        return transport + "://" + host + ":" + port + "/?serializationtype=" + serializationType.name().toLowerCase();
    }


    @Override
    public T getObject() throws Exception {
        return serviceProxy;
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
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // now, unless i miss my mark, the interface of this proxy and the proxy we've created locally are the same,
        // so it should be a simple matter to forward the MethodInvocation on to the local client
        Method method = invocation.getMethod();

        if (method.getName().equals("toString")) {
            return "proxy for " + getServiceInterface();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("invoking " + invocation.toString() + " on the client proxy");
        }

        // find the same method on the client
        if (logger.isDebugEnabled()) {
            for (Method m : client.getClass().getMethods()) {
                logger.debug("discovered " + m.toGenericString() + " on the client proxy.");
            }
        }


        Assert.isInstanceOf( getServiceInterface(), this.client );
        Method clientMethodSpecifically = this.client.getClass().getMethod( method.getName(),  invocation.getMethod().getParameterTypes()) ;
        Assert.notNull(clientMethodSpecifically);
        return clientMethodSpecifically.invoke( this.client, invocation.getArguments());
        //return res ;

        /*Method clientMethod = ReflectionUtils.findMethod( client, method.getName(), method.getParameterTypes());

        if (null != clientMethod) {
            logger.debug("found method : " + clientMethod.getName());
        }


        if (null != clientMethod) {
            return clientMethod.invoke(this.client, invocation.getArguments());
        }
*/
        ///    return method.invoke(this.client, invocation.getArguments());
    }


}
