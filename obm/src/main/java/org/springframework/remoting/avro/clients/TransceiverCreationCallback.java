package org.springframework.remoting.avro.clients;


import org.apache.avro.ipc.Transceiver;

import java.net.InetSocketAddress;

/**
 * Interface so that a client may plugin any implementation
 * of the {@link Transceiver} interface while benefiting from the defaults of the
 * {@link org.springframework.remoting.avro.AvroProxyFactoryBean} as well as reducing to truly unique concerns the
 * configuration required
 *
 * @author Josh Long
 */
public interface TransceiverCreationCallback {
    Transceiver buildTransceiver(InetSocketAddress address) throws Exception;
}
