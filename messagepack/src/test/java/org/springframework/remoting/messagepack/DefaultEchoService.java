package org.springframework.remoting.messagepack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class DefaultEchoService implements EchoService {


	private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

	private Log log = LogFactory.getLog(getClass());

	@Override
	public String alarm(String msg) {
		return (msg == null ? "" : msg).toUpperCase() + "!!";
	}

	public DefaultEchoService() {
		executor.afterPropertiesSet();
	}

	@Override
	public Cat fetch() {
		Cat garfield = new Cat("Garfield", 53);
		Cat nermel = new Cat("Nermel", 12);
		Cat george = new Cat("George", 42);
		garfield.addFriend(nermel);
		garfield.addFriend(george);

		Human john = new Human("John");
		Human mary = new Human("Mary");

		garfield.addHuman(john);
		garfield.addHuman(mary);

		return garfield;
	}

	/**
	 * nb: this is not part of the interface, but MessagePack will attempt to call it if it's available.
	 * <p/>
	 * The {@link org.msgpack.rpc.Request} parameter's used to give the callee a chance to handle the request asynchronously or to leverage the error handling mechanism.
	 *
	 * @param request object required to facilitate communication
	 * @param in      the string to send back
	 */
	public void hello(final org.msgpack.rpc.Request request, final String in) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(10 * 1000);
					if (log.isDebugEnabled()) {
						log.debug("sleeping for 10s");
					}
					request.sendResult("Hello, " + in);
				} catch (Throwable thro) {
					if (log.isErrorEnabled()) {
						log.error(thro);
					}
				}
			}
		});
	}

	@Override
	public String echo(String in) {
		return "No, " + in;
	}
}