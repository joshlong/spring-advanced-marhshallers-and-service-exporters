package org.springframework.remoting.avro.clients;

import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.Transceiver;

import java.net.InetSocketAddress;

public class NettyTransceiverCreationCallback implements TransceiverCreationCallback{

	@Override
	public Transceiver buildTransceiver(InetSocketAddress address) throws Exception {
	 return new NettyTransceiver(address);
	}
}
