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
public class EventLoopFactoryBean implements FactoryBean<EventLoop>, InitializingBean , DisposableBean {
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

	private int ioThreads = 10 ;
	private int workerThreads = 10 ;
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
			this.ioExecutor = Executors.newFixedThreadPool( this.ioThreads );
		}

		if (this.scheduledExecutorService == null) {
			this.scheduledExecutorService = Executors.newScheduledThreadPool( this.scheduledThreads);
		}

		this.eventLoop = EventLoop.start(this.workerExecutor, this.ioExecutor, this.scheduledExecutorService);

		this.setup = true;

	}


	@Override
	public void destroy() throws Exception {
	  eventLoop.shutdown();
	}
}
