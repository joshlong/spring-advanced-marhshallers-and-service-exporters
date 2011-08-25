package org.springframework.http.converter.obm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.obm.Marshaller;
import org.springframework.obm.Unmarshaller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MarshallingHttpMessageConverter  extends AbstractHttpMessageConverter<Object>   {

	private Log log = LogFactory.getLog(getClass());
/*
	public static final String MEDIA_TYPE_STRING = "application/x-thrift";

	public static final MediaType MEDIA_TYPE = new MediaType("application", "x-thrift");*/

	private Marshaller marshaller;

	private Unmarshaller unmarshaller;


	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}


	@Override
	protected boolean supports(Class<?> clazz) {
		return unmarshaller.supports(clazz) && marshaller.supports(clazz);
	}


	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		InputStream in = inputMessage.getBody();
		return unmarshaller.unmarshal(in);
	}

	@Override
	protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		OutputStream out = outputMessage.getBody();
		marshaller.marshal(o,out);
	}

}
