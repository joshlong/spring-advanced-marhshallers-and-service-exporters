package org.springframework.http.converter.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.springframework.avro.DecoderFactoryBuilder;
import org.springframework.avro.EncoderFactoryBuilder;
import org.springframework.avro.SchemaFactoryBean;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Arrays;

/**
 * This {@link org.springframework.http.converter.HttpMessageConverter} provides first class support for the {@link org.springframework.web.client.RestTemplate}
 * REST client and the server side machinery for producing REST services in Spring MVC.
 *
 * @author Josh Long
 * @see org.springframework.http.converter.HttpMessageConverter
 * @see org.springframework.web.client.RestTemplate
 */
public class AvroHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

	public static final String MEDIA_TYPE_STRING = "application/x-avro";

	public static final MediaType MEDIA_TYPE = new MediaType("application", "x-avro");

	private boolean validate = false;

	/**
	 * dictates whether the {@link org.apache.avro.io.Encoder encoders} and {@link Decoder decoders} will
	 * be wrapped in a {@link org.apache.avro.io.ValidatingDecoder} or {@link org.apache.avro.io.ValidatingEncoder}
	 *
	 * @param validate whether or not to validate
	 */
	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		try {
			Assert.notNull(clazz, "the class must not be null");
			Schema s = new SchemaFactoryBean(clazz).getObject();
			boolean supports = s != null;

			if(logger.isDebugEnabled())
			logger.debug("returning " + supports + " for class " + clazz.getName());

			return supports;
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("exception when trying to test whether the class " + clazz.getName() + " has an Avro schema");
			}
			return false;
		}
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		try {

			Assert.notNull(clazz, "the class must not be null");

			Schema schema = new SchemaFactoryBean(clazz).getObject();
			Assert.notNull(schema, "the schema must not be null");

			GenericDatumReader reader = new GenericDatumReader(schema);

			Object old = clazz.newInstance();

			Decoder decoder = new DecoderFactoryBuilder()
					                  .setInputStream(inputMessage.getBody())
					                  .setUseBinary(true)
					                  .setSchema(schema)
					                  .setValidate(this.validate)
					                  .build();

			return reader.read(old, decoder);


		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("exception when trying to test whether the class " + clazz.getName() + " has an Avro schema");
			}
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		try {

			Assert.notNull(obj, "the object to encode must not be null");

			Schema schema = new SchemaFactoryBean(obj.getClass()).getObject();
			Assert.notNull(schema, "the schema must not be null");

			GenericDatumWriter writer = new GenericDatumWriter(schema);


			Encoder encoder = new EncoderFactoryBuilder()
					                  .setOutputStream(outputMessage.getBody())
					                  .setSchema(schema)
					                  .setUseBinary(true)
					                  .setValidate(this.validate)
					                  .build();
			writer.write(obj, encoder);
			encoder.flush();
//			outputMessage.getBody().flush();
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("exception when trying to test whether the class " + obj.getClass().getName() + " has an Avro schema");
			}
			throw new RuntimeException(e);
		}
	}


	public AvroHttpMessageConverter() {
		super();
		setSupportedMediaTypes(Arrays.asList(MEDIA_TYPE));
	}


	public AvroHttpMessageConverter(MediaType supportedMediaType) {
		super(supportedMediaType);
	}

	public AvroHttpMessageConverter(MediaType... supportedMediaTypes) {
		super(supportedMediaTypes);
	}

}
