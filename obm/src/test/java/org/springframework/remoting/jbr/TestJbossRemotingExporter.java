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
package org.springframework.remoting.jbr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class TestJbossRemotingExporter {

    private Log log = LogFactory.getLog(getClass());

    @Configuration
    public static class ClientConfiguration {
        @Bean
        public JbossRemotingProxyFactoryBean client() {
            JbossRemotingProxyFactoryBean<Crm> bean = new JbossRemotingProxyFactoryBean<Crm>();
            bean.setServiceInterface(Crm.class);
            return bean;
        }
    }

    @Configuration
    public static class ServerConfiguration {
        @Bean
        public CrmImpl crm() {
            return new CrmImpl();
        }

        @Bean
        public JbossRemotingExporter exporter() {
            JbossRemotingExporter exporter = new JbossRemotingExporter();
            exporter.setService(crm());
            exporter.setServiceInterface(Crm.class);
            return exporter;
        }
    }

    @Test
    public void testRetreivingDataFromRpc() throws Throwable {

        AnnotationConfigApplicationContext client = null, server = null;

        try {
            server = new AnnotationConfigApplicationContext(ServerConfiguration.class);
            client = new AnnotationConfigApplicationContext(ClientConfiguration.class);
            Crm clientBean = client.getBean(Crm.class);

            long id =8709;
            Customer customer =clientBean.getCustomerById(id) ;
            Assert.assertNotNull(customer);
            Assert.assertEquals(customer.getId(), id );

            if (log.isDebugEnabled()) {
                log.debug( customer.toString());
            }
        } finally {
            if (null != client) {
                client.stop();
            }
            if (null != server) {
                server.stop();
            }
        }
    }


}
