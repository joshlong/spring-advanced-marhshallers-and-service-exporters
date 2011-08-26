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

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.obm.thrift.ThriftMarshaller;
import org.springframework.obm.thrift.crm.Customer;

/**
 * @author Josh Long
 */
public class ThriftHttpMessageConverterTest extends BaseMarshallingHttpMessageConverterTest {

    private Customer customer = new Customer("Josh", "Long", "josh@email.com", 242);
    private MediaType mediaType = new MediaType("application", "x-thrift");

    @Before
    public void before() throws Throwable {

        ThriftMarshaller marshaller = new ThriftMarshaller();

        marshaller.afterPropertiesSet();

        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        setMediaType(mediaType);

        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(marshaller);
        setHttpMessageConverter(converter);
    }

    @Test
    public void testHttpReading() throws Throwable {
        doTestHttpWriting(customer.getClass(), customer);
    }
}
//
// {
///*
//	ThriftHttpMessageConverter thriftMessageConverter = new ThriftHttpMessageConverter();
//
//	TDeserializer deserializer = new TDeserializer();
//
//	TSerializer serializer = new TSerializer();
//
//	MediaType thriftMediaType = ThriftHttpMessageConverter.MEDIA_TYPE;
//
//	Customer customer = new Customer("Josh", "Long", "josh@email.com", 242);
//
//	@Before
//	public void before() throws Throwable {
//		this.thriftMessageConverter.afterPropertiesSet();
//	}
//
//	@Test
//	public void testHttpWriting() throws Throwable {
//		Assert.assertTrue("the converter should be able to read the class", thriftMessageConverter.canWrite(Customer.class, thriftMediaType));
//		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
//		HttpOutputMessage httpOutputMessage = Mockito.mock(HttpOutputMessage.class);
//		Mockito.when(httpOutputMessage.getHeaders()).thenReturn(headers);
//		Mockito.when(headers.getContentType()).thenReturn(this.thriftMediaType);
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		Mockito.when(httpOutputMessage.getBody()).thenReturn(byteArrayOutputStream);
//
//		thriftMessageConverter.write(customer, this.thriftMediaType, httpOutputMessage);
//		byteArrayOutputStream.flush();
//		byteArrayOutputStream.close();
//		byte[] bytesWritten = byteArrayOutputStream.toByteArray();
//		Customer readCustomer = new Customer();
//		deserializer.deserialize(readCustomer, bytesWritten);
//		Assert.assertEquals("the two customers should have the same properties ", this.customer, readCustomer);
//	}
//
//	@Test
//	public void testHttpReading() throws Throwable {
//		Assert.assertTrue("the converter should be able to read the class.", thriftMessageConverter.canRead(Customer.class, thriftMediaType));
//		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
//		HttpInputMessage inputMessage = Mockito.mock(HttpInputMessage.class);
//		Mockito.when(headers.getContentType()).thenReturn(this.thriftMediaType);
//		Mockito.when(inputMessage.getHeaders()).thenReturn(headers);
//		InputStream fakeBody = fakeInputStream(customer);
//		Mockito.when(inputMessage.getBody()).thenReturn(fakeBody);
//		Customer customerResponse = (Customer) this.thriftMessageConverter.read(Customer.class, inputMessage);
//		Assert.assertEquals(customer, customerResponse);
//	}
//
//	private InputStream fakeInputStream(TBase obj) throws Exception {
//		byte[] bytesSrc = serializer.serialize(obj);
//		return new ByteArrayInputStream(bytesSrc);
//	}*/
//}
