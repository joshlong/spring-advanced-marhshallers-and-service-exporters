package org.springframework.http.converter.avro;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * This {@link org.springframework.http.converter.HttpMessageConverter} provides first class support for the {@link org.springframework.web.client.RestTemplate}
 * REST client and the server side machinery for producing REST services in Spring MVC.
 *
 * @author Josh Long
 * @see org.springframework.http.converter.HttpMessageConverter
 * @see org.springframework.web.client.RestTemplate
 */
public class AvroHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements InitializingBean {
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
	}
}
