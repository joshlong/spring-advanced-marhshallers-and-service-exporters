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
 * <P> This marshaller is a wrapping {@link org.springframework.obm.Marshaller}, meant to delegate actual
 * serialization duties to another {@link org.springframework.obm.Marshaller}.
 * <P>  The Snappy library (http://code.google.com/p/snappy-java) is a compression library from Google that is optimized for speed and modest comppression.
 * <P> While Snappy is a C++ library, C bindings are provided and that C binding is wrappd
 * by the Snappy-Java project, which consumes the C bindings through JNI. There are 32 and 64 bit builds packaged with the Snappy library for OSX, Linux, and Windows.
 * You should not need to do anything to use this (not even setup <CODE>LD_PATH</CODE> or <CODE>library.path</CODE>, but you
 * should be aware that the native code is there. It has been testd and works correctly in multi-classloader environments like Tomcat
 * </p>
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
