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
			encoder = encoderFactory.jsonEncoder(this.schema, this.outputStream) ;
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
