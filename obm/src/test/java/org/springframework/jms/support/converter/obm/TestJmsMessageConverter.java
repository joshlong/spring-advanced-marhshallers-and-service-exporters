package org.springframework.jms.support.converter.obm;

import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.obm.avro.AvroMarshaller;
import org.springframework.obm.avro.crm.Customer;
import org.springframework.obm.messagepack.Cat;
import org.springframework.obm.messagepack.MessagePackMarshaller;
import org.springframework.obm.protocolbuffers.ProtocolBuffersMarshaller;
import org.springframework.obm.protocolbuffers.crm.Crm;
import org.springframework.obm.thrift.ThriftMarshaller;
import org.springframework.util.jms.JmsIntegrationTestUtils;

/**
 * Tests the {@link org.springframework.obm.avro.AvroMarshaller} with JMS (specifically, an embedded ActiveMQ broker).
 *
 * @author Josh Long
 */
public class TestJmsMessageConverter {

    // thrift
    private ThriftMarshaller thriftMarshaller = new ThriftMarshaller();
    private org.springframework.obm.thrift.crm.Customer thriftCustomer = new org.springframework.obm.thrift.crm.Customer("John", "Doe", "email@email.com", 22);


    // messagepack
    private MessagePackMarshaller msgPackMarshaller = new MessagePackMarshaller();
    private Cat msgPackCat;

    // protocol buffers
    private ProtocolBuffersMarshaller buffersMarshaller = new ProtocolBuffersMarshaller();
    private Crm.Customer buffersCustomer;
    // avro
    private AvroMarshaller avroMarshaller = new AvroMarshaller();
    private Customer avroCustomer = new Customer();

    @Before
    public void before() throws Throwable {
        // protocol buffers
        buffersCustomer = Crm.Customer.newBuilder().setEmail("email@e.com").setFirstName("john").setLastName("long").build();

        // avro
        avroCustomer.firstName = "josh";
        avroCustomer.lastName = "long";
        avroCustomer.email = "em@em.com";
        avroCustomer.id = (int) (1000 * Math.random());

        // message pack
        msgPackCat = new Cat("Felix", (int) (1000 * Math.random()));
        this.msgPackMarshaller.afterPropertiesSet();

        // thrift
        thriftMarshaller.afterPropertiesSet();
    }

    @Test
    public void testThrift() throws Throwable {
        JmsIntegrationTestUtils.startAndConnectToJmsBroker(org.springframework.obm.thrift.crm.Customer.class, thriftMarshaller, new JmsIntegrationTestUtils.JmsBrokerExecutionCallback() {
            @Override
            public void doWithActiveMq(BrokerService brokerService, JmsTemplate jmsTemplate) throws Throwable {
                String avroDestination = "thrift";
                jmsTemplate.convertAndSend(avroDestination, thriftCustomer);

                org.springframework.obm.thrift.crm.Customer customerReceived =
                     (org.springframework.obm.thrift.crm.Customer) jmsTemplate.receiveAndConvert(avroDestination);
                Assert.assertEquals(thriftCustomer, customerReceived);
            }
        });
    }

    @Test
    public void testAvro() throws Throwable {
        JmsIntegrationTestUtils.startAndConnectToJmsBroker(Customer.class, avroMarshaller, new JmsIntegrationTestUtils.JmsBrokerExecutionCallback() {
            @Override
            public void doWithActiveMq(BrokerService brokerService, JmsTemplate jmsTemplate) throws Throwable {
                String avroDestination = "avro";
                jmsTemplate.convertAndSend(avroDestination, avroCustomer);

                Customer customerReceived = (Customer) jmsTemplate.receiveAndConvert(avroDestination);
                Assert.assertEquals(avroCustomer, customerReceived);
            }
        });
    }

    @Test
    public void testMessagePack() throws Throwable {
        JmsIntegrationTestUtils.startAndConnectToJmsBroker(Cat.class, this.msgPackMarshaller, new JmsIntegrationTestUtils.JmsBrokerExecutionCallback() {
            @Override
            public void doWithActiveMq(BrokerService brokerService, JmsTemplate jmsTemplate) throws Throwable {
                String pbDestination = "messagepack";
                jmsTemplate.convertAndSend(pbDestination, msgPackCat);
                Cat rCat = (Cat) jmsTemplate.receiveAndConvert(pbDestination);
                Assert.assertEquals(rCat, msgPackCat);
            }
        });
    }

    @Test
    public void testProtocolBuffers() throws Throwable {
        JmsIntegrationTestUtils.startAndConnectToJmsBroker(Crm.Customer.class, this.buffersMarshaller, new JmsIntegrationTestUtils.JmsBrokerExecutionCallback() {
            @Override
            public void doWithActiveMq(BrokerService brokerService, JmsTemplate jmsTemplate) throws Throwable {
                String pbDestination = "pb";
                jmsTemplate.convertAndSend(pbDestination, buffersCustomer);
                Crm.Customer receivedCustomer = (Crm.Customer) jmsTemplate.receiveAndConvert(pbDestination);
                Assert.assertEquals(receivedCustomer, buffersCustomer);
            }
        });
    }

}
