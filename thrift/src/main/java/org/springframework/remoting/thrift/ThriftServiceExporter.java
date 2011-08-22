/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.remoting.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.converter.thrift.ThriftHttpMessageConverter;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Does the same thing as {@link ThriftExporter}, except that its hosted by a servlet container.
 * <p/>
 * Based very much on the {@link org.apache.thrift.server.TServlet existing servlet adapter for Thrift}, except made available
 * as a bean that can be managed by the Spring container.
 *
 * @author Josh Long
 * @see org.apache.thrift.server.TServlet
 */
public class ThriftServiceExporter implements HttpRequestHandler, InitializingBean {

	private TProcessor processor;
	private TProtocolFactory inProtocolFactory;
	private TProtocolFactory outProtocolFactory;
	private final Collection<Map.Entry<String, String>> customHeaders = new ArrayList<Map.Entry<String, String>>();

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TTransport inTransport = null;
		TTransport outTransport = null;

		try {
			response.setContentType(ThriftHttpMessageConverter.MEDIA_TYPE_STRING);

			if (null != this.customHeaders) {
				for (Map.Entry<String, String> header : this.customHeaders) {
					response.addHeader(header.getKey(), header.getValue());
				}
			}
			InputStream in = request.getInputStream();
			OutputStream out = response.getOutputStream();

			TTransport transport = new TIOStreamTransport(in, out);
			inTransport = transport;
			outTransport = transport;

			TProtocol inProtocol = inProtocolFactory.getProtocol(inTransport);
			TProtocol outProtocol = outProtocolFactory.getProtocol(outTransport);

			processor.process(inProtocol, outProtocol);
			out.flush();
		} catch (TException te) {
			throw new ServletException(te);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.processor == null) {
			//todo  whats a sane default here?

		}
		if (this.inProtocolFactory == null) {
			// todo whats a sane default here?
		}
		if (this.outProtocolFactory == null) {
			this.outProtocolFactory = this.inProtocolFactory;
		}

	}

	public void setProcessor(TProcessor processor) {
		this.processor = processor;
	}

	public void setInProtocolFactory(TProtocolFactory inProtocolFactory) {
		this.inProtocolFactory = inProtocolFactory;
	}

	public void setOutProtocolFactory(TProtocolFactory outProtocolFactory) {
		this.outProtocolFactory = outProtocolFactory;
	}

	public void setCustomHeaders(Collection<Map.Entry<String, String>> headers) {
		this.customHeaders.clear();
		this.customHeaders.addAll(headers);
	}
}
