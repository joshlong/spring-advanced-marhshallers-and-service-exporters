package org.springframework.http.converter.avro;

import junit.framework.Assert;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.avro.EncoderFactoryBuilder;
import org.springframework.avro.SchemaFactoryBean;
import org.springframework.avro.crm.Customer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * test the {@link org.springframework.http.converter.HttpMessageConverter avro http converter}
 *
 * @author Josh Long
 */
public class AvroHttpMessageConverterTest {

	private AvroHttpMessageConverter messageConverter = new AvroHttpMessageConverter();

	private Log log = LogFactory.getLog(getClass());

	private Schema schema;
	private MediaType mediaType = AvroHttpMessageConverter.MEDIA_TYPE;
	private Customer customer = new Customer();


	@Before
	public void before() throws Throwable {

		// the public fields-based approach is ... regrettable
		// the 1.6 version of Avro will remedy this: AVRO-784 or AVRO-839
		//
		customer.id = 225;
		customer.firstName = "Josh";
		customer.lastName = "Long";
		customer.email = "josh@emai.com";

		schema = new SchemaFactoryBean(Customer.class).getObject();

	}

	@Test
	public void testHttpWriting() throws Throwable {
		boolean canWrite  = messageConverter.canWrite(Customer.class, mediaType);
		Assert.assertTrue("the converter should be able to read the class",canWrite );
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		HttpOutputMessage httpOutputMessage = Mockito.mock(HttpOutputMessage.class);
		Mockito.when(httpOutputMessage.getHeaders()).thenReturn(headers);
		Mockito.when(headers.getContentType()).thenReturn(this.mediaType);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Mockito.when(httpOutputMessage.getBody()).thenReturn(byteArrayOutputStream);

		messageConverter.write(customer, this.mediaType, httpOutputMessage);
		byteArrayOutputStream.flush();
		byteArrayOutputStream.close();
		// now we have the bytes of the customer
		byte[] bytesWritten = byteArrayOutputStream.toByteArray();
		Customer readCustomer = new Customer();
		// lets 'restore' the bytes to a customer object

//

//		deserializer.deserialize(readCustomer, bytesWritten);
//		Assert.assertEquals("the two customers should have the same properties ", this.customer, readCustomer);
	}

	@Test
	public void testHttpReading() throws Throwable {
		Assert.assertTrue("the converter should be able to read the class.", messageConverter.canRead(Customer.class, mediaType));
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		HttpInputMessage inputMessage = Mockito.mock(HttpInputMessage.class);
		Mockito.when(headers.getContentType()).thenReturn(this.mediaType);
		Mockito.when(inputMessage.getHeaders()).thenReturn(headers);
		InputStream fakeBody = fakeInputStream(customer);
		Mockito.when(inputMessage.getBody()).thenReturn(fakeBody);
		Customer customerResponse = (Customer) this.messageConverter.read(Customer.class, inputMessage);
		Assert.assertEquals(customer, customerResponse);
	}

	private InputStream fakeInputStream(Object obj) throws Exception {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		Schema s = new SchemaFactoryBean(obj.getClass()).getObject();
		Encoder e = new EncoderFactoryBuilder().
							   setOutputStream(arrayOutputStream)
				            .setSchema(s)
				            .build();

		GenericDatumWriter writer = new GenericDatumWriter();
		writer.setSchema(s);
		writer.write(obj, e);
	    e.flush();
		arrayOutputStream.flush();
		byte[] bytes = arrayOutputStream.toByteArray();
		return new ByteArrayInputStream(bytes);
	}

	/*


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

	*/
}
