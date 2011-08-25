package org.springframework.remoting.avro;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Responder;
import org.apache.avro.ipc.Server;

/**
 * <p/>
 * implementation of {@link AbstractAvroServerExporter} that builds a {@link NettyServer}.
 * <p/>
 * This should be your default choice to expose services in the {@link AbstractAvroServerExporter} tree.
 *
 * @author Josh Long
 */
public class NettyAvroServerExporter extends AbstractAvroServerExporter {
	@Override
	protected Server buildServer(Responder responder) throws Exception {
		return new NettyServer(responder, this.inetSocketAddress);
	}

	@Override
	protected void onInit() throws Exception {
	}

}
