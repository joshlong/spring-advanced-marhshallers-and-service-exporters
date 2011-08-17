package org.springframework.http.converter.messagepack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.msgpack.MessagePack;
import org.msgpack.Template;
import org.msgpack.template.builder.BeansTemplateBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * simple {@link org.springframework.http.converter.HttpMessageConverter} that delegates to {@link org.msgpack.MessagePack#pack(java.io.OutputStream, Object)}
 *
 * @author Josh Long
 * @see org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
 */
public class MessagePackHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements  InitializingBean {

	private Log log = LogFactory.getLog(getClass());

	private BeansTemplateBuilder beansTemplateBuilder = new BeansTemplateBuilder();

	private boolean serializeJavaBeanProperties = true;

	private Set<Class<?>> messagePackClasses = new CopyOnWriteArraySet<Class<?>>(); // = new ConcurrentSkipListSet<Class<?>>(new ClassComparator());

	public static final String MEDIA_TYPE_STRING = "application/x-msgpack";

	public static final MediaType MEDIA_TYPE = new MediaType("application", "x-msgpack");

	public MessagePackHttpMessageConverter() {
		super();
		setSupportedMediaTypes(Arrays.asList(MEDIA_TYPE));
	}

	public MessagePackHttpMessageConverter(MediaType supportedMediaType) {
		super(supportedMediaType);
	}

	public MessagePackHttpMessageConverter(MediaType... supportedMediaTypes) {
		super(supportedMediaTypes);
	}

	public void setSerializeJavaBeanProperties(boolean serializeJavaBeanProperties) {
		this.serializeJavaBeanProperties = serializeJavaBeanProperties;
	}

	private boolean messagePackSupports(Class<?> clazz) {

		if (messagePackClasses.contains(clazz)) {
			return true;
		}

		// otherwise, register it
		messagePackClasses.add(clazz);

		if (serializeJavaBeanProperties) {
			Template template = beansTemplateBuilder.buildTemplate(clazz);
			MessagePack.register(clazz, template);
		} else {
			MessagePack.register(clazz);
		}

		return true;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return messagePackSupports(clazz);
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		Assert.isTrue(messagePackSupports(clazz), "the class must be registered");
		InputStream in = inputMessage.getBody();
		return MessagePack.unpack(in, clazz);
	}

	@Override
	protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Assert.isTrue(messagePackSupports(o.getClass()), "the class must be registered");
		MessagePack.pack(outputMessage.getBody(), o);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(!serializeJavaBeanProperties) {
			if(log.isDebugEnabled()){
				log.debug("the 'serializeJavaBeanProperties' property has been set to false, " +
				  "which means that all POJOs must expose public variables to properly be serialized.");
			}
		}
	}
}
