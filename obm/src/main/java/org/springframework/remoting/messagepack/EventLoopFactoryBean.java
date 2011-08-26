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

import org.msgpack.rpc.loop.EventLoop;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Josh Long
 */
public class EventLoopFactoryBean implements FactoryBean<EventLoop>, InitializingBean, DisposableBean {
    private volatile boolean setup = false;
    private ExecutorService workerExecutor;
    private ExecutorService ioExecutor;
    private ScheduledExecutorService scheduledExecutorService;
    private EventLoop eventLoop;

    @Override
    public EventLoop getObject() throws Exception {
        afterPropertiesSet();
        return this.eventLoop;
    }

    @Override
    public Class<?> getObjectType() {
        return EventLoop.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public void setWorkerExecutor(ExecutorService workerExecutor) {
        this.workerExecutor = workerExecutor;
    }

    public void setIoExecutor(ExecutorService ioExecutor) {
        this.ioExecutor = ioExecutor;
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    private int ioThreads = 10;
    private int workerThreads = 10;
    private int scheduledThreads = 10;

    @Override
    public void afterPropertiesSet() throws Exception {

        if (this.setup) {
            return;
        }

        if (this.workerExecutor == null) {
            this.workerExecutor = Executors.newFixedThreadPool(this.workerThreads);
        }

        if (this.ioExecutor == null) {
            this.ioExecutor = Executors.newFixedThreadPool(this.ioThreads);
        }

        if (this.scheduledExecutorService == null) {
            this.scheduledExecutorService = Executors.newScheduledThreadPool(this.scheduledThreads);
        }

        this.eventLoop = EventLoop.start(this.workerExecutor, this.ioExecutor, this.scheduledExecutorService);

        this.setup = true;

    }


    @Override
    public void destroy() throws Exception {
        eventLoop.shutdown();
    }
}
