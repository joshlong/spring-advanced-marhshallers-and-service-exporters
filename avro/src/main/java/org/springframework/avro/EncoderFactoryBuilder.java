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
package org.springframework.avro;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.springframework.util.Assert;

import java.io.OutputStream;

/**
 * Convenience class to build an {@link Encoder}
 *
 * @author Josh Long
 */
public class EncoderFactoryBuilder {
	private final static ThreadLocal<Encoder> encoderThreadLocal = new ThreadLocal<Encoder>();
	private final EncoderFactory encoderFactory = new EncoderFactory();
	private OutputStream outputStream;
	private boolean useBinary = true;
	private boolean validate = false;
	private boolean useJson = false;
	private Schema schema;

	public EncoderFactoryBuilder setUseBinary(boolean useBinary) {
		this.useBinary = useBinary;
		return this;
	}

	public EncoderFactoryBuilder setValidate(boolean validate) {
		this.validate = validate;
		return this;
	}

	public EncoderFactoryBuilder setUseJson(boolean useJson) {
		this.useJson = useJson;
		return this;
	}

	public EncoderFactoryBuilder setSchema(Schema schema) {
		this.schema = schema;
		return this;
	}

	public EncoderFactoryBuilder setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
		return this;
	}

	protected Encoder build(Encoder reuse) throws Exception {
		Encoder encoder = null;

		if (useBinary) {
			Assert.notNull(this.outputStream, "you've selected to use a binary encoder. Please provide an output stream to encode to by setting the 'outputStream' property");
			encoder = encoderFactory.binaryEncoder(this.outputStream, (BinaryEncoder) reuse);
		} else if (useJson) {
			Assert.notNull(this.outputStream, "you've selected to use a JSON encoder. Please provide an output stream to encode to by setting the 'outputStream' property");
			encoder = encoderFactory.jsonEncoder(this.schema, this.outputStream);
		}
		Assert.notNull(encoder, "could not build an encoder. Did you set both 'useJson' and 'useBinary' to false?");
		if (validate) {
			Assert.notNull(this.schema, "you've selected to validate. Please provide a target schema by setting the 'schema' property");
			encoder = encoderFactory.validatingEncoder(this.schema, encoder);
		}
		return encoder;
	}


	protected Encoder buildThreadAwareEncoder() throws Exception {
		Encoder encoder = encoderThreadLocal.get();
		encoderThreadLocal.set(build(encoder));
		return encoderThreadLocal.get();
	}

	public Encoder build() throws Exception {
		return buildThreadAwareEncoder();
	}
}
