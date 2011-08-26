package org.springframework.http.converter.obm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.obm.Marshaller;
import org.springframework.obm.Unmarshaller;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The idea is that this class wil do the work that all of other defintions do because, essentially, their only differentiator is that
 * they do project-specific IO, which the {@link Marshaller} and {@link Unmarshaller} encapsulate nicely.
 *
 * @author Josh Long
 * @see org.springframework.http.converter.HttpMessageConverter
 */
public class MarshallingHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements InitializingBean {

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
        return unmarshaller.unmarshal( clazz, in);
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        OutputStream out = outputMessage.getBody();
        marshaller.marshal(o, out);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == marshaller) {
            if (null != unmarshaller) {
                if (unmarshaller instanceof Marshaller) {
                    marshaller = (Marshaller) unmarshaller;
                }
            }
        }

        if (null == unmarshaller) {
            if (null != marshaller) {
                if (marshaller instanceof Unmarshaller) {
                    unmarshaller = (Unmarshaller) marshaller;
                }
            }
        }


        Assert.notNull(this.marshaller, "the 'marshaller' can't be null");
        Assert.notNull(this.unmarshaller, "the 'unmarshaller' can't be null");
    }
}
