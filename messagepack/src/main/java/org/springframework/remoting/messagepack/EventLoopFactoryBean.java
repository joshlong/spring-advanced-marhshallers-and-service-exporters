package org.springframework.remoting.messagepack;

import org.msgpack.rpc.loop.EventLoop;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Josh Long
 *
 */
public class EventLoopFactoryBean implements FactoryBean<EventLoop> {

	@Override
	public EventLoop getObject() throws Exception {
		return EventLoop.defaultEventLoop();
	}

	@Override
	public Class<?> getObjectType() {
		return EventLoop .class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
}
