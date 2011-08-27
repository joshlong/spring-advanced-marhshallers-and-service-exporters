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

package org.springframework.obm.protocolbuffers;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import org.springframework.obm.support.AbstractMarshaller;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * Protocol buffers is one of the most mature serialization libraries out there at the moment.
 *
 * @author Josh Long
 */
public class ProtocolBuffersMarshaller<T> extends AbstractMarshaller<T> {

    @Override
    public boolean supports(Class<T> clazz) {
        return Message.class.isAssignableFrom(clazz);
    }


    @Override
    public void marshal(T obj, OutputStream os) throws Exception {
        Assert.isTrue(obj instanceof Message, "the marshaller can only serialize subclasses of " + Message.class.getName());
        Message msg = (Message) obj;
        os.write(msg.toByteArray());
    }

    @Override
    public T unmarshal(Class<T> clazz, InputStream source) throws Exception {
        Method newBuilder = clazz.getMethod("newBuilder");
        Assert.isAssignable(Message.class, clazz, "the marshaller can only unmarshal subclases of " + Message.class.getName());
        GeneratedMessage.Builder<?> builder = (GeneratedMessage.Builder<?>) newBuilder.invoke(clazz);
        Message msg = builder.mergeFrom(source).build();
        return (T) msg;
    }
}