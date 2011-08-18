package org.springframework.remoting.messagepack;

/**
 * @author Josh Long
 */
public interface EchoService extends AlarmService {
	String echo(String in);

	Cat fetch();

}
