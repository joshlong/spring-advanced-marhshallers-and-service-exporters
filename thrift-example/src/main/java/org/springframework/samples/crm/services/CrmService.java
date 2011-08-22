package org.springframework.samples.crm.services;

import org.springframework.stereotype.Service;
import org.springframework.thrift.crm.Customer;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service("crm")
public class CrmService {

	private List<String> firstNames = Arrays.asList("Josh","Oliver", "Costin", "Juergen", "Rod", "Mark", "Dave", "Arjen", "Keith", "Adam", "Mike", "Mario");
	private List<String> lastNames = Arrays.asList("Lee", "Loo", "Wi", "Li", "Humble", "Duong", "Kuo");

	private final Random lnRandom = new Random();
	private final Random fnRandom = new Random();

	private String lastName() {
		int i = lnRandom.nextInt(lastNames.size());
		return lastNames.get(i);
	}

	private String firstName() {
		int i = fnRandom.nextInt(firstNames.size());
		return firstNames.get(i);
	}

	public Customer createCustomer(String fn, String ln, String email, int id) {
		return new Customer(fn, ln, email, id);
	}

	public Customer getCustomerById( int  customerId) {
		String fn = firstName();
		String ln = lastName();
		return new Customer(fn, ln, fn + "@email.com",  customerId);
	}
}
