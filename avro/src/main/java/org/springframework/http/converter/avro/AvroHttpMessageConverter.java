package org.springframework.http.converter.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.springframework.beans.factory.InitializingBean;
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
public class AvroHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements InitializingBean {

	public static final String MEDIA_TYPE_STRING = "application/x-avro";

	public static final MediaType MEDIA_TYPE = new MediaType("application", "x-avro");

	private Schema writerSchema;
	private Schema readerSchema;
	private GenericDatumWriter writer;
	private GenericDatumReader reader;
	private Schema schema;

	public void setReaderSchema(Schema readerSchema) {
		this.readerSchema = readerSchema;
	}

	public void setWriterSchema(Schema writerSchema) {
		this.writerSchema = writerSchema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
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

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		// ok, only a couple of conditions are acceptable
		if (this.writerSchema != null) {
			Assert.isNull(this.schema, "the 'schema' must be null if you've selected a 'writerSchema'");
		}
		if (this.readerSchema != null) {
			Assert.isNull(this.schema, "the 'schema' must be null if you've selected a 'readerSchema'");
		}
		if (schema != null) {
			Assert.isTrue(this.readerSchema == null && this.writerSchema == null,
					             "you must either select a single 'schema,' or specify " +
								 "a 'readerSchema' and a 'writerSchema.' If the 'readerSchema' and 'writerSchema' schema " +
								 "are the same, then simply specify the 'schema' property");
			this.readerSchema = schema;
			this.writerSchema = schema;
		}


		Assert.notNull(this.writerSchema, "the 'writerSchema' must not be null");
		Assert.notNull(this.readerSchema, "the 'readerSchema' must not be null");

		writer = new GenericDatumWriter(this.writerSchema);
		reader = new GenericDatumReader(this.readerSchema);

	}
}
