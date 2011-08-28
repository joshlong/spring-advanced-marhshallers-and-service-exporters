package org.springframework.remoting.jbr;

import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.serialization.SerializationStreamFactory;
import org.jboss.remoting.transport.ClientFactory;
import org.jboss.remoting.transport.ClientInvoker;
import org.jboss.remoting.transporter.TransporterServer;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Supports proxying clients for JBoss Remoting.
 *
 * There are two different types of serialization
 *
 * the default, JBoss, uses a special serialization library from JBoss that
 * <ul>
 *     <LI> Eschews the need to implement {@link java.io.Serializable}</LI>
 *     <LI> Eschews the need to </LI>
 * </ul>
 *
 * http://www.theserverlabs.com/blog/2009/02/19/jboss-remoting-jboss-serialization-kills-javarmi-and-spring-remoting/
 *
 * @author Josh Long
 */
public class JbossRemotingProxyFactoryBean
  implements BeanNameAware   , InitializingBean
{

    private JbossSerialization serializationType  =  JbossSerialization.JBOSS;
    private String host = "127.0.0.1";
    private String serverName ;
    private int port  = 1992;
    private String beanName ;

    @Override
    public void setBeanName(String name) {
     this.beanName = name ;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
     if(!StringUtils.hasText(this.serverName ))
       this.serverName  = this.beanName;
    }


    protected String buildUrl() {
     return "socket://" + host + ":" + port +
            "/?serializationtype=" +
            serializationType.name().toLowerCase();
    }


    void foo ()throws Throwable {

//server side code
//where this is the target object
        TransporterServer.createTransporterServer("socket://" + this.host + ": port/?serializationtype=jboss", this,serverName  );

//client side code
//where remoteServerClass.class is just the remote server interface like RMI
     //   remoteServer = (T) TransporterClient.createTransporterClient("socket://" + remoteHostname + ":remotePort/?serializationtype=jboss", remoteServerClass.class);

    }
    // todo
    SerializationStreamFactory ssf;   /*

http://docs.jboss.org/jbossremoting/docs/guide/2.5/JBoss_Remoting_Guide.pdf

new InvokerLocator("http://localhost:1234/services/uri:Test").isSameEndpoint(
new InvokerLocator("http://localhost:1234")) returns tru



public ClientInvoker createClientInvoker(InvokerLocator locator, Map config) throws IOException;
public boolean supportsSSL()





ClientFactory clientFactory ;

     */

 public static class TransportClientFactory implements  ClientFactory {
        @Override
        public ClientInvoker createClientInvoker(InvokerLocator invokerLocator, Map map) throws IOException {
        return null ;
        }

        @Override
        public boolean supportsSSL() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }



}
