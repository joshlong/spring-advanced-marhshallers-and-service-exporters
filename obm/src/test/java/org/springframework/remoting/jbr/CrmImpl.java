package org.springframework.remoting.jbr;


public class CrmImpl implements Crm {
    public Customer getCustomerById(long id) {
        return new Customer(id, "josh", "long");
    }
}
