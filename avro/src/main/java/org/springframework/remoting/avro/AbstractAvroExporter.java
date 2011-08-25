package org.springframework.remoting.avro;

import org.apache.avro.ipc.Responder;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.springframework.remoting.support.RemoteExporter;

public class AbstractAvroExporter extends RemoteExporter  {

	protected Responder getResponder() {
		return new SpecificResponder(getServiceInterface(), getService());
	}
}
