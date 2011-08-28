package org.springframework.remoting.jbr;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class TestJbossRemotingExporter {


    @Configuration
    public static class ClientConfiguration {
        @Bean
        public JbossRemotingProxyFactoryBean client() {
            JbossRemotingProxyFactoryBean<Crm> bean = new JbossRemotingProxyFactoryBean<Crm>();
            bean.setServiceInterface(Crm .class);
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

    public static void main(String args[]) throws Throwable {
        AnnotationConfigApplicationContext client = null ,
                                            server =  null ;
        try {
        server = new AnnotationConfigApplicationContext(ServerConfiguration.class);
        client = new AnnotationConfigApplicationContext(ClientConfiguration.class);
        Crm crn = client.getBean(Crm.class);
        System.out.println(crn.getCustomerById(2423));
        } finally {
            if(null !=client)
                client.stop();
            if(null !=server)
            server.stop();
        }
    }


}
