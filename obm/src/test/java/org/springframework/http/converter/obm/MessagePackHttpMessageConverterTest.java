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

package org.springframework.http.converter.obm;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.obm.messagepack.Cat;
import org.springframework.obm.messagepack.MessagePackMarshaller;

/**
 * @author Josh Long
 */
public class MessagePackHttpMessageConverterTest extends BaseMarshallingHttpMessageConverterTest {

    private Cat cat = new Cat();

    @Before
    public void before() throws Throwable {
        cat.setAge(4);
        cat.setName("Felix");

        MessagePackMarshaller marshaller = new MessagePackMarshaller();
        marshaller.afterPropertiesSet();

        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        setMediaType(new MediaType("application", "x-msgpack"));
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(marshaller);
        setHttpMessageConverter(converter);
    }

    @Test
    public void testHttpReading() throws Throwable {
        doTestHttpWriting(cat.getClass(), cat);
    }
}

