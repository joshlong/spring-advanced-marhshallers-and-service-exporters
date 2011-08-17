package org.springframework.remoting.messagepack;

import org.msgpack.rpc.loop.EventLoop;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Josh Long
 */
public class EventLoopFactoryBean implements FactoryBean<EventLoop>, InitializingBean {
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

	@Override
	public void afterPropertiesSet() throws Exception {

		if (this.setup) {
			return;
		}
		if (workerExecutor == null && ioExecutor == null && scheduledExecutorService == null) {
			eventLoop = EventLoop.defaultEventLoop();
		} else {
			this.eventLoop = EventLoop.start(this.workerExecutor, this.ioExecutor, this.scheduledExecutorService);
		}
		this.setup = true;

	}
}
