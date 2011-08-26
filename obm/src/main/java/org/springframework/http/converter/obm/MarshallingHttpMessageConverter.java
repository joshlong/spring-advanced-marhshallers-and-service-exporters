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

package org.springframework.http.converter.obm;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.obm.Marshaller;
import org.springframework.obm.Unmarshaller;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * The idea is that this class wil do the work that all of other defintions do because, essentially, their only differentiator is that
 * they do project-specific IO, which the {@link Marshaller} and {@link Unmarshaller} encapsulate nicely.
 *
 * @author Josh Long
 * @see org.springframework.http.converter.HttpMessageConverter
 */
public class MarshallingHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements InitializingBean {

    private Marshaller marshaller;

    private Unmarshaller unmarshaller;

    public MarshallingHttpMessageConverter(Marshaller marshaller) {
        Assert.isInstanceOf(Unmarshaller.class, marshaller);
        this.marshaller = marshaller;
        this.unmarshaller = (Unmarshaller) marshaller;
    }

    public MarshallingHttpMessageConverter(Marshaller marshaller, Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
        this.marshaller = marshaller;
    }

    public void setMarshaller(Marshaller<Object> marshaller) {
        this.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller<Object> unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return marshaller.supports(clazz) && unmarshaller.supports(clazz);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<MediaType> mediaTypes = this.getSupportedMediaTypes();
        Assert.isTrue(mediaTypes.size() > 0, "the " + getClass().getName() + " has no " +
                                                     "'supportedMediaTypes.' This is most likely a configuration error" +
                                                     " and is not likely to work the way you expect it.");
        Assert.notNull(this.marshaller, "the 'thriftMarshaller' can't be null");
        Assert.notNull(this.unmarshaller, "the 'unmarshaller' can't be null");
    }

    @Override
    protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream in = inputMessage.getBody();
        try {
            return unmarshaller.unmarshal(clazz, in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        OutputStream out = outputMessage.getBody();
        try {
            marshaller.marshal(o, out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
