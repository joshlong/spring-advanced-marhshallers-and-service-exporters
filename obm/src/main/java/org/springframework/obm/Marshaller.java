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

package org.springframework.obm;


import java.io.OutputStream;

/**
 * <p/>
 * Defines the contract for Object to Binary Marshallers. Implementations of this interface
 * can serialize a given Object to an {@link OutputStream}.
 * <p/>
 * <p>Although the <code>marshal</code> method accepts a <code>java.lang.Object</code> as its
 * first parameter, most <code>Marshaller</code> implementations cannot handle arbitrary
 * <code>Object</code>s. Instead, a object class must be registered with the thriftMarshaller,
 * or have a common base class.
 *
 * @author Josh Long
 * @see org.springframework.obm.Unmarshaller
 * @see org.springframework.oxm.Marshaller
 * @see org.springframework.oxm.Unmarshaller
 */
public interface Marshaller<T> {

    boolean supports(Class<T> clazz);

    void marshal(T obj, OutputStream os) throws Exception;

}
