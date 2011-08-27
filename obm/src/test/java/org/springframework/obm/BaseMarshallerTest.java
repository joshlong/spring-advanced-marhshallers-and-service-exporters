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

import junit.framework.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Josh Long
 */
abstract public class BaseMarshallerTest<T> {
    protected Marshaller<T> marshaller;
    protected Unmarshaller<T> unmarshaller;

    public void setUnmarshaller(Unmarshaller<T> unmarshaller) {
        Assert.assertNotNull(unmarshaller);
        this.unmarshaller = unmarshaller;
    }

    public void setMarshaller(Marshaller<T> marshaller) {
        Assert.assertNotNull(marshaller);
        this.marshaller = marshaller;
    }


    protected void doTestMarshalling(Class<T> cl, T input) throws Exception {
        boolean canItWriteACustomer = marshaller.supports(cl);
        Assert.assertTrue(canItWriteACustomer);

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

        marshaller.marshal(input, arrayOutputStream);

        arrayOutputStream.flush();
        arrayOutputStream.close();

        byte[] bytesWritten = arrayOutputStream.toByteArray();
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytesWritten);
        T output = unmarshaller.unmarshal(cl, arrayInputStream);
        Assert.assertEquals(output, input);
    }
}
