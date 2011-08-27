/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
                         