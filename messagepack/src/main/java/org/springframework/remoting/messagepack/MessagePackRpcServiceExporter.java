package org.springframework.remoting.messagepack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.msgpack.rpc.Server;
import org.msgpack.rpc.config.ClientConfig;
import org.msgpack.rpc.dispatcher.MethodDispatcher;
import org.msgpack.rpc.loop.EventLoop;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link RemoteInvocationBasedExporter} based on the {@link org.msgpack.MessagePack} RPC framework.
 *
 * @author Josh Long
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 * @see org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter
 */
public class MessagePackRpcServiceExporter extends RemoteInvocationBasedExporter implements InitializingBean {

	private Log log = LogFactory.getLog(getClass());

	private Server server;
	private ClientConfig clientConfig;
	private String listenHost = "127.0.0.1";
	private int listenPort = 1995;

	private EventLoop eventLoop;

	public void setHost(String lh) {
		if (StringUtils.hasText(lh)) {
			this.listenHost = lh;
		}
	}

	public void setPort(int lp) {
		if (lp > 0) {
			this.listenPort = lp;
		}
	}

	public void setEventLoop(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
	}

	public void setServer(Server server) {
		this.server = server;
	}


	public void setClientConfig(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	protected Server buildServer() {
		Server svr;
		if (clientConfig != null) {
			if (log.isDebugEnabled()) {
				log.debug("using user provided clientConfig.");
			}
			svr = new Server(clientConfig, eventLoop);
		} else {
			svr = new Server(eventLoop);
		}
		return svr;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Object service = getService();
		Class<?> serviceInterface = getServiceInterface();

		Assert.notNull(service, "the service target can NOT be null!");

		if (eventLoop == null) {
			EventLoopFactoryBean eventLoopFactoryBean = new EventLoopFactoryBean();
			eventLoop = eventLoopFactoryBean.getObject();
			if (log.isDebugEnabled()) {
				log.debug("using EventLoop#defaultEventLoop(). " +
						          "Consider using an existing EventLoop," +
						          " or using a " + EventLoopFactoryBean.class.getName());
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("using existing " + EventLoop.class.getName());
			}
		}

		if (server == null) {
			server = buildServer();
		}

		Assert.notNull(this.server, "the server must not be null");

		MethodDispatcher dispatcher = null;
		if (serviceInterface != null) {
			if (log.isDebugEnabled()) {
				log.debug("service interface (" + serviceInterface.getName() + ") was provided. Using to limit the methods exposed.");
			}
			dispatcher = new MethodDispatcher(service, serviceInterface);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("no service interface was provided. Exposing target object directly.");
			}
			dispatcher = new MethodDispatcher(service);
		}

		server.serve(dispatcher);
		server.listen(this.listenHost, this.listenPort);

		eventLoop.join();

	}
}
