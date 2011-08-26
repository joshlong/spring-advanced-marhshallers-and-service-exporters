package org.springframework.util;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;

/**
 * @author Josh Long
 */
abstract public class EndpointTestUtils {


    public static interface JettyContextConfigurationCallback {
        void configure(Context c) throws Exception;
    }

    /**
     * Launches a Jetty server and then preconfigures Spring's web infrastructure and the bootstraps the
     * configuration object that's passed in as a parameter
     *
     * @param bindAddress the address to which to bind the server (can be null)
     */
    static public Server serve(String bindAddress, int port, JettyContextConfigurationCallback c) throws Throwable {
        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setLowResourceMaxIdleTime(10000);
        connector.setAcceptQueueSize(128);
        connector.setResolveNames(false);
        connector.setUseDirectBuffers(false);
        if (bindAddress != null) {
            connector.setHost(bindAddress);
        }
        connector.setPort(port);
        server.addConnector(connector);
        Assert.notNull(c, "the " + JettyContextConfigurationCallback.class.getSimpleName() + " instance can't be null ");
        c.configure(new Context(server, "/"));
        return server;
    }

    static public Server serve(JettyContextConfigurationCallback callback) throws Throwable {
        return serve(null, 8080, callback);
    }


}
