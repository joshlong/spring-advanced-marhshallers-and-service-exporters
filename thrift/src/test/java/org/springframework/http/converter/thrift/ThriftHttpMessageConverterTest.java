package org.springframework.http.converter.thrift;

import junit.framework.Assert;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.thrift.crm.Customer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * put the {@link ThriftHttpMessageConverter} implementation under test
 *
 * @author Josh Long
 */
public class ThriftHttpMessageConverterTest {

	ThriftHttpMessageConverter thriftMessageConverter = new ThriftHttpMessageConverter();

	TDeserializer deserializer = new TDeserializer();

	TSerializer serializer = new TSerializer();

	MediaType thriftMediaType = ThriftHttpMessageConverter.MEDIA_TYPE;

	Customer customer = new Customer("Josh", "Long", "josh@email.com", 242);

	@Before
	public void before() throws Throwable {
		this.thriftMessageConverter.afterPropertiesSet();
	}

	@Test
	public void testHttpWriting() throws Throwable {
		Assert.assertTrue("the converter should be able to read the class", thriftMessageConverter.canWrite(Customer.class, thriftMediaType));
		Class<Customer> classToWrite = Customer .class;
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		HttpOutputMessage httpOutputMessage = Mockito.mock(HttpOutputMessage.class);
		Mockito.when(httpOutputMessage.getHeaders()).thenReturn(headers);
		Mockito.when(headers.getContentType()).thenReturn(this.thriftMediaType);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Mockito.when(httpOutputMessage.getBody()).thenReturn(byteArrayOutputStream);

		thriftMessageConverter.write(customer, this.thriftMediaType, httpOutputMessage);
		byteArrayOutputStream.flush();
		byteArrayOutputStream.close();
		byte[] bytesWritten = byteArrayOutputStream.toByteArray();
		Customer readCustomer= new Customer() ;
		deserializer.deserialize(readCustomer, bytesWritten);
		Assert.assertEquals("the two customers should have the same properties ", this.customer, readCustomer);
	}

	// todo fix the same test in the MessagePack code on master
	@Test
	public void testHttpReading() throws Throwable {
		Assert.assertTrue("the converter should be able to read the class.", thriftMessageConverter.canRead(Customer.class, thriftMediaType));
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		HttpInputMessage inputMessage = Mockito.mock(HttpInputMessage.class);
		Mockito.when(headers.getContentType()).thenReturn(this.thriftMediaType);
		Mockito.when(inputMessage.getHeaders()).thenReturn(headers);
		InputStream fakeBody = fakeInputStream(customer);
		Mockito.when(inputMessage.getBody()).thenReturn(fakeBody);
		Customer customerResponse = (Customer) this.thriftMessageConverter.read(Customer.class, inputMessage);
		Assert.assertEquals(customer, customerResponse);
	}

	private InputStream fakeInputStream(TBase obj) throws Exception {
		byte[] bytesSrc = serializer.serialize(obj);
		return new ByteArrayInputStream(bytesSrc);
	}
}
