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

package org.springframework.http.converter.thrift;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 *
 * This class is different than the {@link org.apache.thrift.server.TServlet thrift servlet} that ships with
 * Thrift itself. This class lets you use send RESTful requests and responses with Thrift structures as the
 * payloads. This is different from using a servlet for the server-side end of the HTTP-based Thrift RPC transport.
 *
 * This {@link org.springframework.http.converter.HttpMessageConverter} works well with both the {@link org.springframework.web.client.RestTemplate}
 * on the client and and the server-side Spring MVC-based REST support.
 *
 * @author Josh Long
 * @see org.springframework.http.converter.HttpMessageConverter
 * @see org.apache.thrift.server.TServlet
 * @see org.springframework.web.client.RestTemplate
 */

public class ThriftHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements InitializingBean {

	private Log log = LogFactory.getLog(getClass());

	private final Object setup = new Object();

	public static final String MEDIA_TYPE_STRING = "application/x-thrift";

	public static final MediaType MEDIA_TYPE = new MediaType("application", "x-thrift");

	private TDeserializer deserializer;

	private TSerializer serializer;

	public ThriftHttpMessageConverter() {
		super();
		setSupportedMediaTypes(Arrays.asList(MEDIA_TYPE));
	}


	public ThriftHttpMessageConverter(MediaType supportedMediaType) {
		super(supportedMediaType);
	}

	public ThriftHttpMessageConverter(MediaType... supportedMediaTypes) {
		super(supportedMediaTypes);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}


	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		InputStream in = inputMessage.getBody();
		Assert.isTrue(TBase.class.isAssignableFrom(clazz), "the request payload must be a subclas of TBase");
		Class<? extends TBase> tBaseClass = (Class<? extends TBase>) clazz;
		try {
			TBase obj = tBaseClass.newInstance();

			byte[] bytes = FileCopyUtils.copyToByteArray(in);

			this.deserializer.deserialize(obj, bytes);

			return obj;
		} catch (Throwable e) {
			if (log.isErrorEnabled()) {
				log.error("something occurred when trying to TDeserializer#deserialize() the incoming request", e);
			}
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Assert.isInstanceOf(TBase.class, o);
		try {
			TBase tBaseOut = (TBase) o;
			byte[] bytesForObj = this.serializer.serialize(tBaseOut);
			FileCopyUtils.copy(bytesForObj, outputMessage.getBody());
		} catch (Throwable e) {
			if (log.isErrorEnabled()) {
				log.error("something occurred when trying to TSerializer#serialize() the response", e);
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * optional
	 * @param deserializer the {@link TDeserializer Thrift deserializer}
	 */
	public void setDeserializer(TDeserializer deserializer) {
		this.deserializer = deserializer;
	}

	/**
	 * optional
	 * @param serializer the {@link TSerializer Thrift serializer}
	 */
	public void setSerializer(TSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		synchronized (setup) {
			if (serializer == null) {
				this.serializer = new TSerializer();
			}

			if (deserializer == null) {
				this.deserializer = new TDeserializer();
			}
		}
	}
}
