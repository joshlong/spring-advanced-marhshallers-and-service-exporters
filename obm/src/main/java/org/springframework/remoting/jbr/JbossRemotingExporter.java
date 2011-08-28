package org.springframework.remoting.jbr;

import org.jboss.remoting.InvocationRequest;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.ServerInvocationHandler;
import org.jboss.remoting.ServerInvoker;
import org.jboss.remoting.callback.InvokerCallbackHandler;
import org.jboss.remoting.transport.Connector;
import org.jboss.remoting.transporter.TransporterServer;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.management.MBeanServer;
import java.util.HashMap;
import java.util.Map;

/**
 * <P> Exporter that delegates to the JBoss Remoting (http://community.jboss.org/en/jbossremoting) project to handle the RPC support. <p/>
 *
 * @author Josh Long
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 */
public class JbossRemotingExporter extends RemoteExporter implements InitializingBean, BeanNameAware, SmartLifecycle {

    private TransporterServer server;

    private volatile boolean running = false;

    // configuration to use in setting up the server
    private Map<String,String> configuration  = new HashMap<String, String>() ;

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

    /*protected void setupServer(String locatorURI) throws Exception {
        InvokerLocator locator = new InvokerLocator(locatorURI);
        if (logger.isDebugEnabled()) {
            logger.debug("Starting remoting server with locator uri of: " + locatorURI);
        }

        Connector connector = new Connector();
        connector.setInvokerLocator(locator.getLocatorURI());
        connector.create();

        JbossRemotingExporterInvocationHandler invocationHandler = new JbossRemotingExporterInvocationHandler();
        // first parameter is sub-system name.  can be any String value.
        connector.addInvocationHandler(beanName, invocationHandler);

        connector.start();
    }
*/
    static class JbossRemotingExporterInvocationHandler implements ServerInvocationHandler {

        @Override
        public void setMBeanServer(MBeanServer mBeanServer) {

        }

        @Override
        public void setInvoker(ServerInvoker serverInvoker) {
        }

        @Override
        public Object invoke(InvocationRequest invocationRequest) throws Throwable {
            return null;
        }

        @Override
        public void addListener(InvokerCallbackHandler invokerCallbackHandler) {
        }
        @Override
        public void removeListener(InvokerCallbackHandler invokerCallbackHandler) {
        }

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

       this.server = TransporterServer.createTransporterServer(this.url, this, serverName, this.configuration, this.clustered);

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
            Assert.notNull(this.server, "The server is not created. Did you call #afterPropertiesSet()?");
            server.start();
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
        Assert.notNull(this.server, "The server is not created. Did you call #afterPropertiesSet()?");
        this.server.stop();
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
}
