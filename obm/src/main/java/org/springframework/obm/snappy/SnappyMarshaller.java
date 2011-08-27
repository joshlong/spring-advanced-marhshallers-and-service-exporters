package org.springframework.obm.snappy;

import org.springframework.obm.Marshaller;
import org.springframework.obm.Unmarshaller;
import org.springframework.obm.support.AbstractMarshaller;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.xerial.snappy.Snappy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This marshaller is a wrapping {@link org.springframework.obm.Marshaller}, meant to be used in conjunction with
 * any other {@link org.springframework.obm.Marshaller}.
 *
 * @author Josh Long
 */
public class SnappyMarshaller extends AbstractMarshaller {

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public SnappyMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
        if (this.marshaller instanceof Unmarshaller) {
            this.unmarshaller = (Unmarshaller) this.marshaller;
        }
        Assert.notNull(this.unmarshaller);
        Assert.notNull(this.marshaller);
    }

    public SnappyMarshaller(Marshaller marshaller, Unmarshaller unmarshaller) {
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
        Assert.notNull(this.unmarshaller);
        Assert.notNull(this.marshaller);
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    @Override
    public boolean supports(Class clazz) {
        return marshaller.supports(clazz) && unmarshaller.supports(clazz);
    }

    @Override
    public void marshal(Object obj, OutputStream os) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            marshaller.marshal(obj, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byte[] uncompressed = byteArrayOutputStream.toByteArray();
            byte[] compressed = Snappy.compress(uncompressed);
            FileCopyUtils.copy(compressed, os);
        } catch (Throwable th) {
            if (log.isErrorEnabled()) {
                log.error("could not compress the object " + (null == obj ? "" : obj + ""), th);
            }
            throw new RuntimeException(th);
        } finally {
            if (null != byteArrayOutputStream) {
                byteArrayOutputStream.close();
            }
        }
    }

    @Override
    public Object unmarshal(Class clazz, InputStream source) throws Exception {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(source);
            byte[] ogBytes = Snappy.uncompress(bytes);
            byteArrayInputStream = new ByteArrayInputStream(ogBytes);
            return unmarshaller.unmarshal(clazz, byteArrayInputStream);
        } finally {
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
        }

    }
}
