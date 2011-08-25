package org.springframework.remoting.avro;

import org.apache.avro.ipc.Responder;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;

/**
 * <p>Provides convenience 'getter' to retrieve the constructed {@link Responder}  </p>
 *
 * @author Josh Long
 */
abstract public class AbstractAvroExporter extends RemoteExporter implements InitializingBean {
	public Responder getResponder() {
		return new SpecificResponder(getServiceInterface(), getService());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}
}
