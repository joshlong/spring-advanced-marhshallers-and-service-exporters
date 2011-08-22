package org.springframework.thrift.rpc;

import org.apache.thrift.TException;
import org.springframework.remoting.thrift.ThriftExporter;
import org.springframework.thrift.crm.Crm;
import org.springframework.thrift.crm.Customer;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Server {



	public static void main(String args[]) throws Throwable {

		CustomCrmService crmService = new CustomCrmService();

		ThriftExporter exporter = new ThriftExporter();
		exporter.setService(crmService);
		exporter.setServiceInterface( Crm.class);
		exporter.setBeanClassLoader(ClassUtils.getDefaultClassLoader());
		exporter.afterPropertiesSet();
		exporter.start();

		Thread.sleep(1000 * 2);
		exporter.stop(  null );

	}

}
