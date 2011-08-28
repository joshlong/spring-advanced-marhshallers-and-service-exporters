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


/**
 * Enum for the two types of Java serialization supported by JBoss Remoting
 * <p/>
 * <UL>
 * <li>Java RMI serialization, which delegates to RMI</li>
 * <li>JBoss Serialization, which is smarter in how it clones objects, avoids the need to implement {@link java.io.Serializable} and to provide a <CODE>SerialUID</CODE> </li>
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
