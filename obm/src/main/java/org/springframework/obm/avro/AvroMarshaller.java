package org.springframework.obm.avro;


import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.Decoder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.obm.avro.support.DecoderFactoryBuilder;
import org.springframework.obm.avro.support.SchemaFactoryBean;
import org.springframework.obm.support.AbstractMarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Josh Long
 * @param <T>
 */
public class AvroMarshaller <T> extends AbstractMarshaller<T> implements InitializingBean {

    private boolean validate = false;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public boolean supports(Class clazz) {
          try {
            Assert.notNull(clazz, "the class must not be null");
            Schema s = new SchemaFactoryBean(clazz).getObject();
            boolean supports = s != null;

            if ( log.isDebugEnabled()) {
                log.debug("returning " + supports + " for class " + clazz.getName());
            }

            return supports;
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("exception when trying to test whether the class " + clazz.getName() + " has an Avro schema");
            }
            return false;
        }
    }


    @Override
    public void marshal(Object graph, OutputStream result) throws IOException, XmlMappingException {

    }/*

    @Override
    public T unmarshal(Class clazz, InputStream source) throws IOException, XmlMappingException {
        try {

            Assert.notNull(clazz, "the class must not be null");

            Schema schema = new SchemaFactoryBean(clazz).getObject();
            Assert.notNull(schema, "the schema must not be null");

            GenericDatumReader reader = new GenericDatumReader(schema);

            Object old = clazz.newInstance();

            Decoder decoder = new DecoderFactoryBuilder()
                                      .setInputStream( source)
                                      .setUseBinary(true)
                                      .setSchema(schema)
                                      .setValidate(this.validate)
                                      .build();

            return reader.read(old, decoder);


        } catch (Exception e) {
            if (log .isDebugEnabled()) {
                log .debug("exception when trying to test whether the class " + clazz.getName() + " has an Avro schema");
            }
            throw new RuntimeException(e);
        }
    }*/

    @Override
    public T unmarshal(Class<T> clazz, InputStream source) throws IOException, XmlMappingException {

         try {

            Assert.notNull(clazz, "the class must not be null");

            Schema schema = new SchemaFactoryBean(clazz).getObject();
            Assert.notNull(schema, "the schema must not be null");

            GenericDatumReader reader = new GenericDatumReader(schema);

            Object old = clazz.newInstance();

            Decoder decoder = new DecoderFactoryBuilder()
                                      .setInputStream( source)
                                      .setUseBinary(true)
                                      .setSchema(schema)
                                      .setValidate(this.validate)
                                      .build();

            return (T) reader.read(old, decoder);


        } catch (Exception e) {
            if (log .isDebugEnabled()) {
                log .debug("exception when trying to test whether the class " + clazz.getName() + " has an Avro schema");
            }
            throw new RuntimeException(e);
        }
    }



    /**
     * dictates whether the {@link org.apache.avro.io.Encoder encoders} and {@link org.apache.avro.io.Decoder decoders} will
     * be wrapped in a {@link org.apache.avro.io.ValidatingDecoder} or {@link org.apache.avro.io.ValidatingEncoder}
     *
     * @param validate whether or not to validate
     */
    public void setValidate(boolean validate) {
        this.validate = validate;
    }

   /* @Override
    public boolean supports(Class<?> clazz) {
      try {
            Assert.notNull(clazz, "the class must not be null");
            Schema s = new SchemaFactoryBean(clazz).getObject();
            boolean supports = s != null;

            if ( log.isDebugEnabled()) {
                log.debug("returning " + supports + " for class " + clazz.getName());
            }

            return supports;
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("exception when trying to test whether the class " + clazz.getName() + " has an Avro schema");
            }
            return false;
        }
    }

    @Override
    public T unmarshal(Class<T> clazz, InputStream source) throws IOException, XmlMappingException {
         try {

            Assert.notNull(clazz, "the class must not be null");

            Schema schema = new SchemaFactoryBean(clazz).getObject();
            Assert.notNull(schema, "the schema must not be null");

            GenericDatumReader reader = new GenericDatumReader(schema);

            Object old = clazz.newInstance();

            Decoder decoder = new DecoderFactoryBuilder()
                                      .setInputStream(inputMessage.getBody())
                                      .setUseBinary(true)
                                      .setSchema(schema)
                                      .setValidate(this.validate)
                                      .build();

            return reader.read(old, decoder);


        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("exception when trying to test whether the class " + clazz.getName() + " has an Avro schema");
            }
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void marshal(Object graph, OutputStream result) throws IOException, XmlMappingException {
    }*/
}
