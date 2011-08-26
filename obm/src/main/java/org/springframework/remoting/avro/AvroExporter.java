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
package org.springframework.remoting.avro;


import org.apache.avro.ipc.Server;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.remoting.avro.servers.NettyServerCreationCallback;
import org.springframework.remoting.avro.servers.ServerCreationCallback;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;

/**
 * {@link RemoteExporter} for Avro's {@link org.apache.avro.Protocol protocols}.
 * This {@link RemoteExporter} builds an instance of Avro's {@link Server}, and then starts it up
 * and configures it to expose your service bean. If you would rather let a web container
 * handle the server duties, then use the {@link AvroServiceExporter}, which can be used in any
 * {@link org.springframework.web.context.WebApplicationContext}
 *
 * @author Josh Long
 * @see AvroServiceExporter
 */
public class AvroExporter extends AbstractAvroExporter implements InitializingBean, SmartLifecycle {

    private volatile boolean running = false;
    private final Object monitor = new Object();
    private volatile boolean setup = false;
    private Server server;
    protected InetSocketAddress inetSocketAddress;
    private ServerCreationCallback serverCreationCallback;

    protected int port = 2003;


    public void setServerCreationCallback(ServerCreationCallback serverCreationCallback) {
        this.serverCreationCallback = serverCreationCallback;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public final void afterPropertiesSet() throws Exception {
        synchronized (this.monitor) {
            if (setup) {
                return;
            }

            if (inetSocketAddress == null) {
                Assert.isTrue(port > 0, "the port must be greater than 0");
                inetSocketAddress = new InetSocketAddress(this.port);
            }

            if (this.serverCreationCallback == null) {
                serverCreationCallback = new NettyServerCreationCallback();
            }

            server = this.serverCreationCallback.buildServer(this.inetSocketAddress, getResponder());
            Assert.notNull(this.server, "the server was not properly constructed!");
            setup = true;
        }
    }


    @Override
    public boolean isAutoStartup() {
        return true;
    }


    @Override
    public void stop() {
        // shut it down
        this.running = false;
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
        Assert.notNull(this.server, "the service is null");
        server.start();
        running = true;
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
