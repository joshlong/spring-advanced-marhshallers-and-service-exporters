package org.springframework.remoting.jbr;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class TestJbossRemotingExporter {

    public static class Customer {

        public Customer(long id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        private long id;

        private String firstName, lastName;

        @Override
        public String toString() {
            return "Customer{" +
                           "firstName='" + firstName + '\'' +
                           ", lastName='" + lastName + '\'' +
                           ", id=" + id +
                           '}';
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

    }

    public static interface Crm {
        Customer getCustomerById(long id);
    }

    public static class CrmImpl implements Crm {
        public Customer getCustomerById(long id) {
            return new Customer( id , "josh", "long");
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

    public static void main (String args []) throws Throwable {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(ServerConfiguration.class);
        Crm crn = annotationConfigApplicationContext.getBean( Crm.class);
        System.out.println(crn.getCustomerById(2423));
    }


}
