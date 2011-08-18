package org.springframework.remoting.messagepack;


/**
 * interface that requires support for broadcasting (in all-caps, no less!) a given message.
 */
public interface AlarmService {

	String alarm(String msg);
}
