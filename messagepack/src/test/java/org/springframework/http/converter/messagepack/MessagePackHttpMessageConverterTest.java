package org.springframework.http.converter.messagepack;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.msgpack.MessagePack;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * tests functionality of the {@link MessagePackHttpMessageConverter}
 *
 * @author Josh Long
 */
public class MessagePackHttpMessageConverterTest {
	// make it public so that the reflective code in the template can 'see' it
	public static class CatWithProperties {
		private String name;

		private int age;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public CatWithProperties(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public CatWithProperties() {
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			CatWithProperties that = (CatWithProperties) o;

			return age == that.age && !(name != null ? !name.equals(that.name) : that.name != null);

		}

		@Override
		public int hashCode() {
			int result = name != null ? name.hashCode() : 0;
			result = 31 * result + age;
			return result;
		}
	}


	private MessagePackHttpMessageConverter converter = new MessagePackHttpMessageConverter();

	private MediaType messagePackMediaType = MessagePackHttpMessageConverter.MEDIA_TYPE;

	@Before
	public void setup() throws Throwable {
		converter.afterPropertiesSet();
	}

	private InputStream fakeInputStream(Object obj) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(MessagePack.pack(obj));
		byteArrayOutputStream.flush();
		byte[] bytes = byteArrayOutputStream.toByteArray();
		byteArrayOutputStream.close();
		return new ByteArrayInputStream(bytes);
	}

	// todo fix this silly test
	@Test
	public void testHttpReading() throws Throwable {
		Assert.assertTrue("the converter should be able to read the class.", converter.canRead(CatWithProperties.class, messagePackMediaType));
		CatWithProperties srcCat = new CatWithProperties("George", 22);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		HttpInputMessage inputMessage = Mockito.mock(HttpInputMessage.class);
		Mockito.when(headers.getContentType()).thenReturn(this.messagePackMediaType);
		Mockito.when(inputMessage.getHeaders()).thenReturn(headers);
		InputStream inputStream = fakeInputStream(srcCat);
		CatWithProperties cwp = MessagePack.unpack(inputStream, srcCat.getClass());
		Assert.assertEquals(cwp, srcCat);
	}

	@Test
	public void testHttpWriting() throws Throwable {
		Assert.assertTrue("the converter should be able to read the class", converter.canWrite(CatWithProperties.class, messagePackMediaType));
		Class<CatWithProperties> classToWrite = CatWithProperties.class;
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		HttpOutputMessage httpOutputMessage = Mockito.mock(HttpOutputMessage.class);
		Mockito.when(httpOutputMessage.getHeaders()).thenReturn(headers);
		Mockito.when(headers.getContentType()).thenReturn(this.messagePackMediaType);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Mockito.when(httpOutputMessage.getBody()).thenReturn(byteArrayOutputStream);
		CatWithProperties sourceCat = new CatWithProperties("Felix", 100);
		converter.write(sourceCat, this.messagePackMediaType, httpOutputMessage);
		byteArrayOutputStream.flush();
		byteArrayOutputStream.close();
		byte[] bytesWritten = byteArrayOutputStream.toByteArray();
		CatWithProperties destinationCat = MessagePack.unpack(bytesWritten, classToWrite);
		Assert.assertEquals("the two cats should have the same properties ", destinationCat, sourceCat);
	}
}

