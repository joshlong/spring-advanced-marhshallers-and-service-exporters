package org.springframework.remoting.jbr;


/**
 *
 * Enum for the two types of Java serialization supported by JBoss Remoting
 *
 * <UL>
 *  <li>Java RMI serialization, which delegates to RMI</li>
 *  <li>JBoss Serialization, which is smarter in how it clones objects, avoids the need to implement {@link java.io.Serializable} and to provide a <CODE>SerialUID</CODE> </li>
 * </UL>
 *
 * @author Josh Long
 */
public enum JbossSerialization {

    /**
     * this is basically a fallback to RMI
     */
    JAVA,

    /**
     * this is the default in both the client and the
     * server components provided by this support, and lets you serialize
     * virtually any object over the wire.
     */
    JBOSS

}
