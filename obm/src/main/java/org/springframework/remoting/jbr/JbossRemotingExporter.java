package org.springframework.remoting.jbr;

import org.apache.commons.logging.LogFactory;
import org.jboss.remoting.InvocationRequest;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.ServerInvocationHandler;
import org.jboss.remoting.ServerInvoker;
import org.jboss.remoting.callback.InvokerCallbackHandler;
import org.jboss.remoting.invocation.NameBasedInvocation;
import org.jboss.remoting.transport.Connector;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.management.MBeanServer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <P> Exporter that delegates to the JBoss Remoting (http://community.jboss.org/en/jbossremoting) project to handle the RPC support. <p/>
 *
 * @author Josh Long
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 */
public class JbossRemotingExporter extends RemoteExporter implements InitializingBean, BeanNameAware, SmartLifecycle {

    //  private TransporterServer server;

    private volatile boolean running = false;

    // configuration to use in setting up the server
    private Map<String, String> configuration = new HashMap<String, String>();

    // the type of serialization - two values:'jboss' and 'java'
    private JbossSerialization serializationType = JbossSerialization.JBOSS;
    private String host = "127.0.0.1";
    private String serverName;
    private boolean clustered;
    private int port = 5400;
    private String beanName;
    private String url;
    private String transport = "socket";

    private Connector connector;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    protected Connector setupServer(String locatorUrl) throws Exception {
        InvokerLocator locator = new InvokerLocator(locatorUrl);
        if (logger.isDebugEnabled()) {
            logger.debug("Starting remoting server with locator uri of: " + locatorUrl);
        }
        Connector connector = new Connector(locator);
        connector.create();

        ReflectiveInvocationHandler invocationHandler = new ReflectiveInvocationHandler(getService());
        connector.addInvocationHandler(beanName, invocationHandler);
        return connector;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.beanName, "the 'beanName' can't be null");
        if (!StringUtils.hasText(this.serverName)) {
            this.serverName = this.beanName;
        }
        if (!StringUtils.hasText(this.url)) {
            this.url = buildUrl();
        }
        connector = setupServer(this.url);
    }

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

    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        if (null != callback) {
            callback.run();
        }
    }

    @Override
    public void start() {
        try {
            connector.start();
            this.running = true;

            if (logger.isDebugEnabled()) {
                logger.debug("just server#start()'d the exporter.");
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("could not start the server for url '%s'", this.url), e);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        connector.stop();
        if (logger.isDebugEnabled()) {
            logger.debug("stopping the exporter.");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 0;
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


    /**
     * <P> This {@link ServerInvocationHandler} crawls every method on the {@link #service} and
     * constructs a {@link Map} which has as its keys a value object ({@link MethodNameAndArguments}), and
     * as its key the correct method to call on the target {@link #service} object, cached.
     * </p>
     * <P> The {@link MethodNameAndArguments} value object simply contains the String forms of
     * the method to invoke and the names of the types of the method signature (e.g., {"long","a.Class"}).
     * </P>
     * <P> This is a best faith effort, and no guarantees are made as to the correct resolution of the methods.
     * To avoid any complications (and this advice is valid for most RPC systems, I think you'll find),
     * prefer methods with unique signatures. Method overloading and var-args don't translate well.
     * </p>
     *
     * @author Josh Long
     */
    public static class ReflectiveInvocationHandler implements ServerInvocationHandler {
        Object service;
        org.apache.commons.logging.Log log = LogFactory.getLog(getClass());
        Map<MethodNameAndArguments, Method> methodMap = new ConcurrentHashMap<MethodNameAndArguments, Method>();

        public ReflectiveInvocationHandler(Object target) throws Exception {
            this.service = target;
            buildMapOfMethods(this.service);
        }

        /**
         * this is a simple value object that we use to create a unique key for every method that
         * can be retrieved safely from a JDK collection because the {@link #equals(Object)} implementation
         * is stable / predictable.
         */
        static private class MethodNameAndArguments {
            private String methodName;
            private String[] classOfArguments;

            @Override
            public String toString() {
                return "MethodNameAndArguments{" +
                               "methodName='" + methodName + '\'' +
                               ", classOfArguments=" + (classOfArguments == null ? null : Arrays.asList(classOfArguments)) +
                               '}';
            }

            private MethodNameAndArguments(String methodName, String[] classOfArguments) {
                this.methodName = methodName;
                this.classOfArguments = classOfArguments;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }

                MethodNameAndArguments that = (MethodNameAndArguments) o;

                return Arrays.equals(classOfArguments, that.classOfArguments) && !(methodName != null ? !methodName.equals(that.methodName) : that.methodName != null);

            }

            @Override
            public int hashCode() {
                int result = methodName != null ? methodName.hashCode() : 0;
                result = 31 * result + (classOfArguments != null ? Arrays.hashCode(classOfArguments) : 0);
                return result;
            }
        }

        /**
         * convenience method to build a {@link MethodNameAndArguments} value object given a unique
         * {@link NameBasedInvocation}.
         *
         * @param i the invocation
         * @return the value object that - for any two invocations of the same method on the server
         *         and given the same argument types - should be {@link #equals(Object) equal to one another}.
         */
        MethodNameAndArguments fromNameBasedInvocation(NameBasedInvocation i) {
            Class[] classes = new Class[i.getParameters().length];

            int indx = 0;

            for (Object p : i.getParameters()) {
                classes[indx++] = p.getClass();
            }

            String[] clzzNames = fromClasses(classes);

            String methodName = i.getMethodName();

            if (log.isDebugEnabled()) {
                log.debug("searching for " + methodMap);
            }

            return new MethodNameAndArguments(methodName, clzzNames);
        }

        /**
         * Turn a collection of {@link Class classes} into a collection of {@link String strings}.
         *
         * @param clzz the classes
         * @return the array of strings
         */
        String[] fromClasses(Class[] clzz) {
            Assert.notNull(clzz, "the classes can't be null");
            String[] classes = new String[clzz.length];

            int indx = 0;

            for (Class p : clzz) {
                classes[indx++] = p.getClass().getName();
            }
            return classes;
        }

        MethodNameAndArguments fromMethod(Method m) {
            return new MethodNameAndArguments(m.getName(), fromClasses(m.getParameterTypes()));
        }

        void buildMapOfMethods(Object svc) throws Exception {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(svc.getClass());
            for (Method m : methods) {
                methodMap.put(fromMethod(m), m);
            }
        }

        Method findWinningMethod(Object s, NameBasedInvocation parameter) throws Throwable {
            MethodNameAndArguments nameAndArguments = fromNameBasedInvocation(parameter);
            return methodMap.get(nameAndArguments);
        }

        public Object invoke(InvocationRequest invocation) throws Throwable {
            Object parm = invocation.getParameter();
            Assert.isInstanceOf(NameBasedInvocation.class, parm);
            NameBasedInvocation parameter = (NameBasedInvocation) invocation.getParameter();
            Method method = findWinningMethod(service, parameter);
            Assert.notNull(method, "we couldn't find the method to invoke!");
            return method.invoke(this.service, ((NameBasedInvocation) invocation.getParameter()).getParameters());
        }

        public void addListener(InvokerCallbackHandler callbackHandler) {
        }

        public void removeListener(InvokerCallbackHandler callbackHandler) {
        }

        public void setMBeanServer(MBeanServer server) {
        }

        public void setInvoker(ServerInvoker invoker) {
        }

    }
}
