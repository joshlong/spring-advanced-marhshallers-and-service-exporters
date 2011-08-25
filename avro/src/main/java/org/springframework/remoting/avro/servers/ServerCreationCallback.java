package org.springframework.remoting.avro.servers;

import org.apache.avro.ipc.Responder;
import org.apache.avro.ipc.Server;

import java.net.InetSocketAddress;

/**
 * Callback to allow clients to tailor which server is used.
 * @author Josh Long
 */
public interface ServerCreationCallback {
	Server buildServer(InetSocketAddress address, Responder responder  ) throws Exception ;
}
