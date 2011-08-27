/*
 * Copyright 2011 the original author or authors.
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

package org.springframework.util.http;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.springframework.util.Assert;

/**
 * provides support for launching a Jetty {@link Server} instance. Users may implement {@link JettyContextConfigurationCallback}
 * to tailor the deployed web applications in the Jetty context as required.
 *
 * @author Josh Long
 * @see DispatcherServletJettyConfigurationCallback
 * @see Server
 */
abstract public class EndpointTestUtils {

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
