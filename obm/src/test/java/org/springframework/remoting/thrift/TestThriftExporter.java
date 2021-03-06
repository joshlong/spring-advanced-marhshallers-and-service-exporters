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

package org.springframework.remoting.thrift;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.obm.thrift.crm.Crm;
import org.springframework.obm.thrift.crm.Customer;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * a strange, but working, test case that launches the thrift service in a background thread and then shuts it down afterwards which lets us
 * verify that we can talk to the exporter service with no problems.
 *
 * @author Josh Long
 */
public class TestThriftExporter {

    private Log log = LogFactory.getLog(getClass());
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private ThriftExporter server = new ThriftExporter();

    private Crm.Iface client;

    private static class ServerRunnable implements Runnable {

        private ThriftExporter exporter;

        public ServerRunnable(ThriftExporter exporter) {
            this.exporter = exporter;
        }

        @Override
        public void run() {
            try {
                exporter.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testConsumingFromTheService() throws Throwable {

        Assert.assertNotNull(client);

        String fn = "Josh", ln = "Long", email = "josh@email.com";

        Customer customer = client.createCustomer(fn, ln, email);

        Assert.assertNotNull(customer);
        Assert.assertEquals(customer.getFirstName(), fn);
        Assert.assertEquals(customer.getLastName(), ln);
        Assert.assertEquals(customer.getEmail(), email);
    }

    @Before
    public void startServer() throws Exception {

        CustomCrmService crmService = new CustomCrmService();

        // create the server
        ThriftExporter e = new ThriftExporter();
        e.setService(crmService);
        e.setServiceInterface(Crm.class);
        e.setBeanClassLoader(ClassUtils.getDefaultClassLoader());
        e.afterPropertiesSet();
        server = e;
        executor.submit(new ServerRunnable(e));

        // give the server a bit to bind to the socket and so on...
        Thread.sleep(1000 * 1);

        // create the client
        ThriftProxyFactoryBean<Crm.Iface> client = new ThriftProxyFactoryBean<Crm.Iface>();
        client.setBeanClassLoader(ClassUtils.getDefaultClassLoader());
        client.setServiceInterface(Crm.Iface.class);
        client.afterPropertiesSet();
        this.client = client.getObject();

    }

    @After
    public void stopServer() throws Throwable {
        server.stop(new Runnable() {
            @Override
            public void run() {
                if (log.isDebugEnabled()) {
                    log.debug("stopping the service exporter...");
                }
            }
        });
    }

    static public class CustomCrmService implements Crm.Iface {

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

}
