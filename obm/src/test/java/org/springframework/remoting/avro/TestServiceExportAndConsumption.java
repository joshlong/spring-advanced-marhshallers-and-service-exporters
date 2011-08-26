package org.springframework.remoting.avro;

import junit.framework.Assert;
import org.apache.avro.AvroRemoteException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Test;
import org.springframework.obm.avro.crm.Crm;
import org.springframework.obm.avro.crm.Customer;
import org.springframework.remoting.avro.clients.SaslTransceiverCreationCallback;
import org.springframework.remoting.avro.servers.SaslServerCreationCallback;
import org.springframework.util.ClassUtils;

import java.util.Random;

/**
 * @author Josh Long
 */
public class TestServiceExportAndConsumption {

	Log log = LogFactory.getLog(getClass()) ;
	SaslTransceiverCreationCallback transceiverCreationCallback = new SaslTransceiverCreationCallback();
	SaslServerCreationCallback serverCreationCallback = new SaslServerCreationCallback();
	AvroExporter avroExporter ;

	static class MyCrm implements Crm {

		static private Random randomIdGenerator = new Random();

		@Override
		public Customer createCustomer(CharSequence fn, CharSequence ln, CharSequence email) throws AvroRemoteException {
			Customer c = new Customer();
			c.email = email;
			c.lastName = ln;
			c.firstName = fn;
			c.id = randomIdGenerator.nextInt();
			return c;
		}
	}

	@Test
	public void testCreatingAServer() throws Throwable {

		MyCrm crmImpl = new MyCrm() ;

		avroExporter = new AvroExporter();
		avroExporter.setService(crmImpl);
		avroExporter.setServiceInterface(Crm.class);
		avroExporter.setServerCreationCallback(serverCreationCallback);
		avroExporter.setBeanClassLoader(ClassUtils.getDefaultClassLoader());
		avroExporter.afterPropertiesSet();
		avroExporter.start();

		AvroProxyFactoryBean<Crm> proxyFactoryBean = new AvroProxyFactoryBean<Crm>();
		proxyFactoryBean.setServiceInterface(Crm.class);
		proxyFactoryBean.setTransceiverCreationCallback(transceiverCreationCallback);
		proxyFactoryBean.afterPropertiesSet();
		Crm crmClient = proxyFactoryBean.getObject() ;
		Customer customer = crmClient.createCustomer("Josh", "Long", "email@email.com");
		if( log.isDebugEnabled()) {
			log.debug( "received result " + ToStringBuilder.reflectionToString(customer));
		}
		Assert.assertNotNull(customer);
	}

	@After
	public void stop () throws Throwable {
		Assert.assertTrue(avroExporter != null );
		avroExporter.stop();
	}



}
