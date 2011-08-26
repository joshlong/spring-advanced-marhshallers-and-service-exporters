package org.springframework.remoting.thrift;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.obm.thrift.crm.Crm;
import org.springframework.obm.thrift.crm.Customer;
import org.springframework.util.ClassUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * a strange, but working, test case that launches the thrift service in a background thread and then shuts it down afterwards which lets us
 * verify that we can talk to the exporter service with no problems.
 *
 * @author Josh Long
 */
public class TestThriftServiceExporter {


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
}
