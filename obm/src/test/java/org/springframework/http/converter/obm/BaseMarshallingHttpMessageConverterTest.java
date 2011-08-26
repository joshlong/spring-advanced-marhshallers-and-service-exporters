package org.springframework.http.converter.obm;

import junit.framework.Assert;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.obm.MarshallingHttpMessageConverter;
import org.springframework.obm.BaseMarshallerTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Tests the {@link org.springframework.http.converter.obm.MarshallingHttpMessageConverter}
 *
 * @author Josh Long
 */
public class BaseMarshallingHttpMessageConverterTest extends BaseMarshallerTest {
    protected MarshallingHttpMessageConverter marshallingHttpMessageConverter;

    protected MediaType mediaType;

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setHttpMessageConverter(MarshallingHttpMessageConverter marshallingHttpMessageConverter) {
        this.marshallingHttpMessageConverter = marshallingHttpMessageConverter;
    }

    private void setup() {
        Assert.assertNotNull(this.marshallingHttpMessageConverter);
        Assert.assertNotNull(this.marshaller);
        Assert.assertNotNull(this.unmarshaller);
        Assert.assertNotNull(this.mediaType);
    }

    protected void doTestHttpWriting(Class<?> clazz, Object output) throws Throwable {
        setup();

        Assert.assertNotNull("object to output can't be null", output);

        Assert.assertTrue("the marshaller must be able to read this class ",
                                 marshallingHttpMessageConverter.supports(clazz) &&
                                         this.unmarshaller.supports(clazz));
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        HttpOutputMessage httpOutputMessage = Mockito.mock(HttpOutputMessage.class);
        Mockito.when(httpOutputMessage.getHeaders()).thenReturn(headers);
        Mockito.when(headers.getContentType()).thenReturn(this.mediaType);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Mockito.when(httpOutputMessage.getBody()).thenReturn(byteArrayOutputStream);
        marshallingHttpMessageConverter.write(output, this.mediaType, httpOutputMessage);
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        byte[] bytesWritten = byteArrayOutputStream.toByteArray();

        doTestHttpReading(clazz, output, new ByteArrayInputStream(bytesWritten));
    }

    private void doTestHttpReading(Class<?> clazz, Object output, InputStream inputStream) throws Throwable {
        setup();
        Assert.assertNotNull("object to output can't be null", output);
        Assert.assertTrue("the marshaller must be able to read this class ",
                                 marshallingHttpMessageConverter.supports(clazz) &&
                                         this.unmarshaller.supports(clazz));
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        HttpInputMessage inputMessage = Mockito.mock(HttpInputMessage.class);
        Mockito.when(headers.getContentType()).thenReturn(this.mediaType);
        Mockito.when(inputMessage.getHeaders()).thenReturn(headers);
        Mockito.when(inputMessage.getBody()).thenReturn(inputStream);
        Object result = marshallingHttpMessageConverter.read(clazz, inputMessage);
        Assert.assertNotNull(result);
        Assert.assertEquals("the resulting objects must be .equals()", result, output);

    }
}
