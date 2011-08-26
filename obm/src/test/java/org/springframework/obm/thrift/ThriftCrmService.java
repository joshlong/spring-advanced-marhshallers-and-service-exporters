package org.springframework.obm.thrift;

import org.apache.thrift.TException;
import org.springframework.obm.thrift.crm.Crm;
import org.springframework.obm.thrift.crm.Customer;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Simple service definition written with Thrift in mind.
 *
 * @author Josh Long
 */
@Service("crm")
public class ThriftCrmService implements Crm.Iface {

    private List<String> firstNames = Arrays.asList("Josh", "Oliver", "Costin", "Juergen", "Rod", "Mark", "Dave", "Arjen", "Keith", "Adam", "Mike", "Mario");
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

    @Override
    public Customer createCustomer(String fn, String ln, String email) throws TException {
        return createCustomer(fn, ln, email, (int) (Math.random() * 2002));
    }

    public Customer getCustomerById(int customerId) {
        String fn = firstName();
        String ln = lastName();
        return new Customer(fn, ln, fn + "@email.com", customerId);
    }
}
                         