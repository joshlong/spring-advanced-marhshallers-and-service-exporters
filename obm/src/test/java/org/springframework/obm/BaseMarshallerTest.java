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


    protected  void doTestMarshalling(Class<T> cl, T input) throws Exception {
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
