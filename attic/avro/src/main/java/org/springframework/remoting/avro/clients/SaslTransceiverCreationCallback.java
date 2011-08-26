package org.springframework.remoting.avro.clients;

import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;

import java.net.InetSocketAddress;

/**
 * Implements {@link TransceiverCreationCallback} which in turn
 *
 * @author Josh Long
 */
public class SaslTransceiverCreationCallback implements TransceiverCreationCallback{
	@Override
	public Transceiver buildTransceiver(InetSocketAddress address) throws Exception {
		return new SaslSocketTransceiver(address);
	}
}
