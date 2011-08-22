package org.springframework.remoting.thrift;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;

/**
 * Simple service exporter to automatically export Thrift services
 *
 * @author Josh Long
 */
public class ThriftRpcServiceExporter extends RemoteInvocationBasedExporter implements InitializingBean , SmartLifecycle {

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public int getPhase() {
		return 0;
	}
}
