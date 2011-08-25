package org.springframework.http.converter.avro;

import org.apache.avro.Schema;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.thrift.SchemaFactoryBean;

/**
 * test the {@link org.springframework.http.converter.HttpMessageConverter avro http converter}
 *
 * @author Josh Long
 */
public class AvroHttpMessageConverterTest {

	private AvroHttpMessageConverter messageConverter = new AvroHttpMessageConverter();

	private Log log = LogFactory.getLog(getClass());

	private Schema schema;
	private Resource resource  = new ClassPathResource("/crm.avpr");
	private MediaType avroMediaType = AvroHttpMessageConverter.MEDIA_TYPE;


	@Before
	public void before() throws Throwable {
		schema = new SchemaFactoryBean(resource ).getObject();
//		this.messageConverter.setSchema(this.schema);
//		this.messageConverter.afterPropertiesSet();
	}


	@Test
	public void testFoo() throws Throwable {

		if (log.isDebugEnabled()) {
			log.debug("testFoo()");
		}
	}
/*
	Customer customer = new Customer("Josh", "Long", "josh@email.com", 242);

	@Before
	public void before() throws Throwable {
		this.thriftMessageConverter.afterPropertiesSet();
	}

	@Test
	public void testHttpWriting() throws Throwable {
		Assert.assertTrue("the converter should be able to read the class", thriftMessageConverter.canWrite(Customer.class, thriftMediaType));
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
		Customer readCustomer = new Customer();
		deserializer.deserialize(readCustomer, bytesWritten);
		Assert.assertEquals("the two customers should have the same properties ", this.customer, readCustomer);
	}

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
	}*/
}
