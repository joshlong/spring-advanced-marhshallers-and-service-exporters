/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.remoting.avro.servers;

import org.apache.avro.ipc.Responder;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.Server;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.security.auth.callback.CallbackHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * {@link org.springframework.remoting.avro.AvroExporter} that delegates to Avro's {@link org.apache.avro.ipc.SaslSocketServer},
 * which in turn implements SASL - the simple authentication and security layer.
 * <p/>
 * <P> See  http://en.wikipedia.org/wiki/Simple_Authentication_and_Security_Layer for details.
 *
 * @author Josh Long
 * @see org.springframework.remoting.avro.AvroExporter
 */
public class SaslServerCreationCallback implements ServerCreationCallback {

	private CallbackHandler callbackHandler;
	private String saslMechanism;
	private String saslProtocol;
	private String serverName;
	private Map<String, ?> saslProperties;

	public void setCallbackHandler(CallbackHandler callbackHandler) {
		this.callbackHandler = callbackHandler;
	}

	public void setSaslMechanism(String saslMechanism) {
		this.saslMechanism = saslMechanism;
	}

	public void setSaslProtocol(String saslProtocol) {
		this.saslProtocol = saslProtocol;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setSaslProperties(Map<String, ?> saslProperties) {
		this.saslProperties = saslProperties;
	}


	@Override
	public Server buildServer(InetSocketAddress address, Responder responder) throws Exception {
	  	try {
			boolean fullSasl = StringUtils.hasText(this.saslMechanism) || StringUtils.hasText(this.saslProtocol) ||
					                   StringUtils.hasText(serverName) || callbackHandler != null || saslProperties != null;
			SaslSocketServer saslSocketServer;
			if (fullSasl) {
				if (saslProperties == null) {
					saslProperties = new HashMap<String, Object>();
				}
				saslSocketServer = new SaslSocketServer(responder, address, saslMechanism, saslProtocol, serverName, saslProperties, callbackHandler);
			} else {
				// default, anonymous SASL (nothing wrong with that!)
				saslSocketServer = new SaslSocketServer(responder,  address);
			}
			Assert.notNull(saslSocketServer, "we experienced an error building the server");
			return saslSocketServer;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
