package org.springframework.thrift.rpc;

import org.apache.thrift.TException;
import org.springframework.remoting.thrift.ThriftExporter;
import org.springframework.thrift.crm.Crm;
import org.springframework.thrift.crm.Customer;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestRpcServiceExporter {

	static class CustomCrmService implements Crm.Iface {

		private List<String> firstNames = Arrays.asList("Josh", "Oliver", "Costin", "Juergen", "Rod", "Mark", "Dave", "Arjen", "Keith", "Adam", "Mike", "Mario");
		private List<String> lastNames = Arrays.asList("Lee", "Loo", "Wi", "Li", "Humble", "Duong", "Kuo");
		private final Random lnRandom = new Random();
		private final Random fnRandom = new Random();
		private final Random idRandom = new Random();

		private String lastName() {
			int i = lnRandom.nextInt(lastNames.size());
			return lastNames.get(i);
		}

		private String firstName() {
			int i = fnRandom.nextInt(firstNames.size());
			return firstNames.get(i);
		}

		@Override
		public Customer createCustomer(String fn, String ln, String email) throws TException {
			return new Customer(fn, ln, email, idRandom.nextInt());
		}

		@Override
		public Customer getCustomerById(int customerId) throws TException {
			String fn = firstName();
			String ln = lastName();
			return new Customer(fn, ln, fn + "@email.com", customerId);
		}
	}

	public static void main(String args[]) throws Throwable {

		CustomCrmService crmService = new CustomCrmService();


		ThriftExporter exporter = new ThriftExporter();
		exporter.setService(crmService);
		exporter.setServiceInterface( Crm.class);
		exporter.setBeanClassLoader(ClassUtils.getDefaultClassLoader());
		exporter.start();

	}

}
